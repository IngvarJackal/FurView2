package ru.furry.furview2.drivers.dbDriver;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.furry.furview2.database.FurryDatabase;
import ru.furry.furview2.database.FurryDatabaseUtils;
import ru.furry.furview2.drivers.Driver;
import ru.furry.furview2.images.FurImage;
import ru.furry.furview2.images.Rating;
import ru.furry.furview2.images.RemoteFurImage;
import ru.furry.furview2.system.AsyncHandlerUI;
import ru.furry.furview2.system.Files;
import ru.furry.furview2.system.Utils;

import static ru.furry.furview2.drivers.DriverUtils.checkPathStructureForImages;

public class DriverDB extends Driver {

    private FurryDatabase furryDatabase;
    private FurryDatabaseUtils databaseUtils;

    private boolean isSfw;
    private final static ImageLoader imageLoader = ImageLoader.getInstance();
    private final static Pattern md5Pattern = Pattern.compile("md5:([1234567890abcdef]+)");
    private String permanentStorage;
    private final DisplayImageOptions downloadOptions = new DisplayImageOptions.Builder()
            .build();
    private final static DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
            .build();

    @Override
    public void init(String permanentStorage, Context context) {
        this.permanentStorage = permanentStorage;
        checkPathStructureForImages(permanentStorage);
        furryDatabase = new FurryDatabase(context);
        databaseUtils = new FurryDatabaseUtils(furryDatabase);
    }

    @Override
    public void setSfw(boolean sfw) {
        this.isSfw = sfw;
    }

    @Override
    public void search(String searchQuery, final AsyncHandlerUI<RemoteFurImage> remoteImagesHandler) {
        Matcher md5Matcher = md5Pattern.matcher(searchQuery);
        if (md5Matcher.find()) {
            furryDatabase.searchByMD5(new BigInteger(md5Matcher.group(1), 16), new AsyncHandlerUI<FurImage>() {
                @Override
                public void blockUI() {
                    remoteImagesHandler.blockUI();
                }

                @Override
                public void unblockUI() {
                    remoteImagesHandler.unblockUI();
                }

                @Override
                public void retrieve(List<? extends FurImage> images) {
                    List<FurImage> filteredImages = new ArrayList<>(images.size());
                    for (FurImage image : images) {
                        if (Collections.disjoint(image.getTags(), databaseUtils.getBlacklist())) {
                            filteredImages.add(image);
                        }
                    }
                    if (!filteredImages.isEmpty()) {
                        if (isSfw && filteredImages.get(1).getRating() == Rating.SAFE) {
                            remoteImagesHandler.retrieve(filteredImages);
                        }
                    } else {
                        remoteImagesHandler.retrieve(filteredImages);
                    }
                }
            });
        } else {
            if (isSfw)
                searchQuery += " rating:s";
            furryDatabase.search(searchQuery, new AsyncHandlerUI<FurImage>() {
                @Override
                public void blockUI() {
                    remoteImagesHandler.blockUI();
                }

                @Override
                public void unblockUI() {
                    remoteImagesHandler.unblockUI();
                }

                @Override
                public void retrieve(List<? extends FurImage> images) {
                    List<FurImage> filteredImages = new ArrayList<>(images.size());
                    for (FurImage image : images) {
                        if (Collections.disjoint(image.getTags(), databaseUtils.getBlacklist())) {
                            filteredImages.add(image);
                        }
                    }
                    remoteImagesHandler.retrieve(filteredImages);
                }
            });
        }
    }

    @Override
    public void getNext(AsyncHandlerUI<RemoteFurImage> remoteImagesHandler) {
        remoteImagesHandler.retrieve(new ArrayList<RemoteFurImage>());
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public void downloadFurImage(List<RemoteFurImage> images, List<AsyncHandlerUI<FurImage>> furImagesHandlers) {
        for (int i = 0; i < images.size(); i++) {
            furImagesHandlers.get(i).retrieve(new ArrayList<>(Arrays.asList((FurImage) images.get(i))));
        }
    }

    @Override
    public void downloadImageFile(FurImage image, ImageAware listener, ImageLoadingListener loadingListener) {
        String imagePath = image.getFilePath();
        if (!new File(imagePath).exists()) {
            imagePath = image.getFileUrl();
        } else {
            imagePath = Uri.decode(Uri.fromFile(new File(imagePath)).toString());
        }
        Log.d("fgsfds", "loading image " + imagePath);
        imageLoader.displayImage(imagePath, listener, displayOptions, loadingListener);
    }

    @Override
    public void downloadPreviewFile(List<? extends RemoteFurImage> images, List<? extends ImageAware> listeners, List<ImageLoadingListener> loadingListeners) {
        for (int i = 0; i < images.size(); i++) {
            downloadImageFile(((FurImage) images.get(i)), listeners.get(i), loadingListeners.get(i));
        }
    }

    @Override
    public void saveToDBandStorage(FurImage image, FurryDatabase database, AsyncHandlerUI<FurImage> furImageHandler) {
        saveToDBandStorage(image, database);
    }


    @Override
    public void saveToDBandStorage(FurImage image, FurryDatabase database) {
        String imagePath_;
        if (!new File(image.getFilePath()).exists()) {
            return; // don't need to execute -- is alerady saved
        } else {
            imagePath_ = image.getFilePath();
        }
        image.setFilePath(String.format("%s/%s/", permanentStorage, Files.IMAGES) + image.getMd5() + "." + image.getFileExt());
        database.create(image);
        final String imagePath = imagePath_;
        imageLoader.loadImage(image.getFileUrl(), downloadOptions, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(imagePath);
                    loadedImage.compress(Bitmap.CompressFormat.PNG, 100, out);
                } catch (Exception e) {
                    Utils.printError(e);
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        Utils.printError(e);
                    }
                }
            }
        });
    }

    @Override
    public void deleteFromDBandStorage(FurImage image, FurryDatabase database) {
        database.deleteByMd5(image.getMd5());
        File file = new File(image.getFilePath());
        file.delete();
    }
}
