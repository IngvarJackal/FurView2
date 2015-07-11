package ru.furry.furview2.system;

import java.util.List;

public interface AsyncHandlerUI<T> {
    void blockUI();
    void unblockUI();
    void retrieve(List<? extends T> images);
}
