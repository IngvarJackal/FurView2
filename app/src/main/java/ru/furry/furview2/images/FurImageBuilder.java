package ru.furry.furview2.images;

import org.joda.time.DateTime;

import java.math.BigInteger;
import java.util.List;

public class FurImageBuilder {
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
    @Deprecated
    private int previewWidth;
    @Deprecated
    private int previewHeight;
    @Deprecated
    private String rootPath;
    private String previewUrl;
    private int localScore;
    private String filePath;
    private List<String> localTags;

    public FurImageBuilder setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
        return this;
    }

    public FurImageBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public FurImageBuilder setScore(int score) {
        this.score = score;
        return this;
    }

    public FurImageBuilder setRating(Rating rating) {
        this.rating = rating;
        return this;
    }

    public FurImageBuilder setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
        return this;
    }

    public FurImageBuilder setFileExt(String fileExt) {
        this.fileExt = fileExt;
        return this;
    }

    public FurImageBuilder setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
        return this;
    }

    public FurImageBuilder setAuthor(String author) {
        this.author = author;
        return this;
    }

    public FurImageBuilder setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public FurImageBuilder setSources(List<String> sources) {
        this.sources = sources;
        return this;
    }

    public FurImageBuilder setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public FurImageBuilder setArtists(List<String> artists) {
        this.artists = artists;
        return this;
    }

    public FurImageBuilder setDownloadedAt(DateTime downloadedAt) {
        this.downloadedAt = downloadedAt;
        return this;
    }

    public FurImageBuilder setMd5(BigInteger md5) {
        this.md5 = md5;
        return this;
    }

    public FurImageBuilder setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public FurImageBuilder setFileSize(int fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    public FurImageBuilder setFileWidth(int fileWidth) {
        this.fileWidth = fileWidth;
        return this;
    }

    public FurImageBuilder setFileHeight(int fileHeight) {
        this.fileHeight = fileHeight;
        return this;
    }

    @Deprecated
    public FurImageBuilder setPreviewWidth(int previewWidth) {
        this.previewWidth = previewWidth;
        return this;
    }

    @Deprecated
    public FurImageBuilder setPreviewHeight(int previewHeight) {
        this.previewHeight = previewHeight;
        return this;
    }

    @Deprecated
    public FurImageBuilder setRootPath(String rootPath) {
        this.rootPath = rootPath;
        return this;
    }

    public FurImageBuilder setLocalScore(int score) {
        this.localScore = score;
        return this;
    }

    public FurImageBuilder setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public FurImageBuilder setLocalTags(List<String> localTags) {
        this.localTags = localTags;
        return this;
    }

    public FurImageBuilder setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
        return this;
    }

    public FurImageBuilder makeFromRemoteFurImage(RemoteFurImage remoteImage) {
        this.searchQuery = remoteImage.getSearchQuery();
        this.description = remoteImage.getDescription();
        this.rating = remoteImage.getRating();
        this.fileUrl = remoteImage.getFileUrl();
        this.fileExt = remoteImage.getFileExt();
        this.pageUrl = remoteImage.getPageUrl();
        this.previewUrl = remoteImage.getPreviewUrl();
        this.filePath = remoteImage.getFileUrl();
        return this;
    }

    public FurImage createFurImage() {
        FurImage i = new FurImage(searchQuery, description, score, rating, fileUrl, fileExt, pageUrl, author, createdAt, sources, tags, artists, downloadedAt, md5, fileName, fileSize, fileWidth, fileHeight, previewWidth, previewHeight, rootPath, previewUrl);
        i.setFilePath(filePath);
        i.setLocalTags(localTags);
        i.setLocalScore(localScore);
        return i;
    }
}