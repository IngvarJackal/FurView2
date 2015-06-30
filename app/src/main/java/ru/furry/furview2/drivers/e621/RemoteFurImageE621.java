package ru.furry.furview2.drivers.e621;

import org.joda.time.DateTime;

import java.math.BigInteger;
import java.util.List;

import ru.furry.furview2.images.Rating;
import ru.furry.furview2.images.RemoteFurImage;

public class RemoteFurImageE621 extends RemoteFurImage {

    private final int idE621;
    private String author;
    private DateTime createdAt;
    private List<String> sources;
    private List<String> tags;
    private List<String> artists;
    private int score;
    private BigInteger md5;
    private int fileSize;
    private int fileWidth;
    private int fileHeight;
    private String filePath;

    public RemoteFurImageE621(String searchQuery, String description, int score, Rating rating, String fileUrl, String fileExt, String pageUrl, int idE621, String author, DateTime createdAt, List<String> sources, List<String> tags, List<String> artists, BigInteger md5, int fileSize, int fileWidth, int fileHeight, String filePath, String previewUrl) {
        super(searchQuery, description, rating, fileUrl, fileExt, pageUrl, previewUrl);
        this.idE621 = idE621;
        this.author = author;
        this.createdAt = createdAt;
        this.sources = sources;
        this.tags = tags;
        this.artists = artists;
        this.score = score;
        this.md5 = md5;
        this.fileSize = fileSize;
        this.fileWidth = fileWidth;
        this.fileHeight = fileHeight;
        this.filePath = filePath;
    }

    public int getIdE621() {
        return idE621;
    }

    public String getAuthor() {
        return author;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public List<String> getSources() {
        return sources;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<String> getArtists() {
        return artists;
    }

    public BigInteger getMd5() {
        return md5;
    }

    public int getFileSize() {
        return fileSize;
    }

    public int getFileWidth() {
        return fileWidth;
    }

    public int getFileHeight() {
        return fileHeight;
    }

    public int getScore() {
        return score;
    }

    public String getFilePath() {
        return filePath;
    }
}