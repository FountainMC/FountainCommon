package org.fountainmc.common.mixins.entity

import com.google.common.collect.ImmutableList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.world.World
import org.fountainmc.api.Server
import org.fountainmc.api.entity.Entity
import org.fountainmc.api.entity.EntityType
import org.fountainmc.api.world.Location
import org.fountainmc.common.AsyncCatcher.checkNotAsync
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject

@Mixin(net.minecraft.entity.Entity::class)
abstract class MixinEntity : Entity {
    @Shadow
    private var posX = 0.0
    @Shadow
    private var posY = 0.0
    @Shadow
    private var posZ = 0.0
    @Shadow
    private var worldObj = null as World

    @Shadow
    internal val isDead = false

    override fun isDead() = isDead

    override fun getWorld() = worldObj as org.fountainmc.api.world.World

    override fun getServer() = worldObj.minecraftServer as Server

    override fun getLocation() = Location(world, posX, posY, posZ)

    override fun teleport(destination: Location) {
        requireNotNull(destination, { "Null location" })
        checkNotAsync("teleport")

        check(!isDead, { "$this is dead!" })
        if (isBeingRidden()) ejectAll()
        this.dismountVehicle()

        this.worldObj = destination.world!! as World
        this.setPositionAndRotation(destination.x, destination.y, destination.z, yaw, pitch)
    }

    @Shadow
    abstract fun setPositionAndRotation(x: Double, y: Double, z: Double, yaw: Float, pitch: Float)

    @Inject(method = "setPositionAndRotation", at = arrayOf(At("HEAD")))
    private final fun validatePositionAndRotation(x: Double, y: Double, z: Double, yaw: Float, pitch: Float) {
        checkNotAsync("setPositionAndRotation")
        require(x.isFinite(), { "X position $x isn't finite" })
        require(y.isFinite(), { "Y position $y isn't finite" })
        require(z.isFinite(), { "Z position $z isn't finite" })
        require(yaw.isFinite(), { "Yaw $yaw isn't finite" })
        require(pitch.isFinite(), { "Pitch $pitch isn't finite" })
    }

    @Shadow
    var rotationYaw: Float = 0.0f
    @Shadow
    var rotationPitch: Float = 0.0f
    @Shadow
    var prevRotationYaw: Float = 0.0f
    @Shadow
    var prevRotationPitch: Float = 0.0f

    override fun getPitch() = rotationPitch
    override fun getYaw() = rotationYaw

    override fun setPitch(pitch: Float) {
        checkNotAsync("set pitch")
        require(pitch.isFinite(), { "Pitch $pitch isn't finite" })
        prevRotationPitch = pitch
        rotationPitch = pitch
    }

    override fun setYaw(yaw: Float) {
        checkNotAsync("set yaw")
        require(yaw.isFinite(), { "Yaw $yaw isn't finite" })
        rotationYaw = yaw
        prevRotationYaw = yaw
    }


    override fun getMaximumPassengers() = 1

    @Overwrite
    fun canFitPassenger(other: net.minecraft.entity.Entity): Boolean {
        return this.passengers.size < maximumPassengers
    }

    @Shadow
    protected abstract fun startRiding(passenger: net.minecraft.entity.Entity, force: Boolean): Boolean

    override fun startRiding(vehicle: Entity, force: Boolean): Boolean {
        requireNotNull(vehicle, { "Null vehicle" })
        require(vehicle.world == this.world, { "Vehicle's world ${vehicle.world} doesn't equal this entity's world ${this.world}" })
        checkNotAsync("add a passenger")
        check(force || vehicle.asNMS().canFitPassenger(this.asNMS()), { "Vehicle $vehicle can't fit this entity $this because it can only have ${vehicle.maximumPassengers} passengers!" })
        return startRiding(vehicle.asNMS(), force)
    }

    override fun setPrimaryPassenger(passenger: Entity) {
        requireNotNull(passenger, { "Null passenger" })
        require(passenger.world == this.world, { "Passenger's world ${passenger.world} doesn't equal vehicle's world ${this.world}" })
        ejectPrimaryPassenger()
        passenger.asNMS().startRiding(this.asNMS(), true)
    }

    @Shadow
    @Final
    var riddenByEntities: MutableList<net.minecraft.entity.Entity> = mutableListOf()
        private set // Don't allow setting!

    @Suppress("UNCHECKED_CAST")
    override fun getPassengers() = ImmutableList.copyOf(riddenByEntities as List<Entity>)

    override fun getPrimaryPassenger() = if (hasPassengers()) riddenByEntities[0] as Entity? else null;

    @Shadow
    abstract fun isBeingRidden(): Boolean

    override fun hasPassengers() = isBeingRidden()

    @Shadow
    abstract fun getRidingEntity(): net.minecraft.entity.Entity // getVehicle

    override fun getVehicle() = getRidingEntity() as Entity

    @Shadow
    abstract fun dismountRidingEntity() // dismountVehicle

    override fun dismountVehicle() = dismountRidingEntity()

    @Inject(method = "dismountRidingEntity", at = arrayOf(At("HEAD")))
    private fun checkDismountNotAsync() = checkNotAsync("dismount")

    override fun ejectPassenger(passenger: Entity) = requireNotNull(passenger, { "Null passenger" }).dismountVehicle()

    @Shadow
    abstract fun removePassengers()

    override fun ejectAll() = removePassengers()

    override fun getEntityType(): EntityType<*> = TODO("Implement EntityType")

    @Shadow
    internal var isOnGround = false

    override fun isOnGround() = isOnGround

    @Shadow
    abstract fun getEntityBoundingBox(): AxisAlignedBB

    override fun getNearbyEntities(radius: Double): ImmutableList<Entity> {
        return ImmutableList.copyOf(worldObj.getEntitiesInAABBexcluding(asNMS(), getEntityBoundingBox().expandXyz(radius), {true})) as ImmutableList<Entity>
    }

    @Shadow
    internal var fire: Int = 0
    @Shadow
    internal var isImmuneToFire: Boolean = false;

    override fun setTicksOnFire(ticks: Int) {
        checkNotAsync("setting ticks on fire")
        require(ticks >= 0, { "Ticks on fire can't be negative: $ticks" })
        fire = ticks
    }

    override fun getTicksOnFire(): Int = ticksOnFire

    override fun setImmuneToFire(immuneToFire: Boolean) {
        isImmuneToFire = immuneToFire
    }

    override fun isImmuneToFire() = this.isImmuneToFire
}

fun Entity.asNMS(): net.minecraft.entity.Entity {
    return this as net.minecraft.entity.Entity;
}