package me;

public class KeepAlive implements Runnable {

    @Override
    public void run() {
        while (Client.run) {
            if (Client.status) {
                ClientCore.write(Client.heat);
            }
            ClientCore.sleep(1000);
        }
    }
}
