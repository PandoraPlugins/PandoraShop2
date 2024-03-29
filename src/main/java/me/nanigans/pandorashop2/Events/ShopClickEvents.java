package me.nanigans.pandorashop2.Events;

import com.earth2me.essentials.Enchantments;
import me.nanigans.pandorashop2.PandoraShop2;
import me.nanigans.pandorashop2.Utils.ItemClickUtils.UtilItemClick;
import me.nanigans.pandorashop2.Utils.Items.InventoryUtils;
import me.nanigans.pandorashop2.Utils.Items.Items;
import me.nanigans.pandorashop2.Utils.PathUtils.ShopPath;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShopClickEvents implements Listener {
    private final Player player;
    private Inventory inv;
    private String currentShopPath;
    private String shopNameDir;
    private int page;
    private Map<String, Object> itemInPurchase;
    private final PandoraShop2 plugin = PandoraShop2.getPlugin(PandoraShop2.class);
    private ClickType clickType;
    private ItemStack clicked;
    private boolean inChangingInventory = false;
    private UtilItemClick itemClick;

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
        this.itemClick = new UtilItemClick(this);
    }

    /**
     * When an item is clicked, it will perform the necessary actions according to its json data
     * @param event click event
     * @throws IOException error
     * @throws ParseException error
     */
    @EventHandler
    public void inventoryClick(InventoryClickEvent event) throws IOException, ParseException {

        if(event.getClickedInventory() != null) {

            if(event.getClick().isShiftClick() && event.getClickedInventory().equals(this.player.getInventory())){
                event.setCancelled(true);
                return;
            }
            if (event.getClickedInventory().equals(this.inv) && event.getWhoClicked().getUniqueId().equals(this.player.getUniqueId())) {

                ItemStack clicked = event.getCurrentItem();
                this.clicked = clicked;

                player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 2f, 1f);
                this.clickType = event.getClick();
                if (clicked != null && clicked.getType() != Material.AIR) {

                    event.setCancelled(true);

                    Map<String, Object> backBtn = Items.getJsonItem(event.getSlot(), this.page, this.currentShopPath);
                    if((clicked.getItemMeta().getDisplayName().equals(ChatColor.RED+"Back") && clicked.getType() == Material.BARRIER) ||
                            (backBtn != null && backBtn.get("isBackButton") != null && Boolean.parseBoolean(backBtn.get("isBackButton").toString()))){
                        Inventory inv = InventoryUtils.createInventoryShop(this.getShopNameDir()+"/Categories.json", 1, this.player);
                        InventoryUtils.backBtn(inv, player, this, "/Categories.json");
                        return;
                    }

                    if (!this.currentShopPath.endsWith("extraItems.json") && !this.currentShopPath.endsWith("addEnchantment.json")) {
                        Map<String, Object> item = Items.getJsonItem(event.getSlot(), this.page, this.currentShopPath);
                        if (item != null)
                            this.itemClick.execute(item);

                    }else if(this.currentShopPath.endsWith("extraItems.json")){

                        ItemStack clickedCopy = clicked.clone();
                        clickedCopy.setAmount(clicked.getAmount()*64);
                        this.itemClick.purchaseStack(clickedCopy, this.clickType.isLeftClick());

                    }else if(this.currentShopPath.endsWith("addEnchantment.json")){

                        ItemStack item = this.clicked;
                        final List<String> lore = item.getItemMeta().getLore();
                        if(lore != null && lore.size() > 0){
                            final List<String> collect = lore.stream().filter(i -> i.contains("Set " + ChatColor.DARK_AQUA)).collect(Collectors.toList());
                            final String[] s = collect.get(0).split(ChatColor.DARK_AQUA.toString())[1].split(" ");

                            Enchantment enchantment = Enchantments.getByName(s[0]);
                            int level = Integer.parseInt(s[1]);

                            this.itemClick.enchantItem(this.player, enchantment, level, this.clicked);

                        }

                    }
                }


            }
        }

    }

    @EventHandler
    public void dragItem(InventoryDragEvent event){

        if(event.getInventory().equals(this.inv) && event.getWhoClicked().getUniqueId().equals(this.player.getUniqueId())){
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void inventoryClose(InventoryCloseEvent event) throws Throwable {

        if(event.getPlayer().getUniqueId().equals(this.player.getUniqueId())){
            if(!this.inChangingInventory) {
                this.finalize();
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


    public ClickType getClickType() {
        return clickType;
    }

    public ItemStack getClicked() {
        return clicked;
    }

    public void setInChangingInventory(boolean inChangingInventory) {
        this.inChangingInventory = inChangingInventory;
    }
}
