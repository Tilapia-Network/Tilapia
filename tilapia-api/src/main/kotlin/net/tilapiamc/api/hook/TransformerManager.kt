package net.tilapiamc.api.hook

import me.fan87.javainjector.NativeInstrumentation
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import java.lang.instrument.ClassDefinition
import java.lang.instrument.ClassFileTransformer

object TransformerManager {

    val classesCache = HashMap<Class<*>, ClassNode>()
    val instrumentation = NativeInstrumentation()

    init {
        try {
            NativeInstrumentation.init()
        } catch (e: Throwable) {}
    }

    fun transformClass(classTransformer: ClassTransformer) {
        if (classTransformer.targetClass in classesCache) {
            val classNode = classesCache[classTransformer.targetClass]!!
            classTransformer.transform(classNode)
            val classWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES)
            classNode.accept(classWriter)
            instrumentation.redefineClasses(ClassDefinition(classTransformer.targetClass, classWriter.toByteArray()))
        } else {
            val transformer = ClassFileTransformer { loader, className, classBeingRedefined, protectionDomain, classfileBuffer ->
                if (classBeingRedefined == classTransformer.targetClass) {
                    val classNode = ClassNode()
                    val classReader = ClassReader(classfileBuffer)
                    classReader.accept(classNode, 0)
                    classTransformer.transform(classNode)
                    val classWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES)
                    classesCache[classBeingRedefined] = classNode
                    classNode.accept(classWriter)
                    classWriter.toByteArray()
                } else {
                    classfileBuffer
                }
            }
            instrumentation.addTransformer(transformer, true)
            instrumentation.retransformClasses(classTransformer.targetClass)
            instrumentation.removeTransformer(transformer)
        }
    }

}