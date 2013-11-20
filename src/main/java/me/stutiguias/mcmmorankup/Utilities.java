package me.stutiguias.mcmmorankup;

import org.bukkit.ChatColor;

public class Utilities {

    public static String parseColor(String message) {
        try { 
            for (ChatColor color : ChatColor.values()) {
                message = message.replaceAll(String.format("&%c", color.getChar()), color.toString());
            }
            return message;
        } catch(Exception ex) {
            return message;
        }
    }
    
    public static boolean isChar (String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isLetter(c)) return false;
        }
        return true;
    }    
    
    public static boolean isNumeric (String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    public static String getCapitalized (String target) {
      String firstLetter = target.substring(0, 1);
      String remainder = target.substring(1);
      String capitalized = firstLetter.toUpperCase() + remainder.toLowerCase();

      return capitalized;
    }
    
    public static int getInt (String string) {
      try {
        return Integer.parseInt(string);
      } catch (NumberFormatException nFE) {
    	  //
      }
      return 0;
    }

    public static long getLong (String string) {
      try {
        return Long.parseLong(string);
      } catch (NumberFormatException nFE) {
    	  //
      }
      return 0L;
    }

    public static boolean isInt (String string) {
      try {
        Integer.parseInt(string);
        return true;
      } catch (NumberFormatException nFE) {
    	  //
      }
      return false;
    }

    public static boolean isLong (String string) {
      try       {
        Long.parseLong(string);
        return true;
      } catch (NumberFormatException nFE) {
    	  //
      }
      return false;
    }

    public static boolean isDouble (String string) {
      try {
        Double.parseDouble(string);
        return true;
      } catch (NumberFormatException nFE) {
    	  //
      }
      return false;
    }
      
    public static String addColors(String input) {
		input = input.replaceAll("\\Q[[BLACK]]\\E", ChatColor.BLACK.toString());
		input = input.replaceAll("\\Q[[DARK_BLUE]]\\E", ChatColor.DARK_BLUE.toString());
		input = input.replaceAll("\\Q[[DARK_GREEN]]\\E", ChatColor.DARK_GREEN.toString());
		input = input.replaceAll("\\Q[[DARK_AQUA]]\\E", ChatColor.DARK_AQUA.toString());
		input = input.replaceAll("\\Q[[DARK_RED]]\\E", ChatColor.DARK_RED.toString());
		input = input.replaceAll("\\Q[[DARK_PURPLE]]\\E", ChatColor.DARK_PURPLE.toString());
		input = input.replaceAll("\\Q[[GOLD]]\\E", ChatColor.GOLD.toString());
		input = input.replaceAll("\\Q[[GRAY]]\\E", ChatColor.GRAY.toString());
		input = input.replaceAll("\\Q[[DARK_GRAY]]\\E", ChatColor.DARK_GRAY.toString());
		input = input.replaceAll("\\Q[[BLUE]]\\E", ChatColor.BLUE.toString());
		input = input.replaceAll("\\Q[[GREEN]]\\E", ChatColor.GREEN.toString());
		input = input.replaceAll("\\Q[[AQUA]]\\E", ChatColor.AQUA.toString());
		input = input.replaceAll("\\Q[[RED]]\\E", ChatColor.RED.toString());
		input = input.replaceAll("\\Q[[LIGHT_PURPLE]]\\E", ChatColor.LIGHT_PURPLE.toString());
		input = input.replaceAll("\\Q[[YELLOW]]\\E", ChatColor.YELLOW.toString());
		input = input.replaceAll("\\Q[[WHITE]]\\E", ChatColor.WHITE.toString());
		input = input.replaceAll("\\Q[[BOLD]]\\E", ChatColor.BOLD.toString());
		input = input.replaceAll("\\Q[[UNDERLINE]]\\E", ChatColor.UNDERLINE.toString());
		input = input.replaceAll("\\Q[[ITALIC]]\\E", ChatColor.ITALIC.toString());
		input = input.replaceAll("\\Q[[STRIKE]]\\E", ChatColor.STRIKETHROUGH.toString());
		input = input.replaceAll("\\Q[[MAGIC]]\\E", ChatColor.MAGIC.toString());
		input = input.replaceAll("\\Q[[RESET]]\\E", ChatColor.RESET.toString());

        return input;
      }
    
}
