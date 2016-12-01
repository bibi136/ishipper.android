package com.framgia.ishipper.presentation.invoice.detail;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.framgia.ishipper.R;
import com.framgia.ishipper.base.BaseToolbarActivity;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.Invoice;
import com.framgia.ishipper.model.ReviewUser;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.EmptyData;
import com.framgia.ishipper.net.data.InvoiceData;
import com.framgia.ishipper.net.data.ReportUserData;
import com.framgia.ishipper.net.data.ShowInvoiceData;
import com.framgia.ishipper.presentation.route.RouteActivity;
import com.framgia.ishipper.ui.activity.MainActivity;
import com.framgia.ishipper.util.Const;
import com.framgia.ishipper.widget.dialog.CancelDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HungNT on 11/28/16.
 */

public class InvoiceDetailPresenter implements InvoiceDetailContact.Presenter {
    public static final String KEY_INVOICE_ID = "invoice_id";

    private InvoiceDetailContact.View mView;
    private BaseToolbarActivity mActivity;
    private User mCurrentUser;

    public InvoiceDetailPresenter(InvoiceDetailContact.View view, BaseToolbarActivity activity) {
        mView = view;
        mActivity = activity;
        mCurrentUser = Config.getInstance().getUserInfo(mActivity.getBaseContext());
    }

    @Override
    public void getInvoiceDetail(int invoiceId) {
        // get Invoice from invoice id
        API.getInvoiceDetail(
                mCurrentUser.getRole(),
                String.valueOf(invoiceId),
                mCurrentUser.getAuthenticationToken(),
                new API.APICallback<APIResponse<ShowInvoiceData>>() {
                    @Override
                    public void onResponse(APIResponse<ShowInvoiceData> response) {
                        Invoice invoice = response.getData().mInvoice;
                        if (invoice == null) return;

                        mView.onGetInvoiceDetailSuccess(invoice);
                        mView.setInvoiceStatus(invoice);
                        mView.showActionButton(invoice.getStatusCode());
                        mView.showInvoiceData(invoice);

                        if (invoice.getUser() == null) return;
                        mView.showUserData(invoice.getUser());
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        mActivity.showUserMessage(message);
                    }
                });
    }

    @Override
    public void readNotification(String notiId) {
        API.updateNotification(mCurrentUser.getUserType(), notiId,
                mCurrentUser.getAuthenticationToken(), true,
                new API.APICallback<APIResponse<EmptyData>>() {
                    @Override
                    public void onResponse(
                            APIResponse<EmptyData> response) {
                        //TODO: read notificationItem
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        mActivity.showUserMessage(message);
                    }
                });
    }

