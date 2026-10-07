package io.github.mortuusars.envelope.world.block.mailbox;

import com.google.common.base.Preconditions;
import com.mojang.logging.LogUtils;
import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.Platform;
import io.github.mortuusars.envelope.network.Packets;
import io.github.mortuusars.envelope.network.packet.clientbound.MailboxHasNewMailS2CP;
import io.github.mortuusars.envelope.world.mail.delivery.Delivery;
import io.github.mortuusars.envelope.world.entity.CourierBat;
import io.github.mortuusars.envelope.world.entity.Pigeon;
import io.github.mortuusars.envelope.world.mail.delivery.CourierOrigin;
import io.github.mortuusars.envelope.Config;
import io.github.mortuusars.envelope.world.Position;
import io.github.mortuusars.envelope.world.inventory.MailboxMenu;
import io.github.mortuusars.envelope.world.item.mail.Mail;
import io.github.mortuusars.envelope.world.mail.address.SimpleBlockAddressGenerator;
import io.github.mortuusars.envelope.world.mail.MailService;
import io.github.mortuusars.envelope.world.mail.address.type.BlockAddress;
import net.minecraft.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;

public class MailboxBlockEntity extends BaseContainerBlockEntity implements Inbox {
    public static final int REGULAR_SLOTS = 3;
    public static final int SLOT_FOOD = 0;
    public static final int SLOT_MAIL = 1;
    public static final int SLOT_BAT_FOOD = 2;
    public static final int INBOX_SLOT = 3;

    private static final Logger LOGGER = LogUtils.getLogger();

    private NonNullList<ItemStack> items = NonNullList.withSize(REGULAR_SLOTS, ItemStack.EMPTY);
    private @NotNull UUID inboxId = UUID.randomUUID();
    private @Nullable BlockAddress address;
    private @Nullable UUID owner;

    private @NotNull List<ItemStack> mail = new ArrayList<>();
    private boolean loaded = false;
    private boolean blockRemoved = false;
    private boolean deliveredWithPigeon;
    private boolean deliveredWithBat;
    private boolean courierAdvancementTriggered;
    private int batEmployCooldown;
    private int batEmployAttempt;

    protected MailboxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public MailboxBlockEntity(BlockPos pos, BlockState blockState) {
        this(Envelope.BlockEntityTypes.MAILBOX.get(), pos, blockState);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return address != null
              ? address.getComponent()
              : Component.translatable("container.envelope.mailbox");
    }

    // -- Address

    public @NotNull BlockAddress getAddress() {
        return Preconditions.checkNotNull(address,
              "Address of mailbox at [" + getBlockPos().toShortString() + "] was not set.");
    }

