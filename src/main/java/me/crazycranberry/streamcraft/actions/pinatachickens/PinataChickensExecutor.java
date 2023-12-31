package me.crazycranberry.streamcraft.actions.pinatachickens;

import me.crazycranberry.streamcraft.actions.Executor;
import me.crazycranberry.streamcraft.config.Action;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.Message;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static me.crazycranberry.streamcraft.StreamCraft.logger;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.getTargetedPlayers;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.maybeSendPlayerMessage;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.randomFromList;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.triggerer;

public class PinataChickensExecutor implements Executor {
    public static final List<Consumer<Location>> nonItemGoodies = List.of(
        Goodies::spawnZombie,
        Goodies::spawnHorseAndSaddle,
        Goodies::spawnWater,
        Goodies::spawnSuperChargedCreeper,
        Goodies::spawnLava,
        Goodies::doNothing,
        Goodies::spawnArmorStand
    );

    @Override
    public void execute(Message twitchMessage, Action action) {
        if (!(action instanceof PinataChickens)) {
            logger().warning("Somehow the following action was passed to " + this.getClass().getName() + ": " + action + "\nAborting execution.");
            return;
        }
        PinataChickens pc = (PinataChickens) action;
        for (Player p : getTargetedPlayers(pc)) {
            maybeSendPlayerMessage(p, String.format("Piñata chickens! courtesy of %s%s%s", ChatColor.GOLD, triggerer(twitchMessage, action), ChatColor.RESET));
            double x = Math.floor(p.getLocation().getX()) + 0.5;
            double y = p.getLocation().getY();
            double z = Math.floor(p.getLocation().getZ()) + 0.5;
            List<Location> possibleSpawnLocations = new ArrayList<>();
            for (int i = -5; i < 5; i++) {
                for (int j = -5; j < 5; j++) {
                    for (int k = -5; k < 5; k++) {
                        Location potentialLoc = new Location(p.getWorld(), x + i, y + j, z + k);
                        if (i == 0 && j == 0 && k == 0) {
                            continue; // We will always add the player location in the event that there are no valid spawns
                        }
                        if (isValidSpawnBlock(potentialLoc)) {
                            possibleSpawnLocations.add(potentialLoc);
                        }
                    }
                }
            }
            possibleSpawnLocations.add(new Location(p.getWorld(), x, y, z));
            for (int l = 0; l < pc.getNumChickens(); l++) {
                Chicken chicken = (Chicken) p.getWorld().spawnEntity(randomFromList(possibleSpawnLocations), EntityType.CHICKEN);
                chicken.setCustomName("Piñata");
                chicken.setCustomNameVisible(true);
            }
        }
    }

    /** Makes sure there is a 1x2x1 box open at the given location. */
    private boolean isValidSpawnBlock(Location loc) {
        Block blockAbove = loc.getBlock().getRelative(0, 1, 0);
        Block blockAboveAbove = loc.getBlock().getRelative(0, 2, 0);
        return (blockAbove.getType().equals(Material.AIR) || blockAbove.getType().equals(Material.WATER)) &&
                (blockAboveAbove.getType().equals(Material.AIR) || blockAboveAbove.getType().equals(Material.WATER));
    }

    public static class Goodies {
        public static final List<ItemStack> droppableItems = List.of(
                new ItemStack(Material.DIAMOND),
                new ItemStack(Material.BREAD, 3),
                new ItemStack(Material.FLINT),
                new ItemStack(Material.BOW),
                new ItemStack(Material.TADPOLE_BUCKET),
                new ItemStack(Material.KELP, 6),
                new ItemStack(Material.CARROT, 5),
                new ItemStack(Material.EGG),
                new ItemStack(Material.ENDER_PEARL),
                new ItemStack(Material.ENDER_EYE),
                new ItemStack(Material.FISHING_ROD),
                new ItemStack(Material.CROSSBOW),
                new ItemStack(Material.GLOW_ITEM_FRAME),
                new ItemStack(Material.LEAD, 2),
                new ItemStack(Material.MELON_SEEDS),
                new ItemStack(Material.TNT_MINECART),
                new ItemStack(Material.NETHER_WART),
                new ItemStack(Material.PAINTING),
                new ItemStack(Material.POTATO, 10),
                new ItemStack(Material.REDSTONE, 4),
                new ItemStack(Material.TRIDENT),
                new ItemStack(Material.WHEAT_SEEDS),
                new ItemStack(Material.ARROW, 32),
                new ItemStack(Material.APPLE, 2),
                new ItemStack(Material.BONE),
                new ItemStack(Material.COOKIE, 16),
                new ItemStack(Material.GOLDEN_HOE),
                new ItemStack(Material.DIAMOND_HORSE_ARMOR),
                new ItemStack(Material.MUSIC_DISC_PIGSTEP),
                new ItemStack(Material.NAME_TAG),
                new ItemStack(Material.EXPERIENCE_BOTTLE),
                new ItemStack(Material.GLASS_BOTTLE),
                new ItemStack(Material.SHEARS),
                new ItemStack(Material.SADDLE),
                new ItemStack(Material.SPYGLASS),
                new ItemStack(Material.COOKED_BEEF, 3),
                new ItemStack(Material.TURTLE_HELMET),
                new ItemStack(Material.PAPER),
                new ItemStack(Material.FEATHER),
                new ItemStack(Material.EMERALD),
                new ItemStack(Material.RAW_GOLD, 3),
                new ItemStack(Material.SHULKER_SHELL, 2),
                new ItemStack(Material.SLIME_BALL),
                new ItemStack(Material.TORCH, 10),
                new ItemStack(Material.FROG_SPAWN_EGG),
                new ItemStack(Material.CHERRY_BOAT)
        );

