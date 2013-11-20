package me.stutiguias.listeners;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;

import me.stutiguias.mcmmorankup.Mcmmorankup;
import me.stutiguias.mcmmorankup.Utilities;
import me.stutiguias.mcmmorankup.XpCalc;
import me.stutiguias.profile.AdminProfiler;
import me.stutiguias.profile.Profile;
import org.apache.commons.lang.WordUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class MRUCommandListener implements CommandExecutor {

    public Mcmmorankup plugin;
    private CommandSender sender;
    
    public MRUCommandListener(Mcmmorankup instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmnd, String string, String[] args) {
        
        this.sender = sender;
        
        if (sender.getName().equalsIgnoreCase("CONSOLE")) return isConsole(args);

        if (!(sender instanceof Player)) return false;
        Player _player = (Player) sender;
        
        if (args.length == 0) return Help();

        switch(args[0].toLowerCase()){
            case "buy":
                if (args.length < 3) return BuyRank(args, false);
                if (args.length == 3) return BuyRank(args, true);
            case "view":
                return  View(args);
            case "rank":
                if (!plugin.hasPermission(_player, "mru.rankup")) return false;
                return RankUp();
            case "display":
                return isDisplayCommand(args);
            case "hab":
                if (!plugin.hasPermission(_player, "mru.hability")) return false;
                return isHabCommand(args);
            case "stats":
                if (asOtherStatsPerm(args,_player) || plugin.hasPermission(_player, "mru.stats") || plugin.hasPermission(_player, "mru.admin.config")) {
                    return PlayerStats(args);
                }
                return false;
            case "ver":
                return showVersionInfo();
            case "male":
                if(plugin.UseGenderClass) return SetGender("Male");
                return false;
            case "female":
                if(plugin.UseGenderClass) return SetGender("Female");
                return false;
            case "feeds":
                if (!plugin.playerBroadcastFeed) {
                    sender.sendMessage(Utilities.parseColor(plugin.PlayerWarnings + plugin.Message.PlayerFeedsDisabled));
                    return true;
                }

                if (!plugin.hasPermission(_player, "mru.playerfeeds")) {
                    sender.sendMessage(Utilities.parseColor(plugin.PlayerWarnings + plugin.Message.NoPermPlayerFeeds));
                    return true;
                } 
                return managePlayerFeeds(args);
            case "help":
            case "?":
                return Help();
            case "reload":
                if (!plugin.hasPermission(_player, "mru.admin.config"))return false;
                return Reload();
            case "pinfo":
                if (!plugin.hasPermission(_player, "mru.admin.config")) return false;
                return isPromotionalSetCommand();
            case "report":
                if (!plugin.hasPermission(_player, "mru.admin.config")) return false;
                return AdminReport(args);
            case "set":
                if (!plugin.hasPermission(_player, "mru.admin.config")) return false;
                if (args.length == 1) return ShowConfigSettting();
                if (args.length > 1)  return isSetCommand(args);
            default:
                SendFormatMessage("&3&lThis command don't exists or you don't have permission");
                SendFormatMessage("&3&lTry /mru ? or help");
                return true;
        }
    }

    public boolean isConsole(String[] args) {
        if (args.length < 1) {
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "report":
                return AdminReport(args);
            case "reload":
                return Reload();
            case "set":
                if (args.length == 1) {
                    return ShowConfigSettting();
                }
                if (args.length > 1) {
                    return isSetCommand(args);
                }
                return false;
            default:
                return false;
        }
    }

    public boolean isPromotionalSetCommand(){
        plugin.displayNextPromo = !plugin.displayNextPromo;
        plugin.config.getConfig().set("Config.DisplayNextPromo",plugin.displayNextPromo);
        plugin.saveConfig(); 
        SendFormatMessage("&6&lPromotional Info. Toggled &7&l" + (plugin.displayNextPromo ? "ON" : "OFF"));
        return true;
    }
    
    public boolean isSetCommand(String[] args) {
        switch (args[1].toLowerCase()) {
            case "ability":
            case "defskill":
                return ChangeAbilityConfig(args);
            default:
                return ChangeOtherConfigs(args);
        }
    }

    public boolean isHabCommand(String[] args) {
        if (args.length > 1) {
            if (plugin.isIgnored((Player) sender)) {
                SendFormatMessage(plugin.Message.IgnoredRankLineSet);
                return true;
            } 
            return SetRankOnSkill(args[1]);
        } 
        return ListHability();
    }
    
    public boolean isDisplayCommand(String[] args) {
        
        if(args.length < 3) {
            SendFormatMessage(plugin.MessageSeparator);
            SendFormatMessage(plugin.Message.DisplayInformAll);
            SendFormatMessage(plugin.MessageSeparator);
            return true;
        }
        
        String skill = args[1];
        String gender = args[2];
        skill = skill.toUpperCase();
        gender = WordUtils.capitalize(gender);
        
        if(!gender.equalsIgnoreCase("male") && !gender.equalsIgnoreCase("female")) {
            SendFormatMessage(plugin.MessageSeparator);
            SendFormatMessage(plugin.Message.DisplayValidGender);
            SendFormatMessage(plugin.MessageSeparator);
            return true;
        }
        
        ArrayList<String> ranks;
        try{
            ranks = plugin.RankUpConfig.get(skill).get(gender);
        }catch(Exception ex) {
            SendFormatMessage(plugin.MessageSeparator);
            SendFormatMessage(plugin.Message.DisplayValidSkill);
            SendFormatMessage(plugin.MessageSeparator);
            return true; 
        }
        
        SendFormatMessage(plugin.MessageSeparator);
        SendFormatMessage(plugin.Message.DisplayTitle.replace("%skill%", skill).replace("%gender%", gender));
        
        for (Iterator<String> it = ranks.iterator(); it.hasNext();) {
            String entry = it.next();
            String[] levelRank = entry.split(",");
            SendFormatMessage(plugin.Message.DisplayLine.replace("%point%", levelRank[1]).replace("%rank%",levelRank[0]));
        }
        SendFormatMessage(plugin.MessageSeparator);
        return true;
    }
    
    public boolean View(String args[]) {
        Profile profile = new Profile(plugin,(Player)sender);
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        
        if(args.length > 1 && args[1].equalsIgnoreCase("on")) {
            Scoreboard board = manager.getNewScoreboard();
            Objective objective = board.registerNewObjective("mru","dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.setDisplayName("Rank Line");
            
            Score score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + profile.GetHabilityForRank()));
            score.setScore(profile.GetSkillLevelRankLine()); 
            
            profile.player.setScoreboard(board);
            return true;
        }else{
            profile.player.setScoreboard(manager.getNewScoreboard());
            return true;
        }
        
    }
    
    public boolean Reload() {
        SendFormatMessage(ChatColor.YELLOW + "Start reload...");
        plugin.onReload();
        SendFormatMessage(ChatColor.YELLOW + "Reload complete...");
        return true;
    }

    public boolean ShowConfigSettting() {
        SendFormatMessage("&2Usage: &c/mru set <setting> <value>&2:");
        SendFormatMessage(ShowConfigSettings());
        return true;
    }

    public boolean RankUp() {

        if (plugin.Playertime.containsKey(sender.getName()) && plugin.Playertime.get(sender.getName()) + 5000 > plugin.GetCurrentMilli()) {
            SendFormatMessage(plugin.MessageSeparator);
            SendFormatMessage(plugin.Message.CommandAttempt);
            SendFormatMessage(plugin.MessageSeparator);
            return true;
        }

        Profile profile = new Profile(plugin,(Player)sender);
        String skill = profile.GetHabilityForRank().toUpperCase();
        String gender = profile.GetGender();

        if (!plugin.CheckRankExist(skill)) {
            SendFormatMessage(plugin.Message.NoLongerExists.replace("%rankline%", skill.toUpperCase()));
            return false;
        }

        if (!plugin.isRankAvailable(skill,profile.player)) {
            SendFormatMessage(plugin.Message.NoAccess.replace("%rankline%", skill.toUpperCase()));
            return true;
        }

        String ruStatus = plugin.RankUp.TryRankUp(profile.player, skill, gender);
        plugin.Playertime.put(sender.getName(), plugin.GetCurrentMilli());

        if (plugin.playerBroadcastFeed && !profile.GetPlayerRankupFeed()) {
            return true;
        }

        String bCastMsg;
        switch(ruStatus.toLowerCase()){
            case "promoted":
                bCastMsg = plugin.Message.Sucess;
                break;
            case "demoted":
                bCastMsg = plugin.Message.Demotion;
                break;
            case "ignore":
                 bCastMsg = plugin.Message.PromosIgnored;
                 break;
            default:
                 bCastMsg = null;
                 break;
        }

        if (bCastMsg != null) {
            SendFormatMessage(plugin.MessageSeparator);
            SendFormatMessage(bCastMsg);
            SendFormatMessage(plugin.MessageSeparator);
        }
        
        return true;
  
    }

    public boolean SetRankOnSkill(String Skill) {

        Profile profile = new Profile(plugin, (Player) sender);

        if (!Skill.equalsIgnoreCase("POWERLEVEL") && !plugin.isRankAvailable(Skill, profile.player)) {
            SendFormatMessage(plugin.Message.HabilitySetFail.replace("%ability%", Utilities.getCapitalized(Skill)));
            return true;
        }
        
        if (!plugin.CheckRankExist(Skill)) {
            SendFormatMessage(plugin.MessageSeparator);
            SendFormatMessage(plugin.Message.NotAvailable);
            SendFormatMessage(plugin.MessageSeparator);
            return false;
        }
        return profile.SetHabilityForRank(Skill);
    }

    public boolean Help() {
        Player player = (Player) sender;
        SendFormatMessage(plugin.MessageSeparator);
        SendFormatMessage(" &7RANKING HELP ");
        
        SendFormatMessage("&6/mru view <on|off> &7" + plugin.Message.HelpView);
        
        if (plugin.hasPermission(player, "mru.rankup")) {
            SendFormatMessage("&6/mru rank &7" + plugin.Message.HelpRank);
        }

        if (plugin.UseGenderClass) {
            SendFormatMessage("&6/mru <male|female> &7" + plugin.Message.HelpMaleFemale);
        }

        if (plugin.hasPermission(player, "mru.hability")) {
            SendFormatMessage("&6/mru hab &7" + plugin.Message.HelpHab);
            String outMsg = "&6/mru hab <ability> ";
            if (!plugin.isIgnored(player)) {
                outMsg += "&7" + plugin.Message.HelpSethab;
            } else {
                outMsg += "&c" + plugin.Message.HelpSethabIgnore;
            }
            SendFormatMessage(outMsg);
            SendFormatMessage("&6/mru display <ability> <gender> &7" + plugin.Message.HelpDisplayHab);
        }

        if (plugin.hasPermission(player, "mru.playerfeeds") && plugin.playerBroadcastFeed) {
            SendFormatMessage("&6/mru feeds &7" + plugin.Message.HelpFeeds);
        }

        if (plugin.hasPermission(player, "mru.buyrankxp") || plugin.hasPermission(player, "mru.buyrankbuks")) {
            SendFormatMessage("&6/mru buy <x | b> &7" + plugin.Message.BuyMenu.replace("%currencyname%", plugin.BuyRankCurrencyName));
        }

        if (plugin.hasPermission(player, "mru.admin.config") || plugin.hasPermission(player, "mru.stats") || plugin.hasPermission(player, "mru.stats.others")) {
            SendFormatMessage(plugin.MessageSeparator);
            SendFormatMessage(" &7ADMIN HELP ");
            SendFormatMessage("&6/mru ver &7Show mcmmoRankup version information");
            SendFormatMessage("&6/mru stats [player] &7Stats Skill Check. [player] Optional");
        }
        
        if (plugin.hasPermission(player, "mru.admin.config")) {
            SendFormatMessage("&6/mru report &7Admin Ranking Report Options");
            SendFormatMessage("&6/mru set <setting> <value> &7Set Config. Settings");
            SendFormatMessage("&6/mru pinfo &7Toggle Next Promotion Info. &e" + (plugin.displayNextPromo ? "OFF" : "ON"));
        }

        if (plugin.hasPermission(player, "mru.admin.reload")) {
            SendFormatMessage("&6/mru reload &7Reload the all configs...");
        }

        SendFormatMessage(plugin.MessageSeparator);

        return true;
    }

    public boolean managePlayerFeeds(String[] args) {

        Profile profile = new Profile(plugin, (Player) sender);

        boolean rankupFeed  = profile.GetPlayerRankupFeed();
        boolean xpFeed      = profile.GetPlayerXpUpdateFeed();
        boolean levelFeed   = profile.GetPlayerLevelUpsFeed();
        boolean globalFeed  = profile.GetPlayerGlobalFeed();

        if (args.length < 3) {
            SendFormatMessage(plugin.MessageSeparator);
            SendFormatMessage("&3Manage Your mcmmoRank Feeds &a(&f&lcurrent status&a)");
            SendFormatMessage(plugin.PlayerWarnings + "Usage: /mru feeds <feedname> <on | off>");
            SendFormatMessage(plugin.MessageSeparator);
            SendFormatMessage("&aRankup &6(&f" + (rankupFeed ? "On" : "Off") + "&6)&a - Show your Promotion when you rank up");
            SendFormatMessage("&aXp &6(&f" + (xpFeed ? "On" : "Off") + "&6)&a - Excluding Powerlevel, show your skills XP/Lvl Ups?");
            SendFormatMessage("&aLevels &6(&f" + (levelFeed ? "On" : "Off") + "&6)&a - Celebrate Rank Line Ability Level Ups??");
            SendFormatMessage("&aGlobal &6(&f" + (globalFeed ? "On" : "Off") + "&6)&a - Share your Promotions/Demotions with others??\n");
            SendFormatMessage(plugin.MessageSeparator);
            return true;
        }

        String msg = "";
        boolean toggle = ParseToggleInput(args[2]);

        if (!toggle) {
            SendFormatMessage(plugin.GeneralMessages + "Invalid setting: Expected On, Off, True or False");
            return false;
        }
        
        toggle = (args[2].toUpperCase().matches("[oO]N|[tT]RUE") ? true : false);
        
        switch(args[1].toLowerCase()) {
            case "rankup": 
                if (toggle != rankupFeed) {
                    profile.SetPlayerRankupFeed(toggle);
                    msg = "&3Rank Up";
                }
                break;
            case "global":
                if (toggle != globalFeed) {
                    profile.SetPlayerGlobalFeed(toggle);
                    msg = "&3Your Global";
                }
                break;
            case "xp":
                if (toggle != xpFeed) {
                    profile.SetPlayerXpUpdateFeed(toggle);
                    msg = "&3Ability Xp Updates";
                }
                break;
            case "levels":
                if (toggle != levelFeed) {
                    profile.SetPlayerXpUpdateFeed(toggle);
                    msg = "&3Celebratory Level Up";
                }
                break;
        }

        if (!msg.isEmpty()) {
            msg += " Feed has been set: &f" + (toggle ? "On" : "Off");
        } else {
            msg = plugin.PlayerWarnings + "Was already " + (toggle ? "On" : "Off") + " or not a valid feed!";
        }
        
        SendFormatMessage(msg);
        return true;
    }

    public boolean ParseToggleInput(String parse) {
        if (!parse.matches("[oO]n|[oO]ff|[tT]rue|[fF]alse|[T]RUE|[F]ALSE")) {
            return false;
        }
        return true;
    }
    
    public Boolean BuyRank(String[] args,Boolean isBuying) {

        Profile profile = new Profile(plugin, (Player) sender);
        String skill = profile.GetHabilityForRank();
        Boolean canBuy;

        if (!plugin.AllowBuyingRanks || !plugin.BuyRankEnabled.get(skill.toUpperCase())) {
            SendFormatMessage(plugin.Message.BuyDisabled);
            return false;
        }
        
        if (plugin.hasPermission(profile.player, "mru.ignore")) {
            SendFormatMessage("&3&lYou r setting to ignore!(Permission)");
            return false;
        }
        
        if (plugin.GroupToIgnore(profile.player)) {
            SendFormatMessage("&3&lYou r setting to ignore!(Group)");
            return false;
        }
	
        if (plugin.BuyRankUsePerms.get(skill.toUpperCase()) && args.length > 1) {
            if (!plugin.hasPermission(profile.player, "mru.buyrankxp") && args[1].equalsIgnoreCase("x")) {
                SendFormatMessage(plugin.Message.BuyNoPermXp);
                return true;
            }

            if (!plugin.hasPermission(profile.player, "mru.buyrankbuks") && args[1].equalsIgnoreCase("b")) {
                SendFormatMessage(plugin.Message.BuyNoPermBuks.replace("%currency%", plugin.BuyRankCurrencyName));
                return true;
            }
        }

        if (args.length < 2 || !args[1].toLowerCase().matches("[xb]")) {
            SendFormatMessage(plugin.MessageSeparator);
            SendFormatMessage("&6Usage: /mru buy <x | b>");
            SendFormatMessage("&3'x' to buy using 'XP'");
            SendFormatMessage("&3'b' to buy using ' " + plugin.BuyRankCurrencyName + " '");
            SendFormatMessage(plugin.MessageSeparator);
            return true;
        }

        if (!isBuying) {
            canBuy = plugin.BuyRank.ShowBuyableRanks(profile.player, args);

            if (canBuy) {
                SendFormatMessage(plugin.MessageSeparator);
                SendFormatMessage(plugin.Message.BuyPurchaseReq.replace("%buyflag%", args[1]).replace("%command%", "/mru buy") + "\n");
            } else {

                String noBuyMsg = plugin.Message.BuyPurchaseNot;

                if (args[1].equalsIgnoreCase("x")) {
                    noBuyMsg = noBuyMsg.replace("%buymethod%", "XP points and/or Levels");
                }
                if (args[1].equalsIgnoreCase("b")) {
                    noBuyMsg = noBuyMsg.replace("%buymethod%", plugin.BuyRankCurrencyName);
                }

                SendFormatMessage(noBuyMsg);

            }
        } else {
            plugin.BuyRank.PurchaseRank(profile.player, args[2], args[1]);
        }
        return true;
    }

    public boolean showVersionInfo() {
        SendFormatMessage("VERSION INFO.");
        SendFormatMessage(plugin.getDescription().getVersion());
        SendFormatMessage("&6Report an Issue??:\n&3" + plugin.verIssues);
        SendFormatMessage("&6\nDownload today at:\n&3" + plugin.verLink);
        SendFormatMessage(plugin.MessageSeparator);
        return true;
    }

    public boolean PlayerStats(String[] args) {

        Profile profile = new Profile(plugin, (Player)sender);

        boolean lastSet = false;
        
        if (args.length == 2 && !args[1].isEmpty()) {
            OfflinePlayer ckPlayer = Bukkit.getOfflinePlayer(args[1].toString());
            if (!ckPlayer.hasPlayedBefore()) {
                SendFormatMessage(plugin.PlayerWarnings + "Not a player of the server...");
                return true;
            } 
            if (!ckPlayer.isOnline()) {
                SendFormatMessage("&2&l" + ckPlayer.getName() + plugin.PlayerWarnings + " is not online!");
                SendFormatMessage(plugin.PlayerWarnings + "\nStats are restricted to online players!");
                return true;
            } 			
            profile = new Profile(plugin,(Player)ckPlayer.getPlayer());
        }
        
        String lastRank        = profile.GetQuitRank();
        String lastSkill       = profile.GetQuitSkill();
        int lastSkillLevel     = profile.GetQuitLevel();
        int lastXp             = (int) profile.GetQuitXp();
        int lastXpL            = profile.GetQuitXpLevel();
        double lastBalance     = profile.GetQuitBalance();
    
        String rank            = plugin.TagSystem ? profile.GetTag() : plugin.permission.getPrimaryGroup(profile.player.getWorld(), profile.player.getName());
        String skill           = profile.GetHabilityForRank();
        Integer skillLevel     = plugin.GetSkillLevel(profile.player, skill);
        int Xp                 = XpCalc.GetTotalExperience(profile.player);
        int XpL                = XpCalc.GetPlayerXpl(profile.player);
        double balance         = plugin.GetPlayerCurrency(profile.player);

        if (lastSkill.isEmpty() || lastSkill.equalsIgnoreCase("N/A")) {
            SendFormatMessage(plugin.Message.LastQuitStatsFail + "\n\n");
            profile.SetQuitStats();
            lastSet = true;
        }	
        
        StringBuilder outPut = new StringBuilder();
        outPut.append("\n").append(profile.player.getName());
        outPut.append("\n").append(String.format("%1$45s", "&a&oLAST STATS:"));
        if (!lastSet) {
            outPut.append("\n&3").append(plugin.BuyRankCurrencyName).append(" &6").append(String.format("$%.2f", lastBalance));
            outPut.append("\n").append(plugin.Message.BuyPurchaseXp.replace("%xp%", String.valueOf(lastXp)).replace("%level%", String.valueOf(lastXpL)));
            outPut.append("\n&3 Rank: &6").append(plugin.Message.BuyProfile.replace("%rankline%", lastSkill.toLowerCase()).replace("%group%", Utilities.getCapitalized(lastRank)).replace("%level%", String.valueOf(lastSkillLevel)));
        } else {
            outPut.append("\n").append(plugin.PlayerWarnings).append("No last stats available - Profile just migrated...");
        }
        outPut.append("\n").append(plugin.MessageSeparator);
        outPut.append(String.format("%1$45s", "&a&oCURRENT STATS:"));
        outPut.append("\n&3").append(plugin.BuyRankCurrencyName).append(" &6").append(String.format("$%.2f", balance));
        outPut.append("\n").append(plugin.Message.BuyPurchaseXp.replace("%xp%", String.valueOf(Xp)).replace("%level%", String.valueOf(XpL)));
        outPut.append("\n&3 Rank: &6").append(plugin.Message.BuyProfile.replace("%rankline%", skill.toLowerCase()).replace("%group%", Utilities.getCapitalized(rank)).replace("%level%", String.valueOf(skillLevel)));
        outPut.append("\n").append(plugin.MessageSeparator).append("\n");

        SendFormatMessage(outPut.toString());
        return true;
    }

    public boolean SetGender(String gender) {
        Profile profile = new Profile(plugin, (Player)sender);
        profile.SetGender(gender);
        SendFormatMessage("GENDER SELECTED");
        SendFormatMessage(plugin.GeneralMessages + plugin.Message.SetGender.replace("%gender%", gender));
        SendFormatMessage(plugin.MessageSeparator);
        return true;
    }

    public boolean ListHability() {
        SendFormatMessage(plugin.Message.BaseRanksListing);
        String outMsg;
        String preFix="";
        Profile profile = new Profile(plugin, (Player)sender);
        String skill = profile.GetHabilityForRank();
        String fmt = "%1$-2s &6%2$-25s %3$-32s";
        boolean noPerm = false;

        for (String key : plugin.isRankExist.keySet()) {
 		
            if (!plugin.isRankAvailable(key, profile.player)) {
                noPerm = true;
            } else {
                if (plugin.BuyRankEnabled.size() == 0 && plugin.BuyRankEnabled.get(key)) {
                    preFix = plugin.Message.HabListPrefixBuy;
                }

                if (plugin.isRankExist.get(key)) {
                      outMsg = plugin.Message.AbilityEnabled;
                }else{
                      if(!plugin.displayDisabledRanks) continue;
                      outMsg = plugin.Message.AbilityDisabled;
                } 
                
                if (key.equalsIgnoreCase(skill)) {
                      outMsg = plugin.Message.HabListCurRankLine;
                } 

                String playerLevel = String.valueOf(plugin.GetSkillLevel(profile.player, key));
                SendFormatMessage(String.format(fmt, preFix, key + plugin.Message.HabListLevel.replace("%level%", " " + playerLevel) , outMsg));
            }

        }

        SendFormatMessage(("\n" + plugin.Message.DefaultSkilltoRank).replace("%ability%", plugin.DefaultSkill));
        if (!plugin.displayDisabledRanks || noPerm) {
            SendFormatMessage("\n" + plugin.Message.NotShowInfo);
        }
        SendFormatMessage(plugin.MessageSeparator);
 	    	
        if (!plugin.isRankAvailable(skill, profile.player)) {
            SendFormatMessage(plugin.Message.NoAccess.replaceAll("%rankline%", skill.toUpperCase()));
        }

        if (!plugin.CheckRankExist(skill.toUpperCase())) {
            SendFormatMessage(plugin.Message.NoLongerExists.replaceAll("%rankline%", skill.toUpperCase()));
        }

        return true;
    }

    public boolean ChangeAbilityConfig(String[] args) {
        boolean validSkill = false;
        String skill = "";
        String rankSkills = "\n";

        if (args.length >= 3) {
            skill = args[2].toUpperCase();
        }

        for (String key : plugin.isRankExist.keySet()) {
            if (plugin.isRankExist.get(key)) {
                rankSkills += "&a" + key + "  - Enabled\n";
            } else {
                rankSkills += "&8" + key + "  - Disabled\n";
            }

            if (!validSkill) {
                validSkill = skill.equalsIgnoreCase(key);
            }
        }

        if (args.length == 2) {
            SendFormatMessage(plugin.MessageSeparator);
            SendFormatMessage("&6RankUp Files and Status: (default: &c" + plugin.DefaultSkill + "&6)" + rankSkills);
            SendFormatMessage("&7  ** (ability names are not case sensitive)");
            SendFormatMessage(plugin.MessageSeparator);
        }
        
        if (args.length == 3 && !validSkill) {
            sender.sendMessage(Utilities.parseColor("&4" + "Invalid ability section..."));
            return true;
        }
        
        if (args[1].equalsIgnoreCase("defskill")) {
           return SetDefaultSkill(args,skill);
        }

        if (args[1].equalsIgnoreCase("ability")) {
            return SetSkillStatus(args,skill);
        }
        
        return false;
    }
    
    private boolean SetSkillStatus(String[] args,String skill) {
        if (args.length < 4) {
             SendFormatMessage("&6Usage: &f/mru set ability &6<&7ability&6> <&7on &6or &7off&6>");
             SendFormatMessage("&6Type: &f/mru set ability &6for Rank File Listings");
             return true;
         }
         if (!ParseToggleInput(args[3])) {
             SendFormatMessage("&6&lInvalid ability setting. &6Values allowed: &eon,off,true,false");
             return true;
         }

         String toggle = (args[3].toUpperCase().matches("[oO]N|[tT]RUE") ? "true" : "false");
         if (UpdateSetConfig("Config.Skills." + skill + ".enabled", toggle)) {

             if (skill.equalsIgnoreCase(plugin.DefaultSkill)) {
                 SendFormatMessage("&cYou disabled the current default skill.");
                 SendFormatMessage("&cDefault Skill reset back to POWERLEVEL, if it");
                 SendFormatMessage("&cis disabled, POWERLEVEL will be enabled on reload");
                 plugin.DefaultSkill = "POWERLEVEL";
                 plugin.config.getConfig().set("Config.DefaultSkill", plugin.DefaultSkill);
                 plugin.config.saveConfig();
                 plugin.config.reloadConfig();
                 Mcmmorankup.logger.log(Level.INFO, "{0} {1} just updated Default Skill: {2} to a {3} status", new Object[]{Mcmmorankup.logPrefix, sender.getName(), skill,toggle});
             }

             SendFormatMessage("&fRanking Ability ' &3" + skill + " &f' has been set: &3" + args[3].toUpperCase());
             SendFormatMessage("&cNote: &6Ability files disabled &cARE NOT DELETED&6!!");
             Mcmmorankup.logger.log(Level.INFO, "{0} {1} just updated ability: {2} to a {3} status", new Object[]{Mcmmorankup.logPrefix, sender.getName(), skill,toggle});
         }
         SendFormatMessage(plugin.MessageSeparator);
         return true;
    }
    
    private boolean SetDefaultSkill(String[] args,String skill) {
        if (args.length < 3) {
           SendFormatMessage("&6" + "Usage: &f/mru set &6<&7setting&6> <&7value&6>");
           SendFormatMessage("&6" + "Type: &f/mru set &6For Configuration settings");
           return true;
       }

       plugin.DefaultSkill = skill;
       plugin.config.getConfig().set("Config.DefaultSkill", plugin.DefaultSkill);
       plugin.config.saveConfig();
       plugin.config.reloadConfig();
       SendFormatMessage("&f" + "DefaultSkill updated to: &3" + skill);
       SendFormatMessage("&cNote: Players who were using previous default skill will be");
       SendFormatMessage("&c       notified to change their rank-up ability to new default");
       SendFormatMessage("&cNote: If new default skill is currently disabled, it will");
       SendFormatMessage("&c       be enabled upon reload or server restart.");
       return true;
    }

    private boolean UpdateSetConfig(String setting, String value) {
        try {
            if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                plugin.config.getConfig().set(setting,Boolean.parseBoolean(value));
            }else{
                plugin.config.getConfig().set(setting, value);
            }
            plugin.config.saveConfig();
            plugin.config.reloadConfig();
            plugin.onLoadConfig();
            return true;
        } catch (Exception e) {
            SendFormatMessage("&4There was a problem setting that configuration setting!");
        }
        return false;
    }

    public String ShowConfigSettings() {
        StringBuilder settings = new StringBuilder();
        settings.append("&2Settings: &cability <ability> <value>, autorank, autotime <#m/#h>, defskill <ability>, starttag <name>, demotions, brdemotions, brsamedemotions, globalfeed, ");
        settings.append("nextpromoinfo, rankinfoxp, rankinfotitles, onjoin, onjoindelay, playerfeed, playerxpfeed, buyranks, rankrewards, remplugingrp, ");
        settings.append("showdisabled, usealtbroadcast, usegenders, usetag, startsummary");
        settings.append("\n&2Values: '&con&2' or '&coff&2', &cM&2=minutes & &cH&2=Hours, &cAbility &2= Skill Name");
        return settings.toString();
    }

    private boolean ChangeOtherConfigs(String[] args) {
        if (args.length < 3) {
            SendFormatMessage("&6" + "Usage: &f/mru set &6<&7setting&6> <&7value&6>");
            SendFormatMessage("&6" + "Type: &f/mru set &6For Configuration settings");
            return true;
        }

        String cfg = "";
        String eMsg = "&c/mru set value is invalid... \n&2expected: ";
        String value;
        boolean invalid = true;
        boolean stdParse = true;

        switch (args[1].toLowerCase()) {
            case "startsummary":
                cfg = "mruStartupSummary";
                break;
            case "usetag":
                cfg = "UseTagOnlySystem";
                break;
            case "remplugingrp":
                cfg = "RemoveOnlyPluginGroup";
                break;
            case "showdisabled":
                cfg = "DisplayDisabledRanks";
                break;
            case "demotions":
                cfg = "AllowDemotions";
                break;
            case "brdemotions":
                cfg = "AllowBuyRankDemotions";
                break;
            case "brsamedemotions":
                cfg = "AllowBuyRankSameRankLineDemotions";
                break;
            case "onjoin":
                cfg = "PromoteOnJoin";
                break;
            case "globalfeed":
                cfg = "GlobalBroadcastFeed";
                break;
            case "playerfeed":
                cfg = "PlayerBroadcastFeed";
                break;
            case "nextpromoinfo":
                cfg = "DisplayNextPromo";
                break;
            case "autorank":
                cfg = "AutoUpdate";
                break;
            case "usealtbroadcast":
                cfg = "UseAlternativeBroadCast";
                break;
            case "usegenders":
                cfg = "UseGenderClass";
                break;
            case "buyranks":
                cfg = "AllowBuyRanks";
                break;
            case "rankrewards":
                cfg = "AllowRankRewards";
                break;
            case "rankinfoxp":
                cfg = "DisplayRankInfoXp";
                break;
            case "rankinfotitles":
                cfg = "RankInfoTitles";
                break;
            case "playerxpupdate":
                cfg = "PlayerAbilityXpUpdateFeed";
                break;
            case "autotime":
                cfg = "AutoUpdateTime";
                stdParse = false;
                String tstTime = args[2] = args[2].toLowerCase();
                int upTime = Integer.parseInt(tstTime.replace("h", "").replace("m", ""));
                if (upTime >= 1 && upTime <= 24) {
                    invalid = false;
                }
                eMsg += "number<d|h> betwen &71<d|h>&2 and &724<d|h>";
                break;
            case "onjoindelay":
                cfg = "OnJoinDelay";
                stdParse = false;
                if (Utilities.isNumeric(args[2])) {
                    int delay = Integer.parseInt(args[2]);
                    if (delay >= 0 && delay <= 72000) {
                        invalid = false;
                    }
                }
                eMsg += "number between &70&2 and &772000&2 (60 min)";
                break;
            case "starttag":
                cfg = "StartTagName";
                stdParse = false;
                if (Utilities.isChar(args[2])) {
                    invalid = false;
                }
                eMsg += "'chars' for Starting Tag Name for new players";
                break;
        }

        if (cfg.isEmpty()) {
            SendFormatMessage(plugin.GeneralMessages + "Invalid configuration setting. Type /mru set for settings");
            return true;
        }
        
        if (stdParse) {
            if (ParseToggleInput(args[2])) {
                invalid = false;
                eMsg += "on, off, true, false";
            }
            value = (args[2].matches("[oO]n|[tT]rue") ? "true" : "false");
        }else{
            value = args[2];
        }

        if (invalid) {
            SendFormatMessage(plugin.MessageSeparator);
            SendFormatMessage(plugin.GeneralMessages + eMsg);
            return false;
        } 
        
        cfg = "Config." + cfg;
        if (UpdateSetConfig(cfg, value)) {
            SendFormatMessage(plugin.MessageSeparator);
            SendFormatMessage(plugin.GeneralMessages + cfg + " has been updated to: &7" + value);
            Mcmmorankup.logger.log(Level.INFO, "{0} {1} just updated Configuration Setting: {2} to a {3} status", new Object[]{Mcmmorankup.logPrefix, sender.getName(), args[1],value});
        }
        
        SendFormatMessage(plugin.MessageSeparator);
        return true;
    }

    public boolean AdminReport(String[] args) {

        if (!sender.getName().equalsIgnoreCase("CONSOLE") && !plugin.hasPermission((Player) sender,"mru.admin.config")) {
                return false;
        }

        if (args.length < 2 || args.length > 4) {
            SendFormatMessage("&6Usage: /mru report <a | g | c> <skill> [filename]");
            SendFormatMessage("&2  - a [filename] - all players grouped by ability report");
            SendFormatMessage("&2  - g [filename] - all players grouped by gender report");
            SendFormatMessage("&2  - c <skill> [filename] - Specific ability only report");
            SendFormatMessage("&7** FileName is Optional.");
            return true;
        }
        
        String cat = null;
        String reportFileName = null;

        switch(args[1].toLowerCase()) {
            case "c":
                if (!plugin.isRankExist.containsKey(args[2].toUpperCase())) {
                    SendFormatMessage(plugin.PlayerWarnings + "Huh! Expected a skill as the reports category and a valid skill!");
                    return false;
                }
                cat = args[2];
                
                if (args.length == 4) {
                    reportFileName = args[3];
                }
                break;
            case "g":
                cat = args[1];
            case "a":
                if (args.length == 3) {
                    reportFileName = args[2];
                }
                break;
            default:
                SendFormatMessage(plugin.PlayerWarnings + "not a valid argument for this command.");
                return true;
        }

        AdminProfiler AdminProfiler = new AdminProfiler(sender, plugin, args[1], cat);
        AdminProfiler.PrintReport(reportFileName, cat);

        return true;
    }
    
    public boolean asOtherStatsPerm(String[] args, Player player) {
        return args.length == 2 && plugin.hasPermission(player, "mru.stats.others");
    }
    
    public void SendFormatMessage(String msg) {
        sender.sendMessage(Utilities.parseColor(msg));
    }
}
