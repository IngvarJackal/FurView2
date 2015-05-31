package ru.furry.furview2.images;

import org.joda.time.DateTime;

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

    public FurImageBuilder makeFromRemoteFurImage(RemoteFurImage image) {
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

    public FurImage createFurImage() {
        return new FurImage(searchQuery, description, score, rating, fileUrl, fileExt, pageUrl, author, createdAt, sources, tags, artists);
    }
}