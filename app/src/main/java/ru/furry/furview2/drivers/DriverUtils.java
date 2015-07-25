package ru.furry.furview2.drivers;

import java.io.File;
import java.io.IOException;

import ru.furry.furview2.system.Files;
import ru.furry.furview2.system.Utils;

public class DriverUtils {
    public static void checkPathStructureForImages(String path) {
        try {
            checkDir(new File(path));
            checkDir(new File(String.format("%s/%s", path, Files.IMAGES)));
        } catch (IOException e) {
            Utils.printError(e);
        }
    }

    private static void checkDir(File path) throws IOException {
        if (!path.exists()) {
            path.mkdirs();
        } else if (!path.isDirectory()) {
            path.delete();
            path.mkdir();
        }
    }
}
