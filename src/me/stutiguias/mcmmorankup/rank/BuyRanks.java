package me.stutiguias.mcmmorankup.rank;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;

import me.stutiguias.mcmmorankup.config.ConfigAccessor;
import me.stutiguias.mcmmorankup.Mcmmorankup;
import me.stutiguias.mcmmorankup.Utilities;
import me.stutiguias.mcmmorankup.XpCalc;
import me.stutiguias.mcmmorankup.profile.Profile;

import org.bukkit.entity.Player;

public class BuyRanks {

    private static Mcmmorankup plugin;
    private Profile _profile;
    private Player _player;
    
    public BuyRanks(Mcmmorankup instance) {
        plugin = instance;
    }

    public boolean ShowBuyableRanks(Player player, String[] args) {

        _profile = new Profile(plugin, player);
        _player = player;
        String playerCurrentSkill = _profile.GetHabilityForRank();
        boolean isXp = (args[1].equalsIgnoreCase("x")) ? true : false;

        String rankNow = plugin.TagSystem ? _profile.GetTag() : plugin.permission.getPrimaryGroup(_player.getWorld(), _player.getName());
   
        ShowPlayerPurchaseProfile(rankNow,playerCurrentSkill,isXp);
 
        Map<String, String> rankCosts = isXp ? plugin.XpRanks.get(playerCurrentSkill) : plugin.BuksRanks.get(playerCurrentSkill);

        ShowRankPlayerCanBuy(isXp,rankNow,rankCosts);

        return true;
    }
    
    private void ShowRankPlayerCanBuy(boolean isXp,String rankNow,Map<String, String> rankCosts) {
        
        int startLevelRankNow = plugin.GetRankStartLevel(_profile.GetHabilityForRank(),_profile.GetGender() , rankNow);

        TreeMap<String, String> temp = new TreeMap(new ValueComparator(rankCosts));
        temp.putAll(rankCosts);
        
        for (Entry<String, String> rankCost : temp.entrySet()) {

            String rank = rankCost.getValue().toUpperCase();
            String cost = rankCost.getKey();
            
            int startLevel = plugin.GetRankStartLevel(_profile.GetHabilityForRank(),_profile.GetGender() , rank);
            if(startLevelRankNow > startLevel) continue;
            
            boolean xpL = checkIfLExists(cost.toUpperCase());
            if(xpL) cost = cost.substring(0, cost.length() - 1);
            if (rank.equalsIgnoreCase(rankNow)) continue;

            if (CanBuy(isXp,xpL,cost)) {
                String costOut = cost + (!isXp ? " " + plugin.BuyRankCurrencyName : xpL ? plugin.Message.BuyLevels : plugin.Message.BuyPoints);
                SendFormatMessage(plugin.Message.BuyListEntry.replace("%cost%", costOut).replace("%rank%", rank));
            }
        }
    }
    
    private void SendFormatMessage(String message) {
        _player.sendMessage(Utilities.parseColor(message));
    }
    
    public boolean CanBuy(boolean isXp,boolean xpL,String cost) {
        if(Integer.valueOf(cost) <= 0) return false;
        if(isXp) {
            return CanBuyWithXp(cost,xpL);
        } else {
            if (isPlayerBalanceLessThen(cost)) return false;
        }
        return true;
    }

    private boolean CanBuyWithXp(String cost,boolean xpL) {
        if (xpL) {
            if (isPlayerXpLevelLessThen(cost)) return false;
        } else {
            if (isPlayerXpLessThen(cost)) return false;
        }
        return true;
    }
    
