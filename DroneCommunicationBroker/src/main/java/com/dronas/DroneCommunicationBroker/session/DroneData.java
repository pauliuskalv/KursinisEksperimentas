package com.dronas.DroneCommunicationBroker.session;

import java.net.InetAddress;

public class DroneData {
    private static Object latestDroneData;
    private static Object latestVideoData;

    private static InetAddress latestEndPoint;

    public static synchronized Object getVideoData() {
        return latestVideoData;
    }

    public static synchronized void setVideoData(Object bytes) {
        latestVideoData = bytes;
    }

    public static synchronized Object getDroneData() {
        return latestDroneData;
    }

    public static synchronized void setDroneData(Object bytes) {
        latestDroneData = bytes;
    }

    public static synchronized InetAddress getLatestEndpoint() {
        return latestEndPoint;
    }

    public static synchronized void setLatestEndPoint(InetAddress endpoint) {
        latestEndPoint = endpoint;
    }
}
