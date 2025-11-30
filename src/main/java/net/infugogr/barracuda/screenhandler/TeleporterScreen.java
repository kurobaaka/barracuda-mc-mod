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

public class TeleporterScreen extends HandledScreen<TeleporterScreenHandler> {
    private static final Identifier TEXTURE = Barracuda.id("textures/gui/container/teleporter_gui.png");

    public TeleporterScreen(TeleporterScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, Text.empty());
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = 1000;
        this.playerInventoryTitleX = 1000;
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
        int segmentCount = 10; // Всего 10 столбцов
        int segmentHeight = 51; // Высота каждого столбца
        int segmentWidth = 6;   // Ширина столбца
        int startX = 14;       // Начальная позиция X
        int startY = 64;       // Нижняя граница столбцов
        int normalSpacing = 9;  // Обычный отступ
        int specialSpacing = 62; // Большой отступ после 5 столбцов
        if (energy > maxEnergy - 10000){
            context.drawTexture(TEXTURE, x + 4, y + 4, 0, 187, 168, 69);
        }

        for (int i = 0; i < segmentCount; i++) {
            // Рассчитываем позицию X с учетом специального отступа
            int xPos;
            if (i < 5) {
                xPos = startX + i * normalSpacing;
            } else {
                xPos = startX + 5 * normalSpacing + specialSpacing + (i - 5) * normalSpacing;
            }

            // Определяем заполнение для текущего столбца
            long segmentStart = i * 1_000_000L;
            if (energy >= segmentStart) {
                // Вычисляем высоту заполнения (не менее 0 и не более segmentHeight)
                long segmentEnergy = Math.min(energy - segmentStart, 1_000_000L);
                int fillHeight = (int) Math.min(segmentHeight,
                        Math.max(0, segmentEnergy * segmentHeight / 1_000_000L));

                // Отрисовываем столбец
                context.fill(
                        x+xPos,
                        y+startY,
                        x+xPos + segmentWidth,
                        y+startY - fillHeight,
                        0xFFD8372F
                );
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
        if (isPointWithinBounds(14, 13, 43, 52, mouseX, mouseY)) {
            long energy = this.handler.getEnergy();
            long maxEnergy = this.handler.getMaxEnergy();
            context.drawTooltip(this.textRenderer, Text.literal(EnergyCounter.CounterAh(energy) + " / " + EnergyCounter.CounterAh(maxEnergy)), mouseX, mouseY);
        }
        if (isPointWithinBounds(121, 13, 43, 52, mouseX, mouseY)) {
            long energy = this.handler.getEnergy();
            long maxEnergy = this.handler.getMaxEnergy();
            context.drawTooltip(this.textRenderer, Text.literal(EnergyCounter.CounterAh(energy) + " / " + EnergyCounter.CounterAh(maxEnergy)), mouseX, mouseY);
        }
    }
}
