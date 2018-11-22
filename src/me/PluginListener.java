package me;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PluginListener implements Listener {

    private final String[] COLOR = { "§4", "§c", "§6", "§e", "§2", "§a", "§b", "§3", "§1", "§9", "§d", "§5", "§f", "§7",
            "§8", "§0", "§l", "§n", "§o", "§k", "§m", "§r" };

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (Client.status) {
            String worldName = new StringBuilder()
                    .append("[§e")
                    .append(e.getPlayer().getWorld().getName())
                    .append("§r]")
                    .toString();
            String playerName = new StringBuilder()
                    .append("<")
                    .append(e.getPlayer().getDisplayName())
                    .append("> ")
                    .toString();
            String msg = filterColor(e.getMessage());
            String finalMsg = new StringBuilder().append(worldName).append(playerName).append(msg).toString();
            ClientCore.write(finalMsg);
            e.setCancelled(true);
            msg = null;
        } else {
            //Client.getIns().getLogger().info("MultiServerChat 未连接!");
        }
    }

    private String filterColor(String msg) {
        for (int i = 0; i < COLOR.length; i++) {
            msg = msg.replace(COLOR[i], "");
        }
        return msg;
    }
}
