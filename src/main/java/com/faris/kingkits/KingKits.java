package com.faris.kingkits;

import com.faris.kingkits.guis.GuiKingKits;
import com.faris.kingkits.guis.GuiKitMenu;
import com.faris.kingkits.guis.GuiPreviewKit;
import com.faris.kingkits.helpers.Lang;
import com.faris.kingkits.helpers.UUIDFetcher;
import com.faris.kingkits.helpers.Utils;
import com.faris.kingkits.hooks.PvPKits;
import com.faris.kingkits.listeners.commands.*;
import com.faris.kingkits.listeners.event.PlayerListener;
import com.faris.kingkits.sql.KingKitsSQL;
import com.faris.kingkits.updater.BukkitUpdater;
import com.faris.kingkits.updater.SpigotUpdater;
import com.faris.kingkits.values.CommandValues;
import com.faris.kingkits.values.ConfigValues;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.mcstats.Metrics;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

@SuppressWarnings({"unused", "deprecation"})
public class KingKits extends JavaPlugin {
    private static KingKits pluginInstance = null;

    // Class Variables
    private PvPKits pvpKits = null;
    private KingKitsSQL kkSql = null;
    public Permissions permissions = new Permissions();
    public CommandValues cmdValues = new CommandValues();
    public ConfigValues configValues = new ConfigValues();
    public Vault vault = new Vault(this);

    // Plugin Variables
    public Map<String, String> usingKits = new HashMap<>();
    public Map<String, String> playerKits = new HashMap<>();
    public Map<UUID, Object> playerScores = new HashMap<>();
    public Map<Player, Player> compassTargets = new HashMap<>();
    public Map<String, Long> playerKillstreaks = new HashMap<>();

    // Listeners
    private PlayerListener pListener = null;
    private KingKitsCommand cmdKingKits = null;
    private KitCommand cmdKitL = null;
    private CreateKitCommand cmdKitC = null;
    private DeleteKitCommand cmdKitD = null;
    private RenameKitCommand cmdKitR = null;
    private RefillCommand cmdRefill = null;

    public Map<String, Kit> kitList = new HashMap<>();
    private int cooldownTaskID = -1;

    @Override
    public void onEnable() {
        pluginInstance = this;

        this.pvpKits = new PvPKits();
        // Clear all lists
        this.usingKits.clear();
        this.playerKits.clear();
        this.playerScores.clear();
        this.compassTargets.clear();
        this.playerKillstreaks.clear();

        // Initialise variables
        ConfigurationSerialization.registerClass(Kit.class);
        try {
            this.loadConfiguration();
        } catch (Exception ex) {
        }
        try {
            Lang.init(this);
        } catch (Exception ex) {
        }

        this.pListener = new PlayerListener(this);
        // Update commands
        this.cmdKingKits = new KingKitsCommand(this);
        this.cmdKitL = new KitCommand(this);
        this.cmdKitC = new CreateKitCommand(this);
        this.cmdKitD = new DeleteKitCommand(this);
        this.cmdRefill = new RefillCommand(this);
        this.cmdKitR = new RenameKitCommand(this);

        // Register commands
        this.getCommand("kingkits").setExecutor(this.cmdKingKits);
        this.getCommand("kingkits").setAliases(Arrays.asList("kk"));
        this.getCommand("pvpkit").setExecutor(this.cmdKitL);
        this.getCommand("createkit").setExecutor(this.cmdKitC);
        this.getCommand("deletekit").setExecutor(this.cmdKitD);
        this.getCommand("refill").setExecutor(this.cmdRefill);
        this.getCommand("refill").setAliases(Arrays.asList("soup"));
        this.getCommand("renamekit").setExecutor(this.cmdKitR);

        // Register permissions
        for (Permission registeredPerm : this.permissions.permissionsList)
            this.getServer().getPluginManager().addPermission(registeredPerm);
        this.getServer().getPluginManager().registerEvents(this.pListener, this);

        // Check for updates
        if (this.configValues.checkForUpdates) {
            if (this.getServer().getVersion().contains("Spigot")) {
                SpigotUpdater updater = new SpigotUpdater(this, 2209, false);
                if (updater.getResult() == SpigotUpdater.UpdateResult.UPDATE_AVAILABLE) {
                    String title = "============================================";
                    String titleSpace = "                                            ";
                    this.getLogger().info(title);
                    try {
                        this.getLogger().log(Level.INFO, "{0}KingKits{1}", new Object[]{titleSpace.substring(0, titleSpace.length() / 2 - "KingKits".length() + 3), titleSpace.substring(0, titleSpace.length() / 2 - "KingKits".length())});
                    } catch (Exception ex) {
                        this.getServer().getConsoleSender().sendMessage("KingKits");
                    }
                    this.getLogger().info(title);
                    this.getLogger().log(Level.INFO, "A new version is available: KingKits v{0}", updater.getVersion());
                    this.getLogger().log(Level.INFO, "Your current version: KingKits v{0}", this.getDescription().getVersion());
                    this.getLogger().log(Level.INFO, "{0}Download it from: http://www.spigotmc.org/threads/kingkits.37947", (this.configValues.automaticUpdates ? "Automatic updates do not work for Spigot. " : ""));
                }
            } else {
                BukkitUpdater updater = new BukkitUpdater(this, 56371, this.getFile(), BukkitUpdater.UpdateType.NO_DOWNLOAD, false);
                if (updater.getResult() == BukkitUpdater.UpdateResult.UPDATE_AVAILABLE) {
                    String title = "============================================";
                    String titleSpace = "                                            ";
                    this.getLogger().info(title);
                    try {
                        this.getLogger().log(Level.INFO, "{0}KingKits{1}", new Object[]{titleSpace.substring(0, titleSpace.length() / 2 - "KingKits".length() + 3), titleSpace.substring(0, titleSpace.length() / 2 - "KingKits".length())});
                    } catch (Exception ex) {
                        this.getServer().getConsoleSender().sendMessage("KingKits");
                    }
                    this.getLogger().info(title);
                    this.getLogger().log(Level.INFO, "A new version is available: {0}", updater.getLatestName());
                    this.getLogger().log(Level.INFO, "Your current version: KingKits v{0}", this.getDescription().getVersion());
                    if (this.configValues.automaticUpdates) {
                        this.getLogger().log(Level.INFO, "Downloading {0}...", updater.getLatestName());
                        updater = new BukkitUpdater(this, 56371, this.getFile(), BukkitUpdater.UpdateType.NO_VERSION_CHECK, false);
                        BukkitUpdater.UpdateResult updateResult = updater.getResult();
                        if (updateResult == BukkitUpdater.UpdateResult.FAIL_APIKEY)
                            this.getLogger().warning("Download failed: Improperly configured the server's API key in the configuration");
                        else if (updateResult == BukkitUpdater.UpdateResult.FAIL_DBO)
                            this.getLogger().warning("Download failed: Could not connect to BukkitDev.");
                        else if (updateResult == BukkitUpdater.UpdateResult.FAIL_DOWNLOAD)
                            this.getLogger().warning("Download failed: Could not download the file.");
                        else if (updateResult == BukkitUpdater.UpdateResult.FAIL_NOVERSION)
                            this.getLogger().warning("Download failed: The latest version has an incorrect title.");
                        else this.getLogger().info("The latest version of KingKits has been downloaded.");
                    } else {
                        this.getLogger().log(Level.INFO, "Download it from: {0}", updater.getLatestFileLink());
                    }
                }
            }
        }

        try {
            new Metrics(this).start();
        } catch (Exception ex) {
            this.getLogger().log(Level.WARNING, "Could not start metrics due to a(n) {0} error.", ex.getClass().getSimpleName());
        }
    }

