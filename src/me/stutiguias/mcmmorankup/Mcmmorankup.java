/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.mcmmorankup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.stutiguias.apimcmmo.PowerLevel;
import me.stutiguias.listeners.MRUCommandListener;
import me.stutiguias.listeners.MRUPlayerListener;
import me.stutiguias.mcmmorankup.task.UpdateTask;
import me.stutiguias.metrics.Metrics;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Stutiguias
 */
public class Mcmmorankup extends JavaPlugin {

    public String logPrefix = "[McMMoRankUp] ";
    String PluginDir = "plugins" + File.separator + "Mcmmorankup";
    public static final Logger log = Logger.getLogger("Minecraft");
    public Permission permission = null;
    public final MRUPlayerListener playerlistener = new MRUPlayerListener(this);
    public Economy economy = null;
    public PowerLevel PowerLevel = null;
    public ArrayList<String> RankLevel;
    public HashMap<String,String> RealBroadCast;
    public String[] PlayerToIgnore;
    public String[] GroupToIgnore;
    public HashMap<String,Long> Playertime;
    public Integer total;
    public String MPromote;
    public String MSucess;
    public String MFail;
    public String AutoUpdateTime;
    
    public boolean UseAlternativeBroadcast;
    public boolean PromoteOnJoin;
    public boolean AutoUpdate;
    
    @Override
    @SuppressWarnings("LoggerStringConcat")
    public void onEnable() {

            log.log(Level.INFO,logPrefix + "Mcmmorankup is initializing.");

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
                getServer().getScheduler().scheduleAsyncRepeatingTask(this, new UpdateTask(this), uptime, uptime);
            }
            
            if(this.permission.isEnabled() == true)
            {
                log.log(Level.INFO,logPrefix + "Vault perm enable.");    
            }else{
                log.log(Level.INFO,logPrefix + "Vault NOT ENABLE.");    
            }
            
            //Metrics 
            try {
              log.info(logPrefix + "Sending Metrics for help the dev... http://metrics.griefcraft.com :-)");
              Metrics metrics = new Metrics(this);
              metrics.start();
            } catch (IOException e) {
              log.info(logPrefix + "Failed to submit the stats :-(");
            }

    }

    @Override
    public void onDisable() {
            getServer().getPluginManager().disablePlugin(this);
            log.log(Level.INFO, logPrefix + " Disabled. Bye :D");
    }
    
    public void onReload() {
        this.reloadConfig();
        saveConfig();
        getServer().getPluginManager().disablePlugin(this);
        getServer().getPluginManager().enablePlugin(this);
    }

    private void initConfig() {
                PowerLevel = new PowerLevel(this);
                getConfig().addDefault("Message.RankUp", "Player %player% promote to %group%");
                getConfig().addDefault("Message.Sucess", "Promote Sucess");
                getConfig().addDefault("Message.Fail", "Promote Fail");
                getConfig().addDefault("Config.PromoteOnJoin", true);
                getConfig().addDefault("Config.AutoUpdate", true);
                getConfig().addDefault("Config.AutoUpdateTime", "1h");
                getConfig().addDefault("PlayerToIgnore", "Stutiguias,Player2");
                getConfig().addDefault("GroupToIgnore","Admin,Moderator");
                HashMap<Integer, String> rl = new HashMap<Integer, String>();
                rl.put(100, "test");
                rl.put(200, "test2");
                rl.put(300, "test3"); 
                getConfig().addDefault("PowerLevelRankUp", rl);
                getConfig().addDefault("UseAlternativeBroadCast", true);
                HashMap<String,String> broadcastName = new HashMap<String, String>();
                broadcastName.put("test","rank1");
                broadcastName.put("test2","rank2");
                broadcastName.put("test3","rank3");
                getConfig().addDefault("UseThisBroadcast", broadcastName);
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
            getRanks();
            getAlternativeBroadcast();
            UseAlternativeBroadcast = getConfig().getBoolean("UseAlternativeBroadCast");
            PromoteOnJoin = getConfig().getBoolean("Config.PromoteOnJoin");
            AutoUpdate = getConfig().getBoolean("Config.AutoUpdate");
            AutoUpdateTime = getConfig().getString("Config.AutoUpdateTime");
            log.log(Level.INFO,logPrefix + " Alternative Broadcast " + UseAlternativeBroadcast);
            PlayerToIgnore = getConfig().getString("PlayerToIgnore").split((","));
            GroupToIgnore = getConfig().getString("GroupToIgnore").split((","));
            MPromote = getConfig().getString("Message.RankUp");
            MSucess = getConfig().getString("Message.Sucess");
            MFail = getConfig().getString("Message.Fail");
            Playertime = new HashMap<String, Long>();
    }
   
    public long getCurrentMilli() {
		return System.currentTimeMillis();
    }
    
    public void getRanks(){
        total = 0;
        RankLevel = new ArrayList<String>();
        for (String key : getConfig().getConfigurationSection("PowerLevelRankUp.").getKeys(false)){
          RankLevel.add(key + "," + getConfig().getString("PowerLevelRankUp." + key));
          log.log(Level.INFO, logPrefix + "Rank " + key + " message " + getConfig().getString("PowerLevelRankUp." + key));
          total++;
        }
    }
    
    public String parseColor(String message) {
	 for (ChatColor color : ChatColor.values()) {
            message = message.replaceAll(String.format("&%c", color.getChar()), color.toString());
        }
        return message;
    }
    
    public void getAlternativeBroadcast(){
        RealBroadCast = new HashMap<String, String>();
        for (String key : getConfig().getConfigurationSection("UseThisBroadcast.").getKeys(false)){
          RealBroadCast.put(key, getConfig().getString("UseThisBroadcast." + key));
        }
    }
}
