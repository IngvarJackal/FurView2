package ru.furry.furview2.system;

import ru.furry.furview2.drivers.Driver;
import ru.furry.furview2.images.RemoteFurImage;

public interface ListlikeIterator<T> {
    public void init(Driver driver, String searchQuery);
    public void asyncLoad(AsyncHandlerUI<Boolean> remoteImagesHandler);
    public boolean hasPrevious();
    public T next();
    public T previous();
}
