package org.fountainmc.common;

import javax.annotation.Nullable;

import com.google.common.base.Verify;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;

import org.fountainmc.api.Fountain;
import org.fountainmc.api.NonnullByDefault;
import org.fountainmc.api.Server;
import org.fountainmc.api.command.CommandManager;
import org.fountainmc.api.event.EventManager;
import org.fountainmc.api.plugin.PluginManager;
import org.fountainmc.common.mixins.MixinMinecraftServer;
import org.fountainmc.common.mixins.world.AbstractWorldEventListener;
import org.spongepowered.asm.mixin.Mixin;

@NonnullByDefault
public interface FountainImplementation extends Server {

    static FountainImplementation getInstance() {
        return ((MixinMinecraftServer) Verify.verifyNotNull(Fountain.getServer())).getFountainImplementation();
    }

    static MinecraftServer getMinecraftServer() {
        return getInstance().getServer();
    }

    FountainConfiguration getConfiguration();

    String getImplementationName();

    String getImplementationVersion();

    MinecraftServer getServer();

    PluginManager createPluginManager();

    EventManager createEventManager();

    CommandManager createCommandManager();

    // Hooks

    default void onServerStart() {
    }

    @Nullable
    default IWorldEventListener createWorldEventListener(World world) {
        return null;
    }

}