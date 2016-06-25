package org.fountainmc.common.mixins.entity

import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.IAttribute
import net.minecraft.entity.ai.attributes.IAttributeInstance
import net.minecraft.util.DamageSource
import org.fountainmc.api.entity.LivingEntity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow

@Mixin(EntityLivingBase::class)
abstract class MixinEntityLivingBase() : MixinEntity(), LivingEntity {

    abstract fun attackEntityFrom(damageSource: DamageSource, amount: Float)

    override fun damage(amount: Double) {
        require(amount.isFinite(), { "Amount $amount must be finite" })
        require(amount > 0, { "Amount $amount must be greater than 0" })
        attackEntityFrom(DamageSource.generic, amount.toFloat())
    }

    @Shadow
    override abstract fun getHealth(): Double

    @Shadow
    override abstract fun setHealth(p0: Double)

    abstract fun getEntityAttribute(attribute: IAttribute): IAttributeInstance

    override fun setMaxHealth(amount: Double) {
        require(amount.isFinite(), { "Amount $amount must be finite" })
        require(amount > 0, { "Amount $amount must be greater than 0" })
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).baseValue = amount
        if (amount > health) health = amount
    }

    override fun getMaxHealth() = getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).attributeValue
}