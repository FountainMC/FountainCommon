package org.fountainmc.common.mixins.entity

import com.google.common.collect.ImmutableCollection
import net.minecraft.entity.player.EntityPlayerMP
import org.fountainmc.api.entity.Entity
import org.fountainmc.api.entity.EntityType
import org.fountainmc.api.entity.LivingEntity
import org.fountainmc.api.entity.Player
import org.spongepowered.asm.mixin.Mixin
import java.util.*

@Mixin(EntityPlayerMP::class)
abstract class MixinEntityPlayerMP: MixinEntityLivingBase(), Player {
    override fun getHiddenEntities(): ImmutableCollection<out Entity>? {
        TODO("Finish implementing Player")
    }

    override fun hide(p0: Entity?) {
        TODO("Finish implementing Player")
    }

    override fun setExperienceLevel(p0: Int) {
        TODO("Finish implementing Player")
    }

    override fun sendMessage(p0: String?) {
        TODO("Finish implementing Player")
    }

    override fun getUniqueId(): UUID {
        TODO("Finish implementing Player")
    }

    override fun getGameMode(): Player.GameMode {
        TODO("Finish implementing Player")
    }

    override fun setPercentageToNextExperienceLevel(p0: Float) {
        TODO("Finish implementing Player")
    }

    override fun canSee(p0: Entity?): Boolean {
        TODO("Finish implementing Player")
    }

    override fun setGameMode(p0: Player.GameMode?) {
        TODO("Finish implementing Player")
    }

    override fun getName(): String {
        TODO("Finish implementing Player")
    }

    override fun isConnected(): Boolean {
        TODO("Finish implementing Player")
    }

    override fun getExperienceLevel(): Int {
        TODO("Finish implementing Player")
    }

    override fun canFly(): Boolean {
        TODO("Finish implementing Player")
    }

    override fun setCanFly(p0: Boolean): Boolean {
        TODO("Finish implementing Player")
    }

    override fun getPercentageToNextExperienceLevel(): Float {
        TODO("Finish implementing Player")
    }

    override fun getEntityType() = EntityType.PLAYER
}
