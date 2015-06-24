package ru.furry.furview2.system;

import java.util.List;

import ru.furry.furview2.images.FurImage;

public interface AsyncDatabaseResponseHandlerGUI {
    void blockInterfaceForDBResponse();
    void unblockInterfaceForDBResponse();
    void retrieveDBResponse(List<FurImage> images);
}
