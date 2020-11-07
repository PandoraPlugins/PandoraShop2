package me.nanigans.pandorashop2.Utils.ItemClickUtils;

import me.nanigans.pandorashop2.Events.ShopClickEvents;
import me.nanigans.pandorashop2.PandoraShop2;
import me.nanigans.pandorashop2.Utils.Config.ConfigCreators;
import me.nanigans.pandorashop2.Utils.Items.InventoryUtils;
import org.bukkit.inventory.Inventory;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class UtilItemClick {

    private static final List<String> utilList = Arrays.asList("goTo", "purchase", "sell", "pageForward", "pageBackwards");
    private ShopClickEvents shopInfo;

   public UtilItemClick(Map<String, Object> jsonItem, ShopClickEvents shopInfo) {

       this.shopInfo = shopInfo;

       for (Map.Entry<String, Object> method : ((Map<String, Object>)jsonItem.get("shopData")).entrySet()) {

           try {
               this.getClass().getMethod(method.getKey(), Map.class).invoke(this, jsonItem);
           }catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored){
           }

       }

   }

   public void buyPrice(Map<String, Object> buyPriceMap){

       if(((Map<String, Object>)buyPriceMap.get("shopData")).containsKey("buyPrice") && ((Map<String, Object>)buyPriceMap.get("shopData")).get("buyPrice") != null)
           purchaseItem(buyPriceMap);
   }
   public void sellPrice(Map<String, Object> sellPriceMap){
       if(((Map<String, Object>)sellPriceMap.get("shopData")).containsKey("sellPrice") && ((Map<String, Object>)sellPriceMap.get("shopData")).get("sellPrice") != null)
       purchaseItem(sellPriceMap);
   }

   public void purchaseItem(Map<String, Object> boughtItem){

       try {
           File purchase = ConfigCreators.createFile(this.shopInfo.getShopNameDir() + "/PurchaseInventory.json");
           purchase = ConfigCreators.createDefaultJsonData(purchase, PandoraShop2.getPlugin(PandoraShop2.class));

           Inventory inv = InventoryUtils.createPurchaseInventory(this.shopInfo.getShopNameDir()+"/PurchaseInventory.json",
                   this.shopInfo.getCurrentShopPath(), this.shopInfo.getPage(), boughtItem, this.shopInfo.getPlayer());
           if(inv != null) {
               this.shopInfo.getPlayer().openInventory(inv);
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
                this.shopInfo.getPlayer().openInventory(inv);
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
               this.shopInfo.getPlayer().openInventory(inv);
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
               this.shopInfo.getPlayer().openInventory(inv);
               this.shopInfo.setInv(inv);
               this.shopInfo.setCurrentShopPath(dir);
           }

       }

   }

}
