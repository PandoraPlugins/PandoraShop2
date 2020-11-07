package me.nanigans.pandorashop2.Utils.Items;

import com.earth2me.essentials.Enchantments;
import me.nanigans.pandorashop2.Utils.Config.ConfigCreators;
import me.nanigans.pandorashop2.Utils.Config.JsonUtils;
import me.nanigans.pandorashop2.Utils.Glow;
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

public class Items {

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
        meta.setDisplayName(json.getData(itemPath+".displayName").toString());

        JSONArray arr = ((JSONArray) json.getData(itemPath + ".lore"));
        if(arr.size() != 0) {
            List<String> lore = ((List<String>) arr);
            meta.setLore(lore);
        }

        if(json.getData(itemPath+".shopData.buyPrice") != null){

            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            lore.add(ChatColor.GRAY+"Buy Price: $" +ChatColor.RED+ json.getData(itemPath+".shopData.buyPrice").toString());
            meta.setLore(lore);
        }

        if(json.getData(itemPath+".shopData.sellPrice") != null){

            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            lore.add(ChatColor.GRAY+"Sell Price: $" +ChatColor.GREEN+ json.getData(itemPath+".shopData.sellPrice").toString());
            meta.setLore(lore);
        }

        if(Boolean.parseBoolean(json.getData(itemPath+".glow").toString())){
            item.addUnsafeEnchantment(new Glow(70), 1);
        }

        JSONObject enchants = (JSONObject) json.getData(itemPath+".enchantments");

        if(enchants != null && enchants.size() > 0){
            enchants.forEach((i, j) -> item.addUnsafeEnchantment(Enchantments.getByName(i.toString()), Integer.parseInt(j.toString())));
        }

        item.setItemMeta(meta);

        JSONObject nbts = (JSONObject) json.getData(itemPath+".NBTData");

        if(nbts != null && nbts.size() > 0){

            nbts.forEach((i, j) -> setNBT(item, i.toString(), j.toString()));

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
