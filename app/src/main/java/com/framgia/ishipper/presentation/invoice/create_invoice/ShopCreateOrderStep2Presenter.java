package com.framgia.ishipper.presentation.invoice.create_invoice;

import android.app.TimePickerDialog;
import android.text.TextUtils;
import android.widget.TimePicker;

import com.framgia.ishipper.base.BaseFragment;
import com.framgia.ishipper.ui.activity.ShopCreateOrderActivity;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by framgia on 18/11/2016.
 */

public class ShopCreateOrderStep2Presenter implements ShopCreateOrderStep2Contract.Presenter {
    private final ShopCreateOrderStep2Contract.View mView;
    private BaseFragment mFragment;

    public ShopCreateOrderStep2Presenter(ShopCreateOrderStep2Contract.View view, BaseFragment fragment) {
        mView = view;
        mFragment = fragment;
    }

    @Override
    public boolean validateDataInput(String name, String weight, String orderPrice, String shipPrice, String time) {
        if (TextUtils.isEmpty(name)) {
            mView.validateNameFailure();
            return false;
        }
        if (TextUtils.isEmpty(weight)) {
            mView.validateWeightFailure();
            return false;
        }
        if (TextUtils.isEmpty(orderPrice)) {
            mView.validateOrderPriceFailure();
            return false;
        }
        if (TextUtils.isEmpty(shipPrice)) {
            mView.validateShipPriceFailure();
            return false;
        }
        if (TextUtils.isEmpty(time)) {
            mView.validateTimeFailure();
            return false;
        }
        return true;
    }

    @Override
    public void saveInvoice(String name, String weight, String orderPrice, String shipPrice,
                            String time, String customerName, String customerPhone, String note) {
        ShopCreateOrderActivity.sInvoice.setName(name);
        ShopCreateOrderActivity.sInvoice.setWeight(Float.valueOf(weight));
        ShopCreateOrderActivity.sInvoice.setPrice(Double.valueOf(orderPrice));
        ShopCreateOrderActivity.sInvoice.setShippingPrice(Double.valueOf(shipPrice));
        ShopCreateOrderActivity.sInvoice.setDeliveryTime(time);
        ShopCreateOrderActivity.sInvoice.setCustomerName(customerName);
        ShopCreateOrderActivity.sInvoice.setCustomerNumber(customerPhone);
        ShopCreateOrderActivity.sInvoice.setDescription(note);

        if (mFragment.getActivity() instanceof ShopCreateOrderActivity) {
            ((ShopCreateOrderActivity) mFragment.getActivity())
                    .addFragment(new ShopCreateOrderStep3Fragment());
        }
    }

    @Override
    public void pickTime(final String userTime) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        TimePickerDialog dialog = new TimePickerDialog(mFragment.getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        mView.showTextTime(String.valueOf(i) + ":" + i1 + " " + userTime);
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        dialog.show();
    }


}
