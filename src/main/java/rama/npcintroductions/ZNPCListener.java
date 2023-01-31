package rama.npcintroductions;

import io.github.znetworkw.znpcservers.npc.event.NPCInteractEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static rama.npcintroductions.NPCIntroductions.playIntroduction;
import static rama.npcintroductions.NPCIntroductions.plugin;

public class ZNPCListener implements Listener {

    @EventHandler
    public void ZNPCClickListener(NPCInteractEvent e){
        if(e.isRightClick()){
            Player clicker = e.getPlayer();
            io.github.znetworkw.znpcservers.npc.NPC clickedNPC = e.getNpc();
            int clickedNPCid = clickedNPC.getNpcPojo().getId();
            for (String key:plugin.getConfig().getConfigurationSection("introductions").getKeys(false)) {
                int i = Integer.parseInt(key);
                if(clickedNPCid == i){
                    playIntroduction(i, clicker);
                }
            }
        }
    }
}
