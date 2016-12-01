package com.framgia.ishipper.presentation.settings;

import android.content.Intent;

import com.framgia.ishipper.R;
import com.framgia.ishipper.base.BaseActivity;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.UpdateProfileData;
import com.framgia.ishipper.presentation.block.BlackListActivity;
import com.framgia.ishipper.util.Const;
import com.framgia.ishipper.util.StorageUtils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.HashMap;

/**
 * Created by HungNT on 11/22/16.
 */

class SettingPresenter implements SettingContact.Presenter {

    private final SettingContact.View mView;
    private BaseActivity mActivity;


    public SettingPresenter(SettingContact.View view, BaseActivity activity) {
        mView = view;
        mActivity = activity;
    }


    @Override
    public void saveSetting(boolean receiveNotification, int invoiceRadius) {
        User mCurrentUser = Config.getInstance().getUserInfo(mActivity);
        saveSettingLocal(receiveNotification, invoiceRadius);

        if (mCurrentUser != null) {
            mCurrentUser.setNotification(receiveNotification
                    ? Const.Notification.ON
                    : Const.Notification.OFF);
            Config.getInstance().setUserInfo(mActivity, mCurrentUser);

            HashMap<String, String> params = new HashMap<>();
            params.put(APIDefinition.PutUpdateProfile.PARAM_NOTIFICATION,
                    String.valueOf(mCurrentUser.getNotification()));
            mActivity.showDialog();
            API.putUpdateProfile(params, new API.APICallback<APIResponse<UpdateProfileData>>() {
                @Override
                public void onResponse(APIResponse<UpdateProfileData> response) {
                    mActivity.dismissDialog();
                    mActivity.showUserMessage(response.getMessage());
                }

                @Override
                public void onFailure(int code, String message) {
                    mActivity.dismissDialog();
                    mActivity.showUserMessage(message);
                }
            });
        }
    }

    @Override
    public void saveSettingLocal(boolean receiveNotification, int invoiceRadius) {
        StorageUtils.setValue(mActivity, Const.Storage.KEY_SETTING_NOTIFICATION, receiveNotification);
        StorageUtils.setValue(mActivity, Const.Storage.KEY_SETTING_INVOICE_RADIUS, invoiceRadius);
    }

    @Override
    public void startBlacklistActivity() {
        Intent intent = new Intent(mActivity, BlackListActivity.class);
        mActivity.startActivity(intent);
    }

    @Override
    public void pickFavoriteLocation(int requestCode) {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                    .build();
            Intent searchIntent = new PlaceAutocomplete
                    .IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setFilter(typeFilter)
                    .build(mActivity);
            mActivity.startActivityForResult(searchIntent, requestCode);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            mActivity.showUserMessage(R.string.err_google_play_service_not_available);
        }
    }

    @Override
    public void getSetting() {
        //TODO get setting from server
    }

    @Override
    public void onPickFavoritePlace(Intent data) {
        Place place = PlaceAutocomplete.getPlace(mActivity, data);
        mView.setPlace(place.getName().toString());
    }
}
