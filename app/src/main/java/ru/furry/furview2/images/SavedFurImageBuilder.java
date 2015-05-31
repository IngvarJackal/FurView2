package ru.furry.furview2.images;

import org.joda.time.DateTime;

import java.math.BigInteger;
import java.util.List;

public class SavedFurImageBuilder {
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
    private String fileName;
    private int fileSize;
    private int fileWidth;
    private int fileHeight;
    private int previewWidth;
    private int previewHeight;
    private int id;
    private List<String> localTags;
    private int localScore;
    private String rootPath;

    public SavedFurImageBuilder setRootPath(String rootPath) {
        this.rootPath = rootPath;
        return this;
    }

    public SavedFurImageBuilder setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
        return this;
    }

    public SavedFurImageBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public SavedFurImageBuilder setScore(int score) {
        this.score = score;
        return this;
    }

    public SavedFurImageBuilder setRating(Rating rating) {
        this.rating = rating;
        return this;
    }

    public SavedFurImageBuilder setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
        return this;
    }

    public SavedFurImageBuilder setFileExt(String fileExt) {
        this.fileExt = fileExt;
        return this;
    }

    public SavedFurImageBuilder setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
        return this;
    }

    public SavedFurImageBuilder setAuthor(String author) {
        this.author = author;
        return this;
    }

    public SavedFurImageBuilder setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public SavedFurImageBuilder setSources(List<String> sources) {
        this.sources = sources;
        return this;
    }

    public SavedFurImageBuilder setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public SavedFurImageBuilder setArtists(List<String> artists) {
        this.artists = artists;
        return this;
    }

    public SavedFurImageBuilder setDownloadedAt(DateTime downloadedAt) {
        this.downloadedAt = downloadedAt;
        return this;
    }

    public SavedFurImageBuilder setMd5(BigInteger md5) {
        this.md5 = md5;
        return this;
    }

    public SavedFurImageBuilder setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public SavedFurImageBuilder setFileSize(int fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    public SavedFurImageBuilder setFileWidth(int fileWidth) {
        this.fileWidth = fileWidth;
        return this;
    }

    public SavedFurImageBuilder setFileHeight(int fileHeight) {
        this.fileHeight = fileHeight;
        return this;
    }


    public SavedFurImageBuilder setPreviewWidth(int previewWidth) {
        this.previewWidth = previewWidth;
        return this;
    }

    public SavedFurImageBuilder setPreviewHeight(int previewHeight) {
        this.previewHeight = previewHeight;
        return this;
    }

    public SavedFurImageBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public SavedFurImageBuilder setLocalTags(List<String> localTags) {
        this.localTags = localTags;
        return this;
    }

    public SavedFurImageBuilder setLocalScore(int localScore) {
        this.localScore = localScore;
        return this;
    }

    public SavedFurImageBuilder makeFromRemoteFurImage(RemoteFurImage image) {
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

    public SavedFurImageBuilder makeFromFurImage(FurImage image) {
        return (this
                .makeFromRemoteFurImage(image)
                .setAuthor(image.getAuthor())
                .setCreatedAt(image.getCreatedAt())
                .setSources(image.getSources())
                .setTags(image.getTags())
                .setArtists(image.getArtists())
        );
    }

    public SavedFurImageBuilder makeFromDownloadedFurImage(DownloadedFurImage image) {
        return (this
                .makeFromFurImage(image)
                .setMd5(image.getMd5())
                .setFileName(image.getFileName())
                .setFileSize(image.getFileSize())
                .setFileWidth(image.getFileWidth())
                .setFileHeight(image.getFileHeight())
                .setDownloadedAt(image.getDownloadedAt())
                .setRootPath(image.getRootPath())
        );
    }

    public SavedFurImage createSavedFurImage() {
        return new SavedFurImage(searchQuery, description, score, rating, fileUrl, fileExt, pageUrl, author, createdAt, sources, tags, artists, downloadedAt, md5, rootPath, fileName, fileSize, fileWidth, fileHeight, previewWidth, previewHeight, id, localScore, localTags);
    }
}