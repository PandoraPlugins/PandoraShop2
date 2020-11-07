package me.nanigans.pandorashop2.Commands;

import me.nanigans.pandorashop2.PandoraShop2;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class CreateShop implements CommandExecutor {
    private final PandoraShop2 plugin = PandoraShop2.getPlugin(PandoraShop2.class);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(command.getName().equalsIgnoreCase("createshop")) {
            if (sender instanceof Player) {

                if (args.length > 0) {
                    Player player = ((Player) sender);

                    NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, ChatColor.translateAlternateColorCodes('&', args[0]));
                    npc.spawn(player.getLocation());
                    npc.data().set("IsShop", plugin.getDataFolder() + "/Shops/" + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', npc.getName())));

                    player.sendMessage(ChatColor.GREEN+"Shop: " + ChatColor.YELLOW+args[0] + ChatColor.GREEN+" created!");

                } else {
                    sender.sendMessage(ChatColor.RED + "Please specify a shop name");
                }
                return true;

            } else {
                sender.sendMessage(ChatColor.RED + "Only players may use this command");
                return true;
            }

        }
        return false;
    }
}
