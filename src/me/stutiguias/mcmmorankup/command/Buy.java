/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.stutiguias.mcmmorankup.command;

import me.stutiguias.mcmmorankup.Mcmmorankup;
import static me.stutiguias.mcmmorankup.Mcmmorankup.Message;
import me.stutiguias.mcmmorankup.profile.Profile;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Daniel
 */
public class Buy extends CommandHandler {

    Profile profile;
    String skill;
    
    public Buy(Mcmmorankup plugin) {
        super(plugin);
    }

    @Override
    protected Boolean OnCommand(CommandSender sender, String[] args) {
        this.sender = sender;
        profile = new Profile(plugin, (Player) sender);
        skill = profile.GetHabilityForRank();
        
        if(isInvalid(sender, args)) return true;
        
        if (args.length < 3) return BuyRank(args, false);
        if (args.length == 3) return BuyRank(args, true);
        return true;
    }

    @Override
    protected Boolean isInvalid(CommandSender sender, String[] args) {
        if (!plugin.AllowBuyingRanks || !plugin.BuyRankEnabled.get(skill.toUpperCase())) {
            SendMessage(Message.BuyDisabled);
            return true;
        }

        if (plugin.hasPermission(profile.player, "mru.ignore")) {
            SendMessage("&3&lYou r setting to ignore!(Permission)");
            return true;
        }

        if (plugin.GroupToIgnore(profile.player)) {
            SendMessage("&3&lYou r setting to ignore!(Group)");
            return true;
        }
               
        if (plugin.BuyRankUsePerms.get(skill.toUpperCase()) && args.length > 1) {
           if (!plugin.hasPermission(profile.player, "mru.buyrankxp") && args[1].equalsIgnoreCase("x")) {
               SendMessage(Message.BuyNoPermXp);
               return true;
           }

           if (!plugin.hasPermission(profile.player, "mru.buyrankbuks") && args[1].equalsIgnoreCase("b")) {
               SendMessage(Message.BuyNoPermBuks.replace("%currency%", plugin.BuyRankCurrencyName));
               return true;
           }
        }

        if (args.length < 2 || !args[1].toLowerCase().matches("[xb]")) {
            SendMessage(Message.MessageSeparator);
            SendMessage("&6Usage: /mru buy <x | b>");
            SendMessage("&3'x' to buy using 'XP'");
            SendMessage("&3'b' to buy using '%s'",new Object[] { plugin.BuyRankCurrencyName });
            SendMessage(Message.MessageSeparator);
            return true;
        }
        
        return false;
    }
    
    public Boolean BuyRank(String[] args,Boolean isBuying) {
        
       if (!isBuying) {
           Boolean canBuy = plugin.BuyRank.ShowBuyableRanks(profile.player, args);

           if (canBuy) {
               SendMessage(Message.MessageSeparator);
               SendMessage(Message.BuyPurchaseReq.replace("%buyflag%", args[1]).replace("%command%", "/mru buy") + "\n");
           } else {

               String noBuyMsg = Message.BuyPurchaseNot;

               if (args[1].equalsIgnoreCase("x")) {
                   noBuyMsg = noBuyMsg.replace("%buymethod%", "XP points and/or Levels");
               }
               if (args[1].equalsIgnoreCase("b")) {
                   noBuyMsg = noBuyMsg.replace("%buymethod%", plugin.BuyRankCurrencyName);
               }

               SendMessage(noBuyMsg);

           }
       } else {
           plugin.BuyRank.PurchaseRank(profile.player, args[2], args[1]);
       }
       return true;
   }
    
}
