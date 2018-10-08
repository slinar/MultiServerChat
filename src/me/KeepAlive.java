package me;

public class KeepAlive implements Runnable {
	
	@Override
	public void run() {
		while(true) {
			if (Client.status) {
				ClientCore.write(Client.heat);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
	}
}
