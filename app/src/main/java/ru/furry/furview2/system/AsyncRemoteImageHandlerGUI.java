package ru.furry.furview2.system;

import java.util.List;

import ru.furry.furview2.images.RemoteFurImage;

public interface AsyncRemoteImageHandlerGUI {
    void blockInterfaceForRemoteImages();
    void unblockInterfaceForRemoteImages();
    void retrieveRemoteRemoteImages(List<? extends RemoteFurImage> images);
}
