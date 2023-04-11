package net.tilapiamc.api.utils


import me.fan87.regbex.PrimitiveType
import net.tilapiamc.api.events.EventsManager
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
        return MethodInsnNode(
            opcode,
            javaMethod.declaringClass.getInternalName(),
            javaMethod.name,
            getMethodDesc(javaMethod.returnType, *javaMethod.parameterTypes)
        )
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
}