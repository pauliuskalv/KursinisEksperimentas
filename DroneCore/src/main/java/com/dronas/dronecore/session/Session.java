package com.dronas.dronecore.session;

import com.dronas.dronecore.comm.BrokerCommunicator;

import com.dronas.dronecore.conf.TomlConfReader;
import com.dronas.dronecore.io.Gps;
import com.dronas.dronecore.video.Camera;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class Session {
    private static Timer sVideoSendTimer;

    private static Timer sDataSendTimer;

    private static Thread dataListenThread;

    private static final int DATA_SEND_FREQUENCY_MILLIS = TomlConfReader.getParameterInteger("timers.data_send_frequency");
    private static final int VIDEO_SEND_FREQUENCY_MILLIS = TomlConfReader.getParameterInteger("timers.video_send_frequency");

    private static Camera sDroneCamera;

    public static void start(String brokerIP) {
        // Initialize devices
        Gps.init();
        sDroneCamera = new Camera();

        sVideoSendTimer = new Timer();
        sDataSendTimer = new Timer();

        sVideoSendTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                videoSendTask();
            }
        }, VIDEO_SEND_FREQUENCY_MILLIS, VIDEO_SEND_FREQUENCY_MILLIS);

        sDataSendTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                dataSendTask();
            }
        }, DATA_SEND_FREQUENCY_MILLIS, DATA_SEND_FREQUENCY_MILLIS);

        Logger.getAnonymousLogger().info("Data and Video streams started");
    }

    public static void stop() {
        if (sVideoSendTimer == null || sDataSendTimer == null)
            return;

        sVideoSendTimer.cancel();
        sDataSendTimer.cancel();
    }

    private static void videoSendTask() {
        BrokerCommunicator.sendVideoData(sDroneCamera.grabFrame());
    }

    private static void dataSendTask() {
        String toSend = DroneDataBuilder.build().toString();

        BrokerCommunicator.sendMessage(toSend);
    }
}
