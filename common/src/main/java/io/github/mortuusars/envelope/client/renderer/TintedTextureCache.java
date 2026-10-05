package io.github.mortuusars.envelope.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.util.TintColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public final class TintedTextureCache {
    private static final int MAX_CACHED_TEXTURES = 128;
    private static final Map<Key, Identifier> TEXTURES = new LinkedHashMap<>(16, 0.75F, true);
    private static long nextTextureId;

    private TintedTextureCache() {
    }

    public static Identifier get(Identifier source, TintColor tint) {
        Key key = new Key(source, tint);
        Identifier cached = TEXTURES.get(key);
        if (cached != null) {
            return cached;
        }

        Identifier textureId = Envelope.resource("dynamic/seal_tint/" + nextTextureId++);
        DynamicTexture texture = createTexture(source, tint);
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        textureManager.register(textureId, texture);
        TEXTURES.put(key, textureId);

        if (TEXTURES.size() > MAX_CACHED_TEXTURES) {
            Iterator<Identifier> iterator = TEXTURES.values().iterator();
            Identifier leastRecentlyUsed = iterator.next();
            iterator.remove();
            textureManager.release(leastRecentlyUsed);
        }

        return textureId;
    }

    public static void clear() {
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        for (Identifier texture : TEXTURES.values()) {
            textureManager.release(texture);
        }
        TEXTURES.clear();
    }

    private static DynamicTexture createTexture(Identifier sourceId, TintColor tint) {
        Resource resource = Minecraft.getInstance().getResourceManager().getResource(sourceId)
                .orElseThrow(() -> new IllegalStateException("Missing seal impression texture: " + sourceId));

        try (NativeImage source = NativeImage.read(resource.open())) {
            NativeImage tinted = new NativeImage(source.getWidth(), source.getHeight(), false);
            try {
                for (int y = 0; y < source.getHeight(); y++) {
                    for (int x = 0; x < source.getWidth(); x++) {
                        tinted.setPixel(x, y, tint.tint(source.getPixel(x, y)));
                    }
                }
                return new DynamicTexture(() -> "Seal tint " + sourceId, tinted);
            } catch (RuntimeException | Error exception) {
                tinted.close();
                throw exception;
            }
        } catch (IOException exception) {
            throw new UncheckedIOException("Failed to tint seal impression texture " + sourceId, exception);
        }
    }

    private record Key(Identifier source, TintColor tint) {
    }
}
