package me.nanigans.pandorashop2.Utils.ItemClickUtils;

import me.nanigans.pandorashop2.Events.ShopClickEvents;
import me.nanigans.pandorashop2.Utils.Items.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class UtilItemClick {

    private static final List<String> utilList = Arrays.asList("goTo", "purchase", "sell");
    private ShopClickEvents shopInfo;

   public UtilItemClick(Map<String, Object> jsonItem, ShopClickEvents shopInfo) {

       Map<String, Object> shopData = (Map<String, Object>) jsonItem.get("shopData");
       this.shopInfo = shopInfo;

       for (Map.Entry<String, Object> method : shopData.entrySet()) {

           try {
               this.getClass().getMethod(method.getKey(), Map.class).invoke(method.getKey(), shopData);
           }catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored){
           }

       }

   }

   public void goTo(Map<String, Object> goToMap) throws IOException, ParseException {

       if(goToMap.containsKey("goTo") && goToMap.get("goTo") != null){

           String dir = this.shopInfo.getShopNameDir()+goToMap.get("goTo");

           this.shopInfo.getPlayer().closeInventory();

           Inventory inv = InventoryUtils.createInventoryShop(dir, this.shopInfo.getPage(), this.shopInfo.getPlayer());
           if(inv != null) {
               this.shopInfo.getPlayer().openInventory(inv);
               this.shopInfo.setInv(inv);
               this.shopInfo.setCurrentShopPath(dir);
           }else{
               this.shopInfo.getPlayer().sendMessage(ChatColor.RED+"This shop does not yet exist. Please create it first");

           }


       }

   }

}
