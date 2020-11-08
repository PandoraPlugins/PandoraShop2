package me.nanigans.pandorashop2.Events;

import me.nanigans.pandorashop2.PandoraShop2;
import me.nanigans.pandorashop2.Utils.ItemClickUtils.UtilItemClick;
import me.nanigans.pandorashop2.Utils.Items.Items;
import me.nanigans.pandorashop2.Utils.PathUtils.ShopPath;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Map;

public class ShopClickEvents implements Listener {
    private final Player player;
    private Inventory inv;
    private String currentShopPath;
    private String shopNameDir;
    private int page;
    private Map<String, Object> itemInPurchase;
    private final PandoraShop2 plugin = PandoraShop2.getPlugin(PandoraShop2.class);

    /**
     * Initializes a customer of the shop
     * @param player the player viewing the shop
     * @param inv the current shop inventory
     * @param shopPath the path of the shop. /Shops/ShopName
     */
    public ShopClickEvents(Player player, Inventory inv, String shopPath){
        this.player = player;
        this.inv = inv;
        this.currentShopPath = ShopPath.shopPath(shopPath+"/Categories.json", plugin);
        this.shopNameDir = shopPath;
        this.page = 1;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) throws IOException, ParseException, NoSuchMethodException {

        if(event.getClickedInventory() != null) {
            if (event.getClickedInventory().equals(this.inv) && event.getWhoClicked().getUniqueId().equals(this.player.getUniqueId())) {

                ItemStack clicked = event.getCurrentItem();

                player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 2f, 1f);
                if (clicked != null) {

                    if (!this.shopNameDir.endsWith("_")) {
                        Map<String, Object> item = Items.getJsonItem(event.getSlot(), this.page, this.currentShopPath);
                        if (item != null)
                            new UtilItemClick(item, this);

                    }else{
                        Map<String, Object> item = Items.getJsonItem(event.getSlot(), this.page,
                                this.currentShopPath.substring(0, this.currentShopPath.length()-1));
                        item.replace("amount", 64*clicked.getAmount());
                        new UtilItemClick(item, this, UtilItemClick.class.getMethod("purchaseButton", Map.class));

                    }
                }

                event.setCancelled(true);

            }
        }

    }

    public void setItemInPurchase(Map<String, Object> item){
        this.itemInPurchase = item;
    }

    public Map<String, Object> getItemInPurchase(){
        return this.itemInPurchase;
    }

    public Player getPlayer(){
        return this.player;
    }

    public String getShopNameDir(){
        return this.shopNameDir;
    }

    public Inventory getInv() {
        return inv;
    }

    public void setInv(Inventory inv) {
        this.inv = inv;
    }

    public String getCurrentShopPath() {
        return currentShopPath;
    }

    public void setCurrentShopPath(String currentShopPath) {
        this.currentShopPath = currentShopPath;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }


}
