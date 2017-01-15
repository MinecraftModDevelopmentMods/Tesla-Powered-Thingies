package net.ndrei.teslapoweredthingies.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.ndrei.teslacorelib.tileentities.ElectricTileEntity;
import net.ndrei.teslapoweredthingies.TeslaThingiesMod;
import org.lwjgl.opengl.GL11;

/**
 * Created by CF on 2017-01-10.
 */
public class DualTankEntityRenderer extends TileEntitySpecialRenderer<ElectricTileEntity> {
    public void renderTileEntityAt(ElectricTileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
        IDualTankMachine machine = (IDualTankMachine)te;

        GlStateManager.pushMatrix();

        GlStateManager.translate((float) x + 0.5F, (float) y + 1.0F, (float) z + 0.5F);
        switch(te.getFacing()) {
            case NORTH:
                GlStateManager.rotate(180, 0.0F, 1.0F, 0.0F);
                break;
            case WEST:
                GlStateManager.rotate(-90, 0.0F, 1.0F, 0.0F);
                break;
            case EAST:
                GlStateManager.rotate(90, 0.0F, 1.0F, 0.0F);
                break;
            case SOUTH:
                // GlStateManager.rotate(0, 0.0F, 1.0F, 0.0F);
                break;
        }
        GlStateManager.translate(-0.5D, 0.0D, 0.501D);

        super.setLightmapDisabled(true);

        float magicNumber = 0.03125f;
        GlStateManager.scale(magicNumber, -magicNumber, magicNumber);
        // GlStateManager.glNormal3f(0.0F, 0.0F, 1.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        this.drawTank(5.0f, machine.getLeftTankFluid(), machine.getLeftTankPercent());
        this.drawTank(19.0f, machine.getRightTankFluid(), machine.getRightTankPercent());

        super.setLightmapDisabled(false);
        GlStateManager.popMatrix();
    }

    private void drawTank(float tankX, Fluid fluid, float fluidPercent) {
        if (fluidPercent == 0.0f) {
            return;
        }

        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableLighting();

        if (fluid != null) {
            if (fluidPercent > 0) {
                ResourceLocation fluidTexture = fluid.getFlowing();
                if (fluidTexture != null) {
                    TextureAtlasSprite fluidSprite = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluidTexture.toString());

                    int color = fluid.getColor();
                    GlStateManager.color((color >> 16 & 0xFF) / 255.0f, (color >> 8 & 0xFF) / 255.0f, (color & 0xFF) / 255.0f);
                    super.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                    float height = 14.02f * fluidPercent;
                    this.drawRectangle(
                            tankX + 0.99f, 5.99f + 14.02f - height, 6.02f, height,
                            fluidSprite.getMinU(), fluidSprite.getMinV(), fluidSprite.getMaxU(), fluidSprite.getMaxV());
                }
            }
        }

        GlStateManager.translate(0.0F, 0F, 0.001F);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        super.bindTexture(TeslaThingiesMod.MACHINES_TEXTURES);

        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        drawRectangle(
                tankX, 5f, 8f, 16f,
                (23.0f + tankX) / 256.0f, 81.0f / 256.0f, (31.0f + tankX) / 256.0f, 97.0f / 256.0f);

        GlStateManager.translate(0.0F, 0F, -0.001F);

        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    private void drawRectangle(float x, float y, float width, float height, float minU, float minV, float maxU, float maxV) {
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        buffer.pos(x, y, 0.0D).tex(minU, minV).endVertex();
        buffer.pos(x, y + height, 0.0D).tex(minU, maxV).endVertex();
        buffer.pos(x + width, y + height, 0.0D).tex(maxU, maxV).endVertex();
        buffer.pos(x + width, y, 0.0D).tex(maxU, minV).endVertex();

        tessellator.draw();
    }
}
