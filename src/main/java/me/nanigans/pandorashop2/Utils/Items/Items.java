package me.nanigans.pandorashop2.Utils.Items;

import com.earth2me.essentials.Enchantments;
import me.nanigans.pandorashop2.Utils.Config.ConfigCreators;
import me.nanigans.pandorashop2.Utils.Config.JsonUtils;
import me.nanigans.pandorashop2.Utils.Glow;
import me.nanigans.pandorashop2.Utils.PathUtils.ShopPath;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Items {

    public static Map<String, Object> getJsonItem(int itemLoc, int page, String path) throws IOException, ParseException {

        JsonUtils json = new JsonUtils(path);
        return ShopPath.getConfigSectionValue(json.getData("page"+page+".items."+itemLoc), true);

    }


    public static ItemStack updatePriceLore(ItemStack item, int itemAmt){

        List<Double> unitPrice = getUnitPrice(item);
        ItemMeta meta = Items.stripPriceLore(item.clone()).getItemMeta();

        assert unitPrice != null;
        if(unitPrice.get(0) != null){

            double price = unitPrice.get(0);
            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            lore.add(ChatColor.GRAY+"Buy Price: $" +ChatColor.RED+ (price*itemAmt));
            meta.setLore(lore);
        }

        if(unitPrice.get(1) != null){
            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            lore.add(ChatColor.GRAY+"Sell Price: $" +ChatColor.GREEN+ (unitPrice.get(1)*itemAmt));
            meta.setLore(lore);
        }

        item.setItemMeta(meta);

        return item;

    }



    public static List<Double> getUnitPrice(ItemStack item){

        ItemMeta meta = item.getItemMeta();
        if(meta != null && meta.getLore() != null){

            List<String> lore = meta.getLore();
            if(lore.stream().anyMatch(i -> i.contains("Buy Price: $") || i.contains("Sell Price: $"))){

                List<String> collect = Arrays.asList(lore.stream().filter(i -> i.contains("Buy Price: $")).findAny().orElse(null), lore.stream().filter(i -> i.contains("Sell Price: $")).findAny().orElse(null));

                for (int i = 0; i < collect.size(); i++) {

                    if (collect.get(i) != null) {

                        final Matcher doubles = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?").matcher(ChatColor.stripColor(collect.get(i)));
                        if (doubles.find())
                            collect.set(i, doubles.group(0));
                    }

                }

                List<Double> priceList = Arrays.asList(null, null);

                int p = 0;
                for (String s : collect) {
                    if (s != null) {
                        double i = Double.parseDouble(s);

                        priceList.set(p, i);
                    }
                    p++;

                }

                return priceList;

            }

        }

        return null;
    }

    /**
     * Strips an itemstack of:
     * Buy Price:
     * Sell Price:
     * lore
     * @param item the item to strip down
     * @return a new itemstack without the price lore
     */
    public static ItemStack stripPriceLore(ItemStack item){

        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();

        if(lore != null){

            lore = lore.stream().filter(i -> !i.contains("Buy Price:") && !i.contains("Sell Price:")).collect(Collectors.toList());
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Creates a shop item based on the json data directly
     * @param itemData the item data of the itemstack
     * @return the itemstack created
     */
    public static ItemStack createShopItem(Map<String, Object> itemData){

        if(!itemData.containsKey("material") || itemData.get("material") == null) return null;
        ItemStack item = new ItemStack(Material.valueOf(itemData.get("material").toString()));

        item.setAmount(Integer.parseInt(itemData.get("amount").toString()));

        ItemMeta meta = item.getItemMeta();

        if(itemData.containsKey("displayName") && itemData.get("displayName") != null)
            meta.setDisplayName(itemData.get("displayName").toString());

        JSONArray arr = ((JSONArray) itemData.get("lore"));
        if(arr.size() != 0) {
            List<String> lore = ((List<String>) arr);
            meta.setLore(lore);
        }

        Map<String, Object> shopData = ((Map<String, Object>) itemData.get("shopData"));
        if(shopData.containsKey("buyPrice") && shopData.get("buyPrice") != null){
            int amount = Integer.parseInt(itemData.get("amount").toString());
            int price = Integer.parseInt(shopData.get("buyPrice").toString());

            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            lore.add(ChatColor.GRAY+"Buy Price: $" +ChatColor.RED+ (amount*price));
            meta.setLore(lore);
        }

        if(shopData.containsKey("sellPrice") && shopData.get("sellPrice") != null){
            int amount = Integer.parseInt(itemData.get("amount").toString());
            int price = Integer.parseInt(shopData.get("sellPrice").toString());

            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            lore.add(ChatColor.GRAY+"Sell Price: $" +ChatColor.GREEN+ (amount*price));
            meta.setLore(lore);
        }

        if(itemData.containsKey("glow") && Boolean.parseBoolean(itemData.get("glow").toString())){
            meta.addEnchant(new Glow(70), 1, true);
        }

        JSONObject enchants = (JSONObject) itemData.get("enchantments");

        if(enchants != null && enchants.size() > 0){

            for (Map.Entry<String, Object> o : ((Map<String, Object>)enchants).entrySet()) {
                meta.addEnchant(Enchantments.getByName(o.getKey()), Integer.parseInt(o.getValue().toString()), true);
            }

        }

        item.setItemMeta(meta);

        Map<String, Object> nbts = (Map<String, Object>) itemData.get("NBTData");

        if(nbts != null && nbts.size() > 0){

            for (Map.Entry<String, Object> o : nbts.entrySet()) {
                item = setNBT(item, o.getKey(), o.getValue().toString());
            }

        }

        return item;


    }

    /**
     * This creates a new itemstack from the json file that is specified. It will create the:
     * material, display name, buy price, sell price, lore, glow, enchantments, and nbt data
     * @param shopDir the directory for the json file
     * @param itemKey the item in the items sections of the json file to create
     * @param shopPage the page number of the shop
     * @return a new itemstack based on what is in the shop json
     * @throws IOException fails to read a json file
     * @throws ParseException fails to parse a json file
     */
    public static ItemStack createShopItem(String shopDir, String itemKey, int shopPage) throws IOException, ParseException {

        File shop = ConfigCreators.createFile(shopDir);

        JsonUtils json = new JsonUtils(shop.getPath());

        String itemPath = "page"+shopPage+".items."+itemKey;

        if(json.getData(itemPath+".material") == null) return null;
        ItemStack item = new ItemStack(Material.valueOf(json.getData(itemPath+".material").toString()));

        item.setAmount(Integer.parseInt(json.getData(itemPath+".amount").toString()));

        ItemMeta meta = item.getItemMeta();

        if(json.getData(itemPath+".displayName") != null)
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', json.getData(itemPath+".displayName").toString()));

        JSONArray arr = ((JSONArray) json.getData(itemPath + ".lore"));
        if(arr.size() != 0) {
            List<String> lore = ((List<String>) arr);
            meta.setLore(lore);
        }

        if(json.getData(itemPath+".shopData.buyPrice") != null){

            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            int amount = Integer.parseInt(json.getData(itemPath+".amount").toString());

            int price = Integer.parseInt(json.getData(itemPath+".shopData.buyPrice").toString());
            lore.add(ChatColor.GRAY+"Buy Price: $" +ChatColor.RED+ (amount*price));
            meta.setLore(lore);
        }

        if(json.getData(itemPath+".shopData.sellPrice") != null){
            int amount = Integer.parseInt(json.getData(itemPath+".amount").toString());
            int price = Integer.parseInt(json.getData(itemPath+".shopData.sellPrice").toString());

            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            lore.add(ChatColor.GRAY+"Sell Price: $" +ChatColor.GREEN+ (amount*price));
            meta.setLore(lore);
        }

        if(Boolean.parseBoolean(json.getData(itemPath+".glow").toString())){
            meta.addEnchant(new Glow(70), 1, true);
        }

        JSONObject enchants = (JSONObject) json.getData(itemPath+".enchantments");

        if(enchants != null && enchants.size() > 0){

            for (Map.Entry<String, Object> o : ((Map<String, Object>)enchants).entrySet()) {
                meta.addEnchant(Enchantments.getByName(o.getKey()), Integer.parseInt(o.getValue().toString()), true);
            }

        }

        item.setItemMeta(meta);

        JSONObject nbts = (JSONObject) json.getData(itemPath+".NBTData");

        if(nbts != null && nbts.size() > 0){

            for (Map.Entry<String, String> o : ((Map<String, String>)nbts).entrySet()) {
                item = setNBT(item, o.getKey(), o.getValue());
            }

        }

        return item;

    }

    public static ItemStack setNBT(ItemStack item, String key, String value){

        net.minecraft.server.v1_8_R3.ItemStack stack = CraftItemStack.asNMSCopy(item);

            NBTTagCompound tag = stack.getTag();

            if (tag != null) {

                tag.setString(key, value);
                stack.setTag(tag);

            }

            item = CraftItemStack.asCraftMirror(stack);
            return item;

    }

    public static boolean containsNBT(ItemStack item, String key){

        try {
            net.minecraft.server.v1_8_R3.ItemStack stack = CraftItemStack.asNMSCopy(item);

            if (stack.hasTag()) {
                NBTTagCompound tag = stack.getTag();

                if (tag != null) {

                    return tag.hasKey(key);

                } else return false;
            } else return false;

        }catch(Exception ignored){
            return false;
        }

    }

    public static String getData(ItemStack item, String key){

        if(containsNBT(item, key)){

            net.minecraft.server.v1_8_R3.ItemStack stack = CraftItemStack.asNMSCopy(item);
            NBTTagCompound tag = stack.getTag();

            return tag.get(key).toString().replaceAll("\"", "");

        }

        return null;
    }

    public static Map<String, String> getAllNBT(ItemStack item){

        net.minecraft.server.v1_8_R3.ItemStack stack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = stack.getTag();

        if(tag != null){

            final Set<String> nbtKeys = tag.c();

            Map<String, String> nbtMap = new HashMap<>();

            for (String nbtKey : nbtKeys) {

                String data = getData(item, nbtKey);

                if(data != null)
                    nbtMap.put(nbtKey, data);

            }

            return nbtMap;

        }
        return null;

    }

}