        public static void dropItem(Location loc) {
            loc.getWorld().dropItem(loc, randomFromList(droppableItems));
        }

        public static void spawnZombie(Location loc) {
            loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
        }

        public static void spawnHorseAndSaddle(Location loc) {
            loc.getWorld().spawnEntity(loc, EntityType.HORSE);
            loc.getWorld().dropItem(loc, new ItemStack(Material.SADDLE));
        }

        public static void spawnWater(Location loc) {
            loc.getBlock().setType(Material.WATER);
        }

        public static void spawnLava(Location loc) {
            loc.getBlock().setType(Material.LAVA);
        }

        public static void spawnSuperChargedCreeper(Location loc) {
            Creeper creeper = (Creeper) loc.getWorld().spawnEntity(loc, EntityType.CREEPER);
            creeper.setPowered(true);
        }

        public static void doNothing(Location loc) { }

        public static void spawnArmorStand(Location loc) {
            ArmorStand armorStand = loc.getWorld().spawn(loc, ArmorStand.class);
            armorStand.setItem(EquipmentSlot.HEAD, randomFromList(armorHeads));
            armorStand.setItem(EquipmentSlot.CHEST, randomFromList(armorChests));
            armorStand.setItem(EquipmentSlot.LEGS, randomFromList(armorLegs));
            armorStand.setItem(EquipmentSlot.FEET, randomFromList(armorBoots));
        }

        private static final List<ItemStack> armorHeads = Arrays.asList(
                new ItemStack(Material.LEATHER_HELMET),
                new ItemStack(Material.LEATHER_HELMET),
                new ItemStack(Material.LEATHER_HELMET),
                new ItemStack(Material.LEATHER_HELMET),
                new ItemStack(Material.IRON_HELMET),
                new ItemStack(Material.IRON_HELMET),
                new ItemStack(Material.IRON_HELMET),
                new ItemStack(Material.GOLDEN_HELMET),
                new ItemStack(Material.GOLDEN_HELMET),
                new ItemStack(Material.GOLDEN_HELMET),
                new ItemStack(Material.DIAMOND_HELMET),
                new ItemStack(Material.DIAMOND_HELMET),
                new ItemStack(Material.NETHERITE_HELMET),
                null,
                null,
                null,
                null
        );

        private static final List<ItemStack> armorChests = Arrays.asList(
                new ItemStack(Material.LEATHER_CHESTPLATE),
                new ItemStack(Material.LEATHER_CHESTPLATE),
                new ItemStack(Material.LEATHER_CHESTPLATE),
                new ItemStack(Material.LEATHER_CHESTPLATE),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemStack(Material.GOLDEN_CHESTPLATE),
                new ItemStack(Material.GOLDEN_CHESTPLATE),
                new ItemStack(Material.GOLDEN_CHESTPLATE),
                new ItemStack(Material.DIAMOND_CHESTPLATE),
                new ItemStack(Material.DIAMOND_CHESTPLATE),
                new ItemStack(Material.NETHERITE_CHESTPLATE),
                null,
                null,
                null,
                null
        );

        private static final List<ItemStack> armorLegs = Arrays.asList(
                new ItemStack(Material.LEATHER_LEGGINGS),
                new ItemStack(Material.LEATHER_LEGGINGS),
                new ItemStack(Material.LEATHER_LEGGINGS),
                new ItemStack(Material.LEATHER_LEGGINGS),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.GOLDEN_LEGGINGS),
                new ItemStack(Material.GOLDEN_LEGGINGS),
                new ItemStack(Material.GOLDEN_LEGGINGS),
                new ItemStack(Material.DIAMOND_LEGGINGS),
                new ItemStack(Material.DIAMOND_LEGGINGS),
                new ItemStack(Material.NETHERITE_LEGGINGS),
                null,
                null,
                null,
                null
        );

        private static final List<ItemStack> armorBoots = Arrays.asList(
                new ItemStack(Material.LEATHER_BOOTS),
                new ItemStack(Material.LEATHER_BOOTS),
                new ItemStack(Material.LEATHER_BOOTS),
                new ItemStack(Material.LEATHER_BOOTS),
                new ItemStack(Material.IRON_BOOTS),
                new ItemStack(Material.IRON_BOOTS),
                new ItemStack(Material.IRON_BOOTS),
                new ItemStack(Material.GOLDEN_BOOTS),
                new ItemStack(Material.GOLDEN_BOOTS),
                new ItemStack(Material.GOLDEN_BOOTS),
                new ItemStack(Material.DIAMOND_BOOTS),
                new ItemStack(Material.DIAMOND_BOOTS),
                new ItemStack(Material.NETHERITE_BOOTS),
                null,
                null,
                null,
                null
        );
    }
}
