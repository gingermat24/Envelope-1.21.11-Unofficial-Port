package io.github.mortuusars.envelope.world.mail.delivery;

import com.mojang.logging.LogUtils;
import io.github.mortuusars.envelope.world.mail.MailService;
import io.github.mortuusars.envelope.Config;
import io.github.mortuusars.envelope.world.entity.CourierBat;
import io.github.mortuusars.envelope.world.entity.Pigeon;
import io.github.mortuusars.envelope.world.mail.delivery.PhysicalCourier;
import io.github.mortuusars.envelope.world.mail.address.Address;
import io.github.mortuusars.envelope.world.mail.address.type.*;
import org.slf4j.Logger;

import java.util.function.Function;

public class DeliveryManager {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final MailService mailService;

    public DeliveryManager(MailService mailService) {
        this.mailService = mailService;
    }

    public MailService getMailService() {
        return mailService;
    }

    // --

    public void start(Pigeon pigeon, DeliveryDraft draft) {
        start(draft, pigeon::startDelivery, pigeon.getDeliveryTravelSpeed());
    }

    public void start(PhysicalCourier courier, DeliveryDraft draft) {
        if (courier instanceof Pigeon pigeon) {
            start(pigeon, draft);
        } else if (courier instanceof CourierBat bat) {
            start(draft, bat::startDelivery, bat.getDeliveryTravelSpeed());
        } else {
            throw new IllegalArgumentException("Unsupported physical courier: " + courier.getClass().getName());
        }
    }

    public void startService(DeliveryDraft draft) {
        var level = getMailService().getLevel();
        boolean useBat = CourierBat.isNight(level) && Config.Server.BAT_EMPLOYED_AT_MAIL_SERVICE.get();
        start(draft, delivery -> useBat
                    ? CourierBat.spawnServiceCourier(level, delivery)
                    : Pigeon.spawnServiceCourier(level, delivery),
              useBat ? Config.Server.DELIVERY_BAT_TRAVEL_SPEED.get()
                    : Config.Server.DELIVERY_PIGEON_TRAVEL_SPEED.get());
    }

    private void start(DeliveryDraft draft, Function<Delivery, Courier> courier, double travelSpeed) {
        Delivery delivery = createDelivery(draft, travelSpeed);
        courier.apply(delivery);
        LOGGER.debug("Started delivery: {}", delivery);
    }

    protected Delivery createDelivery(DeliveryDraft draft, double travelSpeed) {
        return new Delivery(draft.getOrCreateId(getMailService().getLevel()),
              draft.getOwner(),
              draft.getSender(),
              draft.getRecipient(),
              draft.getMail(),
              DeliveryRoute.build(getMailService().getLevel(), draft.getSender(), draft.getRecipient(), travelSpeed),
              draft.getPhase(),
              0,
              false);
    }

    // --

    public boolean canDeliverTo(Address address) {
        address = address.resolve(getMailService());
        return address instanceof BlockAddress || address instanceof ServiceAddress;
    }
}
