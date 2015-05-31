package ru.furry.furview2.images;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Basic class for not downloaded images
 */
public class FurImage extends RemoteFurImage {
    String author;
    DateTime createdAt;
    List<String> sources;
    List<String> tags;
    List<String> artists;

    public FurImage(
            String searchQuery,
            String description,
            int score, Rating rating,
            String fileUrl,
            String fileExt,
            String pageUrl,
            String author,
            DateTime createdAt,
            List<String> sources,
            List<String> tags,
            List<String> artists) {
        super(searchQuery, description, score, rating, fileUrl, fileExt, pageUrl);
        this.author = author;
        this.createdAt = createdAt;
        this.sources = sources;
        this.tags = tags;
        this.artists = artists;

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
}
