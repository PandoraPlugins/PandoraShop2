package me.nanigans.pandorashop2.Utils.ItemClickUtils;

import com.earth2me.essentials.Enchantments;
import me.nanigans.pandorashop2.Events.ShopClickEvents;
import me.nanigans.pandorashop2.PandoraShop2;
import me.nanigans.pandorashop2.Utils.Config.ConfigCreators;
import me.nanigans.pandorashop2.Utils.Items.InventoryUtils;
import me.nanigans.pandorashop2.Utils.Items.Items;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class UtilItemClick {

    private static final List<String> utilList = Arrays.asList("goTo", "purchaseButton", "sellButton", "pageForward", "pageBackwards",
            "increasePurchaseItem", "decreasePurchaseItem", "extraItems", "addEnchantment");
    private final ShopClickEvents shopInfo;

    public UtilItemClick(ShopClickEvents shopInfo){
        this.shopInfo = shopInfo;
    }

    /**
     * This will find the method that an item clicked has according to its json shopData
     * @param jsonItem the item clicked
     * @param shopInfo shop info for th eplayer
     */
   public UtilItemClick(Map<String, Object> jsonItem, ShopClickEvents shopInfo) {

       this.shopInfo = shopInfo;

       for (Map.Entry<String, Object> method : ((Map<String, Object>)jsonItem.get("shopData")).entrySet()) {

           try {
               this.getClass().getMethod(method.getKey(), Map.class).invoke(this, jsonItem);
           }catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored){
//               if(!(ignored.getCause() instanceof NoSuchMethodException) && !(ignored.getCause() instanceof InvocationTargetException))
//               ignored.printStackTrace();
           }

       }

   }


    /**
     * Enchants the item being help when a player clicks on the specified enchantment item
     * @param player the player to add the enchantment to
     * @param enchantment the enchantment to add
     * @param power the power of the enchantment
     * @param bought the item that was bought
     * @throws IOException error
     * @throws ParseException error
     */
   public void enchantItem(Player player, Enchantment enchantment, int power, ItemStack bought) throws IOException, ParseException {

       ItemStack item = player.getInventory().getItemInHand();

       if(item != null){
           if(ShopActionUtils.buy(this.shopInfo, bought)) {
               ItemMeta meta = item.getItemMeta();
               meta.addEnchant(enchantment, power+1, false);
               item.setItemMeta(meta);
               player.getInventory().setItemInHand(item);
               this.shopInfo.setInChangingInventory(true);
               player.openInventory(InventoryUtils.createInventoryShop(this.shopInfo.getShopNameDir()+"/Categories.json", 1, player));
               this.shopInfo.setInChangingInventory(false);

           }
       }else{
           player.sendMessage(ChatColor.RED+"Please hold an item to enchant");
           player.closeInventory();
       }

   }

    /**
     * Creates an enchantment inventory to enchant the held item
     * @param itemClicked the item clicked to get here
     * @return a new inventory
     */
   public Inventory addEnchantment(Map<String, Object> itemClicked){

       Map<String, Object> shopData = ((Map<String, Object>)itemClicked.get("shopData"));
       if(shopData.containsKey("addEnchantment") && shopData.get("addEnchantment") != null) {
           final Map<String, Object> addEnchantment = (Map<String, Object>) shopData.get("addEnchantment");

           if (addEnchantment != null && addEnchantment.size() > 0) {
               if(addEnchantment.entrySet().iterator().hasNext()) {
                   final Map.Entry<String, Object> next = addEnchantment.entrySet().iterator().next();

                   final String s = next.getValue().toString();
                   Enchantment enchantment = Enchantments.getByName(s);
                   if(enchantment != null){
                           Inventory inv = InventoryUtils.createEnchantmentItemInventory(this.shopInfo.getPlayer(),
                                   this.shopInfo.getClicked(), enchantment);

                           if (inv != null) {
                               this.shopInfo.setInChangingInventory(true);
                               this.shopInfo.getPlayer().openInventory(inv);
                               this.shopInfo.setInChangingInventory(false);
                               this.shopInfo.setInv(inv);
                               this.shopInfo.setCurrentShopPath(this.shopInfo.getShopNameDir() + "/addEnchantment.json");
                               this.shopInfo.setPage(1);
                           }

                   }
               }

           }
       }

       return null;
   }

    /**
     * Generates extra items inventory to purchase or sell extra items
     * @param itemClicked the item data for the item clicked
     * @throws IOException error
     * @throws ParseException error
     */
   public void extraItems(Map<String, Object> itemClicked) throws IOException, ParseException {

       if(((Map<String, Object>)itemClicked.get("shopData")).containsKey("extraItems") &&
               ((Map<String, Object>)itemClicked.get("shopData")).get("extraItems") != null){

           ItemStack beingSold = InventoryUtils.getBuyingItem(this.shopInfo.getShopNameDir(), this.shopInfo.getInv(), this.shopInfo.getPage());
           Inventory inv = InventoryUtils.createExtraItemsInventory(this.shopInfo.getPlayer(), itemClicked, beingSold);

           if(inv != null) {
               this.shopInfo.setInChangingInventory(true);
               this.shopInfo.getPlayer().openInventory(inv);
               this.shopInfo.setInChangingInventory(false);
               this.shopInfo.setCurrentShopPath(this.shopInfo.getCurrentShopPath()+"extraItems.json");
               this.shopInfo.setInv(inv);
               this.shopInfo.setPage(1);
           }

       }


   }

    /**
     * Decreases the item in question by the item clicked amount
     * @param itemClicked the item the signifies to decrease the item
     * @throws IOException
     * @throws ParseException
     */
    public void decreasePurchaseItem(Map<String, Object> itemClicked) throws IOException, ParseException {

        if(((Map<String, Object>)itemClicked.get("shopData")).containsKey("decreasePurchaseItem") &&
                Boolean.parseBoolean(((Map<String, Object>)itemClicked.get("shopData")).get("decreasePurchaseItem").toString())) {

            ItemStack item = InventoryUtils.getBuyingItem(this.shopInfo.getShopNameDir(), this.shopInfo.getInv(), this.shopInfo.getPage());
            if (item != null) {
                int incAmt = Integer.parseInt(itemClicked.get("amount").toString());
                final int amount = item.getAmount() - incAmt;

                item.setAmount(Math.max(1, amount));
                Items.updatePriceLore(item, item.getAmount());

            }
        }
    }

    /**
     * Increases the item in question by the item clicked amount
     * @param itemClicked the item the signifies to increase the item
     * @throws IOException
     * @throws ParseException
     */

   public void increasePurchaseItem(Map<String, Object> itemClicked) throws IOException, ParseException {

       if(((Map<String, Object>)itemClicked.get("shopData")).containsKey("increasePurchaseItem") &&
               Boolean.parseBoolean(((Map<String, Object>)itemClicked.get("shopData")).get("increasePurchaseItem").toString())) {

           try {
               ItemStack item = InventoryUtils.getBuyingItem(this.shopInfo.getShopNameDir(), this.shopInfo.getInv(), this.shopInfo.getPage());

               if (item != null) {
                   int incAmt = Integer.parseInt(itemClicked.get("amount").toString());
                   final int amount = item.getAmount() + incAmt;

                   item.setAmount(Math.min(amount, 64));
                   Items.updatePriceLore(item, item.getAmount());

               }
           }catch(Exception e){
               e.printStackTrace();
           }
       }
   }

    /**
     * This will make the player sell the item being bought
     * @param soldItem the item clicked in the inventory
     * @throws IOException IOException
     * @throws ParseException ParseException
     */
    public void sellButton(Map<String, Object> soldItem) throws IOException, ParseException {

        if(((Map<String, Object>)soldItem.get("shopData")).containsKey("sellButton") &&
                Boolean.parseBoolean(((Map<String, Object>)soldItem.get("shopData")).get("sellButton").toString())) {

            Inventory inv = this.shopInfo.getInv();

            String purchInv = this.shopInfo.getShopNameDir();
            ItemStack sellItem = InventoryUtils.getBuyingItem(
                    purchInv, inv, this.shopInfo.getPage());

            if (sellItem != null) {
                ShopActionUtils.sell(this.shopInfo, sellItem);
                Inventory backInv = InventoryUtils.createInventoryShop(this.shopInfo.getShopNameDir() + "/Categories.json", 1, this.shopInfo.getPlayer());
                if (backInv != null) {
                    this.shopInfo.setInChangingInventory(true);
                    this.shopInfo.getPlayer().openInventory(backInv);
                    this.shopInfo.setInChangingInventory(false);
                    this.shopInfo.setInv(backInv);
                    this.shopInfo.setPage(1);
                    this.shopInfo.setCurrentShopPath(this.shopInfo.getShopNameDir()+"/Categories");
                }
                else this.shopInfo.getPlayer().closeInventory();
            }
        }

    }

    /**
     * This will make the player buy the item being bought
     * @param purchasedItem the item clicked in the inventory
     * @throws IOException IOException
     * @throws ParseException ParseException
     */
   public void purchaseButton(Map<String, Object> purchasedItem) throws IOException, ParseException {

       if(((Map<String, Object>)purchasedItem.get("shopData")).containsKey("purchaseButton") &&
               Boolean.parseBoolean(((Map<String, Object>)purchasedItem.get("shopData")).get("purchaseButton").toString())) {

           Inventory inv = this.shopInfo.getInv();

           String purchInv = this.shopInfo.getShopNameDir();

           ItemStack buyingItem = InventoryUtils.getBuyingItem(
                   purchInv, inv, this.shopInfo.getPage());

           if (buyingItem != null) {
               ShopActionUtils.buy(this.shopInfo, buyingItem);
               Inventory backInv = InventoryUtils.createInventoryShop(this.shopInfo.getShopNameDir() + "/Categories.json", 1, this.shopInfo.getPlayer());
               if (backInv != null) {
                   this.shopInfo.setInChangingInventory(true);
                   this.shopInfo.getPlayer().openInventory(backInv);
                   this.shopInfo.setInChangingInventory(false);

                   this.shopInfo.setInv(backInv);
                   this.shopInfo.setPage(1);
                   this.shopInfo.setCurrentShopPath(this.shopInfo.getShopNameDir()+"/Categories");
               }
               else this.shopInfo.getPlayer().closeInventory();
           }
       }
   }

    /**
     * Directly buys or sells an item
     * @param buyingItem the item being bought
     * @param buyOrSell true for buy, false for sell
     * @throws IOException error
     * @throws ParseException error
     */
   public void purchaseStack(ItemStack buyingItem, boolean buyOrSell) throws IOException, ParseException {

       if (buyingItem != null) {
           if(buyOrSell)
           ShopActionUtils.buy(this.shopInfo, buyingItem);
           else ShopActionUtils.sell(this.shopInfo, buyingItem);
           Inventory backInv = InventoryUtils.createInventoryShop(this.shopInfo.getShopNameDir() + "/Categories.json", 1, this.shopInfo.getPlayer());
           if (backInv != null) {
               this.shopInfo.setInChangingInventory(true);
               this.shopInfo.getPlayer().openInventory(backInv);
               this.shopInfo.setInChangingInventory(false);

               this.shopInfo.setInv(backInv);
               this.shopInfo.setPage(1);
               this.shopInfo.setCurrentShopPath(this.shopInfo.getShopNameDir()+"/Categories");
           }
           else this.shopInfo.getPlayer().closeInventory();
       }

   }

    /**
     * Directs to purchaseItem
     * @param buyPriceMap json item clicked
     */
   public void buyPrice(Map<String, Object> buyPriceMap){

       if(((Map<String, Object>)buyPriceMap.get("shopData")).containsKey("buyPrice") && ((Map<String, Object>)buyPriceMap.get("shopData")).get("buyPrice") != null)
           purchaseItem(buyPriceMap);
   }

    /**
     * Directs to purchaseItem
     * @param sellPriceMap json item clicked
     */
   public void sellPrice(Map<String, Object> sellPriceMap){
       if(((Map<String, Object>)sellPriceMap.get("shopData")).containsKey("sellPrice") && ((Map<String, Object>)sellPriceMap.get("shopData")).get("sellPrice") != null)
       purchaseItem(sellPriceMap);
   }

    /**
     * Opens a new purchasing inventory for the player
     * @param boughtItem the item being bought
     */
   public void purchaseItem(Map<String, Object> boughtItem){

       try {
           File purchase = ConfigCreators.createFile(this.shopInfo.getShopNameDir() + "/PurchaseInventory.json");
           purchase = ConfigCreators.createDefaultJsonData(purchase, PandoraShop2.getPlugin(PandoraShop2.class));

           Inventory inv = InventoryUtils.createPurchaseInventory(this.shopInfo.getShopNameDir()+"/PurchaseInventory.json",
                   this.shopInfo.getCurrentShopPath(), this.shopInfo.getPage(), boughtItem, this.shopInfo.getPlayer());
           if(inv != null) {
               this.shopInfo.setItemInPurchase(boughtItem);
               this.shopInfo.setInChangingInventory(true);
               this.shopInfo.getPlayer().openInventory(inv);
               this.shopInfo.setInChangingInventory(false);

               this.shopInfo.setInv(inv);
               this.shopInfo.setPage(1);
               this.shopInfo.setCurrentShopPath(purchase.getPath());
           }

       }catch(IOException | ParseException e){
           e.printStackTrace();
       }

   }

    /**
     * Sends the player a page forward in the shop if that page exists
     * @param backwardMap The shop data map for the item clicked
     * @throws IOException IOException
     * @throws ParseException ParseException
     */
    public void pageBackwards(Map<String, Object> backwardMap) throws IOException, ParseException {

        backwardMap = (Map<String, Object>) backwardMap.get("shopData");
        if(backwardMap.containsKey("pageBackwards") && backwardMap.get("pageBackwards") != null &&
                backwardMap.get("pageBackwards").toString().equals("true")){

            Inventory inv = InventoryUtils.createInventoryShop(this.shopInfo.getCurrentShopPath(), this.shopInfo.getPage()-1, this.shopInfo.getPlayer());
            if(inv != null){
                this.shopInfo.setPage(this.shopInfo.getPage()-1);
                this.shopInfo.setInChangingInventory(true);
                this.shopInfo.getPlayer().openInventory(inv);
                this.shopInfo.setInChangingInventory(false);

                this.shopInfo.setInv(inv);

            }

        }

    }

    /**
     * Sends the player a page forward in the shop if that page exists
     * @param forwardMap The shop data map for the item clicked
     * @throws IOException IOException
     * @throws ParseException ParseException
     */
   public void pageForward(Map<String, Object> forwardMap) throws IOException, ParseException {
        forwardMap = (Map<String, Object>) forwardMap.get("shopData");
       if(forwardMap.containsKey("pageForward") && forwardMap.get("pageForward") != null &&
               forwardMap.get("pageForward").toString().equals("true")){

           Inventory inv = InventoryUtils.createInventoryShop(this.shopInfo.getCurrentShopPath(), this.shopInfo.getPage()+1, this.shopInfo.getPlayer());
           if(inv != null){
               this.shopInfo.setPage(this.shopInfo.getPage()+1);
               this.shopInfo.setInChangingInventory(true);
               this.shopInfo.getPlayer().openInventory(inv);
               this.shopInfo.setInChangingInventory(false);

               this.shopInfo.setInv(inv);
           }

       }

   }

    /**
     * Opens a new inventory for the player based on the item clicked
     * @param goToMap The shop data map for the item clicked
     * @throws IOException yeah
     * @throws ParseException yeah
     */
   public void goTo(Map<String, Object> goToMap) throws IOException, ParseException {

       goToMap = (Map<String, Object>) goToMap.get("shopData");;
       if(goToMap.containsKey("goTo") && goToMap.get("goTo") != null){

           String dir = this.shopInfo.getShopNameDir()+"/"+goToMap.get("goTo");

           Inventory inv = InventoryUtils.createInventoryShop(dir, this.shopInfo.getPage(), this.shopInfo.getPlayer());
           if(inv != null) {
               this.shopInfo.setInChangingInventory(true);
               this.shopInfo.getPlayer().openInventory(inv);
               this.shopInfo.setInChangingInventory(false);

               this.shopInfo.setInv(inv);
               this.shopInfo.setCurrentShopPath(dir);
           }

       }

   }

}
