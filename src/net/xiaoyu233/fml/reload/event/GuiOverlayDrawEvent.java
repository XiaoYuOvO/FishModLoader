package net.xiaoyu233.fml.reload.event;

import net.minecraft.GuiIngame;
import net.minecraft.Minecraft;
import net.minecraft.bdi;

public class GuiOverlayDrawEvent {
    private final bdi clientPlayer;
    private final int guiUp;
    private final int var12;
    private final Minecraft minecraft;
    private final GuiIngame guiIngame;

    public GuiOverlayDrawEvent(bdi clientPlayer, int guiUp, int var12, Minecraft minecraft, GuiIngame guiIngame) {
        this.clientPlayer = clientPlayer;
        this.guiUp = guiUp;
        this.var12 = var12;
        this.minecraft = minecraft;
        this.guiIngame = guiIngame;
    }


    public GuiIngame getGuiIngame() {
        return guiIngame;
    }

    public Minecraft getMinecraft() {
        return minecraft;
    }

    public int getVar12() {
        return var12;
    }

    public int getGuiUp() {
        return guiUp;
    }

    public bdi getClientPlayer() {
        return clientPlayer;
    }
}
