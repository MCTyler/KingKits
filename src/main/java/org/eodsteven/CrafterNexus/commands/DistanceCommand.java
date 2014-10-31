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
package org.eodsteven.CrafterNexus.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.eodsteven.CrafterNexus.CrafterNexus;
import static org.eodsteven.CrafterNexus.Translation._;
import org.eodsteven.CrafterNexus.object.GameTeam;
import org.eodsteven.CrafterNexus.object.PlayerMeta;

public class DistanceCommand implements CommandExecutor {
    private CrafterNexus plugin;

    public DistanceCommand(CrafterNexus instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,
            String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (plugin.getPhase() == 0) {
                p.sendMessage(ChatColor.GOLD + _("CRAFTERNEXUS_PREFIX") + ChatColor.RED + _("ERROR_GAME_NOTSTARTED"));
                return false;
            }

            if (PlayerMeta.getMeta(p).getTeam() == GameTeam.NONE) {
                p.sendMessage(ChatColor.GOLD + _("CRAFTERNEXUS_PREFIX") + ChatColor.RED + _("ERROR_PLAYER_NOTEAM"));
                return false;
            }

            p.sendMessage(ChatColor.GRAY + "=========[ " + ChatColor.DARK_AQUA.toString() + _("INFO_COMMAND_DISTANCE")
                + ChatColor.GRAY + " ]=========");

            for (GameTeam t : GameTeam.values()) {
                if (t != GameTeam.NONE) {
                    showTeam(p, t);
                }
            }

            p.sendMessage(ChatColor.GRAY + "============================");
        } else {
            sender.sendMessage(ChatColor.GOLD + _("CRAFTERNEXUS_PREFIX") + ChatColor.RED + _("ERROR_CONSOLE_PLAYERCOMMAND"));
        }

        return true;
    }

    private void showTeam(Player p, GameTeam t) {
        try {
            if (t.getNexus() != null && t.getNexus().getHealth() > 0)
                p.sendMessage(t.coloredName() + ChatColor.GRAY + " Nexus Distance: " + ChatColor.WHITE + ((int) p.getLocation().distance(t.getNexus().getLocation())) + ChatColor.GRAY + " Blocks");
        } catch (IllegalArgumentException ex) {

        }
    }
}
