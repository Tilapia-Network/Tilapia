package net.tilapiamc.api.hook

import net.tilapiamc.api.utils.ASMUtils
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import java.lang.reflect.Method

abstract class MethodTransformer(val targetMethod: Method): ClassTransformer(targetMethod.declaringClass) {

    override fun transform(classNode: ClassNode) {
        for (method in classNode.methods) {
            if (method.name == targetMethod.name && method.desc == ASMUtils.getMethodDesc(targetMethod.returnType, *targetMethod.parameterTypes)) {
                transform(method)
            }
        }
    }

    abstract fun transform(methodNode: MethodNode)
}