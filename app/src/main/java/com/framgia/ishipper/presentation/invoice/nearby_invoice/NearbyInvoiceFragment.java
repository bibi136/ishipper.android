package com.framgia.ishipper.presentation.invoice.nearby_invoice;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.directions.route.Route;
import com.framgia.ishipper.R;
import com.framgia.ishipper.base.BaseFragment;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.common.Log;
import com.framgia.ishipper.model.Invoice;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.presentation.filter.FilterInvoiceActivity;
import com.framgia.ishipper.ui.activity.MainActivity;
import com.framgia.ishipper.ui.activity.OrderDetailActivity;
import com.framgia.ishipper.ui.activity.RouteActivity;
import com.framgia.ishipper.ui.adapter.NewInvoiceAdapter;
import com.framgia.ishipper.ui.listener.LocationSettingCallback;
import com.framgia.ishipper.ui.listener.OnInvoiceUpdate;
import com.framgia.ishipper.util.CommonUtils;
import com.framgia.ishipper.util.Const;
import com.framgia.ishipper.util.MapUtils;
import com.framgia.ishipper.util.PermissionUtils;
import com.framgia.ishipper.util.StorageUtils;
import com.framgia.ishipper.util.TextFormatUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class NearbyInvoiceFragment extends BaseFragment implements
        NearbyInvoiceContract.View,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, OnInvoiceUpdate,NewInvoiceAdapter.OnItemClickListener {
    private static final String TAG = "NearbyOrderFragment";
    private static final int REQUEST_FILTER = 0x1234;

    @BindView(R.id.img_nearby_order_pos_marker) ImageView mImgPosMarker;
    @BindView(R.id.tv_item_order_ship_price) TextView mTvNearbyShipPrice;
    @BindView(R.id.tv_item_order_from) TextView mTvNearbyFrom;
    @BindView(R.id.tv_item_order_to) TextView mTvNearbyTo;
    @BindView(R.id.tv_item_order_distance) TextView mTvNearbyDistance;
    @BindView(R.id.tv_item_order_ship_time) TextView mTvNearbyShipTime;
    @BindView(R.id.tv_item_order_price) TextView mTvNearbyOrderPrice;
    @BindView(R.id.btn_item_order_show_path) TextView mBtnNearbyShowPath;
    @BindView(R.id.btn_item_order_register_order) TextView mBtnNearbyReceiveOrder;
    @BindView(R.id.window_invoice_detail) RelativeLayout mRlOrderDetail;
    @BindView(R.id.tv_main_search_area) TextView mTvSearchArea;
    @BindView(R.id.rating_order_window) AppCompatRatingBar mRatingOrderWindow;
    @BindView(R.id.tv_item_order_shop_name) TextView mTvItemOrderShopName;
    @BindView(R.id.ll_shop_order_status) LinearLayout mLlShopOrderStatus;
    @BindView(R.id.recyclerListInvoice) RecyclerView mRecyclerListInvoice;
    @BindView(R.id.switcherLayout) ViewSwitcher mSwitcherLayout;
    @BindView(R.id.tvInvoiceCount) TextView mTvInvoiceCount;
    @BindView(R.id.layout_action) RelativeLayout mLayoutAction;

    private Dialog mDialog;

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private GoogleMap mGoogleMap;
    private SupportMapFragment mMapFragment;
    private Polyline mPolylineRoute;
    private Marker mMakerEndOrder;
    private Context mContext;
    private User mCurrentUser;
    private Invoice mInvoice;
    private FetchAddressTask mTask;
    private ArrayList<Invoice> mInvoices = new ArrayList<>();
    private HashMap<Marker, Invoice> mHashMap = new HashMap<>();
    private boolean mAutoRefresh = true;
    private int mRadius;
    private NearbyInvoiceContract.Presenter mPresenter;
    private AlertDialog mReceiveDialog;
    private NewInvoiceAdapter mAdapter;

    public static NearbyInvoiceFragment newInstance() {
        return new NearbyInvoiceFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        if (PermissionUtils.checkLocationPermission(mContext)) return;
        mGoogleMap = googleMap;
        initMap();
    }

    //google api client connected
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: ");
        //check location permission
        if (!PermissionUtils.checkLocationPermission(mContext)) {
            // check location setting
            CommonUtils.checkLocationRequestSetting(
                    getActivity(),
                    mGoogleApiClient,
                    new LocationSettingCallback() {
                        @Override
                        public void onSuccess() {
                            mMapFragment.getMapAsync(NearbyInvoiceFragment.this);
                        }
                    });
        } else {
            // request permission
            PermissionUtils.requestPermission(
                    (AppCompatActivity) getActivity(),
                    Const.RequestCode.LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    false
            );
        }
    }

    // initialize map
    private void initMap() {
        configGoogleMap();
        if (PermissionUtils.checkLocationPermission(mContext)) return;
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation == null) {
            showLoadingDialog();
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, new LocationRequest(), NearbyInvoiceFragment.this);
        } else {
            onLocationChange(mLocation);
            android.util.Log.d(TAG, "initMap: " + mLocation);
        }
    }

    private void onLocationChange(Location location) {
        mCurrentUser.setLatitude(location.getLatitude());
        mCurrentUser.setLongitude(location.getLongitude());
        MapUtils.zoomToPosition(
                mGoogleMap, new LatLng(location.getLatitude(), location.getLongitude()));
        mPresenter.markInvoiceNearby(mInvoices, mCurrentUser.getAuthenticationToken(),
                new LatLng(mCurrentUser.getLatitude(), mCurrentUser.getLongitude()), mRadius);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_shipper_filter, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.menu_filter) {
            Intent intent = new Intent(mContext, FilterInvoiceActivity.class);
            startActivityForResult(intent, REQUEST_FILTER);
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Const.RequestCode.PLACE_AUTOCOMPLETE_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Place place = PlaceAutocomplete.getPlace(mContext, data);
                        Log.d(TAG, "onActivityResult: " + place.getName());
                        mTvSearchArea.setText(place.getName());
                        MapUtils.zoomToPosition(mGoogleMap,
                                new LatLng(place.getLatLng().latitude, place.getLatLng().longitude));
                        break;
                    case PlaceAutocomplete.RESULT_ERROR:
                        Status status = PlaceAutocomplete.getStatus(mContext, data);
                        Toast.makeText(mContext, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.d(TAG, " Search Cancel");
                        break;
                }
                break;
            case Const.REQUEST_CHECK_SETTINGS:
                // location setting is set up
                if (resultCode == Activity.RESULT_OK) {
                    mMapFragment.getMapAsync(NearbyInvoiceFragment.this);
                }
                break;
            case Const.REQUEST_SETTING:
                // setting app is set up
                if (resultCode == Activity.RESULT_OK) {
                    initMap();
                }
                break;
            case REQUEST_FILTER:
                if (resultCode == Activity.RESULT_OK) {
                    // Update list invoice in map
                    String jsonData = data.getStringExtra(FilterInvoiceActivity.INTENT_FILTER_DATA);
                    mInvoices = new Gson().fromJson(
                            jsonData,
                            new TypeToken<List<Invoice>>() {
                            }.getType()
                    );
                    addListMarker(mInvoices);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (PermissionUtils.isPermissionGranted(
                permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            CommonUtils.checkLocationRequestSetting(
                    getActivity(),
                    mGoogleApiClient,
                    new LocationSettingCallback() {
                        @Override
                        public void onSuccess() {
                            mMapFragment.getMapAsync(NearbyInvoiceFragment.this);
                        }
                    });
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        onLocationChange(location);
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        dismissLoadingDialog();
    }

    @Override
    public void updateInvoices(ArrayList<Invoice> invoices) {
        mInvoices = invoices;
    }
    /**
     * mark all invoices near a location on map
     *
     * @param latitude  location's latitude
     * @param longitude location's longitude
     * @param radius    radius where to find invoice
     */
    private void markInvoiceNearby(final double latitude, final double longitude, float radius) {
        Map<String, String> userParams = new HashMap<>();
        userParams.put(APIDefinition.GetInvoiceNearby.PARAM_USER_LAT, String.valueOf(latitude));
        userParams.put(APIDefinition.GetInvoiceNearby.PARAM_USER_LNG, String.valueOf(longitude));
        userParams.put(APIDefinition.GetInvoiceNearby.PARAM_USER_DISTANCE, String.valueOf(radius));
        API.getInvoiceNearby(mCurrentUser.getAuthenticationToken(), userParams,
                new API.APICallback<APIResponse<ListInvoiceData>>() {
                    @Override
                    public void onResponse(APIResponse<ListInvoiceData> response) {
                        Log.d(TAG, "onResponse: " + response.getMessage());
                        // update new invoices
                        ArrayList<Invoice> lastUpdateInvoices = (ArrayList<Invoice>) response
                                .getData().getInvoiceList();
                        // init common list invoices
                        ArrayList<Invoice> commonInvoices = new ArrayList<>(mInvoices);
                        // get common invoices between previous list invoices and new update invoices
                        commonInvoices.retainAll(lastUpdateInvoices);
                        // init list invoices need to be removed
                        ArrayList<Invoice> removeInvoices = new ArrayList<>(mInvoices);
                        // init list invoices need to be added
                        ArrayList<Invoice> addInvoices = new ArrayList<>(lastUpdateInvoices);
                        // get invoices need to be added
                        addInvoices.removeAll(commonInvoices);
                        // get invoices need to be removed
                        removeInvoices.removeAll(commonInvoices);
                        addListMarker(addInvoices);
                        removeListMarker(removeInvoices);
                        // update invoices
                        mInvoices.clear();
                        mInvoices.addAll(lastUpdateInvoices);

                        mAdapter.notifyDataSetChanged();
                        mTvInvoiceCount.setText(
                                getString(R.string.fragment_nearby_invoice_count, mInvoices.size()));
                    }

    @Override
    public void onReceiveInvoiceSuccess(String message) {
        dismissLoadingDialog();
        mReceiveDialog.dismiss();
        showUserMessage(message);
        mRlOrderDetail.setVisibility(View.GONE);
    }

    @Override
    public void onReceiveInvoiceFail(String message) {
        dismissLoadingDialog();
        mReceiveDialog.dismiss();
        showUserMessage(message);
    }

    /**
     * add list invoice marker to Map
     *
     * @param invoiceList list invoice
     */
    @Override
    public void addListMarker(List<Invoice> invoiceList) {
        for (Invoice invoice : invoiceList) {
            addNewMarkerInvoice(invoice);
        }
    }

    @Override
    public void removeListMarker(List<Invoice> invoiceList) {
        for (Invoice invoice : invoiceList) {
            for (Map.Entry<Marker, Invoice> entry : mHashMap.entrySet()) {
                Marker key = entry.getKey();
                Invoice value = entry.getValue();
                if (value.getId() == invoice.getId()) {
                    mHashMap.remove(key);
                    key.remove();
                    break;
                }
            }
        }
    }

    private void addNewMarkerInvoice(Invoice invoice) {
        mInvoices.add(invoice);
        LatLng latLng = new LatLng(invoice.getLatStart(), invoice.getLngStart());
        final Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .alpha(0)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_shop)));
        MapUtils.setAnimatedInMarker(marker);

        /** save position of invoice with marker id to hashmap */
        mHashMap.put(marker, invoice);
    }

    private Point getConfigSizeMap() {
        int widthMap = mMapFragment.getView().getWidth();
        int heightMap;
        if (mRlOrderDetail.getVisibility() == View.VISIBLE) {
            heightMap = mMapFragment.getView().getHeight() + 2 * mRlOrderDetail.getHeight();
        } else {
            heightMap = mMapFragment.getView().getHeight();
        }
        return new Point(widthMap, heightMap);
    }

    /**
     * turn on/of auto fetch address
     *
     * @param status true/false
     */
    private void switchAutoRefresh(boolean status) {
        mAutoRefresh = status;
        mImgPosMarker.setVisibility(status ? View.VISIBLE : View.GONE);
    }

    /**
     * Config google with setting like camera change listener, marker clicked listener...
     */
    private void configGoogleMap() {
        // Disable tilt and rotate map
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);
        if (PermissionUtils.checkLocationPermission(mContext)) return;
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(false);
        mGoogleMap.setPadding(
                Const.MapPadding.LEFT_PADDING,
                Const.MapPadding.TOP_PADDING,
                Const.MapPadding.RIGHT_PADDING,
                Const.MapPadding.BOTTOM_PADDING);
        mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (mAutoRefresh) {
                    if (mTask != null) {
                        mTask.cancel(true);
                    }
                    mTask = new FetchAddressTask();
                    mTask.execute(new LatLng(cameraPosition.target.latitude,
                            cameraPosition.target.longitude));
                }
            }
        });
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Check if marker is not in list of marker invoices, don't need to do anything
                if (!mHashMap.containsKey(marker)) {
                    return false;
                }
                switchAutoRefresh(false);
                mInvoice = mHashMap.get(marker);
                showInvoiceDetailWindow(mInvoice);
                removeRoute();
                mMakerEndOrder = mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(mInvoice.getLatFinish(), mInvoice.getLngFinish())));
                mPresenter.getRoute(
                        new LatLng(mInvoice.getLatStart(), mInvoice.getLngStart()),
                        new LatLng(mInvoice.getLatFinish(), mInvoice.getLngFinish())
                );
                return true;
            }
        });
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                switchAutoRefresh(true);
                mRlOrderDetail.setVisibility(View.GONE);
                removeRoute();
            }
        });
    }

    private void removeRoute() {
        if (mPolylineRoute != null) {
            mPolylineRoute.remove();
            mMakerEndOrder.remove();
        }
    }

    private void showInvoiceDetailWindow(Invoice invoice) {
        mRlOrderDetail.setVisibility(View.VISIBLE);
        mBtnNearbyReceiveOrder.setTag(invoice.getStringId());
        User mUser = invoice.getUser();
        mTvItemOrderShopName.setText(mUser.getName());
        mRatingOrderWindow.setRating((float) mUser.getRate());
        mTvNearbyDistance.setText(
                TextFormatUtils.formatDistance(invoice.getDistance()));
        mTvNearbyFrom.setText(invoice.getAddressStart());
        mTvNearbyTo.setText(invoice.getAddressFinish());
        mTvNearbyShipTime.setText(invoice.getDeliveryTime());
        mTvNearbyShipPrice.setText(
                TextFormatUtils.formatPrice(invoice.getShippingPrice()));
        mTvNearbyOrderPrice.setText(
                TextFormatUtils.formatPrice(invoice.getPrice()));
    }

    @Override
    public void drawRoute(ArrayList<Route> routes) {
        PolylineOptions polyOptions = new PolylineOptions();
        for (int i = 0; i < routes.size(); i++) {
            polyOptions.color(ContextCompat.getColor(
                    mContext.getApplicationContext(),
                    R.color.colorGreen));
            polyOptions.width(8);
            polyOptions.addAll(routes.get(i).getPoints());
        }
        if (mPolylineRoute != null && mPolylineRoute.isVisible()) {
            mPolylineRoute.remove();
        }
        mPolylineRoute = mGoogleMap.addPolyline(polyOptions);

    }

    @Override
    public void updateMapAfterDrawRoute(LatLng startAddress, LatLng finishAddress) {
        Point mapSize = getConfigSizeMap();
        LatLng configLatLng = CommonUtils.configLatLng(startAddress, finishAddress);
        MapUtils.updateZoomMap(mGoogleMap, mapSize.x, mapSize.y, configLatLng);
    }


    @Override
    public void showReceiveDialog(final String invoiceId) {
        View view =
                LayoutInflater.from(mContext).inflate(R.layout.dialog_nearby_receive_order, null);
        view.findViewById(R.id.confirm_dialog_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.receiveInvoice(mInvoice.getStringId());
            }
        });
        view.findViewById(R.id.confirm_dialog_cancel)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mReceiveDialog.dismiss();
                    }
                });
        mReceiveDialog = new AlertDialog.Builder(mContext).setView(view).show();
    }

    @Override
    public void onInvoiceCreate(final Invoice invoice) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addNewMarkerInvoice(invoice);
            }
        });
    }

    @Override
    public void onInvoiceRemove(final Invoice invoice) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int invoiceId = invoice.getId();
                for (Invoice item : mInvoices) {
                    if (item.getId() == invoiceId) {
                        mInvoices.remove(item);
                        break;
                    }
                }

                for (Map.Entry<Marker, Invoice> entry : mHashMap.entrySet()) {
                    final Marker key = entry.getKey();
                    Invoice value = entry.getValue();
                    if (value.getId() == invoiceId) {
                        MapUtils.setAnimatedOutMarker(key);
                        mHashMap.remove(key);
                        break;
                    }
                }
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_nearby_invoice;
    }

    @Override
    public void initViews() {
        mPresenter = new NearbyInvoicePresenter(mContext, this, this);
        mBtnNearbyShowPath.setVisibility(View.VISIBLE);
        mBtnNearbyReceiveOrder.setVisibility(View.VISIBLE);
        mCurrentUser = Config.getInstance().getUserInfo(mContext);
        mRlOrderDetail.setVisibility(View.GONE);
        mRadius = StorageUtils.getIntValue(
                mContext,
                Const.Storage.KEY_SETTING_INVOICE_RADIUS,
                Const.SETTING_INVOICE_RADIUS_DEFAULT
        );
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setOnInvoiceUpdate(this);
        }
        mMapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_nearby_order);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
        }
        setHasOptionsMenu(true);
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setOnInvoiceUpdate(this);
        }
        settingRecyclerView();
    }

    @Override
    public void onInvoiceItemClick(Invoice invoice) {
        showReceiveDialog(invoice.getStringId());
    }

    /**
     * Task fetch address from location in another thread
     */
    private class FetchAddressTask extends AsyncTask<LatLng, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mTvSearchArea != null) {
                mTvSearchArea.setText("...");
            }
        }

        @Override
        protected String doInBackground(LatLng... latLngs) {
            mPresenter.markInvoiceNearby(
                    mInvoices, mCurrentUser.getAuthenticationToken(), latLngs[0], mRadius);
            return MapUtils.getAddressFromLocation(mContext, latLngs[0]);
        }

        @Override
        protected void onPostExecute(String address) {
            super.onPostExecute(address);
            if (mTvSearchArea != null) {
                mTvSearchArea.setText(address);
            }
        }
    }

    @OnClick({R.id.btn_item_order_show_path, R.id.btn_item_order_register_order,
            R.id.rl_search_view, R.id.window_invoice_detail, R.id.btnViewChange})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnViewChange:
                mSwitcherLayout.showNext();
                break;
            case R.id.btn_item_order_show_path:
                mPresenter.showPath(mInvoice);
                break;
            case R.id.btn_item_order_register_order:
                String invoiceId = (String) view.getTag();
                showReceiveDialog(invoiceId);
                break;
            case R.id.rl_search_view:
                mPresenter.clickSearchView();
                break;
            case R.id.window_invoice_detail:
                mPresenter.showInvoiceDetail(mInvoice);
                break;
        }
    }

    private void settingRecyclerView() {
        mAdapter = new NewInvoiceAdapter(mInvoices, this);
        mRecyclerListInvoice.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerListInvoice.setAdapter(mAdapter);
    }
}
