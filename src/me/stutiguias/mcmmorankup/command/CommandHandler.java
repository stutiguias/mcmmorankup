/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.stutiguias.mcmmorankup.command;

import me.stutiguias.mcmmorankup.Mcmmorankup;
import me.stutiguias.mcmmorankup.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Daniel
 */
public abstract class CommandHandler extends Util { 
    
    public Player player;
    public final String MsgHr;
    
    public CommandHandler(Mcmmorankup plugin) {
        super(plugin);
        this.MsgHr = "&e-----------------------------------------------------";
    }

    protected boolean ParseToggleInput(String parse) {
        return parse.matches("[oO]n|[oO]ff|[tT]rue|[fF]alse|[T]RUE|[F]ALSE");
    }
    
    protected abstract Boolean OnCommand(CommandSender sender, String[] args);
    protected abstract Boolean isInvalid(CommandSender sender, String[] args);
}
