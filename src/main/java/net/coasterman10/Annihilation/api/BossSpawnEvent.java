/*******************************************************************************
 * Copyright 2014 stuntguy3000 (Luke Anderson) and coasterman10.
 *  
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 ******************************************************************************/
package org.eodsteven.CrafterNexus.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.eodsteven.CrafterNexus.object.Golem;

public class GolemSpawnEvent extends Event {

    private Golem b;
    
    public GolemSpawnEvent(Golem b) {
        this.b = b;
    }
    
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Golem getBoss() {
        return b;
    }
}
