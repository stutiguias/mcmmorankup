/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.profile;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.util.Users;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import me.stutiguias.mcmmorankup.Mcmmorankup;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author Daniel
 */
public class Profile {
    
    Player player;
    Mcmmorankup plugin;
    String HabilityForRank;
    File configplayerfile;
    YamlConfiguration PlayerYML;
    
    public Profile(Mcmmorankup plugin,Player player) {
        configplayerfile = new File("plugins"+ File.separator +"Mcmmorankup"+ File.separator +"userdata"+ File.separator + player.getName() +".yml");
        PlayerYML = new YamlConfiguration();
        boolean havetocreate = false;
        try {
            havetocreate = configplayerfile.createNewFile();
        }catch(IOException ex) {
            Mcmmorankup.logger.log(Level.WARNING, "{0} Can't create the user file {1}", new Object[]{plugin.logPrefix, ex.getMessage()});
        }
        initLoadYML();
        if(havetocreate) {
            Mcmmorankup.logger.log( Level.INFO, "{0} Profile of user {1} not found, create new one!", new Object[]{plugin.logPrefix, player.getName()});
            PlayerYML.set("Gender", "Male");
            PlayerYML.set("HabilityForRank", plugin.DefaultSkill);
            PlayerYML.set("Tag","");
            if(setInitRank()) Mcmmorankup.logger.log(Level.INFO, "Player {0} rank line is {1}", new Object[]{player.getName(), plugin.DefaultSkill});
        }
        this.player = player;
        this.plugin = plugin;

    }
    
    private boolean setInitRank() {
       SaveYML();
       return true;
    }
    
    public boolean setHabilityForRank(String HabilityForRank) {
        PlayerProfile _playerprofile;
        try {
            _playerprofile = Users.getProfile(player);
        }catch(Exception ex) {
            Mcmmorankup.logger.log(Level.INFO, " Can't find profile for player {0}", player.getName());
            Mcmmorankup.logger.info(ex.getMessage());
            player.sendMessage(plugin.logPrefix + " " + plugin.NotHaveProfile);
            return false;
        }
        if(_playerprofile != null) {
                
                if(HabilityForRank.equalsIgnoreCase("EXCAVATION")) {
                    SendMessage(player, HabilityForRank);
                } else if(HabilityForRank.equalsIgnoreCase("FISHING")) {
                    SendMessage(player, HabilityForRank);
                } else if(HabilityForRank.equalsIgnoreCase("HERBALISM")) {
                    SendMessage(player, HabilityForRank);
                } else if(HabilityForRank.equalsIgnoreCase("MINING")) {
                    SendMessage(player, HabilityForRank);
                } else if(HabilityForRank.equalsIgnoreCase("AXES")) {
                    SendMessage(player, HabilityForRank);
                } else if(HabilityForRank.equalsIgnoreCase("ARCHERY")) {
                    SendMessage(player, HabilityForRank);
                } else if(HabilityForRank.equalsIgnoreCase("SWORDS")) {
                    SendMessage(player, HabilityForRank);
                } else if(HabilityForRank.equalsIgnoreCase("TAMING")) {
                    SendMessage(player, HabilityForRank);
                } else if(HabilityForRank.equalsIgnoreCase("UNARMED")) {
                    SendMessage(player, HabilityForRank);
                } else if(HabilityForRank.equalsIgnoreCase("ACROBATICS")) {
                    SendMessage(player, HabilityForRank);
                } else if(HabilityForRank.equalsIgnoreCase("REPAIR")) { 
                    SendMessage(player, HabilityForRank);
                } else if(HabilityForRank.equalsIgnoreCase("POWERLEVEL")) {
                    SendMessage(player, HabilityForRank);
                } else{
                    return false;
                }
        }
                
        PlayerYML.set("HabilityForRank", HabilityForRank);
        SaveYML();
        return true;
    }
    
    public Boolean setTag(String Tag) {
        PlayerYML.set("Tag",Tag);
        SaveYML();
        return true;
    }
    
    public String getTag() {
        return PlayerYML.getString("Tag");
    }
    
    public String getHabilityForRank(){
        return PlayerYML.getString("HabilityForRank");
    }
    
    public Boolean setGender(String gender) {
        PlayerYML.set("Gender", gender);
        SaveYML();
        return true;
    }
    
    public String getGender(){
        return PlayerYML.getString("Gender");
    }
    
    public void SendMessage(Player player,String Hability) {
             player.sendMessage("-----------------------------------------------------");
             player.sendMessage(plugin.parseColor(plugin.ChooseHability.replace("%hability%", Hability)));
             player.sendMessage("-----------------------------------------------------");
    }
    
    private void initLoadYML() {
        LoadYML();
    }
    
    public void LoadYML() {
        try {
            PlayerYML.load(configplayerfile);
        } catch (FileNotFoundException ex) {
           Mcmmorankup.logger.log(Level.WARNING, "{0} File Not Found {1}", new Object[]{plugin.logPrefix, ex.getMessage()});
        } catch (IOException ex) {
           Mcmmorankup.logger.log(Level.WARNING, "{0} IO Problem {1}", new Object[]{plugin.logPrefix, ex.getMessage()});
        } catch (InvalidConfigurationException ex) {
           Mcmmorankup.logger.log(Level.WARNING, "{0} Invalid Configuration {1}", new Object[]{plugin.logPrefix, ex.getMessage()});
        }
    }
    
    public void SaveYML() {
        try {
            PlayerYML.save(configplayerfile);
        } catch (FileNotFoundException ex) {
           Mcmmorankup.logger.log(Level.WARNING, "{0} File Not Found {1}", new Object[]{plugin.logPrefix, ex.getMessage()});
        } catch (IOException ex) {
           Mcmmorankup.logger.log(Level.WARNING, "{0} IO Problem {1}", new Object[]{plugin.logPrefix, ex.getMessage()});
        }
    }
}
