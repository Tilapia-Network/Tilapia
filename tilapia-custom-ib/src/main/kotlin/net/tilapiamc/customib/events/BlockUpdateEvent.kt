package net.tilapiamc.customib.events

import net.minecraft.server.v1_8_R3.BlockPosition
import net.minecraft.server.v1_8_R3.Chunk
import net.minecraft.server.v1_8_R3.IBlockData
import net.tilapiamc.api.events.EventsManager
import net.tilapiamc.api.hook.MethodTransformer
import net.tilapiamc.api.hook.TransformManager
import net.tilapiamc.api.utils.ASMUtils
import net.tilapiamc.api.utils.LocalVarManager
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlockState
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers
import org.bukkit.event.HandlerList
import org.bukkit.event.block.BlockEvent
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.VarInsnNode
import kotlin.reflect.jvm.javaMethod

class BlockUpdateEvent(block: Block, val to: Material): BlockEvent(block) {

    companion object {
        val handlerList = HandlerList()

        private var initialized = false

        @JvmStatic
        fun init() {
            if (!initialized) {
                initialized = true
                TransformManager.transformClass(object : MethodTransformer(net.minecraft.server.v1_8_R3.Chunk::class.java.getDeclaredMethod("a", BlockPosition::class.java, IBlockData::class.java)) {
                    override fun transform(methodNode: MethodNode) {
                        val out = InsnList()
                        val varManager = LocalVarManager(methodNode)
                        out.add(VarInsnNode(Opcodes.ALOAD, 0))
                        out.add(VarInsnNode(Opcodes.ALOAD, 1))
                        out.add(VarInsnNode(Opcodes.ALOAD, 2))
                        out.add(ASMUtils.reflectionMethodInvoke(BlockUpdateCallBack::fireEvent.javaMethod!!, varManager))
                        for (instruction in methodNode.instructions) {
                            out.add(instruction)
                        }
                        methodNode.instructions = out
                    }
                })
            }
        }
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }


}

object BlockUpdateCallBack {
    @JvmStatic
    fun fireEvent(chunk: Chunk, blockPosition: BlockPosition, blockState: IBlockData) {
        EventsManager.fireEvent(BlockUpdateEvent(chunk.bukkitChunk.getBlock(blockPosition.x, blockPosition.y, blockPosition.z), Material.getMaterial(CraftMagicNumbers.getId(blockState.block))))
    }
}