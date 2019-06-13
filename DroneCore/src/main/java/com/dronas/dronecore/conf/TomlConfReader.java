package com.dronas.dronecore.conf;

import com.moandjiezana.toml.Toml;

import java.io.IOException;
import java.io.InputStream;

public class TomlConfReader {
    private static Toml sToml;

    static {
        sToml = new Toml();
        InputStream stream = Object.class.getResourceAsStream("/app.config");

        sToml.read(stream);

        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getParameterString(String arg) {
        return sToml.contains(arg) ? sToml.getString(arg) : null;
    }

    public static int getParameterInteger(String arg) {
        return sToml.contains(arg) ? sToml.getLong(arg).intValue() : 0;
    }
}
