package ru.furry.furview2.drivers.e621;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ru.furry.furview2.database.FurryDatabase;
import ru.furry.furview2.database.FurryDatabaseUtils;
import ru.furry.furview2.drivers.Driver;
import ru.furry.furview2.images.FurImage;
import ru.furry.furview2.images.FurImageBuilder;
import ru.furry.furview2.images.Rating;
import ru.furry.furview2.images.RemoteFurImage;
import ru.furry.furview2.system.AsyncHandlerUI;
import ru.furry.furview2.system.Files;
import ru.furry.furview2.system.ProxiedHTTPSLoader;
import ru.furry.furview2.system.Utils;

import static ru.furry.furview2.drivers.DriverUtils.checkPathStructureForImages;

public class DriverE621 extends Driver {

    private static final String SEARCH_PATH = "https://e621.net/post/index.xml";
    private static final String CHARSET = "UTF-8";

    private static final int SEARCH_LIMIT = 95;

    private final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("MMM dd kk:mm:ss Z yyyy").withLocale(new Locale("en", "US"));
    private final static ImageLoader imageLoader = ImageLoader.getInstance();
    private final static DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .build();
    private final DisplayImageOptions downloadOptions = new DisplayImageOptions.Builder()
            .cacheOnDisk(true)
            .build();

    private String permanentStorage;
    private boolean hasImages = true;

    private boolean isSfw;

    private int currentPage = 0;
    private String searchQuery;

    private FurryDatabase furryDatabase;
    private FurryDatabaseUtils databaseUtils;

    @Override
    public void init(String permanentStorage, Context context) {
        this.permanentStorage = permanentStorage;
        checkPathStructureForImages(permanentStorage);
        furryDatabase = new FurryDatabase(context);
        databaseUtils = new FurryDatabaseUtils(furryDatabase);
    }

    // UTILITY

    private Rating makeRating(String sRating) {
        Rating rating;
        switch (sRating) {
            case "s":
                rating = Rating.SAFE;
                break;
            case "q":
                rating = Rating.QUESTIONABLE;
                break;
            case "e":
                rating = Rating.EXPLICIT;
                break;
            default:
                rating = Rating.NA;
                break;
        }
        return rating;
    }

    private URL makeURL(String searchURL, String searchQuery, int page, int limit) throws MalformedURLException {
        URL url = null;
        if (isSfw && !searchQuery.contains("rating:safe") && !searchQuery.contains("rating:s")) {
            searchQuery += " rating:s";
        }
        try {
            String query = String.format("%s?tags=%s&page=%s&limit=%s",
                    searchURL,
                    URLEncoder.encode(searchQuery, CHARSET),
                    page,
                    limit);
            url = new URL(query);
        } catch (UnsupportedEncodingException e) {
            Utils.printError(e);
        }
        return url;
    }

    private static FurImage remoteFurImagetoFurImageE621(RemoteFurImageE621 remoteImage) {
        return new FurImageBuilder()
                .makeFromRemoteFurImage(remoteImage)
                .setScore(remoteImage.getScore())
                .setAuthor(remoteImage.getAuthor())
                .setCreatedAt(remoteImage.getCreatedAt())
                .setSources(remoteImage.getSources())
                .setTags(remoteImage.getTags())
                .setArtists(remoteImage.getArtists())
                .setMd5(remoteImage.getMd5())
                .setFileSize(remoteImage.getFileSize())
                .setFileWidth(remoteImage.getFileWidth())
                .setFileHeight(remoteImage.getFileHeight())
                .setDownloadedAt(new DateTime())
                .setPageUrl("https://e621.net/post/show/" + remoteImage.getIdE621())
                .setFilePath(remoteImage.getFileUrl())
                .createFurImage();
    }


    // LOGIC

    class ReadingImages extends AsyncTask<Utils.Tuple<URL, AsyncHandlerUI<RemoteFurImage>>, Void, List<RemoteFurImageE621>> {

        private AsyncHandlerUI<RemoteFurImage> handler;

        @Override
        protected List<RemoteFurImageE621> doInBackground(Utils.Tuple<URL, AsyncHandlerUI<RemoteFurImage>>... tuples) {

            Log.d("fgsfds", "Starting retrieving remote images...");

            URL queryPage = tuples[0].x;
            HttpsURLConnection connection;
            try {
                connection = ProxiedHTTPSLoader.openPage(queryPage);
            } catch (IOException e) {
                Utils.printError(e);
                throw new RuntimeException(e);
            }
            handler = tuples[0].y;

            Document doc = null;
            try {
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                doc = dBuilder.parse(connection.getInputStream());
            } catch (ParserConfigurationException | IOException | SAXException e) {
                Utils.printError(e);
            }

            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("post");

            ArrayList<RemoteFurImageE621> images = new ArrayList<>(SEARCH_LIMIT);
            for (int postNumber = 0; postNumber < nList.getLength(); postNumber++) {
                Node post = nList.item(postNumber);
                Element element = (Element) post;

                // Let's ignore swf and webm
                if (element.getAttribute("file_ext").equals("webm") ||
                        element.getAttribute("file_ext").equals("swf"))
                    continue;
                else
                    images.add(new RemoteFurImageE621Builder()
                            .setSearchQuery(searchQuery)
                            .setDescription(deleteTags(element.getAttribute("description")))
                            .setScore(Integer.parseInt(element.getAttribute("score")))
                            .setRating(makeRating(element.getAttribute("rating")))
                            .setFileUrl(element.getAttribute("file_url"))
                            .setPreviewUrl(element.getAttribute("preview_url"))
                            .setFileExt(element.getAttribute("file_ext"))
                            .setPageUrl(null)
                            .setIdE926(Integer.parseInt(element.getAttribute("id")))
                            .setAuthor(element.getAttribute("author"))
                            .setCreatedAt(formatter.parseDateTime(element.getAttribute("created_at").replace(" 00", " 24").substring(4)))
                            .setSources(Arrays.asList(element.getAttribute("sources").replace("[&quot;", "").replace("&quot;]", "").split("&quot;,&quot;")))
                            .setTags(Arrays.asList(element.getAttribute("tags").split(" ")))
                            .setArtists(Arrays.asList(element.getAttribute("artist").replace("[&quot;", "").replace("&quot;]", "").split("&quot;,&quot;")))
                            .setMd5(new BigInteger(element.getAttribute("md5"), 36))
                            .setFileSize(Integer.parseInt(element.getAttribute("file_size")))
                            .setFileWidth(Integer.parseInt(element.getAttribute("width")))
                            .setFileHeight(Integer.parseInt(element.getAttribute("height")))
                            .createRemoteFurImageE926());
            }

            List<RemoteFurImageE621> filteredImages = new ArrayList<>(images.size());
            for (RemoteFurImageE621 image : images) {
                if (Collections.disjoint(image.getTags(), databaseUtils.getAliasedBlacklist(databaseUtils.getBlacklist()))) {
                    filteredImages.add(image);
                }
            }

            return filteredImages;
        }

