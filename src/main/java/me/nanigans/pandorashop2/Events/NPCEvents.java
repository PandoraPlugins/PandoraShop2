package me.nanigans.pandorashop2.Events;

import me.nanigans.pandorashop2.PandoraShop2;
import me.nanigans.pandorashop2.Utils.Config.ConfigCreators;
import me.nanigans.pandorashop2.Utils.Items.InventoryUtils;
import net.citizensnpcs.api.event.NPCRemoveByCommandSenderEvent;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;

public class NPCEvents implements Listener {
    private final PandoraShop2 plugin = PandoraShop2.getPlugin(PandoraShop2.class);

    @EventHandler
    public void onShopClick(NPCRightClickEvent event) throws IOException, ParseException {

        Player player = event.getClicker();
        NPC npc = event.getNPC();

        if(npc.data().has("IsShop")) {
            File toDir = ConfigCreators.createFile(npc.data().get("IsShop")+"/Categories.json");
            ConfigCreators.createDefaultJsonData(toDir, plugin);

            Inventory inv = InventoryUtils.createInventoryShop(npc.data().get("IsShop") + "/Categories.json", 1, player);
            if(inv != null) {
                player.openInventory(inv);
                new ShopClickEvents(player, inv, npc.data().get("IsShop").toString());
            }
        }
    }

    /**
     * When a shop is removed, we remove all files in its directories
     * @param event npc remove event
     */
    @EventHandler
    public void shopRemove(NPCRemoveEvent event){

        if(event.getNPC().data().has("IsShop")){

            File index = new File(event.getNPC().data().get("IsShop").toString());

            String[]entries = index.list();//need to delete everything inside it first before java can delete the directory
            if(entries != null)
            for(String s: entries){
                File currentFile = new File(index.getPath(), s);
                currentFile.delete();
            }
            index.delete();

        }

    }
    /**
     * When a shop is removed, we remove all files in its directories
     * @param event npc remove event
     */
    @EventHandler
    public void shopDelete(NPCRemoveByCommandSenderEvent event){

        if(event.getNPC().data().has("IsShop")){

            File index = new File(event.getNPC().data().get("IsShop").toString());

            String[]entries = index.list();
            if(entries != null)
            for(String s: entries){
                File currentFile = new File(index.getPath(), s);
                currentFile.delete();
            }
            index.delete();

        }


    }

}
