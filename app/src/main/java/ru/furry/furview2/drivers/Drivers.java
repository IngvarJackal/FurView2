package ru.furry.furview2.drivers;

import java.util.HashMap;
import java.util.Map;

import ru.furry.furview2.drivers.e621.DriverE621;

public class Drivers {
    public static final Map<String, Class<? extends Driver>> drivers = new HashMap<String, Class<? extends Driver>>();
    static {
        drivers.put("e621.net", DriverE621.class);
    }
}
