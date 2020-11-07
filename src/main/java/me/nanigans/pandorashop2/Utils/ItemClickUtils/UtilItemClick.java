package me.nanigans.pandorashop2.Utils.ItemClickUtils;

import me.nanigans.pandorashop2.Events.ShopClickEvents;
import me.nanigans.pandorashop2.Utils.Items.InventoryUtils;
import org.bukkit.inventory.Inventory;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class UtilItemClick {

    private static final List<String> utilList = Arrays.asList("goTo", "purchase", "sell", "pageForward", "pageBackwards");
    private ShopClickEvents shopInfo;

   public UtilItemClick(Map<String, Object> jsonItem, ShopClickEvents shopInfo) {

       Map<String, Object> shopData = (Map<String, Object>) jsonItem.get("shopData");
       this.shopInfo = shopInfo;

       for (Map.Entry<String, Object> method : shopData.entrySet()) {

           try {
               this.getClass().getMethod(method.getKey(), Map.class).invoke(this, shopData);
           }catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored){
           }

       }

   }


    /**
     * Sends the player a page forward in the shop if that page exists
     * @param backwardMap The shop data map for the item clicked
     * @throws IOException IOException
     * @throws ParseException ParseException
     */
    public void pageBackwards(Map<String, Object> backwardMap) throws IOException, ParseException {

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
