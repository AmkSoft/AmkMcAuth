package com.mooo.amksoft.amkmcauth.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.mooo.amksoft.amkmcauth.AuthPlayer;
import com.mooo.amksoft.amkmcauth.Config;
import com.mooo.amksoft.amkmcauth.Language;
import com.mooo.amksoft.amkmcauth.AmkMcAuth;
import com.mooo.amksoft.amkmcauth.AmkAUtils;

public class CmdRegister implements CommandExecutor {

    private final AmkMcAuth plugin;

    public CmdRegister(AmkMcAuth instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("register")) {
            if (!cs.hasPermission("amkauth.register")) {
                AmkAUtils.dispNoPerms(cs);
                return true;
            }
            if (args.length < 1) {
                cs.sendMessage(cmd.getDescription());
                return false;
            }
            if (!(cs instanceof Player)) {
                cs.sendMessage(ChatColor.RED + Language.COMMAND_NO_CONSOLE.toString());
                return true;
            }
            Player p = (Player) cs;
            AuthPlayer ap = AuthPlayer.getAuthPlayer(p);
            if (ap.isLoggedIn() || ap.isRegistered()) {
                cs.sendMessage(ChatColor.RED + Language.ALREADY_REGISTERED.toString());
                return true;
            }
            String rawPassword = args[0]; // no space support
            for (String disallowed : Config.disallowedPasswords) {
                if (!rawPassword.equalsIgnoreCase(disallowed)) continue;
                cs.sendMessage(ChatColor.RED + Language.DISALLOWED_PASSWORD.toString());
                return true;
            }
            if (ap.setPassword(rawPassword, Config.passwordHashType)) {
                this.plugin.getLogger().info(p.getName() + " " + Language.HAS_REGISTERED);
                cs.sendMessage(ChatColor.BLUE + Language.PASSWORD_SET_AND_REGISTERED.toString());
                BukkitTask reminder = ap.getCurrentReminderTask();
                if (reminder != null) reminder.cancel();
                ap.createLoginReminder(this.plugin);
            } else cs.sendMessage(ChatColor.RED + Language.PASSWORD_COULD_NOT_BE_SET.toString());
            return true;
        }
        return false;
    }

}
