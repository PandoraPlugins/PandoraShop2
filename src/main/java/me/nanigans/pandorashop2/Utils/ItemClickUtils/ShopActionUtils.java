package me.nanigans.pandorashop2.Utils.ItemClickUtils;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import me.nanigans.pandorashop2.Events.ShopClickEvents;
import me.nanigans.pandorashop2.Utils.Items.InventoryUtils;
import me.nanigans.pandorashop2.Utils.Items.Items;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.Map;

public class ShopActionUtils {

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
