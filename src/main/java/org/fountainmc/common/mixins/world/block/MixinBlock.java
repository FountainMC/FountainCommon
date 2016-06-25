package org.fountainmc.common.mixins.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

import org.fountainmc.api.BlockType;
import org.fountainmc.api.world.block.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Block.class)
public abstract class MixinBlock implements BlockType {

    @Shadow
    @Final
    private Material blockMaterial;

    @Override
    public boolean isFlammable() {
        return blockMaterial.getCanBurn();
    }

    @Override
    public boolean isOpaque() {
        return blockMaterial.isOpaque();
    }

    @Override
    public boolean isTransparent() {
        return !blockMaterial.blocksLight();
    }

    @Shadow
    public abstract IBlockState shadow$getDefaultState();

    @Override
    @Intrinsic
    public BlockState getDefaultState() {
        return (BlockState) shadow$getDefaultState();
    }

    @Override
    public int getId() {
        return Block.getIdFromBlock((Block) (Object) this);

    }

    @Override
    public String getName() {
        return Block.REGISTRY.getNameForObject((Block) (Object) this).toString();
    }

    @Override
    public boolean isEdible() {
        return false; // Blocks can't be eaten (cake can be eaten in a sense, but not while in a hotbar)
    }
}
