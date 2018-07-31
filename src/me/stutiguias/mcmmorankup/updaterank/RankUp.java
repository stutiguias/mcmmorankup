package me.stutiguias.mcmmorankup.updaterank;

import me.stutiguias.mcmmorankup.Mcmmorankup;
import me.stutiguias.mcmmorankup.Util;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import static me.stutiguias.mcmmorankup.Mcmmorankup.Message;
import me.stutiguias.mcmmorankup.apimcmmo.McMMOApi;

import me.stutiguias.mcmmorankup.profile.Profile;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class RankUp extends Util {

    private static final HashMap<String, String> infoSettings = new HashMap<>();
    private Profile profile;

    public RankUp(Mcmmorankup plugin) {
        super(plugin);
        if (plugin.getServer().getPluginManager().getPlugin("mcMMO") != null) {
            Mcmmorankup.logger.log(Level.INFO, "{0} mcMMO has been hooked", new Object[]{Mcmmorankup.logPrefix});
        }

    }
    
    public void SendFormatMessage(String msg) {
        SendMessage(profile.player,msg);
    }
    
    public String TryRankUp(Player player, String skill, String gender) {
        try {
            if (plugin.isIgnored(player)) return "ignore";

            profile = new Profile(plugin, player);
            
            if(!skill.equals("CUSTOM") && ( !gender.toLowerCase().equals("male") && !gender.toLowerCase().equals("female") )) {
                gender = "Male";
                profile.SetGender(gender);
            }
            
            boolean broadCast = false;

            String rank;		
            String nextGroup = "";				              
            int nextLevel = 0;				
            int level;					

            boolean demote = false;			
            boolean promote = false;							
            boolean maxLvl = false;					
            boolean hasPurchased = false;	
            
            int purchaseLevel = 0;

            String rankNow = plugin.TagSystem ? profile.GetTag() : plugin.permission.getPrimaryGroup(player);
            
            if (profile.GetPurchasedRanks().size() > 0 && plugin.BuyRank.GetLastPurchasedSkill( profile.GetPurchasedRanks() ).equalsIgnoreCase(skill)) {
                hasPurchased = true;	
                String lastPurchasedRank = plugin.BuyRank.getLastPurchasedRank( profile.GetPurchasedRanks() );		
                purchaseLevel = plugin.GetRankLevel(skill, gender, lastPurchasedRank);
            }
            
            int playerSkillLevel = plugin.GetSkillLevel(player, skill);
            int playerLevelNow = plugin.GetRankLevel(skill, gender, rankNow);
            
            String updatedRank = rankNow;
            int updatedLevel = 0;
            boolean playerLevelUpdate = false;

            for (String entry : plugin.RankUpConfig.get(skill).get(gender)) {
                String[] levelRank = entry.split(",");

                level = Integer.parseInt(levelRank[0]);
                rank = levelRank[1];

                if (playerSkillLevel >= level) {

                    demote = level < playerLevelNow;

                    if(demote && plugin.hasPermission(player, "mru.exemptdemotions") ) continue;
                    if(hasPurchased && level < purchaseLevel && !plugin.AllowBuyRankDemotions ) continue;

                    maxLvl = plugin.isRankMaxLevel(skill, gender, level);
                    
                    if(!demote) promote = true;
                    
                    updatedRank = rank;
                    updatedLevel = level;
                    playerLevelUpdate = true;
                    
                }else if(!maxLvl && level > updatedLevel && nextLevel == 0) {
                    nextGroup = rank;
                    nextLevel = level;
                    if(playerLevelUpdate) break;
                }
            }

            String title;
                     

            if (!updatedRank.equalsIgnoreCase(rankNow)) {
                
                if (!plugin.hasPermission(player, "mru.rankup")) return null;
                title = promote ? Message.PromoteTitle : demote ? Message.DemoteTitle : Message.RankInfoTitle;
                
                if (plugin.globalBroadcastFeed) {					
                    broadCast = profile.GetPlayerGlobalFeed();
                }
            
                if (plugin.TagSystem) {
                    ChangeTag(updatedRank, skill, broadCast, demote);
                } else {
                    ChangeGroup(updatedRank, skill, broadCast, demote);
                }
                
            }else{
                title = Message.RankInfoTitle;
                promote = false;
                demote = false;
            }		

            if (plugin.playerBroadcastFeed) {
                broadCast = profile.GetPlayerRankupFeed();
            }

            if (broadCast) {
                infoSettings.put("title", title);
                infoSettings.put("promote", (promote ? "t" : "f"));
                infoSettings.put("skill", skill);
                infoSettings.put("playerGroup", updatedRank);
                infoSettings.put("nGroup", nextGroup);
                infoSettings.put("nLevel", String.valueOf(nextLevel));
                infoSettings.put("skilllevel", String.valueOf(playerSkillLevel));
                infoSettings.put("maxLvl", (maxLvl ? "t" : "f"));
                infoSettings.put("xpNeeded", String.valueOf(McMMOApi.getXpToNextLevel(player, skill)));
                infoSettings.put("cXp", String.valueOf(McMMOApi.getXp(player, skill)));

                ShowRankingInfo(player);

                infoSettings.clear();
            }

            if (promote || demote) {
                return promote ? "promoted" : "demoted";
            }
            return "fail";
            
        } catch (NumberFormatException ex) {
            Mcmmorankup.logger.log(Level.WARNING, "-=tryRankUp=- Error trying to rank up {0}", ex.getMessage());
            ex.printStackTrace();
            return "error";
        }
    }

    public void ShowRankingInfo(Player player) {

        String title = infoSettings.get("title");
        String skill = infoSettings.get("skill");
        String playerGroup = infoSettings.get("playerGroup");
        String nGroup = infoSettings.get("nGroup");

        String line;

        String maxAchieved;
        String promoteDemote;

        HashMap<Integer, String> rankInfo = new HashMap<>();		
        
        boolean promote =  infoSettings.get("promote").equalsIgnoreCase("t");
        boolean maxLvl =   infoSettings.get("maxLvl").equalsIgnoreCase("t");

        int nLevel = Integer.valueOf(infoSettings.get("nLevel"));
        int SkillLevel = Integer.valueOf(infoSettings.get("skilllevel"));

        try {
            int lni = 1;
            line = Message.RankInfoLine1.replaceAll("%ability%", skill);
            rankInfo.put(lni, line);
            lni++;
            
            line = Message.RankInfoLine2.replaceAll("%skilllevel%", String.valueOf(SkillLevel)).replaceAll("%rankline%", playerGroup);
            rankInfo.put(lni, line);
            lni++;
            
            if(skill.equalsIgnoreCase("CUSTOM")) nLevel = nLevel - 1;
            line = Message.RankInfoLine3.replaceAll("%nLevel%", String.valueOf(nLevel + 1)).replaceAll("%nRank%", nGroup);
            rankInfo.put(lni, line);
            lni++;
            
            maxAchieved = Message.RankInfoMax;
            maxAchieved = maxAchieved.replaceAll("%ability%", skill);

            promoteDemote = Message.RankPromoteDemote;
            promoteDemote = promoteDemote.replaceAll("%promotedemote%", promote ? Message.Promote : Message.Demote).replaceAll("%pRank%", playerGroup);
            
            if(skill.equalsIgnoreCase("CUSTOM")){
                Map<String,String> returnInfo = ShowRequirementInfo(player);
                lni++;
                rankInfo.put(lni,"REQUIREMENT ( NAME - NOW / NEED IT )");
                lni++;
                for(String returnInfoName:returnInfo.keySet()){
                    String requimentAmount = returnInfo.get(returnInfoName);
                    rankInfo.put(lni, returnInfoName + " - " + requimentAmount);
                    lni++;
                }
            }
            
            SendFormatMessage(Message.MessageSeparator);
            SendFormatMessage(title);

            for (int ln = 1; ln < lni; ln++) {
                
                String info = "";
                
                switch(ln) {
                    case 1:
                    case 2:
                        info = rankInfo.get(ln);
                        break;
                    case 3:
                        if(maxLvl)
                            info = maxAchieved;
                        else if (plugin.displayNextPromo) 
                            info = rankInfo.get(ln);                        
                        break;
                    case 4:
                        if( promote )
                            info = promoteDemote;
                        break;
                    default:
                        info = rankInfo.get(ln);
                        break;
                }
                
                if (!info.isEmpty()) {
                    SendFormatMessage(info);
                }
            }
            
            SendFormatMessage(Message.MessageSeparator);
            
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        } finally {
            rankInfo.clear();
        }
    }

    private void ChangeTag(String promoteTag, String skill, boolean bCast, boolean demote) {
        profile.SetTag(promoteTag);
        if (bCast) {
            BrcstMsg(Message.BroadcastRankupTitle);
            BrcstMsg(Message.GeneralMessages + BroadcastMessage(promoteTag, skill, demote));
            BrcstMsg(Message.MessageSeparator);
        }
    }

    private boolean ChangeGroup(String newgroup, String skill, boolean bCast, boolean demote) {

        String groupnow = plugin.permission.getPrimaryGroup(profile.player);
        boolean state;
        
        profile.SetTag(newgroup);
        
        if (plugin.RemoveOnlyPluginGroup) {
            RemoveGroup(groupnow, profile.player);
        } else {
            String[] playergroups = plugin.permission.getPlayerGroups(profile.player);
            for (String playergroup : playergroups) {
                RemoveGroup(playergroup, profile.player);
            }
        }
        state = AddGroup(newgroup, profile.player);
        
        if (bCast && state && !groupnow.equalsIgnoreCase(newgroup)) {
            BrcstMsg(Message.BroadcastRankupTitle);
            BrcstMsg(Message.GeneralMessages + BroadcastMessage(newgroup, skill, demote));
            BrcstMsg(Message.MessageSeparator);
        }

        return state;
    }

    public void RemoveGroup(String groupnow,Player player) {
        if(plugin.PerWorldPermission) {
            plugin.permission.playerRemoveGroup(player.getWorld(), player.getName(), groupnow);
        }else{
            plugin.permission.playerRemoveGroup(player, groupnow);
        }
    }
    
    public boolean AddGroup(String group,Player player) {
        if(plugin.PerWorldPermission) {
            return plugin.permission.playerAddGroup(player.getWorld(), player.getName(), group);
        }
        return plugin.permission.playerAddGroup(player, group);
    }
    
    private String BroadcastMessage(String group, String skill, boolean demote) {
        if (plugin.UseAlternativeBroadcast) {
            try {
                HashMap<String, String> broadCast = plugin.BroadCast.get(skill);
                String bc = broadCast.get(group);
                return Message.Promotion.replace("%player%", profile.player.getName()).replace("%promotedemote%",demote ? Message.Demote : Message.Promote).replace("%group%", bc);
            } catch (Exception ex) {
                Mcmmorankup.logger.log(Level.WARNING, "Error trying to broadcast Alternative Messaging {0}", ex.getMessage());
                ex.printStackTrace();
                return "Error trying to Broadcast Alternative Messaging";
            }
        } else {
            return Message.Promotion.replace("%player%", profile.player.getName()).replace("%promotedemote%", demote ? Message.Demote : Message.Promote).replace("%group%", group);
        }
    }
    
    public Map<String,String> ShowRequirementInfo(Player player){
        Map<String,String> returnInfo = new HashMap<>();
        for(int level=0;level<=plugin.CustomRequirements.size();level++){
            Map<String,String> requirements = plugin.CustomRequirements.get(String.valueOf(level));
            int amountreq = requirements.size();
            int playerpass = 0;
            returnInfo = new HashMap<>();
            for(String requirementName:requirements.keySet()){
                int requirementAmountint = 0;
                String requimentAmountstring = "";
                if(requirementName.equalsIgnoreCase("world") || requirementName.equalsIgnoreCase("regionworldguard")){
                    requimentAmountstring = requirements.get(requirementName);
                }else{
                    requirementAmountint = Integer.parseInt(requirements.get(requirementName));
                }
                playerpass = CheckRequerimentLevel(requirementName, player, requirementAmountint, requimentAmountstring, playerpass);
                if(requirementAmountint != 0){
                    returnInfo.put(requirementName.toUpperCase(), "&c" + GetRequimentNowPlayer(requirementName,player) +" &e/&c "+ String.valueOf(requirementAmountint));
                }else{
                    returnInfo.put(requirementName.toUpperCase(), "&c" + GetRequimentNowPlayer(requirementName,player) +" &e/&c  "+ requimentAmountstring);
                }
                
            }
            if(playerpass < amountreq){
                break;
            }
        }
        return returnInfo;
    }
    
    private int CheckRequerimentLevel(String requirementName, Player player, int requirementAmountint,String requimentAmountString, int passhowmany) {
        if(requirementName.equalsIgnoreCase("Powerlevel") && McMMOApi.getPowerLevel(player) > requirementAmountint) passhowmany++;
        if(requirementName.equalsIgnoreCase("Fishing") && McMMOApi.getSkillLevel(player, "Fishing") > requirementAmountint) passhowmany++;
        if(requirementName.equalsIgnoreCase("Axes") && McMMOApi.getSkillLevel(player, "Axes") > requirementAmountint) passhowmany++;
        if(requirementName.equalsIgnoreCase("Acrobatics") && McMMOApi.getSkillLevel(player, "Acrobatics") > requirementAmountint) passhowmany++;
        if(requirementName.equalsIgnoreCase("Archery") && McMMOApi.getSkillLevel(player, "Archery") > requirementAmountint) passhowmany++;
        if(requirementName.equalsIgnoreCase("Excavation") && McMMOApi.getSkillLevel(player, "Excavation") > requirementAmountint) passhowmany++;
        if(requirementName.equalsIgnoreCase("Herbalism") && McMMOApi.getSkillLevel(player, "Herbalism") > requirementAmountint) passhowmany++;
        if(requirementName.equalsIgnoreCase("Mining") && McMMOApi.getSkillLevel(player, "Mining") > requirementAmountint) passhowmany++;
        if(requirementName.equalsIgnoreCase("Repair") && McMMOApi.getSkillLevel(player, "Repair") > requirementAmountint) passhowmany++;
        if(requirementName.equalsIgnoreCase("Smelting") && McMMOApi.getSkillLevel(player, "Smelting") > requirementAmountint) passhowmany++;
        if(requirementName.equalsIgnoreCase("Swords") && McMMOApi.getSkillLevel(player, "Swords") > requirementAmountint) passhowmany++;
        if(requirementName.equalsIgnoreCase("Taming") && McMMOApi.getSkillLevel(player, "Taming") > requirementAmountint) passhowmany++;
        if(requirementName.equalsIgnoreCase("Unarmed") && McMMOApi.getSkillLevel(player, "Unarmed") > requirementAmountint) passhowmany++;
        if(requirementName.equalsIgnoreCase("Woodcutting") && McMMOApi.getSkillLevel(player, "Woodcutting") > requirementAmountint) passhowmany++;
        
        if(requirementName.equalsIgnoreCase("Money")) {
           double balance = plugin.economy.getBalance(player);
           if(balance >= requirementAmountint) passhowmany++;
        }
        
        Profile profile = new Profile(plugin, player);
        for(EntityType type:EntityType.values()){
            if(requirementName.equalsIgnoreCase(type.name()) && profile.GetMOBKILLED(type.name()) > requirementAmountint) passhowmany++;
        }
        
        if(requirementName.equalsIgnoreCase("PLAYERKILLED")){
            if(profile.GetPlayerKILLED() >= requirementAmountint) passhowmany++;
        }
        
        if(requirementName.equalsIgnoreCase("WORLD")) {
            if(player.getWorld().getName().equalsIgnoreCase(requimentAmountString)) passhowmany++;
        }
        
        if(requirementName.equalsIgnoreCase("REGIONWORLDGUARD")) {
            Location loc = player.getLocation();
            com.sk89q.worldguard.bukkit.RegionContainer container = plugin.getWorldGuard().getRegionContainer();
            com.sk89q.worldguard.protection.managers.RegionManager regions = container.get(loc.getWorld());
            // Check to make sure that "regions" is not null
            com.sk89q.worldguard.protection.ApplicableRegionSet set = regions.getApplicableRegions(com.sk89q.worldguard.bukkit.BukkitUtil.toVector(loc));
            for (com.sk89q.worldguard.protection.regions.ProtectedRegion region : set) {
                // Do something with each region
                if(region.getId().equalsIgnoreCase(requimentAmountString)) passhowmany++;
            }
        }
        
        return passhowmany;
    }
    
    private String GetRequimentNowPlayer(String requirementName, Player player) {
        if(requirementName.equalsIgnoreCase("Powerlevel")) return String.valueOf(McMMOApi.getPowerLevel(player));
        if(requirementName.equalsIgnoreCase("Fishing")) return String.valueOf(McMMOApi.getSkillLevel(player, "Fishing"));
        if(requirementName.equalsIgnoreCase("Axes")) return String.valueOf(McMMOApi.getSkillLevel(player, "Axes"));
        if(requirementName.equalsIgnoreCase("Acrobatics")) return String.valueOf(McMMOApi.getSkillLevel(player, "Acrobatics"));
        if(requirementName.equalsIgnoreCase("Archery")) return String.valueOf(McMMOApi.getSkillLevel(player, "Archery"));
        if(requirementName.equalsIgnoreCase("Excavation")) return String.valueOf(McMMOApi.getSkillLevel(player, "Excavation"));
        if(requirementName.equalsIgnoreCase("Herbalism")) return String.valueOf(McMMOApi.getSkillLevel(player, "Herbalism"));
        if(requirementName.equalsIgnoreCase("Mining")) return String.valueOf(McMMOApi.getSkillLevel(player, "Mining"));
        if(requirementName.equalsIgnoreCase("Repair")) return String.valueOf(McMMOApi.getSkillLevel(player, "Repair"));
        if(requirementName.equalsIgnoreCase("Smelting")) return String.valueOf(McMMOApi.getSkillLevel(player, "Smelting"));
        if(requirementName.equalsIgnoreCase("Swords")) return String.valueOf(McMMOApi.getSkillLevel(player, "Swords"));
        if(requirementName.equalsIgnoreCase("Taming")) return String.valueOf(McMMOApi.getSkillLevel(player, "Taming"));
        if(requirementName.equalsIgnoreCase("Unarmed")) return String.valueOf(McMMOApi.getSkillLevel(player, "Unarmed"));
        if(requirementName.equalsIgnoreCase("Woodcutting")) return String.valueOf(McMMOApi.getSkillLevel(player, "Woodcutting"));
        if(requirementName.equalsIgnoreCase("Money")) return String.valueOf(plugin.economy.getBalance(player));
        
        Profile profile = new Profile(plugin, player);
        for(EntityType type:EntityType.values()){
            if(requirementName.equalsIgnoreCase(type.name())) return String.valueOf(profile.GetMOBKILLED(type.name()));
        }
        
        if(requirementName.equalsIgnoreCase("PLAYERKILLED")){
            return String.valueOf(profile.GetPlayerKILLED());
        }
        
        if(requirementName.equalsIgnoreCase("WORLD")) {
            return player.getWorld().getName();
        }
        
        if(requirementName.equalsIgnoreCase("REGIONWORLDGUARD")) {
            Location loc = player.getLocation();
            com.sk89q.worldguard.bukkit.RegionContainer container = plugin.getWorldGuard().getRegionContainer();
            com.sk89q.worldguard.protection.managers.RegionManager regions = container.get(loc.getWorld());
            // Check to make sure that "regions" is not null
            com.sk89q.worldguard.protection.ApplicableRegionSet set = regions.getApplicableRegions(com.sk89q.worldguard.bukkit.BukkitUtil.toVector(loc));
            for (com.sk89q.worldguard.protection.regions.ProtectedRegion region : set) {
                // Do something with each region
                return region.getId();
            }
        }
        
        return "";
    }
}
