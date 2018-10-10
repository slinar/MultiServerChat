package me;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class ClientCore {

    private static final CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
    private static final CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();

    public static String read() {
        if (!Client.status)
            return null;
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        readBuffer.clear();
        int count = 0;
        try {
            while (((count = Client.sc.read(readBuffer)) > 0) || ((count = Client.sc.read(readBuffer)) == -1)) {
                if (count == -1) {
                    System.out.println("ClientCore.read = -1 关闭连接");
                    close();
                    break;
                }
            }
            readBuffer.flip();
            if (readBuffer.limit() == 0)
                return null;
            String msg = decoder.decode(readBuffer).toString().trim();
            if (msg.length() == 0)
                return null;
            return msg;
        } catch (IOException e) {
            close();
            return null;
        }
    }

    public static int write(String msg) {
        if (!Client.status)
            return 0;
        msg = msg.trim();
        if (msg.length() <= 0)
            return 0;
        CharBuffer charBuffer = CharBuffer.allocate(1024);
        ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
        charBuffer.clear();
        charBuffer.put(msg);
        charBuffer.flip();
        try {
            writeBuffer = encoder.encode(charBuffer);
        } catch (CharacterCodingException e1) {
            System.out.println(ConfigFile.prefix + "ClinetCore.write 字符串编码异常");
            return 0;
        }
        int count = 0;
        try {
            while (writeBuffer.position() < writeBuffer.limit()) {
                Client.sc.write(writeBuffer);
            }
        } catch (IOException e) {
            close();
            return 0;
        }
        return count;
    }

    public static void close() {
        Client.status = false;
        try {
            Client.clientKey.cancel();
            Client.sc.close();
        } catch (Exception e) {
        }
        Client.getIns().getLogger().info(Client.addr.toString() + " 断开连接!");
    }

    public static String rand() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * @param string    需要hash的字符串
     * @param algorithm hash算法(MD5,SHA-1,SHA-256)
     * @return String 字符串hash后的值,如果传入的hash算法不被支持则返回null
     */
    public static String hash(String string, String algorithm) {
        final char[] HEX = "0123456789abcdef".toCharArray();
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("不能识别的Hash算法");
            return null;
        }
        byte[] temp = md.digest(string.getBytes());
        char[] chars = new char[temp.length * 2];
        for (int i = 0, offset = 0; i < temp.length; i++) {
            chars[offset++] = HEX[temp[i] >> 4 & 0xf];
            chars[offset++] = HEX[temp[i] & 0xf];
        }
        return new String(chars);
    }
}
