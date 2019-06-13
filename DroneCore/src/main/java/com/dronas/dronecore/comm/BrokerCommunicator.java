package com.dronas.dronecore.comm;

import com.dronas.dronecore.conf.TomlConfReader;
import com.dronas.dronecore.encryption.AESEncryptionHandler;
import com.dronas.dronecore.encryption.IEncryptionHandler;
import com.dronas.dronecore.encryption.RsaEncryptionHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class BrokerCommunicator {
    private static final String BROKER_IP_ADDRESS = TomlConfReader.getParameterString("communication.broker_ip_address");
    private static final int BROKER_DATA_PORT = TomlConfReader.getParameterInteger("communication.broker_data_port");
    private static final int BROKER_VIDEO_PORT = TomlConfReader.getParameterInteger("communication.broker_video_port");
    private static final int PACKET_SIZE = TomlConfReader.getParameterInteger("communication.packet_size");

    private static List<String> sVideoLog = new ArrayList<>();
    private static List<String> sDataLog = new ArrayList<>();

    private static final int TEST_STEPS = 1000;
    private static IEncryptionHandler sEncryptionHandler = new RsaEncryptionHandler();

    public static void sendMessage(String message) {
        long start = System.nanoTime();

        sendData(BROKER_DATA_PORT, sEncryptionHandler.encrypt(message.getBytes()));

        long end = System.nanoTime();

        if (sDataLog.size() <= TEST_STEPS)
            sDataLog.add(start + "," + end + "\n");
        if (sDataLog.size() <= TEST_STEPS)
            System.out.println("Data log size: " + sDataLog.size());
        if (sDataLog.size() == TEST_STEPS) {
            try {
                File file = new File("/home/pi/Documents/out/dataOut.csv");
                FileOutputStream stream = new FileOutputStream(file);

                file.createNewFile();

                for (String string : sDataLog)
                    stream.write(string.getBytes());

                stream.flush();
                stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendVideoData(byte[] bytes) {
        long start = System.nanoTime();

        sendData(BROKER_VIDEO_PORT, sEncryptionHandler.encrypt(bytes));

        long end = System.nanoTime();

        if (sVideoLog.size() <= TEST_STEPS)
            sVideoLog.add(start + "," + end + "\n");
        if (sVideoLog.size() <= TEST_STEPS)
            System.out.println("Video log size: " + sVideoLog.size());
        if (sVideoLog.size() == TEST_STEPS) {
            try {
                File file = new File("/home/pi/Documents/out/videoOut.csv");
                FileOutputStream stream = new FileOutputStream(file);

                file.createNewFile();

                for (String string : sVideoLog)
                    stream.write(string.getBytes());

                stream.flush();
                stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void sendData(int port, byte[] bytes) {
        try {
            BufferedSocket socket = new BufferedSocket(new Socket(InetAddress.getByName(BROKER_IP_ADDRESS), port), PACKET_SIZE);

            // System.out.println(bytes.length);

            socket.sendBytes(bytes);

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
