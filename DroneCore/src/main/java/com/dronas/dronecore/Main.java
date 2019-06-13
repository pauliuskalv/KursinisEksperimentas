package com.dronas.dronecore;

import com.dronas.dronecore.session.Session;

public class Main {
    public static void main(String[] args) throws Exception {
        String brokerIpAddress = null;

        if (args.length == 0) {
            // printUsage();
            // return;
        }

        // System.load("/usr/local/share/OpenCV/java/libopencv_java342.so");
        System.load("/usr/local/share/OpenCV/java/libopencv_java342.so");

        // brokerIpAddress = args[0];

        Session.start(brokerIpAddress);
    }

    private static void printUsage() {
        System.out.println("Usage: {program} {broker_ip_address}");
    }
}
