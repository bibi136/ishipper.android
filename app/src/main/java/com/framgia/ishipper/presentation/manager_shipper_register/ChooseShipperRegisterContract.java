package com.framgia.ishipper.presentation.manager_shipper_register;
import android.content.Context;
import com.framgia.ishipper.model.User;
import java.util.List;

/**
 * Created by vuduychuong1994 on 11/24/16.
 */

public interface ChooseShipperRegisterContract {

    interface View {

        void addListShipper(List<User> userList);

    }

    interface Presenter {

        void getListShipper(int invoiceId);

        void updateNotificationStatus(User currentUser, String notiId);

        void startMainActivity();

        void actionAcceptShipper(User shipper, int invoiceId);
    }

}
