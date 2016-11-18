package com.framgia.ishipper.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.framgia.ishipper.R;
import com.framgia.ishipper.base.BaseActivity;
import com.framgia.ishipper.model.Invoice;
import com.framgia.ishipper.ui.fragment.ShopCreateOrderStep1Fragment;

import butterknife.BindView;

public class ShopCreateOrderActivity extends BaseActivity {
    private static final String TAG = "ShopCreateOrderActivity";
    public static Invoice sInvoice = new Invoice();
    @BindView(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new ShopCreateOrderStep1Fragment())
                .commit();

    }

    public void addFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack(fragment.getClass().getName())
                .commit();
        Log.d(TAG, "addFragment: " + fragment.getClass().getName());
    }

    @Override
    Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    int getActivityTitle() {
        return R.string.title_activity_create_invoice;
    }

    @Override
    int getLayoutId() {
        return R.layout.activity_shop_create_order;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
