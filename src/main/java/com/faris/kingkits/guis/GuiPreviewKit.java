package com.faris.kingkits.guis;

import com.faris.kingkits.Kit;
import com.faris.kingkits.hooks.PvPKits;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GuiPreviewKit extends GuiKingKits {
    private List<ItemStack> guiItemStacks = null;

    /**
     * Create a new Kit GUI preview instance.
     *
     * @param player - The player that is using the menu
     * @param kitName - The kit name
     */
    public GuiPreviewKit(Player player, String kitName) {
        super(player, player.getServer().createInventory(player, 45, ChatColor.RED + kitName + ChatColor.DARK_GRAY + " kit preview"));
        Kit kit = PvPKits.getKitByName(kitName);
        this.guiItemStacks = kit != null ? kit.getMergedItems() : new ArrayList<ItemStack>();
    }

    @Override
    public boolean openMenu() {
        if (guiPreviewKitMap.containsKey(this.getPlayerName())) {
            GuiPreviewKit guiPreviewKitMenu = guiPreviewKitMap.get(this.getPlayerName());
            if (guiPreviewKitMenu != null) {
                guiPreviewKitMenu.closeMenu(true, true);
                guiPreviewKitMap.remove(this.getPlayerName());
            }
        }
        if (super.openMenu()) {
            guiPreviewKitMap.put(this.getPlayerName(), this);
            return true;
        }
        return false;
    }

    @Override
    public void closeMenu(boolean unregisterEvents, boolean closeInventory) {
        super.closeMenu(unregisterEvents, closeInventory);
        guiPreviewKitMap.remove(this.getPlayerName());
    }

    @Override
    protected void fillInventory() {
        for (int itemStackPos = 0; itemStackPos < this.guiItemStacks.size(); itemStackPos++) {
            if (itemStackPos < 36) {
                ItemStack itemStack = this.guiItemStacks.get(itemStackPos);
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    this.guiInventory.addItem(itemStack);
                }
            }
        }
        ItemStack backItem = new ItemStack(Material.STONE_BUTTON);
        ItemMeta backItemMeta = backItem.getItemMeta();
        if (backItemMeta != null) {
            backItemMeta.setDisplayName(ChatColor.AQUA + "Back");
            backItem.setItemMeta(backItemMeta);
        }
        this.guiInventory.setItem(this.guiInventory.getSize() - 1, backItem);
    }

    /**
     *
     * @param event
     */
    @EventHandler
    @Override
    protected void onPlayerClickInventory(InventoryClickEvent event) {
        try {
            if (event.getWhoClicked().getName().equals(this.getPlayerName())) {
                event.setCancelled(true);
                if (event.getWhoClicked() instanceof Player) {
                    final Player player = (Player) event.getWhoClicked();
                    if (event.getSlot() == this.guiInventory.getSize() - 1 && event.getCurrentItem().getType() == Material.STONE_BUTTON) {
                        this.closeMenu(true, true);
                        if (!guiKitMenuMap.containsKey(event.getWhoClicked().getName())) {
                            player.getServer().getScheduler().runTaskLater(this.getPlugin(), new Runnable() {
                                @Override
                                public void run() {
                                    if (player != null) {
                                        PvPKits.showKitMenu(player);
                                    }
                                }
                            }, 3L);
                        }
                    } else {
                        player.getServer().getScheduler().runTaskLater(this.getPlugin(), new Runnable() {
                            @SuppressWarnings("deprecation")
                            @Override
                            public void run() {
                                if (player != null && player.isOnline()) player.updateInventory();
                            }
                        }, 2L);
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.LOW)
    @Override
    protected void onPlayerCloseInventory(InventoryCloseEvent event) {
        try {
            if (this.guiInventory != null && event.getInventory() != null) {
                if (event.getPlayer() instanceof Player) {
                    if (this.getPlayerName().equals(event.getPlayer().getName())) {
                        this.closeMenu(true, false);
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

}
