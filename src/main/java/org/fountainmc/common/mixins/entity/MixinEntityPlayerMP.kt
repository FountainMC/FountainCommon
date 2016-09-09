package org.fountainmc.common.mixins.entity

import com.google.common.collect.ImmutableCollection
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.entity.player.PlayerCapabilities
import org.fountainmc.api.GameMode
import org.fountainmc.api.entity.Entity
import org.fountainmc.api.entity.EntityType
import org.fountainmc.api.entity.Player
import org.fountainmc.common.utils.ExperienceMath
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import java.util.*

@Mixin(EntityPlayerMP::class)
abstract class MixinEntityPlayerMP : MixinEntityLivingBase(), Player {
    override fun getHiddenEntities(): ImmutableCollection<out Entity>? {
        TODO("Finish implementing Player")
    }

    override fun hide(p0: Entity?) {
        TODO("Finish implementing Player")
    }

    override fun sendMessage(p0: String?) {
        TODO("Finish implementing Player")
    }

    override fun getUniqueId(): UUID {
        TODO("Finish implementing Player")
    }

    override fun getGameMode(): GameMode {
        TODO("Finish implementing Player")
    }

    override fun canSee(p0: Entity?): Boolean {
        TODO("Finish implementing Player")
    }

    override fun setGameMode(gameMode: GameMode) {
        TODO("Finish implementing Player")
    }

    override fun getName(): String {
        TODO("Finish implementing Player")
    }

    override fun isConnected(): Boolean {
        TODO("Finish implementing Player")
    }

    @Shadow
    private val capabilities: PlayerCapabilities = null!!

    override fun isFlyingAllowed() = capabilities.allowFlying

    override fun setFlyingAllowed(p0: Boolean) {
        capabilities.allowFlying = p0
    }

    @Shadow
    private var experience: Float = 0f
    @Shadow
    private var experienceLevel: Int = 0
    @Shadow
    private var experienceTotal: Int = 0

    override fun setPercentageToNextExperienceLevel(percentage: Float) {
        require(percentage >= 0, { "Negative percentage $percentage" })
        require(percentage < 1, { "Percentage to next experience level can't be 100%" })
        experience = percentage
    }

    override fun setExperienceLevel(experienceLevel: Int) {
        handle.e
    }

    override fun getPercentageToNextExperienceLevel() = experience
    override fun getExperienceLevel() = experienceLevel
    override fun getTotalExperience(): Long = experienceTotal.toLong()
    override fun setTotalExperience(total: Long) {
        require(total >= 0, { "Negative experience $total" })
        ExperienceMath.set(this, total)
    }

    override fun getEntityType(): EntityType<Player> = EntityType.PLAYER
}
