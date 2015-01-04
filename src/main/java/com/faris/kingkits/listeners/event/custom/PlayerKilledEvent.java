package com.faris.kingkits.listeners.event.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerKilledEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();

	private Player theDead = null;
	
	public PlayerKilledEvent(Player killer, Player dead) {
		super(killer);
		this.theDead = dead;
	}

	/** Returns the dead player
     * @return  **/
	public Player getDead() {
		return this.theDead;
	}

	/** Returns the killer
     * @return  **/
	public Player getKiller() {
		return this.getPlayer();
	}

        @Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}
