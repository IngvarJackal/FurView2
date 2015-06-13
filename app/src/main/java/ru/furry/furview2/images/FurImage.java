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
    private String previewPath;

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

    public int getId() {
        return id;
    }

    public List<String> getLocalTags() {
        return localTags;
    }

    public int getLocalScore() {
        return localScore;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getPreviewPath() {
        return previewPath;
    }

    public int getScore() {
        return score;
    }
}
