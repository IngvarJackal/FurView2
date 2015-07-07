package ru.furry.furview2.system;

import java.util.ArrayList;
import java.util.List;

public abstract class ExtendableWDef<T> {
    public List<T> entries;
    public DefaultCreator<T> creator;

    public ExtendableWDef(DefaultCreator<T> creator) {
        entries = new ArrayList();
        this.creator = creator;
    }

    public ExtendableWDef(int initialCapacity, DefaultCreator<T> creator) {
        entries = new ArrayList(initialCapacity);
        ensureCapacity(initialCapacity);
        this.creator = creator;
    }

    public abstract void ensureCapacity(int index);

    public T get(int index) {
        ensureCapacity(index);
        return entries.get(index);
    }

    public int getCapacity() {
        return entries.size();
    }

}
