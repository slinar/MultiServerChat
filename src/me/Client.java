package me;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class Client extends JavaPlugin {

	static ExecutorService es = Executors.newFixedThreadPool(4);
	static Selector selector;
	static SocketChannel sc;
	static SelectionKey clientKey;
	static boolean status = false;
	static InetSocketAddress addr;
	static String pwd;
	private static Client ins;
	static final String heat = "_ACTIVE_";
	
	/*public static void main(String[] args) {
		init();
	}*/
	
	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		es.shutdownNow();
	}

	@Override
	public void onEnable() {
		ins = this;
		getServer().getPluginManager().registerEvents(new PluginListener(), this);
		ConfigFile.loadConfig();
		init();
	}
	
	private static void init () {
		try {
			selector = Selector.open();
			addr = ConfigFile.getAddr();
			System.out.println(ConfigFile.prefix + addr.toString());
			pwd = ConfigFile.getPwd();
			es.execute(new Connect());
			es.execute(new KeepAlive());
			es.execute(new ReadHandle());
		} catch (IOException e) {}
	}
	
	public static Client getIns() {
		return ins;
	}
}
