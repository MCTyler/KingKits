package com.faris.kingkits.listeners;

import com.faris.kingkits.KingKits;
import com.faris.kingkits.helpers.Lang;
import com.faris.kingkits.helpers.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class KingCommand implements CommandExecutor {
    private KingKits plugin = null;

    public KingCommand(KingKits pluginInstance) {
        this.plugin = pluginInstance;
    }

    /**
     * Ignore this method *
     * @param sender
     * @param cmd
     * @param label
     * @param args
     * @return 
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return this.onCommand(sender, cmd.getName(), args);
    }

    /**
     * Called when a command is typed by a player or the console *
     * @param sender
     * @param command
     * @param args
     * @return 
     */
    protected abstract boolean onCommand(CommandSender sender, String command, String[] args);

    /**
     * Returns the plugin instance *
     * @return 
     */
    protected KingKits getPlugin() {
        return this.plugin;
    }

    /**
     * Returns if a sender is not a player *
     * @param sender
     * @return 
     */
    protected boolean isConsole(CommandSender sender) {
        if (sender != null) {
            return !(sender instanceof Player);
        } else {
            return false;
        }
    }

    /**
     * Returns if a string is a boolean *
     * @param booleanString
     * @return 
     */
    protected boolean isBoolean(String booleanString) {
        return booleanString.equalsIgnoreCase("true") || booleanString.equalsIgnoreCase("false");
    }

    /**
     * Returns if a string is a double *
     * @param doubleString
     * @return 
     */
    protected boolean isDouble(String doubleString) {
        try {
            double d = Double.parseDouble(doubleString);
            return Math.floor(d) != d;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Returns if a string is numeric *
     * @param numericString
     * @return 
     */
    protected boolean isNumeric(String numericString) {
        try {
            double d = Double.parseDouble(numericString);
            return Math.floor(d) == d;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Returns a string with the real colours *
     * @param message
     * @return 
     */
    protected String r(String message) {
        return Utils.replaceChatColour(message);
    }

    /**
     * Send the "no access" message to a player/console *
     * @param sender
     */
    protected void sendNoAccess(CommandSender sender) {
        Lang.sendMessage(sender, Lang.COMMAND_GEN_NO_PERMISSION);
    }

}
