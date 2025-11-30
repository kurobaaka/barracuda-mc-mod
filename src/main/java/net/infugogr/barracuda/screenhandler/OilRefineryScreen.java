package net.infugogr.barracuda.screenhandler;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.infugogr.barracuda.Barracuda;
import net.infugogr.barracuda.util.energy.EnergyCounter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class OilRefineryScreen extends HandledScreen<OilRefineryScreenHandler> {
    private static final Identifier TEXTURE = Barracuda.id("textures/gui/container/oil_refinery_gui.png");

    public OilRefineryScreen(OilRefineryScreenHandler handler, PlayerInventory inventory, Text title) {
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
        context.drawTexture(TEXTURE, x + 161, y + 75 - energySize, 184, 70 - energySize, 7, energySize);

        int burnTime = this.handler.getProgress();
        int fuelTime = this.handler.getMaxProgress();
        float burnTimePercentage = (float) burnTime / fuelTime;
        int burnTimeSize = Math.round(burnTimePercentage * 46);
        context.drawTexture(TEXTURE, this.x + 55, this.y + 24, 176, 0, 7, burnTimeSize);

        int Oil = Math.round(((float) this.handler.getOil() / this.handler.getCapacity()) * 52);
        int Water = Math.round(((float) this.handler.getWater() / this.handler.getCapacity()) * 52);
        int HeavyOil = Math.round(((float) this.handler.getHeavyOil() / this.handler.getCapacity()) * 57);
        int Diesel = Math.round(((float) this.handler.getDiesel() / this.handler.getCapacity()) * 57);
        int Gas = Math.round(((float) this.handler.getGas() / this.handler.getCapacity()) * 57);

        context.drawTexture(TEXTURE, this.x + 37, this.y + 69 - Oil, 200, 52 - Oil, 7, Oil);
        context.drawTexture(TEXTURE, this.x + 46, this.y + 69 - Water, 192, 52 - Water, 7, Water);
        context.drawTexture(TEXTURE, this.x + 80, this.y + 75 - HeavyOil, 208, 129 - HeavyOil, 16, HeavyOil);
        context.drawTexture(TEXTURE, this.x + 98, this.y + 75 - Diesel, 224, 129 - Diesel, 16, Diesel);
        context.drawTexture(TEXTURE, this.x + 116, this.y + 75 - Gas, 240, 129 - Gas, 16, Gas);

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
        if (isPointWithinBounds(37, 17, 16, 69, mouseX, mouseY)) {
            context.drawTooltip(this.textRenderer, Text.literal( this.handler.getOil() / FluidConstants.BUCKET + " / " + this.handler.getCapacity() / FluidConstants.BUCKET), mouseX, mouseY);
        }
    }
}
