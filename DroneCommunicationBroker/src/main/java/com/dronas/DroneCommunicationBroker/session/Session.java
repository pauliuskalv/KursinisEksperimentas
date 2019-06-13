package com.dronas.DroneCommunicationBroker.session;

import com.dronas.DroneCommunicationBroker.comm.BufferedSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class Session {
    private static final int COMM_STATION_PORT = 1233;
    private static final int DRONE_DATA_PORT = 1234;
    private static final int DRONE_VIDEO_PORT = 1235;

    private static Thread sDataListenThread;

    private static Thread sVideoListenThread;

    private static Thread sCommStationThread;

    private static String sHostIpAddress;

    public static void startSession() {
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));

            String ip = in.readLine();
            sHostIpAddress = ip;
        } catch (IOException e) {
            e.printStackTrace();
        }

        sDataListenThread = new Thread(new Runnable() {
            public void run() {
                dataListenSession();
            }
        });

        sVideoListenThread = new Thread(new Runnable() {
            public void run() {
                videoListenSession();
            }
        });

        sCommStationThread = new Thread(new Runnable() {
            public void run() {
                commStationSession();
            }
        });

        sDataListenThread.start();
        sVideoListenThread.start();
        sCommStationThread.start();
    }

    public static void stopSession() {
        sDataListenThread.interrupt();
        sVideoListenThread.interrupt();
        sCommStationThread.interrupt();
    }

    private static void dataListenSession() {
        ServerSocket dataListenSocket;

        try {
            dataListenSocket = new ServerSocket();
            dataListenSocket.setReuseAddress(true);
            dataListenSocket.setSoTimeout(5000);
            dataListenSocket.bind(new InetSocketAddress(DRONE_DATA_PORT));
        } catch (IOException e) {
            System.out.println("Failed to create the drone data socket! Error:");
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                BufferedSocket socket = new BufferedSocket(dataListenSocket.accept());

                byte[] received = socket.receiveBytes();

                // System.out.println("Received data. Size: " + received.length);
                // System.out.println(new String(received));

                DroneData.setDroneData(received);
                DroneData.setLatestEndPoint(socket.getEndpoint());

                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void videoListenSession() {
        ServerSocket videoListenSocket = null;
        try {
            videoListenSocket = new ServerSocket();
            videoListenSocket.setReuseAddress(true);
            videoListenSocket.setSoTimeout(5000);
            videoListenSocket.bind(new InetSocketAddress(DRONE_VIDEO_PORT));
        } catch (IOException e) {
            System.out.println("Failed to create the drone video socket! Error:");
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                BufferedSocket socket = new BufferedSocket(videoListenSocket.accept());

                byte[] received = socket.receiveBytes();

                // System.out.println("Received video data. Size: " + received.length);

                DroneData.setVideoData(received);
                DroneData.setLatestEndPoint(socket.getEndpoint());

                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void commStationSession() {
        ServerSocket commStationListenSocket = null;
        try {
            commStationListenSocket = new ServerSocket();
            commStationListenSocket.setReuseAddress(true);
            commStationListenSocket.bind(new InetSocketAddress(COMM_STATION_PORT));
        } catch (IOException e) {
            System.out.println("Failed to create the comm station socket! Error:");
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                BufferedSocket socket = new BufferedSocket(commStationListenSocket.accept());

                byte[] received = socket.receiveBytes();

                String command = new String(received);

                // System.out.println("Received comm station data: " + command);

                if (command.equals("GET_VIDEO"))
                    socket.sendBytes((byte[]) DroneData.getVideoData());
                else if (command.equals("GET_DATA"))
                    socket.sendBytes((byte[]) DroneData.getDroneData());
                else {
                    byte[] toSend = socket.receiveBytes();

                    BufferedSocket droneSocket = new BufferedSocket(new Socket(DroneData.getLatestEndpoint(), 1233));

                    droneSocket.sendBytes(toSend);

                    droneSocket.close();
                }

                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void clearBuffer(byte[] buffer, int toClear) {
        for (int i = 0; i < toClear; i ++)
            buffer[i] = 0;
    }
}
