package ru.furry.furview2.drivers;

import android.util.Log;

import ru.furry.furview2.R;
import ru.furry.furview2.drivers.dbDriver.DriverDB;
import ru.furry.furview2.drivers.e621.DriverE621;

public enum Drivers {
    E621NET("e621.net", DriverE621.class, R.string.e621help),
    DB_DRIVER("local search", DriverDB.class, R.string.local_search_help);

    private static Drivers[] driverlist = Drivers.values();

    public final String drivername;
    public final Class<? extends Driver> driverclass;
    public final int searchHelpId;

    Drivers(String name, Class<? extends Driver> driverclass, int searchHelp) {
        this.drivername = name;
        this.driverclass = driverclass;
        this.searchHelpId = searchHelp;
    }

    public static Drivers getDriver(String drivername) {
        for (Drivers driver : driverlist) {
            Log.d("fgsfds", "avaliable driver: '" + driver.drivername + "' driver name: '" + drivername + "'");
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

}