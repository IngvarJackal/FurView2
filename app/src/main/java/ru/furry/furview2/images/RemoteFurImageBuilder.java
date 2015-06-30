package ru.furry.furview2.images;

public class RemoteFurImageBuilder {
    private String searchQuery;
    private String description;
    private Rating rating;
    private String fileUrl;
    private String fileExt;
    private String pageUrl;
    private String previewUrl;

    public RemoteFurImageBuilder setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
        return this;
    }

    public RemoteFurImageBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public RemoteFurImageBuilder setRating(Rating rating) {
        this.rating = rating;
        return this;
    }

    public RemoteFurImageBuilder setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
        return this;
    }

    public RemoteFurImageBuilder setFileExt(String fileExt) {
        this.fileExt = fileExt;
        return this;
    }

    public RemoteFurImageBuilder setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
        return this;
    }

    public RemoteFurImage createRemoteFurImage() {
        return new RemoteFurImage(searchQuery, description, rating, fileUrl, fileExt, pageUrl, previewUrl);
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }
}