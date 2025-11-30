package net.infugogr.barracuda.screenhandler;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.infugogr.barracuda.Barracuda;
import net.infugogr.barracuda.block.entity.CircuitImprinterBlockEntity;
import net.infugogr.barracuda.block.recipes.CircuitImprinterRecipes;
import net.infugogr.barracuda.util.energy.EnergyCounter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.PlaySoundCommand;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.Objects;

public class CircuitImprinterScreen extends HandledScreen<CircuitImprinterScreenHandler> {
    private static final Identifier IMPRINTER_SLOT_HIGHLIGHTED_TEXTURE = Barracuda.id("container/imprinter_slot_highlighted");
    private static final Identifier IMPRINTER_SLOT_TEXTURE = Barracuda.id("container/imprinter_slot");
    private static final Identifier TEXTURE = Barracuda.id("textures/gui/container/circuit_imprinter_gui.png");

    public CircuitImprinterScreen(CircuitImprinterScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 184;
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
        playerInventoryTitleX = 1000;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        long energy = this.handler.getEnergy();
        long maxEnergy = this.handler.getMaxEnergy();
        int energySize = MathHelper.ceil((float) energy / maxEnergy * 70);
        context.fill(x + 161, y + 85 - energySize, x+168, y +85, 0xFFDC4242);

        int burnTime = this.handler.getProgress();
        int fuelTime = this.handler.getMaxProgress();
        float burnTimePercentage = (float) burnTime / fuelTime;
        int burnTimeSize = Math.round(burnTimePercentage * 70);
        context.fill(x + 152, y + 85 - burnTimeSize, x+159, y +85, 0xFFD8DB42);

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 5; col++) {
                int iconX = x + 44 + col * 18;
                int iconY = y + 15 + row * 18;
                if (!isPointWithinBounds(44 + col * 18, 15 + row * 18, 16, 16, mouseX, mouseY)) {
                    context.drawGuiTexture(IMPRINTER_SLOT_TEXTURE, iconX, iconY, 16, 16);
                } else {
                    context.drawGuiTexture(IMPRINTER_SLOT_HIGHLIGHTED_TEXTURE, iconX, iconY, 16, 16);
                }
            }
        }
    }

    private ItemStack getRecipeResult(int index) {
        // TODO: Получи результат рецепта по индексу
        return CircuitImprinterRecipes.getRecipeOutput(index);
    }

    private List<Text> getTooltipForRecipe(int index) {
        // TODO: Возврати описание необходимых предметов
        return CircuitImprinterRecipes.getRecipeTooltip(index);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int recipeIndex = 0;
        assert this.client != null;
        assert Objects.requireNonNull(this.client).interactionManager != null;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 5; col++) {
                int iconX = x + 44 + col * 18;
                int iconY = y + 15 + row * 18;
                if (recipeIndex < CircuitImprinterRecipes.getCountRecipe()) {
                    if (mouseX >= iconX
                            && mouseX <= iconX + 16
                            && mouseY >= iconY
                            && mouseY <= iconY + 16
                            && this.handler.onButtonClick(this.client.player, row * 5 + col)) {
                            this.client.interactionManager.clickButton(this.handler.syncId, row * 5 + col);
                            return true;
                    }
                    recipeIndex++;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
        int recipeIndex = 0;
            for (int row = 0; row < 4; row++) {
                for (int col = 0; col < 5; col++) {
                    int iconX = x + 44 + col * 18;
                    int iconY = y + 15 + row * 18;
                    if (recipeIndex < CircuitImprinterRecipes.getCountRecipe()) {
                        ItemStack result = getRecipeResult(recipeIndex);
                        context.drawItem(result, iconX, iconY);
                        if (isPointWithinBounds(44 + col * 18, 15 + row * 18, 16, 16, mouseX, mouseY)) {
                            context.drawTooltip(this.textRenderer, getTooltipForRecipe(recipeIndex), mouseX, mouseY);
                        }
                        recipeIndex++;
                    }
                }
            }
        if (isPointWithinBounds(152, 9, 16, 69, mouseX, mouseY)) {
            long energy = this.handler.getEnergy();
            long maxEnergy = this.handler.getMaxEnergy();
            context.drawTooltip(this.textRenderer, Text.literal(EnergyCounter.CounterAh(energy) + " / " + EnergyCounter.CounterAh(maxEnergy)), mouseX, mouseY);
        }
    }
}
