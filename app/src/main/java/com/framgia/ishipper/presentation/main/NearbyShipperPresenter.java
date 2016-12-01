package com.framgia.ishipper.presentation.main;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.framgia.ishipper.R;
import com.framgia.ishipper.base.BaseFragment;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.GetUserData;
import com.framgia.ishipper.net.data.ShipperNearbyData;
import com.framgia.ishipper.util.Const;
import com.framgia.ishipper.util.MapUtils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HungNT on 11/28/16.
 */

public class NearbyShipperPresenter implements NearbyShipperContract.Presenter {
    private BaseFragment mFragment;
    private NearbyShipperContract.View mView;
    private Context mContext;
    private FetchAddressTask task;

    public NearbyShipperPresenter(BaseFragment fragment, NearbyShipperContract.View view) {
        mFragment = fragment;
        mView = view;
        mContext = fragment.getContext();
    }

    @Override
    public void getPlace(Intent place) {
        Place data = PlaceAutocomplete.getPlace(mFragment.getContext(), place);
        mView.onSearchAreaComplete(data.getName(), data.getLatLng());
    }

    @Override
    public void getShipperInfo(User shipper) {
        /** get shop information */
        API.getUser(
                Config.getInstance().getUserInfo(mContext).getAuthenticationToken(),
                String.valueOf(shipper.getId()),
                new API.APICallback<APIResponse<GetUserData>>() {
                    @Override
                    public void onResponse(APIResponse<GetUserData> response) {
                        User shipper = response.getData().getUser();
                        mFragment.showUserMessage(shipper.getName());
                        // TODO: 30/08/2016
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        mFragment.showUserMessage(message);
                    }
                });
    }

    @Override
    public void startSearchPlace() {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                    .build();

            Intent searchIntent = new PlaceAutocomplete
                    .IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setFilter(typeFilter)
                    .build(mFragment.getActivity());
            mFragment.startActivityForResult(searchIntent, Const.PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * mark all shippers near a location on map
     */
    @Override
    public void requestShipperNearby(LatLng latLng) {
        Map<String, String> userParams = new HashMap<>();
        userParams.put(APIDefinition.GetShipperNearby.PARAM_USER_LAT, String.valueOf(latLng.latitude));
        userParams.put(APIDefinition.GetShipperNearby.PARAM_USER_LNG, String.valueOf(latLng.longitude));
        userParams.put(APIDefinition.GetShipperNearby.PARAM_USER_DISTANCE,
                String.valueOf(Const.SHIPPER_NEARBY_RADIUS));
        API.getShipperNearby(Config.getInstance().getUserInfo(mContext).getAuthenticationToken(),
                userParams, new API.APICallback<APIResponse<ShipperNearbyData>>() {
                    @Override
                    public void onResponse(APIResponse<ShipperNearbyData> response) {
                        mView.onGetShipperNearbyComplete(response.getData().getUsers());
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        mFragment.showUserMessage(message);
                    }
                });
    }

    @Override
    public void getAddressFromLatLng(LatLng latLng) {
        if (task != null) task.cancel(true);
        task = new FetchAddressTask();
        task.execute(latLng);
    }

    /**
     * Task fetch address from location in another thread
     */
    private class FetchAddressTask extends AsyncTask<LatLng, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mView.onAddressChange(mContext.getString(R.string.all_symbol_loading));
        }

        @Override
        protected String doInBackground(LatLng... latLngs) {
            double latitude = latLngs[0].latitude;
            double longitude = latLngs[0].longitude;
            requestShipperNearby(new LatLng(latitude, longitude));
            return MapUtils.getAddressFromLocation(mContext, new LatLng(latitude, longitude));
        }

        @Override
        protected void onPostExecute(String address) {
            super.onPostExecute(address);
            mView.onAddressChange(address);
        }
    }
}

