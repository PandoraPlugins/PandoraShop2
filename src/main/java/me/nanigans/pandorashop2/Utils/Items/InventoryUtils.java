package me.nanigans.pandorashop2.Utils.Items;

import com.earth2me.essentials.Enchantments;
import me.nanigans.pandorashop2.Events.ShopClickEvents;
import me.nanigans.pandorashop2.PandoraShop2;
import me.nanigans.pandorashop2.Utils.Config.JsonUtils;
import me.nanigans.pandorashop2.Utils.PathUtils.ShopPath;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InventoryUtils {

    private static final PandoraShop2 plugin = PandoraShop2.getPlugin(PandoraShop2.class);


    /**
     * Creates a new enchantment page
     * @param player the player to enchant stuff
     * @param item the item clicked to get here
     * @param enchantment the enchantment to add
     * @return
     */
    public static Inventory createEnchantmentItemInventory(Player player, ItemStack item, Enchantment enchantment, Map<String, Object> itemInfo){

        int maxEnchant = enchantment.getMaxLevel();
        Inventory inv = Bukkit.createInventory(player, 27, "Add Enchantments");

        ItemStack backBtn = Items.genBackButton();
        inv.setItem(0, backBtn);

        for (int i = 0; i < maxEnchant; i++) {

            ItemStack enchClone = item.clone();
            enchClone.setAmount(i+1);
            ItemMeta meta = enchClone.getItemMeta();
            List<String> lore = meta.getLore();

            meta.addEnchant(enchantment, i+1, false);
            Items.updatePriceLore(enchClone, enchClone.getAmount(), itemInfo);
            String normalName = Enchantments.entrySet().stream().filter(e -> e.getValue().getName().equals(enchantment.getName())).collect(Collectors.toList()).get(0).getKey();

            lore.add("Set " + ChatColor.DARK_AQUA + (normalName == null ? enchantment.getName() : normalName) + " " + (i+1));
            lore.add("to your item. (This will not add the enchantment");
            lore.add("to previous enchantments)");
            meta.setLore(lore);
            enchClone.setItemMeta(meta);

            inv.setItem(i+11, enchClone);

        }

        return inv;

    }

    /**
     * Creates an extra item purchase inventory
     * @param player the player that wants to purchase more items
     * @param itemData the item data for the item clicked
     * @param item the item being sold
     * @return a new inventory with extra items to buy
     */
    public static Inventory createExtraItemsInventory(Player player, Map<String, Object> itemData, ItemStack item, Map<String, Object> itemInfo) {

        Map<String, Object> shopData = (Map<String, Object>) itemData.get("shopData");
        Map<String, Object> extraItems = ((Map<String, Object>) shopData.get("extraItems"));

        if(extraItems != null) {
            Inventory inventory = Bukkit.createInventory(player, Integer.parseInt(extraItems.get("invSize").toString()),
                    ChatColor.translateAlternateColorCodes('&', extraItems.get("invName").toString()));

            ItemStack backBtn = Items.genBackButton();
            inventory.setItem(0, backBtn);

            Map<String, Object> itemPos = ((Map<String, Object>) extraItems.get("itemPositions"));

            if(itemPos != null && itemPos.size() > 0) {
                for (Map.Entry<String, Object> itemPositions : itemPos.entrySet()) {

                    ItemStack clone = item.clone();
                    clone.setAmount(Integer.parseInt(itemPositions.getKey()));
                    Items.updatePriceLore(clone, clone.getAmount()*64, itemInfo);
                    final ItemMeta itemMeta = clone.getItemMeta();
                    final List<String> lore = itemMeta.getLore();
                    lore.add("Right click to sell");
                    lore.add("Left click to buy");
                    itemMeta.setLore(lore);
                    clone.setItemMeta(itemMeta);
                    if(itemPositions.getValue().equals("0"))
                        itemPositions.setValue("1");

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
     * @throws IOException err
     * @throws ParseException err
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

        final Map<String, Object> purchaseInvData = ShopPath.getConfigSectionValue(jsonPurchase.getData("page1.items"), true);

        Inventory inventory = Bukkit.createInventory(player, Integer.parseInt(jsonPurchase.getData("page1.size").toString()),
                ChatColor.translateAlternateColorCodes('&', jsonPurchase.getData("inventoryName").toString()));

        ItemStack itemBought = Items.createShopItem(itemPurchased);

        for (Map.Entry<String, Object> posItemEntry : purchaseInvData.entrySet()) {

            ItemStack item = null;

            if(posItemEntry.getValue().toString().equalsIgnoreCase("boughtItem")){
                item = itemBought;
            }else {
                try {
                    item = Items.createShopItem(shopPath, posItemEntry.getKey(), page);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

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

    public static void backBtn(Inventory toInv, Player player, ShopClickEvents shopInfo, String shopPath){

        shopInfo.setCurrentShopPath(shopInfo.getShopNameDir()+shopPath);
        shopInfo.setInv(toInv);
        shopInfo.setInChangingInventory(true);
        player.openInventory(toInv);
        shopInfo.setInChangingInventory(false);
        shopInfo.setPage(1);

    }


}
