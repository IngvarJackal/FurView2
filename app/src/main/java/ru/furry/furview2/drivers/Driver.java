package ru.furry.furview2.drivers;

import android.content.Context;

import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import ru.furry.furview2.database.FurryDatabase;
import ru.furry.furview2.images.FurImage;
import ru.furry.furview2.images.RemoteFurImage;
import ru.furry.furview2.system.AsyncHandlerUI;

public abstract class Driver {
    public abstract void search(String searchQuery, AsyncHandlerUI<RemoteFurImage> remoteImagesHandler);
    public abstract void getNext(AsyncHandlerUI<RemoteFurImage> remoteImagesHandler);
    public abstract boolean hasNext();
    public abstract void downloadFurImage(List<RemoteFurImage> images, List<AsyncHandlerUI<FurImage>> furImagesHandlers);
    public abstract void downloadImageFile(FurImage image, ImageAware listener, ImageLoadingListener loadingListener);
    public abstract void downloadPreviewFile(List<? extends RemoteFurImage> images, List<? extends ImageAware> listeners, List<ImageLoadingListener> loadingListeners);
    public abstract void saveToDBandStorage(FurImage image, FurryDatabase database, AsyncHandlerUI<FurImage> furImageHandler);
    public abstract void saveToDBandStorage(FurImage image, FurryDatabase database);
    public abstract void deleteFromDBandStorage(FurImage image, FurryDatabase database);
    public abstract void setSfw(boolean sfw);
    public abstract void init(String permanentStorage, Context context);

}
