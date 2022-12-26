package dev.toma.questing.utils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;

import java.util.function.BiFunction;

public final class RenderUtils {

    public static final int DEFAULT_DIALOG_BG = 0x66 << 24;

    public static void blit(MatrixStack stack, float x1, float y1, float x2, float y2, float u1, float v1, float u2, float v2) {
        Matrix4f pose = stack.last().pose();
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuilder();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.vertex(pose, x1, y2, 0).uv(u1, v2).endVertex();
        bufferbuilder.vertex(pose, x2, y2, 0).uv(u2, v2).endVertex();
        bufferbuilder.vertex(pose, x2, y1, 0).uv(u2, v1).endVertex();
        bufferbuilder.vertex(pose, x1, y1, 0).uv(u1, v1).endVertex();
        bufferbuilder.end();
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.end(bufferbuilder);
    }

    public static void outline(MatrixStack stack, int x1, int y1, int x2, int y2, int color, int innerWidth, int outerWidth) {
        AbstractGui.fill(stack, x1 - outerWidth, y1 - outerWidth, x2 + outerWidth, y1 + innerWidth, color);
        AbstractGui.fill(stack, x1 - outerWidth, y1 - outerWidth, x1 + innerWidth, y2 + outerWidth, color);
        AbstractGui.fill(stack, x2 + outerWidth, y1 - outerWidth, x2 - innerWidth, y2 + outerWidth, color);
        AbstractGui.fill(stack, x1 - outerWidth, y2 - innerWidth, x2 + outerWidth, y2 + outerWidth, color);
    }

    public static <T> void drawAlignedText(Alignment alignment, MatrixStack stack, T text, int x, int y, int width, int height, int color, BiFunction<FontRenderer, T, Integer> fontSize, TextRenderer<T> renderer) {
        FontRenderer font = Minecraft.getInstance().font;
        drawAlignedText(alignment, stack, font, text, x, y, width, height, color, fontSize, renderer);
    }

    public static <T> void drawAlignedText(Alignment alignment, MatrixStack stack, FontRenderer font, T text, int x, int y, int width, int height, int color, BiFunction<FontRenderer, T, Integer> fontSize, TextRenderer<T> renderer) {
        int size = fontSize.apply(font, text);
        float xText = alignment.getHorizontalPosition(x, width, size);
        float yText = alignment.getVerticalPosition(y, height, font.lineHeight);
        renderer.drawText(font, stack, text, xText, yText, color);
    }

    public static float getCenter(float start, float containerSize, float objectSize) {
        return start + (containerSize - objectSize) / 2.0F;
    }

    public static float linearInterpolate(float last, float current, float partial) {
        return last + (current - last) * partial;
    }

    private RenderUtils() {}

    @FunctionalInterface
    public interface TextRenderer<T> {
        void drawText(FontRenderer font, MatrixStack stack, T text, float x, float y, int color);
    }
}
