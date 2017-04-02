package net.ndrei.teslapoweredthingies.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.compatibility.FontRendererUtil;
import net.ndrei.teslacorelib.compatibility.ItemStackUtil;
import net.ndrei.teslacorelib.gui.BasicContainerGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;

/**
 * Created by CF on 2017-01-08.
 */
public class ItemStackPiece extends BasicContainerGuiPiece {
    private IWorkItemProvider provider;

    public ItemStackPiece(int left, int top, int width, int height, IWorkItemProvider provider) {
        super(left, top, width, height);

        this.provider = provider;
    }

    @Override
    public void drawMiddleLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
        ItemStack stack = (this.provider == null) ? ItemStack.EMPTY : this.provider.getWorkItem();
        if (!ItemStackUtil.isEmpty(stack)) {
            int x = this.getLeft() + (this.getWidth() - 16) / 2 + 1;
            int y = this.getTop() + (this.getHeight() - 16) / 2;

            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.pushMatrix();
            GlStateManager.enableDepth();
            container.getItemRenderer().renderItemAndEffectIntoGUI(stack, guiX + x, guiY + y);
            container.getItemRenderer().renderItemOverlayIntoGUI(FontRendererUtil.getFontRenderer(), stack, x, y, null);
            GlStateManager.popMatrix();
            RenderHelper.disableStandardItemLighting();
        }
    }
}
