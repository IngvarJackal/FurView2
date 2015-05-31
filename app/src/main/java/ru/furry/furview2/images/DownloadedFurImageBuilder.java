package ru.furry.furview2.images;

import org.joda.time.DateTime;

import java.math.BigInteger;
import java.util.List;

public class DownloadedFurImageBuilder {
    private String searchQuery;
    private String description;
    private int score;
    private Rating rating;
    private String fileUrl;
    private String fileExt;
    private String pageUrl;
    private String author;
    private DateTime createdAt;
    private List<String> sources;
    private List<String> tags;
    private List<String> artists;
    private DateTime downloadedAt;
    private BigInteger md5;
    private int fileSize;
    private String fileName;
    private String root;
    private int fileWidth;
    private int fileHeight;
    private int previewWidth;
    private int previewHeight;

    public DownloadedFurImageBuilder setRoot(String root) {
        this.root = root;
        return this;
    }

    public DownloadedFurImageBuilder setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
        return this;
    }

    public DownloadedFurImageBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public DownloadedFurImageBuilder setScore(int score) {
        this.score = score;
        return this;
    }

    public DownloadedFurImageBuilder setRating(Rating rating) {
        this.rating = rating;
        return this;
    }

    public DownloadedFurImageBuilder setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
        return this;
    }

    public DownloadedFurImageBuilder setFileExt(String fileExt) {
        this.fileExt = fileExt;
        return this;
    }

    public DownloadedFurImageBuilder setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
        return this;
    }

    public DownloadedFurImageBuilder setAuthor(String author) {
        this.author = author;
        return this;
    }

    public DownloadedFurImageBuilder setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public DownloadedFurImageBuilder setSources(List<String> sources) {
        this.sources = sources;
        return this;
    }

    public DownloadedFurImageBuilder setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public DownloadedFurImageBuilder setArtists(List<String> artists) {
        this.artists = artists;
        return this;
    }

    public DownloadedFurImageBuilder setDownloadedAt(DateTime downloadedAt) {
        this.downloadedAt = downloadedAt;
        return this;
    }

    public DownloadedFurImageBuilder setMd5(BigInteger md5) {
        this.md5 = md5;
        return this;
    }

    public DownloadedFurImageBuilder setFileSize(int fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    public DownloadedFurImageBuilder setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public DownloadedFurImageBuilder setFileWidth(int fileWidth) {
        this.fileWidth = fileWidth;
        return this;
    }

    public DownloadedFurImageBuilder setFileHeight(int fileHeight) {
        this.fileHeight = fileHeight;
        return this;
    }

    public DownloadedFurImageBuilder setPreviewWidth(int previewWidth) {
        this.previewWidth = previewWidth;
        return this;
    }

    public DownloadedFurImageBuilder setPreviewHeight(int previewHeight) {
        this.previewHeight = previewHeight;
        return this;
    }

    public DownloadedFurImageBuilder makeFromRemoteFurImage(RemoteFurImage image) {
        return (this
                .setSearchQuery(image.getSearchQuery())
                .setDescription(image.getDescription())
                .setScore(image.getScore())
                .setRating(image.getRating())
                .setFileUrl(image.getFileUrl())
                .setFileExt(image.getFileExt())
                .setPageUrl(image.getPageUrl())
        );
    }

    public DownloadedFurImageBuilder makeFromFurImage(FurImage image) {
        return (this
                .makeFromRemoteFurImage(image)
                .setAuthor(image.getAuthor())
                .setCreatedAt(image.getCreatedAt())
                .setSources(image.getSources())
                .setTags(image.getTags())
                .setArtists(image.getArtists())
        );
    }

    public DownloadedFurImage createDownloadedFurImage() {
        return new DownloadedFurImage(searchQuery, description, score, rating, fileUrl, fileExt, pageUrl, author, createdAt, sources, tags, artists, downloadedAt, md5, root, fileName, fileSize, fileWidth, fileHeight, previewWidth, previewHeight);
    }
}