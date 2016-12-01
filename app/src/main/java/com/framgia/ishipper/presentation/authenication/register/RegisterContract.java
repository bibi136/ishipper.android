package com.framgia.ishipper.presentation.authenication.register;

import android.widget.EditText;

/**
 * Created by framgia on 18/11/2016.
 */

public interface RegisterContract {
    interface View {

    }

    interface Presenter {
        void requestRegister(EditText edtPlateNumber, EditText edtPhoneNumber,
                             EditText edtNameRegister, EditText edtPasswordRegister,
                             EditText edtPasswordConfirm, String prefixPhoneNumber);
    }
}
