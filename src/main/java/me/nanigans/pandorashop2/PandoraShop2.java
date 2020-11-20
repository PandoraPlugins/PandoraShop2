package me.nanigans.pandorashop2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.nanigans.pandorashop2.Commands.CreateShop;
import me.nanigans.pandorashop2.Commands.NBTGet;
import me.nanigans.pandorashop2.Commands.TabCompleter.CreateShopTab;
import me.nanigans.pandorashop2.Events.NPCEvents;
import me.nanigans.pandorashop2.Utils.Config.ConfigCreators;
import me.nanigans.pandorashop2.Utils.Glow;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class PandoraShop2 extends JavaPlugin {

    GsonBuilder gsonBuilder = new GsonBuilder()
            .registerTypeAdapter(new TypeToken<Map<String, Object>>(){}.getType(),  new CustomizedObjectTypeAdapter());
    public Map<String, Object> map = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        registerGlow();
        getCommand("nbtshow").setExecutor(new NBTGet());
        getCommand("createshop").setExecutor(new CreateShop());
        getCommand("createshop").setTabCompleter(new CreateShopTab());
        getServer().getPluginManager().registerEvents(new NPCEvents(), this);

        File configFile = new File(getDataFolder()+"/InventoryPage.json");

        if(!configFile.exists()) {

            saveResource(configFile.getName(), false);
            try {
                Gson gson = gsonBuilder.create();

                map = gson.fromJson(new FileReader(configFile), HashMap.class);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        try {
            ConfigCreators.createFile(getDataFolder() + "/Shops/Shop/Categories.json");
            ConfigCreators.createDefaultJsonData(new File(getDataFolder() + "/Shops/Shop/Categories.json"), this);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void registerGlow() {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Glow glow = new Glow(70);
            Enchantment.registerEnchantment(glow);
        }
        catch (IllegalArgumentException ignored){
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static boolean hasPerms(Player player, String permission){

        return UltraPermissions.getAPI().getUsers().uuid(player.getUniqueId()).getGroups().stream().flatMap(j -> j.getAdditionalPermissions().stream()).anyMatch(j -> j.getName().equals(permission))
                || UltraPermissions.getAPI().getUsers().uuid(player.getUniqueId()).getGroups().stream().flatMap(j -> j.getPermissions().stream()).anyMatch(j -> j.getName().equals(permission))
                || UltraPermissions.getAPI().getUsers().uuid(player.getUniqueId()).getAllPermissions().stream().anyMatch(j -> j.getName().equals(permission));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
