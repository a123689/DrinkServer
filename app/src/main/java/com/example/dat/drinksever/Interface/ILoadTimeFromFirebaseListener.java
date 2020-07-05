package com.example.dat.drinksever.Interface;

public interface ILoadTimeFromFirebaseListener {
    void onLoadOnlyTimeTimeSuccess(long time);
    void onLoadTimeFailed(String message);
}
