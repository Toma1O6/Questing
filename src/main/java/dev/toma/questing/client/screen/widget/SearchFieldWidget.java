package dev.toma.questing.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.questing.utils.Alignment;
import dev.toma.questing.utils.RenderUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SearchFieldWidget<IN> extends TextFieldWidget {

    private final Supplier<List<IN>> inputDataProvider;
    private Function<IN, String> toStringFormatter;
    private BiPredicate<IN, String> filter;
    private int suggestionsSize = 5;

    private List<IN> results = Collections.emptyList();
    private int tabIndex = -1;

    public SearchFieldWidget(FontRenderer font, int x, int y, int width, int height, Supplier<List<IN>> inputDataProvider) {
        super(font, x, y, width, height, StringTextComponent.EMPTY);
        this.inputDataProvider = inputDataProvider;
        this.toStringFormatter = Object::toString;
        this.filter = (in, query) -> query.isEmpty() || toStringFormatter.apply(in).toLowerCase().contains(query.toLowerCase());
        this.setResponder(text -> {
            this.tabIndex = -1;
            results = inputDataProvider.get().stream()
                    .filter(element -> this.filter.test(element, text))
                    .collect(Collectors.toList());
            setSuggestion(text.isEmpty() ? getMessage().getString() : null);
        });
    }

    public void suggests(int amount) {
        this.suggestionsSize = amount;
    }

    public void setTextFormatter(Function<IN, String> formatter) {
        toStringFormatter = Objects.requireNonNull(formatter);
    }

    public void setFilter(BiPredicate<IN, String> filter) {
        this.filter = filter;
    }

    public void assignDefaultValue() {
        if (inputDataProvider.get().size() == 1) {
            setValue(toStringFormatter.apply(inputDataProvider.get().get(0)));
        }
    }

    @Override
    public void setMessage(ITextComponent message) {
        super.setMessage(message);
        if (this.getValue().isEmpty()) {
            this.setSuggestion(message.getString());
        }
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.renderButton(stack, mouseX, mouseY, partialTicks);
        if (this.isFocused() && suggestionsSize > 0) {
            for (int i = 0; i < suggestionsSize; i++) {
                if (i >= results.size())
                    break;
                IN result = this.results.get(i);
                String displayText = toStringFormatter.apply(result);
                boolean tabSelected = tabIndex == i;
                int suggestHeight = 10;
                int sy1 = y + height + 1 + i * suggestHeight;
                int sy2 = sy1 + suggestHeight;
                fill(stack, x, sy1, x + width, sy2, tabSelected ? 0xFF00005C : 0xFF000010);
                RenderUtils.drawAlignedText(Alignment.VERTICAL, stack, displayText, x + 1, sy1 + 1, width, suggestHeight, 0xFFFFFF, FontRenderer::width, tabSelected ? FontRenderer::drawShadow : FontRenderer::draw);
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_TAB) {
                this.tabIndex = (++this.tabIndex) % Math.min(results.size(), suggestionsSize);
                if (results.size() == 0) {
                    this.tabIndex = -1;
                }
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                if (tabIndex >= 0 && tabIndex < results.size()) {
                    IN resultVal = this.results.get(tabIndex);
                    String text = toStringFormatter.apply(resultVal);
                    this.setValue(text);
                    this.results = Collections.singletonList(resultVal);
                }
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public List<IN> getResults() {
        return results;
    }
}
