package net.tilapiamc.api.hook

import net.tilapiamc.api.utils.ASMUtils
import org.bukkit.Bukkit
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod

abstract class MethodTransformer(val targetMethod: KFunction<*>): ClassTransformer(targetMethod.javaMethod!!.declaringClass) {

    override fun transform(classNode: ClassNode) {
        for (method in classNode.methods) {
            if (method.name == targetMethod.javaMethod!!.name && method.desc == ASMUtils.getMethodDesc(targetMethod.javaMethod!!.returnType, *targetMethod.javaMethod!!.parameterTypes)) {
                transform(method)
            }
        }
    }

    abstract fun transform(methodNode: MethodNode)
}