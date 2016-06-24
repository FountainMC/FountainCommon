package org.fountainmc.common.mixins.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.chunk.Chunk;

import org.fountainmc.api.world.World;
import org.fountainmc.api.world.block.BlockState;
import org.fountainmc.common.AsyncCatcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static com.google.common.base.Preconditions.checkArgument;

@Mixin(Chunk.class)
public abstract class MixinChunk implements org.fountainmc.api.world.Chunk {

    @Shadow
    @Final
    private int xPosition, zPosition;

    @Override
    public int getX() {
        return xPosition;
    }

    @Override
    public int getZ() {
        return zPosition;
    }

    @Shadow
    public abstract net.minecraft.world.World shadow$getWorld();

    @Override
    @Intrinsic
    public World getWorld() {
        return (World) shadow$getWorld();
    }

    @Shadow
    public abstract IBlockState getBlockState(int x, int y, int z);

    @Override
    public BlockState getBlockAt(int x, int y, int z) {
        checkArgument(x >> 4 == this.getX(), "X position %s isn't in chunk %s", x, this);
        checkArgument(z >> 4 == this.getZ(), "Z position %s isn't in chunk %s", z, this);
        checkArgument(y > 0, "Negative y position %s", y);
        AsyncCatcher.checkNotAsync("block access");
        return (BlockState) getBlockState(x, y, z);
    }
}
