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

public class ChemicalPlantScreen extends HandledScreen<ChemicalPlantScreenHandler> {
    private static final Identifier TEXTURE = Barracuda.id("textures/gui/container/chemical_plant_gui.png");

    public ChemicalPlantScreen(ChemicalPlantScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
    }

    int getFluidInt (int i){
        return switch (i){
            case 2 -> 0;
            case 5 -> 1;
            case 7 -> 2;
            case 9 -> 3;
            case 11 -> 4;
            default -> 5;
        };
    }
    String getFluidString (int i){
        return switch (i){
            case 2 -> "Water ";
            case 5 -> "Crude Oil ";
            case 7 -> "Heavy Oil ";
            case 9 -> "Diesel ";
            case 11 -> "Gas ";
            default -> "Empty ";
        };
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
        context.drawTexture(TEXTURE, x + 161, y + 75 - energySize, 176, 70 - energySize, 7, energySize);

        int burnTime = this.handler.getProgress();
        int fuelTime = this.handler.getMaxProgress();
        float burnTimePercentage = (float) burnTime / fuelTime;
        int burnTimeSize = Math.round(burnTimePercentage * 22);
        context.drawTexture(TEXTURE, this.x + 82, this.y + 37, 183, 0, burnTimeSize, 16);

        int Input = Math.round(((float) this.handler.getInputFluid() / this.handler.getCapacity()) * 58);
        int Output = Math.round(((float) this.handler.getOutputFluid() / this.handler.getCapacity()) * 58);

        context.drawTexture(TEXTURE, x + 62, y + 75 - Input, 176 + getFluidInt(this.handler.getInputFluidVariant()) * 16, 128 - Input, 16, Input);
        context.drawTexture(TEXTURE, x + 108, y + 75 - Output, 176 + getFluidInt(this.handler.getOutputFluidVariant())  * 16, 128 - Output, 16, Output);
        // 0xFF060B18 черни 0xFF9A650C diesel  0xFF62311F mazut  0xFFD6D6D6 gas  0x0FF2E58D3 water
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
        if (isPointWithinBounds(161, 5, 9, 72, mouseX, mouseY)) {
            long energy = this.handler.getEnergy();
            long maxEnergy = this.handler.getMaxEnergy();
            context.drawTooltip(this.textRenderer, Text.literal(EnergyCounter.CounterAh(energy) + " / " + EnergyCounter.CounterAh(maxEnergy)), mouseX, mouseY);
        }
        if (isPointWithinBounds(62, 17, 18, 60, mouseX, mouseY)) {
            context.drawTooltip(this.textRenderer, Text.literal(getFluidString(this.handler.getInputFluidVariant()) + this.handler.getInputFluid() / FluidConstants.BUCKET + " / " + this.handler.getCapacity() / FluidConstants.BUCKET), mouseX, mouseY);
        }
        if (isPointWithinBounds(108, 17, 18, 60, mouseX, mouseY)) {
            context.drawTooltip(this.textRenderer, Text.literal( getFluidString(this.handler.getOutputFluidVariant()) + this.handler.getOutputFluid() / FluidConstants.BUCKET + " / " + this.handler.getCapacity() / FluidConstants.BUCKET), mouseX, mouseY);
        }
    }
}
