package ru.furry.furview2.drivers.e926;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ru.furry.furview2.images.FurImage;
import ru.furry.furview2.images.FurImageBuilder;
import ru.furry.furview2.images.Rating;
import ru.furry.furview2.images.RemoteFurImage;
import ru.furry.furview2.system.Files;
import ru.furry.furview2.system.Utils;

public class DriverE926 {

    private final String SEARCH_PATH = "https://e926.net/post/index.xml";
    private final String CHARSET = "UTF-8";

    protected final int SEARCH_LIMIT = 50;

    protected final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    protected final DateTimeFormatter formatter = DateTimeFormat.forPattern("EEE MMM DD kk:mm:ss Z yyyy");
    protected final ImageLoader imageLoader = ImageLoader.getInstance();
    protected final DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .build();

    protected String permanentStorage;
    protected int previewWidth;
    protected int previewHeight;
    protected Proxy proxy;

    public DriverE926(String permanentStorage, Utils.Tuple<Integer, Integer> preview) {
        this.permanentStorage = permanentStorage;
        this.previewHeight = preview.x;
        this.previewWidth = preview.y;
        checkPathStructure(permanentStorage);
    }

    public DriverE926(String permanentStorage, int previewWidth, int previewHeight) {
        this.permanentStorage = permanentStorage;
        this.previewHeight = previewHeight;
        this.previewWidth = previewWidth;
        checkPathStructure(permanentStorage);
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
        // FOR DEBUG
        Log.d("fgsfds", "Proxy used: " + Utils.getIP(proxy));
        //
    }

    class IteratorE926 implements Iterator {

        private String searchUrl;
        private int currentPage = 1;
        private HttpsURLConnection page;
        private String searchQuery;
        private List<RemoteFurImageE926> readedImages;

        public IteratorE926(String searchUrl, String searchQuery) throws IOException, SAXException, ParserConfigurationException {
            this.searchUrl = searchUrl;
            this.searchQuery = searchQuery;
            URL query = makeURL(searchUrl, searchQuery, currentPage, SEARCH_LIMIT);
            page = openPage(query);
            readedImages = readImages(page);
        }

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

        private List<RemoteFurImageE926> readImages(HttpsURLConnection connection) throws ParserConfigurationException, IOException, SAXException {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(connection.getInputStream());
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("post");

            ArrayList<RemoteFurImageE926> images = new ArrayList<>(SEARCH_LIMIT);
            for (int postNumber = 0; postNumber < nList.getLength(); postNumber++) {
                Node post = nList.item(postNumber);
                Element element = (Element) post;

                images.add(new RemoteFurImageE926Builder()
                        .setSearchQuery(searchQuery)
                        .setDescription(element.getAttribute("description"))
                        .setScore(Integer.parseInt(element.getAttribute("score")))
                        .setRating(makeRating(element.getAttribute("rating")))
                        .setFileUrl(element.getAttribute("file_url"))
                        .setFileExt(element.getAttribute("file_ext"))
                        .setPageUrl(null)
                        .setIdE926(Integer.parseInt(element.getAttribute("id")))
                        .setAuthor(element.getAttribute("author"))
                        .setCreatedAt(formatter.parseDateTime(element.getAttribute("created_at").replace(" 00", " 24")))
                        .setSources(Arrays.asList(element.getAttribute("sources").replace("[&quot;", "").replace("&quot;]", "").split("&quot;,&quot;")))
                        .setTags(Arrays.asList(element.getAttribute("tags").split(" ")))
                        .setArtists(Arrays.asList(element.getAttribute("artist").replace("[&quot;", "").replace("&quot;]", "").split("&quot;,&quot;")))
                        .setMd5(new BigInteger(element.getAttribute("md5"), 16))
                        .setFileSize(Integer.parseInt(element.getAttribute("file_size")))
//                        .setFileWidth(Integer.parseInt(element.getAttribute("width"))) // WTF??? iSN'T PRESENT IN SOME POSTS!!!
//                        .setFileHeight(Integer.parseInt(element.getAttribute("height"))) // WTF???
                        .createRemoteFurImageE926());
            }

            return images;
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

        @Override
        public boolean hasNext() {
            RemoteFurImage image = null;

            if (readedImages.size() > 0) {
                image = readedImages.get(0);
            } else {
                currentPage += 1;
                try {
                    readedImages = readImages(openPage(makeURL(searchUrl, searchQuery, currentPage, SEARCH_LIMIT)));
                } catch (ParserConfigurationException | IOException | SAXException e) {
                    Utils.printError(e);
                }
                if (readedImages.size() > 0) {
                    image = readedImages.get(0);
                }
            }

            return image != null;
        }

        @Override
        public RemoteFurImage next() {
            RemoteFurImageE926 image = readedImages.get(0);
            readedImages.remove(0);
            return image;
        }

        @Override
        public void remove() {
            readedImages.remove(0);
        }
    }

    private FurImage remoteFurImagetoFurImageE926(RemoteFurImageE926 remoteImage) {
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
            return (HttpsURLConnection) url.openConnection(proxy);
        } else {
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

    private FurImage downloadImage(RemoteFurImageE926 remoteImage, ImageAware listener) throws IOException {
        Log.d("fgsfds", remoteImage.getFileUrl());
        imageLoader.displayImage(remoteImage.getFileUrl(), listener, displayOptions);
        return remoteFurImagetoFurImageE926(remoteImage);
    }

    public Iterator<RemoteFurImageE926> search(String searchQuery) throws IOException, ParserConfigurationException, SAXException {
        return new IteratorE926(SEARCH_PATH, searchQuery);
    }

    public List<FurImage> download(List<RemoteFurImageE926> images, List<? extends ImageAware> listeners) throws IOException {
        List<FurImage> downloadedImages = new ArrayList<>(images.size());
        for (int i = 0; i < images.size(); i++) {
            downloadedImages.add(downloadImage(images.get(i), listeners.get(i)));
        }
        return downloadedImages;
    }

}
