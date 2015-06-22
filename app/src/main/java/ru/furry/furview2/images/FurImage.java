package ru.furry.furview2.images;

import org.joda.time.DateTime;

import java.math.BigInteger;
import java.util.List;

/**
 * Basic class for downloaded images
 */
public class FurImage extends RemoteFurImage {
    private String author;
    private DateTime createdAt;
    private List<String> sources;
    private List<String> tags;
    private List<String> artists;

    private DateTime downloadedAt;
    private int score;
    private BigInteger md5;
    private String fileName;
    private int fileSize;
    private int fileWidth;
    private int fileHeight;
    private int previewWidth;
    private int previewHeight;
    private String rootPath;

    private int id;
    private List<String> localTags;
    private int localScore;

    private String filePath;

    public FurImage(String searchQuery, String description, int score, Rating rating, String fileUrl, String fileExt, String pageUrl, String author, DateTime createdAt, List<String> sources, List<String> tags, List<String> artists, DateTime downloadedAt, BigInteger md5, String fileName, int fileSize, int fileWidth, int fileHeight, int previewWidth, int previewHeight, String rootPath) {
        super(searchQuery, description, rating, fileUrl, fileExt, pageUrl);
        this.author = author;
        this.createdAt = createdAt;
        this.score = score;
        this.sources = sources;
        this.tags = tags;
        this.artists = artists;
        this.downloadedAt = downloadedAt;
        this.md5 = md5;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileWidth = fileWidth;
        this.fileHeight = fileHeight;
        this.previewWidth = previewWidth;
        this.previewHeight = previewHeight;
        this.rootPath = rootPath;
    }

    @Override
    public String toString() {
        return super.toString() + (new StringBuilder().append("\ngetAuthor ")
                .append(this.getAuthor()).append("\n getCreatedAt ")
                .append(this.getCreatedAt()).append("\ngetDownloadedAt ")
                .append(this.getDownloadedAt()).append("\ngetArtists ")
                .append(this.getArtists()).append("\ngetSources ")
                .append(this.getSources()).append("\ngetScore ")
                .append(this.getScore()).append("\ngetRating ")
                .append(this.getRating()).append("\ngetTags ")
                .append(this.getTags()).append("\ngetFileName ")
                .append(this.getFileName()).append("\ngetFileHeight ")
                .append(this.getFileHeight()).append("\ngetFileWidth ")
                .append(this.getFileWidth()).append("\ngetFileSize ")
                .append(this.getFileSize()).append("\ngetMd5 ")
                .append(this.getMd5().toString(36)).append("\ngetFilePath ")
                .append(this.getFilePath()).append("\ngetID ")
                .append(this.getID()).append("\ngetLocalScore ")
                .append(this.getLocalScore()).append("\ngetLocalTags ")
                .append(this.getLocalTags()).append("\n")
                .toString());
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

    public DateTime getDownloadedAt() {
        return downloadedAt;
    }

    public BigInteger getMd5() {
        return md5;
    }

    public String getFileName() {
        return fileName;
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

    public int getPreviewWidth() {
        return previewWidth;
    }

    public int getPreviewHeight() {
        return previewHeight;
    }

    public String getRootPath() {
        return rootPath;
    }

    public List<String> getLocalTags() {
        return localTags;
    }

    public FurImage setLocalTags(List<String> localTags) {
        this.localTags = localTags;
        return this;
    }

    public int getScore() {
        return score;
    }

    public int getLocalScore() {
        return localScore;
    }

    public FurImage setLocalScore(int localScore) {
        this.localScore = localScore;
        return this;
    }

    public String getFilePath() {
        return filePath;
    }

    public FurImage setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public int getID() {
        return id;
    }

    public FurImage setID(int id) {
        this.id = id;
        return this;
    }

}
