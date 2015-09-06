package ru.furry.furview2.system;

public interface BlockingOrientationHandler {
    void lockOrientation();
    void unlockOrientation();
    void setLocked(boolean locked);
    boolean getLocked();
}
