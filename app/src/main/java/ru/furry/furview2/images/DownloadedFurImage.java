package ru.furry.furview2.images;

import org.joda.time.DateTime;

import java.math.BigInteger;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.List;

public class DownloadedFurImage extends FurImage {
    int id;
    DateTime downloadedAt;
    List<String> localTags;
    int localScore;
    BigInteger md5;
    String filePath;
    int fileSize;
    int fileWidth;
    int fileHeight;
    String previewPath;
    int previewWidth;
    int previewHeight;

    public DownloadedFurImage(
            String searchQuery,
            String description,
            int score,
            Rating rating,
            String file_url,
            String file_ext,
            String page_url,
            String author,
            DateTime createdAt,
            List<String> sources,
            List<String> tags,
            List<String> artists,
            DateTime downloadedAt,
            List<String> localTags,
            int localScore,
            BigInteger md5,
            String filePath,
            int fileSize,
            int fileWidth,
            int fileHeight,
            String previewPath,
            int previewWidth,
            int previewHeight,
            int id) {
        super(searchQuery, description, score, rating, file_url, file_ext, page_url, author, createdAt, sources, tags, artists);
        this.id = id;
        this.downloadedAt = downloadedAt;
        this.localTags = localTags;
        this.localScore = localScore;
        this.md5 = md5;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.fileWidth = fileWidth;
        this.fileHeight = fileHeight;
        this.previewPath = previewPath;
        this.previewWidth = previewWidth;
        this.previewHeight = previewHeight;
    }

    public int getId() {
        return id;
    }

    public DateTime getDownloadedAt() {
        return downloadedAt;
    }

    public List<String> getLocalTags() {
        return localTags;
    }

    public int getLocalScore() {
        return localScore;
    }

    public BigInteger getMd5() {
        return md5;
    }

    public String getFilePath() {
        return filePath;
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

    public String getPreviewPath() {
        return previewPath;
    }

    public int getPreviewWidth() {
        return previewWidth;
    }

    public int getPreviewHeight() {
        return previewHeight;
    }
}
