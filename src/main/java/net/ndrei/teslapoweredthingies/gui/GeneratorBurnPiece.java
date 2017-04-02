package net.ndrei.teslapoweredthingies.gui;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.darkhax.tesla.lib.TeslaUtils;
import net.ndrei.teslacorelib.gui.BasicContainerGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.tileentities.ElectricGenerator;
import net.ndrei.teslapoweredthingies.TeslaThingiesMod;

import java.util.List;

/**
 * Created by CF on 2017-01-07.
 */
public class GeneratorBurnPiece extends BasicContainerGuiPiece {
    private ElectricGenerator te;

    public GeneratorBurnPiece(int left, int top, ElectricGenerator te) {
        super(left, top, 14, 14);

        this.te = te;
    }

    @Override
    public void drawBackgroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
        container.mc.getTextureManager().bindTexture(TeslaThingiesMod.MACHINES_TEXTURES);

        container.drawTexturedRect(this.getLeft(), this.getTop(), 44, 27, 14, 14);
        long generated = this.te.getGeneratedPowerCapacity();
        long stored = this.te.getGeneratedPowerStored();
        int percent = Math.round(14 * Math.min(Math.max((float)(generated - stored) / (float)generated, 0), 1));
        if (percent > 0) {
            container.drawTexturedRect(this.getLeft(), this.getTop() + percent, 8, 27 + percent, 14, 14 - percent);
        }
    }

    @Override
    public void drawForegroundTopLayer(BasicTeslaGuiContainer container, int guiX, int guiY, int mouseX, int mouseY) {
        if (!this.isInside(container, mouseX, mouseY)) {
            return;
        }

        List<String> lines = GeneratorBurnPiece.getTooltipLines(this.te);
        if ((lines != null) && (lines.size() > 0)) {
            container.drawTooltip(lines, mouseX - guiX, mouseY - guiY);
        }
    }

    public static List<String> getTooltipLines(ElectricGenerator entity) {
        List<String> lines = Lists.newArrayList();
        long generated = entity.getGeneratedPowerCapacity();
        if ((generated > 0) && (entity.getGeneratedPowerStored() > 0)) {
            lines.add(ChatFormatting.GRAY + "Total for fuel: "
                    + ChatFormatting.AQUA + TeslaUtils.getDisplayableTeslaCount(generated));
            lines.add(ChatFormatting.GRAY + "Generating "
                    + ChatFormatting.AQUA + TeslaUtils.getDisplayableTeslaCount(entity.getGeneratedPowerReleaseRate())
                    + ChatFormatting.GRAY + " / tick");

            double ticks = (double)entity.getGeneratedPowerStored() / (double)entity.getGeneratedPowerReleaseRate() / 20.0;
            lines.add(ChatFormatting.GRAY + "~ " + String.format("%.2f", ticks) + "s remaining");
        }
        return lines;
    }
}