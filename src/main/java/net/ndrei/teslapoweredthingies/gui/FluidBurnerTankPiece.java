package net.ndrei.teslapoweredthingies.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.ndrei.teslacorelib.gui.BasicContainerGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslapoweredthingies.TeslaThingiesMod;
import net.ndrei.teslapoweredthingies.machines.FluidBurnerEntity;
import org.lwjgl.opengl.GL11;

/**
 * Created by CF on 2017-01-09.
 */
public class FluidBurnerTankPiece extends BasicContainerGuiPiece {
    private FluidBurnerEntity te;

    public FluidBurnerTankPiece(int left, int top, FluidBurnerEntity te) {
        super(left, top, 18, 34);

        this.te = te;
    }

    @Override
    public void drawBackgroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
        long generated = this.te.getGeneratedPowerCapacity();
        long stored = this.te.getGeneratedPowerStored();
        int percent = Math.round(Math.min(Math.max((float)stored / (float)generated, 0), 1)
                * (this.getHeight() - ((null == this.te.getCoolantInUse()) ? 5 : 2)));
        container.mc.getTextureManager().bindTexture(TeslaThingiesMod.MACHINES_TEXTURES);
        container.drawTexturedRect(this.getLeft(), this.getTop(),
                27, 44, this.getWidth(), this.getHeight());
        if (percent > 0) {
            int h = percent;
            this.drawFluid(container, this.te.getCoolantInUse(), guiX + this.getLeft() + 1, guiY + this.getTop() + 1 + (this.getHeight() - 2 - h), this.getWidth() - 2, h);
        }

        container.mc.getTextureManager().bindTexture(TeslaThingiesMod.MACHINES_TEXTURES);
        container.drawTexturedRect(this.getLeft() + 4, this.getTop() + 5,
                31, 49, this.getWidth() - 8, this.getHeight() - 5);

        if (percent > 0) {
            int h = Math.min(percent, this.getHeight() - 5);
            this.drawFluid(container, this.te.getFuelInUse(), guiX + this.getLeft() + 5, guiY + this.getTop() + 4 + (this.getHeight() - 5 - h), this.getWidth() - 10, h);
        }
        container.mc.getTextureManager().bindTexture(TeslaThingiesMod.MACHINES_TEXTURES);
        container.drawTexturedRect(this.getLeft() + 4, this.getTop() + 5,
                47, 49, this.getWidth() - 8, this.getHeight() - 5);
    }

    private void drawFluid(BasicTeslaGuiContainer container, Fluid fluid, int x, int y, int w, int h) {
        if (fluid == null) {
            return;
        }

        int color = fluid.getColor();
        ResourceLocation still = fluid.getFlowing(); //.getStill(stack);
        if (still != null) {
            TextureAtlasSprite sprite = container.mc.getTextureMapBlocks().getTextureExtry(still.toString());
            container.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GL11.glColor3ub((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF));
            GlStateManager.enableBlend();
            container.drawTexturedModalRect(
                    x, y, sprite, w, h);
            GlStateManager.disableBlend();
        }
    }
}