    public boolean PurchaseRank(Player player, String rankToBuy, String purchaseMode) {

        try {
            _player = player;
            _profile = new Profile(plugin, player);

            boolean isXp = (purchaseMode.equalsIgnoreCase("x")) ? true : false;
            Map<String, String> rankCosts = isXp ? plugin.XpRanks.get(_profile.GetHabilityForRank()) : plugin.BuksRanks.get(_profile.GetHabilityForRank());
            
            String costOfRank = null;
            for (Map.Entry<String, String> rankCost : rankCosts.entrySet()) {
                String cost = rankCost.getKey();
                String rank = rankCost.getValue();
                if(rank.equalsIgnoreCase(rankToBuy)) {
                    costOfRank = cost;
                    break;
                }
            }
            if(costOfRank == null) return false;
            
            boolean xpLMode = checkIfLExists(costOfRank);
            if(xpLMode) costOfRank = costOfRank.substring(0, costOfRank.length() - 1);
            
            String RankNow = plugin.TagSystem ? _profile.GetTag() : plugin.permission.getPrimaryGroup(player.getWorld(), player.getName());

            boolean transactionStatus;
            int cost = Integer.parseInt(costOfRank);
            StringBuilder costMsg = new StringBuilder();
            costMsg.append(cost);

            if(!CanBuy(isXp,xpLMode,costOfRank)){
                SendFormatMessage("You can't afford that rank!");
                return true;
            }
            
            if(!ChangePlayerRank(rankToBuy, player, RankNow)) throw new Exception();
            if(!SetHistoryPurchasedRanks(rankToBuy, costOfRank, RankNow, isXp, xpLMode)) throw new Exception();
            
            if (isXp) {

                if (xpLMode) {
                    player.setLevel(player.getLevel() - cost);
                    costMsg.append(" ").append(plugin.Message.BuyLevels);
                } else {
                    int totalPlayerXp = XpCalc.GetTotalExperience(player);
                    XpCalc.clearExperience(player);
                    XpCalc.setTotalExperience(player, totalPlayerXp - cost);
                    costMsg.append(" ").append(plugin.Message.BuyPoints);
                }
                transactionStatus = true;
                
            } else {
                plugin.economy.withdrawPlayer(player.getName(), cost);
                costMsg.append(" ").append(plugin.BuyRankCurrencyName);
                transactionStatus = true;
            }

            SendFormatMessage(plugin.Message.BuyPurchaseConfirm.replace("%rank%", rankToBuy)
                                                           .replace("%cost%", costMsg));

            return transactionStatus;
        } catch (NullPointerException ex) {
            Mcmmorankup.logger.log(Level.WARNING, "'{'BuyRanks Null'}' - Error ocurred during rank purchasing {0}", ex.getMessage());
            return false;
        } catch (Exception ex) {
            Mcmmorankup.logger.log(Level.WARNING, "'{'BuyRanks Exception'}' - An Error ocurred during rank purchasing{0}", ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    public void ShowPlayerPurchaseProfile(String grpNow, String playerCurrentSkill,boolean isXp) {
        
        Boolean hasPurchased = false;
        
        List<String> purchasedRanks = _profile.GetPurchasedRanks();
        
        for (int i = 0; i < purchasedRanks.size(); i++) {
            String[] purchaseSetting = purchasedRanks.get(i).split("\\|");
            if (purchaseSetting[0].equals(grpNow)) {
                hasPurchased = true;
            }
        }

        SendFormatMessage(plugin.MessageSeparator);
        SendFormatMessage(plugin.Message.BuyPurchaseInfo + "\n");
        if(!isXp) {
            SendFormatMessage(plugin.Message.BuyPurchaseBuks + GetPlayerMoneyFormated() + "\n");
        }else{
            SendFormatMessage(ShowXp() + "\n");
        }
        
        SendFormatMessage(plugin.MessageSeparator);
        SendFormatMessage((hasPurchased ? "&f&l" + plugin.Message.BuyPurchase : "  &f&l:> ") + PlayerCurrentProfile(playerCurrentSkill,grpNow) + "\n");
        SendFormatMessage(plugin.MessageSeparator);
        
    }

    public String PlayerCurrentProfile(String playerCurrentSkill, String grpNow) {
        return plugin.Message.BuyProfile.replace("%rankline%", playerCurrentSkill.toLowerCase())
                .replace("%group%", Utilities.getCapitalized(grpNow))
                .replace("%level%", String.valueOf(plugin.GetSkillLevel(_player, playerCurrentSkill)));
    }
    
    public String ShowXp() {
        return plugin.Message.BuyPurchaseXp.replace("%xp%", String.valueOf(XpCalc.GetTotalExperience(_player))).replace("%level%", String.valueOf(XpCalc.GetPlayerXpl(_player)));
    }
    
    public String GetPlayerMoneyFormated() {
        return String.format("$ %.2f", plugin.GetPlayerCurrency(_player));
    }
    
    public String getLastPurchasedRank(List<String> pRanks) {
        String lastPrank;
        String tmp = pRanks.get(pRanks.size() - 1).toString();		
        lastPrank = tmp.substring(0, tmp.indexOf("|"));					
        return lastPrank;
    }

    public String getLastPurchasedDate(List<String> pRanks) {
        String[] tmp = pRanks.get(pRanks.size() - 1).toString().split(("\\|"));	// Get the last element in this Purchased Rank List
        return tmp[1];
    }

    public String getLastPurchasedCost(List<String> pRanks) {
        String[] tmp = pRanks.get(pRanks.size() - 1).toString().split(("\\|"));	// Get the last element in this Purchased Rank List
        return tmp[2];
    }

    public String GetLastPurchasedSkill(List<String> pRanks) {
        String[] tmp = pRanks.get(pRanks.size() - 1).toString().split(("\\|"));	
        return tmp[3];
    }

    public String getLastPurchasedSkillLevel(List<String> pRanks) {
        String[] tmp = pRanks.get(pRanks.size() - 1).toString().split(("\\|"));	// Get the last element in this Purchased Rank List
        return tmp[4];
    }

    public boolean SetHistoryPurchasedRanks(String rank, String cost, String curGroup, boolean isXp, boolean xpL) {

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yy");
        String playerCurrentSkill = _profile.GetHabilityForRank();
        Integer playerCurrentLevel = plugin.GetSkillLevel(_player,playerCurrentSkill);
        StringBuilder pRank = new StringBuilder();
        int curXpl = XpCalc.GetPlayerXpl(_player);
        int curXP = XpCalc.GetTotalExperience(_player);

        try {
            List<String> purchased = _profile.GetPurchasedRanks();

            /* Format:  'Purchased Group'|'Purchase Date'|'Purchase Cost'|'Cur. Rank Line'|'Cur. SkillLevel'|'Current XP/Level'|'From previous group'
             * Example:      Settler     |  03-39-2013   |      120L     |   POWERLEVEL   |       23        |   230495/235L    |      From:visitor
             ********************************************************************************************************* */
            pRank.append(rank).append("|")
                 .append(dateFormat.format(date)).append("|")
                 .append(cost).append(isXp ? xpL ? "L" : "Pts" : "$$").append("|")
                 .append(playerCurrentSkill).append("|").append(playerCurrentLevel).append("|")
                 .append(curXP).append("/").append(curXpl).append("L").append("|").append("From:").append(curGroup);

            purchased.add(pRank.toString());
            _profile.SetPurchasedRank(purchased);
        } catch (Exception e) {
            SendFormatMessage("&cThere was an error updating your rankup profile! Contact an Administrator!");
            return false;
        }
        return true;
    }

    public boolean checkIfLExists(String check) {
        return check.trim().endsWith("L");
    }

    public boolean isPlayerXpLevelLessThen(String compareTo) {
        return Integer.valueOf(XpCalc.GetPlayerXpl(_player)) < Integer.valueOf(compareTo);
    }

    public boolean isPlayerXpLessThen(String compareTo) {
        return Float.valueOf(XpCalc.GetTotalExperience(_player)) < Float.valueOf(compareTo);
    }

    public boolean isPlayerBalanceLessThen(String compareTo) {
        return Double.valueOf(plugin.GetPlayerCurrency(_player)) < Double.valueOf(compareTo);
    }

    public static Map<String, String> getRankBuyXP(ConfigAccessor ca) throws IOException {
        Map<String, String> temp = new HashMap<>();
        for (String rank : ca.getConfig().getConfigurationSection("BuyRank.XP.").getKeys(false)) {
            temp.put(ca.getConfig().getString("BuyRank.XP." + rank), rank );
        }
        return temp;
    }

    public static Map<String, String> getRankBuyBuks(ConfigAccessor ca) throws IOException {
        Map<String, String> temp = new HashMap<>();
        for (String rank : ca.getConfig().getConfigurationSection("BuyRank.BUKS.").getKeys(false)) {
            temp.put( ca.getConfig().getString("BuyRank.BUKS." + rank), rank );
        }
        return temp;
    }

    private boolean ChangePlayerRank(String rankToBuy, Player player, String grpNow) {
        boolean transactionStatus;
        if (plugin.TagSystem) {
            transactionStatus = _profile.SetTag(rankToBuy);
            _profile.SaveYML();
        } else {
            if (plugin.RemoveOnlyPluginGroup) {
                plugin.permission.playerRemoveGroup(player.getWorld(), player.getName(), grpNow);
            } else {
                String[] curGroups = plugin.permission.getPlayerGroups(player);
                for (String curGroup : curGroups) {
                    plugin.permission.playerRemoveGroup(player.getWorld(), player.getName(), curGroup);
                }
            }
            transactionStatus = plugin.permission.playerAddGroup(player.getWorld(), player.getName(), rankToBuy);
        }
        return transactionStatus;
    }

}

class ValueComparator implements Comparator<String> {

    Map<String, String> base;
    public ValueComparator(Map<String, String> base) {
        this.base = base;
    }
   
    @Override
    public int compare(String a, String b) {
        a = a.replace("L","");
        b = b.replace("L","");
        int vala = Integer.parseInt(a);
        int valb = Integer.parseInt(b);
        if (valb >= vala) {
            return -1;
        } else {
            return 1;
        } 
    }
}