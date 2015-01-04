package com.faris.kingkits.listeners.event.custom;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerCreateKitEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private String kitName = "";
    private Map<Integer, ItemStack> kitContents = new HashMap<>();
    private List<ItemStack> armourContents = new ArrayList<>();

    private boolean isCancelled = false;

    /**
     * Create a new PlayerCreateKitEvent instance.
     *
     * @param player - The player.
     * @param kitName - The new kit.
     * @param newKitItems - The kit items.
     * @param armourContents
     */
    public PlayerCreateKitEvent(Player player, String kitName, final List<ItemStack> newKitItems, List<ItemStack> armourContents) {
        this(player, kitName, new HashMap<Integer, ItemStack>() {
            {
                for (int i = 0; i < newKitItems.size(); i++) {
                    this.put(i, newKitItems.get(i));
                }
            }
        }, armourContents);

    }

    /**
     * Create a new PlayerCreateKitEvent instance.
     *
     * @param player - The player.
     * @param kitName - The new kit.
     * @param newKitItems - The kit items.
     * @param armourContents
     */
    public PlayerCreateKitEvent(Player player, String kitName, Map<Integer, ItemStack> newKitItems, List<ItemStack> armourContents) {
        super(player);
        this.kitName = kitName;
        this.kitContents = newKitItems;
        this.armourContents = armourContents;
    }

    /**
     * Returns the kit's name *
     * @return 
     */
    public String getKit() {
        return this.kitName;
    }

    /**
     * Returns an unmodifiable List of armour in the new kit *
     * @return 
     */
    public List<ItemStack> getKitArmour() {
        return Collections.unmodifiableList(this.armourContents);
    }

    /**
     * Returns an unmodifiable List of items in the new kit *
     * @return 
     */
    public List<ItemStack> getKitContents() {
        return new ArrayList<>(this.kitContents.values());
    }

    public Map<Integer, ItemStack> getKitContentsWithSlots() {
        return Collections.unmodifiableMap(this.kitContents);
    }

    /**
     * Set the item contents of the new kit *
     * @param armourContents
     */
    public void setArmourContents(List<ItemStack> armourContents) {
        if (armourContents != null) this.armourContents = armourContents;
    }

    /**
     * Set the item contents of the new kit *
     * @param kitContents
     */
    public void setKitContents(List<ItemStack> kitContents) {
        if (kitContents != null) {
            this.kitContents = new HashMap<>();
            for (int i = 0; i < kitContents.size(); i++) {
                ItemStack kitContent = kitContents.get(i);
                this.kitContents.put(i, kitContent == null ? new ItemStack(Material.AIR) : kitContent);
            }
        }
    }

    /**
     * Set the item contents of the new kit *
     * @param kitContents
     */
    public void setKitContents(Map<Integer, ItemStack> kitContents) {
        if (kitContents != null) this.kitContents = kitContents;
    }

    /**
     *
     * @return
     */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean flag) {
        this.isCancelled = flag;
    }

}
