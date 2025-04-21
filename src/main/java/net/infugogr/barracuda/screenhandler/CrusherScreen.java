package net.infugogr.barracuda.screenhandler;

import com.mojang.blaze3d.systems.RenderSystem;
import net.infugogr.barracuda.Barracuda;
import net.infugogr.barracuda.util.energy.EnergyCounter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class CrusherScreen extends HandledScreen<CrusherScreenHandler> {
    private static final Identifier TEXTURE = Barracuda.id("textures/gui/crusher_gui.png");

    public CrusherScreen(CrusherScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        long energy = this.handler.getEnergy();
        long maxEnergy = this.handler.getMaxEnergy();
        int energySize = MathHelper.ceil((float) energy / maxEnergy * 70);
        context.drawTexture(TEXTURE, x + 152, y + 75 - energySize, 176, 70 - energySize, 16, energySize);

        int burnTime = this.handler.getProgress();
        int fuelTime = this.handler.getMaxProgress();
        float burnTimePercentage = (float) burnTime / fuelTime;
        int burnTimeSize = Math.round(burnTimePercentage * 21);
        context.drawTexture(TEXTURE, this.x + 80, this.y + 35, 192, 0, burnTimeSize, 11);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
        if (isPointWithinBounds(152, 9, 16, 69, mouseX, mouseY)) {
            long energy = this.handler.getEnergy();
            long maxEnergy = this.handler.getMaxEnergy();
            context.drawTooltip(this.textRenderer, Text.literal(EnergyCounter.CounterAh(energy) + " / " + EnergyCounter.CounterAh(maxEnergy)), mouseX, mouseY);
        }
    }
}
