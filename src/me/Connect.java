package me;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Connect implements Runnable {

    @Override
    public void run() {
        while (Client.run) {
            try {
                while (!Client.status) {
                    Client.sc = SocketChannel.open();
                    // Client.sc.configureBlocking(false);
                    boolean a = Client.sc.connect(Client.addr);
                    if (a) {
                        auth();
                        break;
                    }
                    Client.sc.finishConnect();
                    ClientCore.sleep(900);
                    if (Client.sc.finishConnect()) {
                        auth();
                        break;
                    }
                    Client.sc.close();
                    ClientCore.sleep(100);
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Client.getIns().getLogger().info("连接时发生异常!");
            }
            ClientCore.sleep(100);
        }
    }

    private void connected() {
        try {
            Client.sc.configureBlocking(false);
        } catch (IOException e1) {
        }
        System.out.println(ConfigFile.prefix + ConfigFile.getAddr().toString() + " 完成连接成功!");
        Client.status = true;
        try {
            Client.clientKey = Client.sc.register(Client.selector, SelectionKey.OP_READ);
        } catch (ClosedChannelException e) {
        }
    }

    private void auth() {
       /* try {
            Client.sc.configureBlocking(true);
        } catch (IOException e2) {
        }*/
        Socket socket = Client.sc.socket();
        try {
            socket.setSoTimeout(1500);
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        InputStream in = null;
        OutputStream out = null;
        String uuid = null;
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
            byte[] b = new byte[32];
            in.read(b);
            uuid = new String(b, Charset.forName("UTF-8"));
            String hashcode = ClientCore.hash(Client.pwd + uuid, "SHA-1");
            out.write(hashcode.getBytes("UTF-8"));
            b = new byte[2];
            in.read(b);
            String isOk = new String(b, Charset.forName("UTF-8"));
            if (isOk.equals("OK")) {
                connected();
            } else {
                System.out.println(ConfigFile.prefix + "认证失败");
                Client.sc.close();
            }
        } catch (IOException e) {
            try {
                Client.sc.close();
            } catch (IOException e1) {
            }
        }

    }
    
    
}
