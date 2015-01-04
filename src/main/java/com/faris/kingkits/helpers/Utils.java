package com.faris.kingkits.helpers;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.*;

public class Utils {

    public static boolean deleteDirectory(File directory, boolean ignoreErrors) {
        try {
            if (directory.exists() && directory.isDirectory()) {
                File files[] = directory.listFiles();
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file, true);
                    } else {
                        file.delete();
                    }
                }
            }
        } catch (Exception ex) {
            if (!ignoreErrors) ex.printStackTrace();
        }
        return (directory.delete());
    }

    public static int getDye(String friendlyName) {
        if (friendlyName != null) {
            if (friendlyName.equalsIgnoreCase("Aqua")) return Color.AQUA.asRGB();
            else if (friendlyName.equalsIgnoreCase("Black")) return Color.BLACK.asRGB();
            else if (friendlyName.equalsIgnoreCase("Blue")) return Color.BLUE.asRGB();
            else if (friendlyName.equalsIgnoreCase("Fuchsia")) return Color.FUCHSIA.asRGB();
            else if (friendlyName.equalsIgnoreCase("Gray") || friendlyName.equalsIgnoreCase("Grey"))
                return Color.GRAY.asRGB();
            else if (friendlyName.equalsIgnoreCase("Green")) return Color.GREEN.asRGB();
            else if (friendlyName.equalsIgnoreCase("Lime")) return Color.LIME.asRGB();
            else if (friendlyName.equalsIgnoreCase("Maroon")) return Color.MAROON.asRGB();
            else if (friendlyName.equalsIgnoreCase("Navy")) return Color.NAVY.asRGB();
            else if (friendlyName.equalsIgnoreCase("Olive")) return Color.OLIVE.asRGB();
            else if (friendlyName.equalsIgnoreCase("Orange")) return Color.ORANGE.asRGB();
            else if (friendlyName.equalsIgnoreCase("Purple")) return Color.PURPLE.asRGB();
            else if (friendlyName.equalsIgnoreCase("Red")) return Color.RED.asRGB();
            else if (friendlyName.equalsIgnoreCase("Silver")) return Color.SILVER.asRGB();
            else if (friendlyName.equalsIgnoreCase("Teal")) return Color.TEAL.asRGB();
            else if (friendlyName.equalsIgnoreCase("White")) return Color.WHITE.asRGB();
            else if (friendlyName.equalsIgnoreCase("Yellow")) return Color.YELLOW.asRGB();
        }
        return -1;
    }

    public static String getEnchantmentName(String friendlyName) {
        if (friendlyName != null) {
            if (friendlyName.equalsIgnoreCase("Sharpness") || friendlyName.equalsIgnoreCase("Sharp"))
                return Enchantment.DAMAGE_ALL.getName();
            else if (friendlyName.equalsIgnoreCase("Bane of Arthropods") || friendlyName.equalsIgnoreCase("Arthropods") || friendlyName.equalsIgnoreCase("Bane") || friendlyName.equalsIgnoreCase("Arthro"))
                return Enchantment.DAMAGE_ARTHROPODS.getName();
            else if (friendlyName.equalsIgnoreCase("Smite") || friendlyName.equalsIgnoreCase("Undead"))
                return Enchantment.DAMAGE_UNDEAD.getName();
            else if (friendlyName.equalsIgnoreCase("Power")) return Enchantment.ARROW_DAMAGE.getName();
            else if (friendlyName.equalsIgnoreCase("Flame") || friendlyName.equalsIgnoreCase("Flames"))
                return Enchantment.ARROW_FIRE.getName();
            else if (friendlyName.equalsIgnoreCase("Infinite") || friendlyName.equalsIgnoreCase("Infinity"))
                return Enchantment.ARROW_INFINITE.getName();
            else if (friendlyName.equalsIgnoreCase("Punch") || friendlyName.equalsIgnoreCase("Push"))
                return Enchantment.ARROW_KNOCKBACK.getName();
            else if (friendlyName.equalsIgnoreCase("Efficiency") || friendlyName.equalsIgnoreCase("Eff"))
                return Enchantment.DIG_SPEED.getName();
            else if (friendlyName.equalsIgnoreCase("Unbreaking") || friendlyName.equalsIgnoreCase("Durability") || friendlyName.equalsIgnoreCase("Dura"))
                return Enchantment.DURABILITY.getName();
            else if (friendlyName.equalsIgnoreCase("Fire Aspect") || friendlyName.equalsIgnoreCase("Fire"))
                return Enchantment.FIRE_ASPECT.getName();
            else if (friendlyName.equalsIgnoreCase("Knockback") || friendlyName.equalsIgnoreCase("Knock"))
                return Enchantment.KNOCKBACK.getName();
            else if (friendlyName.equalsIgnoreCase("Fortune") || friendlyName.equalsIgnoreCase("Fort"))
                return Enchantment.LOOT_BONUS_BLOCKS.getName();
            else if (friendlyName.equalsIgnoreCase("Looting") || friendlyName.equalsIgnoreCase("Loot"))
                return Enchantment.LOOT_BONUS_MOBS.getName();
            else if (friendlyName.equalsIgnoreCase("Luck")) return Enchantment.LUCK.getName();
            else if (friendlyName.equalsIgnoreCase("Lure")) return Enchantment.LURE.getName();
            else if (friendlyName.equalsIgnoreCase("Oxygen") || friendlyName.equalsIgnoreCase("Breathing") || friendlyName.equalsIgnoreCase("Respiration"))
                return Enchantment.OXYGEN.getName();
            else if (friendlyName.equalsIgnoreCase("Protection") || friendlyName.equalsIgnoreCase("Prot"))
                return Enchantment.PROTECTION_ENVIRONMENTAL.getName();
            else if (friendlyName.equalsIgnoreCase("Blast Protection") || friendlyName.equalsIgnoreCase("BlastProt"))
                return Enchantment.PROTECTION_EXPLOSIONS.getName();
            else if (friendlyName.equalsIgnoreCase("Fall Protection") || friendlyName.equalsIgnoreCase("FallProt") || friendlyName.equalsIgnoreCase("Feather") || friendlyName.equalsIgnoreCase("Feather Falling"))
                return Enchantment.PROTECTION_FALL.getName();
            else if (friendlyName.equalsIgnoreCase("Fire Protection") || friendlyName.equalsIgnoreCase("FireProt"))
                return Enchantment.PROTECTION_FIRE.getName();
            else if (friendlyName.equalsIgnoreCase("Projectile Protection") || friendlyName.equalsIgnoreCase("ProjProt"))
                return Enchantment.PROTECTION_PROJECTILE.getName();
            else if (friendlyName.equalsIgnoreCase("Silk Touch") || friendlyName.equalsIgnoreCase("SilkTouch") || friendlyName.equalsIgnoreCase("Silk"))
                return Enchantment.SILK_TOUCH.getName();
            else if (friendlyName.equalsIgnoreCase("Thorns"))
                return Enchantment.THORNS.getName();
            else if (friendlyName.equalsIgnoreCase("Water Worker") || friendlyName.equalsIgnoreCase("Aqua Affinity"))
                return Enchantment.WATER_WORKER.getName();
        }
        return friendlyName != null ? friendlyName.toUpperCase().replace(" ", "_") : "";
    }

    public static List<Player> getOnlinePlayers() {
        List<Player> onlinePlayers = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            if (world != null) onlinePlayers.addAll(world.getPlayers());
        }
        return onlinePlayers;
    }

    public static String getPotionName(String friendlyName) {
        if (friendlyName != null) {
            if (friendlyName.equalsIgnoreCase("Confusion")) return PotionEffectType.CONFUSION.getName();
            else if (friendlyName.equalsIgnoreCase("Slow") || friendlyName.equalsIgnoreCase("Slowness"))
                return PotionEffectType.SLOW.getName();
            else if (friendlyName.equalsIgnoreCase("Resistance")) return PotionEffectType.DAMAGE_RESISTANCE.getName();
            else if (friendlyName.equalsIgnoreCase("Absorption")) return PotionEffectType.ABSORPTION.getName();
            else if (friendlyName.equalsIgnoreCase("Blindness")) return PotionEffectType.BLINDNESS.getName();
            else if (friendlyName.equalsIgnoreCase("Fast") || friendlyName.equalsIgnoreCase("Haste"))
                return PotionEffectType.FAST_DIGGING.getName();
            else if (friendlyName.equalsIgnoreCase("Fire")) return PotionEffectType.FIRE_RESISTANCE.getName();
            else if (friendlyName.equalsIgnoreCase("Harm") || friendlyName.equalsIgnoreCase("Harming"))
                return PotionEffectType.HARM.getName();
            else if (friendlyName.equalsIgnoreCase("Heal") || friendlyName.equalsIgnoreCase("Healing"))
                return PotionEffectType.HEAL.getName();
            else if (friendlyName.equalsIgnoreCase("Boost")) return PotionEffectType.HEALTH_BOOST.getName();
            else if (friendlyName.equalsIgnoreCase("Hunger")) return PotionEffectType.HUNGER.getName();
            else if (friendlyName.equalsIgnoreCase("Strength")) return PotionEffectType.INCREASE_DAMAGE.getName();
            else if (friendlyName.equalsIgnoreCase("Invisibility")) return PotionEffectType.INVISIBILITY.getName();
            else if (friendlyName.equalsIgnoreCase("Jump")) return PotionEffectType.JUMP.getName();
            else if (friendlyName.equalsIgnoreCase("Night")) return PotionEffectType.NIGHT_VISION.getName();
            else if (friendlyName.equalsIgnoreCase("Poison")) return PotionEffectType.POISON.getName();
            else if (friendlyName.equalsIgnoreCase("Regen")) return PotionEffectType.REGENERATION.getName();
            else if (friendlyName.equalsIgnoreCase("Saturation")) return PotionEffectType.SATURATION.getName();
            else if (friendlyName.equalsIgnoreCase("Fatigue")) return PotionEffectType.SLOW_DIGGING.getName();
            else if (friendlyName.equalsIgnoreCase("Speed")) return PotionEffectType.SPEED.getName();
            else if (friendlyName.equalsIgnoreCase("Water")) return PotionEffectType.WATER_BREATHING.getName();
            else if (friendlyName.equalsIgnoreCase("Weakness")) return PotionEffectType.WEAKNESS.getName();
            else if (friendlyName.equalsIgnoreCase("Wither")) return PotionEffectType.WITHER.getName();
        }
        return friendlyName != null ? friendlyName.toUpperCase().replace(" ", "_") : "";
    }

    public static boolean isInteger(String aString) {
        try {
            Integer.parseInt(aString);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isLong(String aString) {
        try {
            Long.parseLong(aString);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isShort(String aString) {
        try {
            Short.parseShort(aString);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isUUID(String string) {
        try {
            UUID.fromString(string);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static int processDecimal(int decimal, int lastNumber, int lastDecimal) {
        if (lastNumber > decimal) {
            return lastDecimal - decimal;
        } else {
            return lastDecimal + decimal;
        }
    }

    public static boolean renameDirectory(File directory, File newDirectory) {
        try {
            if (directory.isDirectory()) {
                directory.renameTo(newDirectory);
            } else {
                directory.mkdir();
                directory.renameTo(newDirectory);
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static String replaceColours(String aString) {
        char[] b = aString.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == '&' && "0123456789AaBbCcDdEeFf".indexOf(b[i + 1]) > -1) {
                b[i] = ChatColor.COLOR_CHAR;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

    public static String replaceFormat(String aString) {
        char[] b = aString.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == '&' && "KkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = ChatColor.COLOR_CHAR;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

    public static String replaceBukkitColour(String aString) {
        if (aString == null) return "";
        String newString = aString;
        for (ChatColor chatColor : ChatColor.values()) {
            newString = newString.replace(chatColor.toString(), "&" + chatColor.getChar());
        }
        return newString;
    }

    public static List<String> replaceBukkitColours(List<String> someStrings) {
        List<String> stringList = new ArrayList<>();
        if (someStrings != null) {
            for (String aString : someStrings) {
                if (aString != null)
                    stringList.add(replaceBukkitColour(aString));
            }
        }
        return stringList;
    }

    public static String replaceChatColour(String aString) {
        if (aString == null) return "";
        return ChatColor.translateAlternateColorCodes('&', aString);
    }

    public static List<String> replaceChatColours(List<String> someStrings) {
        List<String> stringList = new ArrayList<>();
        if (someStrings != null) {
            for (String aString : someStrings) {
                if (aString != null)
                    stringList.add(replaceChatColour(aString));
            }
        }
        return stringList;
    }

    public static int romanNumeralToInteger(String romanNumber) {
        int romanInteger = 0;
        int lastNumber = 0;
        String romanNumeral = romanNumber.toUpperCase();
        for (int x = romanNumeral.length() - 1; x >= 0; x--) {
            char convertToDecimal = romanNumeral.charAt(x);
            switch (convertToDecimal) {
                case 'M':
                    romanInteger = processDecimal(1000, lastNumber, romanInteger);
                    lastNumber = 1000;
                    break;

                case 'D':
                    romanInteger = processDecimal(500, lastNumber, romanInteger);
                    lastNumber = 500;
                    break;

                case 'C':
                    romanInteger = processDecimal(100, lastNumber, romanInteger);
                    lastNumber = 100;
                    break;

                case 'L':
                    romanInteger = processDecimal(50, lastNumber, romanInteger);
                    lastNumber = 50;
                    break;

                case 'X':
                    romanInteger = processDecimal(10, lastNumber, romanInteger);
                    lastNumber = 10;
                    break;

                case 'V':
                    romanInteger = processDecimal(5, lastNumber, romanInteger);
                    lastNumber = 5;
                    break;

                case 'I':
                    romanInteger = processDecimal(1, lastNumber, romanInteger);
                    lastNumber = 1;
                    break;
            }
        }
        return romanInteger;
    }

    public static String stripColour(String kitName) {
        return kitName != null ? ChatColor.stripColor(kitName) : "";
    }


    public static Comparator<String> ALPHABETICAL_ORDER = new Comparator<String>() {
        @Override
        public int compare(String str1, String str2) {
            int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
            if (res == 0) {
                res = str1.compareTo(str2);
            }
            return res;
        }
    };

    public static List<String> toLowerCaseList(List<String> normalList) {
        List<String> list = new ArrayList<>();
        for (String s : normalList)
            list.add(s.toLowerCase());
        return list;
    }

    public static class ItemUtils {
        public static int getDye(ItemStack itemStack) {
            if (itemStack != null) {
                if (itemStack.hasItemMeta()) {
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    if (itemMeta instanceof LeatherArmorMeta) {
                        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemMeta;
                        return leatherArmorMeta.getColor() != null ? leatherArmorMeta.getColor().asRGB() : -1;
                    }
                }
            }
            return -1;
        }

        public static String getName(ItemStack itemStack) {
            if (itemStack != null) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta != null) {
                    if (itemMeta.hasDisplayName()) return itemMeta.getDisplayName();
                    else return "";
                }
            }
            return "";
        }

        public static List<String> getLore(ItemStack itemStack) {
            if (itemStack != null) {
                if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore())
                    return itemStack.getItemMeta().getLore();
            }
            return new ArrayList<>();
        }

        public static ItemStack setDye(ItemStack itemStack, int itemDye) {
            if (itemStack != null && itemDye > 0) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta != null && itemMeta instanceof LeatherArmorMeta) {
                    LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemMeta;
                    Color itemDyeColor = Color.fromRGB(itemDye);
                    if (itemDyeColor != null) {
                        leatherArmorMeta.setColor(itemDyeColor);
                        itemStack.setItemMeta(itemMeta);
                    }
                }
            }
            return itemStack;
        }

        public static ItemStack setName(ItemStack itemStack, String itemName) {
            if (itemStack != null && itemName != null) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta != null) {
                    itemMeta.setDisplayName(replaceChatColour(itemName));
                    itemStack.setItemMeta(itemMeta);
                }
            }
            return itemStack;
        }

        public static ItemStack setLores(ItemStack itemStack, List<String> itemLores) {
            if (itemStack != null && itemLores != null) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta != null) {
                    itemMeta.setLore(replaceChatColours(itemLores));
                    itemStack.setItemMeta(itemMeta);
                }
            }
            return itemStack;
        }
    }

}
