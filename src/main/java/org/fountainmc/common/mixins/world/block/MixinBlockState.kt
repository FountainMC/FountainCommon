package org.fountainmc.common.mixins.world.block

import net.minecraft.block.state.BlockStateBase
import net.minecraft.block.state.IBlockState
import org.fountainmc.api.Fountain
import org.spongepowered.asm.mixin.Mixin

@Mixin(BlockStateBase::class)
abstract class MixinBlockState {
    @Suppress("CAST_NEVER_SUCCEEDS")
    val fountainState: WetBlockState by lazy { WetBlockState.createState(Fountain.getServer(), this as IBlockState) }
}
