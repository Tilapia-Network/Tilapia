package net.tilapiamc.api.hook

import me.fan87.javainjector.NativeInstrumentation
import org.bukkit.block.Biome
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.analysis.Analyzer
import org.objectweb.asm.tree.analysis.AnalyzerException
import org.objectweb.asm.tree.analysis.SimpleVerifier
import org.objectweb.asm.util.CheckClassAdapter
import java.io.File
import java.io.PrintWriter
import java.lang.instrument.ClassDefinition
import java.lang.instrument.ClassFileTransformer

object TransformManager {

    val classesCache = HashMap<Class<*>, ClassNode>()
    val instrumentation = NativeInstrumentation()

    init {
        try {
            NativeInstrumentation.loadNativeLib()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        try {
            NativeInstrumentation.init()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun transformClass(classTransformer: ClassTransformer) {
        if (classTransformer.targetClass in classesCache) {
            val classNode = classesCache[classTransformer.targetClass]!!
            classTransformer.transform(classNode)
            var result: ByteArray
            try {
                val classWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES)
                classNode.accept(classWriter)
                result = classWriter.toByteArray()
            } catch (e: Exception) {
                e.printStackTrace()
                val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS)
                classNode.accept(classWriter)
                result = classWriter.toByteArray()
            }

            CheckClassAdapter.verify(ClassReader(result), TransformManager::class.java.classLoader, false, PrintWriter(System.err, true));

            instrumentation.redefineClasses(ClassDefinition(classTransformer.targetClass, result))
        } else {
            val transformer = ClassFileTransformer { loader, className, classBeingRedefined, protectionDomain, classfileBuffer ->
                try {
                    if (classBeingRedefined == classTransformer.targetClass) {
                        val classNode = ClassNode()
                        val classReader = ClassReader(classfileBuffer)
                        classReader.accept(classNode, ClassReader.EXPAND_FRAMES)
                        classTransformer.transform(classNode)
                        var result: ByteArray
                        try {
                            val classWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES)
                            classNode.accept(classWriter)
                            result = classWriter.toByteArray()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS)
                            classNode.accept(classWriter)
                            result = classWriter.toByteArray()
                        }
                        CheckClassAdapter.verify(ClassReader(result), TransformManager::class.java.classLoader, false, PrintWriter(System.err, true));
                        classesCache[classBeingRedefined] = classNode
                        result
                    } else {
                        classfileBuffer
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                    classfileBuffer
                }
            }
            instrumentation.addTransformer(transformer, true)
            instrumentation.retransformClasses(classTransformer.targetClass)
            instrumentation.removeTransformer(transformer)
        }
    }

    fun verify(classNode: ClassNode, loader: ClassLoader, printWriter: PrintWriter) {

        var syperType = if (classNode.superName == null) null else Type.getObjectType(classNode.superName)
        var methods = classNode.methods

        var interfaces: MutableList<Type> = ArrayList()
        for ( interfaceName:kotlin.String? in classNode.interfaces)
        {
            interfaces.add(Type.getObjectType(interfaceName))
        }

        for ( method:org.objectweb.asm.tree.MethodNode? in methods)
        {
            val verifier = SimpleVerifier(
                Type.getObjectType(classNode.name),
                syperType,
                interfaces,
                classNode.access and Opcodes.ACC_INTERFACE != 0
            )
            val analyzer = Analyzer(verifier)
            if (loader != null) {
                verifier.setClassLoader(loader)
            }
            try {
                analyzer.analyze(classNode.name, method)
            } catch (e: AnalyzerException) {
                e.printStackTrace(printWriter)
            }
        }
        printWriter.flush()
    }

}