package rama.npcintroductions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static rama.npcintroductions.NPCIntroductions.*;

public class Commands implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission("npci.admin")){
            if(args.length == 0){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&lNPCIntroductions &f/npci reload"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&lNPCIntroductions &f/npci resetData [all/player name]"));
            }else{
                if(args[0].equals("reload")){
                    plugin.reloadConfig();
                    try {
                        reloadData();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&lNPCIntroductions &fYou have successfully reloaded the plugin files."));
                }
                if(args[0].equals("resetData")){
                    if(args.length != 2){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&lNPCIntroductions &cWrong arguments!"));
                    }else{
                        if(args[1].equalsIgnoreCase("all")){
                            for(String ID : getData().getKeys(false)){
                                List<String> uuids = getData().getStringList(ID);
                                for (int i = 0; i < uuids.size(); i++){
                                    uuids.remove(uuids.get(i));
                                    getData().set(String.valueOf(i), uuids);
                                    try {
                                        reloadData();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&lNPCIntroductions &fYou have successfully reset the introductions data for all players."));
                        }else{
                            if(Bukkit.getPlayer(args[1]) == null){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&lNPCIntroductions &cOffline player."));
                                return false;
                            }
                            Player player = Bukkit.getPlayer(args[1]);
                            for(String ID : getData().getKeys(false)){
                                List<String> uuids = getData().getStringList(ID);
                                uuids.remove(player.getUniqueId().toString());
                                getData().set(ID, uuids);
                                try {
                                    reloadData();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&lNPCIntroductions &fYou have successfully reset the introductions data for "+player.getName()+"."));
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();
        if(sender.hasPermission("npci.admin")) {
            if(args.length == 1) {
                commands.add("reload");
                commands.add("resetData");
                StringUtil.copyPartialMatches(args[0], commands, completions);
                Collections.sort(completions);
                return completions;
            } else if (args.length == 2 && args[0].equals("resetData")) {
                List<String> arguments = new ArrayList<>();
                List<String> arguments2 = new ArrayList<>();
                arguments.add("all");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    arguments.add(player.getDisplayName());
                }
                StringUtil.copyPartialMatches(args[1], arguments, arguments2);
                Collections.sort(arguments2);
                return arguments2;
            }
        }
        return null;
    }
}
