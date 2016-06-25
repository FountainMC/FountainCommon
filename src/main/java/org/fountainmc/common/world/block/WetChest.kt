package org.fountainmc.common.world.block

import com.google.common.base.Preconditions.checkArgument
import com.google.common.base.Preconditions.checkNotNull
import net.minecraft.block.BlockChest
import net.minecraft.block.state.IBlockState
import org.fountainmc.api.Direction
import org.fountainmc.api.Server
import org.fountainmc.api.world.block.Chest
import org.fountainmc.common.mixins.world.block.MixinBlockState
import org.fountainmc.common.toFountainDirection
import org.fountainmc.common.toNMSDirection

@BlockStateImpl("chest")
class WetChest(server: Server, handle: IBlockState) : WetBlockState(server, handle), Chest {

    override fun getDirection(): Direction {
        return handle.getValue(BlockChest.FACING).toFountainDirection()
    }

    override fun withDirection(direction: Direction): WetChest {
        checkArgument(!checkNotNull(direction, "Null direction").isVertical, "Direction %s is vertical")
        return (handle.withProperty(BlockChest.FACING, direction.toNMSDirection()) as MixinBlockState).fountainState as WetChest
    }

}
