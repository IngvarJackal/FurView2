package ru.furry.furview2.system;

import java.util.List;

import ru.furry.furview2.images.RemoteFurImage;

public interface AsyncRemoteImageHandler {
    void processRemoteImages(List<? extends RemoteFurImage> images);
}
