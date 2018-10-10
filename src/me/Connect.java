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
        while (true) {
            try {

                while (!Client.status) {
                    Client.sc = SocketChannel.open();
                    Client.sc.configureBlocking(false);
                    boolean a = Client.sc.connect(Client.addr);
                    if (a) {
                        auth();
                        break;
                    }

                    try {
                        Client.sc.finishConnect();
                    } catch (Exception e) {
                    }

                    Thread.sleep(900);
                    if (Client.sc.finishConnect()) {
                        auth();
                        break;
                    }
                    Client.sc.close();
                    // System.out.println(ConfigFile.prefix + ConfigFile.getAddr().toString() + "
                    // 连接失败，正在重试!");
                    Thread.sleep(900);
                }

            } catch (InterruptedException | IOException e) {
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }

    }

    void connected() {
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

    void auth() {
        try {
            Client.sc.configureBlocking(true);
        } catch (IOException e2) {
        }
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
                System.out.println("认证失败");
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
