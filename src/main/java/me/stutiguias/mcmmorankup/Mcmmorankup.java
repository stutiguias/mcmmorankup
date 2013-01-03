package me.stutiguias.mcmmorankup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.stutiguias.apimcmmo.RankUp;
import me.stutiguias.listeners.MRUCommandListener;
import me.stutiguias.listeners.MRUPlayerListener;
import me.stutiguias.metrics.Metrics;
import me.stutiguias.task.UpdateTask;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Mcmmorankup extends JavaPlugin {

    public String logPrefix = "[McMMoRankUp] ";
    String PluginDir = "plugins" + File.separator + "Mcmmorankup";
    public static final Logger logger = Logger.getLogger("Minecraft");
    public Permission permission = null;
    public final MRUPlayerListener playerlistener = new MRUPlayerListener(this);
    public Economy economy = null;
    public RankUp RankUp = null;
    public HashMap<String,Boolean> isHabilityRankExist;
    public HashMap<String,HashMap<String,ArrayList<String>>> RankUpConfig;
    public HashMap<String,HashMap<String,String>> BroadCast;
    public String[] PlayerToIgnore;
    public String[] GroupToIgnore;
    public HashMap<String,Long> Playertime;
    public Integer total;
    
    // Messages
    public String ChooseHability;
    public String NotHaveProfile;
    public String MPromote;
    public String MSucess;
    public String MFail;
    public String NotFound;
    public String setGender;

    // zrocweb: added messaging
    public String rankinfoTitle;
    public String promoteTitle;
    public String globalBroadcastRankupTitle;
    public String baseRanksListing;
    
    //ConfigAcess for hability
    public ConfigAccessor POWERLEVEL;
    public ConfigAccessor EXCAVATION;
    public ConfigAccessor FISHING;
    public ConfigAccessor HERBALISM;
    public ConfigAccessor MINING;
    public ConfigAccessor AXES;
    public ConfigAccessor ARCHERY;
    public ConfigAccessor SWORDS;
    public ConfigAccessor TAMING;
    public ConfigAccessor UNARMED;
    public ConfigAccessor ACROBATICS;
    public ConfigAccessor REPAIR;
    
    public boolean TagSystem;
    public String AutoUpdateTime;
    public String DefaultSkill;
    public boolean UseAlternativeBroadcast;
    public boolean PromoteOnJoin;
    public boolean AutoUpdate;
    public boolean RemoveOnlyPluginGroup;
    
    // zrocweb: add config 
    public long onJoinDelay;
    public Boolean globalBroadcastFeed;
    public Boolean playerBroadcastFeed;
    public Boolean displayNextPromo;
    
    // zrocweb: Formatting
    public String titleHeader;
    public String titleFooter;
    public String titleHeaderLineColor;
    public String titleHeaderTextColor;
    public Boolean titleHeaderTextColorBold;
    public String titleHeaderAltColor;
    public Boolean titleHeaderAltColorBold;
    public String titleFooterLineColor;
    public String titleFooterTextColor;
    public String rankinfoTextColor;
    public String rankinfoAltColor;
    public String promoteTextColor;
    public Boolean promoteTextBold;
    public String promotePreTextColor;
    public String generalMessages;
    
    @Override
    public void onEnable() {

            logger.log(Level.INFO, "{0} Mcmmorankup is initializing", logPrefix);

            onLoadConfig();
            getCommand("mru").setExecutor(new MRUCommandListener(this));
            setupEconomy();
            setupPermissions();
            
            PluginManager pm = getServer().getPluginManager();
            pm.registerEvents(playerlistener, this);
            
            if(AutoUpdate) {
                Long uptime = new Long("0");
                if(AutoUpdateTime.contains("h")) {
                  uptime = Long.parseLong(AutoUpdateTime.replace("h",""));
                  uptime = ( ( uptime * 60 ) * 60 ) * 20;
                }
                if(AutoUpdateTime.contains("m")) {
                  uptime = Long.parseLong(AutoUpdateTime.replace("m",""));
                  uptime =  ( uptime * 60 ) * 20;
                }
                getServer().getScheduler().scheduleSyncRepeatingTask(this, new UpdateTask(this), uptime, uptime);
            }
            
            File f = new File("plugins"+ File.separator +"Mcmmorankup"+ File.separator +"userdata");
            if(!f.exists())  {
                logger.log(Level.INFO, "{0} Diretory not exist creating new one", logPrefix);
                f.mkdirs();
            }
            
            if(this.permission.isEnabled() == true)
            {
                logger.log(Level.INFO, "{0} Vault perms hooked!", logPrefix);    
            }else{
                logger.log(Level.INFO, "{0} Vault WAS NOT ENABLED!", logPrefix);    
            }
            
           //Metrics 
            try {
              logger.log(Level.INFO, "{0} Sending Metrics !", logPrefix);
              Metrics metrics = new Metrics(this);
              metrics.start();
            } catch (IOException e) {
              logger.log(Level.INFO, "{0} Failed to submit the stats :-(", logPrefix);
            }
           
    }

    @Override
    public void onDisable() {
            getServer().getPluginManager().disablePlugin(this);
            logger.log(Level.INFO, "{0} Disabled. Bye :D", logPrefix);
    }
    
    public void onReload() {
        this.reloadConfig();
        saveConfig();
        getServer().getPluginManager().disablePlugin(this);
        getServer().getPluginManager().enablePlugin(this);
    }

    private void initConfig() {
                
                getConfig().addDefault("Message.NotHaveProfile", "Did not find your ranking profile!");
                getConfig().addDefault("Message.ChooseHability", "You chose to rank up based on %ability%");
                getConfig().addDefault("Message.RankUp", "Player %player% has been promoted to %group%");
                getConfig().addDefault("Message.Sucess", "Congrats! Ability has been promoted to the next level");
                getConfig().addDefault("Message.Fail", "Sorry your level is too low for promotion");
                getConfig().addDefault("Message.NotFound", "Ranking File not found or has not been configured");
                getConfig().addDefault("Message.setGender", "Your Gender has been set to %gender%");
                
                // zrocweb: added messaging
                getConfig().addDefault("Message.RankInfoTitle", "RANK INFO");
                getConfig().addDefault("Message.PromoteTitle", "PROMOTION");
                getConfig().addDefault("Message.GlobalBroadcastRankupTitle", "RANK UP");
                getConfig().addDefault("Message.BaseRanksListing", "BASE RANK ABILITIES LISTING");
                
                
                getConfig().addDefault("Config.UseTagOnlySystem", false);
                getConfig().addDefault("Config.RemoveOnlyPluginGroup",true);
                getConfig().addDefault("Config.PromoteOnJoin", true);
                
                // zrocweb: added config
                getConfig().addDefault("Config.OnJoinDelay",300);
                getConfig().addDefault("Config.GlobalBroadcastFeed", true);
                getConfig().addDefault("Config.PlayerBroadcastFeed", true);
                getConfig().addDefault("Config.DisplayNextPromo", true);
                
                // zrocweb: formatting
                getConfig().addDefault("Formatting.TitleHeader", ".oOo.————————————————————————————————————————————————————————————————.oOo.");
                getConfig().addDefault("Formatting.TitleFooter", "——————————————————————————————————————————————————————————————————————————————");
                getConfig().addDefault("Formatting.TitleHeaderLineColor", "&9");
                getConfig().addDefault("Formatting.TitleHeaderTextColor", "&e");
                getConfig().addDefault("Formatting.TitleHeaderTextColorBold", true);
                getConfig().addDefault("Formatting.TitleHeaderAltColor", "&f");
                getConfig().addDefault("Formatting.TitleHeaderAltColorBold", true);
                getConfig().addDefault("Formatting.TitleFooterLineColor", "&9");
                getConfig().addDefault("Formatting.TitleFooterTextColor", "&e"); 
                getConfig().addDefault("Formatting.RankInfoTextColor", "&b");
                getConfig().addDefault("Formatting.RankInfoAltColor", "&3");
                getConfig().addDefault("Formatting.PromoteTextColor", "&e");
                getConfig().addDefault("Formatting.PromoteTextBold", true);
                getConfig().addDefault("Formatting.PromotePreTextColor", "&5");
                getConfig().addDefault("Formatting.GeneralMessages", "&6");
                
                getConfig().addDefault("Config.AutoUpdate", true);
                getConfig().addDefault("Config.AutoUpdateTime", "1h");
                getConfig().addDefault("Config.UseAlternativeBroadCast", true);
                getConfig().addDefault("Config.DefaultSkill", "POWERLEVEL");
                getConfig().addDefault("PlayerToIgnore", "");
                getConfig().addDefault("GroupToIgnore","");

                getConfig().options().copyDefaults(true);
                saveConfig();
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        permission = rsp.getProvider();
        return permission != null;
    }

    private Boolean setupEconomy() {
            RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
            if (economyProvider != null) {
                    economy = economyProvider.getProvider();
            }

            return (economy != null);
    }

    public void onLoadConfig() {
            initConfig();
            UseAlternativeBroadcast = getConfig().getBoolean("Config.UseAlternativeBroadCast");
            PromoteOnJoin = getConfig().getBoolean("Config.PromoteOnJoin");
            AutoUpdate = getConfig().getBoolean("Config.AutoUpdate");
            AutoUpdateTime = getConfig().getString("Config.AutoUpdateTime");
            PlayerToIgnore = getConfig().getString("PlayerToIgnore").split((","));
            GroupToIgnore = getConfig().getString("GroupToIgnore").split((","));
            DefaultSkill = getConfig().getString("Config.DefaultSkill");
            TagSystem = getConfig().getBoolean("Config.UseTagOnlySystem");
            RemoveOnlyPluginGroup = getConfig().getBoolean("Config.RemoveOnlyPluginGroup");
            
            // zrocweb: added config
            onJoinDelay = getConfig().getLong("Config.OnJoinDelay");
            globalBroadcastFeed = getConfig().getBoolean("Config.GlobalBroadcastFeed");
            playerBroadcastFeed = getConfig().getBoolean("Config.PlayerBroadcastFeed");
            displayNextPromo = getConfig().getBoolean("Config.DisplayNextPromo");
            
            // zrocweb: Formatting
            titleHeader = getConfig().getString("Formatting.TitleHeader");
            titleFooter = getConfig().getString("Formatting.TitleFooter");
            titleHeaderLineColor = getConfig().getString("Formatting.TitleHeaderLineColor");
            titleHeaderTextColor = getConfig().getString("Formatting.TitleHeaderTextColor");
            titleHeaderTextColorBold = getConfig().getBoolean("Formatting.TitleHeaderTextColorBold");
            titleHeaderAltColor = getConfig().getString("Formatting.TitleHeaderAltColor");
            titleHeaderAltColorBold = getConfig().getBoolean("Formatting.TitleHeaderAltColorBold");
            titleFooterLineColor = getConfig().getString("Formatting.TitleFooterLineColor");
            titleFooterTextColor = getConfig().getString("Formatting.TitleFooterTextColor");
            rankinfoTextColor = getConfig().getString("Formatting.RankInfoTextColor");
            rankinfoAltColor = getConfig().getString("Formatting.RankInfoAltColor");
            promoteTextColor = getConfig().getString("Formatting.PromoteTextColor");
            promoteTextBold = getConfig().getBoolean("Formatting.PromoteTextBold");
            promotePreTextColor = getConfig().getString("Formatting.PromotePreTextColor");
            generalMessages = getConfig().getString("Formatting.GeneralMessages");
            
            logger.log(Level.INFO, "{0} Alternative Broadcast is {1}", new Object[]{logPrefix, UseAlternativeBroadcast});
            logger.log(Level.INFO, "{0} Default skill is {1}", new Object[]{logPrefix, DefaultSkill});
            
            RankUp = new RankUp(this);
            RankUpConfig = new HashMap<String, HashMap<String,ArrayList<String>>>();
            BroadCast = new HashMap<String, HashMap<String, String>>();
            isHabilityRankExist = new HashMap<String, Boolean>();
            
            // InitAcessor
            POWERLEVEL = new ConfigAccessor(this,"powerlevel.yml");
            SetupAccessor("POWERLEVEL",POWERLEVEL);
            EXCAVATION = new ConfigAccessor(this,"excavation.yml");
            SetupAccessor("EXCAVATION",EXCAVATION);
            FISHING = new ConfigAccessor(this,"fishing.yml");
            SetupAccessor("FISHING",FISHING);
            HERBALISM = new ConfigAccessor(this,"herbalism.yml");
            SetupAccessor("HERBALISM",HERBALISM);
            MINING = new ConfigAccessor(this,"mining.yml");
            SetupAccessor("MINING",MINING);
            AXES = new ConfigAccessor(this,"axes.yml");
            SetupAccessor("AXES",AXES);
            ARCHERY = new ConfigAccessor(this,"archery.yml");
            SetupAccessor("ARCHERY",ARCHERY);
            SWORDS = new ConfigAccessor(this,"swords.yml");
            SetupAccessor("SWORDS",SWORDS);
            TAMING = new ConfigAccessor(this,"taming.yml");
            SetupAccessor("TAMING",TAMING);
            UNARMED = new ConfigAccessor(this,"unarmed.yml");
            SetupAccessor("UNARMED",UNARMED);
            ACROBATICS = new ConfigAccessor(this,"acrobatics.yml");
            SetupAccessor("ACROBATICS",ACROBATICS);
            REPAIR = new ConfigAccessor(this,"repair.yml");
            SetupAccessor("REPAIR",REPAIR);
    
            
            // Messages
            ChooseHability = getConfig().getString("Message.ChooseHability");
            NotHaveProfile = getConfig().getString("Message.NotHaveProfile");
            MPromote = getConfig().getString("Message.RankUp");
            MSucess = getConfig().getString("Message.Sucess");
            MFail = getConfig().getString("Message.Fail");
            NotFound = getConfig().getString("Message.NotFound");
            setGender = getConfig().getString("Message.setGender");
            
            // zrocweb: added messaging
            rankinfoTitle = getConfig().getString("Message.RankInfoTitle");
            promoteTitle = getConfig().getString("Message.PromoteTitle");
            globalBroadcastRankupTitle = getConfig().getString("Message.GlobalBroadcastRankupTitle");
            baseRanksListing = getConfig().getString("Message.BaseRanksListing");
            
            Playertime = new HashMap<String, Long>();
    }
   
    public long getCurrentMilli() {
		return System.currentTimeMillis();
    }
    
    public HashMap<String,ArrayList<String>> getRanks(ConfigAccessor ca){
        HashMap<String,ArrayList<String>> Ranks = new HashMap<String, ArrayList<String>>();
        ArrayList<String> Rank = new ArrayList<String>();
        for (String key : ca.getConfig().getConfigurationSection("RankUp.Male.").getKeys(false)){
          Rank.add(key + "," + ca.getConfig().getString("RankUp.Male." + key));
        }
        Ranks.put("Male", Rank);
        Rank = new ArrayList<String>();
        for (String key : ca.getConfig().getConfigurationSection("RankUp.Female.").getKeys(false)){
          Rank.add(key + "," + ca.getConfig().getString("RankUp.Female." + key));
        }
        Ranks.put("Female", Rank);
        return Ranks;
    }
    
    public String parseColor(String message) {
        try { 
            for (ChatColor color : ChatColor.values()) {
                message = message.replaceAll(String.format("&%c", color.getChar()), color.toString());
            }
            return message;
        }catch(Exception ex) {
            return message;
        }
    }
    
    public HashMap<String,String> getAlternativeBroadcast(ConfigAccessor ca){
        HashMap<String,String> BroadCastCa = new HashMap<String, String>();
        for (String key : ca.getConfig().getConfigurationSection("Broadcast.").getKeys(false)){
          BroadCastCa.put(key, ca.getConfig().getString("Broadcast." + key));
         // log.log(Level.INFO, logPrefix + "Group " + key + " will broadcast " + ca.getConfig().getString("RankUpConfig." + key));
        }
        return BroadCastCa;
    }
    public void SetupAccessor(String name,ConfigAccessor ca) {
        try {
            RankUpConfig.put(name,getRanks(ca));
            if(UseAlternativeBroadcast) BroadCast.put(name,getAlternativeBroadcast(ca));
            logger.log(Level.INFO, "{0}{1} Ranking Enabled!", new Object[]{logPrefix, name});
            isHabilityRankExist.put(name,true);
        }catch(Exception ex) {
            logger.log(Level.INFO, "{0}{1} Rank file is either corrupt and/or missing.", new Object[]{logPrefix, name});
            isHabilityRankExist.put(name,false);
        }
    }
}
