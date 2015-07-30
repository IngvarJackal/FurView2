package ru.furry.furview2.drivers;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.furry.furview2.R;
import ru.furry.furview2.drivers.dbDriver.DriverDB;
import ru.furry.furview2.drivers.e621.DriverE621;

public enum Drivers {

    E621NET("e621.net", DriverE621.class, R.string.e621help, Drivertype.IMAGEBOARD),
    DB_DRIVER("local search", DriverDB.class, R.string.local_search_help, Drivertype.IMAGEBOARD);

    private static Drivers[] driverlist = Drivers.values();

    public final String drivername;
    public final Class<? extends Driver> driverclass;
    public final int searchHelpId;
    public final Drivertype type;

    Drivers(String name, Class<? extends Driver> driverclass, int searchHelp, Drivertype type) {
        this.drivername = name;
        this.driverclass = driverclass;
        this.searchHelpId = searchHelp;
        this.type = type;
    }

    public static Drivers getDriver(String drivername) {
        for (Drivers driver : driverlist) {
            if (driver.drivername.equals(drivername)) {
                return driver;
            }
        }
        return null;
    }

    public static String[] getDriverList() {
        String[] drivernames = new String[driverlist.length];
        for (int i = 0; i < driverlist.length; i++) {
            drivernames[i] = driverlist[i].drivername;
        }
        return drivernames;
    }

    public static List<Drivers> getDriversByGroup(Drivertype type) {
        List<Drivers> drivers = new ArrayList<>();
        for (Drivers driver : driverlist) {
            if (driver.type == type) {
                drivers.add(driver);
            }
        }
        return drivers;
    }

}