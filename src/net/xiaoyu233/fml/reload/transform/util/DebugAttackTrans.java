package net.xiaoyu233.fml.reload.transform.util;

import net.minecraft.Damage;
import net.minecraft.DebugAttack;
import net.minecraft.Entity;
import net.minecraft.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static net.minecraft.DebugAttack.flush;

@Mixin(DebugAttack.class)
public class DebugAttackTrans {
    @Shadow
    private static DebugAttack instance;
    @Shadow
    private float damage_dealt_to_armor;
    @Shadow
    private float resulting_damage;
    @Shadow
    private Entity target;

    @Overwrite
    public static void start(Entity target, Damage damage) {
        if (target.onClient()) {
            Minecraft.setErrorMessage("DebugAttack.start: called on client?");
        }


        if (instance != null) {
            flush();
        }
        instance = ReflectHelper.createInstance(DebugAttack.class,new Class[]{Entity.class,Damage.class}, target, damage);

    }

    @Overwrite
    private void flushInstance() {
        if (this.target.onClient()) {
            Minecraft.setErrorMessage("flushInstance: called on client?");
        }

        if (this.damage_dealt_to_armor != 0.0F || this.resulting_damage != 0.0F) {
            MinecraftServer.F().getLogAgent().logInfo(this.toString());
        }

    }
}
