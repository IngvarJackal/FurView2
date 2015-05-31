package ru.furry.furview2.drivers.e926;

import ru.furry.furview2.images.Rating;

public class RemoteFurImageE926Builder {
    private String searchQuery;
    private String description;
    private int score;
    private Rating rating;
    private String file_url;
    private String file_ext;
    private String page_url;
    private int idE926;

    public RemoteFurImageE926Builder setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
        return this;
    }

    public RemoteFurImageE926Builder setDescription(String description) {
        this.description = description;
        return this;
    }

    public RemoteFurImageE926Builder setScore(int score) {
        this.score = score;
        return this;
    }

    public RemoteFurImageE926Builder setRating(Rating rating) {
        this.rating = rating;
        return this;
    }

    public RemoteFurImageE926Builder setFile_url(String file_url) {
        this.file_url = file_url;
        return this;
    }

    public RemoteFurImageE926Builder setFile_ext(String file_ext) {
        this.file_ext = file_ext;
        return this;
    }

    public RemoteFurImageE926Builder setPage_url(String page_url) {
        this.page_url = page_url;
        return this;
    }

    public RemoteFurImageE926Builder setIdE926(int idE926) {
        this.idE926 = idE926;
        return this;
    }

    public RemoteFurImageE926 createRemoteFurImageE926() {
        return new RemoteFurImageE926(searchQuery, description, score, rating, file_url, file_ext, page_url, idE926);
    }
}