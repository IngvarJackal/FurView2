package ru.furry.furview2.images;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

import java.net.URL;
import java.util.GregorianCalendar;
import java.util.List;

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
            String file_url,
            String file_ext,
            String page_url,
            String author,
            DateTime createdAt,
            List<String> sources,
            List<String> tags,
            List<String> artists) {
        super(searchQuery, description, score, rating, file_url, file_ext, page_url);
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
