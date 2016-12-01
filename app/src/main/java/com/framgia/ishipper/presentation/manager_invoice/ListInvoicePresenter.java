package com.framgia.ishipper.presentation.manager_invoice;
import com.framgia.ishipper.base.BaseActivity;
import com.framgia.ishipper.model.Invoice;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.ListInvoiceData;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vuduychuong1994 on 11/24/16.
 */

public class ListInvoicePresenter implements ListInvoiceContract.Presenter {

    private BaseActivity mActivity;
    private ListInvoiceContract.View mView;

    public ListInvoicePresenter(
            BaseActivity activity, ListInvoiceContract.View view) {
        mActivity = activity;
        mView = view;
    }

    @Override
    public void getInvoice(
            String role, String authenticationToken, int statusCode,
            final ListInvoiceFragment.OnGetInvoiceListener callback) {
        String status = Invoice.getStatusFromCode(statusCode);
        Map<String, String> params = new HashMap<>();
        params.put(APIDefinition.GetListInvoice.PARAM_STATUS, status);
        requestInvoice(role, authenticationToken, params, callback);
    }

    @Override
    public void searchInvoice(
            String role, String authenticationToken, int statusCode, String nameSearch,
            ListInvoiceFragment.OnGetInvoiceListener callback) {
        String status = Invoice.getStatusFromCode(statusCode);
        Map<String, String> params = new HashMap<>();
        params.put(APIDefinition.GetListInvoice.PARAM_STATUS, status);
        params.put(APIDefinition.GetListInvoice.PARAM_QUERY, nameSearch);
        requestInvoice(role, authenticationToken, params, callback);
    }

    private void requestInvoice(String role, String authenticationToken,
                                Map<String, String> params, final ListInvoiceFragment.OnGetInvoiceListener callback) {
        mView.showLoading();
        API.getInvoice(role, authenticationToken, params,
                       new API.APICallback<APIResponse<ListInvoiceData>>() {
                           @Override
                           public void onResponse(APIResponse<ListInvoiceData> response) {
                               mView.addListInvoice(response.getData().getInvoiceList());
                               if (callback != null) { callback.onGetInvoiceSuccess(); }
                               mView.dismissLoading();
                           }

                           @Override
                           public void onFailure(int code, String message) {
                               if (callback != null) { callback.onGetInvoiceFail(); }
                               mView.dismissLoading();
                           }
                       });
    }
}
