package com.framgia.ishipper.presentation.notification;

import com.framgia.ishipper.base.BaseToolbarActivity;
import com.framgia.ishipper.model.Notification;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.ListNotificationData;
import com.framgia.ishipper.util.Const;

import java.util.List;

/**
 * Created by dinhduc on 22/11/2016.
 */

public class NotificationPresenter implements NotificationContract.Presenter {
    private BaseToolbarActivity mActivity;
    private NotificationContract.View mView;

    public NotificationPresenter(BaseToolbarActivity activity, NotificationContract.View view) {
        mActivity = activity;
        mView = view;
    }

    @Override
    public void loadMore(List<Notification> notifications,
                         NotificationAdapter adapter, User currentUser, int page) {
        notifications.add(null);
        adapter.notifyItemInserted(notifications.size() - 1);
        API.getAllNotification(
                currentUser.getAuthenticationToken(),
                currentUser.getRole(),
                page,
                Const.Setting.PER_PAGE,
                new API.APICallback<APIResponse<ListNotificationData>>() {
                    @Override
                    public void onResponse(APIResponse<ListNotificationData> response) {
                        mView.updateListNoti(response);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        mActivity.showUserMessage(message);
                    }
                }

        );
    }
}
