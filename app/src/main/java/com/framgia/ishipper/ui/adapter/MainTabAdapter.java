package com.framgia.ishipper.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.model.Invoice;
import com.framgia.ishipper.presentation.invoice.detail.InvoiceDetailActivity;
import com.framgia.ishipper.presentation.invoice.nearby_invoice.NearbyInvoiceFragment;
import com.framgia.ishipper.presentation.invoice.shipping.ShippingFragment;
import com.framgia.ishipper.presentation.main.NearbyShipperFragment;
import com.framgia.ishipper.presentation.manager_invoice.ListInvoiceFragment;
import com.framgia.ishipper.presentation.manager_shipper_register.ChooseShipperRegisterActivity;
import com.framgia.ishipper.util.Const;

import static com.framgia.ishipper.ui.activity.MainActivity.SHIPPER;
import static com.framgia.ishipper.ui.activity.MainActivity.userType;

/**
 * Created by dinhduc on 20/07/2016.
 */
public class MainTabAdapter extends FragmentPagerAdapter
        implements ListInvoiceFragment.OnActionClickListener {
    private String[] mTitle;
    private Context mContext;
    private ListInvoiceFragment mListInvoiceFragment;
    private SparseArray<Fragment> mFragments = new SparseArray<>();

    public MainTabAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        if (userType == SHIPPER) {
            mTitle = new String[]{
                    context.getString(R.string.all_nearby_order),
                    context.getString(R.string.all_shipping_order)
            };
        } else {
            mTitle = new String[]{
                    context.getString(R.string.all_nearby_order),
                    context.getString(R.string.all_waiting_order)
            };
        }
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            if (userType == SHIPPER) {
                return new NearbyInvoiceFragment();
            } else {
                return new NearbyShipperFragment();
            }
        } else {
            if (userType == SHIPPER) {
                return new ShippingFragment();
            } else {
                mListInvoiceFragment =
                        ListInvoiceFragment.newInstance(
                                mContext.getString(R.string.tab_title_shop_order_wait),
                                Invoice.STATUS_CODE_INIT);
                mListInvoiceFragment.setOnActionClickListener(this);
                return mListInvoiceFragment;
            }
        }
    }

    @Override
    public int getCount() {
        return mTitle.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitle[position];
    }

    @Override
    public void onClickAction(Invoice invoice) {
        Intent intent = new Intent(mContext, ChooseShipperRegisterActivity.class);
        intent.putExtra(Const.KEY_INVOICE_ID, invoice.getId());
        mContext.startActivity(intent);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    @Override
    public void onClickCancel(Invoice invoice) {
        Toast.makeText(mContext, "Đã huỷ đơn", Toast.LENGTH_SHORT).show();
        mListInvoiceFragment.notifyChangedData(null);
    }

    @Override
    public void onClickView(Invoice invoice) {
        Intent intent = new Intent(mContext, InvoiceDetailActivity.class);
        Bundle extras = new Bundle();
        extras.putInt(Const.KEY_INVOICE_ID, invoice.getId());
        intent.putExtras(extras);
        mContext.startActivity(intent);
    }

    public Fragment getFragment(int position) {
        return mFragments.get(position);
    }
}
