package me.nanigans.pandorashop2.Commands.TabCompleter;

import me.nanigans.pandorashop2.PandoraShop2;
import me.nanigans.pandorashop2.Utils.Config.ConfigCreators;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CreateShopTab implements org.bukkit.command.TabCompleter {
    private final PandoraShop2 plugin = PandoraShop2.getPlugin(PandoraShop2.class);

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if(command.getName().equalsIgnoreCase("createshop")){

            if(args.length == 1){

                try {

                    File diectories = ConfigCreators.createPath(plugin.getDataFolder()+"/Shops");
                    if(diectories.list() != null)
                    return Arrays.stream(diectories.list()).filter(i -> !i.contains(".json") && !i.startsWith(".DS")).filter(i -> i.startsWith(args[0])).collect(Collectors.toList());

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

        return null;
    }
}
