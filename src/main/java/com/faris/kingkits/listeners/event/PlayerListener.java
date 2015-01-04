package com.faris.kingkits.listeners.event;

import com.faris.kingkits.KingKits;
import com.faris.kingkits.Kit;
import com.faris.kingkits.guis.GuiKingKits;
import com.faris.kingkits.guis.GuiKitMenu;
import com.faris.kingkits.guis.GuiPreviewKit;
import com.faris.kingkits.helpers.Utils;
import com.faris.kingkits.hooks.PvPKits;
import com.faris.kingkits.listeners.commands.SetKit;
import com.faris.kingkits.listeners.event.custom.PlayerKilledEvent;
import com.faris.kingkits.sql.KingKitsSQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;

public class PlayerListener implements Listener {
    private final KingKits plugin;

    /**
     * Create an instance PlayerListener *
     * @param instance
     */
    public PlayerListener(KingKits instance) {
        this.plugin = instance;
    }

    /**
     * Register custom kill event *
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void registerKillEvent(PlayerDeathEvent event) {
        try {
            if (event.getEntity() != null) {
                if (event.getEntity().getKiller() != null) {
                    if (!event.getEntity().getName().equals(event.getEntity().getKiller().getName()))
                        event.getEntity().getServer().getPluginManager().callEvent(new PlayerKilledEvent(event.getEntity().getKiller(), event.getEntity()));
                }
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Lists all the available kits when a player joins *
     * @param event
     */
    @EventHandler
    public void listKitsOnJoin(final PlayerJoinEvent event) {
        try {
            if (this.getPlugin().configValues.listKitsOnJoin) {
                if (event.getPlayer() != null) {
                    if (this.getPlugin().configValues.pvpWorlds.contains("All") || this.getPlugin().configValues.pvpWorlds.contains(event.getPlayer().getWorld().getName())) {
                        this.listKitsOnJoin(event.getPlayer());
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    private void listKitsOnJoin(final Player p) {
        p.getServer().getScheduler().runTaskLater(this.getPlugin(), new Runnable() {
            @Override
            public void run() {
                if (p != null && p.isOnline()) {
                    List<String> kitList = getPlugin().getKitList();
                    StringBuilder sbKits = new StringBuilder().append(ChatColor.GREEN);
                    if (kitList.isEmpty()) {
                        sbKits.append(ChatColor.DARK_RED).append("No kits made.");
                    } else {
                        for (int kitPos = 0; kitPos < kitList.size(); kitPos++) {
                            String kit = kitList.get(kitPos);
                            ChatColor col = ChatColor.GREEN;
                            boolean ignoreKit = false;
                            if (!p.hasPermission("kingkits.kits." + kit.toLowerCase())) {
                                if (!getPlugin().configValues.kitListPermissionsJoin) ignoreKit = true;
                                else col = ChatColor.DARK_RED;
                            }
                            if (!ignoreKit) {
                                if (kitPos == kitList.size() - 1) sbKits.append(col).append(kit);
                                else sbKits.append(col).append(kit).append(", ");
                            } else {
                                if (kitPos == kitList.size() - 1)
                                    sbKits = new StringBuilder().append(replaceLast(sbKits.toString(), ",", ""));
                            }
                        }
                    }
                    if (sbKits.toString() == null ? ChatColor.GREEN + "" == null : sbKits.toString().equals(ChatColor.GREEN + "")) {
                        sbKits = new StringBuilder().append(ChatColor.RED).append("No kits available");
                    }
                    p.sendMessage(ChatColor.GOLD + "PvP Kits: " + sbKits.toString());
                }
            }
        }, 30L);
    }

    /**
     * @param event
     * Lets players create a sign kit *
     */
    @EventHandler
    public void createKitSign(SignChangeEvent event) {
        try {
            if (this.getPlugin().configValues.pvpWorlds.contains("All") || this.getPlugin().configValues.pvpWorlds.contains(event.getPlayer().getWorld().getName())) {
                Player p = event.getPlayer();
                String signType = event.getLine(0);
                if (signType.equalsIgnoreCase(this.getPlugin().configValues.strKitSign)) {
                    if (p.hasPermission(this.getPlugin().permissions.kitCreateSign)) {
                        if (!event.getLine(1).isEmpty()) {
                            event.setLine(0, this.getPlugin().configValues.strKitSignValid);
                        } else {
                            event.setLine(0, this.getPlugin().configValues.strKitSignInvalid);
                            p.sendMessage(ChatColor.RED + "Please enter a kit name on the second line.");
                        }
                    } else {
                        p.sendMessage(ChatColor.DARK_RED + "You do not have access to create a KingKits sign.");
                        event.setLine(0, "");
                        event.setLine(1, "");
                        event.setLine(2, "");
                        event.setLine(3, "");
                    }
                } else if (signType.equalsIgnoreCase(this.getPlugin().configValues.strKitListSign)) {
                    if (p.hasPermission(this.getPlugin().permissions.kitListSign)) {
                        event.setLine(0, this.getPlugin().configValues.strKitListSignValid);
                    } else {
                        p.sendMessage(ChatColor.DARK_RED + "You do not have access to create a KingKits list sign.");
                        event.setLine(0, "");
                        event.setLine(1, "");
                        event.setLine(2, "");
                        event.setLine(3, "");
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Lets players use sign kits *
     * @param event
     */
    @EventHandler
    public void changeKits(PlayerInteractEvent event) {
        try {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (event.getPlayer().getWorld() != null) {
                    if (this.getPlugin().configValues.pvpWorlds.contains("All") || this.getPlugin().configValues.pvpWorlds.contains(event.getPlayer().getWorld().getName())) {
                        Player player = event.getPlayer();
                        BlockState block = event.getClickedBlock().getState();
                        if ((block instanceof Sign)) {
                            Sign sign = (Sign) block;
                            String signLine0 = sign.getLine(0);
                            if (signLine0.equalsIgnoreCase((this.getPlugin().configValues.strKitSignValid.startsWith("&0") ? this.getPlugin().configValues.strKitSignValid.replaceFirst("&0", "") : this.getPlugin().configValues.strKitSignValid))) {
                                if (player.hasPermission(this.getPlugin().permissions.kitUseSign)) {
                                    String line1 = sign.getLine(1);
                                    if (line1 != null) {
                                        if (!line1.equalsIgnoreCase("")) {
                                            List<String> kitList = this.getPlugin().getKitList();
                                            List<String> kitListLC = Utils.toLowerCaseList(kitList);
                                            if (kitListLC.contains(line1.toLowerCase())) {
                                                String kitName = kitList.get(kitListLC.indexOf(line1.toLowerCase()));
                                                try {
                                                    final Kit kit = PvPKits.getKitByName(kitName);
                                                    boolean validCooldown = true;
                                                    if (kit != null && kit.hasCooldown() && !player.hasPermission(this.getPlugin().permissions.kitBypassCooldown)) {
                                                        if (this.getPlugin().getCooldownConfig().contains(player.getName() + "." + kit.getRealName())) {
                                                            long currentCooldown = this.getPlugin().getCooldown(player.getName(), kit.getRealName());
                                                            if (System.currentTimeMillis() - currentCooldown >= kit.getCooldown() * 1000) {
                                                                this.getPlugin().getCooldownConfig().set(player.getName() + "." + kit.getRealName(), null);
                                                                this.getPlugin().saveCooldownConfig();
                                                            } else {
                                                                player.sendMessage(ChatColor.RED + "You must wait " + (kit.getCooldown() - ((System.currentTimeMillis() - currentCooldown) / 1000)) + " second(s) before using this kit again.");
                                                                validCooldown = false;
                                                            }
                                                        }
                                                    }
                                                    if (validCooldown)
                                                        SetKit.setKingKit(player, kitName, true);
                                                } catch (Exception e) {
                                                    player.sendMessage(ChatColor.RED + "Error while trying to set your kit. If it does not work after trying again, try using /pvpkit.");
                                                }
                                            } else {
                                                player.sendMessage(ChatColor.RED + "Unknown kit " + ChatColor.DARK_RED + line1 + ChatColor.RED + ".");
                                                sign.setLine(0, this.getPlugin().configValues.strKitSignInvalid);
                                                sign.update(true);
                                            }
                                        } else {
                                            player.sendMessage(ChatColor.RED + "Sign incorrectly set up.");
                                            sign.setLine(0, this.getPlugin().configValues.strKitSignInvalid);
                                            sign.update(true);
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.RED + "Sign incorrectly set up.");
                                        sign.setLine(0, this.getPlugin().configValues.strKitSignInvalid);
                                        sign.update(true);
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "You do not have permission to use this sign.");
                                }
                                event.setCancelled(true);
                            } else if (signLine0.equalsIgnoreCase(this.getPlugin().configValues.strKitListSignValid.startsWith("&0") ? this.getPlugin().configValues.strKitListSignValid.replaceFirst("&0", "") : this.getPlugin().configValues.strKitListSignValid)) {
                                if (player.hasPermission(this.getPlugin().permissions.kitListSign)) {
                                    if (!this.getPlugin().configValues.kitListMode.equalsIgnoreCase("Gui") && !this.getPlugin().configValues.kitListMode.equalsIgnoreCase("Menu")) {
                                        List<String> kitList = this.getPlugin().getKitList();
                                        player.sendMessage(this.r("&aKits List (" + kitList.size() + "):"));
                                        if (!kitList.isEmpty()) {
                                            for (int kitPos = 0; kitPos < kitList.size(); kitPos++) {
                                                String kitName = kitList.get(kitPos).split(" ")[0];
                                                if (player.hasPermission("kingkits.kits." + kitName.toLowerCase())) {
                                                    player.sendMessage(this.r("&6" + (kitPos + 1) + ". " + kitName));
                                                } else {
                                                    if (this.getPlugin().configValues.kitListPermissions)
                                                        player.sendMessage(this.r("&4" + (kitPos + 1) + ". " + kitName));
                                                }
                                            }
                                        } else {
                                            player.sendMessage(r("&4There are no kits."));
                                        }
                                    } else {
                                        PvPKits.showKitMenu(player);
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "You do not have permission to use this sign.");
                                }
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Remove a player's kit when they die *
     * @param event
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        try {
            Player player = event.getEntity();
            boolean hadKit = false;
            if (this.getPlugin().playerKits.containsKey(player.getName())) {
                this.getPlugin().playerKits.remove(player.getName());
                hadKit = true;
            }
            if (this.getPlugin().usingKits.containsKey(player.getName())) {
                this.getPlugin().usingKits.remove(player.getName());
                if (!this.getPlugin().configValues.dropItemsOnDeath) event.getDrops().clear();
                hadKit = true;
            }
            if (hadKit) {
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);

                for (PotionEffect activeEffect : player.getActivePotionEffects())
                    player.removePotionEffect(activeEffect.getType());
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Remove a player's kit when they leave *
     * @param event
     */
    @EventHandler
    public void removeKitOnQuit(PlayerQuitEvent event) {
        try {
            Player player = event.getPlayer();
            if (this.getPlugin().configValues.removeItemsOnLeave) {
                if (this.getPlugin().playerKits.containsKey(player.getName()) || this.getPlugin().usingKits.containsKey(player.getName())) {
                    player.getInventory().clear();
                    player.getInventory().setArmorContents(null);
                }
            }
            if (this.getPlugin().playerKits.containsKey(player.getName()))
                this.getPlugin().playerKits.remove(player.getName());
            if (this.getPlugin().usingKits.containsKey(player.getName()))
                this.getPlugin().usingKits.remove(player.getName());
            if (GuiKingKits.guiKitMenuMap.containsKey(event.getPlayer().getName())) {
                GuiKitMenu guiKitMenu = GuiKingKits.guiKitMenuMap.get(event.getPlayer().getName());
                guiKitMenu.closeMenu(true, true);
            }
            if (GuiKingKits.guiPreviewKitMap.containsKey(event.getPlayer().getName())) {
                GuiPreviewKit guiPreviewKit = GuiKingKits.guiPreviewKitMap.get(event.getPlayer().getName());
                guiPreviewKit.closeMenu(true, true);
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Remove a player's kit when they get kicked *
     * @param event
     */
    @EventHandler(ignoreCancelled = true)
    public void removeKitOnKick(PlayerKickEvent event) {
        try {
            Player player = event.getPlayer();
            if (event.getPlayer().getWorld() != null) {
                if (this.getPlugin().configValues.pvpWorlds.contains("All") || this.getPlugin().configValues.pvpWorlds.contains(event.getPlayer().getWorld().getName())) {
                    if (this.getPlugin().configValues.removeItemsOnLeave) {
                        if (this.getPlugin().playerKits.containsKey(player.getName()) || getPlugin().usingKits.containsKey(player.getName())) {
                            player.getInventory().clear();
                            player.getInventory().setArmorContents(null);
                        }
                    }
                }
            }
            if (this.getPlugin().playerKits.containsKey(player.getName()))
                this.getPlugin().playerKits.remove(player.getName());
            if (this.getPlugin().usingKits.containsKey(player.getName()))
                this.getPlugin().usingKits.remove(player.getName());
        } catch (Exception ex) {
        }
    }

    /**
     * Bans item dropping *
     * @param event
     */
    @SuppressWarnings("deprecation")
    @EventHandler
    public void banDropItem(PlayerDropItemEvent event) {
        try {
            if (event.getItemDrop() != null) {
                if (event.getPlayer().getWorld() != null) {
                    if (!this.getPlugin().configValues.dropItems) {
                        if (this.getPlugin().configValues.pvpWorlds.contains("All") || this.getPlugin().configValues.pvpWorlds.contains(event.getPlayer().getWorld().getName())) {
                            if (this.getPlugin().configValues.opBypass) {
                                if (!event.getPlayer().isOp()) {
                                    if (this.getPlugin().playerKits.containsKey(event.getPlayer().getName())) {
                                        if (this.getPlugin().configValues.dropAnimations.contains(event.getItemDrop().getItemStack().getType().getId())) {
                                            event.getItemDrop().remove();
                                        } else {
                                            event.getPlayer().sendMessage(ChatColor.RED + "You cannot drop this item whilst using a kit.");
                                            event.setCancelled(true);
                                        }
                                    }
                                }
                            } else {
                                if (this.getPlugin().usingKits.containsKey(event.getPlayer().getName())) {
                                    if (this.getPlugin().configValues.dropAnimations.contains(event.getItemDrop().getItemStack().getType().getId())) {
                                        event.getItemDrop().remove();
                                    } else {
                                        event.getPlayer().sendMessage(ChatColor.RED + "You cannot drop this item whilst using a kit.");
                                        event.setCancelled(true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Bans item picking *
     * @param event
     */
    @EventHandler
    public void banPickupItem(PlayerPickupItemEvent event) {
        try {
            if (event.getItem() != null) {
                if (event.getPlayer().getWorld() != null) {
                    if (!this.getPlugin().configValues.allowPickingUpItems) {
                        if (this.getPlugin().configValues.pvpWorlds.contains("All") || this.getPlugin().configValues.pvpWorlds.contains(event.getPlayer().getWorld().getName())) {
                            if (this.getPlugin().configValues.opBypass) {
                                if (!event.getPlayer().isOp()) {
                                    if (this.getPlugin().playerKits.containsKey(event.getPlayer().getName())) {
                                        event.setCancelled(true);
                                    }
                                }
                            } else {
                                if (this.getPlugin().usingKits.containsKey(event.getPlayer().getName())) {
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Adds score to a player every time they kill another player *
     * @param event
     */
    @EventHandler
    public void addScore(PlayerDeathEvent event) {
        try {
            if (event.getEntityType() == EntityType.PLAYER) {
                if (this.getPlugin().configValues.scores) {
                    if (this.getPlugin().configValues.pvpWorlds.contains("All") || this.getPlugin().configValues.pvpWorlds.contains(event.getEntity().getWorld().getName())) {
                        final Player killer = event.getEntity().getKiller();
                        if (killer != null && !event.getEntity().getName().equals(killer.getName())) {
                            try {
                                if (!this.getPlugin().playerScores.containsKey(killer.getUniqueId()))
                                    this.getPlugin().playerScores.put(killer.getUniqueId(), 0);
                                int currentScore = (Integer) this.getPlugin().playerScores.get(killer.getUniqueId());
                                int newScore = currentScore + this.getPlugin().configValues.scoreIncrement;
                                if (newScore > this.getPlugin().configValues.maxScore)
                                    newScore = this.getPlugin().configValues.maxScore;
                                this.getPlugin().playerScores.put(killer.getUniqueId(), newScore);
                                this.getPlugin().getScoresConfig().set("Scores." + killer.getUniqueId(), (long) newScore);
                                this.getPlugin().saveScoresConfig();

                                if (KingKitsSQL.sqlEnabled) {
                                    final int kScore = newScore;
                                    killer.getServer().getScheduler().runTaskAsynchronously(this.getPlugin(), new Runnable() {
                                        @Override
                                        public void run() {
                                            KingKitsSQL.setScore(killer, kScore);
                                        }
                                    });
                                }
                            } catch (Exception ex) {
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Makes players have a chat prefix with their score *
     * @param event
     */
    @EventHandler(ignoreCancelled = true)
    public void scoreChat(AsyncPlayerChatEvent event) {
        try {
            if (this.getPlugin().configValues.scores) {
                if (this.getPlugin().configValues.pvpWorlds.contains("All") || this.getPlugin().configValues.pvpWorlds.contains(event.getPlayer().getWorld().getName())) {
                    Player player = event.getPlayer();
                    if (!this.getPlugin().playerScores.containsKey(player.getUniqueId())) {
                        this.getPlugin().playerScores.put(player.getUniqueId(), 0);
                        this.getPlugin().getScoresConfig().set("Scores." + player.getUniqueId(), 0);
                        this.getPlugin().saveScoresConfig();
                    }
                    event.setFormat(Utils.replaceChatColour(this.getPlugin().configValues.scoreFormat).replace("<score>", String.valueOf(this.getPlugin().playerScores.get(player.getUniqueId()))) + ChatColor.WHITE + " " + event.getFormat());
                }
            }
        } catch (IllegalFormatException | NullPointerException ex) {
        }
    }

    /**
     * Removes potion effects of a player when they leave *
     * @param event
     */
    @EventHandler
    public void leaveRemovePotionEffects(PlayerQuitEvent event) {
        try {
            if (this.getPlugin().configValues.removePotionEffectsOnLeave) {
                if (event.getPlayer().getWorld() != null) {
                    if (this.getPlugin().configValues.pvpWorlds.contains("All") || this.getPlugin().configValues.pvpWorlds.contains(event.getPlayer().getWorld().getName())) {
                        for (PotionEffect potionEffectOnPlayer : event.getPlayer().getActivePotionEffects()) {
                            PotionEffectType potionEffectType = potionEffectOnPlayer.getType();
                            event.getPlayer().removePotionEffect(potionEffectType);
                        }
                    }
                } else {
                    for (PotionEffect potionEffectOnPlayer : event.getPlayer().getActivePotionEffects()) {
                        event.getPlayer().removePotionEffect(potionEffectOnPlayer.getType());
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Removes potion effects of a player when they get kicked *
     * @param event
     */
    @EventHandler(ignoreCancelled = true)
    public void kickRemovePotionEffects(PlayerKickEvent event) {
        try {
            if (this.getPlugin().configValues.removePotionEffectsOnLeave) {
                if (event.getPlayer().getWorld() != null) {
                    if (this.getPlugin().configValues.pvpWorlds.contains("All") || this.getPlugin().configValues.pvpWorlds.contains(event.getPlayer().getWorld().getName())) {
                        for (PotionEffect potionEffectOnPlayer : event.getPlayer().getActivePotionEffects()) {
                            event.getPlayer().removePotionEffect(potionEffectOnPlayer.getType());
                        }
                    }
                } else {
                    for (PotionEffect potionEffectOnPlayer : event.getPlayer().getActivePotionEffects()) {
                        event.getPlayer().removePotionEffect(potionEffectOnPlayer.getType());
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Makes it so when you right click with a compass, you track the nearest player *
     * @param event
     */
    @EventHandler
    public void rightClickCompass(PlayerInteractEvent event) {
        try {
            if (this.getPlugin().configValues.rightClickCompass) {
                if (this.getPlugin().configValues.pvpWorlds.contains("All") || this.getPlugin().configValues.pvpWorlds.contains(event.getPlayer().getWorld().getName())) {
                    if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        if (event.getPlayer().getInventory().getItemInHand() != null) {
                            if (event.getPlayer().getInventory().getItemInHand().getType() == Material.COMPASS) {
                                if (event.getPlayer().hasPermission(this.getPlugin().permissions.rightClickCompass) || event.getPlayer().isOp()) {
                                    Player nearestPlayer = null;
                                    double distance = -1D;
                                    for (Player target : event.getPlayer().getServer().getOnlinePlayers()) {
                                        if (!target.getName().equalsIgnoreCase(event.getPlayer().getName())) {
                                            if (event.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(target.getLocation().getWorld().getName())) {
                                                if (distance == -1D) {
                                                    distance = event.getPlayer().getLocation().distance(target.getLocation());
                                                    nearestPlayer = target;
                                                } else {
                                                    if (event.getPlayer().getLocation().distance(target.getLocation()) < distance) {
                                                        distance = event.getPlayer().getLocation().distance(target.getLocation());
                                                        nearestPlayer = target;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (nearestPlayer != null) {
                                        event.getPlayer().setCompassTarget(nearestPlayer.getLocation());
                                        event.getPlayer().sendMessage(ChatColor.YELLOW + "Your compass is pointing at " + nearestPlayer.getName() + ".");
                                        if (this.getPlugin().compassTargets.containsKey(event.getPlayer()))
                                            this.getPlugin().compassTargets.remove(event.getPlayer());
                                        this.getPlugin().compassTargets.put(event.getPlayer().getPlayer(), nearestPlayer.getPlayer());
                                    } else {
                                        event.getPlayer().setCompassTarget(event.getPlayer().getWorld().getSpawnLocation());
                                        event.getPlayer().sendMessage(ChatColor.YELLOW + "Your compass is pointing at spawn.");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Makes compass trackers track the new location of their target *
     * @param event
     */
    @EventHandler(ignoreCancelled = true)
    public void compassTrackMove(PlayerMoveEvent event) {
        try {
            if (this.getPlugin().configValues.rightClickCompass) {
                if (this.getPlugin().compassTargets.containsValue(event.getPlayer())) {
                    Player tracker = null;
                    for (Map.Entry<Player, Player> compassTargetsEntry : this.getPlugin().compassTargets.entrySet()) {
                        Player key = compassTargetsEntry.getKey();
                        Player value = compassTargetsEntry.getValue();
                        if (key != null) {
                            if (value != null) {
                                if (key.isOnline()) {
                                    if (value.isOnline()) {
                                        if (event.getPlayer().getName().equalsIgnoreCase(value.getName()))
                                            tracker = key.getPlayer();
                                    }
                                }
                            }
                        }
                    }
                    if (tracker != null) tracker.setCompassTarget(event.getPlayer().getLocation());
                }
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Makes compass trackers lose their target when they get leave or their target leaves *
     * @param event
     */
    @EventHandler
    public void compassTrackerLeave(PlayerQuitEvent event) {
        try {
            if (this.getPlugin().configValues.rightClickCompass) {
                if (this.getPlugin().compassTargets.containsValue(event.getPlayer())) {
                    Player tracker = null;
                    for (Map.Entry<Player, Player> e : this.getPlugin().compassTargets.entrySet()) {
                        Player key = e.getKey();
                        Player value = e.getValue();
                        if (key != null) {
                            if (value != null) {
                                if (key.isOnline()) {
                                    if (value.isOnline()) {
                                        if (event.getPlayer().getName().equalsIgnoreCase(value.getName()))
                                            tracker = key.getPlayer();
                                    }
                                }
                            }
                        }
                    }
                    if (tracker != null) this.getPlugin().compassTargets.remove(tracker);
                }
                if (this.getPlugin().compassTargets.containsKey(event.getPlayer()))
                    this.getPlugin().compassTargets.remove(event.getPlayer());
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Makes compass trackers lose their target when they get kicked or their target gets kicked *
     * @param event
     */
    @EventHandler(ignoreCancelled = true)
    public void compassTrackerKick(PlayerKickEvent event) {
        try {
            if (this.getPlugin().configValues.rightClickCompass) {
                if (this.getPlugin().compassTargets.containsValue(event.getPlayer())) {
                    Player tracker = null;
                    for (Map.Entry<Player, Player> e : this.getPlugin().compassTargets.entrySet()) {
                        Player key = e.getKey();
                        Player value = e.getValue();
                        if (key != null) {
                            if (value != null) {
                                if (key.isOnline()) {
                                    if (value.isOnline()) {
                                        if (event.getPlayer().getName().equalsIgnoreCase(value.getName()))
                                            tracker = key.getPlayer();
                                    }
                                }
                            }
                        }
                    }
                    if (tracker != null) this.getPlugin().compassTargets.remove(tracker);
                }
                if (this.getPlugin().compassTargets.containsKey(event.getPlayer()))
                    this.getPlugin().compassTargets.remove(event.getPlayer());
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Allows players to right click with soup and instantly drink it to be healed or fed *
     * @param event
     */
    @EventHandler
    public void quickSoup(PlayerInteractEvent event) {
        try {
            if (event.getItem() != null) {
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (event.getItem().getType() == Material.MUSHROOM_SOUP) {
                        if (this.getPlugin().configValues.quickSoup) {
                            if (event.getPlayer().hasPermission(this.getPlugin().permissions.quickSoup) || (this.getPlugin().configValues.opBypass && event.getPlayer().isOp())) {
                                if (this.getPlugin().configValues.pvpWorlds.contains("All") || this.getPlugin().configValues.pvpWorlds.contains(event.getPlayer().getWorld().getName())) {
                                    Player player = event.getPlayer();
                                    int soupAmount = player.getInventory().getItemInHand().getAmount();
                                    if (soupAmount == 1) {
                                        if (player.getHealth() < 20) {
                                            if (player.getHealth() + 5 > player.getMaxHealth())
                                                player.setHealth(player.getMaxHealth());
                                            else player.setHealth(player.getHealth() + 5);
                                        } else if (player.getFoodLevel() < 20) {
                                            if (player.getFoodLevel() + 4 > 20) player.setFoodLevel(20);
                                            else player.setFoodLevel(player.getFoodLevel() + 4);
                                        } else {
                                            return;
                                        }
                                        player.getInventory().setItemInHand(new ItemStack(Material.BOWL, 1));
                                        event.setCancelled(true);
                                    } else if (soupAmount > 0) {
                                        if (player.getHealth() < 20) {
                                            if (player.getHealth() + 5 > player.getMaxHealth())
                                                player.setHealth(player.getMaxHealth());
                                            else player.setHealth(player.getHealth() + 5);
                                        } else if (player.getFoodLevel() < 20) {
                                            if (player.getFoodLevel() + 4 > 20) player.setFoodLevel(20);
                                            else player.setFoodLevel(player.getFoodLevel() + 4);
                                        } else {
                                            return;
                                        }
                                        int newAmount = soupAmount - 1;
                                        ItemStack newItem = player.getInventory().getItemInHand();
                                        newItem.setAmount(newAmount);
                                        player.getInventory().setItemInHand(newItem);
                                        player.getInventory().addItem(new ItemStack(Material.BOWL));
                                        event.setCancelled(true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Disables block breaking *
     * @param event
     */
    @EventHandler(ignoreCancelled = true)
    public void disableBlockBreaking(BlockBreakEvent event) {
        try {
            if (this.getPlugin().configValues.banBlockBreakingAndPlacing) {
                if (this.getPlugin().configValues.pvpWorlds.contains("All") || this.getPlugin().configValues.pvpWorlds.contains(event.getPlayer().getWorld().getName())) {
                    if (this.getPlugin().configValues.opBypass) {
                        if (!event.getPlayer().isOp()) event.setCancelled(true);
                    } else {
                        event.setCancelled(true);
                    }
                }
            }
            if (event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
                String playerKit = this.getPlugin().usingKits.get(event.getPlayer().getName());
                if (playerKit != null) {
                    boolean repair = false;
                    if (this.getPlugin().configValues.disableItemBreaking) {
                        repair = true;
                    } else {
                        Kit kit = PvPKits.getKitByName(playerKit);
                        if (kit != null && !kit.canItemsBreak()) {
                            repair = true;
                        }
                    }
                    if (repair) {
                        final Player player = event.getPlayer();
                        if (player.getItemInHand() != null && (this.isTool(player.getItemInHand().getType()) || player.getItemInHand().getType() == Material.FISHING_ROD || player.getItemInHand().getType() == Material.FLINT_AND_STEEL)) {
                            player.getServer().getScheduler().runTask(this.getPlugin(), new Runnable() {
                                @Override
                                public void run() {
                                    if (player != null && player.isOnline() && player.getItemInHand() != null && (isTool(player.getItemInHand().getType()) || player.getItemInHand().getType() == Material.FISHING_ROD || player.getItemInHand().getType() == Material.FLINT_AND_STEEL)) {
                                        ItemStack item = player.getItemInHand();
                                        item.setDurability((short) 0);
                                        player.setItemInHand(item);
                                        player.updateInventory();
                                    }
                                }
                            });
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Disables block placing *
     * @param event
     */
    @EventHandler(ignoreCancelled = true)
    public void disableBlockPlacing(BlockPlaceEvent event) {
        try {
            if (this.getPlugin().configValues.banBlockBreakingAndPlacing) {
                if (this.getPlugin().configValues.pvpWorlds.contains("All") || this.getPlugin().configValues.pvpWorlds.contains(event.getPlayer().getWorld().getName())) {
                    if (this.getPlugin().configValues.opBypass) {
                        if (!event.getPlayer().isOp()) event.setCancelled(true);
                    } else {
                        event.setCancelled(true);
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Disables minecraft death messages *
     * @param event
     */
    @EventHandler
    public void disableDeathMessages(PlayerDeathEvent event) {
        try {
            if (event.getEntityType() == EntityType.PLAYER) {
                if (this.getPlugin().configValues.disableDeathMessages) {
                    if (this.getPlugin().configValues.pvpWorlds.contains("All") || this.getPlugin().configValues.pvpWorlds.contains(event.getEntity().getWorld().getName()))
                        event.setDeathMessage("");
                }
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Locks hunger bars so players can't lose hunger *
     * @param event
     */
    @EventHandler
    public void lockHunger(FoodLevelChangeEvent event) {
        try {

            if (this.getPlugin().configValues.lockHunger) {
                if (event.getEntity() instanceof Player) {
                    Player player = (Player) event.getEntity();
                    if (this.getPlugin().configValues.pvpWorlds.contains("All") || this.getPlugin().configValues.pvpWorlds.contains(player.getWorld().getName()))
                        event.setFoodLevel(20);
                }
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Gives a player money when they kill another player *
     * @param event
     */
    @EventHandler
    public void moneyPerKill(PlayerKilledEvent event) {
        try {
            Player killer = event.getPlayer();
            if (this.getPlugin().configValues.vaultValues.useEconomy && this.getPlugin().configValues.vaultValues.useMoneyPerKill) {
                if (this.getPlugin().configValues.pvpWorlds.contains("All") || this.getPlugin().configValues.pvpWorlds.contains(killer.getWorld().getName())) {
                    net.milkbowl.vault.economy.Economy economy = (net.milkbowl.vault.economy.Economy) this.getPlugin().vault.getEconomy();
                    if (!economy.hasAccount(killer)) economy.createPlayerAccount(killer);
                    economy.depositPlayer(killer, this.getPlugin().configValues.vaultValues.moneyPerKill);
                    killer.sendMessage(this.getPlugin().getMPKMessage(event.getDead(), this.getPlugin().configValues.vaultValues.moneyPerKill));
                }
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Takes money from a player when they die by another player *
     * @param event
     */
    @EventHandler
    public void moneyPerDeath(PlayerDeathEvent event) {
        try {
            if (event.getEntity().getKiller() != null) {
                if (this.getPlugin().configValues.vaultValues.useEconomy && this.getPlugin().configValues.vaultValues.useMoneyPerDeath) {
                    if (!event.getEntity().getName().equalsIgnoreCase(event.getEntity().getKiller().getName())) {
                        if (this.getPlugin().configValues.pvpWorlds.contains("All") || this.getPlugin().configValues.pvpWorlds.contains(event.getEntity().getKiller().getWorld().getName())) {
                            net.milkbowl.vault.economy.Economy economy = (net.milkbowl.vault.economy.Economy) this.getPlugin().vault.getEconomy();
                            economy.withdrawPlayer(event.getEntity(), this.getPlugin().configValues.vaultValues.moneyPerDeath);
                            event.getEntity().sendMessage(this.getPlugin().getMPDMessage(event.getEntity(), this.getPlugin().configValues.vaultValues.moneyPerDeath));
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Do stuff when the player changes worlds.
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        try {
            if (!this.getPlugin().configValues.pvpWorlds.contains("All") && !this.getPlugin().configValues.pvpWorlds.contains(event.getPlayer().getWorld().getName())) {
                if (this.getPlugin().playerKits.containsKey(event.getPlayer().getName()))
                    this.getPlugin().playerKits.remove(event.getPlayer().getName());
                if (this.getPlugin().usingKits.containsKey(event.getPlayer().getName())) {
                    this.getPlugin().usingKits.remove(event.getPlayer().getName());
                    if (!this.getPlugin().getServer().getPluginManager().isPluginEnabled(this.getPlugin().configValues.multiInvsPlugin) && !this.getPlugin().configValues.multiInvs) {
                        event.getPlayer().getInventory().clear();
                        event.getPlayer().getInventory().setArmorContents(null);
                        for (PotionEffect potionEffectOnPlayer : event.getPlayer().getActivePotionEffects())
                            event.getPlayer().removePotionEffect(potionEffectOnPlayer.getType());
                    }
                }
            } else if (this.getPlugin().configValues.pvpWorlds.contains("All") || (!this.getPlugin().configValues.pvpWorlds.contains(event.getFrom().getName()) && this.getPlugin().configValues.pvpWorlds.contains(event.getPlayer().getWorld().getName()))) {
                if (!this.getPlugin().getServer().getPluginManager().isPluginEnabled(this.getPlugin().configValues.multiInvsPlugin) && !this.getPlugin().configValues.multiInvs) {
                    event.getPlayer().getInventory().clear();
                    event.getPlayer().getInventory().setArmorContents(null);
                    for (PotionEffect potionEffectOnPlayer : event.getPlayer().getActivePotionEffects())
                        event.getPlayer().removePotionEffect(potionEffectOnPlayer.getType());
                }
                if (this.getPlugin().configValues.listKitsOnJoin) this.listKitsOnJoin(event.getPlayer());
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Disables gamemode changes while using a kit *
     * @param event
     */
    @EventHandler(ignoreCancelled = true)
    public void disableGamemode(PlayerGameModeChangeEvent event) {
        try {
            if (this.getPlugin().configValues.disableGamemode) {
                if (event.getNewGameMode() == GameMode.CREATIVE) {
                    if (this.getPlugin().configValues.opBypass && event.getPlayer().isOp()) return;
                    if (this.getPlugin().configValues.pvpWorlds.contains("All") || this.getPlugin().configValues.pvpWorlds.contains(event.getPlayer().getWorld().getName()))
                        event.setCancelled(true);
                }
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Update killstreaks and runs commands (if they exist in the configuration) when a player kills another player *
     * @param event
     */
    @EventHandler
    public void updateKillstreak(PlayerKilledEvent event) {
        try {
            if (this.getPlugin().configValues.killstreaks) {
                if (this.getPlugin().configValues.pvpWorlds.contains("All") || this.getPlugin().configValues.pvpWorlds.contains(event.getPlayer().getWorld().getName())) {
                    if (!this.getPlugin().playerKillstreaks.containsKey(event.getPlayer().getName()))
                        this.getPlugin().playerKillstreaks.put(event.getPlayer().getName(), 0L);

                    long currentKillstreak = this.getPlugin().playerKillstreaks.get(event.getPlayer().getName());
                    if (currentKillstreak + 1L > Long.MAX_VALUE - 1)
                        this.getPlugin().playerKillstreaks.put(event.getPlayer().getName(), 0L);
                    else
                        this.getPlugin().playerKillstreaks.put(event.getPlayer().getName(), this.getPlugin().playerKillstreaks.get(event.getPlayer().getName()) + 1L);

                    currentKillstreak = this.getPlugin().playerKillstreaks.get(event.getPlayer().getName());
                    if (this.getPlugin().getKillstreaksConfig().contains("Killstreak " + currentKillstreak)) {
                        List<String> killstreakCommands = this.getPlugin().getKillstreaksConfig().getStringList("Killstreak " + currentKillstreak);
                        for (String killstreakCommand : killstreakCommands)
                            event.getPlayer().getServer().dispatchCommand(event.getPlayer().getServer().getConsoleSender(), killstreakCommand.replace("<player>", event.getPlayer().getName()).replace("<displayname>", event.getPlayer().getDisplayName()).replace("<killstreak>", "" + currentKillstreak));
                    }
                    if (PvPKits.hasKit(event.getPlayer())) {
                        Kit playerKit = PvPKits.getKitByName(PvPKits.getKit(event.getPlayer()));
                        if (playerKit != null) {
                            if (playerKit.getKillstreaks().containsKey(currentKillstreak)) {
                                List<String> killstreakCommands = playerKit.getKillstreaks().get(currentKillstreak);
                                for (String killstreakCommand : killstreakCommands)
                                    event.getPlayer().getServer().dispatchCommand(event.getPlayer().getServer().getConsoleSender(), killstreakCommand.replace("<player>", event.getPlayer().getName()).replace("<displayname>", event.getPlayer().getDisplayName()).replace("<killstreak>", "" + currentKillstreak));
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Update killstreaks when a player dies *
     * @param event
     */
    @EventHandler
    public void removeKillstreakOnDeath(PlayerDeathEvent event) {
        try {
            if (this.getPlugin().configValues.killstreaks) {
                if (event.getEntity() != null) {
                    this.getPlugin().playerKillstreaks.remove(event.getEntity().getName());
                }
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Update killstreaks when a player leaves *
     * @param event
     */
    @EventHandler
    public void removeKillstreakOnLeave(PlayerQuitEvent event) {
        try {
            if (this.getPlugin().configValues.killstreaks) {
                if (event.getPlayer() != null) this.getPlugin().playerKillstreaks.remove(event.getPlayer().getName());
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Update killstreaks when a player gets kicked *
     * @param event
     */
    @EventHandler(ignoreCancelled = true)
    public void removeKillstreakOnKick(PlayerKickEvent event) {
        try {
            if (this.getPlugin().configValues.killstreaks) {
                if (event.getPlayer() != null) this.getPlugin().playerKillstreaks.remove(event.getPlayer().getName());
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Make weapons unbreakable *
     * @param event
     */
    @EventHandler(ignoreCancelled = true)
    public void noWeaponBreakDamage(EntityDamageByEntityEvent event) {
        try {
            if (event.getDamager() instanceof Player) {
                final Player player = (Player) event.getDamager();
                if (player.getGameMode() == GameMode.SURVIVAL) {
                    if (player.getItemInHand() != null && this.getPlugin().usingKits.containsKey(player.getName())) {
                        boolean repair = false;
                        if ((this.isTool(player.getItemInHand().getType()) || player.getItemInHand().getType() == Material.FISHING_ROD || player.getItemInHand().getType() == Material.FLINT_AND_STEEL)) {
                            if (this.getPlugin().configValues.disableItemBreaking) {
                                repair = true;
                            } else {
                                Kit kit = PvPKits.getKitByName(this.getPlugin().usingKits.get(player.getName()));
                                if (kit != null && !kit.canItemsBreak()) {
                                    repair = true;
                                }
                            }
                        }
                        if (repair) {
                            player.getServer().getScheduler().runTask(this.getPlugin(), new Runnable() {
                                @Override
                                public void run() {
                                    if (player != null && player.isOnline() && getPlugin().usingKits.containsKey(player.getName()) && player.getItemInHand() != null && (isTool(player.getItemInHand().getType()) || player.getItemInHand().getType() == Material.FISHING_ROD || player.getItemInHand().getType() == Material.FLINT_AND_STEEL)) {
                                        ItemStack item = player.getItemInHand();
                                        item.setDurability((short) 0);
                                        player.setItemInHand(item);
                                        player.updateInventory();
                                    }
                                }
                            });
                        }
                    }
                }
            }
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                if (player.getGameMode() == GameMode.SURVIVAL) {
                    if (this.getPlugin().usingKits.containsKey(player.getName())) {
                        boolean repair = false;
                        if (this.getPlugin().configValues.disableItemBreaking) {
                            repair = true;
                        } else {
                            Kit kit = PvPKits.getKitByName(this.getPlugin().usingKits.get(player.getName()));
                            if (kit != null && !kit.canItemsBreak()) {
                                repair = true;
                            }
                        }
                        if (repair) {
                            ItemStack[] armour = player.getInventory().getArmorContents();
                            for (ItemStack i : armour)
                                i.setDurability((short) 0);
                            player.getInventory().setArmorContents(armour);
                            player.updateInventory();
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Make bows unbreakable *
     * @param event
     */
    @EventHandler(ignoreCancelled = true)
    public void noWeaponBreakDamage(EntityShootBowEvent event) {
        try {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                if (this.getPlugin().usingKits.containsKey(player.getName())) {
                    boolean repair = false;
                    if (this.getPlugin().configValues.disableItemBreaking) {
                        repair = true;
                    } else {
                        Kit kit = PvPKits.getKitByName(this.getPlugin().usingKits.get(player.getName()));
                        if (kit != null && !kit.canItemsBreak()) {
                            repair = true;
                        }
                    }
                    if (repair)
                        event.getBow().setDurability((short) 0);
                }
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Make items unbreakable *
     * @param event
     */
    @EventHandler(ignoreCancelled = true)
    public void noWeaponBreakDamage(PlayerInteractEvent event) {
        try {
            if (event.getItem() != null) {
                if (this.getPlugin().usingKits.containsKey(event.getPlayer().getName())) {
                    if (this.isTool(event.getItem().getType()) || event.getItem().getType() == Material.FISHING_ROD || event.getItem().getType() == Material.FLINT_AND_STEEL) {
                        boolean repair = false;
                        if (this.getPlugin().configValues.disableItemBreaking) {
                            repair = true;
                        } else {
                            Kit kit = PvPKits.getKitByName(this.getPlugin().usingKits.get(event.getPlayer().getName()));
                            if (kit != null && !kit.canItemsBreak()) {
                                repair = true;
                            }
                        }
                        if (repair) event.getItem().setDurability((short) 0);
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void kitMenuOnJoin(PlayerJoinEvent event) {
        try {
            if (this.getPlugin().configValues.kitMenuOnJoin) {
                final Player player = event.getPlayer();
                player.getServer().getScheduler().runTaskLater(this.getPlugin(), new Runnable() {
                    @SuppressWarnings("deprecation")
                    @Override
                    public void run() {
                        if (player != null && player.isOnline()) {
                            if (!GuiKingKits.guiKitMenuMap.containsKey(player.getName()) && !GuiKingKits.guiPreviewKitMap.containsKey(player.getName())) {
                                PvPKits.showKitMenu(player, false);
                            }
                        }
                    }
                }, 20L);
            }
        } catch (Exception ex) {
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOW)
    public void playerScoreboardJoin(PlayerJoinEvent event) {
        try {
            if (event.getPlayer().getScoreboard() != null) {
                Objective scoreboardObj = event.getPlayer().getScoreboard().getObjective("KingKits");
                if (scoreboardObj != null) {
                    Scoreboard playerBoard = event.getPlayer().getScoreboard();
                    playerBoard.resetScores(Bukkit.getOfflinePlayer(ChatColor.GREEN + "Score:"));
                    playerBoard.resetScores(Bukkit.getOfflinePlayer(ChatColor.GREEN + "Killstreak:"));
                    event.getPlayer().getScoreboard().resetScores(ChatColor.GREEN + "Score:");
                    event.getPlayer().getScoreboard().resetScores(ChatColor.GREEN + "Killstreak:");
                    playerBoard.clearSlot(DisplaySlot.SIDEBAR);
                    event.getPlayer().setScoreboard(playerBoard);
                }
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
        }
    }

    @EventHandler
    public void playerScoreboardLeave(PlayerQuitEvent event) {
        try {
            Scoreboard pScoreboard = event.getPlayer().getScoreboard();
            if (pScoreboard != null) {
                if (pScoreboard.getObjective("KingKits") != null) {
                    Scoreboard playerBoard = event.getPlayer().getScoreboard();
                    playerBoard.resetScores(Bukkit.getOfflinePlayer(ChatColor.GREEN + "Score:"));
                    playerBoard.resetScores(Bukkit.getOfflinePlayer(ChatColor.GREEN + "Killstreak:"));
                    event.getPlayer().getScoreboard().resetScores(ChatColor.GREEN + "Score:");
                    event.getPlayer().getScoreboard().resetScores(ChatColor.GREEN + "Killstreak:");
                    playerBoard.clearSlot(DisplaySlot.SIDEBAR);
                    event.getPlayer().setScoreboard(playerBoard);
                }
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
        }
    }

    @EventHandler
    public void playerScoreboardKicked(PlayerQuitEvent event) {
        try {
            Scoreboard pScoreboard = event.getPlayer().getScoreboard();
            if (pScoreboard != null) {
                if (pScoreboard.getObjective("KingKits") != null) {
                    event.getPlayer().setScoreboard(event.getPlayer().getServer().getScoreboardManager().getNewScoreboard());
                }
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
        }
    }

    /**
     * Gets the plugin instance *
     */
    private KingKits getPlugin() {
        return this.plugin;
    }

    /**
     * Returns if a material is a tool/sword *
     */
    private boolean isTool(Material material) {
        return material.name().endsWith("SWORD") || material.name().endsWith("PICKAXE") || material.name().endsWith("AXE") || material.name().endsWith("SPADE") || material.name().endsWith("SHOVEL") || material.name().endsWith("HOE");
    }

    /**
     * Returns a list of lower case strings *
     * @param originalMap
     * @return 
     */
    public static List<String> listToLowerCase(List<String> originalMap) {
        List<String> newMap = new ArrayList<>();
        for (String s : originalMap)
            newMap.add(s.toLowerCase());
        return newMap;
    }

    /**
     * Replaces the last occurrence of a string in a string *
     */
    private String replaceLast(String text, String original, String replacement) {
        String message = text;
        if (message.contains(original)) {
            StringBuilder stringBuilder = new StringBuilder(text);
            stringBuilder.replace(text.lastIndexOf(original), text.lastIndexOf(original) + 1, replacement);
            message = stringBuilder.toString();
        }
        return message;
    }

    /**
     * Returns a string with the real colours *
     */
    private String r(String message) {
        return Utils.replaceChatColour(message);
    }

}
