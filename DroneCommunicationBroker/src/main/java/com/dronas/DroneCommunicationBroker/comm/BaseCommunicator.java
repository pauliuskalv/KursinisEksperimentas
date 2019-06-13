package com.dronas.DroneCommunicationBroker.comm;

import java.net.Socket;

public class BaseCommunicator {
    protected void sendData(String address, int port, byte[] bytes) {
        try {
            Socket commSocket = new Socket(address, port);

            commSocket.getOutputStream().write(bytes);

            commSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
