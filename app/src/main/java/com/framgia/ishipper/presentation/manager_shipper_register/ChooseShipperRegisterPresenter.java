package com.framgia.ishipper.presentation.manager_shipper_register;
import android.content.Intent;
import android.widget.Toast;
import com.framgia.ishipper.R;
import com.framgia.ishipper.base.BaseActivity;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.EmptyData;
import com.framgia.ishipper.net.data.ListShipperData;
import com.framgia.ishipper.ui.activity.MainActivity;
import com.framgia.ishipper.util.Const;

/**
 * Created by vuduychuong1994 on 11/24/16.
 */

public class ChooseShipperRegisterPresenter implements ChooseShipperRegisterContract.Presenter {

    private BaseActivity mActivity;
    private ChooseShipperRegisterContract.View mView;

    public ChooseShipperRegisterPresenter(
            BaseActivity activity, ChooseShipperRegisterContract.View view) {
        mActivity = activity;
        mView = view;
    }

    @Override
    public void getListShipper(int invoiceId) {
        API.getListShipperReceived(Config.getInstance().getUserInfo(mActivity).getAuthenticationToken(),
           String.valueOf(invoiceId),
           new API.APICallback<APIResponse<ListShipperData>>() {
               @Override
               public void onResponse(APIResponse<ListShipperData> response) {
                   mView.addListShipper(response.getData().getShippersList());
               }

               @Override
               public void onFailure(int code, String message) {
                   Toast.makeText(mActivity, message,
                                  Toast.LENGTH_SHORT).show();
               }
           });
    }

    @Override
    public void updateNotificationStatus(User currentUser, String notiId) {
        API.updateNotification(currentUser.getUserType(), notiId,
                               currentUser.getAuthenticationToken(), true,
                               new API.APICallback<APIResponse<EmptyData>>() {
                                   @Override
                                   public void onResponse(
                                           APIResponse<EmptyData> response) {
                                       //TODO: read notificationItem
                                   }

                                   @Override
                                   public void onFailure(int code, String message) {

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
    public void actionAcceptShipper(User shipper, final int invoiceId) {
        mActivity.showDialog();
        API.putShopReceiveShipper(Config.getInstance().getUserInfo(mActivity)
                                          .getAuthenticationToken(),
                                  shipper.getUserInvoiceId(), new API.APICallback<APIResponse<EmptyData>>() {
                    @Override
                    public void onResponse(APIResponse<EmptyData> response) {
                        mActivity.dismissDialog();
                        Toast.makeText(mActivity,
                                       R.string.accept_shipper_success, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra(Const.KEY_INVOICE_ID, invoiceId);
                        mActivity.setResult(mActivity.RESULT_OK, intent);
                        mActivity.onBackPressed();
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        mActivity.dismissDialog();
                        Toast.makeText(mActivity, message,
                                       Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