        @Override
        protected void onPostExecute(List<RemoteFurImageE621> images) {
            if (images.size() == 0) {
                hasImages = false;
            }
            handler.retrieve(images);
            handler.unblockUI();
        }
    }

    protected String deleteTags(String incomingString){
        Pattern pattern = Pattern.compile("\\[.*?\\]|<.*?>");
        Matcher matcher = pattern.matcher(incomingString);
        return matcher.replaceAll("");
    }

    private void startReadingRemoteImages(URL queryPage, AsyncHandlerUI<RemoteFurImage> remoteImagesHandler) {
        remoteImagesHandler.blockUI();
        new ReadingImages().execute(new Utils.Tuple<URL, AsyncHandlerUI<RemoteFurImage>>(queryPage, remoteImagesHandler));
    }

    @Override
    public void search(String searchQuery, AsyncHandlerUI<RemoteFurImage> remoteImagesHandler) {
        currentPage = 0;
        this.searchQuery = searchQuery;
        getNext(remoteImagesHandler);
    }

    @Override
    public void getNext(AsyncHandlerUI<RemoteFurImage> remoteImagesHandler) {
        currentPage += 1;
        URL query = null;
        try {
            query = makeURL(SEARCH_PATH, searchQuery, currentPage, SEARCH_LIMIT);
        } catch (IOException e) {
            Utils.printError(e);
        }
        startReadingRemoteImages(query, remoteImagesHandler);
    }

    @Override
    public boolean hasNext() {
        return hasImages;
    }

    @Override
    public void downloadFurImage(List<RemoteFurImage> images, List<AsyncHandlerUI<FurImage>> furImagesHandlers) {
        for (int i = 0; i < images.size(); i++) {
            furImagesHandlers.get(i).retrieve(new ArrayList<>(Arrays.asList(remoteFurImagetoFurImageE621((RemoteFurImageE621)images.get(i)))));
        }
    }

    @Override
    public void downloadImageFile(FurImage image, ImageAware listener, ImageLoadingListener loadingListener) {
        Log.d("fgsfds", "downloading image: " + image.getFileUrl());
        imageLoader.displayImage(image.getFileUrl(), listener, displayOptions, loadingListener);
    }

    @Override
    public void downloadPreviewFile(List<? extends RemoteFurImage> images, List<? extends ImageAware> listeners, List<ImageLoadingListener> loadingListeners) {
        for (int i = 0; i < images.size(); i++) {
            imageLoader.displayImage(images.get(i).getPreviewUrl(), listeners.get(i), displayOptions, loadingListeners.get(i));
        }
    }

    @Override
    public void saveToDBandStorage(FurImage image, FurryDatabase database, final AsyncHandlerUI<FurImage> furImageHandler) {
        image.setFilePath(String.format("%s/%s/", permanentStorage, Files.IMAGES) + image.getMd5() + "." + image.getFileExt());
        database.create(image);
        final String imagePath = image.getFilePath();
        Log.d("fgsfds", "saving image to " + imagePath);
        imageLoader.loadImage(image.getFileUrl(), downloadOptions, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                furImageHandler.blockUI();
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                furImageHandler.unblockUI();
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(imagePath);
                    loadedImage.compress(Bitmap.CompressFormat.PNG, 100, out);
                } catch (Exception e) {
                    Utils.printError(e);
                } finally {
                    furImageHandler.unblockUI();
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        Utils.printError(e);
                    }
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                furImageHandler.unblockUI();
            }
        });
    }

    @Override
    public void saveToDBandStorage(FurImage image, FurryDatabase database) {
        saveToDBandStorage(image, database, new AsyncHandlerUI<FurImage>() {
            @Override
            public void blockUI() {

            }

            @Override
            public void unblockUI() {

            }

            @Override
            public void retrieve(List<? extends FurImage> images) {

            }
        });
    }

    /**
     * Deletes image only from storage, not from database
     *
     * @param image
     * @param database
     */
    @Override
    public void deleteFromDBandStorage(FurImage image, FurryDatabase database) {
        database.deleteByMd5(image.getMd5());
        File file = new File(image.getFilePath());
        file.delete();
    }

    @Override
    public void setSfw(boolean sfw) {
        isSfw = sfw;
    }

}
