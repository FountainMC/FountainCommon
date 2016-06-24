package org.fountainmc.common.mixins.world

import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IWorldEventListener
import net.minecraft.world.World
import net.minecraft.world.storage.WorldInfo
import org.fountainmc.api.Server
import org.fountainmc.api.world.Chunk
import org.fountainmc.api.world.block.BlockState
import org.fountainmc.api.world.tileentity.TileEntity
import org.fountainmc.common.FountainImplementation
import org.fountainmc.common.mixins.MixinMinecraftServer
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow

@Mixin(World::class)
abstract class MixinWorld(internal val server: Server) : org.fountainmc.api.world.World {

    override fun getServer() = server

    val fountainImplementation: FountainImplementation
        get() = (server as MixinMinecraftServer).fountainImplementation

    abstract val eventListeners: MutableList<IWorldEventListener>
        @Shadow
        get

    init {
        val listener = fountainImplementation.createWorldEventListener(this as World);
        if (listener != null) {
            eventListeners.add(listener);
        }
    }

    @Shadow
    abstract fun getWorldInfo(): WorldInfo

    override fun getName() = getWorldInfo().worldName

    @Shadow
    abstract fun setBlockState(pos: BlockPos, state: IBlockState)

    override fun setBlock(x: Int, y: Int, z: Int, state: BlockState?) {
        requireNotNull(state, { "Null state" })
        setBlockState(BlockPos(x, y, z), state as IBlockState)
    }

    @Shadow
    abstract fun getTileEntity(pos: BlockPos): net.minecraft.tileentity.TileEntity

    override fun getTileEntity(x: Int, y: Int, z: Int) = getTileEntity(BlockPos(x, y, z)) as TileEntity

    @Shadow
    abstract fun getChunkFromChunkCoords(x: Int, z: Int): net.minecraft.world.chunk.Chunk

    override fun getChunk(x: Int, z: Int) = getChunkFromChunkCoords(x, z) as Chunk

    @Shadow
    abstract fun getBlockState(pos: BlockPos): IBlockState

    override fun getBlock(x: Int, y: Int, z: Int): BlockState {
        return getBlockState(BlockPos(x, y, z)) as BlockState
    }

}