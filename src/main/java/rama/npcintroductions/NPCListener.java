package rama.npcintroductions;

import jdk.internal.org.jline.reader.ConfigurationPath;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.List;

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
