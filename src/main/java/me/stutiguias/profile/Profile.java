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
        configplayerfile = new File("plugins/Mcmmorankup/userdata/"+ player.getName() +".yml");
        boolean alreadyexist = false;
        try {
            alreadyexist = configplayerfile.createNewFile();
        }catch(IOException ex) {
            Mcmmorankup.log.warning(plugin.logPrefix + " Can't create the user file" + ex.getMessage() );
        }
        
        if(alreadyexist) {
            Mcmmorankup.log.info( plugin.logPrefix + " Profile of user " + player.getName() + " found!" );
        }else{
            Mcmmorankup.log.info( plugin.logPrefix + " Profile of user " + player.getName() + " not found, create new one!" );
        }
        
        PlayerYML = new YamlConfiguration();
        
        try {
            PlayerYML.load(configplayerfile);
        } catch (FileNotFoundException ex) {
           Mcmmorankup.log.warning(plugin.logPrefix + " File Not Found " + ex.getMessage() );
        } catch (IOException ex) {
           Mcmmorankup.log.warning(plugin.logPrefix + " IO Problem " + ex.getMessage() );
        } catch (InvalidConfigurationException ex) {
           Mcmmorankup.log.warning(plugin.logPrefix + " Invalid Configuration " + ex.getMessage() );
        }
    }
    
    public boolean setHabilityForRank(String HabilityForRank) {
        PlayerProfile _playerprofile;
        try {
            _playerprofile = Users.getProfile(player);
        }catch(Exception ex) {
            Mcmmorankup.log.info(" Can't find profile for player " + player.getName());
            Mcmmorankup.log.info(ex.getMessage());
            player.sendMessage(plugin.logPrefix + " " + plugin.NotHaveProfile);
            return false;
        }
        if(_playerprofile != null) {
            switch(Habilitys.valueOf(HabilityForRank)) {
                case EXCAVATION:
                    SendMessage(player, HabilityForRank);
                    break;
                case FISHING:
                    SendMessage(player, HabilityForRank);
                    break;
                case HERBALISM:
                    SendMessage(player, HabilityForRank);
                    break;
                case MINING:
                    SendMessage(player, HabilityForRank);
                    break;
                case AXES:
                    SendMessage(player, HabilityForRank);
                    break;
                case ARCHERY:
                    SendMessage(player, HabilityForRank);
                    break;
                case SWORDS:
                    SendMessage(player, HabilityForRank);
                    break; 
                case TAMING:
                    SendMessage(player, HabilityForRank);
                    break;
                case UNARMED:
                    SendMessage(player, HabilityForRank);
                    break;
                case ACROBATICS:
                    SendMessage(player, HabilityForRank);
                    break;
                case REPAIR: 
                    SendMessage(player, HabilityForRank);
                    break;
                default:
                    return false;
                
            }
                
        }
        PlayerYML.addDefault("HabilityForRank", HabilityForRank);
        return true;
    }
    
    public String getHabilityForRank(){
        return PlayerYML.getString("HabilityForRank");
    }
    
    public enum Habilitys 
    {
        EXCAVATION,FISHING,HERBALISM,MINING,AXES,ARCHERY,SWORDS,TAMING,UNARMED,ACROBATICS,REPAIR;
    }
    
    public void SendMessage(Player player,String Hability) {
             player.sendMessage("-----------------------------------------------------");
             player.sendMessage(plugin.parseColor(plugin.ChooseHability.replace("%hability%", Hability)));
             player.sendMessage("-----------------------------------------------------");
    }
}
