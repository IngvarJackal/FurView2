package ru.furry.furview2.drivers;

import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

import ru.furry.furview2.database.FurryDatabase;
import ru.furry.furview2.drivers.e621.RemoteFurImageE621;
import ru.furry.furview2.images.FurImage;
import ru.furry.furview2.system.AsyncRemoteImageHandlerGUI;
import ru.furry.furview2.system.Utils;

public abstract class Driver {
    public abstract void search(String searchQuery);
    public abstract void getNext(String searchQuery);
    public abstract boolean hasNext();
    public abstract List<FurImage> download(List<? extends RemoteFurImageE621> images, List<? extends ImageAware> listeners, List<ImageLoadingListener> loadingListeners);
    public abstract void downloadImage(String imageUrl, ImageAware listener);
    public abstract void downloadImage(String imageUrl, ImageAware listener, ImageLoadingListener loadingListener);
    public abstract List<FurImage> downloadPreview(List<? extends RemoteFurImageE621> images, List<? extends ImageAware> listeners);
    public abstract void loadFromLocalStorage(List<FurImage> images, List<? extends ImageAware> listeners);
    public abstract void saveToDBandStorage(FurImage image, FurryDatabase database);
    public abstract void deleteFromDBandStorage(FurImage image, FurryDatabase database);
    public abstract void setSfw(boolean sfw);
    public abstract void init(String permanentStorage, AsyncRemoteImageHandlerGUI remoteImagesHandler);

}
