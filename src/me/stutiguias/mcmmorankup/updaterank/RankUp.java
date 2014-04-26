package me.stutiguias.mcmmorankup.updaterank;

import me.stutiguias.mcmmorankup.Mcmmorankup;
import me.stutiguias.mcmmorankup.Util;
import java.util.HashMap;
import java.util.logging.Level;
import static me.stutiguias.mcmmorankup.Mcmmorankup.Message;
import me.stutiguias.mcmmorankup.apimcmmo.McMMOApi;

import me.stutiguias.mcmmorankup.profile.Profile;
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
                purchaseLevel = plugin.GetRankStartLevel(skill, gender, lastPurchasedRank);
            }
            
            int playerSkillLevel = plugin.GetSkillLevel(player, skill);
            int StartLevel = plugin.GetRankStartLevel(skill, gender, rankNow);
            
            String updatedRank = rankNow;
            int updatedLevel = 0;
            boolean willbreak = false;

            for (String entry : plugin.RankUpConfig.get(skill).get(gender)) {
                String[] levelRank = entry.split(",");

                level = Integer.parseInt(levelRank[0]);
                rank = levelRank[1];

                if (playerSkillLevel >= level) {

                    demote = level < StartLevel;

                    if(demote && plugin.hasPermission(player, "mru.exemptdemotions") ) continue;
                    if(hasPurchased && level < purchaseLevel && !plugin.AllowBuyRankDemotions ) continue;

                    maxLvl = plugin.isRankMaxLevel(skill, gender, level);
                    
                    if(!demote) promote = true;
                    updatedRank = rank;
                    updatedLevel = level;
                    willbreak = true;
                }else if(!maxLvl && level > updatedLevel && nextLevel == 0) {
                    nextGroup = rank;
                    nextLevel = level;
                    if(willbreak) break;
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
            
            line = Message.RankInfoLine1.replaceAll("%ability%", skill);
            rankInfo.put(1, line);
            
            line = Message.RankInfoLine2.replaceAll("%skilllevel%", String.valueOf(SkillLevel)).replaceAll("%rankline%", playerGroup);
            rankInfo.put(2, line);

            line = Message.RankInfoLine3.replaceAll("%nLevel%", String.valueOf(nLevel + 1)).replaceAll("%nRank%", nGroup);
            rankInfo.put(3, line);

            maxAchieved = Message.RankInfoMax;
            maxAchieved = maxAchieved.replaceAll("%ability%", skill);

            promoteDemote = Message.RankPromoteDemote;
            promoteDemote = promoteDemote.replaceAll("%promotedemote%", promote ? Message.Promote : Message.Demote).replaceAll("%pRank%", playerGroup);
            
            SendFormatMessage(Message.MessageSeparator);
            SendFormatMessage(title);

            for (int ln = 1; ln <= 5; ln++) {
                
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
            plugin.permission.playerRemoveGroup((String)null, player.getName(), groupnow);
        }
    }
    
    public boolean AddGroup(String group,Player player) {
        if(plugin.PerWorldPermission) {
            return plugin.permission.playerAddGroup(player.getWorld(), player.getName(), group);
        }
        return plugin.permission.playerAddGroup((String)null, player.getName(), group);
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
}
