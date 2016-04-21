package com.example.cmawms0.cafelitoapp.service;

/**
 * Created by Wayne.Segar on 4/21/2016.
 */
public interface CoffeeOrderServiceCallback {
    void orderSuccess();

    void orderFailure(Exception exception);

}
