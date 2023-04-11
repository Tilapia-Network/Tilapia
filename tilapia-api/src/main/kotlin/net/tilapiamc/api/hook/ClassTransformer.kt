package net.tilapiamc.api.hook

import org.objectweb.asm.tree.ClassNode
import java.lang.instrument.ClassFileTransformer
import java.security.ProtectionDomain

abstract class ClassTransformer(val targetClass: Class<*>) {
    abstract fun transform(classNode: ClassNode)
}