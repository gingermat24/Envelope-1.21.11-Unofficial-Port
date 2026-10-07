package io.github.mortuusars.envelope.fabric;

import fuzs.forgeconfigapiport.fabric.api.v5.client.ConfigScreenFactoryRegistry;
import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.EnvelopeClient;
import io.github.mortuusars.envelope.client.gui.screen.*;
import io.github.mortuusars.envelope.client.model.CharredPigeonModel;
import io.github.mortuusars.envelope.client.model.CourierBatModel;
import io.github.mortuusars.envelope.client.model.BatBackpackModel;
import io.github.mortuusars.envelope.client.model.PigeonModel;
import io.github.mortuusars.envelope.client.renderer.TintedTextureCache;
import io.github.mortuusars.envelope.client.renderer.entity.CharredPigeonRenderer;
import io.github.mortuusars.envelope.client.renderer.entity.CourierBatRenderer;
import io.github.mortuusars.envelope.client.renderer.entity.PigeonRenderer;
import io.github.mortuusars.envelope.client.renderer.entity.layer.CharredPigeonBackpackLayer;
import io.github.mortuusars.envelope.client.renderer.entity.layer.BatBackpackLayer;
import io.github.mortuusars.envelope.client.renderer.entity.layer.PigeonBackpackLayer;
import io.github.mortuusars.envelope.client.renderer.entity.layer.PigeonHatLayer;
import io.github.mortuusars.envelope.network.fabric.FabricS2CPacketHandler;
import io.github.mortuusars.envelope.world.item.Sealable;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.reloader.ResourceReloaderKeys;
import net.fabricmc.fabric.api.resource.v1.reloader.SimpleResourceReloader;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

public class EnvelopeFabricClient implements ClientModInitializer {
    private static final Identifier SEAL_TINT_RELOADER = Envelope.resource("seal_tinted_texture_cache");

    @Override
    public void onInitializeClient() {
        EnvelopeClient.init();
        registerSealTintCacheReloader();

        ConfigScreenFactoryRegistry.INSTANCE.register(Envelope.ID, ConfigurationScreen::new);

        EntityRendererRegistry.register(Envelope.EntityTypes.PIGEON.get(), PigeonRenderer::new);
        EntityRendererRegistry.register(Envelope.EntityTypes.CHARRED_PIGEON.get(), CharredPigeonRenderer::new);
        EntityRendererRegistry.register(Envelope.EntityTypes.COURIER_BAT.get(), CourierBatRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(PigeonRenderer.MODEL_LAYER, PigeonModel::createLayerDefinition);
        EntityModelLayerRegistry.registerModelLayer(PigeonRenderer.BABY_MODEL_LAYER, PigeonModel::createBabyLayerDefinition);
        EntityModelLayerRegistry.registerModelLayer(PigeonBackpackLayer.MODEL_LAYER, PigeonModel::createLayerDefinition);
        EntityModelLayerRegistry.registerModelLayer(PigeonHatLayer.MODEL_LAYER, PigeonModel::createLayerDefinition);
        EntityModelLayerRegistry.registerModelLayer(CharredPigeonRenderer.MODEL_LAYER, CharredPigeonModel::createLayerDefinition);
        EntityModelLayerRegistry.registerModelLayer(CharredPigeonRenderer.BABY_MODEL_LAYER, CharredPigeonModel::createBabyLayerDefinition);
        EntityModelLayerRegistry.registerModelLayer(CharredPigeonBackpackLayer.MODEL_LAYER, CharredPigeonModel::createLayerDefinition);
        EntityModelLayerRegistry.registerModelLayer(CourierBatRenderer.MODEL_LAYER, CourierBatModel::createLayerDefinition);
        EntityModelLayerRegistry.registerModelLayer(BatBackpackLayer.MODEL_LAYER, BatBackpackModel::createLayerDefinition);

        BlockRenderLayerMap.putBlock(Envelope.Blocks.LETTER.get(), ChunkSectionLayer.CUTOUT);

        ColorProviderRegistry.BLOCK.register(Sealable::getSealOverlayColor, Envelope.Blocks.SEALED_PACKAGE.get());

        MenuScreens.register(Envelope.MenuTypes.MAILBOX.get(), MailboxScreen::new);
        MenuScreens.register(Envelope.MenuTypes.PACKING.get(), PackingScreen::new);
        MenuScreens.register(Envelope.MenuTypes.PACKAGE.get(), PackageScreen::new);
        MenuScreens.register(Envelope.MenuTypes.PAYBACK_PACKING.get(), PaybackPackingScreen::new);
        MenuScreens.register(Envelope.MenuTypes.PAYBACK_PACKAGE.get(), PaybackPackageScreen::new);
        MenuScreens.register(Envelope.MenuTypes.PAYBACK_TAG.get(), PaybackTagScreen::new);

        FabricS2CPacketHandler.register();
    }

    private static void registerSealTintCacheReloader() {
        ResourceLoader resourceLoader = ResourceLoader.get(PackType.CLIENT_RESOURCES);
        resourceLoader.registerReloader(SEAL_TINT_RELOADER, new SimpleResourceReloader<ResourceManager>() {
            @Override
            protected ResourceManager prepare(SharedState state) {
                return state.resourceManager();
            }

            @Override
            protected void apply(ResourceManager resourceManager, SharedState state) {
                TintedTextureCache.clear();
            }
        });
        resourceLoader.addReloaderOrdering(SEAL_TINT_RELOADER, ResourceReloaderKeys.AFTER_VANILLA);
    }
}
