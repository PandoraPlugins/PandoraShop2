package me.nanigans.pandorashop2.Events;

import me.nanigans.pandorashop2.PandoraShop2;
import me.nanigans.pandorashop2.Utils.ItemClickUtils.UtilItemClick;
import me.nanigans.pandorashop2.Utils.Items.Items;
import me.nanigans.pandorashop2.Utils.PathUtils.ShopPath;
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
    public void inventoryClick(InventoryClickEvent event) throws IOException, ParseException {

        if(event.getClickedInventory().equals(this.inv) && event.getWhoClicked().getUniqueId().equals(this.player.getUniqueId())){

            ItemStack clicked = event.getCurrentItem();

            if(clicked != null){

                Map<String, Object> item = Items.getJsonItem(event.getSlot(), this.page, this.currentShopPath);
                new UtilItemClick(item, this);

            }

            event.setCancelled(true);

        }

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
