package com.candykick.startup.engine;

public interface CdkResponseCallback<T> {

    void onDataLoaded(String result, T item);

    void onDataNotAvailable(String error);

    void onDataEmpty();
}
