package me.stutiguias.mcmmorankup.profile;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

import me.stutiguias.mcmmorankup.Mcmmorankup;
import me.stutiguias.mcmmorankup.Utilities;
import me.stutiguias.mcmmorankup.UtilityReportWriter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;

import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;

public class AdminProfiler extends Utilities {

    Player player;
    File playerfile;
    
    public SortedSetMultimap<String, String> report = TreeMultimap.create();

    public AdminProfiler(CommandSender sender, Mcmmorankup plugin, String type, String category) {
        super(plugin);
        File folder = new File(Mcmmorankup.PluginPlayerDir);
        File[] listOfFiles = folder.listFiles();

        String categoryFormat = ("   %1$-16s %2$17s %3$-18s %4$-12s | %5$17s %6$10s %7$12s | ");
        String catList = null;
        String PlayerName = null;
        Integer fileLevel;
        String fileSkill;
        String fileName;
        String primaryGroup;
        String fileLastPurchased;
        String fileLastPurchasedDate;
        String fileLastPurchasedCost;
        List<String> purchasedRanks;
        boolean errorsDetected = false;

        this.sender = sender;

        if (listOfFiles.length == 0) return;

        for (int i = 0; i < listOfFiles.length; i++) {
            fileName = listOfFiles[i].getName();
            String profileCategory = null;
            
            if (!fileName.endsWith("yml")) return;
            
            try {
                playerfile = new File(Mcmmorankup.PluginPlayerDir + File.separator + fileName);
                PlayerName = fileName.substring(0, fileName.lastIndexOf("."));
                Profile profile = new Profile(plugin,PlayerName);	
                
                if (GetOnlinePlayers(PlayerName)) {
                    fileSkill = profile.GetHabilityForRank();
                    fileLevel = plugin.GetSkillLevel(player, fileSkill);
                } else {
                    fileSkill = profile.GetQuitSkill();
                    fileLevel = profile.GetQuitLevel();
                }
                
                purchasedRanks = profile.GetPurchasedRanks();
                primaryGroup = plugin.permission.getPrimaryGroup(PlayerName, PlayerName);

                if (purchasedRanks.size() > 0) {
                    fileLastPurchased = "* " + plugin.BuyRank.getLastPurchasedRank(purchasedRanks);
                    fileLastPurchasedDate = plugin.BuyRank.getLastPurchasedDate(purchasedRanks);
                    fileLastPurchasedCost = plugin.BuyRank.getLastPurchasedCost(purchasedRanks);
                } else {
                    fileLastPurchased = "Not Purchased";
                    fileLastPurchasedDate = "N/A";
                    fileLastPurchasedCost = "N/A";
                }
                
                switch (type.toLowerCase()) {
                    case "c":
                        if (!fileSkill.equalsIgnoreCase(category)) break;
                        catList = String.format(categoryFormat, PlayerName, primaryGroup, fileLevel.toString(), profile.GetGender(), fileLastPurchased, fileLastPurchasedDate, fileLastPurchasedCost);
                        profileCategory = fileSkill;
                        break;
                    case "g":
                        catList = String.format(categoryFormat, PlayerName, primaryGroup, fileLevel.toString(), Capitalized(fileSkill), fileLastPurchased, fileLastPurchasedDate, fileLastPurchasedCost);
                        profileCategory = profile.GetGender();
                        break;
                    default:
                        catList = String.format(categoryFormat, PlayerName, primaryGroup, fileLevel.toString(), profile.GetGender(), fileLastPurchased, fileLastPurchasedDate, fileLastPurchasedCost);
                        profileCategory = fileSkill;
                        break;
                }
                
                String tag = profile.GetTag().length() != 0 ? " - " + profile.GetTag() + " - " : " - NOTAG - ";
                catList += plugin.TagSystem ? tag : "";
                if (profileCategory == null) continue;
                report.put(profileCategory, catList);
            } catch (NullPointerException ex) {		        	
                Mcmmorankup.logger.log(Level.WARNING, "{0} +AdmRpt- No data collected for: {1}- Profile is probably out-of-date!", new Object[]{Mcmmorankup.logPrefix, PlayerName});
                errorsDetected = true;
            } catch (Exception ex) {
                Mcmmorankup.logger.log(Level.WARNING, "+-AdmRpt- Other Error constructing players data {0}", ex.getMessage());
                ex.printStackTrace();
                errorsDetected = true;
            }

        }
        
        SendMessage("&aPlayers Collected: &e%s&a, Categories: %s", new Object[]{ report.size(), report.keys() });
        
        if (errorsDetected) {
            SendMessage("&cWARNING: There was at least 1 error during report generation.\nCheck the server log or console for more detail");
        }
    }

    private boolean GetOnlinePlayers(String playerName) {
        boolean isOnline = false;
        Player[] onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player oplayer : onlinePlayers) {
            if (oplayer == null) continue;
            if (oplayer.getName().equalsIgnoreCase(playerName)) {
                isOnline = true;
                this.player = oplayer;
                break;
            }
        }
        return isOnline;
    }

    public boolean PrintReport(String rName, String cat) {

        String filePlaced = UtilityReportWriter.SaveReportToFile(report, rName, cat);
        if (filePlaced != null) {
            SendMessage("&6Ranking report file saved: &e" + filePlaced);
        } else {
            SendMessage("&cReport contents was empty... Nothing was generated!!");
        }
        report.clear();
        return true;
    }
}
