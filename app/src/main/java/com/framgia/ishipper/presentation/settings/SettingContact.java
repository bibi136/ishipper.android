package com.framgia.ishipper.presentation.settings;

import android.content.Intent;

/**
 * Created by HungNT on 11/22/16.
 */

class SettingContact {
    interface View {
        void setFavoriteCheckbox(boolean isEnable);
        void setPlace(String name);
    }

    interface Presenter {
        void saveSetting(boolean receiveNotification, int invoiceRadius);
        void saveSettingLocal(boolean receiveNotification, int invoiceRadius);
        void startBlacklistActivity();
        void pickFavoriteLocation(int requestCode);
        void getSetting();
        void onPickFavoritePlace(Intent data);
    }
}
