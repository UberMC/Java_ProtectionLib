package me.ubermc.Listeners;

import me.ubermc.Main.PSettings;
import me.ubermc.Protection.ProtectionManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Horse;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.HorseInventory;
import org.spigotmc.event.entity.EntityMountEvent;

public class EventsProtection implements Listener {
    private Player attacker = null;
    private Player breaker = null;
    private ProtectionManager pman;

    public EventsProtection(ProtectionManager pman) {
        System.out.println("Construct Protection Event, reference ProtectionUtil");
        this.pman = pman;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExplosionEvent(EntityExplodeEvent event) {
        event.blockList().clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockExplosionEvent(BlockExplodeEvent event) {
        event.blockList().clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void IslandBedEnter(PlayerBedEnterEvent event) {
        if (event.getPlayer().getWorld().getName().equalsIgnoreCase(PSettings.worldName)) {

            // Player and Interactable are on an island they own, or are
            // whitelisted on.
            if (event.getBed().getLocation() != null) {
                if (pman.hasFullPermission(event.getPlayer(), event.getBed().getLocation())) {
                    return;
                }
            }

            // visitor settings
            if (!pman.isSettingAllowed("bed", pman.getPZoneLocationbyBlock(event.getBed().getLocation()))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getWorld().getName().equalsIgnoreCase(PSettings.worldName)) {

            if (event.getBlock().getLocation() != null) {
                if (pman.hasFullPermission(event.getPlayer(), event.getBlock().getLocation())) {
                    //      System.out.println("player is white listed or owner!");
                    return;
                } else {
                    //     System.out.println("player is a visitor");
                }
            }

            // visitor settings
            if (!pman.isSettingAllowed("breaking", pman.getPZoneLocationbyBlock(event.getBlock().getLocation()))) {
                //  System.out.println("breaking is false, cancel event");
                event.setCancelled(true);
            } else {
                //  System.out.println("breaking is allowed");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerBlockPlace(BlockPlaceEvent event) {

        if (event.getPlayer().getWorld().getName().equalsIgnoreCase(PSettings.worldName)) {

            if (event.getBlock().getLocation() != null) {
                if (pman.hasFullPermission(event.getPlayer(), event.getBlock().getLocation())) {
                    return;
                }
            }

            // visitor settings
            if (!pman.isSettingAllowed("place", pman.getPZoneLocationbyBlock(event.getBlock().getLocation()))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerArmorStandManipulate(PlayerInteractAtEntityEvent event) {

        if (event.getPlayer().getWorld().getName().equalsIgnoreCase(PSettings.worldName)) {

            // ignore whitelisted players, and island owner
            if (event.getRightClicked() != null) {
                if (event.getRightClicked().getLocation() != null) {

                    // Player and Interactable are on an island they own, or are
                    // whitelisted on.
                    if (event.getRightClicked().getLocation() != null) {
                        if (pman.hasFullPermission(event.getPlayer(), event.getRightClicked().getLocation())) {
                            return;
                        }
                    }

                    if (event.getRightClicked() instanceof ArmorStand) {
                        // visitor settings
                        if (!pman.isSettingAllowed("armorstand", pman.getPZoneLocationbyBlock(event.getRightClicked().getLocation()))) {
                            event.setCancelled(true);
                        }
                    }

                    if (event.getRightClicked() instanceof StorageMinecart) {
                        // visitor settings
                        if (!pman.isSettingAllowed("chest", pman.getPZoneLocationbyBlock(event.getRightClicked().getLocation()))) {
                            event.setCancelled(true);
                        }

                    }

                    if (event.getRightClicked() instanceof ItemFrame) {
                        // visitor settings
                        //   System.out.println("interact item frame");
                        if (!pman.isSettingAllowed("place", pman.getPZoneLocationbyBlock(event.getRightClicked().getLocation()))) {
                            event.setCancelled(true);
                            //    System.out.println("cancel interact item frame");
                        }

                    }

                    if (event.getRightClicked() instanceof HopperMinecart) {
                        // visitor settings
                        if (!pman.isSettingAllowed("dispenser", pman.getPZoneLocationbyBlock(event.getRightClicked().getLocation()))) {
                            event.setCancelled(true);
                        }
                    }

                    //minecart furnace
                    if (event.getRightClicked() instanceof PoweredMinecart) {
                        // visitor settings
                        if (!pman.isSettingAllowed("furnace", pman.getPZoneLocationbyBlock(event.getRightClicked().getLocation()))) {
                            event.setCancelled(true);
                        }
                    }

                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onChunkLoad(ChunkLoadEvent event) {
        for (int x = ((event.getChunk().getX()) * 16); x <= (((event.getChunk().getX()) * 16) + 16); x += 16) {
            for (int z = ((event.getChunk().getZ()) * 16); z <= (((event.getChunk().getZ()) * 16) + 16); z += 16) {

                //load the settings into memory
                pman.isSettingAllowed("chunk", pman.getPZoneLocationbyBlock(new Location(Bukkit.getWorld(PSettings.worldName), x, 70, z)));
                System.out.println("Chunk loaded load, pzone location is " + pman.getPZoneLocationbyBlock(new Location(Bukkit.getWorld(PSettings.worldName), x, 70, z)).toString());

            }
        }

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {

        boolean inlobby = false;
        boolean inspawnerroom = false;
        if (event.getClickedBlock() != null) {
            if (event.getClickedBlock().getLocation().getX() >= -130 && event.getClickedBlock().getLocation().getX() <= 76) // in
                                                                                                                            // lobby
                                                                                                                            // cancel
                                                                                                                            // event
            {
                if (event.getClickedBlock().getLocation().getZ() >= -110 && event.getClickedBlock().getLocation().getZ() <= 159) {
                    inlobby = true;
                }
            }

            if (event.getClickedBlock().getLocation().getX() >= 1 && event.getClickedBlock().getLocation().getX() <= 5) // in
                                                                                                                        // lobby
                                                                                                                        // cancel
                                                                                                                        // event
            {
                if (event.getClickedBlock().getLocation().getZ() >= -16 && event.getClickedBlock().getLocation().getZ() <= -15) {
                    if (event.getClickedBlock().getLocation().getY() >= 169 && event.getClickedBlock().getLocation().getY() <= 173) {
                        inspawnerroom = false;
                    }
                }
            }
        }

        if (event.getPlayer().getItemInHand() != null) {
            if (event.getPlayer().getItemInHand().getType() != null) {
                if (event.getPlayer().getItemInHand().getType() == Material.MONSTER_EGG) {
                    if (inlobby) {
                        if (!inspawnerroom) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }

        if (event.getPlayer().getWorld().getName().equalsIgnoreCase(PSettings.worldName)) {

            if (event.getClickedBlock() != null) {

                if (pman.hasFullPermission(event.getPlayer(), event.getClickedBlock().getLocation())) {
                    return;
                }

                if (event.getClickedBlock().getType() == Material.ANVIL) {
                    // visitor settings
                    if (!pman.isSettingAllowed("anvil", pman.getPZoneLocationbyBlock(event.getClickedBlock().getLocation()))) {
                        event.setCancelled(true);
                    }

                }
                if (event.getClickedBlock().getType() == Material.BEACON) {
                    // visitor settings
                    if (!pman.isSettingAllowed("beacon", pman.getPZoneLocationbyBlock(event.getClickedBlock().getLocation()))) {
                        event.setCancelled(true);
                    }

                }

                if (event.getClickedBlock().getType() == Material.DARK_OAK_DOOR || event.getClickedBlock().getType() == Material.ACACIA_DOOR || event.getClickedBlock().getType() == Material.JUNGLE_DOOR || event.getClickedBlock().getType() == Material.BIRCH_DOOR || event.getClickedBlock().getType() == Material.SPRUCE_DOOR || event.getClickedBlock().getType() == Material.ACACIA_DOOR || event.getClickedBlock().getType() == Material.WOOD_DOOR
                        || event.getClickedBlock().getType() == Material.WOODEN_DOOR || event.getClickedBlock().getType() == Material.IRON_DOOR || event.getClickedBlock().getType() == Material.TRAP_DOOR || event.getClickedBlock().getType() == Material.IRON_TRAPDOOR) {
                    // visitor settings
                    if (!pman.isSettingAllowed("door", pman.getPZoneLocationbyBlock(event.getClickedBlock().getLocation()))) {
                        event.setCancelled(true);
                    }

                }

                if (event.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE) {
                    // visitor settings
                    if (!pman.isSettingAllowed("enchantmenttable", pman.getPZoneLocationbyBlock(event.getClickedBlock().getLocation()))) {
                        event.setCancelled(true);
                    }

                }
                if (event.getClickedBlock().getType() == Material.FURNACE) {
                    // visitor settings
                    if (!pman.isSettingAllowed("furnace", pman.getPZoneLocationbyBlock(event.getClickedBlock().getLocation()))) {
                        event.setCancelled(true);
                    }

                }

                if (event.getClickedBlock().getType() == Material.FENCE_GATE || event.getClickedBlock().getType() == Material.SPRUCE_FENCE_GATE || event.getClickedBlock().getType() == Material.BIRCH_FENCE_GATE || event.getClickedBlock().getType() == Material.JUNGLE_FENCE_GATE || event.getClickedBlock().getType() == Material.DARK_OAK_FENCE_GATE || event.getClickedBlock().getType() == Material.ACACIA_FENCE_GATE) {
                    // visitor settings
                    if (!pman.isSettingAllowed("gate", pman.getPZoneLocationbyBlock(event.getClickedBlock().getLocation()))) {
                        event.setCancelled(true);
                    }

                }

                if (event.getClickedBlock().getType() == Material.JUKEBOX || event.getClickedBlock().getType() == Material.NOTE_BLOCK) {
                    // visitor settings
                    if (!pman.isSettingAllowed("jukebox", pman.getPZoneLocationbyBlock(event.getClickedBlock().getLocation()))) {
                        event.setCancelled(true);
                    }

                }

                if (event.getClickedBlock().getType() == Material.LEVER || event.getClickedBlock().getType() == Material.WOOD_BUTTON || event.getClickedBlock().getType() == Material.STONE_BUTTON) {
                    // visitor settings
                    if (!pman.isSettingAllowed("lever", pman.getPZoneLocationbyBlock(event.getClickedBlock().getLocation()))) {
                        event.setCancelled(true);
                    }

                }

                if (event.getClickedBlock().getType() == Material.CHEST || event.getClickedBlock().getType() == Material.TRAPPED_CHEST || event.getClickedBlock().getType() == Material.STORAGE_MINECART) {
                    // visitor settings
                    if (!pman.isSettingAllowed("chest", pman.getPZoneLocationbyBlock(event.getClickedBlock().getLocation()))) {
                        event.setCancelled(true);
                    }

                }

                if (event.getClickedBlock().getType() == Material.DISPENSER || event.getClickedBlock().getType() == Material.HOPPER || event.getClickedBlock().getType() == Material.DROPPER || event.getClickedBlock().getType() == Material.HOPPER_MINECART) {
                    // visitor settings
                    if (!pman.isSettingAllowed("dispenser", pman.getPZoneLocationbyBlock(event.getClickedBlock().getLocation()))) {
                        event.setCancelled(true);
                    }

                }

                if (event.getClickedBlock().getType() == Material.ARMOR_STAND) {
                    // visitor settings
                    if (!pman.isSettingAllowed("armorstand", pman.getPZoneLocationbyBlock(event.getClickedBlock().getLocation()))) {
                        event.setCancelled(true);
                    }

                }

                if (event.getClickedBlock().getType() == Material.WORKBENCH) {
                    // visitor settings
                    if (!pman.isSettingAllowed("workbench", pman.getPZoneLocationbyBlock(event.getClickedBlock().getLocation()))) {
                        event.setCancelled(true);
                    }

                }

                if (event.getClickedBlock().getType() == Material.BREWING_STAND) {
                    // visitor settings
                    if (!pman.isSettingAllowed("brewing", pman.getPZoneLocationbyBlock(event.getClickedBlock().getLocation()))) {
                        event.setCancelled(true);
                    }

                }

                //Monster spawner (Clicking with spawn egg to change type)
                if (event.getClickedBlock().getType() == Material.MOB_SPAWNER || event.getClickedBlock().getType() == Material.FLOWER_POT) {
                    // visitor settings
                    if (!inlobby) {
                        if (!pman.isSettingAllowed("place", pman.getPZoneLocationbyBlock(event.getClickedBlock().getLocation()))) {
                            event.setCancelled(true);
                        }
                    } else {
                        if (!event.getPlayer().isOp()) {
                            event.setCancelled(true);
                        }
                    }

                }

                if (event.getClickedBlock().getType() == Material.REDSTONE_COMPARATOR || event.getClickedBlock().getType() == Material.REDSTONE_COMPARATOR_OFF || event.getClickedBlock().getType() == Material.REDSTONE_COMPARATOR_ON || event.getClickedBlock().getType() == Material.DIODE || event.getClickedBlock().getType() == Material.DIODE_BLOCK_OFF || event.getClickedBlock().getType() == Material.DIODE_BLOCK_ON) {
                    // visitor settings
                    if (!pman.isSettingAllowed("redstone", pman.getPZoneLocationbyBlock(event.getClickedBlock().getLocation()))) {
                        event.setCancelled(true);
                    }

                }

                if (event.getPlayer().getItemInHand() != null) {
                    if (event.getPlayer().getItemInHand().getType() != null) {
                        if (event.getPlayer().getItemInHand().getType() == Material.LAVA_BUCKET || event.getPlayer().getItemInHand().getType() == Material.WATER_BUCKET || event.getPlayer().getItemInHand().getType() == Material.BUCKET) {
                            // visitor settings
                            if (!pman.isSettingAllowed("bucket", pman.getPZoneLocationbyBlock(event.getClickedBlock().getLocation()))) {
                                event.setCancelled(true);
                            }

                        }
                        if (event.getPlayer().getItemInHand().getType() == Material.EGG || event.getPlayer().getItemInHand().getType() == Material.MONSTER_EGG || event.getPlayer().getItemInHand().getType() == Material.MONSTER_EGGS || event.getPlayer().getItemInHand().getType() == Material.MINECART || event.getPlayer().getItemInHand().getType() == Material.POWERED_MINECART || event.getPlayer().getItemInHand().getType() == Material.BOAT
                                || event.getPlayer().getItemInHand().getType() == Material.BOAT_ACACIA || event.getPlayer().getItemInHand().getType() == Material.BOAT_BIRCH || event.getPlayer().getItemInHand().getType() == Material.BOAT_DARK_OAK || event.getPlayer().getItemInHand().getType() == Material.BOAT_JUNGLE || event.getPlayer().getItemInHand().getType() == Material.BOAT_SPRUCE || event.getPlayer().getItemInHand().getType() == Material.COMMAND_MINECART
                                || event.getPlayer().getItemInHand().getType() == Material.EXPLOSIVE_MINECART || event.getPlayer().getItemInHand().getType() == Material.HOPPER_MINECART || event.getPlayer().getItemInHand().getType() == Material.STORAGE_MINECART) {
                            // visitor settings
                            if (!pman.isSettingAllowed("place", pman.getPZoneLocationbyBlock(event.getClickedBlock().getLocation()))) {
                                if (event.getPlayer().getItemInHand().getType() == Material.MONSTER_EGG) {
                                    if (inlobby) {
                                        if (!inspawnerroom) {
                                            event.setCancelled(true);
                                        }
                                    }
                                } else {
                                    event.setCancelled(true);
                                }
                            }

                        }

                        if (event.getPlayer().getItemInHand().getType() == Material.EXP_BOTTLE) {
                            // visitor settings
                            if (!pman.isSettingAllowed("expbottle", pman.getPZoneLocationbyBlock(event.getClickedBlock().getLocation()))) {
                                event.setCancelled(true);
                            }

                        }
                    }
                }

                if (event.getAction().equals(Action.PHYSICAL)) {
                    if ((event.getClickedBlock().getType() == Material.STONE_PLATE) || (event.getClickedBlock().getType() == Material.WOOD_PLATE) || (event.getClickedBlock().getType() == Material.IRON_PLATE) || (event.getClickedBlock().getType() == Material.GOLD_PLATE)) {
                        if (!pman.isSettingAllowed("pressure", pman.getPZoneLocationbyBlock(event.getClickedBlock().getLocation()))) {
                            event.setCancelled(true);
                        }

                    }

                    if ((event.getClickedBlock().getType() == Material.TRIPWIRE || event.getClickedBlock().getType() == Material.TRIPWIRE_HOOK)) {
                        if (!pman.isSettingAllowed("tripwire", pman.getPZoneLocationbyBlock(event.getClickedBlock().getLocation()))) {
                            event.setCancelled(true);
                        }

                    }

                    if (event.getClickedBlock() != null) {

                        if (event.getClickedBlock().getType() == Material.SOIL) {
                            // Deny event and set the block
                            if (!pman.isSettingAllowed("trampling", pman.getPZoneLocationbyBlock(event.getClickedBlock().getLocation()))) {

                                event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
                                event.setCancelled(true);

                            }

                        }
                    }

                }

            }

            // I cannot cancel unhandled materials, because that's everything in
            // the game (Example clicking air..., the list gones on and on)
        }

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onHorseInventory(InventoryOpenEvent event) {
        if (event.getInventory() instanceof HorseInventory) {
            if (event.getPlayer() instanceof Player) {
                if (event.getPlayer().getWorld().getName().equalsIgnoreCase(PSettings.worldName)) {

                    // ignore whitelisted players, and island owner

                    // Player and Interactable are on an island they own, or
                    // are
                    // whitelisted on.
                    if (event.getInventory().getLocation() != null) {
                        if (pman.hasFullPermission((Player) event.getPlayer(), event.getInventory().getLocation())) {
                            return;
                        }
                    }

                }

                if (!pman.isSettingAllowed("horse", pman.getPZoneLocationbyBlock(event.getInventory().getLocation()))) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onHorseMount(EntityMountEvent event) {
        if (event.getMount() instanceof Horse) {
            if (event.getEntity() instanceof Player) {
                if (((Player) event.getEntity()).getWorld().getName().equalsIgnoreCase(PSettings.worldName)) {

                    // ignore whitelisted players, and island owner

                    // Player and Interactable are on an island they own, or
                    // are
                    // whitelisted on.
                    if (event.getMount().getLocation() != null) {
                        if (pman.hasFullPermission(((Player) event.getEntity()), event.getMount().getLocation())) {
                            return;
                        }
                    }

                }

                if (!pman.isSettingAllowed("horse", pman.getPZoneLocationbyBlock(event.getMount().getLocation()))) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void enderPearlThrown(PlayerTeleportEvent event) {

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {

            if (event.getPlayer().getWorld().getName().equalsIgnoreCase(PSettings.worldName)) {

                // ignore whitelisted players, and island owner

                // Player and Interactable are on an island they own, or are
                // whitelisted on.
                if (event.getTo() != null) {
                    if (pman.hasFullPermission(event.getPlayer(), event.getTo())) {
                        return;
                    }
                }

            }

            if (!pman.isSettingAllowed("enderpearl", pman.getPZoneLocationbyBlock(event.getTo()))) {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDropItem(PlayerDropItemEvent event) {

        if (event.getPlayer().getWorld().getName().equalsIgnoreCase(PSettings.worldName)) {

            // ignore whitelisted players, and island owner

            // Player and Interactable are on an island they own, or are
            // whitelisted on.
            if (event.getPlayer() != null) {
                if (event.getPlayer().getLocation() != null) {
                    if (pman.hasFullPermission(event.getPlayer(), event.getItemDrop().getLocation())) {
                        return;
                    }
                }
            }

            if (!pman.isSettingAllowed("dropitems", pman.getPZoneLocationbyBlock(event.getItemDrop().getLocation()))) {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onMove(PlayerMoveEvent event) {
        // System.out.println("move event");
        if ((event.getFrom().getBlockX() != event.getTo().getBlockX()) || (event.getFrom().getBlockZ() != event.getTo().getBlockZ())) {

            if (event.getPlayer().getWorld().getName().equalsIgnoreCase(PSettings.worldName)) {
                /*
                // System.out.println("move event 1");
                // ignore whitelisted players, and island owner

                // Player and Interactable are on an island they own, or are
                // whitelisted on.
                if (event.getPlayer() != null) {
                    if (event.getPlayer().getLocation() != null) {
                        if (pman.hasFullPermission(event.getPlayer(), event.getTo())) {
                            System.out.println("move event return");
                            return;
                        }
                    }
                }
                */
            }

            if (!pman.isSettingAllowed("walk", pman.getPZoneLocationbyBlock(event.getTo()))) {

                // event.getPlayer().performCommand("spawn");

            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBreed(EntityBreedEvent event) {
        if (event.getBreeder() instanceof Player) {

            if (((Player) event.getBreeder()).getWorld().getName().equalsIgnoreCase(PSettings.worldName)) {

                // ignore whitelisted players, and island owner

                // Player and Interactable are on an island they own, or are
                // whitelisted on.
                if (((Player) event.getBreeder()) != null) {
                    if (((Player) event.getBreeder()).getLocation() != null) {
                        if (pman.hasFullPermission(((Player) event.getBreeder()), event.getEntity().getLocation())) {
                            return;
                        }
                    }
                }

            }

            if (!pman.isSettingAllowed("breeding", pman.getPZoneLocationbyBlock(event.getEntity().getLocation()))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerLeashEntityEvent(PlayerLeashEntityEvent event) {
        if (event.getPlayer().getWorld().getName().equalsIgnoreCase(PSettings.worldName)) {

            // ignore whitelisted players, and island owner
            if (event.getEntity() != null) {
                if (event.getEntity().getLocation() != null) {

                    // Player and Interactable are on an island they own, or are
                    // whitelisted on.
                    if (event.getEntity().getLocation() != null) {
                        if (pman.hasFullPermission(event.getPlayer(), event.getEntity().getLocation())) {
                            return;
                        }
                    }
                }
            }

            if (!pman.isSettingAllowed("leash", pman.getPZoneLocationbyBlock(event.getEntity().getLocation()))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerShearEntity(PlayerShearEntityEvent event) {
        if (event.getPlayer().getWorld().getName().equalsIgnoreCase(PSettings.worldName)) {

            // ignore whitelisted players, and island owner
            if (event.getEntity() != null) {
                if (event.getEntity().getLocation() != null) {

                    // Player and Interactable are on an island they own, or are
                    // whitelisted on.
                    if (event.getEntity().getLocation() != null) {
                        if (pman.hasFullPermission(event.getPlayer(), event.getEntity().getLocation())) {
                            return;
                        }
                    }
                }
            }

            if (!pman.isSettingAllowed("shears", pman.getPZoneLocationbyBlock(event.getEntity().getLocation()))) {
                event.setCancelled(true);
            }
        }
    }

    // armorstand?
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getPlayer().getWorld().getName().equalsIgnoreCase(PSettings.worldName)) {

            // ignore whitelisted players, and island owner
            if (event.getRightClicked() != null) {
                if (event.getRightClicked().getLocation() != null) {

                    // Player and Interactable are on an island they own, or are
                    // whitelisted on.
                    if (event.getRightClicked().getLocation() != null) {
                        if (pman.hasFullPermission(event.getPlayer(), event.getRightClicked().getLocation())) {
                            return;
                        }
                    }

                    if (event.getRightClicked() instanceof ArmorStand) {
                        // visitor settings
                        if (!pman.isSettingAllowed("armorstand", pman.getPZoneLocationbyBlock(event.getRightClicked().getLocation()))) {
                            event.setCancelled(true);
                        }
                    }

                    if (event.getRightClicked() instanceof StorageMinecart) {
                        // visitor settings
                        if (!pman.isSettingAllowed("chest", pman.getPZoneLocationbyBlock(event.getRightClicked().getLocation()))) {
                            event.setCancelled(true);
                        }

                    }

                    if (event.getRightClicked() instanceof ItemFrame) {
                        // visitor settings

                        if (!pman.isSettingAllowed("place", pman.getPZoneLocationbyBlock(event.getRightClicked().getLocation()))) {
                            event.setCancelled(true);

                        }

                    }

                    if (event.getRightClicked() instanceof HopperMinecart) {
                        // visitor settings
                        if (!pman.isSettingAllowed("dispenser", pman.getPZoneLocationbyBlock(event.getRightClicked().getLocation()))) {
                            event.setCancelled(true);
                        }
                    }

                    //minecart furnace
                    if (event.getRightClicked() instanceof PoweredMinecart) {
                        // visitor settings
                        if (!pman.isSettingAllowed("furnace", pman.getPZoneLocationbyBlock(event.getRightClicked().getLocation()))) {
                            event.setCancelled(true);
                        }
                    }

                }
            }

        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        if (event.getPlayer().getWorld().getName().equalsIgnoreCase(PSettings.worldName)) {

            // ignore whitelisted players, and island owner
            if (event.getBlockClicked() != null) {
                if (event.getBlockClicked().getLocation() != null) {

                    // Player and Interactable are on an island they own, or are
                    // whitelisted on.
                    if (event.getBlockClicked().getLocation() != null) {
                        if (pman.hasFullPermission(event.getPlayer(), event.getBlockClicked().getLocation())) {
                            return;
                        }
                    }

                    if (!pman.isSettingAllowed("bucket", pman.getPZoneLocationbyBlock(event.getBlockClicked().getLocation()))) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        if (event.getPlayer().getWorld().getName().equalsIgnoreCase(PSettings.worldName)) {

            if (event.getBlockClicked() != null) {
                if (event.getBlockClicked().getLocation() != null) {

                    // Player and Interactable are on an island they own, or are
                    // whitelisted on.
                    if (event.getBlockClicked().getLocation() != null) {
                        if (pman.hasFullPermission(event.getPlayer(), event.getBlockClicked().getLocation())) {
                            return;
                        }
                    }

                    if (!pman.isSettingAllowed("bucket", pman.getPZoneLocationbyBlock(event.getBlockClicked().getLocation()))) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerBreakHanging(HangingBreakByEntityEvent event) {
        if ((event.getRemover() instanceof Player)) {
            this.breaker = ((Player) event.getRemover());
            if (this.breaker.getWorld().getName().equalsIgnoreCase(PSettings.worldName)) {

                if (event.getEntity().getLocation() != null) {
                    if (pman.hasFullPermission(breaker, event.getEntity().getLocation())) {
                        return;
                    }
                }

                if (!pman.isSettingAllowed("breaking", pman.getPZoneLocationbyBlock(event.getEntity().getLocation()))) {
                    event.setCancelled(true);
                }

            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerVehicleDamage(VehicleDestroyEvent event) {

        if ((event.getAttacker() instanceof Player)) {
            this.breaker = ((Player) event.getAttacker());
            if (this.breaker.getWorld().getName().equalsIgnoreCase(PSettings.worldName)) {

                if (event.getVehicle().getLocation() != null) {
                    if (pman.hasFullPermission(((Player) event.getAttacker()), event.getVehicle().getLocation())) {
                        return;
                    }
                }

                // minecart, boats,
                if (!(event.getVehicle() instanceof Pig)) {
                    if (!(event.getVehicle() instanceof Horse)) {
                        if (!pman.isSettingAllowed("breaking", pman.getPZoneLocationbyBlock(event.getVehicle().getLocation()))) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerVehicleDamage(VehicleDamageEvent event) {
        if ((event.getAttacker() instanceof Player)) {
            this.breaker = ((Player) event.getAttacker());
            if (this.breaker.getWorld().getName().equalsIgnoreCase(PSettings.worldName)) {

                if (event.getVehicle().getLocation() != null) {
                    if (pman.hasFullPermission(((Player) event.getAttacker()), event.getVehicle().getLocation())) {
                        return;
                    }
                }

                // minecart, boats,
                if (!(event.getVehicle() instanceof Pig)) {
                    if (!(event.getVehicle() instanceof Horse)) {
                        if (!pman.isSettingAllowed("breaking", pman.getPZoneLocationbyBlock(event.getVehicle().getLocation()))) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getPlayer() != null) {
            if (event.getBlock().getLocation() != null) {
                if (pman.hasFullPermission(event.getPlayer(), event.getBlock().getLocation())) {
                    return;
                }
            }

            // visitor settings
            if (!pman.isSettingAllowed("breaking", pman.getPZoneLocationbyBlock(event.getBlock().getLocation()))) {

                event.setCancelled(true);

            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onSpawnCreature(CreatureSpawnEvent event) {

        //in lobby
        if (event.getEntity().getLocation().getX() >= -130 && event.getEntity().getLocation().getX() <= 76) // in                                                                                                  // event
        {
            if (event.getEntity().getLocation().getZ() >= -110 && event.getEntity().getLocation().getZ() <= 159) {
                if (event.getSpawnReason() != SpawnReason.SPAWNER) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
