package me;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PluginListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (Client.status) {
            String msg = "[" + e.getPlayer().getWorld().getName() + "]" + "<" + e.getPlayer().getName() + "> "
                    + e.getMessage();
            ClientCore.write(msg);
            e.setCancelled(true);
            msg = null;
        } else {
            Client.getIns().getLogger().info("MultiServerChat 未连接!");
        }
    }
}
