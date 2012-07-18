/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.mcmmorankup;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.stutiguias.apimcmmo.PowerLevel;
import me.stutiguias.listeners.CommandListener;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
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
    public Economy economy = null;
    public PowerLevel PowerLevel = null;
    public ArrayList<String> RankLevel;
    public String[] PlayerToIgnore;
    public String[] GroupToIgnore;
    public HashMap<String,Long> Playertime;
    public Integer total;
    public String MPromote;
    public String MSucess;
    public String MFail;
    
    @Override
    @SuppressWarnings("LoggerStringConcat")
    public void onEnable() {

            log.log(Level.INFO,logPrefix + "Mcmmorankup is initializing.");

            onLoadConfig();
            PowerLevel = new PowerLevel(this);
            getCommand("mru").setExecutor(new CommandListener(this));

            setupEconomy();
            setupPermissions();

            if(this.permission.isEnabled() == true)
            {
                log.log(Level.INFO,logPrefix + "Vault perm enable.");    
            }else{
                log.log(Level.INFO,logPrefix + "Vault NOT ENABLE.");    
            }

    }

    @Override
    public void onDisable() {
            log.log(Level.INFO, logPrefix + " Disabled. Bye :D");
    }

    private void initConfig() {
                getConfig().addDefault("Message.RankUp", "Player %player% promote to %group%");
                getConfig().addDefault("Message.Sucess", "Promote Sucess");
                getConfig().addDefault("Message.Fail", "Promote Fail");
                getConfig().addDefault("PlayerToIgnore", "Stutiguias,Player2");
                getConfig().addDefault("GroupToIgnore","Admin,Moderator");
                HashMap<Integer, String> rl = new HashMap<Integer, String>();
                rl.put(100, "test");
                rl.put(200, "test2");
                rl.put(300, "test3"); 
                getConfig().addDefault("PowerLevelRankUp", rl);
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

}
