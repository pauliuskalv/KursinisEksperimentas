package com.dronas.dronecore.io;

import com.dronas.dronecore.conf.TomlConfReader;
import com.ivkos.gpsd4j.client.GpsdClient;
import com.ivkos.gpsd4j.client.GpsdClientOptions;
import com.ivkos.gpsd4j.messages.DeviceMessage;
import com.ivkos.gpsd4j.messages.reports.TPVReport;
import io.vertx.core.DeploymentOptions;
import org.json.JSONObject;

import java.time.LocalDateTime;

public class Gps {
    private static final String GPS_DEVICE_PATH = TomlConfReader.getParameterString("gps.gps_device_path");

    private static final int GPSD_SERVICE_PORT = TomlConfReader.getParameterInteger("gps.gpsd_port");
    private static final String GPSD_SERVICE_HOST = TomlConfReader.getParameterString("gps.gpsd_host");

    private static final int GPSD_CONNECT_TIMEOUT = TomlConfReader.getParameterInteger("gps.gpsd_connect_timeout");
    private static final int GPSD_IDLE_TIMEOUT = TomlConfReader.getParameterInteger("gps.gpsd_idle_timeout");
    private static final int GPSD_RECONNECT_ATTEMPTS = TomlConfReader.getParameterInteger("gps.gpsd_reconnect_attempts");
    private static final int GPSD_RECONNECT_INTERVAL = TomlConfReader.getParameterInteger("gps.gpsd_reconnect_interval");

    private static GpsdClient sClient;

    private static TPVReport sLatestTPVReport;

    public static void init() {
        new DeploymentOptions().setWorker(true);

        GpsdClientOptions options = new GpsdClientOptions()
                .setReconnectOnDisconnect(true)
                .setConnectTimeout(GPSD_CONNECT_TIMEOUT) // ms
                .setIdleTimeout(GPSD_IDLE_TIMEOUT) // seconds
                .setReconnectAttempts(GPSD_RECONNECT_ATTEMPTS)
                .setReconnectInterval(GPSD_RECONNECT_INTERVAL); // ms

        sClient = new GpsdClient(GPSD_SERVICE_HOST, GPSD_SERVICE_PORT, options);

        sClient.addHandler(TPVReport.class, Gps::receiveTPVData);

        sClient.setSuccessfulConnectionHandler(gpsdClient -> connnectionHandler());

        sClient.start();
    }

    public static JSONObject buildData() {
        JSONObject toReturn = new JSONObject();

        if (sLatestTPVReport != null) {
            toReturn.put("altitude", sLatestTPVReport.getAltitude());
            toReturn.put("altitude_error", sLatestTPVReport.getAltitudeError());
            toReturn.put("climb_rate", sLatestTPVReport.getClimbRate());
            toReturn.put("climb_rate_error", sLatestTPVReport.getClimbRate());
            toReturn.put("speed", sLatestTPVReport.getSpeed());
            toReturn.put("speed_error", sLatestTPVReport.getSpeedError());
            toReturn.put("course", sLatestTPVReport.getCourse());
            toReturn.put("course_error", sLatestTPVReport.getCourseError());
            toReturn.put("longitude", sLatestTPVReport.getLongitude());
            toReturn.put("longitude_error", sLatestTPVReport.getLongitudeError());
            toReturn.put("latitude", sLatestTPVReport.getLatitude());
            toReturn.put("latitude_error", sLatestTPVReport.getLatitude());

            // Statistical data
            toReturn.put("date_created_drone", LocalDateTime.now());
        }

        return toReturn;
    }

    private static synchronized void receiveTPVData(TPVReport tpvReport) {
        sLatestTPVReport = tpvReport;
    }

    private static void connnectionHandler() {
        DeviceMessage device = new DeviceMessage();
        device.setPath(GPS_DEVICE_PATH);
        device.setNative(true);

        sClient.sendCommand(device);
        sClient.watch();
    }
}
