package net.tilapiamc.customib.block

import me.fan87.plugindevkit.events.ServerTickEvent
import net.minecraft.server.v1_8_R3.ChunkRegionLoader
import net.minecraft.server.v1_8_R3.WorldLoader
import net.minecraft.server.v1_8_R3.WorldLoaderServer
import net.tilapiamc.common.events.annotation.Subscribe
import net.tilapiamc.customib.events.BlockUpdateEvent
import net.tilapiamc.customib.item.ItemsManager
import net.tilapiamc.spigotcommon.game.event.GameEventManager
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.event.block.BlockPhysicsEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent

class BlocksManager(val world: World, val eventManager: GameEventManager, val itemsManager: ItemsManager) {

    init {
        eventManager.registerListener(this)
    }

    private val _blocks = ArrayList<CustomBlock>()
    val blocks: List<CustomBlock>
        get() = _blocks

    private val blockItems = HashMap<CustomBlock, ItemBlock>()
    private val oneTimeIgnoreBlocks = HashMap<Block, Int>()


    val worldBlocks = HashMap<Block, CustomBlock>()
    val worldBlockEntities = HashMap<Block, CustomBlockEntity>()

    fun registerBlock(block: CustomBlock) {
        _blocks.add(block)
        val itemBlock = ItemBlock(this, block)
        blockItems[block] = itemBlock
        itemsManager.registerItem(itemBlock)
        eventManager.registerListener(block)
    }

    fun setBlock(location: Location, block: CustomBlock?, updateBlockType: Boolean = true) = setBlock(location.block, block, updateBlockType)
    fun setBlock(blockLocation: Block, block: CustomBlock?, updateBlockType: Boolean = true) {
        println(" - setBlock: ${blockLocation.x}/${blockLocation.y}/${blockLocation.z}  to ${block?.displayName}")
        if (blockLocation.world != world) {
            throw IllegalArgumentException("The world of the location is different from the world of BlocksManager")
        }
        if (block == null) {
            worldBlocks.remove(blockLocation)
            worldBlockEntities[blockLocation]?.let { eventManager.unregisterListener(it) }
            worldBlockEntities.remove(blockLocation)
            if (updateBlockType) {
                blockLocation.type = Material.AIR
            }
            return
        }
        if (block !in blocks) {
            throw IllegalArgumentException("Could not set the block ad the block is not registered")
        }
        if (updateBlockType) {
            block.setBlockType(blockLocation)
        }
        worldBlocks[blockLocation] = block
        if (block is BlockEntityProvider) {
            val createBlockEntity = block.createBlockEntity(blockLocation)
            eventManager.registerListener(createBlockEntity)
            worldBlockEntities[blockLocation] = createBlockEntity
        }
        // Create Block Entities
    }
    fun getBlock(location: Location) = getBlock(location.block)
    fun getBlock(blockLocation: Block): CustomBlock? {
        return worldBlocks[blockLocation]
    }

    fun getBlockEntity(location: Location) = getBlockEntity(location.block)
    fun getBlockEntity(blockLocation: Block): CustomBlockEntity? {
        return worldBlockEntities[blockLocation]
    }


    fun getBlockItem(block: CustomBlock): ItemBlock {
        if (block !in blocks) {
            throw IllegalArgumentException("Could not set the block ad the block is not registered")
        }
        return blockItems[block]!!
    }

    // Handle Chunk Load
    // Handle Chunk Unload



    // Handle Block State Update
    @Subscribe("blocksManager-onBlockStateUpdate")
    fun onBlockStateUpdate(event: BlockUpdateEvent) {
        val theBlock = getBlock(event.block.location)
        if (theBlock != null && event.to != theBlock.material && event.to != Material.PISTON_MOVING_PIECE) {
            if (event.block in oneTimeIgnoreBlocks && oneTimeIgnoreBlocks[event.block]!! > 0) {
                oneTimeIgnoreBlocks[event.block] = oneTimeIgnoreBlocks[event.block]!! - 1
                return
            }
            println("Invalidated Block: ${getBlock(event.block.location)?.displayName}  ->  ${event.to}  at  ${event.block.x}/${event.block.y}/${event.block.z}")
            setBlock(event.block.location, null, false)
        }
    }
    @Subscribe("blocksManager-removeIgnoreList")
    fun removeIgnoreList(event: ServerTickEvent) {
        oneTimeIgnoreBlocks.clear()
    }


    // Handle Falling Block Formation
    // Handle Falling Block Deformation
    @Subscribe("blocksManager-cancelPhysicsOnCustomBlock")
    fun cancelPhysicsOnCustomBlock(event: BlockPhysicsEvent) {
        val theBlock = getBlock(event.block)
        if (theBlock != null) {
            event.isCancelled = true
        }
    }


    // Handle Piston & Slime Blocks
    @Subscribe("blockManager-handlePistonExtend")
    fun handlePistonExtend(event: BlockPistonExtendEvent) {
        for (block in event.blocks.reversed()) {
            val direction = event.direction
            val newBlock = block.location.add(direction.modX.toDouble(), direction.modY.toDouble(), direction.modZ.toDouble())
                .block
            val theBlock = getBlock(block)
            println("Piston Extend: ${theBlock?.displayName}  from ${block.x}/${block.y}/${block.z} -> ${newBlock.x}/${newBlock.y}/${newBlock.z} ")
            if (theBlock != null) {
                val blockEntity = getBlockEntity(block)
                setBlock(block, null, false)
                setBlock(newBlock, theBlock, false)
                oneTimeIgnoreBlocks[newBlock] = 1
                blockEntity?.block = newBlock
            }
        }
    }
    @Subscribe("blockManager-handlePistonRetract")
    fun handlePistonRetract(event: BlockPistonRetractEvent) {
        for (block in event.blocks.reversed()) {
            val direction = event.direction
            val newBlock = block.location.add(direction.modX.toDouble(), direction.modY.toDouble(), direction.modZ.toDouble())
                .block
            val theBlock = getBlock(block)
            println("Piston Retract: ${theBlock?.displayName}  from ${block.x}/${block.y}/${block.z} -> ${newBlock.x}/${newBlock.y}/${newBlock.z} ")
            if (theBlock != null) {
                val blockEntity = getBlockEntity(block)
                setBlock(block, null, false)
                setBlock(newBlock, theBlock, false)
                oneTimeIgnoreBlocks[newBlock] = 1
                blockEntity?.block = newBlock
            }
        }
    }

}