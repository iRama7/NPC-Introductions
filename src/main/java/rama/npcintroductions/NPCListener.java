package rama.npcintroductions;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;

import static rama.npcintroductions.NPCIntroductions.*;

public class NPCListener implements Listener {

    HashMap<Player, Boolean> cd = new HashMap<>();

    @EventHandler
    public void NpcClickListener(NPCRightClickEvent e){
        Player clicker = e.getClicker();
        NPC clickedNPC = e.getNPC();
        int clickedNPCid = clickedNPC.getId();
        for (String key:plugin.getConfig().getConfigurationSection("introductions").getKeys(false)) {
            int i = Integer.parseInt(key);
            if(clickedNPCid == i){
                long cooldown = plugin.getConfig().getLong("introductions." + i + ".cooldown");

                    if(cd.get(clicker) == null || !cd.get(clicker)) {
                        playIntroduction(i, clicker);
                        startCooldown(clicker, cooldown);
                    }else{
                        String cdMessage = plugin.getConfig().getString("introductions." + i + ".cooldown-message");
                        if(cdMessage != null && !cdMessage.isEmpty()){
                            clicker.sendMessage(ChatColor.translateAlternateColorCodes('&', cdMessage));
                        }
                    }
                }

        }
    }

    public void startCooldown(Player p, long cooldown){
        cd.put(p, true);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                cd.put(p, false);
            }
        }, cooldown);
    }
}
