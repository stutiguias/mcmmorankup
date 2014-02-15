/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.stutiguias.mcmmorankup.command;

import me.stutiguias.mcmmorankup.Mcmmorankup;
import static me.stutiguias.mcmmorankup.Mcmmorankup.Message;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Daniel
 */
public class Ver extends CommandHandler {

    public Ver(Mcmmorankup plugin) {
        super(plugin);
    }

    @Override
    protected Boolean OnCommand(CommandSender sender, String[] args) {
        this.sender = sender;
        SendMessage("VERSION INFO.");
        SendMessage(plugin.getDescription().getVersion());
        SendMessage(Message.MessageSeparator);
        return true;
    }

    @Override
    protected Boolean isInvalid(CommandSender sender, String[] args) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
