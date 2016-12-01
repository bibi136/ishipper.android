package com.framgia.ishipper.presentation.manager_invoice;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.framgia.ishipper.base.BaseActivity;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.Invoice;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.InvoiceData;
import com.framgia.ishipper.presentation.invoice.detail.InvoiceDetailActivity;
import com.framgia.ishipper.presentation.manager_shipper_register.ChooseShipperRegisterActivity;
import com.framgia.ishipper.util.Const;

/**
 * Created by vuduychuong1994 on 11/23/16.
 */

public class InvoiceManagerPresenter implements InvoiceManagerContract.Presenter {

    private BaseActivity mActivity;
    private InvoiceManagerContract.View mView;

    public InvoiceManagerPresenter(
            BaseActivity activity, InvoiceManagerContract.View view) {
        mActivity = activity;
        mView = view;
    }

    @Override
    public void actionTakeInvoice(final Invoice invoice) {
        mActivity.showDialog();
        API.putUpdateInvoiceStatus(User.ROLE_SHIPPER, String.valueOf(invoice.getId()),
                                   Config.getInstance().getUserInfo(mActivity).getAuthenticationToken(),
                                   Invoice.STATUS_SHIPPING, new API.APICallback<APIResponse<InvoiceData>>() {
                    @Override
                    public void onResponse(APIResponse<InvoiceData> response) {
                        mView.notifyChangeTab(Invoice.STATUS_CODE_WAITING);
                        mView.notifyChangeTab(Invoice.STATUS_CODE_SHIPPING, true, invoice.getId());
                        mActivity.dismissDialog();
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        mActivity.dismissDialog();
                        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void actionShippedInvoice(final Invoice invoice) {
        API.putUpdateInvoiceStatus(User.ROLE_SHIPPER, String.valueOf(invoice.getId()),
                                   Config.getInstance().getUserInfo(mActivity).getAuthenticationToken(),
                                   Invoice.STATUS_SHIPPED, new API.APICallback<APIResponse<InvoiceData>>() {
                    @Override
                    public void onResponse(APIResponse<InvoiceData> response) {
                        mView.notifyChangeTab(Invoice.STATUS_CODE_SHIPPING);
                        mView.notifyChangeTab(Invoice.STATUS_CODE_SHIPPED, true, invoice.getId());
                        mView.showReviewDialog(invoice);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void actionFinishInvoice(final Invoice invoice) {
        API.putUpdateInvoiceStatus(User.ROLE_SHOP, String.valueOf(invoice.getId()),
                                   Config.getInstance().getUserInfo(mActivity).getAuthenticationToken(),
                                   Invoice.STATUS_FINISHED, new API.APICallback<APIResponse<InvoiceData>>() {
                    @Override
                    public void onResponse(APIResponse<InvoiceData> response) {
                        mView.notifyChangeTab(Invoice.STATUS_CODE_SHIPPED);
                        mView.notifyChangeTab(Invoice.STATUS_CODE_FINISHED, true, invoice.getId());
                        mView.showReviewDialog(invoice);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void startDetailInvoiceActivity(Invoice invoice) {
        Intent intent = new Intent(mActivity, InvoiceDetailActivity.class);
        Bundle extras = new Bundle();
        extras.putInt(Const.KEY_INVOICE_ID, invoice.getId());
        intent.putExtras(extras);
        mActivity.startActivityForResult(intent,
                                         InvoiceDetailActivity.REQUEST_INVOICE_ID);
    }

    @Override
    public void startListShipperRegActivity(Invoice invoice) {
        Intent intent = new Intent(mActivity, ChooseShipperRegisterActivity.class);
        intent.putExtra(Const.KEY_INVOICE_ID, invoice.getId());
        mActivity.startActivityForResult(intent, Const.RequestCode.REQUEST_CODE_CHOOSE_SHIPPER);
    }
}
