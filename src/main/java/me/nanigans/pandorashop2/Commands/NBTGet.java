package me.nanigans.pandorashop2.Commands;

import me.nanigans.pandorashop2.PandoraShop2;
import me.nanigans.pandorashop2.Utils.Items.Items;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class NBTGet implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(command.getName().equalsIgnoreCase("nbtshow")){

            if(sender instanceof Player){

                Player player = (Player) sender;

                if(PandoraShop2.hasPerms(player, "NBT.ShowAll")){

                    if(player.getInventory().getItemInHand() != null) {

                        final Map<String, String> allNBT = Items.getAllNBT(player.getInventory().getItemInHand());

                        if(allNBT == null){
                            player.sendMessage(ChatColor.RED+"Unable to find any data");
                            return true;
                        }

                        StringBuilder data = new StringBuilder();

                        data.append("All NBT Data\n");

                        for (Map.Entry<String, String> string : allNBT.entrySet()) {
                            data.append(ChatColor.GOLD).append(string.getKey()).append(": ").append(ChatColor.YELLOW).append(string.getValue()).append("\n");
                        }

                        player.sendMessage(data.toString());

                    }else{
                        player.sendMessage(ChatColor.RED+"Please hold an item");
                    }


                }else{
                    player.sendMessage(ChatColor.RED+"Invalid permissions");
                }

            }else{
                sender.sendMessage(ChatColor.RED+"Only players may use this command");
            }
            return true;

        }

        return false;
    }
}