    public void setAddress(@Nullable BlockAddress address) {
        @Nullable BlockAddress currentAddress = this.address;
        this.address = address;

        if (getLevel() instanceof ServerLevel serverLevel && MailService.operatesIn(serverLevel)) {
            address = Objects.requireNonNullElseGet(address, () -> generateRandomAddress(serverLevel));
            address = MailService.of(serverLevel).getMailboxes().correctOrRegisterIfNeeded(address, getBlockPos());

            if (!address.equals(currentAddress)) {
                this.address = address;
                setChanged();
                // Syncs address to the client:
                serverLevel.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), MailboxBlock.UPDATE_ALL);
            }
        }
    }

    protected void applyAddress() {
        setAddress(this.address);
    }

    protected @NotNull BlockAddress generateRandomAddress(ServerLevel level) {
        return new SimpleBlockAddressGenerator(MailService.of(level).getKnownAddresses(), 50).generate(level.getRandom());
    }

    // -- Owner

    public @Nullable UUID getOwner() {
        return owner;
    }

    public void setOwner(@Nullable UUID owner) {
        this.owner = owner;
        setChanged();
    }

    public Optional<Player> getOwnerPlayer() {
        if (owner == null || level == null) return Optional.empty();
        for (Player player : level.players()) {
            if (player.getUUID().equals(owner)) {
                return Optional.of(player);
            }
        }
        return Optional.empty();
    }

    // -- Container

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public int getContainerSize() {
        return REGULAR_SLOTS + getAllMail().size();
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        if (slot >= INBOX_SLOT) {
            return getMail(slot - INBOX_SLOT);
        }
        return super.getItem(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        if (slot >= INBOX_SLOT) {
            return removeMail(slot - INBOX_SLOT);
        }
        return super.removeItem(slot, amount);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        if (slot >= INBOX_SLOT) {
            return removeMailNoUpdate(slot - INBOX_SLOT);
        }
        return super.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot >= INBOX_SLOT) {
            addMail(slot - INBOX_SLOT, stack);
            return;
        }
        super.setItem(slot, stack);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == SLOT_FOOD) return stack.is(Envelope.Tags.Items.PIGEON_FOOD);
        if (slot == SLOT_MAIL) return isSendable(stack);
        if (slot == SLOT_BAT_FOOD) return stack.is(Envelope.Tags.Items.BAT_FOOD);
        return false;
    }

    @Override
    public boolean canTakeItem(Container target, int slot, ItemStack stack) {
        return slot >= INBOX_SLOT
              && target.hasAnyMatching(ItemStack::isEmpty); // Prevents hoppers from removing the item and placing it back
    }

    public boolean isSendable(ItemStack stack) {
        return !stack.isEmpty() && stack.is(Envelope.Tags.Items.MAILABLE) && stack.has(Envelope.DataComponents.MAIL_RECIPIENT);
    }

    public boolean isAvailableForPickup() {
        if (level == null) return false;
        int foodSlot = CourierBat.isNight(level) ? SLOT_BAT_FOOD : SLOT_FOOD;
        return !getItem(foodSlot).isEmpty() && isSendable(getItem(SLOT_MAIL));
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        applyAddress();
        return new MailboxMenu(containerId, inventory, getBlockPos(), getAddress(), getAllMail());
    }

    public void openMenu(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (getOwner() == null) {
                setOwner(player.getUUID());
            }

            Platform.openMenu(serverPlayer, this, buffer -> {
                buffer.writeBlockPos(getBlockPos());
                BlockAddress.STREAM_CODEC.encode(buffer, getAddress());
                ItemStack.OPTIONAL_LIST_STREAM_CODEC.encode(buffer, getAllMail());
            });
            playSound(SoundEvents.BARREL_OPEN, 0.6f, 1.1f);
        }
    }

    @Override
    public void clearContent() {
        super.clearContent();
        mail.clear();
    }

    // -- Delivery

    public boolean tryStartDelivery(Pigeon pigeon) {
        return tryStartDelivery(pigeon, SLOT_FOOD);
    }

    public boolean tryStartDelivery(CourierBat bat) {
        return tryStartDelivery(bat, SLOT_BAT_FOOD);
    }

    private boolean tryStartDelivery(io.github.mortuusars.envelope.world.mail.delivery.PhysicalCourier courier, int foodSlot) {
        net.minecraft.world.entity.Entity entity = (net.minecraft.world.entity.Entity) courier;
        if (!MailService.operatesIn(entity.level())) {
            return false;
        }

        ServerLevel level = (ServerLevel) entity.level();

        if (courier.isDelivering()) return false;
        ItemStack mailStack = getItem(SLOT_MAIL);
        if (!isSendable(mailStack)) return false;
        if (!canPlaceItem(foodSlot, getItem(foodSlot)) || getItem(foodSlot).isEmpty()) return false;

        applyAddress();

        ItemStack mail = Mail.removePreviousDeliveryData(mailStack.copyWithCount(1));

        MailService.of(level).getDeliveryManager()
              .start(courier, Delivery.draft()
                    .deliver(mail)
                    .from(getAddress())
                    .to(Mail.getRecipientOrUnknown(mail))
                    .owner(getOwner()));

        removeItem(SLOT_MAIL, 1);
        removeItem(foodSlot, 1);

        Vec3 pos = entity.position();
        level.sendParticles(ParticleTypes.CLOUD, pos.x, pos.y, pos.z, 10, 0.3, 0.3, 0.3, 0.02);
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.NEUTRAL, 1f, 1.3f);

        if (courier instanceof Pigeon) {
            deliveredWithPigeon = true;
        } else if (courier instanceof CourierBat) {
            deliveredWithBat = true;
        }
        triggerCourierAdvancementIfReady();

        setChanged();
        return true;
    }

    // -- Inbox

    @Override
    public int getInboxCapacity() {
        return 512;
    }

    @Override
    public @NotNull List<ItemStack> getAllMail() {
        return mail;
    }

    @Override
    public void onMailInserted(ItemStack mail) {
        playSound(SoundEvents.NOTE_BLOCK_CHIME.value(), 1, 1);
    }

    @Override
    public void onMailAdded(ItemStack mail) {
        if (level instanceof ServerLevel serverLevel) {
            MailboxMenu.playersWithMenu(serverLevel, getAddress())
                  .forEach(player -> Packets.sendToClient(MailboxHasNewMailS2CP.INSTANCE, player));
        }
    }

    @Override
    public void onMailRemoved(int slot, ItemStack mail) {
        if (level instanceof ServerLevel serverLevel) {
            Optional.ofNullable(Mail.getId(mail)).ifPresent(id -> {
                MailboxMenu.executeForPlayersWithMenu(serverLevel, getAddress(),
                      (player, menu) -> menu.onMailRemoved(id));
            });
        }
    }

    @Override
    public void onInboxChanged() {
        setChanged();
        if (level != null) {
            level.updateNeighbourForOutputSignal(getBlockPos(), getBlockState().getBlock());
        }
    }

    // As inbox can be quite large in size, we cannot store it as regular block entity data.
    // So we use dedicated inbox storage for that, and load/unload each time block entity is being loaded/unloaded.

    public void loadInbox() {
        if (level instanceof ServerLevel serverLevel && !inboxId.equals(Util.NIL_UUID)) {
            mail = InboxStorage.get(serverLevel).remove(inboxId)
                  .map(Inbox::getAllMail)
                  .orElseGet(ArrayList::new);
        } else {
            mail = new ArrayList<>();
        }
    }

    public void unloadInbox() {
        if (level instanceof ServerLevel serverLevel && address != null && !inboxId.equals(Util.NIL_UUID)) {
            InboxStorage.get(serverLevel).put(inboxId, this);
        }
        mail.clear();
    }

    // -- Events

    public void serverTick(ServerLevel level, BlockPos blockPos, BlockState blockState) {
        if (!loaded) {
            onLoaded();
            loaded = true;
        }
        updateBlockStateIfNeeded();
        triggerCourierAdvancementIfReady();
        maybeEmployBat(level, blockPos);
    }

    private boolean maybeEmployBat(ServerLevel level, BlockPos blockPos) {
        if (batEmployCooldown > 0) {
            batEmployCooldown--;
        }
        int interval = Config.Server.BAT_EMPLOY_INTERVAL.get();
        if (batEmployCooldown > 0 || !CourierBat.isNight(level) || level.isRaining() || level.isThundering()
              || !isAvailableForPickup() || getItem(SLOT_BAT_FOOD).isEmpty()
              || level.getRandom().nextInt(Math.max(1, interval - batEmployAttempt++)) != 0) {
            return false;
        }

        List<Bat> batsNearby = level.getEntitiesOfClass(Bat.class, new AABB(blockPos).inflate(32),
              bat -> !bat.isDeadOrDying() && !bat.isRemoved());
        CourierBat courierBat = null;
        if (!batsNearby.isEmpty()) {
            Bat wildBat = Util.getRandom(batsNearby, level.getRandom());
            courierBat = wildBat.convertTo(Envelope.EntityTypes.COURIER_BAT.get(),
                  net.minecraft.world.entity.ConversionParams.single(wildBat, true, true),
                  EntitySpawnReason.CONVERSION,
                  converted -> converted.setOrigin(CourierOrigin.regular(blockPos)));
        }

        if (courierBat == null && Config.Server.BAT_SUMMONED_TO_MAILBOX_IF_NONE_NEARBY.get()
              && (Config.Server.BAT_MAILBOX_SUMMON_IGNORES_DOMOBSPAWNING_RULE.get()
              || level.getGameRules().get(GameRules.SPAWN_MOBS))) {
            BlockPos spawnPos = Position.ascendTowards(level, blockPos, Optional.empty(),
                  Config.Server.DELIVERY_ASCEND_DISTANCE.get(), level.getRandom().nextInt());
            courierBat = Envelope.EntityTypes.COURIER_BAT.get().create(level, EntitySpawnReason.MOB_SUMMONED);
            if (courierBat == null) {
                LOGGER.error("Could not create a Courier Bat for mailbox at {}.", blockPos);
            } else {
                courierBat.setPos(spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5);
                courierBat.setOrigin(CourierOrigin.service());
                courierBat.setSpawnPos(spawnPos);
                courierBat.onAppeared(level);
                if (!level.addFreshEntity(courierBat)) {
                    LOGGER.warn("Courier Bat summon was rejected for mailbox at {}.", blockPos);
                    courierBat = null;
                }
            }
        }

        if (courierBat != null) {
            courierBat.getMailboxHandler().setTargetPos(blockPos);
            batEmployCooldown = Config.Server.BAT_EMPLOY_COOLDOWN.get();
            batEmployAttempt = 0;
            return true;
        }
        return false;
    }

    private void triggerCourierAdvancementIfReady() {
        if (courierAdvancementTriggered || !deliveredWithPigeon || !deliveredWithBat) {
            return;
        }
        getOwnerPlayer().filter(ServerPlayer.class::isInstance).map(ServerPlayer.class::cast)
              .ifPresent(player -> {
                  Envelope.CriteriaTriggers.DELIVER_WITH_PIGEON_AND_BAT.get().trigger(player);
                  courierAdvancementTriggered = true;
                  setChanged();
              });
    }

    protected void onLoaded() {
        applyAddress();
        if (level != null) {
            level.updateNeighbourForOutputSignal(getBlockPos(), getBlockState().getBlock());
        }
    }

    public void onBlockRemoved(Level level, BlockPos pos, BlockState state, BlockState newState) {
        if (!state.is(newState.getBlock())) {
            Containers.dropContents(level, pos, this);
        }
        clearMail();
        blockRemoved = true;
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        loadInbox();
        blockRemoved = false;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (!blockRemoved) {
            unloadInbox();
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        updateBlockStateIfNeeded();
    }

    private void updateBlockStateIfNeeded() {
        if (!isRemoved() && level instanceof ServerLevel serverLevel) {
            BlockState state = getBlockState();
            boolean isOpen = state.getValue(MailboxBlock.OPEN);
            boolean hasMail = state.getValue(MailboxBlock.HAS_MAIL);

            boolean shouldBeOpen = isAvailableForPickup();
            boolean shouldHaveMail = !getAllMail().isEmpty();

            if (isOpen != shouldBeOpen || hasMail != shouldHaveMail) {
                serverLevel.setBlockAndUpdate(getBlockPos(), state
                      .setValue(MailboxBlock.OPEN, shouldBeOpen)
                      .setValue(MailboxBlock.HAS_MAIL, shouldHaveMail));

                if (isOpen != shouldBeOpen) {
                    playSound(shouldBeOpen ? SoundEvents.CHERRY_WOOD_TRAPDOOR_OPEN : SoundEvents.CHERRY_WOOD_TRAPDOOR_CLOSE,
                          0.75f,
                          shouldBeOpen ? 1f : 0.75f);
                }
            }
        }
    }

    // -- Sync

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    // -- Loading/Saving

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, items);
        if (address != null) output.putString("address", address.getString());
        if (owner != null) output.store("owner", UUIDUtil.CODEC, owner);
        if (!inboxId.equals(Util.NIL_UUID)) output.store("inbox_id", UUIDUtil.CODEC, inboxId);
        if (deliveredWithPigeon) output.putBoolean("delivered_with_pigeon", true);
        if (deliveredWithBat) output.putBoolean("delivered_with_bat", true);
        if (courierAdvancementTriggered) output.putBoolean("courier_advancement_triggered", true);
        if (batEmployCooldown > 0) output.putInt("bat_employ_cooldown", batEmployCooldown);
        if (batEmployAttempt > 0) output.putInt("bat_employ_attempt", batEmployAttempt);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, items);
        setAddress(input.getString("address").map(BlockAddress::new).orElse(null));
        owner = input.read("owner", UUIDUtil.CODEC).orElse(null);
        inboxId = input.read("inbox_id", UUIDUtil.CODEC).orElseGet(UUID::randomUUID);
        deliveredWithPigeon = input.getBooleanOr("delivered_with_pigeon", false);
        deliveredWithBat = input.getBooleanOr("delivered_with_bat", false);
        courierAdvancementTriggered = input.getBooleanOr("courier_advancement_triggered", false);
        batEmployCooldown = input.getIntOr("bat_employ_cooldown", 0);
        batEmployAttempt = input.getIntOr("bat_employ_attempt", 0);
    }

    // -- Util

    public void playSound(SoundEvent soundEvent, float volume, float pitch) {
        if (level != null) {
            level.playSound(null, getBlockPos(), soundEvent, SoundSource.BLOCKS, volume, pitch);
        }
    }
}
