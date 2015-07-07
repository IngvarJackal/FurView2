package ru.furry.furview2.system;

public abstract class DefaultCreator<T> {
    abstract public T getDefaultValue(Object... params);
}