    @Override
    public void onDisable() {
        if (this.cooldownTaskID != -1) this.getServer().getScheduler().cancelTask(this.cooldownTaskID);

        if (this.kkSql != null) {
            this.kkSql.onDisable();
            this.kkSql = null;
        }

        // Clear inventories on reload
        if (this.configValues.clearInvsOnReload) {
            for (Player target : Utils.getOnlinePlayers()) {
                if (PvPKits.hasKit(target.getName(), false)) {
                    target.getInventory().clear();
                    target.getInventory().setArmorContents(null);
                    for (PotionEffect potionEffect : target.getActivePotionEffects())
                        target.removePotionEffect(potionEffect.getType());
                }
            }
        }

        try {
            for (Entry<String, GuiKitMenu> playerEntry : GuiKingKits.guiKitMenuMap.entrySet()) {
                playerEntry.getValue().closeMenu(true, this.getServer().getPlayerExact(playerEntry.getKey()) != null);
            }
            GuiKingKits.guiKitMenuMap.clear();
        } catch (Exception ex) {
        }

        try {
            for (Entry<String, GuiPreviewKit> playerEntry : GuiKingKits.guiPreviewKitMap.entrySet()) {
                playerEntry.getValue().closeMenu(true, this.getServer().getPlayerExact(playerEntry.getKey()) != null);
            }
            GuiKingKits.guiPreviewKitMap.clear();
        } catch (Exception ex) {
        }

        ConfigurationSerialization.unregisterClass(Kit.class);

        // Clear all lists
        this.usingKits.clear();
        this.playerKits.clear();
        this.playerScores.clear();
        this.compassTargets.clear();
        this.playerKillstreaks.clear();
        this.kitList.clear();

        // Unregister all permissions
        for (Permission registeredPerm : this.permissions.permissionsList)
            this.getServer().getPluginManager().removePermission(registeredPerm);

        this.permissions = null;
        pluginInstance = null;
    }

