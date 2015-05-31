package ru.furry.furview2.images;

import org.joda.time.DateTime;

import java.math.BigInteger;
import java.util.List;

/**
 * Basic class for downloaded and persistently saved into file system and database image
 */
public class SavedFurImage extends DownloadedFurImage {

    int id;
    List<String> localTags;
    int localScore;

    public SavedFurImage(String searchQuery, String description, int score, Rating rating, String fileUrl, String fileExt, String pageUrl, String author, DateTime createdAt, List<String> sources, List<String> tags, List<String> artists, DateTime downloadedAt, BigInteger md5, String rootPath, String filePath, int fileSize, int fileWidth, int fileHeight, int previewWidth, int previewHeight, int id, int localScore, List<String> localTags) {
        super(searchQuery, description, score, rating, fileUrl, fileExt, pageUrl, author, createdAt, sources, tags, artists, downloadedAt, md5, rootPath, filePath, fileSize, fileWidth, fileHeight, previewWidth, previewHeight);
        this.id = id;
        this.localTags = localTags;
        this.localScore = localScore;
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
}
