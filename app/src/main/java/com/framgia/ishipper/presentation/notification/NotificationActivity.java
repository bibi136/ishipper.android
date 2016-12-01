package com.framgia.ishipper.presentation.notification;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.framgia.ishipper.R;
import com.framgia.ishipper.base.BaseToolbarActivity;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.Notification;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.ListNotificationData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class NotificationActivity extends BaseToolbarActivity implements NotificationContract.View {
    private static final String TAG = "NotificationActivity";

    @BindView(R.id.rvListNotification) RecyclerView rvListNotification;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    private NotificationAdapter mAdapter;
    private List<Notification> mNotificationList = new ArrayList<>();
    private User mCurrentUser;
    private LinearLayoutManager mLayoutManager;
    private int mVisibleThreshold = 1;
    private boolean mIsLoading;
    private int mPage = 0;
    private NotificationContract.Presenter mPresenter;

    @Override
    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public int getActivityTitle() {
        return R.string.title_activity_notification;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_notification;
    }

    @Override
    public void initViews() {
        mPresenter = new NotificationPresenter(this, this);
        mCurrentUser = Config.getInstance().getUserInfo(this);
        if (mCurrentUser == null) return;
        mLayoutManager = new LinearLayoutManager(getBaseContext());
        rvListNotification.setLayoutManager(mLayoutManager);
        mAdapter = new NotificationAdapter(getBaseContext(), mNotificationList);
        rvListNotification.setAdapter(mAdapter);
        mPresenter.loadMore(mNotificationList, mAdapter, mCurrentUser, ++mPage);
        rvListNotification.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = mLayoutManager.getItemCount();
                int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
                if (!mIsLoading && totalItemCount <= (lastVisibleItem + mVisibleThreshold)) {
                    mPresenter.loadMore(mNotificationList, mAdapter, mCurrentUser, ++mPage);
                    mIsLoading = true;
                }
            }
        });
    }

    @Override
    public void updateListNoti(APIResponse<ListNotificationData> response) {
        mNotificationList.remove(mNotificationList.size() - 1);
        mAdapter.notifyItemRemoved(mNotificationList.size());
        mNotificationList.addAll(response.getData().getNotifications());
        mAdapter.notifyDataSetChanged();
        mIsLoading = false;
    }
}
