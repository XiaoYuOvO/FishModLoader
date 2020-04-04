package net.xiaoyu233.fml.reload.transform.util;

import net.minecraft.Damage;
import net.minecraft.DebugAttack;
import net.minecraft.Entity;
import net.minecraft.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.xiaoyu233.fml.asm.annotations.Link;
import net.xiaoyu233.fml.asm.annotations.Transform;
import net.xiaoyu233.fml.util.ReflectHelper;

import static net.minecraft.DebugAttack.flush;

@Transform(DebugAttack.class)
public class DebugAttackTrans {
    @Link
    private static DebugAttack instance;
    @Link
    private float damage_dealt_to_armor;
    @Link
    private float resulting_damage;
    @Link
    private Entity target;

    public static void start(Entity target, Damage damage) {
        if (target.onClient()) {
            Minecraft.setErrorMessage("DebugAttack.start: called on client?");
        }


        if (instance != null) {
            flush();
        }
        instance = ReflectHelper.createInstance(DebugAttack.class,new Class[]{Entity.class,Damage.class}, target, damage);

    }

    private void flushInstance() {
        if (this.target.onClient()) {
            Minecraft.setErrorMessage("flushInstance: called on client?");
        }

        if (this.damage_dealt_to_armor != 0.0F || this.resulting_damage != 0.0F) {
            MinecraftServer.F().getAuxLogAgent().a(this.toString());
        }

    }
}
