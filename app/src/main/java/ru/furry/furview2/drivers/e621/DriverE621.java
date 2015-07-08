package ru.furry.furview2.drivers.e621;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
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
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ru.furry.furview2.database.FurryDatabase;
import ru.furry.furview2.drivers.Driver;
import ru.furry.furview2.images.FurImage;
import ru.furry.furview2.images.FurImageBuilder;
import ru.furry.furview2.images.Rating;
import ru.furry.furview2.images.RemoteFurImage;
import ru.furry.furview2.system.AsyncHandlerUI;
import ru.furry.furview2.system.Files;
import ru.furry.furview2.system.ProxiedHTTPSLoader;
import ru.furry.furview2.system.Utils;

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
    private HttpsURLConnection page;
    private String searchQuery;

    @Override
    public void init(String permanentStorage) {
        this.permanentStorage = permanentStorage;
        checkPathStructure(permanentStorage);
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
                .createFurImage();
    }

    private void checkPathStructure(String path) {
        try {
            checkDir(new File(path));
            checkDir(new File(String.format("%s/%s", path, Files.E621_IMAGES)));
        } catch (IOException e) {
            Utils.printError(e);
        }
    }

    private void checkDir(File path) throws IOException {
        if (!path.exists()) {
            path.mkdirs();
        } else if (!path.isDirectory()) {
            path.delete();
            path.mkdir();
        }
    }


    // LOGIC

    class ReadingImages extends AsyncTask<Utils.Tuple<HttpsURLConnection, AsyncHandlerUI<RemoteFurImage>>, Void, List<RemoteFurImageE621>> {

        private AsyncHandlerUI<RemoteFurImage> handler;

        @Override
        protected List<RemoteFurImageE621> doInBackground(Utils.Tuple<HttpsURLConnection, AsyncHandlerUI<RemoteFurImage>>... tuples) {

            Log.d("fgsfds", "Starting retrieving remote images...");

            HttpsURLConnection connection = tuples[0].x;
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
                            .setDescription(element.getAttribute("description"))
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
            return images;
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

    private void startReadingRemoteImages(HttpsURLConnection page, AsyncHandlerUI<RemoteFurImage> remoteImagesHandler) {
        remoteImagesHandler.blockUI();
        new ReadingImages().execute(new Utils.Tuple<HttpsURLConnection, AsyncHandlerUI<RemoteFurImage>>(page, remoteImagesHandler));
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
            page = ProxiedHTTPSLoader.openPage(query);
        } catch (IOException e) {
            Utils.printError(e);
        }
        startReadingRemoteImages(page, remoteImagesHandler);
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

    private static void fetchImage(String url, ImageAware listener, ImageLoadingListener loadingListener) throws IOException {
        imageLoader.displayImage(url, listener, displayOptions, loadingListener);
    }

    private static FurImage fetchPreviews(RemoteFurImageE621 remoteImage, ImageAware listener, ImageLoadingListener loadingListener) throws IOException {
        imageLoader.displayImage(remoteImage.getPreviewUrl(), listener, displayOptions, loadingListener);
        return remoteFurImagetoFurImageE621(remoteImage);
    }

    /*
    @Override
    public void downloadImageFile(String imageUrl, ImageAware listener) {
        Log.d("fgsfds", "downloading image: " + imageUrl);
        try {
            fetchImage(imageUrl, listener, null);
        } catch (IOException e) {
            Utils.printError(e);
        }
    }
    */

    @Override
    public void downloadImageFile(String imageUrl, ImageAware listener, ImageLoadingListener loadingListener) {
        Log.d("fgsfds", "downloading image: " + imageUrl);
        try {
            fetchImage(imageUrl, listener, loadingListener);
        } catch (IOException e) {
            Utils.printError(e);
        }
    }

    @Override
    public void downloadPreviewFile(List<? extends RemoteFurImage> images, List<? extends ImageAware> listeners, List<ImageLoadingListener> loadingListeners) {
        for (int i = 0; i < images.size(); i++) {
            try {
                fetchPreviews((RemoteFurImageE621)images.get(i), listeners.get(i), loadingListeners.get(i));
            } catch (IOException e) {
                Utils.printError(e);
            }
            ;
        }
    }

    @Override
    public void loadFileFromLocalStorage(List<FurImage> images, List<? extends ImageAware> listeners, List<ImageLoadingListener> loadingListeners) {
        for (int i = 0; i < images.size(); i++) {
            imageLoader.displayImage(images.get(i).getFilePath(), listeners.get(i), displayOptions, loadingListeners.get(i));
        }
    }

    @Override
    public void saveToDBandStorage(FurImage image, FurryDatabase database) {
        image.setFilePath(String.format("%s/%s/", permanentStorage, Files.E621_IMAGES) + image.getMd5() + "." + image.getFileExt());
        database.create(image);
        final String imagePath = image.getFilePath();
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