    // Load Configurations
    public void loadConfiguration() throws Exception {
        try {
            if (this.cooldownTaskID != -1 && this.getServer().getScheduler().isQueued(this.cooldownTaskID))
                this.getServer().getScheduler().cancelTask(this.cooldownTaskID);
            if (KingKitsSQL.isInitialised() || KingKitsSQL.isOpen()) KingKitsSQL.closeConnection();
            this.getConfig().options().header("KingKits Configuration");
            this.getConfig().addDefault("Op bypass", true);
            this.getConfig().addDefault("MySQL.Enabled", false);
            this.getConfig().addDefault("MySQL.Host", "127.0.0.1");
            this.getConfig().addDefault("MySQL.Port", 3306);
            this.getConfig().addDefault("MySQL.Username", "root");
            this.getConfig().addDefault("MySQL.Password", "");
            this.getConfig().addDefault("MySQL.Database", "kingkits");
            this.getConfig().addDefault("MySQL.Table prefix", "kk_");
            this.getConfig().addDefault("PvP Worlds", Arrays.asList("All"));
            this.getConfig().addDefault("Multiple world inventories plugin", "Multiverse-Inventories");
            this.getConfig().addDefault("Multi-inventories", this.getServer().getPluginManager().isPluginEnabled(this.getConfig().getString("Multiple world inventories plugin")));
            this.getConfig().addDefault("Enable kits command", true);
            this.getConfig().addDefault("Enable create kits command", true);
            this.getConfig().addDefault("Enable delete kits command", true);
            this.getConfig().addDefault("Enable rename kits command", true);
            this.getConfig().addDefault("Enable refill command", true);
            this.getConfig().addDefault("Kit sign", "[Kit]");
            this.getConfig().addDefault("Kit list sign", "[KList]");
            this.getConfig().addDefault("Kit sign valid", "[&1Kit&0]");
            this.getConfig().addDefault("Kit sign invalid", "[&cKit&0]");
            this.getConfig().addDefault("Kit list sign valid", "[&1KList&0]");
            this.getConfig().addDefault("Kit cooldown enabled", false);
            if (this.getConfig().contains("Kit cooldown.Enabled")) this.getConfig().set("Kit cooldown.Enabled", null);
            if (this.getConfig().contains("Kit cooldown.Time")) this.getConfig().set("Kit cooldown.Time", null);
            this.getConfig().addDefault("List kits on join", true);
            this.getConfig().addDefault("Use permissions on join", true);
            this.getConfig().addDefault("Use permissions for kit list", true);
            this.getConfig().addDefault("Kit list mode", "Text");
            this.getConfig().addDefault("Sort kits alphabetically", true);
            this.getConfig().addDefault("Remove items on leave", true);
            this.getConfig().addDefault("Drop items on death", false);
            this.getConfig().addDefault("Drop items", false);
            this.getConfig().addDefault("Drop animations", Arrays.asList(Material.BOWL.getId()));
            this.getConfig().addDefault("Allow picking up items", true);
            this.getConfig().addDefault("Clear inventories on reload", true);
            this.getConfig().addDefault("One kit per life", false);
            this.getConfig().addDefault("Check for updates", true);
            this.getConfig().addDefault("Automatically update", false);
            this.getConfig().addDefault("Enable score", false);
            this.getConfig().addDefault("Score chat prefix", "&6[&a<score>&6]");
            this.getConfig().addDefault("Score per kill", 2);
            this.getConfig().addDefault("Max score", Integer.MAX_VALUE);
            this.getConfig().addDefault("Remove potion effects on leave", true);
            this.getConfig().addDefault("Set compass target to nearest player", true);
            this.getConfig().addDefault("Quick soup", true);
            this.getConfig().addDefault("Requires kit to use refill", true);
            this.getConfig().addDefault("Command to run when changing kits", "");
            this.getConfig().addDefault("Disable block placing and breaking", false);
            this.getConfig().addDefault("Disable death messages", false);
            this.getConfig().addDefault("Lock hunger level", true);
            this.getConfig().addDefault("Custom message", "&6You have chosen the kit &c<kit>&6.");
            this.getConfig().addDefault("Disable gamemode while using a kit", false);
            this.getConfig().addDefault("Enable killstreaks", false);
            this.getConfig().addDefault("Disable item breaking", true);
            this.getConfig().addDefault("Kit menu on join", false);
            this.getConfig().addDefault("Clear items on kit creation", true);
            this.getConfig().addDefault("Kit particle effects", false);
            this.getConfig().addDefault("Show kit preview", false);
            if (this.getConfig().contains("Scoreboards")) this.getConfig().set("Scoreboards", null);
            if (this.getConfig().contains("Scoreboard title")) this.getConfig().set("Scoreboard title", null);
            this.getConfig().options().copyDefaults(true);
            this.getConfig().options().copyHeader(true);
            this.saveConfig();

            this.configValues.opBypass = this.getConfig().getBoolean("Op bypass");
            this.configValues.pvpWorlds = this.getConfig().getStringList("PvP Worlds");
            this.configValues.multiInvsPlugin = this.getConfig().getString("Multiple world inventories plugin");
            this.configValues.multiInvs = this.getConfig().getBoolean("Multi-inventories");
            if (!this.configValues.multiInvs && this.getServer().getPluginManager().isPluginEnabled(this.getConfig().getString("Multiple world inventories plugin"))) {
                this.configValues.multiInvs = true;
                this.getConfig().set("Multi-inventories", true);
                this.saveConfig();
            }
            this.cmdValues.pvpKits = this.getConfig().getBoolean("Enable kits command");
            this.cmdValues.createKits = this.getConfig().getBoolean("Enable create kits command");
            this.cmdValues.deleteKits = this.getConfig().getBoolean("Enable delete kits command");
            this.cmdValues.renameKits = this.getConfig().getBoolean("Enable rename kits command");
            this.cmdValues.refillKits = this.getConfig().getBoolean("Enable refill command");
            this.configValues.strKitSign = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("Kit sign"));
            this.configValues.strKitListSign = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("Kit list sign"));
            this.configValues.strKitSignValid = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("Kit sign valid"));
            this.configValues.strKitSignInvalid = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("Kit sign invalid"));
            this.configValues.strKitListSignValid = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("Kit list sign valid"));
            this.configValues.kitCooldown = this.getConfig().getBoolean("Kit cooldown enabled");
            this.configValues.listKitsOnJoin = this.getConfig().getBoolean("List kits on join");
            this.configValues.kitListMode = this.getConfig().getString("Kit list mode");
            this.configValues.sortAlphabetically = this.getConfig().getBoolean("Sort kits alphabetically");
            this.configValues.kitListPermissionsJoin = this.getConfig().getBoolean("Use permissions on join");
            this.configValues.kitListPermissions = this.getConfig().getBoolean("Use permissions for kit list");
            this.configValues.removeItemsOnLeave = this.getConfig().getBoolean("Remove items on leave");
            this.configValues.dropItemsOnDeath = this.getConfig().getBoolean("Drop items on death");
            this.configValues.dropItems = this.getConfig().getBoolean("Drop items");
            this.configValues.dropAnimations = this.getConfig().getIntegerList("Drop animations");
            this.configValues.allowPickingUpItems = this.getConfig().getBoolean("Allow picking up items");
            this.configValues.clearInvsOnReload = this.getConfig().getBoolean("Clear inventories on reload");
            this.configValues.oneKitPerLife = this.getConfig().getBoolean("One kit per life");
            this.configValues.checkForUpdates = this.getConfig().getBoolean("Check for updates");
            this.configValues.automaticUpdates = this.getConfig().getBoolean("Automatically update");
            this.configValues.removePotionEffectsOnLeave = this.getConfig().getBoolean("Remove potion effects on leave");
            this.configValues.rightClickCompass = this.getConfig().getBoolean("Set compass target to nearest player");
            this.configValues.quickSoup = this.getConfig().getBoolean("Quick soup");
            this.configValues.quickSoupKitOnly = this.getConfig().getBoolean("Requires kit to use refill");
            this.configValues.banBlockBreakingAndPlacing = this.getConfig().getBoolean("Disable block placing and breaking");
            this.configValues.disableDeathMessages = this.getConfig().getBoolean("Disable death messages");
            this.configValues.lockHunger = this.getConfig().getBoolean("Lock hunger level");
            this.configValues.customMessages = this.getConfig().getString("Custom message");
            this.configValues.commandToRun = this.getConfig().getString("Command to run when changing kits");
            this.configValues.disableGamemode = this.getConfig().getBoolean("Disable gamemode while using a kit");
            this.configValues.killstreaks = this.getConfig().getBoolean("Enable killstreaks");
            this.configValues.disableItemBreaking = this.getConfig().getBoolean("Disable item breaking");
            this.configValues.kitMenuOnJoin = this.getConfig().getBoolean("Kit menu on join");
            this.configValues.removeItemsOnCreateKit = this.getConfig().getBoolean("Clear items on kit creation");
            this.configValues.kitParticleEffects = this.getConfig().getBoolean("Kit particle effects");
            this.configValues.showKitPreview = this.getConfig().getBoolean("Show kit preview");

            this.configValues.scores = this.getConfig().getBoolean("Enable score");
            this.configValues.scoreIncrement = this.getConfig().getInt("Score per kill");
            String scoreChatPrefix = this.getConfig().getString("Score chat prefix");
            if (!scoreChatPrefix.contains("<score>")) {
                this.getConfig().set("Score chat prefix", "&6[&a<score>&6]");
                this.saveConfig();
            }
            this.configValues.scoreFormat = this.getConfig().getString("Score chat prefix");
            this.configValues.maxScore = this.getConfig().getInt("Max score");

            this.loadMySQL();
            this.loadPvPKits();
            this.loadScores();
            this.loadEconomy();
            this.loadKillstreaks();

            for (Player onlinePlayer : Utils.getOnlinePlayers()) {
                Scoreboard playerScoreboard = onlinePlayer.getScoreboard();
                if (playerScoreboard != null) {
                    if (playerScoreboard.getObjective("KingKits") != null) {
                        playerScoreboard.resetScores(Bukkit.getOfflinePlayer(ChatColor.GREEN + "Score:"));
                        playerScoreboard.resetScores(Bukkit.getOfflinePlayer(ChatColor.GREEN + "Killstreak:"));
                        playerScoreboard.clearSlot(DisplaySlot.SIDEBAR);
                        if (playerScoreboard.getObjectives().isEmpty())
                            onlinePlayer.setScoreboard(this.getServer().getScoreboardManager().getNewScoreboard());
                        else onlinePlayer.setScoreboard(playerScoreboard);
                    }
                }
            }

            if (this.configValues.kitCooldown) {
                this.cooldownTaskID = this.getServer().getScheduler().runTaskTimer(this, new Runnable() {
                    @Override
                    public void run() {
                        boolean hasBeenModified = false;
                        for (Map.Entry<String, Object> configEntrySet : getCooldownConfig().getValues(false).entrySet()) {
                            Map<String, Object> playerMap = null;
                            if (configEntrySet.getValue() instanceof ConfigurationSection)
                                playerMap = ((ConfigurationSection) configEntrySet.getValue()).getValues(false);
                            else if (configEntrySet.getValue() instanceof Map)
                                playerMap = (Map<String, Object>) configEntrySet.getValue();
                            if (playerMap != null) {
                                for (Map.Entry<String, Object> entrySet : playerMap.entrySet()) {
                                    String strValue = entrySet.getValue().toString();
                                    if (Utils.isLong(strValue)) {
                                        Kit targetKit = kitList.get(entrySet.getKey());
                                        if (targetKit != null) {
                                            long kitCooldown = targetKit.getCooldown();
                                            long playerCooldown = Long.parseLong(strValue);
                                            if (System.currentTimeMillis() - playerCooldown >= kitCooldown * 1000) {
                                                getCooldownConfig().set(configEntrySet.getKey() + "." + entrySet.getKey(), null);
                                                if (playerMap.size() == 1)
                                                    getCooldownConfig().set(configEntrySet.getKey(), null);
                                                hasBeenModified = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (hasBeenModified) saveCooldownConfig();
                    }
                }, 1200L, 1200L).getTaskId();
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
            throw ex;
        }
    }

    private void loadMySQL() {
        if (KingKitsSQL.isOpen()) KingKitsSQL.closeConnection();
        KingKitsSQL.sqlEnabled = this.getConfig().getBoolean("MySQL.Enabled", false);
        this.kkSql = new KingKitsSQL(this.getConfig().getString("MySQL.Host", "127.0.0.1"), this.getConfig().getInt("MySQL.Port", 3306), this.getConfig().getString("MySQL.Username", "root"), this.getConfig().getString("MySQL.Password", ""), this.getConfig().getString("MySQL.Database", "kingkits"), this.getConfig().getString("MySQL.Table prefix", "kk_"));
    }

    private void loadPvPKits() {
        try {
            this.getKitsConfig().options().header("KingKits Kits Configuration.");
            this.convertOldConfig();
            this.convertSecondOldConfig();

            this.getKitsConfig().addDefault("First run", true);
            if (this.getKitsConfig().getBoolean("First run")) {
                Kit defaultKit = new Kit("Default").setRealName("Default").setCommands(Arrays.asList("feed <player>", "tell <player> &6You have been fed for using the default kit."));
                List<ItemStack> defaultKitItems = new ArrayList<>(), defaultKitArmour = new ArrayList<>();

                ItemStack defaultSword = new ItemStack(Material.IRON_SWORD);
                defaultSword.addEnchantment(Enchantment.DURABILITY, 3);
                defaultKitItems.add(Utils.ItemUtils.setLores(Utils.ItemUtils.setName(defaultSword, "Default Kit Sword"), Arrays.asList("&6Iron Slayer")));
                defaultKitItems.add(new ItemStack(Material.GOLDEN_APPLE, 2));
                defaultKitArmour.add(new ItemStack(Material.IRON_HELMET));
                defaultKitArmour.add(Utils.ItemUtils.setDye(new ItemStack(Material.LEATHER_CHESTPLATE), 10040115));
                defaultKitArmour.add(new ItemStack(Material.IRON_LEGGINGS));
                defaultKitArmour.add(new ItemStack(Material.IRON_BOOTS));
                defaultKitItems.add(new ItemStack(Material.POTION, 1, (short) 8201));

                defaultKit.setItems(defaultKitItems);
                defaultKit.setArmour(defaultKitArmour);
                defaultKit.setPotionEffects(Arrays.asList(new PotionEffect(PotionEffectType.SPEED, 200, 1)));

                this.getKitsConfig().addDefault("Default", defaultKit.serialize());
                this.getKitsConfig().set("First run", false);
            }
            this.getKitsConfig().options().copyDefaults(true);
            this.getKitsConfig().options().copyHeader(true);
            this.saveKitsConfig();

            this.kitList.clear();
            List<String> kitList = this.getConfigKitList();
            for (String kitName : kitList) {
                try {
                    Object objKitConfigSection = this.getKitsConfig().get(kitName);
                    Kit kit = null;
                    if (objKitConfigSection instanceof ConfigurationSection)
                        kit = Kit.deserialize(((ConfigurationSection) objKitConfigSection).getValues(false));
                    else if (objKitConfigSection instanceof Map)
                        kit = Kit.deserialize((Map<String, Object>) objKitConfigSection);
                    if (kit != null) this.kitList.put(kitName, kit.setRealName(kitName));
                    else
                        this.getLogger().log(Level.WARNING, "Could not register the kit ''{0}'' it has been invalidly defined in the configuration.", kitName);
                } catch (NullPointerException | ClassCastException ex) {
                    this.getLogger().log(Level.WARNING, "Could not register the kit ''{0}'' due to a(n) {1} error:", new Object[]{kitName, ex.getClass().getSimpleName()});
                }
            }

            this.setupPermissions(true);
        } catch (Exception ex) {
        }
    }

    private void loadScores() {
        try {
            this.getScoresConfig().options().header("KingKits Score Configuration");
            Map<String, Integer> scores = new HashMap<>();
            scores.put("Player1", 2);
            if (!this.getScoresConfig().contains("Scores")) this.getScoresConfig().createSection("Scores", scores);
            this.getScoresConfig().options().copyDefaults(true);
            this.getScoresConfig().options().copyHeader(true);
            this.saveScoresConfig();

            Map<String, Object> scoresMap = this.getScoresConfig().getConfigurationSection("Scores").getValues(true);
            List<String> unconvertedScores = new ArrayList<>();
            for (Entry<String, Object> scoreEntry : scoresMap.entrySet()) {
                if (!Utils.isUUID(scoreEntry.getKey())) unconvertedScores.add(scoreEntry.getKey());
            }
            boolean hasConverted = false;
            try {
                Map<String, UUID> uuidList = new UUIDFetcher(unconvertedScores).call();
                for (Entry<String, UUID> uuidEntry : uuidList.entrySet()) {
                    scoresMap.put(uuidEntry.getValue().toString(), scoresMap.get(uuidEntry.getKey()));
                    this.getScoresConfig().set("Scores." + uuidEntry.getValue().toString(), scoresMap.get(uuidEntry.getKey()));
                }
                hasConverted = true;
            } catch (Exception ex) {
                hasConverted = false;
            }
            for (String unconvertedPlayer : unconvertedScores) {
                scoresMap.remove(unconvertedPlayer);
                if (hasConverted) {
                    this.getScoresConfig().set("Scores." + unconvertedPlayer, null);
                }
            }
            this.saveScoresConfig();
            this.playerScores = new HashMap<>();
            for (Entry<String, Object> mapEntry : scoresMap.entrySet()) {
                if (Utils.isUUID(mapEntry.getKey()))
                    this.playerScores.put(UUID.fromString(mapEntry.getKey()), mapEntry.getValue());
            }
        } catch (Exception ex) {
        }
    }

    private void loadEconomy() {
        this.getEconomyConfig().options().header("KingKits Economy Configuration");
        this.getEconomyConfig().addDefault("Use economy", false);
        this.getEconomyConfig().addDefault("Enable cost per kit", false);
        this.getEconomyConfig().addDefault("Enable cost per refill", false);
        this.getEconomyConfig().addDefault("Cost per refill", 2.50);
        this.getEconomyConfig().addDefault("Currency", "dollars");
        this.getEconomyConfig().addDefault("Message", "&a<money> <currency> was taken from your balance.");
        this.getEconomyConfig().addDefault("Enable money per kill", false);
        this.getEconomyConfig().addDefault("Money per kill", 5.00);
        this.getEconomyConfig().addDefault("Money per kill message", "&aYou received <money> <currency> for killing <target>.");
        this.getEconomyConfig().addDefault("Enable money per death", false);
        this.getEconomyConfig().addDefault("Money per death", 5.00);
        this.getEconomyConfig().addDefault("Money per death message", "&aYou lost <money> <currency> for being killed by <killer>.");
        if (this.getEconomyConfig().contains("Cost per kit")) this.getEconomyConfig().set("Cost per kit", null);
        this.getEconomyConfig().options().copyDefaults(true);
        this.getEconomyConfig().options().copyHeader(true);
        this.saveEconomyConfig();

        this.configValues.vaultValues.useEconomy = this.getEconomyConfig().getBoolean("Use economy");

        boolean useCostPerKit = this.getEconomyConfig().getBoolean("Enable cost per kit");
        if (useCostPerKit && !this.configValues.vaultValues.useEconomy) {
            this.getEconomyConfig().set("Enable cost per kit", false);
            this.saveEconomyConfig();
            this.reloadEconomyConfig();
        }
        this.configValues.vaultValues.useCostPerKit = this.getEconomyConfig().getBoolean("Enable cost per kit");

        boolean useCostPerRefill = this.getEconomyConfig().getBoolean("Enable cost per refill");
        if (useCostPerRefill && !this.configValues.vaultValues.useEconomy) {
            this.getEconomyConfig().set("Enable cost per refill", false);
            this.saveEconomyConfig();
            this.reloadEconomyConfig();
        }
        this.configValues.vaultValues.useCostPerRefill = this.getEconomyConfig().getBoolean("Enable cost per refill");

        this.configValues.vaultValues.costPerRefill = this.getEconomyConfig().getDouble("Cost per refill");

        boolean useMoneyPerKill = this.getEconomyConfig().getBoolean("Enable money per kill");
        if (useMoneyPerKill && !this.configValues.vaultValues.useEconomy) {
            this.getEconomyConfig().set("Enable money per kill", false);
            this.saveEconomyConfig();
            this.reloadEconomyConfig();
        }
        this.configValues.vaultValues.useMoneyPerKill = this.getEconomyConfig().getBoolean("Enable money per kill");
        this.configValues.vaultValues.moneyPerKill = this.getEconomyConfig().getDouble("Money per kill");

        boolean useMoneyPerDeath = this.getEconomyConfig().getBoolean("Enable money per death");
        if (useMoneyPerDeath && !this.configValues.vaultValues.useEconomy) {
            this.getEconomyConfig().set("Enable money per death", false);
            this.saveEconomyConfig();
            this.reloadEconomyConfig();
        }
        this.configValues.vaultValues.useMoneyPerDeath = this.getEconomyConfig().getBoolean("Enable money per death");
        this.configValues.vaultValues.moneyPerDeath = this.getEconomyConfig().getDouble("Money per death");
    }

    private void loadKillstreaks() {
        this.getKillstreaksConfig().options().header("KingKits Killstreak Configuration");
        this.getKillstreaksConfig().addDefault("First run", true);
        if (this.getKillstreaksConfig().getBoolean("First run")) {
            this.getKillstreaksConfig().set("Killstreak 9001", Arrays.asList("broadcast &c<player>&a's killstreak is over 9000!", "msg <player> Well done!"));
            this.getKillstreaksConfig().set("First run", false);
        }
        this.getKillstreaksConfig().options().copyDefaults(true);
        this.getKillstreaksConfig().options().copyHeader(true);
        this.saveKillstreaksConfig();
    }

    public boolean checkConfig() {
        String scoreChatPrefix = this.getConfig().getString("Score chat prefix");
        if (!scoreChatPrefix.contains("<score>")) {
            this.getConfig().set("Score chat prefix", "&6[&a<score>&6]");
            this.saveConfig();
            return false;
        }
        return true;
    }

    private void setupPermissions(boolean unregisterFirst) {
        if (unregisterFirst) {
            try {
                List<String> kitNames = new ArrayList<>(this.getKitsConfig().getKeys(false));
                for (String kit : kitNames) {
                    if (kit.split(" ").length > 1) kit = kit.split(" ")[0];
                    try {
                        this.getServer().getPluginManager().removePermission(new Permission("kingkits.kits." + kit.toLowerCase()));
                    } catch (Exception ex) {
                    }
                }
            } catch (Exception ex) {
            }
        }
        try {
            List<String> kitNames = new ArrayList<>(this.getKitsConfig().getKeys(false));
            for (String kit : kitNames) {
                if (kit.split(" ").length > 1) kit = kit.split(" ")[0];
                try {
                    this.getServer().getPluginManager().addPermission(new Permission("kingkits.kits." + kit.toLowerCase()));
                } catch (Exception ex) {
                    this.getLogger().log(Level.INFO,"Couldn''t register the permission node: " + "kingkits.kits.{0}", kit.toLowerCase());
                    this.getLogger().info("This error probably occurred because it's already registered.");
                }
            }
        } catch (Exception ex) {
        }
    }

    public String getEconomyMessage(double amount) {
        this.reloadEconomyConfig();
        String message = this.getEconomyConfig().getString("Message");
        String currency = this.getEconomyConfig().getString("Currency");
        if (amount == 1) {
            if (currency.contains("s")) {
                if (currency.lastIndexOf('s') == message.length()) currency = this.replaceLast(currency, "s", "");
            }
        }
        message = message.replace("<currency>", currency);
        message = message.replace("<money>", String.valueOf(amount));
        return Utils.replaceChatColour(message);
    }

    public List<String> getConfigKitList() {
        List<String> configKeys = new ArrayList<>(this.getKitsConfig().getKeys(false));
        configKeys.remove("First run");
        return configKeys;
    }

    public long getCooldown(String playerName, String kitName) {
        if (playerName != null && kitName != null && this.kitList.containsKey(kitName) && this.getCooldownConfig().contains(playerName)) {
            Object objCooldownPlayer = this.getCooldownConfig().get(playerName);
            Map<String, Object> playerKitCooldowns = objCooldownPlayer instanceof ConfigurationSection ? ((ConfigurationSection) objCooldownPlayer).getValues(false) : (objCooldownPlayer instanceof Map ? (Map) objCooldownPlayer : new HashMap<String, Object>());
            if (playerKitCooldowns.containsKey(kitName)) {
                String strPlayerCooldown = playerKitCooldowns.get(kitName) != null ? playerKitCooldowns.get(kitName).toString() : null;
                if (!Utils.isLong(strPlayerCooldown)) {
                    this.getCooldownConfig().set(playerName + "." + kitName, null);
                    this.saveCooldownConfig();
                } else {
                    return Long.parseLong(strPlayerCooldown);
                }
            }
        }
        return System.currentTimeMillis();
    }

    public Map<String, Long> getCooldowns(String playerName) {
        Map<String, Long> kitCooldowns = new HashMap<>();
        if (playerName != null && this.getCooldownConfig().contains(playerName)) {
            Object objCooldownPlayer = this.getCooldownConfig().get(playerName);
            Map<String, Object> configKitCooldowns = objCooldownPlayer instanceof ConfigurationSection ? ((ConfigurationSection) objCooldownPlayer).getValues(false) : (objCooldownPlayer instanceof Map ? (Map) objCooldownPlayer : new HashMap<String, Object>());
            for (Entry<String, Object> kitEntry : configKitCooldowns.entrySet()) {
                String strPlayerCooldown = kitEntry.getValue().toString();
                if (!Utils.isLong(strPlayerCooldown)) {
                    this.getCooldownConfig().set(playerName + "." + strPlayerCooldown, null);
                    this.saveCooldownConfig();
                } else {
                    kitCooldowns.put(kitEntry.getKey(), Long.parseLong(strPlayerCooldown));
                }
            }
        }
        return kitCooldowns;
    }

    public List<String> getKitList() {
        return new ArrayList<>(this.kitList.keySet());
    }

    public String getMPKMessage(Player killer, double amount) {
        this.reloadEconomyConfig();
        String message = this.getEconomyConfig().getString("Money per kill message");
        String currency = this.getEconomyConfig().getString("Currency");
        if (amount == 1) {
            if (currency.contains("s")) {
                if (currency.lastIndexOf('s') == message.length() - 1) currency = this.replaceLast(currency, "s", "");
            }
        }
        message = message.replace("<currency>", currency);
        message = message.replace("<money>", String.valueOf(amount));
        message = message.replace("<target>", killer.getName());
        return Utils.replaceChatColour(message);
    }

    public String getMPDMessage(Player dead, double amount) {
        this.reloadEconomyConfig();
        String message = this.getEconomyConfig().getString("Money per death message");
        String currency = this.getEconomyConfig().getString("Currency");
        if (amount == 1) {
            if (currency.contains("s")) {
                if (currency.lastIndexOf('s') == message.length() - 1) currency = this.replaceLast(currency, "s", "");
            }
        }
        message = message.replace("<currency>", currency);
        message = message.replace("<money>", String.valueOf(amount));
        message = message.replace("<killer>", dead.getKiller().getName());
        return Utils.replaceChatColour(message);
    }

    private String replaceLast(String s, String character, String targetChar) {
        String string = s;
        if (string.contains(character)) {
            StringBuilder b = new StringBuilder(string);
            b.replace(string.lastIndexOf(character), string.lastIndexOf(character) + 1, targetChar);
            string = b.toString();
        }
        return string;
    }

    private FileConfiguration kitsConfig = null;
    private File customKitsConfig = null;

    public void reloadKitsConfig() {
        if (this.customKitsConfig == null) {
            this.customKitsConfig = new File(this.getDataFolder(), "kits.yml");
        }
        this.kitsConfig = YamlConfiguration.loadConfiguration(this.customKitsConfig);

        InputStream defConfigStream = this.getResource("kits.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            this.kitsConfig.setDefaults(defConfig);
        }
    }

    public FileConfiguration getKitsConfig() {
        if (this.kitsConfig == null) {
            this.reloadKitsConfig();
        }
        return this.kitsConfig;
    }

    public void saveKitsConfig() {
        if (this.kitsConfig == null || this.customKitsConfig == null) {
            return;
        }
        try {
            this.getKitsConfig().save(this.customKitsConfig);
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Could not save the Kits config as " + this.customKitsConfig.getName(), ex);
        }
    }

    private FileConfiguration scoresConfig = null;
    private File customScoresConfig = null;

    public void reloadScoresConfig() {
        if (this.customScoresConfig == null) {
            this.customScoresConfig = new File(this.getDataFolder(), "scores.yml");
        }
        this.scoresConfig = YamlConfiguration.loadConfiguration(this.customScoresConfig);

        InputStream defConfigStream = this.getResource("scores.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            this.scoresConfig.setDefaults(defConfig);
        }
    }

    public FileConfiguration getScoresConfig() {
        if (this.scoresConfig == null) {
            this.reloadScoresConfig();
        }
        return this.scoresConfig;
    }

    public void saveScoresConfig() {
        if (this.scoresConfig == null || this.customScoresConfig == null) {
            return;
        }
        try {
            this.getScoresConfig().save(this.customScoresConfig);
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Could not save the Scores config as " + this.customScoresConfig.getName(), ex);
        }
    }

    private FileConfiguration economyConfig = null;
    private File customEconomyConfig = null;

    public void reloadEconomyConfig() {
        if (this.customEconomyConfig == null) {
            this.customEconomyConfig = new File(this.getDataFolder(), "economy.yml");
        }
        this.economyConfig = YamlConfiguration.loadConfiguration(this.customEconomyConfig);

        InputStream defConfigStream = this.getResource("economy.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            this.economyConfig.setDefaults(defConfig);
        }
    }

    public FileConfiguration getEconomyConfig() {
        if (this.economyConfig == null) {
            this.reloadEconomyConfig();
        }
        return this.economyConfig;
    }

    public void saveEconomyConfig() {
        if (this.economyConfig == null || this.customEconomyConfig == null) {
            return;
        }
        try {
            this.getEconomyConfig().save(this.customEconomyConfig);
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Could not save the Economy config as " + this.customEconomyConfig.getName(), ex);
        }
    }

    private FileConfiguration killstreaksConfig = null;
    private File customKillstreaksConfig = null;

    public void reloadKillstreaksConfig() {
        if (this.customKillstreaksConfig == null) {
            this.customKillstreaksConfig = new File(this.getDataFolder(), "killstreaks.yml");
        }
        this.killstreaksConfig = YamlConfiguration.loadConfiguration(this.customKillstreaksConfig);

        InputStream defConfigStream = this.getResource("killstreaks.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            this.killstreaksConfig.setDefaults(defConfig);
        }
    }

    public FileConfiguration getKillstreaksConfig() {
        if (this.killstreaksConfig == null) {
            this.reloadKillstreaksConfig();
        }
        return this.killstreaksConfig;
    }

    public void saveKillstreaksConfig() {
        if (this.killstreaksConfig == null || this.customKillstreaksConfig == null) {
            return;
        }
        try {
            this.getKillstreaksConfig().save(this.customKillstreaksConfig);
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Could not save the Killstreaks config as " + this.customKillstreaksConfig.getName(), ex);
        }
    }

    private FileConfiguration cooldownConfig = null;
    private File customCooldownConfig = null;

    public void reloadCooldownConfig() {
        if (this.customCooldownConfig == null) {
            this.customCooldownConfig = new File(this.getDataFolder(), "cooldown.yml");
        }
        this.cooldownConfig = YamlConfiguration.loadConfiguration(this.customCooldownConfig);

        InputStream defConfigStream = this.getResource("cooldown.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            this.cooldownConfig.setDefaults(defConfig);
        }
    }

    public FileConfiguration getCooldownConfig() {
        if (this.cooldownConfig == null || this.customCooldownConfig == null) this.reloadCooldownConfig();
        return this.cooldownConfig;
    }

    public void saveCooldownConfig() {
        if (this.cooldownConfig == null || this.customCooldownConfig == null) {
            return;
        }
        try {
            this.cooldownConfig.save(this.customCooldownConfig);
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Could not save the cooldown config as " + this.customCooldownConfig.getName(), ex);
        }
    }

    /**
     * Reloads all the configurations *
     */
    public void reloadAllConfigs() {
        this.reloadConfig();
        this.reloadKitsConfig();

        this.reloadScoresConfig();
        this.reloadEconomyConfig();
        this.reloadKillstreaksConfig();
        this.reloadCooldownConfig();
    }

    private void convertOldConfig() {
        File kitsFolder = new File(this.getDataFolder(), "kits");
        if (kitsFolder.exists() && kitsFolder.isDirectory()) {
            File oldKitsFile = new File(kitsFolder, "config.yml");
            FileConfiguration oldKitsFileConfig = YamlConfiguration.loadConfiguration(oldKitsFile);
            File oldCPKFile = new File(kitsFolder, "costperkit.yml");
            FileConfiguration oldCPKFileConfig = YamlConfiguration.loadConfiguration(oldCPKFile);
            File oldDyesFile = new File(kitsFolder, "dyes.yml");
            FileConfiguration oldDyesFileConfig = YamlConfiguration.loadConfiguration(oldDyesFile);
            File oldEnchantmentsFile = new File(kitsFolder, "enchantments.yml");
            FileConfiguration oldEnchantmentsFileConfig = YamlConfiguration.loadConfiguration(oldEnchantmentsFile);
            File oldGUIFile = new File(kitsFolder, "guiitems.yml");
            FileConfiguration oldGUIFileConfig = YamlConfiguration.loadConfiguration(oldGUIFile);
            File oldLoresFile = new File(kitsFolder, "lores.yml");
            FileConfiguration oldLoresFileConfig = YamlConfiguration.loadConfiguration(oldLoresFile);
            File oldPotionsFile = new File(kitsFolder, "potions.yml");
            FileConfiguration oldPotionsFileConfig = YamlConfiguration.loadConfiguration(oldPotionsFile);

            List<String> kitList = oldKitsFileConfig.getStringList("Kits");
            if (kitList != null) {
                for (String kitName : kitList) {
                    if (oldKitsFileConfig.contains(kitName)) {
                        Kit kit = new Kit(kitName).setRealName(kitName);
                        List<ItemStack> kitItems = new ArrayList<>(), kitArmour = new ArrayList<>();
                        List<String> strKitItems = oldKitsFileConfig.getStringList(kitName);
                        for (String strKitItem : strKitItems) {
                            String[] kitSplit = strKitItem.contains(" ") ? strKitItem.split(" ") : null;
                            if (kitSplit != null) {
                                if (kitSplit.length >= 3) {
                                    String strID = kitSplit[0];
                                    if (Utils.isInteger(strID)) {
                                        int itemID = Integer.parseInt(strID);
                                        int itemAmount = Utils.isInteger(kitSplit[1]) ? Integer.parseInt(kitSplit[1]) : 1;
                                        short itemData = Utils.isShort(kitSplit[2]) ? Short.parseShort(kitSplit[2]) : (short) 0;
                                        try {
                                            ItemStack kitItem = new ItemStack(itemID, itemAmount, itemData);
                                            if (kitSplit.length > 3) {
                                                StringBuilder sbKitItemName = new StringBuilder();
                                                for (int kitSplitIndex = 3; kitSplitIndex < kitSplit.length; kitSplitIndex++) {
                                                    StringBuilder append = sbKitItemName.append(kitSplit[kitSplitIndex]).append(" ");
                                                }
                                                String kitItemName = sbKitItemName.toString().trim();
                                                if (!kitItemName.isEmpty()) {
                                                    if (kitItem.hasItemMeta()) {
                                                        ItemMeta kitMeta = kitItem.getItemMeta();
                                                        kitMeta.setDisplayName(Utils.replaceChatColour(kitItemName));
                                                        kitItem.setItemMeta(kitMeta);
                                                    }
                                                }
                                            }
                                            if (oldDyesFileConfig.contains(kitName + " " + kitItem.getType().getId())) {
                                                int itemDye = oldDyesFileConfig.getInt(kitName + " " + kitItem.getType().getId());
                                                if (kitItem.hasItemMeta()) {
                                                    ItemMeta kitItemMeta = kitItem.getItemMeta();
                                                    if (kitItemMeta instanceof LeatherArmorMeta) {
                                                        LeatherArmorMeta kitItemLeatherArmorMeta = (LeatherArmorMeta) kitItemMeta;
                                                        Color targetDyeColor = Color.fromRGB(itemDye);
                                                        if (targetDyeColor != null) {
                                                            kitItemLeatherArmorMeta.setColor(targetDyeColor);
                                                            kitItem.setItemMeta(kitItemLeatherArmorMeta);
                                                        }
                                                    }
                                                }
                                            }
                                            if (oldEnchantmentsFileConfig.contains(kitName + " " + kitItem.getType().getId())) {
                                                List<String> itemEnchantments = oldEnchantmentsFileConfig.getStringList(kitName + " " + kitItem.getType().getId());
                                                if (itemEnchantments != null) {
                                                    for (String itemEnchantment : itemEnchantments) {
                                                        if (itemEnchantment.contains(" ")) {
                                                            String[] enchantmentSplit = itemEnchantment.split(" ");
                                                            if (enchantmentSplit.length >= 2) {
                                                                String enchantmentName = enchantmentSplit[0];
                                                                Enchantment targetEnchantment = Utils.isInteger(enchantmentName) ? Enchantment.getById(Integer.parseInt(enchantmentName)) : Enchantment.getByName(enchantmentName);
                                                                if (targetEnchantment != null) {
                                                                    int targetLevel = Utils.isInteger(enchantmentSplit[1]) ? Integer.parseInt(enchantmentSplit[1]) : 1;
                                                                    kitItem.addUnsafeEnchantment(targetEnchantment, targetLevel);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if (oldLoresFileConfig.contains(kitName + " " + kitItem.getType().getId())) {
                                                List<String> kitItemLores = oldLoresFileConfig.getStringList(kitName + " " + kitItem.getType().getId());
                                                if (kitItemLores != null) {
                                                    if (kitItem.hasItemMeta()) {
                                                        ItemMeta kitItemMeta = kitItem.getItemMeta();
                                                        kitItemMeta.setLore(Utils.replaceChatColours(kitItemLores));
                                                        kitItem.setItemMeta(kitItemMeta);
                                                    }
                                                }
                                            }
                                            if (kitItem != null) {
                                                if (kitArmour.size() < 4) {
                                                    String strKitType = kitItem.getType().toString().toLowerCase();
                                                    if (strKitType.endsWith("helmet") || strKitType.endsWith("chestplate") || strKitType.endsWith("leggings") || strKitType.endsWith("pants") || strKitType.endsWith("boots")) {
                                                        kitArmour.add(kitItem);
                                                    } else {
                                                        kitItems.add(kitItem);
                                                    }
                                                } else {
                                                    kitItems.add(kitItem);
                                                }
                                            }
                                        } catch (Exception ex) {
                                        }
                                    }
                                }
                            }
                        }
                        kit.setItems(kitItems);
                        kit.setArmour(kitArmour);
                        if (oldCPKFileConfig.contains(kitName)) kit.setCost(oldCPKFileConfig.getDouble(kitName, 0D));
                        if (oldGUIFileConfig.contains(kitName)) {
                            int targetItemID = oldGUIFileConfig.getInt(kitName);
                            try {
                                kit.setGuiItem(new ItemStack(targetItemID));
                            } catch (Exception ex) {
                            }
                        }
                        if (oldPotionsFileConfig.contains(kitName)) {
                            List<PotionEffect> kitPotionEffects = new ArrayList<>();
                            List<String> strPotions = oldPotionsFileConfig.getStringList(kitName);
                            if (strPotions != null) {
                                for (String strPotion : strPotions) {
                                    if (strPotion.contains(" ")) {
                                        String[] potionSplit = strPotion.split(" ");
                                        if (potionSplit.length >= 3) {
                                            String potionName = potionSplit[0];
                                            PotionEffectType potionEffectType = Utils.isInteger(potionName) ? PotionEffectType.getById(Integer.parseInt(potionName)) : PotionEffectType.getByName(potionName.toUpperCase());
                                            if (potionEffectType != null) {
                                                int potionAmplifier = Utils.isInteger(potionSplit[1]) ? Integer.parseInt(potionSplit[1]) : Utils.romanNumeralToInteger(potionSplit[1]);
                                                if (potionAmplifier == 0) potionAmplifier++;
                                                else if (potionAmplifier < 0) potionAmplifier *= -1;
                                                potionAmplifier--;
                                                int potionDuration = Utils.isInteger(potionSplit[2]) ? Integer.parseInt(potionSplit[2]) : 10;
                                                try {
                                                    kitPotionEffects.add(new PotionEffect(potionEffectType, potionDuration, potionAmplifier));
                                                } catch (Exception ex) {
                                                }
                                            }
                                        }
                                    }
                                }
                                kit.setPotionEffects(kitPotionEffects);
                            }
                        }

                        this.getKitsConfig().set(kitName, kit.serialize());
                    }
                }
            }
            this.getKitsConfig().set("First run", this.getKitsConfig().getValues(false).isEmpty());
            if (!Utils.renameDirectory(kitsFolder, new File(this.getDataFolder(), "oldkits"))) {
                if (!Utils.deleteDirectory(kitsFolder, kitsFolder.listFiles().length == 0))
                    this.getLogger().log(Level.SEVERE, "Could not rename or delete the 'kits' folder in /plugins/KingKits. You must manually delete the folder and then reload KingKits to prevent configuration events.");
            }
        }
    }

    private void convertSecondOldConfig() {
        boolean hasModified = false;
        for (String kitName : this.getConfigKitList()) {
            if (this.getKitsConfig().contains(kitName + ".Items")) {
                Map<String, Object> itemsMap = Kit.getValues(this.getKitsConfig().get(kitName + ".Items"));
                if (itemsMap != null) {
                    int currentIndex = 0;
                    for (Entry<String, Object> itemsEntry : itemsMap.entrySet()) {
                        if (Utils.isInteger(itemsEntry.getKey()) ? Material.getMaterial(Integer.parseInt(itemsEntry.getKey())) != null : Material.getMaterial(itemsEntry.getKey()) != null) {
                            Map<String, Object> itemMap = Kit.getValues(itemsEntry);
                            itemMap.put("Type", itemsEntry.getKey());
                            this.getKitsConfig().set(kitName + ".Items." + itemsEntry.getKey(), null);
                            this.getKitsConfig().set(kitName + ".Items.Slot " + currentIndex, itemMap);
                            currentIndex++;
                            hasModified = true;
                        }
                    }
                }
            }
        }
        if (hasModified) this.saveKitsConfig();
    }

    public static KingKits getInstance() {
        return pluginInstance;
    }

}
