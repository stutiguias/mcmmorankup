package me.stutiguias.mcmmorankup.profile;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.player.UserManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import me.stutiguias.mcmmorankup.Mcmmorankup;
import static me.stutiguias.mcmmorankup.Mcmmorankup.Message;
import me.stutiguias.mcmmorankup.Util;
import me.stutiguias.mcmmorankup.XpCalc;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Profile extends Util {

    public Player player;
    File configplayerfile;
    YamlConfiguration PlayerYML;

    public Profile(Mcmmorankup plugin, String playerName) {
        super(plugin);
        LoadPlayerProfile(playerName);
    }

    public Profile(Mcmmorankup plugin, Player player) {
        super(plugin);
        LoadPlayerProfile(player.getName());
        this.player = player;
    }

    private McMMOPlayer SetMcMMOPlayer() {
        McMMOPlayer mcmmoPlayer;

        try {
            mcmmoPlayer = UserManager.getPlayer(player.getName());
        } catch (Exception ex) {
            Mcmmorankup.logger.log(Level.WARNING, "{0} Can't find mcMMO profile for player {1}", new Object[]{Mcmmorankup.logPrefix, player.getName()});
            Mcmmorankup.logger.log(Level.WARNING, "{0} Extended Error was: {1}", new Object[]{Mcmmorankup.logPrefix, ex.getMessage()});
            player.sendMessage(plugin.Message.NotHaveProfile);
            return null;
        }
        
        return mcmmoPlayer;
    }
    
    private boolean SetInitRank() {
        SaveYML();
        return true;
    }

    public boolean SetHabilityForRank(String HabilityForRank) {
        McMMOPlayer mcMMOPlayer = SetMcMMOPlayer();

        if (mcMMOPlayer != null) {
            switch(HabilityForRank.toUpperCase()){
                case "EXCAVATION":
                case "FISHING":
                case "HERBALISM":
                case "MINING":
                case "AXES":
                case "ARCHERY":
                case "SWORDS":
                case "TAMING":
                case "UNARMED":
                case "ACROBATICS":
                case "REPAIR":
                case "WOODCUTTING":
                case "SMELTING":
                case "POWERLEVEL":
                case "CUSTOM":
                    ChangeMessage(player, HabilityForRank);
                    break;
                default:
                    return false;
            }

        }

        PlayerYML.set("HabilityForRank", HabilityForRank.toUpperCase());
        SaveYML();
        return true;
    }

    public void SetHealth() {
        AttributeInstance healthAttributeInstance = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        String skill = GetHabilityForRank().toUpperCase();
        String rank = GetTag().toUpperCase();
        try{
            double health;
            if(rank.equals("VISITOR")) 
                health = 2;
            else
                health = plugin.Health.get(skill).get(rank);
            if(!plugin.UseHealthSystem) health = 20;
            healthAttributeInstance.setBaseValue(health);
        }catch(Exception ex){
            Mcmmorankup.logger.log(Level.INFO, "--=== Send this erro to dev ===---", rank);
            Mcmmorankup.logger.log(Level.INFO, "Erro Rank : {0}", rank);
            Mcmmorankup.logger.log(Level.INFO, "Erro skill : {0}", skill);
            ex.printStackTrace();
        }
    }
    
    public Boolean SetTag(String Tag) {
        PlayerYML.set("Tag", Tag);
        SaveYML();
        SetHealth();
        return true;
    }

    public Boolean SetPurchasedRank(List<String> ranks) {
        PlayerYML.set("PurchasedRanks", ranks);
        SaveYML();
        return true;
    }

    public void SetQuitStats() {
        McMMOPlayer mcmmoPlayer = SetMcMMOPlayer();

        if (mcmmoPlayer != null) {
            PlayerYML.set("Last Stats.LastQuitSkill", GetHabilityForRank());
            PlayerYML.set("Last Stats.LastQuitLevel", plugin.GetSkillLevel(player, GetHabilityForRank()));
            PlayerYML.set("Last Stats.LastRank", plugin.TagSystem ? GetTag() : plugin.GetPlayerCurrentGroup(player));
            PlayerYML.set("Last Stats.LastXp", XpCalc.GetTotalExperience(player));
            PlayerYML.set("Last Stats.LastXpLevel", XpCalc.GetPlayerXpl(player));
            PlayerYML.set("Last Stats.LastBalance", plugin.GetPlayerCurrency(player));

            SaveYML();
        }
    }

    public boolean SetPlayerRankupFeed(boolean rankupFeed) {
        PlayerYML.set("PlayerFeeds.Rankup", rankupFeed);
        SaveYML();
        return true;
    }

    public boolean SetPlayerRankCheckingFeed(boolean rankChecker) {
        PlayerYML.set("PlayerFeeds.RankChecker", rankChecker);
        SaveYML();
        return true;
    }
    
    public boolean SetMOBKILLED(String mob,int qtd) {
        PlayerYML.set("MOBKILLED." + mob, qtd);
        SaveYML();
        return true;
    }
    
    public boolean SetPlayerKILLED(int qtd) {
        PlayerYML.set("PlayerKilled", qtd);
        SaveYML();
        return true;
    }
    
    public boolean SetPlayerGlobalFeed(boolean globalFeed) {
        PlayerYML.set("PlayerFeeds.Global", globalFeed);
        SaveYML();
        return true;
    }

    public boolean SetPlayerXpUpdateFeed(boolean xpUpdateFeed) {
        PlayerYML.set("PlayerFeeds.XpUpdates", xpUpdateFeed);
        SaveYML();
        return true;
    }

    public String GetTag() {
        return PlayerYML.getString("Tag");
    }
    
    public int GetMOBKILLED(String mob) {
        return PlayerYML.getInt("MOBKILLED." + mob);
    }
        
    public int GetPlayerKILLED() {
        return PlayerYML.getInt("PlayerKilled");
    }
        
    public List<String> GetPurchasedRanks() {
        return PlayerYML.getStringList("PurchasedRanks");
    }

    public String GetHabilityForRank() {
        return PlayerYML.getString("HabilityForRank");
    }

    public int GetSkillLevelRankLine() {
        return plugin.GetSkillLevel(player, GetHabilityForRank());
    }
    
    public Boolean SetGender(String gender) {
        PlayerYML.set("Gender", gender);
        SaveYML();
        return true;
    }

    public String GetGender() {
        return PlayerYML.getString("Gender");
    }

    public String GetQuitSkill() {
        return PlayerYML.getString("Last Stats.LastQuitSkill");
    }

    public Integer GetQuitLevel() {
        return PlayerYML.getInt("Last Stats.LastQuitLevel");
    }

    public String GetQuitRank() {
        return PlayerYML.getString("Last Stats.LastRank");
    }

    public double GetQuitBalance() {
        return PlayerYML.getDouble("Last Stats.LastBalance");
    }

    public double GetQuitXp() {
        return PlayerYML.getDouble("Last Stats.LastXp");
    }

    public int GetQuitXpLevel() {
        return PlayerYML.getInt("Last Stats.LastXpLevel");
    }

    public boolean GetPlayerRankupFeed() {
        return PlayerYML.getBoolean("PlayerFeeds.Rankup");
    }

    public boolean GetPlayerGlobalFeed() {
        return PlayerYML.getBoolean("PlayerFeeds.Global");
    }

    public boolean GetPlayerXpUpdateFeed() {
        return PlayerYML.getBoolean("PlayerFeeds.XpUpdates");
    }

    public void ChangeMessage(Player player, String Hability) {       
        SendMessage(player,"ABILITY SELECTED");
        SendMessage(player,Message.HabilitySet.replace("%ability%", Hability.toUpperCase()));
        SendMessage(player,Message.MessageSeparator);
    }
    
    private void initLoadYML() {
        LoadYML();
    }

    public void LoadYML() {
        try {
            PlayerYML.load(configplayerfile);
        } catch (FileNotFoundException ex) {
            Mcmmorankup.logger.log(Level.WARNING, "{0} File Not Found {1}", new Object[]{Mcmmorankup.logPrefix, ex.getMessage()});
        } catch (IOException ex) {
            Mcmmorankup.logger.log(Level.WARNING, "{0} IO Problem {1}", new Object[]{Mcmmorankup.logPrefix, ex.getMessage()});
        } catch (InvalidConfigurationException ex) {
            Mcmmorankup.logger.log(Level.SEVERE, "{0} Invalid Configuration {1}", new Object[]{Mcmmorankup.logPrefix, ex.getMessage()});
        }
    }

    public void CheckPlayerConfig() {

        if (!PlayerYML.isSet("Tag") && !plugin.StartTagName.isEmpty()) {
            PlayerYML.set("Tag", plugin.StartTagName);						   		
        }
        
        if(!PlayerYML.isSet("MOBKILLED")){
            for(EntityType type:EntityType.values()){
                PlayerYML.set("MOBKILLED." + type.name(), 0 );
            }
        }
        
        if (!PlayerYML.isSet("PlayerKilled"))             PlayerYML.set("PlayerKilled", 0);
        if (!PlayerYML.isSet("Last Stats.LastQuitSkill")) PlayerYML.set("Last Stats.LastQuitSkill", "N/A");
        if (!PlayerYML.isSet("Last Stats.LastQuitLevel")) PlayerYML.set("Last Stats.LastQuitLevel", 0);
        if (!PlayerYML.isSet("Last Stats.LastRank"))      PlayerYML.set("Last Stats.LastRank", "N/A");
        if (!PlayerYML.isSet("Last Stats.LastXp"))        PlayerYML.set("Last Stats.LastXp", 0);
        if (!PlayerYML.isSet("Last Stats.LastXpLevel"))   PlayerYML.set("Last Stats.LastXpLevel", 0);
        if (!PlayerYML.isSet("Last Stats.LastBalance"))   PlayerYML.set("Last Stats.LastBalance", 0);
        if (!PlayerYML.isSet("PurchasedRanks"))           PlayerYML.set("PurchasedRanks", "");
        if (!PlayerYML.isSet("PlayerFeeds.Rankup"))       PlayerYML.set("PlayerFeeds.Rankup", true);
        if (!PlayerYML.isSet("PlayerFeeds.Global"))       PlayerYML.set("PlayerFeeds.Global", true);
        if (!PlayerYML.isSet("PlayerFeeds.XpUpdates"))    PlayerYML.set("PlayerFeeds.XpUpdates", false);
        
        SaveYML();

    }

    public void SaveYML() {
        try {
            PlayerYML.save(configplayerfile);
        } catch (FileNotFoundException ex) {
            Mcmmorankup.logger.log(Level.WARNING, "{0} File Not Found {1}", new Object[]{Mcmmorankup.logPrefix, ex.getMessage()});
        } catch (IOException ex) {
            Mcmmorankup.logger.log(Level.WARNING, "{0} IO Problem {1}", new Object[]{Mcmmorankup.logPrefix, ex.getMessage()});
        }
    }

    private void LoadPlayerProfile(String playerName) {
        configplayerfile = new File(Mcmmorankup.PluginPlayerDir + File.separator + playerName + ".yml");
        PlayerYML = new YamlConfiguration();

        boolean havetocreate = false;

        try {
            havetocreate = configplayerfile.createNewFile();
        } catch (IOException ex) {
            Mcmmorankup.logger.log(Level.WARNING, "{0} Can't create the rankup user file {1}", new Object[]{Mcmmorankup.logPrefix, ex.getMessage()});
        }

        initLoadYML();

        if (havetocreate) {
            Mcmmorankup.logger.log(Level.INFO, "{0} Creating profile for {1}!", new Object[]{Mcmmorankup.logPrefix, playerName});
            PlayerYML.set("Gender", "Male");		
            PlayerYML.set("HabilityForRank", plugin.DefaultSkill.toUpperCase());
            PlayerYML.set("Tag", plugin.StartTagName);
            PlayerYML.set("Last Stats.LastQuitSkill", "N/A");
            PlayerYML.set("Last Stats.LastQuitLevel", 0);
            PlayerYML.set("Last Stats.LastRank", "N/A");
            PlayerYML.set("Last Stats.LastXp", 0);
            PlayerYML.set("Last Stats.LastXpLevel", 0);
            PlayerYML.set("Last Stats.LastBalance", 0);
            PlayerYML.set("PurchasedRanks", "");
            PlayerYML.set("PlayerFeeds.Rankup", true);
            PlayerYML.set("PlayerFeeds.Global", true);
            PlayerYML.set("PlayerFeeds.XpUpdates", false);

            if (SetInitRank()) {
                Mcmmorankup.logger.log(Level.INFO, "Player {0}:- Set Auto Rank Line to: {1}", new Object[]{playerName, plugin.DefaultSkill});
            }
            
        } else {
            CheckPlayerConfig();
        }
    }
}
