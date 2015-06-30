package ru.furry.furview2.drivers.e621;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
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
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ru.furry.furview2.database.FurryDatabase;
import ru.furry.furview2.images.FurImage;
import ru.furry.furview2.images.FurImageBuilder;
import ru.furry.furview2.images.Rating;
import ru.furry.furview2.images.RemoteFurImage;
import ru.furry.furview2.system.AsyncRemoteImageHandler;
import ru.furry.furview2.system.AsyncRemoteImageHandlerGUI;
import ru.furry.furview2.system.Files;
import ru.furry.furview2.system.NoSSLv3Factory;
import ru.furry.furview2.system.Utils;

public class DriverE621 implements AsyncRemoteImageHandler{

    private Object parentActivity;

    private static final String SEARCH_PATH = "https://e621.net/post/index.xml";
    private static final String CHARSET = "UTF-8";

    private static final int SEARCH_LIMIT = 95;

    private final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("EEE MMM DD kk:mm:ss Z yyyy");
    private final static ImageLoader imageLoader = ImageLoader.getInstance();
    private final static DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .build();
    private final DisplayImageOptions downloadOptions = new DisplayImageOptions.Builder()
            .cacheOnDisk(true)
            .build();

    private String permanentStorage;
    private int previewWidth;
    private int previewHeight;
    private Proxy proxy;
    private boolean hasImages = true;

    private int currentPage = 0;
    private HttpsURLConnection page;
    private String searchQuery;

    public DriverE621(String permanentStorage, Utils.Tuple<Integer, Integer> preview, AsyncRemoteImageHandlerGUI parentActivity) {
        this.parentActivity = parentActivity;
        this.permanentStorage = permanentStorage;
        this.previewHeight = preview.x;
        this.previewWidth = preview.y;
        checkPathStructure(permanentStorage);
    }

    public DriverE621(String permanentStorage, int previewWidth, int previewHeight, AsyncRemoteImageHandlerGUI parentActivity) {
        this.parentActivity = parentActivity;
        this.permanentStorage = permanentStorage;
        this.previewHeight = previewHeight;
        this.previewWidth = previewWidth;
        checkPathStructure(permanentStorage);
    }

    private <T extends AsyncRemoteImageHandlerGUI, AsyncImageHandlerGUI> void setParent(T val) {
        parentActivity = val;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
        // FOR DEBUG
        Log.d("fgsfds", "Proxy used: " + Utils.getIP(proxy));
        //
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

    private static FurImage remoteFurImagetoFurImageE926(RemoteFurImageE621 remoteImage) {
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

    HttpsURLConnection openPage(URL url) throws IOException {
        HttpsURLConnection.setDefaultSSLSocketFactory(new NoSSLv3Factory());
        if (proxy != null) {
            return (HttpsURLConnection) url.openConnection(proxy);
        } else {
            return (HttpsURLConnection) url.openConnection();
        }
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

    class ReadingImages extends AsyncTask<Utils.Tuple<HttpsURLConnection, ? extends AsyncRemoteImageHandler>, Void, List<RemoteFurImageE621>> {

        private AsyncRemoteImageHandler delegate;

        @Override
        protected List<RemoteFurImageE621> doInBackground(Utils.Tuple<HttpsURLConnection, ? extends AsyncRemoteImageHandler>... tuples) {

            Log.d("fgsfds", "Starting retrieving remote images...");

            HttpsURLConnection connection = tuples[0].x;
            delegate = tuples[0].y;

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
                            .setCreatedAt(formatter.parseDateTime(element.getAttribute("created_at").replace(" 00", " 24")))
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
            delegate.processRemoteImages(images);
        }
    }

    @Override
    public void processRemoteImages(List<? extends RemoteFurImage> images) {
        ((AsyncRemoteImageHandlerGUI)parentActivity).retrieveRemoteImages(images);
        ((AsyncRemoteImageHandlerGUI)parentActivity).unblockInterfaceForRemoteImages();
    }

    private void startReadingImages(HttpsURLConnection page) {
        ((AsyncRemoteImageHandlerGUI)parentActivity).blockInterfaceForRemoteImages();
        new ReadingImages().execute(new Utils.Tuple<HttpsURLConnection, AsyncRemoteImageHandler>(page, this));
    }

    public void search(String searchQuery) throws IOException {
        currentPage = 0;
        this.searchQuery = searchQuery;
        getNext(searchQuery);
    }

    public void getNext(String searchQuery) throws IOException {
        currentPage += 1;
        URL query = makeURL(SEARCH_PATH, searchQuery, currentPage, SEARCH_LIMIT);
        page = openPage(query);
        startReadingImages(page);
    }

    public boolean hasNext() {
        return hasImages;
    }

    private static void fetchImage(String url, ImageAware listener) throws IOException {
        imageLoader.displayImage(url, listener, displayOptions);
    }

    private static FurImage downloadPreviews(RemoteFurImageE621 remoteImage, ImageAware listener) throws IOException {
        imageLoader.displayImage(remoteImage.getPreviewUrl(), listener, displayOptions);
        return remoteFurImagetoFurImageE926(remoteImage);
    }

    public static List<FurImage> download(List<? extends RemoteFurImageE621> images, List<? extends ImageAware> listeners) throws IOException {
        List<FurImage> downloadedImages = new ArrayList<>(images.size());
        for (int i = 0; i < images.size(); i++) {
            fetchImage(images.get(i).getFileUrl(), listeners.get(i));
            downloadedImages.add(remoteFurImagetoFurImageE926(images.get(i)));
        }
        return downloadedImages;
    }

    public static void downloadImage(String imageUrl, ImageAware listener) throws IOException {
        fetchImage(imageUrl, listener);
    }

    public static List<FurImage> downloadPreview(List<? extends RemoteFurImageE621> images, List<? extends ImageAware> listeners) throws IOException {
        List<FurImage> downloadedImages = new ArrayList<>(images.size());
        for (int i = 0; i < images.size(); i++) {
            downloadedImages.add(downloadPreviews(images.get(i), listeners.get(i)));
        }
        return downloadedImages;
    }

    public static void loadFromStorage(List<FurImage> images, List<? extends ImageAware> listeners) {
        for (int i = 0; i < images.size(); i++) {
            imageLoader.displayImage(images.get(i).getFilePath(), listeners.get(i), displayOptions);
        }
    }

    public void save(FurImage image, FurryDatabase database) {
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
     * @param image
     * @param database
     */
    public void delete(FurImage image, FurryDatabase database) {
        database.deleteByMd5(image.getMd5());
        File file = new File(image.getFilePath());
        file.delete();
    }

}
