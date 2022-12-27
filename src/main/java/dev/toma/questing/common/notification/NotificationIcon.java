package dev.toma.questing.common.notification;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.questing.Questing;
import dev.toma.questing.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class NotificationIcon<T> {

    private static final ResourceLocation KEY_NONE = new ResourceLocation(Questing.MODID, "none");
    private static final ResourceLocation KEY_ITEM = new ResourceLocation(Questing.MODID, "item");
    private static final ResourceLocation KEY_TEXTURE = new ResourceLocation(Questing.MODID, "texture");
    private static final ResourceLocation KEY_SKIN = new ResourceLocation(Questing.MODID, "skin");
    private static final Map<ResourceLocation, IconTypeNetworkSerializer<?>> ICON_TYPES = new HashMap<>();
    private static final NotificationIcon<Object> NONE = new NotificationIcon<>(KEY_NONE, null);
    private final ResourceLocation identifier;
    private final T iconData;

    protected NotificationIcon(ResourceLocation identifier, T iconData) {
        this.identifier = identifier;
        this.iconData = iconData;
    }

    public boolean isNone() {
        return this == NONE;
    }

    @SuppressWarnings("unchecked")
    public void encodeIcon(PacketBuffer buffer) {
        buffer.writeResourceLocation(this.identifier);
        IconTypeNetworkSerializer<T> serializer = (IconTypeNetworkSerializer<T>) ICON_TYPES.get(this.identifier);
        if (serializer == null) {
            Questing.LOGGER.error(Notification.MARKER, "Unregistered icon type: {}", identifier);
            return;
        }
        serializer.encode(buffer, this.getIconData());
    }

    @SuppressWarnings("unchecked")
    public static <T> NotificationIcon<T> decodeIcon(PacketBuffer buffer) {
        ResourceLocation location = buffer.readResourceLocation();
        IconTypeNetworkSerializer<T> serializer = (IconTypeNetworkSerializer<T>) ICON_TYPES.get(location);
        if (serializer == null) {
            Questing.LOGGER.error(Notification.MARKER, "Unregistered icon type: {}", location);
            return none();
        }
        return serializer.decode(buffer);
    }

    @SuppressWarnings("unchecked")
    public static <T> NotificationIcon<T> none() {
        return (NotificationIcon<T>) NONE;
    }

    public static NotificationIcon<?> item(ItemStack stack) {
        return new ItemIcon(stack);
    }

    public static NotificationIcon<?> texture(ResourceLocation iconPath, float u1, float v1, float u2, float v2) {
        return new TextureIcon(iconPath, u1, v1, u2, v2);
    }

    public static NotificationIcon<?> texture(ResourceLocation iconPath) {
        return texture(iconPath, 0.0F, 0.0F, 1.0F, 1.0F);
    }

    public static NotificationIcon<?> skin(UUID player) {
        return new PlayerSkinIcon(player);
    }

    public static <T> void registerNotificationIconType(ResourceLocation location, IconTypeNetworkSerializer<T> networkSerializer) {
        ICON_TYPES.put(Objects.requireNonNull(location), Objects.requireNonNull(networkSerializer));
    }

    @OnlyIn(Dist.CLIENT)
    public void drawIcon(MatrixStack stack, float x, float y, int width, int height) {
    }

    protected T getIconData() {
        return iconData;
    }

    @Override
    public String toString() {
        return "EmptyIcon{}";
    }

    public interface IconTypeNetworkSerializer<T> {

        void encode(PacketBuffer buffer, T type);

        NotificationIcon<T> decode(PacketBuffer buffer);

        static <T> IconTypeNetworkSerializer<T> create(BiConsumer<PacketBuffer, T> encoder, Function<PacketBuffer, NotificationIcon<T>> decoder) {
            return new IconTypeNetworkSerializer<T>() {
                @Override
                public void encode(PacketBuffer buffer, T type) {
                    encoder.accept(buffer, type);
                }

                @Override
                public NotificationIcon<T> decode(PacketBuffer buffer) {
                    return decoder.apply(buffer);
                }
            };
        }
    }

    private static final class TextureIcon extends NotificationIcon<TextureIcon.TextureSpec> {

        private TextureIcon(ResourceLocation iconPath, float u1, float v1, float u2, float v2) {
            super(KEY_TEXTURE, new TextureSpec(iconPath, u1, v1, u2, v2));
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void drawIcon(MatrixStack stack, float x, float y, int width, int height) {
            TextureSpec spec = this.getIconData();
            Minecraft.getInstance().getTextureManager().bind(spec.iconPath);
            RenderUtils.blit(stack, x, y, x + width, y + height, spec.u1, spec.v1, spec.u2, spec.v2);
        }

        @Override
        public String toString() {
            TextureSpec spec = this.getIconData();
            return "TextureIcon{icon=" + spec.iconPath + ",uv[" +
                    spec.u1 + "," + spec.v1 + ";" + spec.u2 + "," + spec.v2 +
                    "]}";
        }

        private static final class TextureSpec {
            private final ResourceLocation iconPath;
            private final float u1, v1, u2, v2;

            public TextureSpec(ResourceLocation iconPath, float u1, float v1, float u2, float v2) {
                this.iconPath = iconPath;
                this.u1 = u1;
                this.v1 = v1;
                this.u2 = u2;
                this.v2 = v2;
            }
        }
    }

    private static final class ItemIcon extends NotificationIcon<ItemStack> {

        public ItemIcon(ItemStack stack) {
            super(KEY_ITEM, stack);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void drawIcon(MatrixStack stack, float x, float y, int width, int height) {
            ItemStack itemStack = this.getIconData();
            Minecraft client = Minecraft.getInstance();
            client.getItemRenderer().renderGuiItem(itemStack, (int) x, (int) y);
        }

        @Override
        public String toString() {
            return "ItemStackIcon{item=" + this.getIconData().toString() + "}";
        }
    }

    private static final class PlayerSkinIcon extends NotificationIcon<UUID> {

        private ResourceLocation cachedLocation;

        public PlayerSkinIcon(UUID playerId) {
            super(KEY_SKIN, playerId);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void drawIcon(MatrixStack stack, float x, float y, int width, int height) {
            Minecraft client = Minecraft.getInstance();
            if (cachedLocation == null) {
                ClientWorld world = client.level;
                AbstractClientPlayerEntity clientPlayer = (AbstractClientPlayerEntity) world.getPlayerByUUID(this.getIconData());
                this.cachedLocation = clientPlayer.getSkinTextureLocation();
            }
            client.getTextureManager().bind(cachedLocation);
            float min =  8.0F / 64.0F;
            float max = 16.0F / 64.0F;
            RenderUtils.blit(stack, x, y, x + width, y + height, min, min, max, max);
        }

        @Override
        public String toString() {
            return "PlayerSkinIcon{playerId=" + this.getIconData() + "}";
        }
    }

    static {
        registerNotificationIconType(KEY_NONE, IconTypeNetworkSerializer.create((buffer, o) -> {}, buffer -> NONE));
        registerNotificationIconType(KEY_ITEM, IconTypeNetworkSerializer.create(PacketBuffer::writeItem, pb -> new ItemIcon(pb.readItem())));
        registerNotificationIconType(KEY_SKIN, IconTypeNetworkSerializer.create(PacketBuffer::writeUUID, pb -> new PlayerSkinIcon(pb.readUUID())));
        registerNotificationIconType(KEY_TEXTURE, IconTypeNetworkSerializer.create((buffer, spec) -> {
            buffer.writeResourceLocation(spec.iconPath);
            buffer.writeFloat(spec.u1);
            buffer.writeFloat(spec.v1);
            buffer.writeFloat(spec.u2);
            buffer.writeFloat(spec.v2);
        }, buffer -> {
            ResourceLocation icon = buffer.readResourceLocation();
            float u1 = buffer.readFloat();
            float v1 = buffer.readFloat();
            float u2 = buffer.readFloat();
            float v2 = buffer.readFloat();
            return new TextureIcon(icon, u1, v1, u2, v2);
        }));
    }
}
