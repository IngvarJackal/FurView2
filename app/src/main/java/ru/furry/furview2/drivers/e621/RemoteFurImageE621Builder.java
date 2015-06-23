package ru.furry.furview2.drivers.e621;

import org.joda.time.DateTime;

import java.math.BigInteger;
import java.util.List;

import ru.furry.furview2.images.Rating;

public class RemoteFurImageE621Builder {
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

    public RemoteFurImageE621Builder setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
        return this;
    }

    public RemoteFurImageE621Builder setDescription(String description) {
        this.description = description;
        return this;
    }

    public RemoteFurImageE621Builder setScore(int score) {
        this.score = score;
        return this;
    }

    public RemoteFurImageE621Builder setRating(Rating rating) {
        this.rating = rating;
        return this;
    }

    public RemoteFurImageE621Builder setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
        return this;
    }

    public RemoteFurImageE621Builder setFileExt(String fileExt) {
        this.fileExt = fileExt;
        return this;
    }

    public RemoteFurImageE621Builder setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
        return this;
    }

    public RemoteFurImageE621Builder setIdE926(int idE926) {
        this.idE926 = idE926;
        return this;
    }

    public RemoteFurImageE621Builder setAuthor(String author) {
        this.author = author;
        return this;
    }

    public RemoteFurImageE621Builder setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public RemoteFurImageE621Builder setSources(List<String> sources) {
        this.sources = sources;
        return this;
    }

    public RemoteFurImageE621Builder setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public RemoteFurImageE621Builder setArtists(List<String> artists) {
        this.artists = artists;
        return this;
    }

    public RemoteFurImageE621Builder setMd5(BigInteger md5) {
        this.md5 = md5;
        return this;
    }

    public RemoteFurImageE621Builder setFileSize(int fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    public RemoteFurImageE621Builder setFileWidth(int fileWidth) {
        this.fileWidth = fileWidth;
        return this;
    }

    public RemoteFurImageE621Builder setFileHeight(int fileHeight) {
        this.fileHeight = fileHeight;
        return this;
    }

    public RemoteFurImageE621Builder setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public RemoteFurImageE621 createRemoteFurImageE926() {
        return new RemoteFurImageE621(searchQuery, description, score, rating, fileUrl, fileExt, pageUrl, idE926, author, createdAt, sources, tags, artists, md5, fileSize, fileWidth, fileHeight, filePath);
    }
}