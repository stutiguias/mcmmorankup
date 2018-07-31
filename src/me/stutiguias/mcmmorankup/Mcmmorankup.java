package me.stutiguias.mcmmorankup;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import me.stutiguias.mcmmorankup.config.ConfigAccessor;
import me.stutiguias.mcmmorankup.config.MessageConfig;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.stutiguias.mcmmorankup.apimcmmo.McMMOApi;
import me.stutiguias.mcmmorankup.updaterank.RankUp;
import me.stutiguias.mcmmorankup.command.MRUCommand;
import me.stutiguias.mcmmorankup.listeners.MRUPlayerListener;
import me.stutiguias.mcmmorankup.profile.Profile;
import me.stutiguias.mcmmorankup.rank.BuyRanks;
import me.stutiguias.mcmmorankup.task.UpdateTask;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Mcmmorankup extends JavaPlugin {

    public static final String logPrefix = "[" + ChatColor.GREEN + "mcmmoRankUp"+ ChatColor.WHITE+"]";
    public static final String PluginDir = "plugins" + File.separator + "Mcmmorankup";
    public static String PluginPlayerDir = PluginDir + File.separator + "userdata";
    public static String PluginSkillsDir = PluginDir + File.separator + "skills";
    public static String PluginReportsDir = PluginDir + File.separator + "reports";
    public static final Logger logger = Logger.getLogger("Minecraft");
    public final MRUPlayerListener playerlistener = new MRUPlayerListener(this);
    
    public Permission permission = null;
    public Economy economy = null;
    
    public RankUp RankUp = null;
    public BuyRanks BuyRank = null;
    public UtilityReportWriter ReportWriter = null;
    public HashMap<String, Long> Playertime;
    public HashMap<String, Boolean> isRankExist;
    public HashMap<String, HashMap<String, ArrayList<String>>> RankUpConfig;
    public HashMap<String, HashMap<String, String>> BroadCast;
    public HashMap<String, HashMap<String, Double>> Health;
    public HashMap<String, Boolean> BuyRankUsePerms;
    public HashMap<String, Boolean> BuyRankEnabled;
    
    public HashMap<String, Map<String,String>> CustomRequirements;
    
    public HashMap<String, Map<String, String>> XpRanks;	
    public HashMap<String, Map<String, String>> BuksRanks;		
    public HashMap<String, HashMap<String, String>> RewardsConfig;
    
    // Messaging
    public static MessageConfig Message;
    
    public ConfigAccessor config;

    // System Configurations
    public boolean mruStartupSummary;
    public boolean TagSystem;
    public String AutoUpdateTime;
    public String DefaultSkill;
    public String StartTagName;
    public boolean UseAlternativeBroadcast;
    public boolean AllowDemotions;
    public boolean AllowBuyRankDemotions;
    public boolean PromoteOnJoin;
    public boolean AutoUpdate;
    public boolean UseHealthSystem;
    public boolean RemoveOnlyPluginGroup;
    public long onJoinDelay;
    public boolean globalBroadcastFeed;
    public boolean playerBroadcastFeed;
    public boolean playerAbilityXpUpdateFeed;
    public boolean displayNextPromo;
    public boolean RankInfoTitles;
    public boolean displayDisabledRanks;
    public boolean UseGenderClass;
    public boolean AllowBuyingRanks;
    public boolean AllowRankRewards;
    public String BuyRankCurrencyName;
    public String[] GroupToIgnore;
    public List<String> CustomAvaibleRanks;
    public boolean PerWorldPermission;
    public boolean GenderFirst;
    public String GenderOnlyGroup;
    public String DefaultGroupAfterChoose;
            
    @Override
    public void onEnable() {
        File dir = getDataFolder();
        if (!dir.exists()) {
          dir.mkdirs();
        }
        
        File fuserdata = new File(PluginDir + File.separator + "userdata");
        if (!fuserdata.exists()) {
            logger.log(Level.WARNING, "{0} UserData folder does not exist. Creating 'userdata' Folder", new Object[]{logPrefix});
            fuserdata.mkdirs();
        }

        File fskills = new File(PluginSkillsDir);
        if (!fskills.exists()) {
            logger.log(Level.WARNING, "{0} Skills folder does not exist. Creating 'skills' folder", new Object[]{logPrefix});
            fskills.mkdirs();
        }

        File freports = new File(PluginReportsDir);
        if (!freports.exists()) {
            logger.log(Level.WARNING, "{0} Reports folder does not exist. Creating 'reports' folder", new Object[]{logPrefix});
            freports.mkdirs();
        }
        
        onLoadConfig();
        getCommand("mru").setExecutor(new MRUCommand(this));

        setupEconomy();
        setupPermissions();
        
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(playerlistener, this);

        if (mruStartupSummary) {


            if (economy == null) {
                logger.log(Level.WARNING, "{0} - No Economy found!", new Object[]{logPrefix});
            }

            if (permission == null) {
                logger.log(Level.WARNING, "{0} - Vault WAS NOT HOOKED!", new Object[]{logPrefix});
            }

            logger.log(Level.INFO, "{0} Ignoring Groups     : {1}", new Object[]{logPrefix, Arrays.toString(GroupToIgnore)});
            logger.log(Level.INFO, "{0} Ranking System      : {1}", new Object[]{logPrefix, (TagSystem ? "Tags" : "Permissions")});
            logger.log(Level.INFO, "{0} Demotions are       : {1}", new Object[]{logPrefix, (AllowDemotions ? "Allowed" : "DisAllowed")});
            logger.log(Level.INFO, "{0} P.Rank Demotions    : {1}", new Object[]{logPrefix, (AllowBuyRankDemotions ? "Enabled" : "Disabled")});
            logger.log(Level.INFO, "{0} Auto Ranking is     : {1}", new Object[]{logPrefix, (AutoUpdate ? "On" : "Off")});
            if(AutoUpdate) {
                logger.log(Level.INFO, "{0} Auto Ranking every  : {1}", new Object[]{logPrefix, AutoUpdateTime});
            }
           
        } else {
            logger.log(Level.INFO, "{0} - has been initialized!", new Object[]{logPrefix});
        }

        if (AutoUpdate) {
            Long uptime = new Long("0");
            if (AutoUpdateTime.contains("h")) {
                uptime = Long.parseLong(AutoUpdateTime.replace("h", ""));
                uptime = ((uptime * 60) * 60) * 20;
            }

            if (AutoUpdateTime.contains("m")) {
                uptime = Long.parseLong(AutoUpdateTime.replace("m", ""));
                uptime = (uptime * 60) * 20;
            }

            getServer().getScheduler().scheduleSyncRepeatingTask(this, new UpdateTask(this), uptime, uptime);
        }
    }

    @Override
    public void onDisable() {

        getServer().getPluginManager().disablePlugin(this);
        logger.log(Level.INFO, "{0} {1} - mcmmoRankup has been disabled...", new Object[]{logPrefix, "[System]"});
    }

    public void onReload() {
        config.reloadConfig();
        Message.Reload();
        onLoadConfig();
        getServer().getPluginManager().disablePlugin(this);
        getServer().getPluginManager().enablePlugin(this);
    }

    private void sendMessage(String msg) {
        Bukkit.getConsoleSender().sendMessage(msg);
    }
    
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        return (economy != null);
    }

    public void onLoadConfig() {

        try {
            CustomAvaibleRanks = new ArrayList<>();
            
            config = new ConfigAccessor(this, "config.yml");
            config.setupConfig();
            FileConfiguration fc = config.getConfig();
            
            if(!fc.isSet("configversion") || fc.getInt("configversion") != 6){ 
                config.MakeOld();
                config.setupConfig();
                fc = config.getConfig();
            }
            
            // System Configurations
            mruStartupSummary = fc.getBoolean("Config.mruStartupSummary");
            UseAlternativeBroadcast = fc.getBoolean("Config.UseAlternativeBroadCast");
            PromoteOnJoin = fc.getBoolean("Config.PromoteOnJoin");
            AllowDemotions = fc.getBoolean("Config.AllowDemotions");
            AllowBuyRankDemotions = fc.getBoolean("Config.AllowBuyRankDemotions");
            AutoUpdate = fc.getBoolean("Config.AutoUpdate");
            UseHealthSystem = fc.getBoolean("Config.UseHealthSystem");
            AutoUpdateTime = fc.getString("Config.AutoUpdateTime");
            GroupToIgnore = fc.getString("Config.GroupToIgnore").split((","));
            DefaultSkill = fc.getString("Config.DefaultSkill").toUpperCase();
            StartTagName = fc.getString("Config.StartTagName");
            TagSystem = fc.getBoolean("Config.UseTagOnlySystem");
            RemoveOnlyPluginGroup = fc.getBoolean("Config.RemoveOnlyPluginGroup");
            onJoinDelay = fc.getLong("Config.OnJoinDelay");
            globalBroadcastFeed = fc.getBoolean("Config.GlobalBroadcastFeed");
            playerBroadcastFeed = fc.getBoolean("Config.PlayerBroadcastFeed");
            playerAbilityXpUpdateFeed = fc.getBoolean("Config.PlayerAbilityXpUpdateFeed");
            displayNextPromo = fc.getBoolean("Config.DisplayNextPromo");
            RankInfoTitles = fc.getBoolean("Config.RankInfoTitles");
            UseGenderClass = fc.getBoolean("Config.UseGenderClass");
            displayDisabledRanks = fc.getBoolean("Config.DisplayDisabledRanks");
            AllowBuyingRanks = fc.getBoolean("Config.AllowBuyingRanks");
            AllowRankRewards = fc.getBoolean("Config.AllowRankRewards");
            BuyRankCurrencyName = fc.getString("Config.BuyRankCurrencyName");
            PerWorldPermission = fc.getBoolean("Config.PerWorldPermission");
            GenderFirst = fc.getBoolean("Config.GenderFirst");
            GenderOnlyGroup = fc.getString("Config.GenderOnlyGroup");
            DefaultGroupAfterChoose = fc.getString("Config.DefaultGroupAfterChoose");
            
            Message = new MessageConfig(this,fc.getString("Config.Language"));
            MessagesReplaces();
            
        } catch (IOException ex) {
            logger.log(Level.INFO, "{0} Error on config file", new Object[]{logPrefix});
            ex.printStackTrace();
            onDisable();
        }


        if (mruStartupSummary) {
            logger.log(Level.INFO, "{0} Alternative Broadcast is {1}", new Object[]{logPrefix, UseAlternativeBroadcast});
            logger.log(Level.INFO, "{0} Default skill is {1}", new Object[]{logPrefix, DefaultSkill.toUpperCase()});
        }

        OnSetupSkills();
        
        
        RankUp = new RankUp(this);
        BuyRank = new BuyRanks(this);
        ReportWriter = new UtilityReportWriter(this);
        Playertime = new HashMap<>();
    }

    public void OnSetupSkills() {
        
        RankUpConfig = new HashMap<>();
        BroadCast = new HashMap<>();
        Health = new HashMap<>();
        isRankExist = new HashMap<>();
        BuyRankEnabled = new HashMap<>();
        BuyRankUsePerms = new HashMap<>();
        XpRanks = new HashMap<>();
        BuksRanks = new HashMap<>();

        SetupAccessor("POWERLEVEL", new ConfigAccessor(this, "powerlevel.yml"));
        SetupAccessor("EXCAVATION", new ConfigAccessor(this, "excavation.yml"));
        SetupAccessor("FISHING", new ConfigAccessor(this, "fishing.yml"));
        SetupAccessor("HERBALISM", new ConfigAccessor(this, "herbalism.yml"));
        SetupAccessor("MINING", new ConfigAccessor(this, "mining.yml"));
        SetupAccessor("AXES", new ConfigAccessor(this, "axes.yml"));
        SetupAccessor("ARCHERY", new ConfigAccessor(this, "archery.yml"));
        SetupAccessor("SWORDS", new ConfigAccessor(this, "swords.yml"));
        SetupAccessor("TAMING", new ConfigAccessor(this, "taming.yml"));
        SetupAccessor("UNARMED", new ConfigAccessor(this, "unarmed.yml"));
        SetupAccessor("ACROBATICS", new ConfigAccessor(this, "acrobatics.yml"));
        SetupAccessor("REPAIR", new ConfigAccessor(this, "repair.yml"));
        SetupAccessor("WOODCUTTING", new ConfigAccessor(this, "woodcutting.yml"));
        SetupAccessor("SMELTING", new ConfigAccessor(this, "smelting.yml"));
        
        SetupAccessor("CUSTOM", new ConfigAccessor(this, "custom.yml"));
    }    
    
    public void MessagesReplaces() {
        Message.BuyPurchaseBuks = Message.BuyPurchaseBuks.replace("%currencyname%", BuyRankCurrencyName);
        Message.HabilitySetFail = Message.PlayerWarnings + Message.HabilitySetFail.replace("%colorreset%", Message.PlayerWarnings);
    }

    public HashMap<String, ArrayList<String>> GetRanks(ConfigAccessor ca) throws IOException {
        HashMap<String, ArrayList<String>> Ranks = new HashMap<>();
        ArrayList<String> Rank = new ArrayList<>();
        for (String key : ca.getConfig().getConfigurationSection("RankUp.Male.").getKeys(false)) {
            Rank.add(key + "," + ca.getConfig().getString("RankUp.Male." + key));
        }
        Ranks.put("Male", Rank);

        if (UseGenderClass) {						
            Rank = new ArrayList<>();
            for (String key : ca.getConfig().getConfigurationSection("RankUp.Female.").getKeys(false)) {
                Rank.add(key + "," + ca.getConfig().getString("RankUp.Female." + key));
            }
            Ranks.put("Female", Rank);
        }
        
        if(!ca.fileName.toLowerCase().contains("custom")) return Ranks;
        
        Rank = new ArrayList<>();
        CustomAvaibleRanks = new ArrayList<>();
        for (String key : ca.getConfig().getConfigurationSection("ExtraBaseRank.").getKeys(false)){
            String customRank = ca.getConfig().getString("ExtraBaseRank." + key);
            for (String customKey : ca.getConfig().getConfigurationSection("RankUp."+ customRank + ".").getKeys(false)) {
                Rank.add(customKey + "," + ca.getConfig().getString("RankUp."+ customRank + "." + customKey));
            }
            sendMessage(String.format("%s Adding custom rank %s",new Object[]{logPrefix, customRank}));
            Ranks.put(customRank,Rank);
            CustomAvaibleRanks.add(customRank);
        }
        
        return Ranks;
    }

    public HashMap<String, String> GetAlternativeBroadcast(ConfigAccessor ca) throws IOException {
        HashMap<String, String> BroadCastCa = new HashMap<>();
        for (String key : ca.getConfig().getConfigurationSection("Broadcast.").getKeys(false)) {
            BroadCastCa.put(key, ca.getConfig().getString("Broadcast." + key));
        }
        return BroadCastCa;
    }
    
    public HashMap<String, Double> GetHealth(ConfigAccessor ca) throws IOException {
        HashMap<String, Double> HealthCa = new HashMap<>();
        for (String key : ca.getConfig().getConfigurationSection("Health.").getKeys(false)) {
            HealthCa.put(key.toUpperCase(), ca.getConfig().getDouble("Health." + key));
        }
        return HealthCa;
    }
    
    public void SetupAccessor(String skill, ConfigAccessor ca) {
        boolean canBuy = false;
        
        try {
            if (isSkillEnable(skill)) {
                RankUpConfig.put(skill, GetRanks(ca));
                Health.put(skill, GetHealth(ca));
                isRankExist.put(skill, true);
                if (UseAlternativeBroadcast) {
                    BroadCast.put(skill, GetAlternativeBroadcast(ca));
                }
                ca.setupConfig();
                
                if(skill.equalsIgnoreCase("CUSTOM")){
                    // TODO : LOADING REQUERIMENTS
                    CustomRequirements = new HashMap<>();
                    for (String key : ca.getConfig().getConfigurationSection("Requirements.").getKeys(false)) {
                        HashMap <String,String> newRequirement = new HashMap();
                        for(String keyRequirement : ca.getConfig().getConfigurationSection("Requirements." + key).getKeys(false)) {
                            newRequirement.put(keyRequirement, ca.getConfig().getString("Requirements." + key + "." + keyRequirement)); 
                        }
                        CustomRequirements.put(key.toUpperCase(),newRequirement);
                    }
                    if (mruStartupSummary) {
                        logger.log(Level.INFO, "{0} {1} - Loaded", new Object[]{logPrefix, skill.toUpperCase()});
                    }
                    return;
                }
                
                BuyRankUsePerms.put(skill, ca.getConfig().getConfigurationSection("BuyRank").getBoolean("usepermissions") );
                
                if (AllowBuyingRanks && ca.getConfig().getConfigurationSection("BuyRank").getBoolean("enabled") ) {
                    BuyRankEnabled.put(skill, true);
                    XpRanks.put(skill, BuyRanks.getRankBuyXP(ca));
                    BuksRanks.put(skill, BuyRanks.getRankBuyBuks(ca));
                    canBuy = true;
                }

                if (mruStartupSummary) {
                    logger.log(Level.INFO, "{0} {1} - Loaded | BuyRanks {2}", new Object[]{logPrefix, skill.toUpperCase(),canBuy ? "On" : "Off"});
                }
            } else {
                if (mruStartupSummary) {
                    logger.log(Level.INFO, "{0} {1} is disabled.", new Object[]{logPrefix, skill});
                }
                isRankExist.put(skill, false);
                BuyRankEnabled.put(skill, false);
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "{0} {1} - Ability Rank file is either corrupt and/or missing.", new Object[]{logPrefix, skill});
            isRankExist.put(skill, false);
            BuyRankEnabled.put(skill, false);
        }
    }

    private boolean isSkillEnable(String skill) {
        boolean enabled = config.getConfig().getBoolean("Config.Skills." + skill + ".enabled");

        if (!enabled && skill.equalsIgnoreCase(DefaultSkill)) {
            logger.log(Level.WARNING, "{0} - Default Skill ({1}) was disabled. Enabling...", new Object[]{logPrefix, skill.toUpperCase()});
            config.getConfig().set("Config.Skills." + skill + ".enabled", true);
            config.saveConfig();
            enabled = true;
        }

        return enabled;
    }

    public boolean CheckRankExist(String Skill) {
        boolean exists = false;
        for (String key : isRankExist.keySet()) {
            if (Skill.equalsIgnoreCase(key)) {
                exists = isRankExist.get(key);
            }
        }
        return exists;
    }

    public int GetRankLevel(String skill, String gender, String rank) {
        int rankLevel = 0;

        for (String entry : RankUpConfig.get(skill).get(gender)) {
            String[] levelGroup = entry.split(",");
            if (levelGroup[1].equalsIgnoreCase(rank)) {
                rankLevel = Integer.parseInt(levelGroup[0]);
                break;
            }
        }
        return rankLevel;
    }
    
    public boolean isRankMaxLevel(String skill, String gender,int level) {
        for (String entry : RankUpConfig.get(skill).get(gender)) {
            String[] levelGroup = entry.split(",");
            if (Integer.parseInt(levelGroup[0]) > level) {
                return false;
            }
        }
        return true;
    }
    
    public long GetCurrentMilli() {
        return System.currentTimeMillis();
    }

    public boolean hasPermission(Player player, String node) {
        return permission.has(player.getWorld(), player.getName(), node.toLowerCase());
    }

    public int GetSkillLevel(Player player, String skill) {
        if (skill.equalsIgnoreCase("CUSTOM")) return GetCustomLevel(player);
        if (skill.equalsIgnoreCase("POWERLEVEL")) {
            return McMMOApi.getPowerLevel(player);
        } else {
            return McMMOApi.getSkillLevel(player, skill);
        }
    }

    public int GetCustomLevel(Player player){
        int PlayerLevel = 0;
        
        for(int level=0;level<=CustomRequirements.size();level++){
            Map<String,String> requirements = CustomRequirements.get(String.valueOf(level));
            int howmanyreq = requirements.size();
            int playerpasshowmany = 0;
            for(String requirementName:requirements.keySet()){
                int requirementAmountint = 0;
                String requimentAmountstring = "";
                if(requirementName.equalsIgnoreCase("world") || requirementName.equalsIgnoreCase("regionworldguard")){
                    requimentAmountstring = requirements.get(requirementName);
                }else{
                    requirementAmountint = Integer.parseInt(requirements.get(requirementName));
                }
                playerpasshowmany = CheckRequerimentLevel(requirementName, player, requirementAmountint, requimentAmountstring, playerpasshowmany);
            }
            if(playerpasshowmany >= howmanyreq){
                PlayerLevel = level;
            }else{
                break;
            }
        }
        return PlayerLevel;
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
           double balance = economy.getBalance(player);
           if(balance >= requirementAmountint) passhowmany++;
        }
        
        Profile profile = new Profile(this, player);
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
            com.sk89q.worldguard.bukkit.RegionContainer container = getWorldGuard().getRegionContainer();
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
    
    public int GetSkillLevelOffline(String playerName, String skill) {
        if (skill.equalsIgnoreCase("POWERLEVEL")) {
            return McMMOApi.getPowerLevelOffline(playerName);
        } else {
            return McMMOApi.getSkillLevelOffline(playerName, skill);
        }
    }

    public Boolean GroupToIgnore(Player player) {
        for (String Group : GroupToIgnore) {
            if (Group.equalsIgnoreCase(permission.getPrimaryGroup(player))) {
                return true;
            }
        }
        return false;
    }

    public double GetPlayerCurrency(Player pl) {
        return economy.getBalance(pl.getName());
    }

    public String GetPlayerCurrentGroup(Player pl) {
        return permission.getPrimaryGroup(pl.getWorld(), pl.getName());
    }

    public boolean isIgnored(Player pl) {
        if (hasPermission(pl,"mru.ignore") || GroupToIgnore(pl)) {
            return true;
        }
        return false;
    }

    public boolean isRankAvailable(String skill, Player pl) {
        if(skill.toLowerCase().contains("powerlevel")) return true;
        if(skill.toLowerCase().contains("custom")) return true;
        return hasPermission(pl, "mcmmo.skills." + skill.toLowerCase());
    }
   
    public WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        return (WorldGuardPlugin) plugin;
    }
}
