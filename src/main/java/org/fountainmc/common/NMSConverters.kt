package org.fountainmc.common

import com.google.common.collect.ImmutableBiMap
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Maps
import net.minecraft.util.EnumFacing
import org.fountainmc.api.Direction

private val DIRECTIONS_BY_NMS = Maps.immutableEnumMap(ImmutableMap.builder<EnumFacing, Direction>()
        .put(EnumFacing.NORTH, Direction.NORTH)
        .put(EnumFacing.EAST, Direction.EAST)
        .put(EnumFacing.SOUTH, Direction.SOUTH)
        .put(EnumFacing.WEST, Direction.WEST)
        .put(EnumFacing.UP, Direction.UP)
        .put(EnumFacing.DOWN, Direction.DOWN)
        .build())
private val DIRECTIONS_BY_FOUNTAIN = Maps.immutableEnumMap(ImmutableBiMap.copyOf(DIRECTIONS_BY_NMS).inverse())

fun EnumFacing.toFountainDirection(): Direction {
    return DIRECTIONS_BY_NMS[this] ?: throw AssertionError("EnumFacing $this has no fountain equivalent!")
}

fun Direction.toNMSDirection(): EnumFacing {
    return DIRECTIONS_BY_FOUNTAIN[this] ?: throw AssertionError("Fountain direction $this has no NMS equivalent!")
}