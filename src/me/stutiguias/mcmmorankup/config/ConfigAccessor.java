package me.stutiguias.mcmmorankup.config;

import java.io.*;
import java.util.logging.Level;
import me.stutiguias.mcmmorankup.Mcmmorankup;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigAccessor {

    private final String fileName;
    private final JavaPlugin plugin;
    
    private File configFile;
    private FileConfiguration fileConfiguration;

    public ConfigAccessor(JavaPlugin plugin, String fileName) {
        if (!plugin.isEnabled())
            throw new IllegalArgumentException("plugin must be initiaized");
        this.plugin = plugin;
        this.fileName = fileName;         
    }

    public void setupConfig() throws IOException {
        configFile = new File(this.getPluginDir(), fileName.toLowerCase());   
        
        if(configFile.exists()) return;
        
        configFile.createNewFile();            
        copy(plugin.getResource(fileName), configFile);
    }

    public void reloadConfig() {    	
    	if (configFile == null) {
            configFile = new File(this.getPluginDir(), fileName.toLowerCase());            
        }        

        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);

        InputStream defConfigStream = plugin.getResource(fileName);
        
        if (defConfigStream == null) return;
        
        Reader targetReader = new InputStreamReader(defConfigStream);
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(targetReader);
        fileConfiguration.setDefaults(defConfig);
    }

    private void copy(java.io.InputStream input, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=input.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
    	if (fileConfiguration == null) {
            this.reloadConfig();
        }

    	return fileConfiguration;
    }

    public void saveConfig() {
        if (fileConfiguration == null && configFile == null) return;
        
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "{0} {1} - Could not save config to {2}: {3}", new Object[]{Mcmmorankup.logPrefix, "[Ability Config]", configFile, ex});
        }
    }
    
    public void saveDefaultConfig() {
        if (!configFile.exists()) {            
            this.plugin.saveResource(fileName, false);
        }
    }

    public boolean MakeOld() {
        File file = new File(plugin.getDataFolder(),fileName + "_old");
        file.delete();
        return configFile.renameTo(new File(plugin.getDataFolder(),fileName + "_old"));
    }

    /**
     * @return the pluginDir
     */
    public String getPluginDir() {
        if(fileName.equalsIgnoreCase("config.yml") || fileName.equalsIgnoreCase("menu.yml") || fileName.equalsIgnoreCase("eng.yml")) 
        {
           return Mcmmorankup.PluginDir;
        }else{
           return Mcmmorankup.PluginSkillsDir;
        }    
    }
}
