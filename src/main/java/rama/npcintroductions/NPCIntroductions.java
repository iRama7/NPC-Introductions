package rama.npcintroductions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public final class NPCIntroductions extends JavaPlugin {

    public static NPCIntroductions plugin;
    private static File dataFile;
    private static FileConfiguration Data;
    public static BukkitTask talkTask;

    @Override
    public void onEnable() {
        plugin = this;
        //new UpdateChecker(this, 99981).getVersion(version -> {
            //if (this.getDescription().getVersion().equals(version)) {
                //sendLog("&eYou are using the latest version.");
            //} else {
                //sendLog("&eThere is a new update available!");
                //sendLog("&eYour current version: "+"&c"+plugin.getDescription().getVersion());
                //sendLog("&eLatest version: "+"&a"+version);
            //}
       //});
        this.saveDefaultConfig();
        createDataFile();
        Bukkit.getPluginManager().registerEvents(new NPCListener(), this);
        TabExecutor tabExecutor = new Commands();
        this.getCommand("npci").setExecutor(tabExecutor);
        this.getCommand("npci").setTabCompleter(tabExecutor);
        if(getServer().getPluginManager().getPlugin("Citizens") == null){
            sendLog("Disabling due to Citizens dependency not found!");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static void sendLog(String message) {
        String prefix = ChatColor.translateAlternateColorCodes('&', "&c[&3NPCIntroductions&c] ");
        Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void reloadData() throws IOException {
        Data.save(dataFile);
        Data = YamlConfiguration.loadConfiguration(dataFile);
    }
    public static FileConfiguration getData(){
        return Data;
    }
    public void createDataFile(){
        dataFile = new File(getDataFolder(), "data.yml");
        if(!dataFile.exists()){
            dataFile.getParentFile().mkdirs();
            saveResource("data.yml", false);
        }
        Data = new YamlConfiguration();
        try{
            Data.load(dataFile);
        }catch(IOException | InvalidConfigurationException e){
            e.printStackTrace();
        }
    }
    public static void playIntroduction(int i, Player p){
        FileConfiguration config = plugin.getConfig();
        //variables
        String talk_type = config.getString("introductions."+i+".talk-type");
            //action
            String action_command = config.getString("introductions."+i+".action.command").replaceAll("%player%", p.getName());
            String action_type = config.getString("introductions."+i+".action.type");
            String action_sound_name = config.getString("introductions."+i+".action.sound");
            Sound action_sound = null;
            if(!action_sound_name.equalsIgnoreCase("NONE")) {
                action_sound = Sound.valueOf(action_sound_name);
            }
            long action_sound_pitch = config.getInt("introductions."+i+".action.sound-pitch");
            //action
        List<String> messages = config.getStringList("introductions."+i+".messages");
        String message_sound_name = config.getString("introductions."+i+".sound");
        Sound message_sound = null;
        if(!message_sound_name.equalsIgnoreCase("NONE")){
            message_sound = Sound.valueOf(message_sound_name);
        }

        long message_sound_pitch = config.getInt("introductions."+i+".sound-pitch");
        int interval = config.getInt("introductions."+i+".interval");

        List<String> uuids = getData().getStringList(String.valueOf(i));
        Boolean playerAlreadyClicked = uuids.contains(p.getUniqueId().toString());
        //variables

        if(playerAlreadyClicked){
            executeAction(action_type, p, action_command, action_sound, action_sound_pitch, i);
            return;
        }

        Sound finalMessage_sound = message_sound;
        if(!talk_type.equalsIgnoreCase("NONE")){
            startNpcTalk(talk_type, p, interval, messages.size());
        }
        Sound finalAction_sound = action_sound;
        updateData(i, p);
        new BukkitRunnable() {
            int counter = 0;
            @Override
            public void run() {
                if (counter >= messages.size()) {
                    cancel();
                    executeAction(action_type, p, action_command, finalAction_sound, action_sound_pitch, i);
                    return;
                }

                String message = ChatColor.translateAlternateColorCodes('&', messages.get(counter)).replaceAll("%player%", p.getName());
                p.sendMessage(message);
                if(finalMessage_sound != null) {
                    p.playSound(p.getLocation(), finalMessage_sound, 100, message_sound_pitch);
                }
                counter++;
            }
        }.runTaskTimer(plugin, 0L, interval);
    }

    public static void updateData(int i, Player p){
        List<String> uuids = getData().getStringList(String.valueOf(i));
        uuids.add(p.getUniqueId().toString());
        getData().set(String.valueOf(i), uuids);
        try {
            reloadData();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public static void startNpcTalk(String talk_type, Player p, double interval, int size){
        //variables
        List<String> sounds = plugin.getConfig().getStringList("villager-talk."+talk_type+".sounds");
        long pitch = plugin.getConfig().getInt("villager-talk."+talk_type+".pitch");
        if(sounds == null){
            sendLog("&eCould not found "+talk_type+" talk type");
            return;
        }
        //variables
            interval = interval - 5;
        new BukkitRunnable() {
            int counter = 0;
            @Override
            public void run() {
                if(counter >= size){
                    cancel();
                    return;
                }
                Sound sound = Sound.valueOf(sounds.get(new Random().nextInt(sounds.size())));
                p.playSound(p.getLocation(), sound, 100, pitch);
                counter++;
            }
        }.runTaskTimerAsynchronously(plugin, 0, (long) interval);
    }
    public static void executeAction(String action_type, Player p, String action_command, Sound action_sound, long action_sound_pitch, int i){
        if(action_type.equalsIgnoreCase("PLAYER")){
            p.performCommand(action_command);
            if(action_sound != null){
                p.playSound(p.getLocation(), action_sound, 100, action_sound_pitch);
            }
        }else if(action_type.equalsIgnoreCase("CONSOLE")){
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), action_command);
            if(action_sound != null){
                p.playSound(p.getLocation(), action_sound, 100, action_sound_pitch);
            }
        }else{
            sendLog("&eUnrecognized action type for introduction "+i);
        }
    }
}
