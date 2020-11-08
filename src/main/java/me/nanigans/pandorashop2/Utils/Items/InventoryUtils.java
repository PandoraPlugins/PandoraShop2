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
import java.util.HashMap;
import java.util.Map;

public class InventoryUtils {

    private static final PandoraShop2 plugin = PandoraShop2.getPlugin(PandoraShop2.class);


    public static ItemStack getBuyingItem(String purchaseInvPath, Inventory inv, int page) throws IOException, ParseException {

        JsonUtils json = new JsonUtils(purchaseInvPath+"/PurchaseInventory.json");
        Map<String, Object> itemData = (Map<String, Object>) json.getData("page"+page+".items");

        for (Map.Entry<String, Object> stringObjectEntry : itemData.entrySet()) {

            if(stringObjectEntry.getValue().toString().equalsIgnoreCase("boughtItem")){
                return inv.getItem(Integer.parseInt(stringObjectEntry.getKey()));
            }

        }
        return null;
    }

    /**
     * Creates a new inventory to actually purchase items
     * @param shopPath the path to the purchaseinventory
     * @param page the page number of the previous inventory
     * @param itemPurchased - the position in the previous item that was purchased
     * @param player - the player viewing
     * @return a new inventory
     * @throws IOException a
     * @throws ParseException a
     */
    public static Inventory createPurchaseInventory(String shopPath, String previousShopPath, int page, Map<String, Object> itemPurchased, Player player) throws IOException, ParseException {

        shopPath = ShopPath.shopPath(shopPath, plugin);
        JsonUtils jsonPurchase = new JsonUtils(shopPath);//purchaseInventory json
        //JsonUtils jsonClicked = new JsonUtils(previousShopPath); // item clicked inv

        final Map<String, Object> purchaseInvData = ShopPath.getConfigSectionValue(jsonPurchase.getData("page1.items"), true);

        Inventory inventory = Bukkit.createInventory(player, Integer.parseInt(jsonPurchase.getData("page1.size").toString()),
                ChatColor.translateAlternateColorCodes('&', jsonPurchase.getData("inventoryName").toString()));

        ItemStack itemBought = Items.createShopItem(itemPurchased);

        for (Map.Entry<String, Object> posItemEntry : purchaseInvData.entrySet()) {

            ItemStack item;

            if(posItemEntry.getValue().toString().equalsIgnoreCase("boughtItem")){
                item = itemBought;
            }else
                item = Items.createShopItem(previousShopPath, posItemEntry.getKey(), page);

            inventory.setItem(Integer.parseInt(posItemEntry.getKey()), item);
        }

        return inventory;

    }


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

    public static int getAmountInInv(ItemStack item, Inventory inv){

        final HashMap<Integer, ? extends ItemStack> all = inv.all(item);
        return all.values().stream().map(i -> i.getAmount()).reduce(0, Integer::sum);

    }

    public static Inventory copyInventory(Inventory inv){

        Inventory newInv = Bukkit.createInventory(inv.getHolder(), inv.getSize(), inv.getName());

        newInv.setContents(inv.getContents());

        return newInv;

    }


}
