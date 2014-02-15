/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.stutiguias.mcmmorankup.command;

import me.stutiguias.mcmmorankup.Mcmmorankup;
import me.stutiguias.mcmmorankup.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

/**
 *
 * @author Daniel
 */
public class View extends CommandHandler {

    public View(Mcmmorankup plugin) {
        super(plugin);
    }

    @Override
    protected Boolean OnCommand(CommandSender sender, String[] args) {
        this.sender = sender;
        
        Profile profile = new Profile(plugin,(Player)sender);
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        
        if(args.length > 1 && args[1].equalsIgnoreCase("on")) {
            Scoreboard board = manager.getNewScoreboard();
            Objective objective = board.registerNewObjective("mru","dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.setDisplayName("Rank Line");
            
            Score score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + profile.GetHabilityForRank()));
            score.setScore(profile.GetSkillLevelRankLine()); 
            
            profile.player.setScoreboard(board);
            return true;
        }else{
            profile.player.setScoreboard(manager.getNewScoreboard());
            return true;
        }
    }

    @Override
    protected Boolean isInvalid(CommandSender sender, String[] args) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
