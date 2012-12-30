package me.stutiguias.apimcmmo;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.util.Users;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;

import me.stutiguias.mcmmorankup.ChatTools;
import me.stutiguias.mcmmorankup.Mcmmorankup;
import me.stutiguias.profile.Profile;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class RankUp {
    
    private static Mcmmorankup plugin;
    
    public RankUp(Mcmmorankup instance)
    {
        plugin = instance;
        Plugin pl = plugin.getServer().getPluginManager().getPlugin("mcMMO");
        if(pl != null) {
            Mcmmorankup.logger.log(Level.INFO, plugin.logPrefix + " mcMMO found--- hooking");
        }
    }
    
    public String tryRankUp(Player player, String skill, String gender, String cmd) {
        try{
        	  if(PlayerToIgnore(player)) return "ignore";
              if(GroupToIgnore(player)) return "ignore";

              PlayerProfile _McMMOPlayerProfile =  Users.getProfile(player);
              Integer SkillLevel;
              
              if (skill.equalsIgnoreCase("POWERLEVEL")) {
                  SkillLevel = getPowerLevel(player);
              } else {
                  SkillLevel = _McMMOPlayerProfile.getSkillLevel(getSkillType(skill));
              }
              
              // zrocweb:
              String title = "";
              String msg = "";
              String grp = "";
              String pGroup = "";
              String nGroup = "";
              Integer nLevel = 0;
              Integer lvl = 0;
              String retM = "";

              Boolean promote = false;
              Boolean dummy = false;
              Boolean maxLvl = false;
              Boolean promoteInto=false;
              
              String groupNow = plugin.permission.getPrimaryGroup(player);
              
              for (Iterator<String> it = plugin.RankUpConfig.get(skill).get(gender).iterator(); it.hasNext();) {
                  String entry = it.next();
                  String[] levelGroup = entry.split(",");
                  
                  // zrocweb:
                  lvl = Integer.parseInt(levelGroup[0]);
                  grp = levelGroup[1];

                  if(lvl < SkillLevel) {                	  
                	  if(SkillLevel >= lvl) {
	        			  if(!grp.equalsIgnoreCase(groupNow)) {
	        				  pGroup = grp;
	        				  promote = true;
	        			  } else {
	        				  pGroup = groupNow;
	        				  promote = false;
	        			  }
                	  } else {
                		  pGroup = groupNow;
                		  promote = false;
                	  }
                  } else {            	  
                	  if(SkillLevel <= lvl && nGroup.equalsIgnoreCase("")) {
                		  nGroup = grp;
                		  nLevel = lvl;
                	  }                	  
                  }                                                  
                  //player.sendMessage("cG:" + groupNow + "(" + SkillLevel + ") | tG: " + grp + "(" + lvl + ") | pG:" + pGroup + " | nG:" + nGroup + "(" + (nLevel+1) +")");
              }
              
              if(promote && nGroup.equalsIgnoreCase("") && grp.equalsIgnoreCase(groupNow)) {
            	  // last level has been reached within this ability and has been promoted to this level
            	  maxLvl=true;
              } else if (promote && nGroup.equalsIgnoreCase("")) {
            	  if(cmd.equalsIgnoreCase("rank")) {
            		  maxLvl=true;
            	  } else {
            		  promoteInto=true;
            	  }            	  
              } else if (!promote && nGroup.equalsIgnoreCase("")) {
            	  maxLvl=true;
              }
                            
              if (promote && cmd.equalsIgnoreCase("rank")) {
              	  title = plugin.promoteTitle;
            	  if(!pGroup.equalsIgnoreCase(groupNow)) {
            		  dummy = ChangeGroup(player, pGroup, skill);
            	  }
              } else {            	  
            	  title = plugin.rankinfoTitle;
            	  if(maxLvl && cmd.equalsIgnoreCase("rank") && pGroup.equalsIgnoreCase(groupNow)) retM="already";
              }
              
              // Color Formatting init
              String ht = ChatTools.getAltColor(plugin.titleHeaderTextColor);
              String rt = ChatTools.getAltColor(plugin.rankinfoTextColor);
              String ra = ChatTools.getAltColor(plugin.rankinfoAltColor);
              String pt = ChatTools.getAltColor(plugin.promoteTextColor);
              String pp = ChatTools.getAltColor(plugin.promotePreTextColor);
              Boolean pb = plugin.promoteTextBold;

        	  msg = msg + rt + "Base Ability: " + ra + skill + rt + 
        			" @ Level: " + ra + SkillLevel + rt + " (" + 
        			(cmd.equalsIgnoreCase("show") ? (promote ? ra + pp + ChatColor.BOLD + " * " + ra + groupNow + rt + ")" : ra + groupNow+rt+")") : ra + groupNow+rt+")");         	  

        	  msg = msg + pp + (cmd.equalsIgnoreCase("show") ? (promote ? "\n* use " + ChatColor.YELLOW + "/mru rank" + pp + " to promote ability to: " + ChatColor.YELLOW + pGroup : "") :
        		          (promote ? "" + pp + (pb ? ChatColor.BOLD : ChatColor.RESET) + "\nPromoted to: " + pt + (pb ? ChatColor.BOLD : ChatColor.RESET) + pGroup : ""));
        	  
        	  msg = msg + (plugin.displayNextPromo ? "\n" : "") + ra + 
        			      ((maxLvl ? "Ability (" + ht + skill + ra + ") Achieved!\n" : 
        		            (promoteInto ? ra + "Your next promotion will achieve greatness in this ability!" :
        		              (plugin.displayNextPromo ? rt + "Next Promotion @ Level: " + ra + (nLevel+1) + rt + " (" : ""))) + ra +
        		               (maxLvl ? "Use " + ht + "/mru hab " + ChatColor.WHITE + "<" + ht + "ability" + ChatColor.WHITE + ">" + ra + " to select a new Ability" :
        		             (plugin.displayNextPromo ? nGroup + rt+")" : ""))) + (plugin.displayNextPromo ? "\n" : "");              
        	  
        	  /*
        	  msg = msg + "\n" + ra + 
    			      ((maxLvl ? "Ability (" + ht + skill + ra + ") Achieved!\n" : 
    		          (promoteInto ? ra + "Your next promotion will achieve greatness in this ability!" : rt + "Next Promotion @ Level: " + ra + (nLevel+1) +
    		          rt + " (")) + ra + (maxLvl ? "Use " + ht + "/mru hab " +
    		          ChatColor.WHITE + "<" + ht + "ability" + ChatColor.WHITE + ">" + ra + " to select a new Ability" : nGroup + rt+")")) + "\n";
        	  */
        	  
        	  
        	  
        	  /* before color/formatting changes
        	  msg = msg + ChatColor.AQUA + "Base Ability: " + ChatColor.DARK_AQUA + skill + ChatColor.AQUA + 
        			" @ Level: " + ChatColor.DARK_AQUA + SkillLevel + ChatColor.AQUA + " - " + 
        			(cmd.equalsIgnoreCase("show") ? (promote ? ChatColor.DARK_AQUA + groupNow + ChatColor.YELLOW + "(" + ChatColor.DARK_AQUA + " * promotable " + ChatColor.YELLOW + ")" : ChatColor.DARK_AQUA + groupNow) : ChatColor.DARK_AQUA + groupNow);         	  

        	  msg = msg + ChatColor.DARK_PURPLE + (cmd.equalsIgnoreCase("show") ? (promote ? "\n* use " + ChatColor.YELLOW + "/mru rank" + ChatColor.DARK_PURPLE + " to promote ability to: " + ChatColor.YELLOW + pGroup : "") :
        		          (promote ? "" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "\nPromoted to: " + ChatColor.YELLOW + ChatColor.BOLD + pGroup : ""));
        	  
        	  msg = msg + "\n" + ChatColor.DARK_AQUA + 
        			      ((maxLvl ? "Ability (" + ChatColor.YELLOW + skill + ChatColor.DARK_AQUA + ") Achieved!\n" : 
        		          (promoteInto ? ChatColor.DARK_AQUA + "Your next promotion will achieve greatness in this ability!" : ChatColor.AQUA + "Next Promotion @ Level: " + ChatColor.DARK_AQUA + nLevel +
        		          ChatColor.AQUA + " ( ")) + ChatColor.DARK_AQUA + (maxLvl ? "Use " + ChatColor.YELLOW + "/mru hab " +
        		          ChatColor.WHITE + "<" + ChatColor.YELLOW + "ability" + ChatColor.WHITE + ">" + ChatColor.DARK_AQUA + " to select a new Ability" : nGroup + ")")) + "\n";
        	  /******************************************************************* */
              
        	  player.sendMessage("\n" + ChatTools.formatTitle(title, plugin.titleHeader, plugin.titleHeaderLineColor, plugin.titleHeaderTextColor, plugin.titleHeaderAltColorBold,
        			                                                 plugin.titleHeaderAltColor, plugin.titleHeaderAltColorBold));
              player.sendMessage(msg);
              player.sendMessage(ChatTools.getAltColor(plugin.titleFooterLineColor) + plugin.titleFooter);
              
              
              if (promote) {
            	  if(dummy) {
            		  return "promoted";
            	  } else {
            		  return "";
            	  }
              } else if (!retM.equalsIgnoreCase("")) {
            	  return retM;
              }

        } catch (NullPointerException ex) {
              Mcmmorankup.logger.log(Level.WARNING,"{tryRankUp} - Error trying to rank up " + ex.getMessage());
              return "error";
        } catch (Exception ex) {
              Mcmmorankup.logger.log(Level.WARNING,"{tryRankUp} - Error trying to rank up " + ex.getMessage());
              ex.printStackTrace();
              return "error";
        }
        return "failed";
      }

    
    /* Original
    public Boolean tryRankUp(Player player,String skill,String gender) {
      try{
            if(PlayerToIgnore(player)) return false;
            if(GroupToIgnore(player)) return false;
            PlayerProfile _McMMOPlayerProfile =  Users.getProfile(player);
            Integer SkillLevel;
            if(skill.equalsIgnoreCase("POWERLEVEL")) {
                SkillLevel = getPowerLevel(player);
            }else{
                SkillLevel = _McMMOPlayerProfile.getSkillLevel(getSkillType(skill));
            }
            String group = "";
            String groupNow = plugin.permission.getPrimaryGroup(player);
            for (Iterator<String> it = plugin.RankUpConfig.get(skill).get(gender).iterator(); it.hasNext();) {
                String entry = it.next();
                String[] levelGroup = entry.split(",");
                if(Integer.parseInt(levelGroup[0]) < SkillLevel){
                    group = levelGroup[1]; 
                }
            }
            if(group.equalsIgnoreCase(groupNow)) return false;
            if(!group.equalsIgnoreCase("")) return ChangeGroup(player,group,skill);
      }catch(NullPointerException ex) {
            Mcmmorankup.logger.log(Level.WARNING,"{tryRankUp} - Error trying to rank up " + ex.getMessage());
            return false;
      }catch(Exception ex) {
            Mcmmorankup.logger.log(Level.WARNING,"{tryRankUp} - Error trying to rank up " + ex.getMessage());
            ex.printStackTrace();
            return false;
      }
      return false;
    }
    ************************************************************************************************ */
    
    public boolean tryRankUpWithoutGroup(Player player,String skill,String gender) {
        try{
            if(PlayerToIgnore(player)) return false;
            if(GroupToIgnore(player)) return false;
            PlayerProfile _McMMOPlayerProfile =  Users.getProfile(player);
            Integer SkillLevel;
            if(skill.equalsIgnoreCase("POWERLEVEL")) {
                SkillLevel = getPowerLevel(player);
            }else{
                SkillLevel = _McMMOPlayerProfile.getSkillLevel(getSkillType(skill));
            }
            String Tag = "";
            for (Iterator<String> it = plugin.RankUpConfig.get(skill).get(gender).iterator(); it.hasNext();) {
                String entry = it.next();
                String[] values = entry.split(",");
                if(Integer.parseInt(values[0]) < SkillLevel){
                    Tag = values[1]; 
                }
            }
            System.out.print(Tag);
            if(!Tag.equalsIgnoreCase("")) return ChangeTag(player,Tag,skill);
      }catch(NullPointerException ex) {
            Mcmmorankup.logger.log(Level.WARNING,"{tryRankUpW/OutGroup} - Error trying to rank up " + ex.getMessage());
            return false;
      }catch(Exception ex) {
            Mcmmorankup.logger.log(Level.WARNING,"{tryRankUpW/OutGroup} - Error trying to rank up " + ex.getMessage());
            ex.printStackTrace();
            return false;
      }
      return false;
    }
    
    private boolean ChangeTag(Player player,String tag,String skill) {
         Profile _profile = new Profile(plugin, player);
         String tagnow = _profile.getTag();
         if(tagnow == null) tagnow = "Default"; 
         if(!tag.equalsIgnoreCase(tagnow))  {
            _profile.setTag(tag);
            //plugin.getServer().broadcastMessage("-=-=-=-=-=-=-=-=- [ RANK UP] -=-=-=-=-=-=-=-=-=-=-=-");
            plugin.getServer().broadcastMessage("\n"+ChatTools.formatTitle(plugin.globalBroadcastRankupTitle, plugin.titleHeader, plugin.titleHeaderLineColor, plugin.titleHeaderTextColor, plugin.titleHeaderAltColorBold,
				       																						  plugin.titleHeaderAltColor, plugin.titleHeaderAltColorBold));
            //plugin.getServer().broadcastMessage(plugin.parseColor(BroadcastMessage(player, tag, skill)));
            plugin.getServer().broadcastMessage(ChatTools.getAltColor(plugin.generalMessages) + BroadcastMessage(player, tag, skill));
            //plugin.getServer().broadcastMessage("----------------------------------------------------");
            plugin.getServer().broadcastMessage(ChatTools.getAltColor(plugin.titleFooterLineColor) + plugin.titleFooter);
            return true;
        }else{
            return false;
        }
    }
    
    private boolean ChangeGroup(Player player,String newgroup,String skill)
    {
        String groupnow = plugin.permission.getPrimaryGroup(player);
        boolean state;
        
        if(plugin.RemoveOnlyPluginGroup) {
            plugin.permission.playerRemoveGroup(player.getWorld(), player.getName(), groupnow);
        }else{
            String[] playergroups = plugin.permission.getPlayerGroups(player);
            for(String playergroup:playergroups) {
                plugin.permission.playerRemoveGroup(player.getWorld(), player.getName(), playergroup);
            }
        }
        state = plugin.permission.playerAddGroup(player.getWorld(),player.getName(),newgroup);
        
        if(!groupnow.equalsIgnoreCase(newgroup))  {
            //plugin.getServer().broadcastMessage("-=-=-=-=-=-=-=-=- [ RANK UP] -=-=-=-=-=-=-=-=-=-=-=-");
        	plugin.getServer().broadcastMessage("\n"+ChatTools.formatTitle(plugin.globalBroadcastRankupTitle, plugin.titleFooter, plugin.titleHeaderLineColor, plugin.titleHeaderTextColor, plugin.titleHeaderAltColorBold,
						  												   plugin.titleHeaderAltColor, plugin.titleHeaderAltColorBold));
        	//plugin.getServer().broadcastMessage(plugin.parseColor(BroadcastMessage(player, newgroup, skill)));
        	plugin.getServer().broadcastMessage(ChatTools.getAltColor(plugin.generalMessages) + BroadcastMessage(player, newgroup, skill));
            //plugin.getServer().broadcastMessage("----------------------------------------------------");
            plugin.getServer().broadcastMessage(ChatTools.getAltColor(plugin.titleFooterLineColor) + plugin.titleFooter);
        }
        
        return state;   
    }
    
    private static int getPowerLevel(Player player)
    {
        return ExperienceAPI.getPowerLevel(player);
    }
    
    private String BroadcastMessage(Player player, String group, String skill)
    {
        if(plugin.UseAlternativeBroadcast) {
            try {
               HashMap<String,String> _BroadCast = plugin.BroadCast.get(skill);
               String bc = _BroadCast.get(group);               
               return plugin.MPromote.replace("%player%", player.getName()).replace("%group%", bc);
            } catch (Exception ex) {
                Mcmmorankup.logger.log(Level.WARNING,"Error trying to broadcast Alternative " + ex.getMessage());
                ex.printStackTrace();
                return "Error try to broadcast Alternative";
            }
        }else {
            return plugin.MPromote.replace("%player%", player.getName()).replace("%group%", group);
        }
    }
    
    
    public SkillType getSkillType(String skill) {
         if(skill.equalsIgnoreCase("EXCAVATION")) return SkillType.EXCAVATION;
         if(skill.equalsIgnoreCase("FISHING")) return SkillType.FISHING;
         if(skill.equalsIgnoreCase("HERBALISM")) return SkillType.HERBALISM;
         if(skill.equalsIgnoreCase("MINING")) return SkillType.MINING;
         if(skill.equalsIgnoreCase("AXES")) return SkillType.AXES;
         if(skill.equalsIgnoreCase("ARCHERY")) return SkillType.ARCHERY;
         if(skill.equalsIgnoreCase("SWORDS")) return SkillType.SWORDS;
         if(skill.equalsIgnoreCase("TAMING")) return SkillType.TAMING;
         if(skill.equalsIgnoreCase("UNARMED")) return SkillType.UNARMED;
         if(skill.equalsIgnoreCase("ACROBATICS")) return SkillType.ACROBATICS;
         if(skill.equalsIgnoreCase("REPAIR")) return SkillType.REPAIR;
         return null;
    }
    
    public Boolean PlayerToIgnore(Player player) {
        for(String PlayerToIgnore:plugin.PlayerToIgnore) {
            if(player.getName().equalsIgnoreCase(PlayerToIgnore)) {
                return true;
            }
        }
        return false;
    }
    
    public Boolean GroupToIgnore(Player player) {
        for (String GroupToIgnore  : plugin.GroupToIgnore) {
            if(GroupToIgnore.equalsIgnoreCase(plugin.permission.getPrimaryGroup(player))) {
                return true;
            }
        }
        return false;
    }
}
