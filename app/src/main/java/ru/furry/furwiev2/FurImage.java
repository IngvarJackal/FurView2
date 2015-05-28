package ru.furry.furwiev2;

import java.math.BigInteger;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.List;

public class FurImage {
    /**
     * This is mainly container class for images
     *
     * id The ID of the post
     * author Username of the user who uploaded the post
     * created_at When the post was uploaded
     * downloaded_at When the image was downloaded
     * sources An array of the post's sources
     * tags The post's tags
     * local_tags The post's tags on local machine
     * artist List of artists
     * description The post's description
     * score The post's score
     * local_score The post's score on local machine
     * rating The post's rating. One of: e, q, s
     * parent_id If the post has a parent, the ID of the parent post
     * children List of post IDs of this post's children
     * md5 The post's MD5 hash
     * file_url Relative URL of the filename
     * file_ext The post's extension
     * file_size Size (in bytes) of the post
     * width Height of the image
     * height Height of the image
     * preview_url Relative URL of the preview (thumbnail) filename
     * preview_width Height of the preview (thumbnail) image
     * preview_height Height of the preview (thumbnail) image
     */
    private int id;
    private String author;
    private GregorianCalendar created_at;
    private GregorianCalendar downloaded_at;
    private List<URL> sources;
    private List<String> tags;
    private List<String> local_tags;
    private List<String> artists;
    private String description;
    private int score;
    private int local_score;
    private Rating rating;
    private int parent_id;
    private List<Integer> children;
    private BigInteger md5;
    private URL file_url;
    private String file_ext;
    private int file_size;
    private int width;
    private int height;
    private URL preview_url;
    private int preview_width;
    private int preview_height;

    public int getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public GregorianCalendar getCreated_at() {
        return created_at;
    }

    public List<URL> getSources() {
        return sources;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<String> getArtists() {
        return artists;
    }

    public String getDescription() {
        return description;
    }

    public int getScore() {
        return score;
    }

    public Rating getRating() {
        return rating;
    }

    public int getParent_id() {
        return parent_id;
    }

    public List<Integer> getChildren() {
        return children;
    }

    public BigInteger getMd5() {
        return md5;
    }

    public URL getFile_url() {
        return file_url;
    }

    public String getFile_ext() {
        return file_ext;
    }

    public int getFile_size() {
        return file_size;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public URL getPreview_url() {
        return preview_url;
    }

    public int getPreview_width() {
        return preview_width;
    }

    public int getPreview_height() {
        return preview_height;
    }
}
