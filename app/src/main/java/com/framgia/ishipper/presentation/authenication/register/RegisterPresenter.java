package com.framgia.ishipper.presentation.authenication.register;

import android.widget.EditText;

import com.framgia.ishipper.R;
import com.framgia.ishipper.base.BaseActivity;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.SignUpData;
import com.framgia.ishipper.ui.fragment.ValidatePinFragment;
import com.framgia.ishipper.util.InputValidate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by framgia on 18/11/2016.
 */

public class RegisterPresenter implements RegisterContract.Presenter {
    private final RegisterContract.View mView;
    private BaseActivity mActivity;

    public RegisterPresenter(RegisterContract.View view, BaseActivity activity) {
        mView = view;
        mActivity = activity;
    }

    @Override
    public void requestRegister(EditText edtPlateNumber, EditText edtPhoneNumber,
                               EditText edtNameRegister, EditText edtPasswordRegister,
                               EditText edtPasswordConfirm, String prefixPhoneNumber) {
        final User currentUser = Config.getInstance().getUserInfo(mActivity.getBaseContext());
        if (currentUser.getRole().equals(User.ROLE_SHIPPER)
                && !InputValidate.notEmpty(edtPlateNumber, mActivity)) {
            return;
        }

        if (!InputValidate.notEmpty(edtPhoneNumber, mActivity)
                || !InputValidate.notEmpty(edtNameRegister, mActivity)
                || !InputValidate.confirmPassword(edtPasswordRegister, edtPasswordConfirm, mActivity)) {
            return;
        }

        currentUser.setPhoneNumber(prefixPhoneNumber + edtPhoneNumber.getText().toString());
        currentUser.setPassword(edtPasswordRegister.getText().toString());
        currentUser.setName(edtNameRegister.getText().toString());
        currentUser.setPlateNumber(edtPlateNumber.getText().toString());
        Map<String, String> userParams = new HashMap<>();
        userParams.put(APIDefinition.RegisterUser.PARAM_USER_PHONE_NUMBER, currentUser.getPhoneNumber());
        userParams.put(APIDefinition.RegisterUser.PARAM_USER_PASSWORD, currentUser.getPassword());
        userParams.put(APIDefinition.RegisterUser.PARAM_USER_PASSWORD_CONFIRMATION, currentUser.getPassword());
        userParams.put(APIDefinition.RegisterUser.PARAM_USER_NAME, currentUser.getName());
        userParams.put(APIDefinition.RegisterUser.PARAM_USER_ROLE, currentUser.getRole());
        userParams.put(APIDefinition.RegisterUser.PARAM_USER_PLATE_NUMBER, currentUser.getPlateNumber());

        mActivity.showDialog();
        API.signUp(userParams, new API.APICallback<APIResponse<SignUpData>>() {
            @Override
            public void onResponse(APIResponse<SignUpData> response) {
                mActivity.dismissDialog();
                Config.getInstance().setUserInfo(mActivity.getBaseContext(), response.getData().getUser());
                mActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.container, ValidatePinFragment.newInstance(currentUser.getPhoneNumber(),
                                ValidatePinFragment.ACTION_ACTIVATE))
                        .addToBackStack(null)
                        .commit();

            }

            @Override
            public void onFailure(int code, String message) {
                mActivity.dismissDialog();
                mActivity.showUserMessage(message);
            }
        });
    }
}
