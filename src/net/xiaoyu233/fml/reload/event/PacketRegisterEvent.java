package net.xiaoyu233.fml.reload.event;

public class PacketRegisterEvent {
    private final RegisterFunction registerer;

    public PacketRegisterEvent(RegisterFunction registerer) {
        this.registerer = registerer;
    }

    public void register(int id, boolean clientProcess, boolean serverProcess, Class<?> packetClass) {
        this.registerer.register(id, clientProcess, serverProcess, packetClass);
    }

    @FunctionalInterface
    public interface RegisterFunction {
        void register(int id, boolean clientProcess, boolean serverProcess, Class<?> packetClass);
    }
}
