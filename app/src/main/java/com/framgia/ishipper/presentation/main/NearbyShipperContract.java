package com.framgia.ishipper.presentation.main;

import android.content.Intent;

import com.framgia.ishipper.model.User;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by HungNT on 11/28/16.
 */

public class NearbyShipperContract {
    interface View {

        void onSearchAreaComplete(CharSequence name, LatLng latLng);

        void onGetShipperNearbyComplete(List<User> users);

        void onAddressChange(String string);
    }

    interface Presenter {
        void getPlace(Intent place);

        void getShipperInfo(User shipper);

        void startSearchPlace();

        void requestShipperNearby(LatLng latLng);

        void getAddressFromLatLng(LatLng latLng);
    }
}
