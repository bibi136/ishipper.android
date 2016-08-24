package com.framgia.ishipper.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by HungNT on 7/18/16.
 */
public class MapUtils {
    private static final int ZOOM_LEVEL = 15;
    public static void routing(LatLng start, LatLng end, RoutingListener listener) {
        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.WALKING)
                .withListener(listener)
                .waypoints(start, end)
                .build();
        routing.execute();
    }

    public static void updateZoomMap(GoogleMap googleMap, int width, int height, LatLng... points) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng item : points) {
            builder.include(item);
        }
        LatLngBounds bounds = builder.build();

        int padding = (int) (width * 0.12);

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        googleMap.animateCamera(cu);
    }

    public static void zoomToPosition(GoogleMap map, LatLng latLng) {
        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL);
        map.animateCamera(center);
    }

    public static String getAddressFromLocation(Context context, LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size()>0) {
                return addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getAddressLine(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static LatLng getLocationFromAddress(Context context, String address) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(address, 1);
            if (addresses.size()>0) {
                return new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
