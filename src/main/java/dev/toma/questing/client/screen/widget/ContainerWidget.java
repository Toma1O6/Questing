package dev.toma.questing.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.INestedGuiEventHandler;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;

public class ContainerWidget extends Widget implements INestedGuiEventHandler {

    private final List<IGuiEventListener> listeners = new ArrayList<>();
    private final List<Widget> widgets = new ArrayList<>();
    private IGuiEventListener focused;
    private boolean dragging;

    public ContainerWidget(int x, int y, int width, int height, ITextComponent title) {
        super(x, y, width, height, title);
    }

    public <L extends IGuiEventListener> L addGuiEventListener(L listener) {
        this.listeners.add(listener);
        return listener;
    }

    public void removeGuiEventListener(IGuiEventListener listener) {
        listeners.remove(listener);
    }

    public <W extends Widget> W addWidget(W widget) {
        widgets.add(widget);
        return addGuiEventListener(widget);
    }

    public void removeWidget(Widget widget) {
        widgets.remove(widget);
        removeGuiEventListener(widget);
    }

    public void clear() {
        listeners.clear();
        widgets.clear();
        focused = null;
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        widgets.forEach(widget -> widget.render(stack, mouseX, mouseY, partialTicks));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return INestedGuiEventHandler.super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return INestedGuiEventHandler.super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return INestedGuiEventHandler.super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return INestedGuiEventHandler.super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        INestedGuiEventHandler.super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return INestedGuiEventHandler.super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return INestedGuiEventHandler.super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public List<? extends IGuiEventListener> children() {
        return listeners;
    }

    @Override
    public boolean isDragging() {
        return dragging;
    }

    @Override
    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    @Override
    public IGuiEventListener getFocused() {
        return focused;
    }

    @Override
    public void setFocused(IGuiEventListener focused) {
        this.focused = focused;
    }
}
