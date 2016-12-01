package com.framgia.ishipper.presentation.manager_shipper_register;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.framgia.ishipper.R;
import com.framgia.ishipper.base.BaseToolbarActivity;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.common.Log;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.util.CommonUtils;
import com.framgia.ishipper.util.Const;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by vuduychuong1994 on 7/20/16.
 */
public class ChooseShipperRegisterActivity extends BaseToolbarActivity implements
        ShipperRegAdapter.OnItemClickShipperRegListener,
        ShipperRegAdapter.OnClickAcceptShipperListener, ChooseShipperRegisterContract.View {
    private static final String TAG = "ChooseShipperRegisterActivity";

    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    private ShipperRegAdapter mShipperRegAdapter;
    private int mInvoiceId;
    private List<User> mShipperList;
    private User mCurrentUser;
    private ChooseShipperRegisterContract.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, FirebaseInstanceId.getInstance().getToken());

    }

    @Override
    public void onItemClickShipperRegListener(User shipper) {
        //TODO: show Info user
    }

    @Override
    public void onClickAcceptShipperListener(User shipper) {
        mPresenter.actionAcceptShipper(shipper, mInvoiceId);
    }

    @Override
    public void initViews() {
        mCurrentUser = Config.getInstance().getUserInfo(this);
        mPresenter = new ChooseShipperRegisterPresenter(this, this);
        if (CommonUtils.isOpenFromNoti(this)) {
            // Explicit Intent
            mInvoiceId = Integer.valueOf(getIntent().getExtras()
                    .getString(Const.FirebaseData.INVOICE_ID));
            String notiId = getIntent().getExtras().getString(Const.FirebaseData.NOTI_ID);
            mPresenter.updateNotificationStatus(mCurrentUser, notiId);
        } else {
            // Implicit Intent
            mInvoiceId = getIntent().getIntExtra(Const.KEY_INVOICE_ID, -1);
        }
        mShipperList = new ArrayList<>();
        mShipperRegAdapter = new ShipperRegAdapter(this, mShipperList);
        mShipperRegAdapter.setListener(this);
        mShipperRegAdapter.setAcceptShipperListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mShipperRegAdapter);
        mPresenter.getListShipper(mInvoiceId);
    }

    @Override
    public void addListShipper(List<User> userList) {
        mShipperList.clear();
        mShipperList.addAll(userList);
        mShipperRegAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return super.onNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (CommonUtils.isOpenFromNoti(this)) {
            mPresenter.startMainActivity();
        }
    }

    @Override
    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public int getActivityTitle() {
        return R.string.title_activity_list_shipper_reg;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_list_shipper_reg;
    }
}
