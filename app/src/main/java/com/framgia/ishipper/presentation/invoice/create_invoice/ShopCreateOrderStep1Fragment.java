package com.framgia.ishipper.presentation.invoice.create_invoice;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.framgia.ishipper.R;
import com.framgia.ishipper.base.BaseFragment;
import com.framgia.ishipper.ui.activity.ShopCreateOrderActivity;
import com.framgia.ishipper.util.MapUtils;
import com.framgia.ishipper.util.PermissionUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.seatgeek.placesautocomplete.DetailsCallback;
import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
import com.seatgeek.placesautocomplete.model.Place;
import com.seatgeek.placesautocomplete.model.PlaceDetails;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class ShopCreateOrderStep1Fragment extends BaseFragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ShopCreateOrderStep1Contract.View {
    private static final String TAG = "ShopCreateOrder1";
    private final int NONE = 1;
    private final int PICK_START_POINT = 2;
    private final int PICK_END_POINT = 3;

    @BindView(R.id.layoutMapContainer) FrameLayout mFrameMapContainer;
    @BindView(R.id.imgPickPosition) ImageView mImgPickPosition;
    @BindView(R.id.edt_address_start) PlacesAutocompleteTextView mEdtAddressStart;
    @BindView(R.id.edt_address_end) PlacesAutocompleteTextView mEdtAddressEnd;
    @BindView(R.id.btnPickStart) ImageView mBtnPickStart;
    @BindView(R.id.btnPickEnd) ImageView mBtnPickEnd;
    @BindView(R.id.tvDistance) TextView mTvDistance;

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private Marker mMarkerStart, mMarkerEnd;
    private int mStatus = NONE;
    private Polyline mPolylineRoute;
    private Context mContext;
    private FetchAddressTask task;
    private float mDistance;
    private boolean mDisableCameraChange;
    private ShopCreateOrderStep1Presenter mPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_shop_create_order_step1;
    }

    @Override
    public void initViews() {
        mPresenter = new ShopCreateOrderStep1Presenter(this, this);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(this);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.layoutMapContainer, mapFragment).commit();
        // Connect To Google Api
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mEdtAddressStart.setOnPlaceSelectedListener(new OnPlaceSelectedListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mEdtAddressStart.clearFocus();
                mEdtAddressStart.getDetailsFor(place, new DetailsCallback() {
                    @Override
                    public void onSuccess(PlaceDetails placeDetails) {
                        LatLng place = new LatLng(placeDetails.geometry.location.lat,
                                placeDetails.geometry.location.lng);
                        mPresenter.saveLatLngStart(place);
                        mDisableCameraChange = true;
                        MapUtils.zoomToPosition(mMap, place);
                        mPresenter.getDistance();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        // Dont update
                    }
                });
            }
        });

        mEdtAddressEnd.setOnPlaceSelectedListener(new OnPlaceSelectedListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mEdtAddressEnd.clearFocus();
                mEdtAddressEnd.getDetailsFor(place, new DetailsCallback() {
                    @Override
                    public void onSuccess(PlaceDetails placeDetails) {
                        LatLng place = new LatLng(placeDetails.geometry.location.lat,
                                placeDetails.geometry.location.lng);
                        mDisableCameraChange = true;
                        mPresenter.saveLatLngEnd(place);
                        MapUtils.zoomToPosition(mMap, place);
                        mPresenter.getDistance();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        // Dont update
                    }
                });
            }
        });
    }

    @OnFocusChange({R.id.edt_address_start, R.id.edt_address_end})
    public void onFocusChange(View view, boolean hasFocus) {
        switch (view.getId()) {
            case R.id.edt_address_start:
                if (hasFocus && mStatus != PICK_START_POINT) {
                    mPresenter.onSuggestStartClick(mMap.getCameraPosition().target);
                }
                break;
            case R.id.edt_address_end:
                if (hasFocus && mStatus != PICK_END_POINT) {
                    mPresenter.onSuggestEndClick(mMap.getCameraPosition().target);
                }
                break;
        }
    }

    @OnClick({R.id.btnPickStart, R.id.btnPickEnd, R.id.btnContinue})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPickStart:
                // user is picking start point, goto done pick
                if (mStatus == PICK_START_POINT) {
                    mPresenter.setStartLocation(mMap.getCameraPosition().target);
                    // if user haven't pick end point, go to pick end location
                    if (mMarkerEnd == null) {
                        pickEndLocation();
                    } else {
                        // all pick, confirm
                        mPresenter.confirmPickLocation();
                    }
                    // user is picking end point
                } else if (mStatus == PICK_END_POINT) {
                    mPresenter.setEndLocation(mMap.getCameraPosition().target);
                    mPresenter.confirmPickLocation();
                    // if user isn't picking start point or end point
                } else {
                    mImgPickPosition.setImageResource(R.drawable.ic_map_picker_start);
                    mPresenter.reset();
                    pickStartLocation();
                }

                break;
            case R.id.btnPickEnd:
                // if user is picking end point
                if (mStatus == PICK_END_POINT) {
                    mPresenter.setEndLocation(mMap.getCameraPosition().target);
                    mPresenter.confirmPickLocation();
                    // if user is picking start point
                } else if (mStatus == PICK_START_POINT) {
                    mPresenter.setStartLocation(mMap.getCameraPosition().target);
                    if (mMarkerEnd == null) {
                        pickEndLocation();
                    } else {
                        mPresenter.confirmPickLocation();
                    }
                    // if user isn't picking start point or end point
                } else {
                    pickEndLocation();
                }
                break;
            case R.id.btnContinue:
                mPresenter.confirmPickLocation();
                mPresenter.saveInvoiceData(mEdtAddressStart.getText().toString(),
                        mEdtAddressEnd.getText().toString(), mDistance);
                ((ShopCreateOrderActivity) getActivity()).addFragment(
                        new ShopCreateOrderStep2Fragment());
                break;
        }
    }

    @Override
    public void moveTo(Marker marker) {
        mDisableCameraChange = true;
        if (marker != null) {
            marker.remove();
            MapUtils.zoomToPosition(mMap, marker.getPosition());
            marker = null;
        }
    }

    @Override
    public void onDistanceResponse(float distance) {
        mDistance = distance;
        mTvDistance.setText(getString(R.string.text_distance, mDistance));
    }

    @Override
    public void showGetDistanceLoading() {
        mTvDistance.setText(R.string.all_symbol_loading);
    }

    @Override
    public void onRoutingSuccess(PolylineOptions polyOptions) {
        removePath();
        mPolylineRoute = mMap.addPolyline(polyOptions);
        MapUtils.zoomToBounds(mMap, polyOptions);
    }

    @Override
    public void updateDoneUI() {
        mStatus = NONE;
        mImgPickPosition.setImageDrawable(null);
    }

    @Override
    public void pickStartLocation() {
        mStatus = PICK_START_POINT;
        // remove start marker from map
        if (mMarkerStart != null) {
            moveTo(mMarkerStart);
            mMarkerStart.remove();
            mMarkerStart = null;
        }

        mImgPickPosition.setImageResource(R.drawable.ic_map_picker_start);
        mBtnPickStart.setImageResource(R.drawable.ic_done);
        removePath();

    }

    @Override
    public void pickEndLocation() {
        mStatus = PICK_END_POINT;
        if (mMarkerEnd != null) {
            moveTo(mMarkerEnd);
            mMarkerEnd.remove();
            mMarkerEnd = null;
        }
        mImgPickPosition.setImageResource(R.drawable.ic_map_picker_end);
        mBtnPickEnd.setImageResource(R.drawable.ic_done);
        removePath();
    }

    @Override
    public void clear() {
        // if user haven't pick end point then reset
        mMarkerEnd = null;
        mMap.clear();
        mTvDistance.setText("");
        mEdtAddressEnd.setText("");
        removePath();
    }

    @Override
    public void onEndLocationSave(LatLng endLocation) {
        mBtnPickEnd.setImageResource(R.drawable.ic_map_picker_end);
        if (mMarkerEnd != null) mMarkerEnd.remove();
        mMarkerEnd = mMap.addMarker(
                new MarkerOptions().position(endLocation)
                        .icon(BitmapDescriptorFactory.fromResource(
                                R.drawable.ic_map_picker_end))
        );
    }

    @Override
    public void onStartLocationSave(LatLng startLocation) {
        mBtnPickStart.setImageResource(R.drawable.ic_map_picker_start);
        mMarkerStart = mMap.addMarker(
                new MarkerOptions()
                        .position(startLocation)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_picker_start)));
    }

    private void removePath() {
        if (mPolylineRoute != null) {
            mPolylineRoute.remove();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Disable tilt and rotate map
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);

        if (PermissionUtils.checkLocationPermission(mContext)) return;

        googleMap.setMyLocationEnabled(true);
        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (mDisableCameraChange) {
                    mDisableCameraChange = false;
                    return;
                }
                if (mStatus == NONE) return;
                LatLng position = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
                if (mStatus == PICK_START_POINT) {
                    mPresenter.saveLatLngStart(position);
                } else if (mStatus == PICK_END_POINT) {
                    mPresenter.saveLatLngEnd(position);
                }

                if (task != null) {
                    task.cancel(true);
                }
                task = new FetchAddressTask();
                task.execute(position);
                mPresenter.getDistance();
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (PermissionUtils.checkLocationPermission(mContext)) return;
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            MapUtils.zoomToPosition(mMap, new LatLng(location.getLatitude(), location.getLongitude()));
            mPresenter.saveLatLngStart(new LatLng(location.getLatitude(), location.getLongitude()));
            pickStartLocation();
        } else {
            showUserMessage(R.string.all_cant_get_location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    /**
     * Task fetch address from location in another thread
     */
    private class FetchAddressTask extends AsyncTask<LatLng, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mStatus == PICK_START_POINT) {
                mEdtAddressStart.setText(R.string.all_symbol_loading);
            } else if (mStatus == PICK_END_POINT) {
                mEdtAddressEnd.setText(R.string.all_symbol_loading);
            }
        }

        @Override
        protected String doInBackground(LatLng... latLngs) {
            double latitude = latLngs[0].latitude;
            double longitude = latLngs[0].longitude;
            return MapUtils.getAddressFromLocation(mContext, new LatLng(latitude, longitude));
        }

        @Override
        protected void onPostExecute(String address) {
            super.onPostExecute(address);
            if (mStatus == NONE) return;
            PlacesAutocompleteTextView tvShowing;
            tvShowing = mStatus == PICK_START_POINT ? mEdtAddressStart : mEdtAddressEnd;
            // Disable dropdown showing up
            tvShowing.clearFocus();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                tvShowing.setText(address, false);
            } else {
                mEdtAddressStart.setFocusable(false);
                mEdtAddressStart.setFocusableInTouchMode(false);
                mEdtAddressStart.setText(address);
                mEdtAddressStart.setFocusable(true);
                mEdtAddressStart.setFocusableInTouchMode(true);
            }
        }
    }
}
