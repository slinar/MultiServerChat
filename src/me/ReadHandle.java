package me;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Bukkit;

public class ReadHandle implements Runnable {

    @Override
    public void run() {
        while (true) {
            try {
                if (Client.status && (Client.selector.select() > 0)) {
                    Set<SelectionKey> keys = Client.selector.selectedKeys();
                    Iterator<SelectionKey> iterator = keys.iterator();
                    while (iterator.hasNext()) {
                        if (iterator.next().isReadable()) {
                            String msg = ClientCore.read();
                            if (msg != null) {
                                // System.out.println(msg);
                                Bukkit.broadcastMessage(msg);
                            }
                        }
                        iterator.remove();
                    }
                }
                Thread.sleep(1);
            } catch (InterruptedException | IOException e) {
            }
        }
    }
}
