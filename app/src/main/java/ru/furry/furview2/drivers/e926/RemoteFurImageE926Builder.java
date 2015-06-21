package ru.furry.furview2.drivers.e926;

import org.joda.time.DateTime;

import java.math.BigInteger;
import java.util.List;

import ru.furry.furview2.images.Rating;

public class RemoteFurImageE926Builder {
    private String searchQuery;
    private String description;
    private int score;
    private Rating rating;
    private String fileUrl;
    private String fileExt;
    private String pageUrl;
    private int idE926;
    private String author;
    private DateTime createdAt;
    private List<String> sources;
    private List<String> tags;
    private List<String> artists;
    private BigInteger md5;
    private int fileSize;
    private int fileWidth;
    private int fileHeight;
    private String filePath;

    public RemoteFurImageE926Builder setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
        return this;
    }

    public RemoteFurImageE926Builder setDescription(String description) {
        this.description = description;
        return this;
    }

    public RemoteFurImageE926Builder setScore(int score) {
        this.score = score;
        return this;
    }

    public RemoteFurImageE926Builder setRating(Rating rating) {
        this.rating = rating;
        return this;
    }

    public RemoteFurImageE926Builder setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
        return this;
    }

    public RemoteFurImageE926Builder setFileExt(String fileExt) {
        this.fileExt = fileExt;
        return this;
    }

    public RemoteFurImageE926Builder setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
        return this;
    }

    public RemoteFurImageE926Builder setIdE926(int idE926) {
        this.idE926 = idE926;
        return this;
    }

    public RemoteFurImageE926Builder setAuthor(String author) {
        this.author = author;
        return this;
    }

    public RemoteFurImageE926Builder setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public RemoteFurImageE926Builder setSources(List<String> sources) {
        this.sources = sources;
        return this;
    }

    public RemoteFurImageE926Builder setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public RemoteFurImageE926Builder setArtists(List<String> artists) {
        this.artists = artists;
        return this;
    }

    public RemoteFurImageE926Builder setMd5(BigInteger md5) {
        this.md5 = md5;
        return this;
    }

    public RemoteFurImageE926Builder setFileSize(int fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    public RemoteFurImageE926Builder setFileWidth(int fileWidth) {
        this.fileWidth = fileWidth;
        return this;
    }

    public RemoteFurImageE926Builder setFileHeight(int fileHeight) {
        this.fileHeight = fileHeight;
        return this;
    }

    public RemoteFurImageE926Builder setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public RemoteFurImageE926 createRemoteFurImageE926() {
        return new RemoteFurImageE926(searchQuery, description, score, rating, fileUrl, fileExt, pageUrl, idE926, author, createdAt, sources, tags, artists, md5, fileSize, fileWidth, fileHeight, filePath);
    }
}