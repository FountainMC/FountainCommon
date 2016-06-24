package org.fountainmc.common.mixins.world.block

import com.google.common.base.Verify
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import org.fountainmc.api.BlockType
import org.fountainmc.api.Server
import org.fountainmc.api.world.block.BlockState
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.scanners.TypeAnnotationsScanner
import org.reflections.scanners.TypeElementsScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.FilterBuilder

// NOTE: normally we'd use a mixin but BlockState isn't type safe so we implement our types which wrap the BlockState
open class WetBlockState protected constructor(val server: Server, val handle: IBlockState) : BlockState {

    override fun getBlockType(): BlockType {
        return handle.block as BlockType
    }

    companion object { // Dark kotlin magic....

        private val factories: ImmutableMap<Block, (Server, IBlockState) -> WetBlockState> by lazy {
            val builder = ImmutableMap.Builder<Block, (Server, IBlockState) -> WetBlockState>()
            val reflections = Reflections(getReflectionsConfiguration("org.fountainmc.world.block"))
            val types = reflections.getTypesAnnotatedWith(BlockStateImpl::class.java)
            if (types != null) {
                for (type in types) {
                    Verify.verify(WetBlockState::class.java.isAssignableFrom(type), "Class %s isn't instanceof WetBlockState", type.typeName)
                    val constructor = type.getConstructor(Server::class.java, IBlockState::class.java)
                    ImmutableList.copyOf<String>(type.getAnnotation(BlockStateImpl::class.java).value).forEach { blockName ->
                        val block = Verify.verifyNotNull(Block.getBlockFromName(blockName),
                                "Class %s specified unknown block name minecraft:%s.", type.typeName, blockName)
                        builder.put(block, { server, state ->
                            constructor.newInstance(server, state) as WetBlockState
                        })
                    }
                }
            }
            builder.build()
        }

        private fun getReflectionsConfiguration(packageName: String): ConfigurationBuilder {
            return ConfigurationBuilder().addUrls(ClasspathHelper.forPackage(packageName)).filterInputsBy(FilterBuilder().include(FilterBuilder.prefix(packageName + "."))).setScanners(TypeElementsScanner(), TypeAnnotationsScanner(), SubTypesScanner())
        }

        internal fun createState(server: Server, handle: IBlockState): WetBlockState {
            return factories.getOrElse(requireNotNull(handle, { "Null block state" }).block, {{ server, handle -> WetBlockState(server, handle) }})(server, handle)
        }
    }
}
