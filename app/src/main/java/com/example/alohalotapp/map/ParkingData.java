package com.example.alohalotapp.map;

import android.util.Pair;

import com.example.alohalotapp.admin.FirebaseAdminHelperClass;

import java.util.ArrayList;
import java.util.function.Consumer;

public class ParkingData {
    FirebaseAdminHelperClass firebaseHelper;

    public ParkingData(){
        firebaseHelper = new FirebaseAdminHelperClass();
    }

    public void getCoordinates(Consumer<ArrayList<String>> onLoaded, Consumer<String> onError){
        firebaseHelper.loadCoordinates(onLoaded, onError);
    }

    public void getCapacities(Consumer<ArrayList<Integer>> onLoaded, Consumer<String> onError){
        firebaseHelper.loadCapacities(onLoaded, onError);
    }

    public void getCurrentUsers(Consumer<ArrayList<Integer>> onLoaded, Consumer<String> onError){
        firebaseHelper.loadCurrentUsers(onLoaded, onError);
    }

    public void getIsHandicapped(Consumer<ArrayList<Boolean>> onLoaded, Consumer<String> onError){
        firebaseHelper.loadIsHandicapped(onLoaded, onError);
    }

    public void getOpeningHours(Consumer<ArrayList<Pair<String, String>>> onLoaded, Consumer<String> onError){
        firebaseHelper.loadOpeningHours(onLoaded, onError);
    }
}
