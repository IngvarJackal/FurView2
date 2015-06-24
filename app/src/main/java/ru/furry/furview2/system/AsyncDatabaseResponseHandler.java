package ru.furry.furview2.system;

import java.util.List;

import ru.furry.furview2.images.FurImage;

public interface AsyncDatabaseResponseHandler {
    void processDBResponse(List<FurImage> images);
}
