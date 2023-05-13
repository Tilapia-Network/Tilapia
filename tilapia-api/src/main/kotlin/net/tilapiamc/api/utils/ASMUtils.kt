package net.tilapiamc.api.utils


import javassist.bytecode.Opcode
import me.fan87.regbex.PrimitiveType
import net.tilapiamc.api.events.EventsManager
import org.bukkit.Bukkit
import org.bukkit.plugin.PluginManager
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaMethod


object ASMUtils {

    fun generateSout(message: String): InsnList {
        val out = InsnList()
        out.add(FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"))
        out.add(LdcInsnNode(message))
        out.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V"))
        return out
    }

    fun Class<*>.getInternalName(): String = name.replace(".", "/")
    fun Class<*>.getDescName(): String {
        if (isPrimitive) {
            for (value in PrimitiveType.values()) {
                if (this == value.primitiveType) {
                    return value.jvmName
                }
            }
            TODO("Unsupported Primitive Type!")
        } else if (isArray) {
            return "[${componentType.getDescName()}"
        } else {
            return "L${getInternalName()};"
        }
    }

    fun generateMethodCall(javaMethod: Constructor<*>): AbstractInsnNode {
        return MethodInsnNode(
            Opcodes.INVOKESPECIAL,
            javaMethod.declaringClass.getInternalName(),
            "<init>",
            getMethodDesc(Void.TYPE, *javaMethod.parameterTypes)
        )
    }

    fun generateMethodCall(javaMethod: Method): AbstractInsnNode {
        var opcode = Opcodes.INVOKEVIRTUAL
        if (Modifier.isStatic(javaMethod.modifiers)) {
            opcode = Opcodes.INVOKESTATIC
        }
        if (javaMethod.declaringClass.isInterface) {
            opcode = Opcodes.INVOKEINTERFACE
        }
        return MethodInsnNode(
            opcode,
            javaMethod.declaringClass.getInternalName(),
            javaMethod.name,
            getMethodDesc(javaMethod.returnType, *javaMethod.parameterTypes),
            javaMethod.declaringClass.isInterface
        )
    }

    fun generatePushInt(value: Int): AbstractInsnNode {
        if (value <= 5 && value >= -1) {
            return InsnNode(value + 3)
        }
        if ((value < 255) and (value > 0)) {
            return IntInsnNode(Opcode.BIPUSH, value)
        }
        if ((value < 32767) and (value > 255)) {
            return IntInsnNode(Opcode.BIPUSH, value)
        }
        return LdcInsnNode(value)
    }

    fun newArrayAndAddContent(arraySize: Int, type: Class<*>, gen: (Int) -> InsnList): InsnList {
        val out = InsnList()
        out.add(generatePushInt(arraySize))
        out.add(TypeInsnNode(Opcode.ANEWARRAY, type.getName().replace(".", "/")))
        for (i in 0 until arraySize) {
            out.add(InsnNode(Opcode.DUP))
            out.add(generatePushInt(i))
            out.add(gen(i))
            out.add(InsnNode(Opcode.AASTORE))
        }
        return out
    }

    fun loadClassFromPluginClassLoader(clazz: Class<*>): InsnList {
        val out = InsnList()
        out.add(LdcInsnNode(clazz.name))
        out.add(generatePushInt(1)) // true

        out.add(generateMethodCall(Bukkit::getPluginManager))
        out.add(generateMethodCall(PluginManager::getPlugins))
        out.add(generatePushInt(0))
        out.add(InsnNode(Opcode.AALOAD))
        out.add(generateMethodCall(Any::class.java.getDeclaredMethod("getClass")))
        out.add(generateMethodCall(Class<*>::getClassLoader))
        out.add(generateMethodCall(Class::class.java.getDeclaredMethod(
            "forName",
            String::class.java,
            Boolean::class.javaPrimitiveType,
            ClassLoader::class.java
        )))
        return out
    }

    fun generateGetClassNode(clazz: Class<*>): InsnList {
        val out = InsnList()
        out.add(LdcInsnNode(clazz.name))
        out.add(generateMethodCall(Class::class.java.getDeclaredMethod(
            "forName",
            String::class.java
        )))
        return out
    }

    fun reflectionMethodInvoke(method: Method, varManager: LocalVarManager): InsnList {
        val out = InsnList()
        val argumentVarIndex = ArrayList<Int>()
        var obj = -1
        if (!Modifier.isStatic(method.modifiers)) {
            obj = varManager.allocateVarNumber()
            out.add(VarInsnNode(Opcode.ASTORE, obj))
        }
        val length: Int = method.parameterTypes.size
        for (i in 0 until length) {
            val arg: Int = varManager.allocateVarNumber()
            argumentVarIndex.add(arg)
            out.add(VarInsnNode(Opcode.ASTORE, arg))
        }
        argumentVarIndex.reverse()

        out.add(loadClassFromPluginClassLoader(method.declaringClass))
        out.add(LdcInsnNode(method.name))
        out.add(newArrayAndAddContent(method.parameterTypes.size, Class::class.java) { index ->
            generateGetClassNode(method.parameterTypes[index])
        })
        out.add(generateMethodCall(Class<*>::getDeclaredMethod))
        if (obj == -1) {
            out.add(InsnNode(Opcode.ACONST_NULL))
        } else {
            out.add(VarInsnNode(Opcode.ALOAD, obj))
        }
        out.add(newArrayAndAddContent(argumentVarIndex.size, Any::class.java) { index ->
            val list = InsnList()
            try {
                list.add(VarInsnNode(Opcode.ALOAD, argumentVarIndex[index]))
            } catch (ignored: Exception) {
            }
            list
        })
        out.add(generateMethodCall(Method::invoke))
        if (method.returnType != Void::class.javaPrimitiveType) {
            out.add(generateCast(method.returnType))
        }
        return out
    }

    fun generateCast(type: Class<*>): InsnList {
        val out = InsnList()
        if (type.isPrimitive) {
            val primitiveType = PrimitiveType.values().first { it.primitiveType == type }
            out.add(TypeInsnNode(Opcodes.CHECKCAST, primitiveType.objectType.getInternalName()))
            out.add(generateMethodCall(primitiveType.objectType.methods.first { it.name == primitiveType.primitiveType.name + "Value" }))
            return out
        } else if (type.isArray) {
            out.add(TypeInsnNode(Opcodes.CHECKCAST, type.getInternalName()))
            return out
        } else {
            out.add(TypeInsnNode(Opcodes.CHECKCAST, type.getInternalName()))
            return out
        }
    }

    fun generateMethodCall(method: KFunction<*>): AbstractInsnNode {
        val javaMethod = method.javaMethod!!
        return generateMethodCall(javaMethod)
    }

    fun generateGetField(javaField: Field): AbstractInsnNode {
        var opcode = Opcodes.GETFIELD
        if (Modifier.isStatic(javaField.modifiers)) {
            opcode = Opcodes.GETSTATIC
        }
        return FieldInsnNode(
            opcode,
            javaField.declaringClass.getInternalName(),
            javaField.name,
            javaField.type.getDescName()
        )
    }

    fun generatePutField(javaField: Field): AbstractInsnNode {
        var opcode = Opcodes.PUTFIELD
        if (Modifier.isStatic(javaField.modifiers)) {
            opcode = Opcodes.PUTSTATIC
        }
        return FieldInsnNode(
            opcode,
            javaField.declaringClass.getInternalName(),
            javaField.name,
            javaField.type.getDescName()
        )
    }

    fun generateGetField(field: KProperty<*>): AbstractInsnNode {
        if (field.javaField?.modifiers?.let { Modifier.isPublic(it) } == true) {
            return generateGetField(field.javaField!!)
        }
        return generateMethodCall(field.getter)
    }

    fun generatePutField(field: KMutableProperty<*>): AbstractInsnNode {
        return generateMethodCall(field.setter)
    }

    fun getMethodDesc(returnType: Class<*>, vararg parameterTypes: Class<*>): String {
        return getMethodDesc(returnType.getDescName(), *(parameterTypes.map { it.getDescName() }.toTypedArray()))
    }

    fun getMethodDesc(returnType: String, vararg parameterTypes: String): String {
        return "(${parameterTypes.joinToString("")})$returnType"
    }

    fun fireEventAndPush(type: Class<*>, arguments: ((InsnList) -> Unit) = {}): InsnList {
        val out = InsnList()
        out.add(TypeInsnNode(Opcodes.NEW, type.getInternalName()))
        out.add(InsnNode(Opcodes.DUP))
        out.add(arguments.let { val o = InsnList(); it(o); o })
        out.add(
            MethodInsnNode(
                Opcodes.INVOKESPECIAL,
                type.getInternalName(),
                "<init>",
                getMethodDesc(Void::class.javaPrimitiveType!!, *type.constructors[0].parameterTypes)
            )
        )
        out.add(generateMethodCall(EventsManager::fireEvent))
        out.add(TypeInsnNode(Opcodes.CHECKCAST, type.getInternalName()))
        return out
    }

    fun getLatestVarNumber(instructions: InsnList): Int {
        var out = 0
        for (instruction in instructions) {
            if (instruction is VarInsnNode) {
                out = maxOf(out, instruction.`var`)
            }
        }
        return out.coerceAtLeast(100)
    }
}

class LocalVarManager(methodNode: MethodNode) {
    var latestUnusedVarNumber: Int
        private set

    init {
        latestUnusedVarNumber = ASMUtils.getLatestVarNumber(methodNode.instructions) + 1
    }

    fun allocateVarNumber(): Int {
        return latestUnusedVarNumber++
    }
}