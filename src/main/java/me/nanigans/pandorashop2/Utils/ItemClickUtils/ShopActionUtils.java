package me.nanigans.pandorashop2.Utils.ItemClickUtils;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import me.nanigans.pandorashop2.Events.ShopClickEvents;
import me.nanigans.pandorashop2.Utils.Items.InventoryUtils;
import me.nanigans.pandorashop2.Utils.Items.Items;
import net.ess3.api.MaxMoneyException;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.Map;

public class ShopActionUtils {

    public static void sell(ShopClickEvents shopInfo, ItemStack sold){

        int amountSold = sold.getAmount();
        Map<String, Object> jsonItem = shopInfo.getItemInPurchase();
        int price = Integer.parseInt(((Map<String, Object>) jsonItem.get("shopData")).get("sellPrice").toString());

        final int totalPrice = amountSold*price;
        User user = Essentials.getPlugin(Essentials.class).getUser(shopInfo.getPlayer());

        if(InventoryUtils.getAmountInInv(Items.stripPriceLore(sold), shopInfo.getPlayer().getInventory()) >= sold.getAmount()){

            try {
                shopInfo.getPlayer().getInventory().removeItem(Items.stripPriceLore(sold));
                user.giveMoney(BigDecimal.valueOf(totalPrice));
                user.sendMessage(ChatColor.GREEN+"Successfully sold " + sold.getAmount() + " " + sold.getItemMeta().getDisplayName()+"!");
            }catch(MaxMoneyException e){
                shopInfo.getPlayer().sendMessage(ChatColor.RED+"Cannot sell this item due to your balance being full");
            }

        }else{
            shopInfo.getPlayer().sendMessage(ChatColor.RED+"You do not have enough items in your inventory to sell this item");
        }

    }


    /**
     * Buys the item in question
     * @param shopInfo shopinfo for the current player
     * @param bought what item was bought
     */
    public static void buy(ShopClickEvents shopInfo, ItemStack bought){

        int amountPurchased = bought.getAmount();
        Map<String, Object> jsonItem = shopInfo.getItemInPurchase();
        int price = Integer.parseInt(((Map<String, Object>) jsonItem.get("shopData")).get("buyPrice").toString());

        final int totalPrice = amountPurchased*price;

        User user = Essentials.getPlugin(Essentials.class).getUser(shopInfo.getPlayer());
        final BigDecimal bigDecimal = BigDecimal.valueOf(totalPrice);
        if(user.canAfford(bigDecimal)){

            if(InventoryUtils.copyInventory(shopInfo.getPlayer().getInventory()).addItem(Items.stripPriceLore(bought)).isEmpty()) {

                user.sendMessage(ChatColor.GREEN + "Purchased: " + bought.getItemMeta().getDisplayName() + "!");
                shopInfo.getPlayer().getInventory().addItem(Items.stripPriceLore(bought));
                user.takeMoney(bigDecimal);
            }else{
                shopInfo.getPlayer().closeInventory();
                shopInfo.getPlayer().sendMessage(ChatColor.RED+"Your inventory is too full to add this item!");
            }

        }else{
            shopInfo.getPlayer().closeInventory();
            user.sendMessage(ChatColor.RED+"You cannot afford this item.");
        }

    }

}
