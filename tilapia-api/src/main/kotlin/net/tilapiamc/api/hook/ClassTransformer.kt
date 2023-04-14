package net.tilapiamc.api.hook

import org.objectweb.asm.tree.ClassNode

abstract class ClassTransformer(val targetClass: Class<*>) {
    abstract fun transform(classNode: ClassNode)
}