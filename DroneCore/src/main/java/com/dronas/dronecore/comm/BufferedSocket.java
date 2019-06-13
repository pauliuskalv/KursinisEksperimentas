package com.dronas.dronecore.comm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class BufferedSocket {
    private static final int DEFAULT_BUFFER_SIZE = 512;

    private Socket mSocket;
    private int mBufferSize;

    public BufferedSocket(Socket socket) {
        this(socket, DEFAULT_BUFFER_SIZE);
    }

    public BufferedSocket(Socket socket, int bufferSize) {
        this.mSocket = socket;
        this.mBufferSize = bufferSize;
    }

    public InetAddress getEndpoint() {
        return this.mSocket.getInetAddress();
    }

    public void close() {
        try {
            this.mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] receiveBytes() {
        try {
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            InputStream stream = mSocket.getInputStream();

            int received = 0;
            int toReceiveBytes = readIncomingSize(stream);
            byte[] buffer = new byte[mBufferSize];

            do {
                int recv = stream.read(buffer, 0, mBufferSize);

                byteArray.write(buffer, 0, recv);

                received += recv;
            } while (received != toReceiveBytes);

            return byteArray.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendBytes(byte[] bytes) {
        send(Integer.toString(bytes.length).getBytes());
        send("\n".getBytes());
        send(bytes);
    }

    private void send(byte[] bytes) {
        try {
            int size = bytes.length;
            int sent = 0;
            OutputStream stream = this.mSocket.getOutputStream();

            do {
                int toSend = size - sent < mBufferSize ? size - sent : mBufferSize;

                stream.write(bytes, sent, toSend);

                sent += toSend;
            } while (sent != size);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int readIncomingSize(InputStream stream) {
        try {
            char c;
            String s = "";

            do
            {
                c = (char) stream.read();

                if (c == '\n')
                    break;

                s += c + "";
            } while (c != -1);

            return Integer.parseInt(s);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
