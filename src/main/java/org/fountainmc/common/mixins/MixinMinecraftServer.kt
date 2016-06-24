package org.fountainmc.common.mixins

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.server.MinecraftServer
import net.minecraft.server.management.PlayerList
import org.fountainmc.api.BlockType
import org.fountainmc.api.Fountain
import org.fountainmc.api.Material
import org.fountainmc.api.Server
import org.fountainmc.api.enchantments.EnchantmentType
import org.fountainmc.api.entity.EntityType
import org.fountainmc.api.inventory.item.ItemFactory
import org.fountainmc.common.AsyncCatcher.verifyNotAsync
import org.fountainmc.common.FountainImplementation
import org.fountainmc.common.Metrics
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import java.net.InetSocketAddress

@Mixin(MinecraftServer::class)
abstract class MixinMinecraftServer(internal val launchArguments: ImmutableList<String>, val fountainImplementation: FountainImplementation) : Server {
    internal val pluginManager = fountainImplementation.createPluginManager()
    internal val eventManager = fountainImplementation.createEventManager()
    internal val commandManager = fountainImplementation.createCommandManager()

    override fun getPluginManager() = pluginManager
    override fun getEventManager() = eventManager
    override fun getCommandManager() = commandManager
    override fun getLaunchArguments() = launchArguments

    @Volatile var materialsByName: Map<String, Material> = ImmutableMap.of(); // COW
    @Volatile var materialsById: Array<Material?> = arrayOf();

    override fun getMaterial(name: String): Material {
        return materialsByName[name] ?: throw IllegalArgumentException("The material named '$name' doesn't exist!")
    }

    override fun getMaterial(id: Int): Material {
        val materialsById: Array<Material?> = this.materialsById;
        if (id < 0) {
            throw IllegalArgumentException("Negative id: $id")
        } else if (id < materialsById.size) {
            val material: Material? = materialsById[id];
            if (material != null) return material;
        }
        throw IllegalArgumentException("Material with id $id is unknown")
    }

    @Inject(method = "main", at = arrayOf(At("HEAD")))
    fun checkServerInitialized() {
        if (Fountain.getServer() == null) {
            System.err.println("Fountain.getServer() isn't initialized!")
            System.exit(1)
        } else if (Fountain.getServer() !is FountainImplementation) {
            System.err.println("Fountain.getServer() is a ${Fountain.getServer().javaClass.typeName}, not a FountainImplementation")
            System.exit(1)
        }
    }

    val metrics = Metrics(fountainImplementation.implementationName, fountainImplementation.implementationVersion, fountainImplementation);

    @Inject(method = "main", at = arrayOf(At("INVOKE", target = "net.minecraft.server.MinecraftServer.startServerThread()")))
    fun startFountain() {
        fountainImplementation.onServerStart()
        metrics.start()
    }

    @Overwrite
    fun getServerModName() = fountainImplementation.implementationName

    fun registerItem(item: Item) {
        verifyNotAsync()
        val material = if (item is ItemBlock) {
            item.block as BlockType;
        } else {
            item as Material;
        }
        val newArray: Array<Material?> = materialsById.copyOf(materialsById.size + 1);
        newArray[item.getId()] = material;
        materialsById = newArray;
    }


    override fun getEntityType(s: String): EntityType<*> {
        TODO("Implement EntityType")
    }

    override fun getExpAtLevel(level: Int): Long {
        return when {
            level > 29 -> 62 + (level - 30L) * 7;
            level > 15 -> 17 + (level - 15L) * 3;
            else -> 17;
        }
    }

    override fun getEnchantmentTypeByName(s: String): EnchantmentType {
        TODO("Implement EnchantmentType")
    }

    override fun getItemFactory(): ItemFactory {
        TODO("Implement ItemFactory")
    }

    @Shadow
    override abstract fun getName(): String

    @Shadow
    abstract fun getMinecraftVersion(): String;

    override fun getVersion(): String = getMinecraftVersion()

    @Shadow
    override abstract fun getMotd(): String

    @Shadow
    override abstract fun getMaxPlayers(): Int

    @Shadow
    abstract fun getServerOwner(): String

    override fun getOwner() = getServerOwner()

    @Shadow
    abstract fun getHostname(): String

    @Shadow
    abstract fun getPort(): Int

    override fun getAddress(): InetSocketAddress? {
        return InetSocketAddress(getHostname(), getPort());
    }

    abstract val playerList: PlayerList
        @Shadow // This shadows getPlayerList()
        get

    override fun getOnlinePlayers(): Int {
        return playerList.maxPlayers
    }
}

fun Block.getId(): Int {
    return Block.getIdFromBlock(this);
}

fun Item.getId(): Int {
    return Item.REGISTRY.getIDForObject(this);
}