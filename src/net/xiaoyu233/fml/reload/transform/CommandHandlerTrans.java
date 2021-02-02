package net.xiaoyu233.fml.reload.transform;

import net.minecraft.*;
import net.minecraft.server.MinecraftServer;
import net.xiaoyu233.fml.asm.annotations.Link;
import net.xiaoyu233.fml.asm.annotations.Marker;
import net.xiaoyu233.fml.asm.annotations.Transform;
import net.xiaoyu233.fml.reload.event.HandleChatCommandEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.minecraft.CommandHandler.spawning_disabled;

@Transform(CommandHandler.class)
public class CommandHandlerTrans {
    @Link
    private Map a;

    public int executeCommand(ICommandListener par1ICommandSender, String par2Str, boolean permission_override) {
        par2Str = par2Str.trim();
        if (par2Str.startsWith("/")) {
            par2Str = par2Str.substring(1);
        }

        MinecraftServer mc_server = MinecraftServer.F();
        WorldServer world = (WorldServer)par1ICommandSender.f_();
        EntityPlayer player = (EntityPlayer)world.a(par1ICommandSender.c_());
        EnumCommand command = EnumCommand.get0(par2Str);
        HandleChatCommandEvent event = new HandleChatCommandEvent(par1ICommandSender,par2Str,player,world);
        net.xiaoyu233.fml.reload.event.MITEEvents.MITE_EVENT_BUS.post(event);
        if (event.isExecuteSuccess()){
            return 1;
        }
        if (command == EnumCommand.version) {
            par1ICommandSender.a(
                    ChatMessage.d(
                            LocaleI18n.a("你现在正在游玩 ") + "MITE-ITE 1.6.4 R196 "+ (Minecraft.inDevMode() ? EnumChatFormat.m + " DEV" : "")).a(EnumChatFormat.o));
            return 1;
        } else {
            WorldData info;
            if (command == EnumCommand.versions) {
                info = par1ICommandSender.f_().N();
                if (info.getEarliestMITEReleaseRunIn() == info.getLatestMITEReleaseRunIn()) {
                    par1ICommandSender.a(ChatMessage.d("This world has been played in MITE R" + info.getEarliestMITEReleaseRunIn() + " only").a(EnumChatFormat.o));
                } else {
                    par1ICommandSender.a(ChatMessage.d("This world has been played in MITE releases R" + info.getEarliestMITEReleaseRunIn() + " to R" + info.getLatestMITEReleaseRunIn()).a(EnumChatFormat.o));
                }

                return 1;
            } else if (command == EnumCommand.villages) {
                info = par1ICommandSender.f_().N();
                if (info.getVillageConditions() < WorldData.getVillagePrerequisites()) {
                    par1ICommandSender.a(ChatMessage.d("Villages can generate after the following conditions are met:").a(EnumChatFormat.o));
                    if (!BitHelper.isBitSet(info.getVillageConditions(), 16)) {
                        par1ICommandSender.a(ChatMessage.d("- Craft an iron pickaxe or war hammer (or better)").a(EnumChatFormat.o));
                    }
                } else if (mc_server.b[0].getDayOfWorld() < 60) {
                    par1ICommandSender.a(ChatMessage.d("Villages can generate at day 60").a(EnumChatFormat.o));
                } else {
                    par1ICommandSender.a(ChatMessage.d("Villages can generate").a(EnumChatFormat.o));
                }

                return 1;
            } else {
                int x;
                int dx1;
                if (command == EnumCommand.tournament) {
                    if (DedicatedServer.isTournament()) {
                        par1ICommandSender.a(ChatMessage.d(DedicatedServer.getTournamentObjective()).a(EnumChatFormat.o));
                        return 1;
                    }
                } else {
                    if (command == EnumCommand.day) {
                        par1ICommandSender.a(ChatMessage.d(("现在是这个世界的第 " + mc_server.b[0].getDayOfWorld() + " 天")).a(EnumChatFormat.o));
                        return 1;
                    }

                    if (command == EnumCommand.mem) {
                        par1ICommandSender.a(ChatMessage.d("The server is using " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024L / 1024L + "MB of memory (" + Runtime.getRuntime().totalMemory() / 1024L / 1024L + "MB is allocated)").a(EnumChatFormat.o));
                        return 1;
                    }

                    int x1;
                    if (command == EnumCommand.load) {
                        x1 = (int)(mc_server.getLoadOnServer() * 100.0F);
                        if (x1 < 0) {
                            par1ICommandSender.a(ChatMessage.d("The load on the server is still being calculated").a(EnumChatFormat.o));
                        } else {
                            par1ICommandSender.a(ChatMessage.d("The server is at " + x1 + "% of its processing limit").a(EnumChatFormat.o));
                        }

                        return 1;
                    }

                    int z;
                    if (command == EnumCommand.chunks) {
                        x1 = 0;
                        WorldServer[] world_servers = mc_server.b;

                        for(z = 0; z < world_servers.length; ++z) {
                            if (world_servers[z] != null) {
                                x1 += world_servers[z].g.size();
                            }
                        }

                        par1ICommandSender.a(ChatMessage.d("There are " + x1 + " chunks loaded").a(EnumChatFormat.o));
                        return 1;
                    }

                    if (command == EnumCommand.commands) {
                        player.sendPacket(new Packet85SimpleSignal(EnumSignal.list_commands));
                        return 1;
                    }

                    StringBuffer sb;
                    if (command == EnumCommand.skills) {
                        if (world.areSkillsEnabled()) {
                            sb = new StringBuffer("Available skills are: ");
//                            sb.append(Skill.getSkillsStr(-1, false, ", "));
                            par1ICommandSender.a(ChatMessage.d(sb.toString()).a(EnumChatFormat.o));
                            par1ICommandSender.a(ChatMessage.d("Type /skill <skill> for more information").a(EnumChatFormat.h));
                        } else {
                            par1ICommandSender.a(ChatMessage.d("Skills are not enabled").a(EnumChatFormat.m));
                        }

                        return 1;
                    }

                    String param;
                    Skill skill;
                    if (par2Str.startsWith("skill ")) {
                        if (world.areSkillsEnabled()) {
                            param = par2Str.substring(6);
                            skill = Skill.getByLcName(param, false);
                            if (skill == null) {
                                skill = Skill.getByLcName(param, true);
                            }

                            if (skill == null) {
                                par1ICommandSender.a(ChatMessage.d("Invalid skill name \"" + param + "\"").a(EnumChatFormat.m));
                            } else {
                                par1ICommandSender.a(ChatMessage.d(skill.getLocalizedDescription()).a(EnumChatFormat.o));
                                if (player != null) {
                                    if (player.hasSkill(skill)) {
                                        par1ICommandSender.a(ChatMessage.d("Type /abandon <skill> to forget a skill").a(EnumChatFormat.h));
                                    } else {
                                        par1ICommandSender.a(ChatMessage.d("Type /learn <skill> to gain a skill").a(EnumChatFormat.h));
                                    }
                                }
                            }
                        } else {
                            par1ICommandSender.a(ChatMessage.d("Skills are not enabled").a(EnumChatFormat.m));
                        }

                        return 1;
                    }

                    if (Minecraft.inDevMode() && par2Str.equals("dragon")){
                        par1ICommandSender.a(ChatMessage.d("Dragon spawned").a(EnumChatFormat.o));
                        EntityEnderDragon dragon = new EntityEnderDragon(player.q);
                        player.q.d(dragon);
                        dragon.setPosition(player.u,player.v,player.w,false);
                        return 1;
                    }

                    if (Minecraft.inDevMode() && par2Str.equals("skills on")) {
                        par1ICommandSender.a(ChatMessage.d(world.areSkillsEnabled() ? "Skills are already enabled" : "Skills are now enabled").a(EnumChatFormat.o));
                        if (!world.N().areSkillsEnabled()) {
                            world.N().setSkillsEnabled(true);
                            player.sendPacket((new Packet85SimpleSignal(EnumSignal.skills)).setBoolean(true));
                        }

                        return 1;
                    }

                    if (Minecraft.inDevMode() && par2Str.equals("skills off")) {
                        par1ICommandSender.a(ChatMessage.d(world.areSkillsEnabled() ? "Skills are now disabled" : "Skills are already disabled").a(EnumChatFormat.o));
                        if (world.N().areSkillsEnabled()) {
                            world.N().setSkillsEnabled(false);
                            player.sendPacket((new Packet85SimpleSignal(EnumSignal.skills)).setBoolean(false));
                        }

                        return 1;
                    }

                    if ("mute".equals(par2Str)) {
                        player.sendPacket(new Packet85SimpleSignal(EnumSignal.toggle_mute));
                        return 1;
                    }

                    int y;
                    if ((player == null || Minecraft.inDevMode()) && par2Str.equals("time")) {
                        sb = new StringBuffer("Time Progressing? ");

                        WorldServer world_server;
                        for(y = 0; y < mc_server.b.length; ++y) {
                            if (y > 0) {
                                sb.append(", ");
                            }

                            world_server = mc_server.b[y];
                            sb.append(world_server.getDimensionName());
                            sb.append("=");
                            sb.append(StringHelper.capitalize(StringHelper.yesOrNo(world_server.shouldTimeProgress())));
                        }

                        par1ICommandSender.a(ChatMessage.d(sb.toString()).a(EnumChatFormat.o));
                        sb = new StringBuffer("Random Block Ticks? ");

                        for(y = 0; y < mc_server.b.length; ++y) {
                            if (y > 0) {
                                sb.append(", ");
                            }

                            world_server = mc_server.b[y];
                            sb.append(world_server.getDimensionName());
                            sb.append("=");
                            sb.append(StringHelper.capitalize(StringHelper.yesOrNo(world_server.shouldRandomBlockTicksBePerformed())));
                        }

                        par1ICommandSender.a(ChatMessage.d(sb.toString()).a(EnumChatFormat.o));
                        sb = new StringBuffer("Time Forwarding? ");

                        for(y = 0; y < mc_server.b.length; ++y) {
                            if (y > 0) {
                                sb.append(", ");
                            }

                            world_server = mc_server.b[y];
                            sb.append(world_server.getDimensionName());
                            sb.append("=");
                            sb.append(StringHelper.capitalize(StringHelper.yesOrNo(!world_server.shouldTimeForwardingBeSkipped())));
                        }

                        par1ICommandSender.a(ChatMessage.d(sb.toString()).a(EnumChatFormat.o));
                        sb = new StringBuffer("Total World Time: ");

                        for(y = 0; y < mc_server.b.length; ++y) {
                            if (y > 0) {
                                sb.append(", ");
                            }

                            world_server = mc_server.b[y];
                            sb.append(world_server.getDimensionName());
                            sb.append("=");
                            sb.append(world_server.I());
                        }

                        par1ICommandSender.a(ChatMessage.d(sb.toString()).a(EnumChatFormat.o));
                        return 1;
                    }

                    if ((player == null || Minecraft.inDevMode()) && par2Str.equals("village")) {
                        par1ICommandSender.a(ChatMessage.d("Village Conditions: " + mc_server.getVillageConditions()).a(EnumChatFormat.o));
                        return 1;
                    }

                    if (player == null) {
                        if (command != EnumCommand.version && Minecraft.inDevMode() && par2Str.equals("hour")) {
                            mc_server.addTotalTimeForAllWorlds(1000);
                            par1ICommandSender.a(ChatMessage.d("The time is now " + world.getHourOfDayAMPM()).a(EnumChatFormat.o));
                            return 1;
                        }
                    } else {
                        if (command == EnumCommand.xp) {
                            par1ICommandSender.a(ChatMessage.d("You have " + player.bJ + " experience").a(EnumChatFormat.o));
                            return 1;
                        }

                        if (command == EnumCommand.syncpos) {
                            player.sendPacket((new Packet85SimpleSignal(EnumSignal.sync_pos)).setExactPosition(player.u, player.getEyePosY(), player.w));
                            return 1;
                        }

                        if (par2Str.equals("pushout")) {
                            if (!player.isNearToBlock(Block.X, 2, 2) && !Minecraft.inDevMode()) {
                                par1ICommandSender.a(ChatMessage.d("This command only works if you are near a bed").a(EnumChatFormat.o));
                            } else {
                                player.try_push_out_of_blocks = true;
                            }

                            return 1;
                        }

                        if (command == EnumCommand.ground) {
                            double pos_y = player.v;
                            z = (int)pos_y;
                            double pos_y_fraction = pos_y - (double)z;
                            if (pos_y_fraction > 1.591496843E-314D) {
                                ++z;
                            }

                            if (!world.isAirOrPassableBlock(player.getBlockPosX(), z - 1, player.getBlockPosZ(), true)) {
                                player.a(player.u, z, player.w);
                            }

                            return 1;
                        }

                        if (command == EnumCommand.stats) {
                            player.sendPacket(EntityStatsDump.generatePacketFor(player));
                            return 1;
                        }

                        if (world.areSkillsEnabled() && par2Str.startsWith("learn ")) {
                            param = par2Str.substring(6);
                            skill = Skill.getByLcName(param, false);
                            if (skill == null) {
                                skill = Skill.getByLcName(param, true);
                            }

                            if (skill == null) {
                                par1ICommandSender.a(ChatMessage.d("Invalid skill name \"" + param + "\"").a(EnumChatFormat.m));
                                return 1;
                            }

                            if (player.hasSkill(skill)) {
                                par1ICommandSender.a(ChatMessage.d("You are already " + StringHelper.aOrAn(skill.getLocalizedName(true))).a(EnumChatFormat.o));
                                return 1;
                            }

                            z = player.getNumSkills();
                            x1 = 5 * (z + 1);
                            dx1 = EntityHuman.getExpRequired(x1);
                            if (player.bJ < dx1) {
                                par1ICommandSender.a(ChatMessage.d("You must reach level " + x1 + " before learning " + (z == 0 ? "a" : (z == 1 ? "a second" : (z == 2 ? "a third" : (z == 3 ? "a fourth" : (z == 4 ? "a fifth" : "another"))))) + " profession").a(EnumChatFormat.o));
                                return 1;
                            }

                            if (player.last_skill_learned_on_day >= world.getDayOfWorld()) {
                                par1ICommandSender.a(ChatMessage.d("You can only learn one skill per day").a(EnumChatFormat.o));
                                return 1;
                            }

                            player.addSkill(skill);
                            player.s(-dx1);
                            par1ICommandSender.a(ChatMessage.d("You are now " + (player.hasSkills() ? StringHelper.aOrAn(player.getSkillsString(true)) : LocaleI18n.a("skill.none").toLowerCase())).a(EnumChatFormat.o));
                            player.last_skill_learned_on_day = world.getDayOfWorld();
                            return 1;
                        }

                        if (world.areSkillsEnabled() && par2Str.startsWith("abandon ")) {
                            param = par2Str.substring(8);
                            skill = Skill.getByLcName(param, false);
                            if (skill == null) {
                                skill = Skill.getByLcName(param, true);
                            }

                            if (skill == null) {
                                par1ICommandSender.a(ChatMessage.d("Invalid skill name \"" + param + "\"").a(EnumChatFormat.m));
                            } else if (player.hasSkill(skill)) {
                                player.removeSkill(skill);
                                world.playSoundAtEntity(player, "imported.random.level_drain");
                                par1ICommandSender.a(ChatMessage.d("You are now " + (player.hasSkills() ? StringHelper.aOrAn(player.getSkillsString(true)) : LocaleI18n.a("skill.none").toLowerCase())).a(EnumChatFormat.o));
                            } else {
                                par1ICommandSender.a(ChatMessage.d("You don't have that skill").a(EnumChatFormat.o));
                            }

                            return 1;
                        }

                        EntityLivestock livestock;
                        RaycastCollision rc1;
                        if (par2Str.equals("hunger")) {
                            rc1 = player.getSelectedObject(1.0F, false);
                            if (rc1 != null && rc1.isEntity() && rc1.getEntityHit() instanceof EntityLivestock) {
                                livestock = (EntityLivestock)rc1.getEntityHit();
//                                livestock.sFood(0.0F);
                                return 1;
                            }

                            par1ICommandSender.a(
                                    ChatMessage.d("Your metabolic hunger rate is x" + player.getWetnessAndMalnourishmentHungerMultiplier()).a(
                                            EnumChatFormat.o));
                            return 1;
                        }

                        if (this.isUserPrivileged(player)) {
                            if (par2Str.startsWith("decoy ")) {
                                param = par2Str.substring(6).replaceAll(" ", "");
                                Entity entity = EntityTypes.getEntityInstanceByNameCaseInsensitive(param, world);
                                if (entity instanceof EntityInsentient) {
                                    world.spawnDecoy(entity.getClass(), player);
                                } else {
                                    par1ICommandSender.a(ChatMessage.d("Invalid entity name \"" + par2Str.substring(6) + "\"").a(EnumChatFormat.m));
                                }

                                return 1;
                            }

                            List loadedEntities;
                            if (par2Str.equals("decoy")) {
                                loadedEntities = world.a(EntityInsentient.class, player.E.b(0.0D, 0.0D, 0.0D));

                                for(y = 0; y < loadedEntities.size(); ++y) {
                                    EntityInsentient entity_living = (EntityInsentient)loadedEntities.get(y);
                                    if (entity_living.isDecoy()) {
                                        entity_living.x();
                                    }
                                }

                                return 1;
                            }

                            if (par2Str.equals("grace")) {
                                if (DedicatedServer.disconnection_penalty_enabled) {
                                    DedicatedServer.disconnection_penalty_enabled = false;
                                    par1ICommandSender.a(ChatMessage.d("Disconnection penalties are now disabled").a(EnumChatFormat.o));
                                } else {
                                    DedicatedServer.disconnection_penalty_enabled = true;
                                    par1ICommandSender.a(ChatMessage.d("Disconnection penalties are now enabled").a(EnumChatFormat.o));
                                }

                                return 1;
                            }

                            if (Minecraft.inDevMode() || player.isZevimrgvInTournament()) {
                                if (par2Str.startsWith("level ")) {
                                    x1 = MathHelper.a(Integer.parseInt(par2Str.substring(6)), -40, EntityHuman.getHighestPossibleLevel());
                                    y = EntityPlayer.getExpRequired(x1);
                                    player.s(y - player.bJ);
                                    return 1;
                                }

                                if (par2Str.equals("heal")) {
                                    player.g(player.aT());
                                    return 1;
                                }

                                if (par2Str.startsWith("health ")) {
                                    x1 = Integer.parseInt(par2Str.substring(7));
                                    player.g((float)x1);
                                    return 1;
                                }

                                if (par2Str.equals("fill")) {
                                    player.bI().addSatiation(20);
                                    player.bI().addNutrition(20);
                                    return 1;
                                }

                                if (par2Str.equals("nourish")) {
                                    player.setProtein(160000);
                                    player.setEssentialFats(160000);
                                    player.setPhytonutrients(160000);
                                    return 1;
                                }

                                if (par2Str.equals("starve")) {
                                    player.bI().setSatiation(0, false);
                                    player.bI().setNutrition(0, false);
                                    return 1;
                                }

                                if (par2Str.startsWith("satiation ")) {
                                    x1 = Integer.valueOf(par2Str.substring(10));
                                    player.bI().setSatiation(x1, true);
                                    return 1;
                                }

                                if (par2Str.startsWith("nutrition ")) {
                                    x1 = Integer.valueOf(par2Str.substring(10));
                                    player.bI().setNutrition(x1, true);
                                    return 1;
                                }

                                if (par2Str.equals("night vision")) {
                                    player.sendPacket(new Packet85SimpleSignal(EnumSignal.toggle_night_vision_override));
                                    return 1;
                                }

                                if (par2Str.equals("test")) {
                                    player.in_test_mode = !player.in_test_mode;
                                    par1ICommandSender.a(ChatMessage.d("Test mode toggled " + (player.in_test_mode ? "on" : "off")).a(EnumChatFormat.o));
                                    return 1;
                                }

                                if (par2Str.equals("bolt")) {
                                    if (world.isThundering(true) && world.F(player.getBlockPosX(), player.getBlockPosY(), player.getBlockPosZ())) {
                                        world.c(new EntityLightning(world, player.u, player.v, player.w));
                                    } else {
                                        par1ICommandSender.a(ChatMessage.d("Lightning cannot strike your position").a(EnumChatFormat.o));
                                    }

                                    return 1;
                                }

                                if (par2Str.equals("damage armor")) {
                                    player.bn.tryDamageArmor(DamageSource.j, 10.0F, null);
                                    return 1;
                                }

                                if (par2Str.startsWith("xp ")) {
                                    x1 = Integer.valueOf(par2Str.substring(3));
                                    player.s(x1 - player.bJ);
                                    return 1;
                                }

                                if (par2Str.startsWith("day ")) {
                                    x1 = (Integer.valueOf(par2Str.substring(4)) - 1) * 24000;
                                    world.setTotalWorldTime(x1, true);
                                    return 1;
                                }

                                if (par2Str.equals("end")) {
                                    if (world.t.i != 1) {
                                        player.b(1);
                                    }

                                    return 1;
                                }

                                if (par2Str.startsWith("metadata ")) {
                                    x1 = Integer.valueOf(par2Str.substring(9));
                                    RaycastCollision rc = player.getSelectedObject(1.0F, true);
                                    if (rc != null && rc.isBlock()) {
                                        if (rc.getNeighborOfBlockHitMaterial().d()) {
                                            world.b(rc.neighbor_block_x, rc.neighbor_block_y, rc.neighbor_block_z, x1, 3);
                                        } else {
                                            world.b(rc.block_hit_x, rc.block_hit_y, rc.block_hit_z, x1, 3);
                                        }
                                    }

                                    return 1;
                                }

                                ItemStack held_item_stack;
                                if (par2Str.equals("damage item")) {
                                    held_item_stack = player.getHeldItemStack();
                                    if (held_item_stack != null) {
                                        held_item_stack.applyRandomItemStackDamageForChest();
                                    }

                                    return 1;
                                }

                                if (par2Str.startsWith("damage item ")) {
                                    x1 = Integer.valueOf(par2Str.substring(12));
                                    player.tryDamageHeldItem(DamageSource.j, x1);
                                    return 1;
                                }

                                if (par2Str.equals("repair item")) {
                                    held_item_stack = player.getHeldItemStack();
                                    if (held_item_stack != null && held_item_stack.i()) {
                                        held_item_stack.setItemDamage(0);
                                    }

                                    return 1;
                                }

                                if (par2Str.equals("thirst")) {
                                    rc1 = player.getSelectedObject(1.0F, false);
                                    if (rc1 != null && rc1.isEntity() && rc1.getEntityHit() instanceof EntityLivestock) {
                                        livestock = (EntityLivestock)rc1.getEntityHit();
//                                        livestock.sWater(0.0F);
                                    }

                                    return 1;
                                }

                                if (par2Str.equals("grow")) {
                                    loadedEntities = world.a(EntityAgeable.class, player.E.b(0.0D, 0.0D, 0.0D));

                                    for(y = 0; y < loadedEntities.size(); ++y) {
                                        EntityAgeable entity = (EntityAgeable)loadedEntities.get(y);
                                        if (entity.g_()) {
                                            entity.c(0);
                                        }
                                    }

                                    return 1;
                                }

                                if (par2Str.equals("tame")) {
                                    rc1 = player.getSelectedObject(1.0F, false);
                                    if (rc1 != null && rc1.isEntity() && rc1.getEntityHit() instanceof EntityHorse) {
                                        EntityHorse horse = (EntityHorse)rc1.getEntityHit();
                                        horse.g(player);
                                    }

                                    return 1;
                                }

                                if (par2Str.equals("clear books")) {
                                    player.referenced_books_read.clear();
                                    return 1;
                                }

                                int dy;
                                int dz;
                                int dx;
                                if (par2Str.startsWith("clear ")) {
                                    x1 = player.getBlockPosX();
                                    y = player.getBlockPosY();
                                    z = player.getBlockPosZ();
                                    boolean include_permanent_blocks;
                                    if (par2Str.endsWith("!")) {
                                        par2Str = StringHelper.stripTrailing("!", par2Str);
                                        include_permanent_blocks = true;
                                    } else {
                                        include_permanent_blocks = false;
                                    }

                                    dx = Integer.valueOf(par2Str.substring(6));

                                    for(dy = -dx; dy <= dx; ++dy) {
                                        for(dz = -dx; dz <= dx; ++dz) {
                                            for(dx = -dx; dx <= dx; ++dx) {
                                                if (include_permanent_blocks) {
                                                    world.i(x1 + dy, y + dz, z + dx);
                                                } else {
                                                    Block block = world.getBlock(x1 + dy, y + dz, z + dx);
                                                    if (block != Block.E && block != Block.mantleOrCore) {
                                                        world.i(x1 + dy, y + dz, z + dx);
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    return 1;
                                }

                                byte radius;
                                if (par2Str.startsWith("timber")) {
                                    x1 = player.getBlockPosX();
                                    y = player.getBlockPosY();
                                    z = player.getBlockPosZ();
                                    radius = 10;

                                    for(dx = -radius; dx <= radius; ++dx) {
                                        for(dy = -radius; dy <= radius; ++dy) {
                                            for(dz = -radius; dz <= radius; ++dz) {
                                                Block block = world.getBlock(x1 + dx, y + dy, z + dz);
                                                if (block == Block.O || block == Block.P || block == Block.bz) {
                                                    world.i(x1 + dx, y + dy, z + dz);
                                                }
                                            }
                                        }
                                    }

                                    return 1;
                                }

                                if (par2Str.startsWith("fill ")) {
                                    x1 = player.getBlockPosX();
                                    y = player.getBlockPosY();
                                    z = player.getBlockPosZ();
                                    x1 = Integer.parseInt(par2Str.substring(5));

                                    for(dx = -x1; dx <= x1; ++dx) {
                                        for(dy = 1; dy <= x1; ++dy) {
                                            for(dz = -x1; dz <= x1; ++dz) {
                                                world.f(x1 + dx, y + dy, z + dz, Block.bT.cF, 8, 3);
                                            }
                                        }
                                    }

                                    return 1;
                                }

                                if (par2Str.equals("mobs")) {
                                    par1ICommandSender.a(ChatMessage.d("There are " + world.a(
                                            IMonster.class) + " mobs loaded").a(EnumChatFormat.o));
                                    return 1;
                                }

                                if (par2Str.equals("peaceful")) {
                                    player.getWorldServer().decreased_hostile_mob_spawning_counter = 4000;
                                    return 1;
                                }

                                if (par2Str.equals("hostile")) {
                                    player.getWorldServer().increased_hostile_mob_spawning_counter = 2000;
                                    return 1;
                                }

                                if (par2Str.equals("malnourish")) {
                                    player.setProtein(0);
                                    player.setEssentialFats(0);
                                    player.setPhytonutrients(0);
                                    return 1;
                                }

                                if (par2Str.startsWith("protein ")) {
                                    player.setProtein(Integer.valueOf(par2Str.substring(8)));
                                    return 1;
                                }

                                if (par2Str.startsWith("essential fats ")) {
                                    player.setEssentialFats(Integer.valueOf(par2Str.substring(15)));
                                    return 1;
                                }

                                if (par2Str.startsWith("phytonutrients ")) {
                                    player.setPhytonutrients(Integer.valueOf(par2Str.substring(15)));
                                    return 1;
                                }

                                if (par2Str.startsWith("insulin ")) {
                                    player.setInsulinResistance(Integer.valueOf(par2Str.substring(8)));
                                    return 1;
                                }

                                if (par2Str.equals("slaughter")) {
                                    loadedEntities = world.e;
                                    y = 0;
                                    z = 0;
                                    x1 = 0;

                                    for(dx = 0; dx < loadedEntities.size(); ++dx) {
                                        Entity entity = (Entity)loadedEntities.get(dx);
                                        Class c = entity.getClass();
                                        if (EntityAnimal.class.isAssignableFrom(c) || EntityWaterAnimal.class.isAssignableFrom(c) || EntityAmbient.class.isAssignableFrom(c)) {
                                            if (entity instanceof EntityHorse) {
                                                EntityHorse entity_horse = (EntityHorse)entity;
                                                if (entity_horse.co()) {
                                                    continue;
                                                }
                                            }

                                            entity.x();
                                            if (EntityAnimal.class.isAssignableFrom(c)) {
                                                ++y;
                                            } else if (EntityWaterAnimal.class.isAssignableFrom(c)) {
                                                ++z;
                                            } else if (EntityAmbient.class.isAssignableFrom(c)) {
                                                ++x1;
                                            }
                                        }
                                    }

                                    par1ICommandSender.a(ChatMessage.d(y + " animals were slaughtered, " + z + " squids and " + x1 + " bats").a(EnumChatFormat.o));
                                    return 1;
                                }

                                if (par2Str.equals("spawning")) {
                                    par1ICommandSender.a(ChatMessage.d("Random mob spawning is now " + (spawning_disabled ? "enabled" : "disabled")).a(EnumChatFormat.o));
                                    spawning_disabled = !spawning_disabled;
                                    return 1;
                                }

                                Entity entity;
                                if (par2Str.equals("killall")) {
                                    loadedEntities = world.e;
                                    y = 0;

                                    for(z = 0; z < loadedEntities.size(); ++z) {
                                        entity = (Entity)loadedEntities.get(z);
                                        if (EntityInsentient.class.isAssignableFrom(entity.getClass())) {
                                            entity.x();
                                            ++y;
                                        }
                                    }

                                    par1ICommandSender.a(ChatMessage.d(y + " living entities were killed").a(EnumChatFormat.o));
                                    return 1;
                                }

                                if (par2Str.equals("killmobs")) {
                                    loadedEntities = world.e;
                                    y = 0;

                                    for(z = 0; z < loadedEntities.size(); ++z) {
                                        entity = (Entity)loadedEntities.get(z);
                                        if (IMonster.class.isAssignableFrom(entity.getClass())) {
                                            if (entity instanceof EntityWitch) {
                                                world.removeCursesForWitch((EntityWitch)entity);
                                            }

                                            entity.x();
                                            ++y;
                                        }
                                    }

                                    par1ICommandSender.a(ChatMessage.d(y + " living entities were killed").a(EnumChatFormat.o));
                                    return 1;
                                }

                                if (par2Str.equals("nomobs")) {
                                    if (!spawning_disabled) {
                                        this.executeCommand(par1ICommandSender, "spawning", permission_override);
                                    }

                                    this.executeCommand(par1ICommandSender, "killmobs", permission_override);
                                    return 1;
                                }

                                if (par2Str.equals("recall") && player.q.t.i == 0) {
                                    info = world.N();
                                    player.travelInsideDimension((float)info.c() + 0.5F,
                                            (float)info.d() + 0.1F,
                                            (float)info.e() + 0.5F);
                                    return 1;
                                }

                                if (par2Str.equals("corrupt")) {
                                    player.getChunkFromPosition().invalidate_checksum = true;
                                    player.getChunkFromPosition().l = true;
                                    return 1;
                                }

                                if (par2Str.equals("crash")) {
                                    Object o = new Object();
                                    boolean b = true;
                                    if (b) {
                                        o = null;
                                    }

                                    System.out.println(o.toString());
                                    return 1;
                                }

                                if (par2Str.startsWith("achievement ")) {
                                    boolean recursive = par2Str.endsWith("!");
                                    if (recursive) {
                                        par2Str = StringHelper.left(par2Str, par2Str.length() - 1);
                                    }

                                    y = Integer.valueOf(par2Str.substring(12));
                                    Achievement achievement = AchievementList.getAchievementForId(y);
                                    if (achievement == null) {
                                        par1ICommandSender.a(ChatMessage.d("Invalid achievement id \"" + par2Str.substring(12) + "\"").a(EnumChatFormat.m));
                                        return 1;
                                    }

                                    if (recursive) {
                                        ArrayList<Achievement> achievements;
                                        for(achievements = new ArrayList<>(); achievement != null; achievement = achievement.c) {
                                            achievements.add(achievement);
                                        }

                                        for(dx = achievements.size() - 1; dx >= 0; --dx) {
                                            player.a(achievements.get(dx));
                                        }
                                    }

                                    player.a(achievement);
                                    return 1;
                                }

                                if (par2Str.equals("books")) {
                                    for(x1 = 1; x1 <= 9; ++x1) {
                                        ItemStack book = ItemReferencedBook.generateBook(x1);
                                        if (!world.N().hasSignatureBeenAdded(book.getSignature())) {
                                            world.N().addSignature(book.getSignature());
                                            player.bn.addItemStackToInventoryOrDropIt(book);
                                        }
                                    }

                                    return 1;
                                }

                                if (par2Str.equals("layer air")) {
                                    x1 = player.getBlockPosX();
                                    y = player.getFootBlockPosY() - 1;
                                    z = player.getBlockPosZ();
                                    radius = 64;

                                    for(dx = -radius; dx <= radius; ++dx) {
                                        for(dy = -radius; dy <= radius; ++dy) {
                                            world.f(x1 + dx, y, z + dy, 0, 0, 2);
                                        }
                                    }

                                    world.c(x1, y, z, Block.au.cF);
                                    return 1;
                                }

                                if (par2Str.startsWith("layer ")) {
                                    param = par2Str.substring(6);
                                    Block block;
                                    if (param.isEmpty()) {
                                        block = null;
                                    } else if (StringHelper.startsWithDigit(param)) {
                                        block = Block.getBlock(Integer.valueOf(param));
                                    } else if (param.equalsIgnoreCase("grass")) {
                                        block = Block.z;
                                    } else if (param.equalsIgnoreCase("tall grass")) {
                                        block = Block.ac;
                                    } else if (!param.equalsIgnoreCase("slab") && !param.equalsIgnoreCase("wooden slab")) {
                                        if (param.equalsIgnoreCase("stone slab")) {
                                            block = Block.ap;
                                        } else if (param.equalsIgnoreCase("stairs")) {
                                            block = Block.ay;
                                        } else {
                                            block = Block.getBlock(param);
                                        }
                                    } else {
                                        block = Block.bT;
                                    }

                                    if (block == null) {
                                        par1ICommandSender.a(ChatMessage.d("Invalid block id \"" + param + "\"").a(EnumChatFormat.m));
                                    } else {
                                        byte metadata;
                                        if (block == Block.ac) {
                                            metadata = 1;
                                        } else {
                                            metadata = 0;
                                        }

                                        x1 = player.getBlockPosX();
                                        dx = player.getFootBlockPosY() - 1;
                                        dy = player.getBlockPosZ();
                                        int range = 64;

                                        for(dx = -range; dx <= range; ++dx) {
                                            for(int dz1 = -range; dz1 <= range; ++dz1) {
                                                if (block.canBePlacedAt(world, x1 + dx, dx, dy + dz1, metadata)) {
                                                    world.f(x1 + dx, dx, dy + dz1, block.cF, metadata, 2);
                                                }
                                            }
                                        }

                                        world.c(x1, dx, dy, Block.au.cF);
                                    }

                                    return 1;
                                }

                                if (par2Str.startsWith("layermd ")) {
                                    param = par2Str.substring(8);
                                    y = Integer.valueOf(param);
                                    z = player.getBlockPosX();
                                    x1 = player.getFootBlockPosY() - 1;
                                    dx = player.getBlockPosZ();
                                    int range = 64;

                                    for(dz = -range; dz <= range; ++dz) {
                                        for(dx = -range; dx <= range; ++dx) {
                                            world.b(z + dz, x1, dx + dx, y, 2);
                                        }
                                    }

                                    return 1;
                                }

                                if (par2Str.equals("noliquid")) {
                                    x1 = player.getBlockPosX();
                                    y = player.getBlockPosZ();
                                    z = player.getFootBlockPosY();
                                    radius = 64;

                                    for(dx = -radius; dx <= radius; ++dx) {
                                        for(dy = -radius; dy <= radius; ++dy) {
                                            for(dz = -radius; dz <= radius; ++dz) {
                                                if (world.g(x1 + dx, z + dz, y + dy).d()) {
                                                    world.f(x1 + dx, z + dz, y + dy, Block.y.cF, 0, 2);
                                                }
                                            }
                                        }
                                    }

                                    return 1;
                                }

                                if (par2Str.equals("spawn animals")) {
                                    x1 = world.getAnimalSpawner().trySpawningPeacefulMobs(world, EnumCreatureType.animal);
                                    par1ICommandSender.a(ChatMessage.d(x1 + " animals were spawned").a(EnumChatFormat.o));
                                    return 1;
                                }

                                if (par2Str.equals("spawn squids")) {
                                    x1 = world.getAnimalSpawner().trySpawningPeacefulMobs(world, EnumCreatureType.aquatic);
                                    par1ICommandSender.a(ChatMessage.d(x1 + " squids were spawned").a(EnumChatFormat.o));
                                    return 1;
                                }

                                if (par2Str.equals("spawn bats")) {
                                    x1 = world.getAnimalSpawner().trySpawningPeacefulMobs(world, EnumCreatureType.c);
                                    par1ICommandSender.a(ChatMessage.d(x1 + " bats were spawned").a(EnumChatFormat.o));
                                    return 1;
                                }

                                if (par2Str.equals("spawn mobs")) {
                                    x1 = world.getAnimalSpawner().trySpawningHostileMobs(world, false);
                                    x1 += world.getAnimalSpawner().trySpawningHostileMobs(world, true);
                                    par1ICommandSender.a(ChatMessage.d(x1 + " mobs were spawned").a(EnumChatFormat.o));
                                    return 1;
                                }

                                if (par2Str.equals("bb")) {
                                    Entity.apply_MITE_bb_limits_checking = !Entity.apply_MITE_bb_limits_checking;
                                    par1ICommandSender.a(ChatMessage.d("MITE BB limits checking is now " + (Entity.apply_MITE_bb_limits_checking ? "enabled" : "disabled")).a(EnumChatFormat.o));
                                    return 1;
                                }

                                if (par2Str.equals("tile entities")) {
                                    TileEntity.printTileEntitiesList("Loaded Entities on Server", world.g);
                                    player.sendPacket(new Packet85SimpleSignal(EnumSignal.loaded_tile_entities));
                                    return 1;
                                }

                                if (par2Str.equals("weather")) {
                                    x1 = world.getDayOfWorld();
                                    world.generateWeatherReport(x1, x1 + 31);
                                    return 1;
                                }

                                long seed;
                                CaveNetworkGenerator cg;
                                if (par2Str.equals("cavern")) {
                                    seed = world.s.nextLong();
                                    Debug.println("Using seed " + seed);
                                    cg = new CaveNetworkGenerator(new CaveNetworkStub(0, 0, 64, 48, 64, seed, world.s.nextInt(2) == 0, true, true));
                                    cg.apply(world, player.getBlockPosX(), player.getFootBlockPosY(), player.getBlockPosZ());
                                    return 1;
                                }

                                if (par2Str.equals("cavern+")) {
                                    seed = world.s.nextLong();
                                    seed = 3629381082L;
                                    Debug.println("Using seed " + seed);
                                    cg = new CaveNetworkGenerator(new CaveNetworkStub(0, 0, 64, 48, 64, seed, true, true, true));
                                    cg.apply(world, player.getBlockPosX(), player.getFootBlockPosY(), player.getBlockPosZ());
                                    return 1;
                                }

                                if (par2Str.equals("cavern!")) {
                                    CaveNetworkGenerator cg1 = new CaveNetworkGenerator(new CaveNetworkStub(-14, 29, 64, 48, 64, 1976929657L, true, true, true));
                                    cg1.apply(world, player.getBlockPosX(), player.getFootBlockPosY(), player.getBlockPosZ());
                                    return 1;
                                }



                                if (par2Str.equals("drill")) {
                                    for(x1 = 10; x1 < player.getFootBlockPosY(); ++x1) {
                                        world.i(player.getBlockPosX(), x1, player.getBlockPosZ());
                                    }

                                    player.a((double)player.getBlockPosX()  + 0.5D, 10.5D, (double)player.getBlockPosZ()+ 0.5D);
                                    x1 = player.getBlockPosX();
                                    y = player.getBlockPosY();
                                    z = player.getBlockPosZ();
                                    radius = 5;

                                    for(dx = -radius; dx <= radius; ++dx) {
                                        for(dy = -radius; dy <= radius; ++dy) {
                                            for(dz = -radius; dz <= radius; ++dz) {
                                                world.i(x1 + dx, y + dy, z + dz);
                                            }
                                        }
                                    }

                                    player.bn.addItemStackToInventoryOrDropIt(new ItemStack(Item.k));

                                    for(dx = 0; dx < 4; ++dx) {
                                        for(dy = 0; dy < 5; ++dy) {
                                            if (dx == 0 || dx == 3 || dy == 0 || dy == 4) {
                                                world.c(x1 - 1 + dx, y - 5 + dy, z - 5, Block.au.cF);
                                            }
                                        }
                                    }

                                    return 1;
                                }

                                if (player.isZevimrgvInTournament() && par2Str.equals("see")) {
                                    player.sendPacket(new Packet85SimpleSignal(EnumSignal.see));
                                    return 1;
                                }
                            }
                        }
                    }
                }

                String[] var3 = par2Str.split(" ");
                String var4 = var3[0];
                var3 = a(var3);
                ICommand var5 = (ICommand) this.a.get(var4);
                x = this.a(var5, var3);
                dx1 = 0;
                boolean permission_always_denied = false;
                if (var5 instanceof CommandTime || var5 instanceof CommandGamemode || var5 instanceof CommandDifficulty || var5 instanceof CommandGamemodeDefault || var5 instanceof CommandToggleDownfall || var5 instanceof CommandWeather || var5 instanceof CommandXp || var5 instanceof CommandEffect || var5 instanceof CommandEnchant || var5 instanceof CommandGamerule || var5 instanceof CommandClear || var5 instanceof CommandGive) {
                    permission_always_denied = true;
                }

                if (Minecraft.inDevMode()) {
                    permission_always_denied = false;
                }

                try {
                    if (var5 == null) {
                        throw new ExceptionUnknownCommand();
                    }

                    if ((permission_override || var5.a(par1ICommandSender)) && !permission_always_denied) {
                        if (x > -1) {
                            EntityPlayer[] var8 = PlayerSelector.c(par1ICommandSender, var3[x]);
                            String var9 = var3[x];
                            EntityPlayer[] var10 = var8;
                            int var11 = var8.length;

                            for(int var12 = 0; var12 < var11; ++var12) {
                                EntityPlayer var13 = var10[var12];
                                var3[x] = var13.an();

                                try {
                                    var5.b(par1ICommandSender, var3);
                                    ++dx1;
                                } catch (CommandException var21) {
                                    par1ICommandSender.a(ChatMessage.b(var21.getMessage(), var21.a()).a(EnumChatFormat.m));
                                }
                            }

                            var3[x] = var9;
                        } else {
                            var5.b(par1ICommandSender, var3);
                            ++dx1;
                        }
                    } else {
                        par1ICommandSender.a(ChatMessage.e("commands.generic.permission").a(EnumChatFormat.m));
                    }
                } catch (ExceptionUsage var22) {
                    par1ICommandSender.a(ChatMessage.b("commands.generic.usage", new Object[]{ChatMessage.b(var22.getMessage(), var22.a())}).a(EnumChatFormat.m));
                } catch (CommandException var23) {
                    par1ICommandSender.a(ChatMessage.b(var23.getMessage(), var23.a()).a(EnumChatFormat.m));
                } catch (Throwable var24) {
                    par1ICommandSender.a(ChatMessage.e("commands.generic.exception").a(EnumChatFormat.m));
                    var24.printStackTrace();
                }

                return dx1;
            }
        }
    }

    @Marker
    private static String[] a(String[] par0ArrayOfStr){
        return null;
    }
    @Marker
    private int a(ICommand par1ICommand, String[] par2ArrayOfStr){
        return 0;
    }

    @Marker
    private boolean isUserPrivileged(EntityHuman player) {
        return false;
    }

}

