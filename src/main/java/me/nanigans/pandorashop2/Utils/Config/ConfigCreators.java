package me.nanigans.pandorashop2.Utils.Config;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.TechsCode.UltraPermissions.dependencies.commons.io.FileUtils;
import me.nanigans.pandorashop2.CustomizedObjectTypeAdapter;
import me.nanigans.pandorashop2.PandoraShop2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ConfigCreators {
    static GsonBuilder gsonBuilder = new GsonBuilder()
            .registerTypeAdapter(new TypeToken<Map<String, Object>>(){}.getType(),  new CustomizedObjectTypeAdapter());
    static Map<String, Object> map = new HashMap<>();

    /**
     * Creates a new file/directory if the specified path is not found
     * @param path the path to create the file or get the file -> needs plugin.datafolder
     * @return the file the path leads to
     * @throws IOException an error when a file fails to create
     */
    public static File createPath(String path) throws IOException {

        File file = new File(path);
        if(!file.exists()) {
            Path paths = Paths.get(path);
            Files.createDirectories(paths);
        }
        return file;

    }

    /**
     * Creates a new file/directory if the specified path is not found
     * @param path the path to create the file or get the file -> needs plugin.datafolder
     * @return the file the path leads to
     * @throws IOException an error when a file fails to create
     */
    public static File createFile(String path) throws IOException{

        File file = new File(path);
        if(!file.exists()) {
            Path paths = Paths.get(path);
            try {
                Files.createFile(paths);
            }catch(IOException e){
                createPath(path.substring(0, path.lastIndexOf("/")));
                Files.createFile(paths);
            }

        }
        return file;

    }

    /**
     * Copys the default inventory page to a new file
     * @param file the file to copy to
     * @param plugin the plugin we're in
     * @return the new file created
     */
    public static File createDefaultJsonData(File file, PandoraShop2 plugin){

        File configFile = new File(plugin.getDataFolder()+"/InventoryPage.json");

        if(!file.exists() || FileUtils.sizeOf(file) < 5)
        FileUtils.copyFile(configFile, file, true);

        return file;
    }


}
