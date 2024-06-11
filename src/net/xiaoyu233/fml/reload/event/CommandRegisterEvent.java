package net.xiaoyu233.fml.reload.event;

import net.minecraft.ICommand;

import java.util.function.Consumer;

public class CommandRegisterEvent {
    private final Consumer<ICommand> registerer;

    public CommandRegisterEvent(Consumer<ICommand> registerer) {
        this.registerer = registerer;
    }

    public void register(ICommand command){
        registerer.accept(command);
    }
}
