package me.nanigans.pandorashop2.Utils.Items;

import me.nanigans.pandorashop2.PandoraShop2;
import me.nanigans.pandorashop2.Utils.Config.JsonUtils;
import me.nanigans.pandorashop2.Utils.PathUtils.ShopPath;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Map;

public class InventoryUtils {

    private static final PandoraShop2 plugin = PandoraShop2.getPlugin(PandoraShop2.class);

    /**
     * Creates a new inventory based on the parameters given
     * @param shopPath the path for the json file containing the shop information
     * @param page the page of the shop
     * @param player the player who needs the shop opened
     * @return a new inventory with the itemstack contents
     * @throws IOException fails to read a json file
     * @throws ParseException fails to parse json data
     */
    public static Inventory createInventoryShop(String shopPath, int page, Player player) throws IOException, ParseException {

        shopPath = ShopPath.shopPath(shopPath, plugin);
        System.out.println("shopPath = " + shopPath);
        JsonUtils json = new JsonUtils(shopPath);

        String invPath = "page"+page;

        Inventory inventory = Bukkit.createInventory(player, Integer.parseInt(json.getData(invPath+".size").toString()),
                ChatColor.translateAlternateColorCodes('&', json.getData("inventoryName").toString()));

        if(json.getData(invPath) != null) {

            Map<String, Object> itemMap = ShopPath.getConfigSectionValue(json.getData(invPath+".items"), false);

            for (Map.Entry<String, Object> itemEntr : itemMap.entrySet()) {

                ItemStack item = Items.createShopItem(shopPath, itemEntr.getKey(), page);
                inventory.setItem(Integer.parseInt(itemEntr.getKey()), item);

            }

        }


        return inventory;

    }


}
