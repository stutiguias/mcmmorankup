package me.stutiguias.mcmmorankup;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Util {
    
    public final Mcmmorankup plugin;
    public CommandSender sender;
    
    public Util(Mcmmorankup plugin) {
        this.plugin = plugin;
    }
    
    public String parseColor(String message) {
        try { 
            for (ChatColor color : ChatColor.values()) {
                message = message.replaceAll(String.format("&%c", color.getChar()), color.toString());
            }
            return message;
        } catch(Exception ex) {
            return message;
        }
    }

    public boolean isChar (String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isLetter(c)) return false;
        }
        return true;
    }    

    public boolean isNumeric (String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    public String Capitalized (String target) {
      String firstLetter = target.substring(0, 1);
      String remainder = target.substring(1);
      String capitalized = firstLetter.toUpperCase() + remainder.toLowerCase();

      return capitalized;
    }

    public int getInt (String string) {
      try {
        return Integer.parseInt(string);
      } catch (NumberFormatException nFE) {
          //
      }
      return 0;
    }

    public long getLong (String string) {
      try {
        return Long.parseLong(string);
      } catch (NumberFormatException nFE) {
          //
      }
      return 0L;
    }

    public boolean isInt (String string) {
      try {
        Integer.parseInt(string);
        return true;
      } catch (NumberFormatException nFE) {
          //
      }
      return false;
    }

    public boolean isLong (String string) {
      try       {
        Long.parseLong(string);
        return true;
      } catch (NumberFormatException nFE) {
          //
      }
      return false;
    }

    public boolean isDouble (String string) {
      try {
        Double.parseDouble(string);
        return true;
      } catch (NumberFormatException nFE) {
          //
      }
      return false;
    }

    public String addColors(String input) {
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
        
    public void SendMessage(String msg) {
        sender.sendMessage(parseColor(msg));
    }

    public void SendMessage(String msg,Object[] args) {
        sender.sendMessage(parseColor(String.format(msg,args)));
    }

    public void SendMessage(Player player,String msg) {
        player.sendMessage(parseColor(msg));
    }

    public void SendMessage(Player player,String msg,Object[] args) {
        player.sendMessage(parseColor(String.format(msg,args)));
    }

    public void BrcstMsg(String msg) {
        plugin.getServer().broadcastMessage(parseColor(msg));
    }

    public void BrcstMsg(String msg,Object[] args) {
        plugin.getServer().broadcastMessage(parseColor(String.format(msg,args)));
    }
}
