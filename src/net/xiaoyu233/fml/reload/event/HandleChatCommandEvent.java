package net.xiaoyu233.fml.reload.event;

import net.minecraft.EntityPlayer;
import net.minecraft.ICommandListener;
import net.minecraft.World;

public class HandleChatCommandEvent {
    private String command;
    private ICommandListener listener;
    private EntityPlayer player;
    private World world;
    private boolean executeSuccess = false;
    public HandleChatCommandEvent(ICommandListener par1ICommandSender, String par2Str, EntityPlayer player,World world){
        this.listener = par1ICommandSender;
        this.command = par2Str;
        this.player = player;
        this.world = world;
    }

    public ICommandListener getListener() {
        return listener;
    }

    public World getWorld() {
        return world;
    }

    public String getCommand() {
        return command;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public void setExecuteSuccess(boolean executeSuccess) {
        this.executeSuccess = executeSuccess;
    }

    public boolean isExecuteSuccess() {
        return executeSuccess;
    }
}
