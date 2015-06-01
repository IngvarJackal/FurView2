package ru.furry.furview2.drivers.e926;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
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

import ru.furry.furview2.images.DownloadedFurImage;
import ru.furry.furview2.images.DownloadedFurImageBuilder;
import ru.furry.furview2.images.FurImage;
import ru.furry.furview2.images.FurImageBuilder;
import ru.furry.furview2.images.Rating;
import ru.furry.furview2.images.RemoteFurImage;
import ru.furry.furview2.system.Files;
import ru.furry.furview2.system.Utils;

public class DriverE926 {

    private final String SEARCH_PATH = "https://e926.net/post/index.xml";
    private final String SHOW_IMAGE_PATH = "https://e926.net/post/show.xml";
    private final String E926_IMAGE_PAGE = "https://e926.net/post/show/";
    private final String CHARSET = "UTF-8";

    protected final int SEARCH_LIMIT = 50;

    protected final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    protected final DateTimeFormatter formatter = DateTimeFormat.forPattern("EEE MMM DD kk:mm:ss Z yyyy");

    protected String permanentStorage;
    protected String cacheStorage;
    protected int previewWidth;
    protected int previewHeight;

    public DriverE926(String permanentStorage, String cacheStorage, Utils.Tuple<Integer, Integer> preview) throws IOException {
        this.permanentStorage = permanentStorage;
        this.cacheStorage = cacheStorage;
        this.previewHeight = preview.x;
        this.previewWidth = preview.y;
        checkPathStructure(permanentStorage);
        checkPathStructure(cacheStorage);
    }

    public DriverE926(String permanentStorage, String cacheStorage, int previewWidth, int previewHeight) throws IOException {
        this.permanentStorage = permanentStorage;
        this.cacheStorage = cacheStorage;
        this.previewHeight = previewHeight;
        this.previewWidth = previewWidth;
        checkPathStructure(permanentStorage);
        checkPathStructure(cacheStorage);
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
                        .setFile_url(element.getAttribute("file_url"))
                        .setFile_ext(element.getAttribute("file_ext"))
                        .setPage_url(null)
                        .setIdE926(Integer.parseInt(element.getAttribute("id")))
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

    HttpsURLConnection openPage(URL url) throws IOException {
        return (HttpsURLConnection) url.openConnection();
    }

    private FurImage makeImage(HttpsURLConnection connection, RemoteFurImageE926 remoteImage) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(connection.getInputStream());
        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("post");
        Node post = nList.item(0);
        Element element = (Element) post;

        return (new FurImageBuilder()
                .makeFromRemoteFurImage(remoteImage)
                .setPageUrl(String.format("%s%s", E926_IMAGE_PAGE, remoteImage.idE926))
                .setAuthor(element.getAttribute("author"))
                .setCreatedAt(formatter.parseDateTime(element.getAttribute("created_at")))
                .setSources(Arrays.asList(element.getAttribute("sources").replace("[&quot;", "").replace("&quot;]", "").split("&quot;,&quot;")))
                .setTags(Arrays.asList(element.getAttribute("tags").split(" ")))
                .setArtists(Arrays.asList(element.getAttribute("artist").replace("[&quot;", "").replace("&quot;]", "").split("&quot;,&quot;")))
                .createFurImage());
    }

    private FurImage getImage(RemoteFurImageE926 post) throws IOException, ParserConfigurationException, SAXException {
        URL url = new URL(String.format("%s?id=%s", SHOW_IMAGE_PATH, post.idE926));
        HttpsURLConnection conn = openPage(url);
        return makeImage(conn, post);
    }

    private void checkPathStructure(String path) throws IOException {
        checkDir(new File(path));
        checkDir(new File(String.format("%s/%s", path, Files.IMAGES)));
        checkDir(new File(String.format("%s/%s", path, Files.THUMBS)));
    }

    private void checkDir(File path) throws IOException {
        if (!path.exists()) {
            path.mkdirs();
        } else if (!path.isDirectory()) {
            path.delete();
            path.mkdir();
        }
    }

    private DownloadedFurImage downloadImage(FurImage image) throws IOException {
        URL url = new URL(image.getFileUrl());
        InputStream is = url.openStream();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[16384];
        int nRead;
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();

        BigInteger md5 = Utils.getMD5(buffer.toByteArray());
        String filePath = String.format("%s/%s/%s.%s", cacheStorage, Files.IMAGES, md5.toString(16), image.getFileExt());

        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        bos.write(buffer.toByteArray());
        bos.flush();
        bos.close();

        Bitmap bImage = BitmapFactory.decodeFile(filePath);

        return (new DownloadedFurImageBuilder()
                .makeFromFurImage(image)
                .setDownloadedAt(new DateTime())
                .setMd5(md5)
                .setFileSize(buffer.size())
                .setFileHeight(bImage.getHeight())
                .setFileWidth(bImage.getWidth())
                .setFileName(String.format("%s.%s", md5.toString(16), image.getFileExt()))
                .setRoot(cacheStorage)
                .setPreviewHeight(previewHeight)
                .setPreviewWidth(previewWidth)
                .createDownloadedFurImage()
                );
    }


    public Iterator<RemoteFurImageE926> search(String searchQuery) throws IOException, ParserConfigurationException, SAXException {
        return new IteratorE926(SEARCH_PATH, searchQuery);
    }

    public List<FurImage> fetch(Iterator<RemoteFurImageE926> posts) throws IOException, ParserConfigurationException, SAXException {
        List<RemoteFurImageE926> p = new ArrayList<>();
        while (posts.hasNext()) {
            p.add(posts.next());
        }
        return fetch(p);
    }

    public List<FurImage> fetch(Iterator<RemoteFurImageE926> posts, int limit) throws IOException, ParserConfigurationException, SAXException {
        List<RemoteFurImageE926> p = new ArrayList<>(limit);
        int i = 0;
        while (i < limit && posts.hasNext()) {
            i++;
            p.add(posts.next());
        }
        return fetch(p);
    }

    public List<FurImage> fetch(List<RemoteFurImageE926> posts) throws IOException, ParserConfigurationException, SAXException {
        List<FurImage> images = new ArrayList<>(posts.size());
        for (RemoteFurImageE926 post : posts) {
            images.add(getImage(post));
        }
        return images;
    }

    public List<DownloadedFurImage> download(List<FurImage> images) throws IOException {
        List<DownloadedFurImage> downloadedImages = new ArrayList<>(images.size());
        for (FurImage image : images) {
            downloadedImages.add(downloadImage(image));
        }
        return downloadedImages;
    }

}
