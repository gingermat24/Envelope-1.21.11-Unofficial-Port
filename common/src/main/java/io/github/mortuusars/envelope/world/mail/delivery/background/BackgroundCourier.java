package io.github.mortuusars.envelope.world.mail.delivery.background;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mortuusars.envelope.Config;
import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.world.entity.spawning.SpawnableItem;
import io.github.mortuusars.envelope.world.mail.MailService;
import io.github.mortuusars.envelope.world.mail.delivery.Courier;
import io.github.mortuusars.envelope.world.mail.delivery.CourierOrigin;
import io.github.mortuusars.envelope.world.mail.delivery.Delivery;
import io.github.mortuusars.envelope.world.entity.spawning.SpawnableEntityData;
import io.github.mortuusars.envelope.world.entity.CourierBat;
import io.github.mortuusars.envelope.world.entity.Pigeon;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import java.util.Optional;

public class BackgroundCourier implements Courier {
    public static final Codec<BackgroundCourier> CODEC = RecordCodecBuilder.create(i -> i.group(
          SpawnableEntityData.CODEC.fieldOf("entity").forGetter(BackgroundCourier::getSpawnableEntityData),
          CourierOrigin.CODEC.optionalFieldOf("origin", CourierOrigin.service()).forGetter(BackgroundCourier::getOrigin),
          Delivery.CODEC.fieldOf("delivery").forGetter(BackgroundCourier::getDelivery),
          Codec.DOUBLE.optionalFieldOf("travel_speed", 25.0).forGetter(courier -> courier.travelSpeed),
          Codec.BOOL.optionalFieldOf("bat_courier", false).forGetter(courier -> courier.batCourier),
          Codec.BOOL.optionalFieldOf("time_switchable", true).forGetter(courier -> courier.timeSwitchable)
    ).apply(i, BackgroundCourier::new));
    public static final Logger LOGGER = LogUtils.getLogger();

    private SpawnableEntityData entityData;
    private final Delivery delivery;
    private final CourierOrigin origin;
    private double travelSpeed;
    private boolean batCourier;
    private final boolean timeSwitchable;
    private boolean removed;

    public BackgroundCourier(SpawnableEntityData entityData, CourierOrigin origin, Delivery delivery,
                             double travelSpeed, boolean batCourier, boolean timeSwitchable) {
        this.entityData = entityData;
        this.delivery = delivery;
        this.origin = origin;
        this.travelSpeed = travelSpeed;
        this.batCourier = batCourier;
        this.timeSwitchable = timeSwitchable;
    }

    public BackgroundCourier(SpawnableEntityData entityData, CourierOrigin origin, Delivery delivery) {
        this(entityData, origin, delivery, Config.Server.DELIVERY_PIGEON_TRAVEL_SPEED.get(), false, true);
    }

    public SpawnableEntityData getSpawnableEntityData() {
        return entityData;
    }

    @Override
    public CourierOrigin getOrigin() {
        return origin;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    @Override
    public double getDeliveryTravelSpeed() {
        return travelSpeed;
    }

    @Override
    public Optional<Delivery> getCurrentDelivery() {
        return Optional.of(delivery);
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved() {
        this.removed = true;
    }

    @Override
    public void updateCourierAtHub(ServerLevel level, Delivery delivery) {
        if (!timeSwitchable) {
            return;
        }

        boolean shouldBeBat = CourierBat.isNight(level);
        if (shouldBeBat == batCourier) {
            travelSpeed = shouldBeBat
                  ? Config.Server.DELIVERY_BAT_TRAVEL_SPEED.get()
                  : Config.Server.DELIVERY_PIGEON_TRAVEL_SPEED.get();
            return;
        }

        Entity replacement = (shouldBeBat
              ? Envelope.EntityTypes.COURIER_BAT.get()
              : Envelope.EntityTypes.PIGEON.get()).create(level, EntitySpawnReason.LOAD);
        if (!(replacement instanceof io.github.mortuusars.envelope.world.mail.delivery.PhysicalCourier physicalCourier)) {
            throw new IllegalStateException("Could not create the replacement courier at the mail hub.");
        }

        BlockPos hub = delivery.getRoute().getHubPos().orElseGet(() -> origin.pos().orElse(BlockPos.ZERO));
        replacement.setPos(hub.getX() + 0.5, hub.getY() + 0.5, hub.getZ() + 0.5);
        if (replacement instanceof Pigeon pigeon) {
            pigeon.setOrigin(origin);
            pigeon.setDelivery(delivery);
        } else if (replacement instanceof CourierBat bat) {
            bat.setOrigin(origin);
            bat.setDelivery(delivery);
        }

        entityData = physicalCourier.toSpawnableData();
        batCourier = shouldBeBat;
        travelSpeed = physicalCourier.getDeliveryTravelSpeed();
    }

    // -- Delivery

    public void tick(ServerLevel level) {
        if (!isRemoved()) {
            tickDelivery(level, getDelivery());
        }
    }

    @Override
    public void endDelivery(ServerLevel level, Delivery delivery) {
        setRemoved();
        playAmbientSound(level, delivery);

        if (getOrigin().isRegular()) {
            BackgroundDelivery background = MailService.of(level).getBackgroundDelivery();
            background.addFinishedCourier(new FinishedBackgroundCourier(getSpawnableEntityData(), getOrigin().getPos(), level.getGameTime()));
            if (!delivery.getMail().isEmpty()) {
                background.addDroppedMail(new SpawnableItem(delivery.getMail(), getOrigin().getPos()));
            }
        } else if (!delivery.getMail().isEmpty()) {
            delivery.getRoute().getSenderPos().ifPresentOrElse(
                  senderPos -> {
                      LOGGER.warn("Dropping undelivered mail on the ground. Delivery: {}.", delivery);
                      MailService.of(level).getBackgroundDelivery().addDroppedMail(new SpawnableItem(delivery.getMail(), senderPos));
                  },
                  () ->
                        LOGGER.warn("Voiding undelivered mail. Delivery: {}.", delivery));
        }
    }

    private void playAmbientSound(ServerLevel level, Delivery delivery) {
        if (!timeSwitchable) {
            return;
        }
        BlockPos soundPos = getOrigin().pos()
              .or(() -> delivery.getRoute().getRecipientPos())
              .or(() -> delivery.getRoute().getSenderPos())
              .orElse(null);
        if (soundPos == null) {
            return;
        }

        SoundEvent sound = batCourier ? SoundEvents.BAT_AMBIENT : Envelope.SoundEvents.PIGEON_AMBIENT.get();
        float volume = batCourier ? 0.1f : 1.0f;
        Vec3 position = Vec3.atCenterOf(soundPos);
        level.playSound(null, position.x, position.y, position.z, sound, SoundSource.NEUTRAL, volume, 1.0f);
    }
}
