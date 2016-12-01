package com.framgia.ishipper.presentation.notification;

import com.framgia.ishipper.model.Notification;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.ListNotificationData;

import java.util.List;

/**
 * Created by dinhduc on 22/11/2016.
 */

public class NotificationContract {
    interface View {
        void updateListNoti(APIResponse<ListNotificationData> response);
    }

    interface Presenter {
        void loadMore(List<Notification> notifications,
                      NotificationAdapter adapter, User currentUser, int page);
    }
}
