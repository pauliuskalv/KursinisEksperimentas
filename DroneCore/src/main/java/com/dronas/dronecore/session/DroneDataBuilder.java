package com.dronas.dronecore.session;

import com.dronas.dronecore.io.Gps;
import org.json.JSONObject;

import java.util.Calendar;

public class DroneDataBuilder {
    public static JSONObject build() {
        JSONObject toReturn = new JSONObject();

        toReturn = toReturn.put("date", Calendar.getInstance().getTime().toString());

        // region[Build gps data]
        toReturn = toReturn.put("gps", Gps.buildData());
        // endregion

        // region[Build arduino data]
        // TODO
        // endregion

        return toReturn;
    }
}