    @Override
    public void receiveInvoice(final String invoiceId) {
        final AlertDialog dialog = new AlertDialog.Builder(mActivity).create();
        View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_nearby_receive_order, null);
        dialog.setView(view);
        view.findViewById(R.id.confirm_dialog_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                API.postShipperReceiveInvoice(
                        mCurrentUser.getAuthenticationToken(),
                        invoiceId,
                        new API.APICallback<APIResponse<EmptyData>>() {
                            @Override
                            public void onResponse(APIResponse<EmptyData> response) {
                                dialog.dismiss();
                                mActivity.showUserMessage(response.getMessage());
                            }

                            @Override
                            public void onFailure(int code, String message) {
                                mActivity.showUserMessage(message);
                            }
                        }
                );
            }
        });

        view.findViewById(R.id.confirm_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void report(final Invoice invoice) {
        final CancelDialog cancelDialog = new CancelDialog(mActivity);
        cancelDialog.setOnReportListener(new CancelDialog.OnReportListener() {
            @Override
            public void onReportListener(final ReviewUser reviewUser) {
                mActivity.showDialog();
                API.putUpdateInvoiceStatus(
                        mCurrentUser.getRole(),
                        invoice.getStringId(),
                        mCurrentUser.getAuthenticationToken(),
                        Invoice.STATUS_CANCEL,
                        new API.APICallback<APIResponse<InvoiceData>>() {
                            @Override
                            public void onResponse(APIResponse<InvoiceData> response) {
                                // Report User
                                Map<String, String> params = new HashMap<>();
                                params.put(APIDefinition.ReportUser.PARAM_INVOICE_ID, invoice.getStringId());
                                params.put(APIDefinition.ReportUser.PARAM_REVIEW_TYPE, ReviewUser.TYPE_REPORT);
                                params.put(APIDefinition.ReportUser.PARAM_CONTENT, reviewUser.getContent());

                                API.reportUser(mCurrentUser.getRole(),
                                        mCurrentUser.getAuthenticationToken(),
                                        params,
                                        new API.APICallback<APIResponse<ReportUserData>>() {
                                            @Override
                                            public void onResponse(APIResponse<ReportUserData> response) {
                                                mActivity.showUserMessage(response.getMessage());
                                                mActivity.dismissDialog();
                                                Intent intent = new Intent();
                                                intent.putExtra(KEY_INVOICE_ID, invoice.getId());
                                                mActivity.setResult(Activity.RESULT_OK, intent);
                                                mActivity.onBackPressed();
                                            }

                                            @Override
                                            public void onFailure(int code, String message) {
                                                mActivity.dismissDialog();
                                                mActivity.showUserMessage(message);
                                            }
                                        });
                            }

                            @Override
                            public void onFailure(int code, String message) {
                                mActivity.dismissDialog();
                                mActivity.showUserMessage(message);
                            }
                        });
            }
        });
        cancelDialog.show();
    }

    @Override
    public void cancelInvoice(final int invoiceId) {
        mActivity.showDialog();
        API.putUpdateInvoiceStatus(mCurrentUser.getRole(), String.valueOf(invoiceId),
                mCurrentUser.getAuthenticationToken(), Invoice.STATUS_CANCEL,
                new API.APICallback<APIResponse<InvoiceData>>() {
                    @Override
                    public void onResponse(APIResponse<InvoiceData> response) {
                        mActivity.dismissDialog();
                        mActivity.showUserMessage(response.getMessage());
                        Intent intent = new Intent();
                        intent.putExtra(KEY_INVOICE_ID, invoiceId);
                        mActivity.setResult(Activity.RESULT_OK, intent);
                        mActivity.onBackPressed();
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        mActivity.dismissDialog();
                        mActivity.showUserMessage(message);
                    }
                });
    }

    @Override
    public void takeInvoice(int invoiceId) {
        mActivity.showDialog();
        API.putUpdateInvoiceStatus(User.ROLE_SHIPPER, String.valueOf(invoiceId),
                mCurrentUser.getAuthenticationToken(),
                Invoice.STATUS_SHIPPING, new API.APICallback<APIResponse<InvoiceData>>() {
                    @Override
                    public void onResponse(APIResponse<InvoiceData> response) {
                        mActivity.dismissDialog();
                        mActivity.onBackPressed();
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        mActivity.dismissDialog();
                        mActivity.showUserMessage(message);
                    }
                }
        );
    }

    @Override
    public void finishedInvoice(String stringId) {
        mActivity.showDialog();
        String status;
        if (!mCurrentUser.isShop()) {
            status = Invoice.STATUS_SHIPPED;
        } else {
            status = Invoice.STATUS_FINISHED;
        }
        API.putUpdateInvoiceStatus(mCurrentUser.getRole(),
                stringId, mCurrentUser.getAuthenticationToken(),
                status, new API.APICallback<APIResponse<InvoiceData>>() {
                    @Override
                    public void onResponse(APIResponse<InvoiceData> response) {
                        mActivity.showUserMessage(response.getMessage());
                        mActivity.dismissDialog();
                        mActivity.onBackPressed();
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        mActivity.showUserMessage(message);
                        mActivity.dismissDialog();
                    }
                });
    }

    @Override
    public void startMainActivity() {
            Intent intent = new Intent(mActivity, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mActivity.startActivity(intent);
    }

    @Override
    public void showRouteActivity(Invoice invoice) {
        Intent showPathIntent = new Intent(mActivity, RouteActivity.class);
        showPathIntent.putExtra(Const.KeyIntent.KEY_INVOICE, invoice);
        mActivity.startActivity(showPathIntent);
    }
}
