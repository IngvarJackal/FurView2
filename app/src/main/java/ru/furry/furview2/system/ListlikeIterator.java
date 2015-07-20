package ru.furry.furview2.system;

import ru.furry.furview2.drivers.Driver;
import ru.furry.furview2.images.RemoteFurImage;

public interface ListlikeIterator<T> {
    void init(Driver driver, String searchQuery);
    void asyncLoad(AsyncHandlerUI<Boolean> remoteImagesHandler);
    boolean hasPrevious();
    T next();
    T previous();
}
