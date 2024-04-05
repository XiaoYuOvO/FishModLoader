package net.xiaoyu233.fml.reload.event;

public class SoundsRegisterEvent {

    private final SoundRegisterer registerer;

    public SoundsRegisterEvent(SoundRegisterer registerer) {
        this.registerer = registerer;
    }

    public void register(String path){
        this.registerer.register(path);
    }

    public interface SoundRegisterer {
        void register(String path);
    }
}
