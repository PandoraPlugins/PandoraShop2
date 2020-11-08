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
     * Creates an extra item purchase inventory
     * @param player the player that wants to purchase more items
     * @param itemData the item data for the item clicked
     * @param item the item being sold
     * @return a new inventory with extra items to buy
     */
    public static Inventory createExtraItemsInventory(Player player, Map<String, Object> itemData, ItemStack item) {

        Map<String, Object> shopData = (Map<String, Object>) itemData.get("shopData");
        Map<String, Object> extraItems = ((Map<String, Object>) shopData.get("extraItems"));

        if(extraItems != null) {
            Inventory inventory = Bukkit.createInventory(player, Integer.parseInt(extraItems.get("invSize").toString()),
                    ChatColor.translateAlternateColorCodes('&', extraItems.get("invName").toString()));

            Map<String, Object> itemPos = ((Map<String, Object>) extraItems.get("itemPositions"));

            if(itemPos != null && itemPos.size() > 0) {
                for (Map.Entry<String, Object> itemPositions : itemPos.entrySet()) {

                    ItemStack clone = item.clone();
                    clone.setAmount(Integer.parseInt(itemPositions.getKey()));
                    Items.updatePriceLore(clone, clone.getAmount()*64);

                    inventory.setItem(Integer.parseInt(itemPositions.getValue().toString()), clone);
                }

                return inventory;
            }
        }

        return null;

    }


    /**
     * Gets the item being bought in purchaseinventory when the player is looking at it
     * @param purchaseInvPath The path to the purchase inventory
     * @param inv the inventory currently looked at
     * @param page the page of the inventory
     * @return a new ItemStack of the item being bought
     * @throws IOException
     * @throws ParseException
     */
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
     * Gets the item being bought's json data
     * @param purchaseInvPath the path to the purchase inventory
     * @param page the page the player is on
     * @return the json data for the item
     * @throws IOException error
     * @throws ParseException error
     */

    public static Map<String, Object> getBuyingItemData(String purchaseInvPath, int page) throws IOException, ParseException {

        JsonUtils json = new JsonUtils(purchaseInvPath+"/PurchaseInventory.json");
        Map<String, Object> itemData = (Map<String, Object>) json.getData("page"+page+".items");

        for (Map.Entry<String, Object> stringObjectEntry : itemData.entrySet()) {

            if(stringObjectEntry.getValue().toString().equalsIgnoreCase("boughtItem")){
                return ((Map<String, Object>) itemData.get(stringObjectEntry.getKey()));
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

    /**
     * Gets an amount of an itemstack matching in an inventory
     * @param item the item to match
     * @param inv the inventory to match to
     * @return the amount of items in an inventory matching an item
     */
    public static int getAmountInInv(ItemStack item, Inventory inv){

        int amount = 0;
        for(ItemStack currItm : inv.getContents()){
            if(currItm != null && currItm.isSimilar(item))
                amount+=currItm.getAmount();

        }

        return amount;

    }

    /**
     * Copies an inventory to a new inventory
     * @param inv the inventory to copy
     * @return the new copied inventory
     */
    public static Inventory copyInventory(Inventory inv){

        Inventory newInv = Bukkit.createInventory(inv.getHolder(), inv.getSize(), inv.getName());

        newInv.setContents(inv.getContents());

        return newInv;

    }


}
