package ru.furry.furview2.drivers.e926;

import ru.furry.furview2.images.Rating;
import ru.furry.furview2.images.RemoteFurImage;

public class RemoteFurImageE926 extends RemoteFurImage {

    public final int idE926;

    public RemoteFurImageE926(String searchQuery, String description, int score, Rating rating, String file_url, String file_ext, String page_url, int idE926) {
        super(searchQuery, description, score, rating, file_url, file_ext, page_url);
        this.idE926 = idE926;
    }
}