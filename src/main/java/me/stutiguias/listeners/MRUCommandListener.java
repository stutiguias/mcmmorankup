package me.stutiguias.listeners;

import me.stutiguias.mcmmorankup.ChatTools;
import me.stutiguias.mcmmorankup.Mcmmorankup;
import me.stutiguias.profile.Profile;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MRUCommandListener implements CommandExecutor {
    
    public Mcmmorankup plugin;
    
    public MRUCommandListener(Mcmmorankup instance)
    {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
         
    	 // Do not use Console !
         //if(!(cs instanceof Player) || args.length == 0) return false;
    	if(!(cs instanceof Player)) {
    		return false;
    	} else if (args.length==0) {
    		return Help(cs);
    	}

         // Show ? - Checks the players current and next promotional levels and displays them only      
         // Rank ? - Checks and attempts to promote the player to the next level in the promotion ladder
         // args[0] = show or rank
         if( (args[0].equalsIgnoreCase("rank")) || (args[0].equalsIgnoreCase("show")) ) return Check(cs, args[0]);
         
         // Change to Rank Up on Ability
         if(args[0].equalsIgnoreCase("hab")) {
             if(args.length > 1) {
                return RankOnHability(cs,args[1].toString());
             }else{
                return ListHability(cs);
             }
         } 
         
         if(args[0].equalsIgnoreCase("male")) {
             setGender(cs,"Male");
         }
         
         if(args[0].equalsIgnoreCase("female")) {
             setGender(cs,"Female");
         }
         
         // Toggle Displaying The Next Promotional Information in Rank Info and/or Promotion Messaging
         if(args[0].equalsIgnoreCase("pinfo")) {
             Player _player = (Player)cs;
             if(!plugin.permission.has(_player.getWorld(), _player.getName(),"mru.reload")) return false;
        	 
             plugin.displayNextPromo = !plugin.displayNextPromo;
             plugin.getConfig().set("Config.DisplayNextPromo",  plugin.displayNextPromo);
             plugin.saveConfig();
             
             cs.sendMessage("" + ChatColor.YELLOW + ChatColor.BOLD + "Promotional Information Toggled " +
                ChatColor.WHITE + ChatColor.BOLD + (plugin.displayNextPromo ? "ON" : "OFF")); 
         }
         
         // Its Reload ?
         if(args[0].equalsIgnoreCase("reload")) {
             Player _player = (Player)cs;
             if(!plugin.permission.has(_player.getWorld(), _player.getName(),"mru.reload")) return false;
             plugin.onReload();
             cs.sendMessage("Reload complete...");
             return false;
         }
         
         if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) return Help(cs);
         
         return false;
    }
    
    public boolean Check(CommandSender cs, String cmd) {
             boolean alreadyuse = false;
             
             if(cmd.equalsIgnoreCase("rank")) {
	             if(plugin.Playertime.isEmpty()) {
	                 alreadyuse = false;
	             } else if (plugin.Playertime.containsKey(cs.getName()) && plugin.Playertime.get(cs.getName()) + 10000 > plugin.getCurrentMilli()) {
	                 alreadyuse = true;
	             }
             } else {
            	 alreadyuse=false;
             }
         
             if(!alreadyuse)    
             {
                Player pl = plugin.getServer().getPlayerExact(cs.getName());
                Profile _profile = new Profile(plugin, pl);
                String skill = _profile.getHabilityForRank().toUpperCase();
                String gender = _profile.getGender();
                Boolean sucess=false;
                String promoted="";
                
                if(plugin.TagSystem) {
                  sucess = plugin.RankUp.tryRankUpWithoutGroup(pl, skill, gender);
                } else {
                  promoted =  plugin.RankUp.tryRankUp(pl, skill, gender);
                }
                
                if ("promoted".equals(promoted) || sucess) {
                    cs.sendMessage("-----------------------------------------------------");
                    //cs.sendMessage(plugin.parseColor(plugin.MSucess));
                    cs.sendMessage(ChatTools.getAltColor(plugin.generalMessages) + plugin.MSucess);
                    cs.sendMessage("-----------------------------------------------------");
                } else {
                	if (cmd.equalsIgnoreCase("rank") && "failed".equals(promoted)) {		// zrocweb
	                    cs.sendMessage("-----------------------------------------------------");
	                    //cs.sendMessage(plugin.parseColor(plugin.MFail));
	                    cs.sendMessage(ChatTools.getAltColor(plugin.generalMessages) + plugin.MFail);
	                    cs.sendMessage("-----------------------------------------------------");
                	} else if ("already".equals(promoted)) {
	                    cs.sendMessage("-----------------------------------------------------");
	                    //cs.sendMessage("No further promotions available for this ability!");
	                    cs.sendMessage(ChatTools.getAltColor(plugin.generalMessages) + "No further promotions available for this ability");
	                    cs.sendMessage("-----------------------------------------------------");
                	} else if ("ignore".equals(promoted)) {
	                    cs.sendMessage("-----------------------------------------------------");
	                    //cs.sendMessage("Promotions are ignored for this player and/or group");
	                    cs.sendMessage(ChatTools.getAltColor(plugin.generalMessages) + "Promotions are ignored for this player and/or group");
	                    cs.sendMessage("-----------------------------------------------------");
                	}
                }
                plugin.Playertime.put(cs.getName(),plugin.getCurrentMilli());
                return true;
             } else {
                cs.sendMessage(ChatColor.RED + "Command attempted too soon!");
                return false;
             }
    }
    
    public boolean RankOnHability(CommandSender cs,String Hability) {
        Player _player = (Player)cs;
        if(!plugin.permission.has(_player.getWorld(), _player.getName(),"mru.hability")) return false;
        Profile _profile = new Profile(plugin,_player);
        boolean exists = false;
        for(String key:plugin.isHabilityRankExist.keySet()) {
           if(Hability.equalsIgnoreCase(key))
               exists = plugin.isHabilityRankExist.get(key);
        }
        if(!exists) {
            cs.sendMessage("-----------------------------------------------------");
            cs.sendMessage(plugin.parseColor(plugin.NotFound));
            cs.sendMessage("-----------------------------------------------------");
            return false;
        }
        return _profile.setHabilityForRank(Hability);
    }
    
    public boolean Help(CommandSender cs) {
        Player _player = (Player)cs;
        cs.sendMessage("\n"+ChatTools.formatTitle("RANKING HELP",  plugin.titleHeader, plugin.titleHeaderLineColor, plugin.titleHeaderTextColor, plugin.titleHeaderAltColorBold,
        			                                          plugin.titleHeaderAltColor, plugin.titleHeaderAltColorBold));
        cs.sendMessage(plugin.parseColor("&6/mru show &7Shows your current ranking info."));
        cs.sendMessage(plugin.parseColor("&6/mru rank &7Execute Rankup Promotion??"));
        cs.sendMessage(plugin.parseColor("&6/mru male &7Set your Gender to Male"));
        cs.sendMessage(plugin.parseColor("&6/mru female &7Set your Gender to Female"));
        if(plugin.permission.has(_player.getWorld(), _player.getName(),"mru.hability")) {
            cs.sendMessage(plugin.parseColor("&6/mru hab &7List all Available Abilities"));
            cs.sendMessage(plugin.parseColor("&6/mru hab <ability> &7Set your Rank Base Ability to <ability>"));
        }
        if(plugin.permission.has(_player.getWorld(), _player.getName(),"mru.reload")) {
            cs.sendMessage(plugin.parseColor("&6/mru pinfo &7Toggle Next Promotion Info. &e" + (plugin.displayNextPromo ? "OFF" : "ON")));
        	cs.sendMessage(plugin.parseColor("&6/mru reload &7Reload the all configs..."));
        }
        cs.sendMessage(ChatTools.getAltColor(plugin.titleFooterLineColor) + plugin.titleFooter);
        
        return true;
    }
    
    public boolean setGender(CommandSender cs, String gender) { 
        Player _player = (Player)cs;
        Profile _profile = new Profile(plugin, _player);
        _profile.setGender(gender);
	    cs.sendMessage("\n"+ChatTools.formatTitle("GENDER SELECTED",  plugin.titleHeader, plugin.titleHeaderLineColor, plugin.titleHeaderTextColor, plugin.titleHeaderAltColorBold,	plugin.titleHeaderAltColor, plugin.titleHeaderAltColorBold));
	    cs.sendMessage(ChatTools.getAltColor(plugin.generalMessages) + plugin.setGender.replace("%gender%", gender));
        cs.sendMessage(ChatTools.getAltColor(plugin.titleFooterLineColor) + plugin.titleFooter);
        return true;
    }
    
    // zrocweb: TODO: detail out each level/group for the skills rank bases displayed below...
    public boolean ListHability(CommandSender cs) {
    	cs.sendMessage("\n"+ChatTools.formatTitle(plugin.baseRanksListing,  plugin.titleHeader, plugin.titleHeaderLineColor, plugin.titleHeaderTextColor, plugin.titleHeaderAltColorBold,
                															 plugin.titleHeaderAltColor, plugin.titleHeaderAltColorBold));    	
        for(String key:plugin.isHabilityRankExist.keySet()) {
           if(plugin.isHabilityRankExist.get(key)) {
               cs.sendMessage(plugin.parseColor("&6" + key + " &3 - Available for Rank Base"));
           }else{
               cs.sendMessage(plugin.parseColor("&6" + key + " &7 - Not Available for Rank Base"));
           }
        }
        cs.sendMessage(ChatTools.getAltColor(plugin.titleFooterLineColor) + plugin.titleFooter);
        return true;
    }
}
