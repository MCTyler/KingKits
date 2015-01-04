package com.faris.kingkits.guis;

import com.faris.kingkits.Kit;
import com.faris.kingkits.helpers.KitStack;
import com.faris.kingkits.helpers.Utils;
import com.faris.kingkits.hooks.PvPKits;
import com.faris.kingkits.listeners.commands.SetKit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GuiKitMenu extends GuiKingKits {
    private KitStack[] guiKitStacks = null;

    /**
     * Create a new gui menu instance.
     *
     * @param player - The player that is using the menu
     * @param title - The title of the menu
     * @param kitStacks - The kits in the menu
     */
    public GuiKitMenu(Player player, String title, KitStack[] kitStacks) {
        super(player, player.getServer().createInventory(null, kitStacks.length > 32 ? 45 : 36, title));
        this.guiKitStacks = kitStacks;
    }

    @Override
    public boolean openMenu() {
        try {
            if (guiKitMenuMap.containsKey(this.getPlayerName())) {
                GuiKitMenu guiKitMenu = guiKitMenuMap.get(this.getPlayerName());
                if (guiKitMenu != null) {
                    guiKitMenu.closeMenu(true, true);
                    guiKitMenuMap.remove(this.getPlayerName());
                }
            }
            if (super.openMenu()) {
                guiKitMenuMap.put(this.getPlayerName(), this);
                return true;
            }
        } catch (Exception ex) {
        }
        return false;
    }

    @Override
    public void closeMenu(boolean unregisterEvents, boolean closeInventory) {
        super.closeMenu(unregisterEvents, closeInventory);
        guiKitMenuMap.remove(this.getPlayerName());
    }

    @Override
    protected void fillInventory() {
        for (KitStack guiKitStack : this.guiKitStacks) {
            try {
                ItemStack currentStack = guiKitStack.getItemStack();
                if (currentStack != null) {
                    if (currentStack.getType() != Material.AIR) {
                        if (currentStack.getItemMeta() != null) {
                            ItemMeta itemMeta = currentStack.getItemMeta();
                            ChatColor kitColour = this.getPlayer().hasPermission("kingkits.kits." + guiKitStack.getKitName().toLowerCase()) ? ChatColor.GREEN : ChatColor.DARK_RED;
                            itemMeta.setDisplayName(ChatColor.RESET + "" + kitColour + guiKitStack.getKitName());
                            currentStack.setItemMeta(itemMeta);
                        }
                        this.guiInventory.addItem(currentStack);
                    }
                }
            }catch (Exception ex) {
            }
        }
    }

    /**
     * Returns the kit item stacks *
     * @return 
     */
    public KitStack[] getKitStacks() {
        return this.guiKitStacks;
    }

    /**
     * Sets the kit item stacks *
     * @param kitStacks
     * @return 
     */
    public GuiKitMenu setKitStacks(KitStack[] kitStacks) {
        this.guiKitStacks = kitStacks;
        return this;
    }

    /**
     *
     * @param event
     */
    @EventHandler
    @Override
    protected void onPlayerClickInventory(InventoryClickEvent event) {
        try {
            if (this.guiInventory != null && event.getInventory() != null && event.getWhoClicked() != null) {
                if (event.getWhoClicked() instanceof Player) {
                    if (event.getSlot() >= 0) {
                        if (event.getSlotType() == SlotType.CONTAINER) {
                            if (event.getWhoClicked().getName().equals(this.getPlayerName())) {
                                if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                                    event.setCurrentItem(null);
                                    event.setCancelled(true);
                                    this.closeMenu(true, true);
                                    if (this.guiKitStacks.length >= event.getSlot()) {
                                        final String kitName = Utils.stripColour(this.guiKitStacks[event.getSlot()].getKitName());
                                        if (kitName != null) {
                                            if (event.getWhoClicked().hasPermission("kingkits.kits." + kitName.toLowerCase())) {
                                                final Kit kit = PvPKits.getKitByName(kitName);
                                                final Player player = (Player) event.getWhoClicked();
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
                                                if (validCooldown) {
                                                    SetKit.setKingKit(player, kit != null ? kit.getRealName() : kitName, true);
                                                }
                                            } else if (this.getPlugin().configValues.showKitPreview) {
                                                if (!guiPreviewKitMap.containsKey(event.getWhoClicked().getName())) {
                                                    final Player player = (Player) event.getWhoClicked();
                                                    player.getServer().getScheduler().runTaskLater(this.getPlugin(), new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (player != null) {
                                                                if (!guiPreviewKitMap.containsKey(player.getName())) {
                                                                    new GuiPreviewKit(player, kitName).openMenu();
                                                                }
                                                            }
                                                        }
                                                    }, 3L);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            if (event.getInventory() != null && this.guiInventory != null) {
                if (this.getPlayer().equals(event.getWhoClicked().getName())) {
                    event.setCurrentItem(null);
                    event.setCancelled(true);
                    this.closeMenu(true, true);
                }
            }
        }
    }
}
