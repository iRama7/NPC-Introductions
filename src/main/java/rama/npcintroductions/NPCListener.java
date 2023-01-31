package rama.npcintroductions;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static rama.npcintroductions.NPCIntroductions.*;

public class NPCListener implements Listener {

    @EventHandler
    public void NpcClickListener(NPCRightClickEvent e){
        Player clicker = e.getClicker();
        NPC clickedNPC = e.getNPC();
        int clickedNPCid = clickedNPC.getId();
        for (String key:plugin.getConfig().getConfigurationSection("introductions").getKeys(false)) {
            int i = Integer.parseInt(key);
            if(clickedNPCid == i){
                playIntroduction(i, clicker);
                }

        }
    }
}
