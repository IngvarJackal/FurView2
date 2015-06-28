package ru.furry.furview2.drivers.e621;

import android.os.AsyncTask;
import android.util.Log;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ru.furry.furview2.images.FurImage;
import ru.furry.furview2.images.FurImageBuilder;
import ru.furry.furview2.images.Rating;
import ru.furry.furview2.images.RemoteFurImage;
import ru.furry.furview2.system.AsyncRemoteImageHandler;
import ru.furry.furview2.system.AsyncRemoteImageHandlerGUI;
import ru.furry.furview2.system.Files;
import ru.furry.furview2.system.NoSSLv3SocketFactory;
import ru.furry.furview2.system.Utils;

public class DriverE621 implements AsyncRemoteImageHandler{

    private Object parentActivity;

    private static final String SEARCH_PATH = "https://e621.net/post/index.xml";
    private static final String CHARSET = "UTF-8";

    private static final int SEARCH_LIMIT = 95;

    private final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("EEE MMM DD kk:mm:ss Z yyyy");
    private SSLSocketFactory NoSSLv3Factory;

    private final ImageLoader imageLoader = ImageLoader.getInstance();
    private final DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
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

    private void init() throws NoSuchAlgorithmException, KeyManagementException {
        checkPathStructure(permanentStorage);
        SSLContext sslcontext = SSLContext.getInstance("TLSv1");
        sslcontext.init(null, null, null);
        NoSSLv3Factory = new NoSSLv3SocketFactory(sslcontext.getSocketFactory());
    }

    public DriverE621(String permanentStorage, Utils.Tuple<Integer, Integer> preview, AsyncRemoteImageHandlerGUI parentActivity) throws NoSuchAlgorithmException, KeyManagementException {
        this.previewHeight = preview.x;
        this.previewWidth = preview.y;
        this.parentActivity = parentActivity;
        this.permanentStorage = permanentStorage;
        init();
    }

    public DriverE621(String permanentStorage, int previewWidth, int previewHeight, AsyncRemoteImageHandlerGUI parentActivity) throws NoSuchAlgorithmException, KeyManagementException {
        this.previewHeight = previewHeight;
        this.previewWidth = previewWidth;
        this.parentActivity = parentActivity;
        this.permanentStorage = permanentStorage;
        init();
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

    private FurImage remoteFurImagetoFurImageE926(RemoteFurImageE621 remoteImage) {
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
                .createFurImage();
    }

    HttpsURLConnection openPage(URL url) throws IOException {
        if (proxy != null) {
            HttpsURLConnection.setDefaultSSLSocketFactory(NoSSLv3Factory);
            return (HttpsURLConnection) url.openConnection(proxy);
        } else {
            HttpsURLConnection.setDefaultSSLSocketFactory(NoSSLv3Factory);
            return (HttpsURLConnection) url.openConnection();
        }
    }

    private void checkPathStructure(String path) {
        try {
            checkDir(new File(path));
            checkDir(new File(String.format("%s/%s", path, Files.IMAGES)));
            checkDir(new File(String.format("%s/%s", path, Files.THUMBS)));
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
                            .setFileExt(element.getAttribute("file_ext"))
                            .setPageUrl(null)
                            .setIdE926(Integer.parseInt(element.getAttribute("id")))
                            .setAuthor(element.getAttribute("author"))
                            .setCreatedAt(formatter.parseDateTime(element.getAttribute("created_at").replace(" 00:", " 24:")))
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

    private FurImage downloadImage(RemoteFurImageE621 remoteImage, ImageAware listener) throws IOException {
        imageLoader.displayImage(remoteImage.getFileUrl(), listener, displayOptions);
        return remoteFurImagetoFurImageE926(remoteImage);
    }

    public List<FurImage> download(List<RemoteFurImageE621> images, List<? extends ImageAware> listeners) throws IOException {
        List<FurImage> downloadedImages = new ArrayList<>(images.size());
        for (int i = 0; i < images.size(); i++) {
            downloadedImages.add(downloadImage(images.get(i), listeners.get(i)));
        }
        return downloadedImages;
    }

}
