package ru.furry.furview2.drivers.e926;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ru.furry.furview2.images.Rating;
import ru.furry.furview2.images.RemoteFurImage;

public class DriverE926 {

    private final String SEARCH_PATH = "https://e926.net/post/index.xml";
    private final String SHOW_IMAGE_PATH = "https://e926.net/post/show.xml";
    private String charset = "UTF-8";

    protected int SEARCH_LIMIT = 50;

    class IteratorE926 implements Iterator {

        private String searchUrl;
        private int currentPage = 1;
        private HttpsURLConnection page;
        private String searchQuery;
        private List<RemoteFurImageE926> readedImages;
        private DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

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
                images.add(new RemoteFurImageE926(
                        searchQuery,
                        element.getAttribute("description"),
                        Integer.parseInt(element.getAttribute("score")),
                        makeRating(element.getAttribute("rating")),
                        element.getAttribute("file_url"),
                        element.getAttribute("file_ext"),
                        null,
                        Integer.parseInt(element.getAttribute("id"))
                ));
            }

            return images;
            }

        private URL makeURL(String searchURL, String searchQuery, int page, int limit) throws MalformedURLException {
            URL url = null;
            try {
                String query = String.format("%s?tags=%s&page=%s&limit=%s",
                        searchURL,
                        URLEncoder.encode(searchQuery, charset),
                        page,
                        limit);
                url = new URL(query);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return url;
        }

        private HttpsURLConnection openPage(URL url) {
            HttpsURLConnection connection = null;
            try {
                connection = (HttpsURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return connection;
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
                    e.printStackTrace();
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

    public Iterator search(String searchQuery) throws IOException, ParserConfigurationException, SAXException {
        return new IteratorE926(SEARCH_PATH, searchQuery);
    }

}
