package com.example.cmawms0.cafelitoapp.service;

import android.location.Location;

import com.example.cmawms0.cafelitoapp.data.CoffeeShop;

/**
 * Created by Wayne.Segar on 4/12/2016.
 */
public interface CoffeeShopServiceCallback {
    void serviceSuccess(CoffeeShop coffeeShop);

    void serviceFailure(Exception exception);
}
