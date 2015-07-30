package ru.furry.furview2.drivers;

import ru.furry.furview2.R;

public enum Drivertype {
    IMAGEBOARD(R.string.imageboard_name),
    GALLERY(R.string.gallery_name);

    public int nameId;

    Drivertype(int nameId) {
        this.nameId = nameId;
    }
}
