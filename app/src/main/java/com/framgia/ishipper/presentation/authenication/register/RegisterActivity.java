package com.framgia.ishipper.presentation.authenication.register;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.framgia.ishipper.R;
import com.framgia.ishipper.base.BaseToolbarActivity;
import com.framgia.ishipper.model.User;

import butterknife.BindView;
import butterknife.OnClick;

public class RegisterActivity extends BaseToolbarActivity implements RegisterContract.View {

    @BindView(R.id.edtPhoneNumber) EditText mEdtPhoneNumber;
    @BindView(R.id.edtPasswordRegister) EditText mEdtPasswordRegister;
    @BindView(R.id.edtPasswordConfirm) EditText mEdtPasswordConfirm;
    @BindView(R.id.edtNameRegister) EditText mEdtNameRegister;
    @BindView(R.id.edtPlateNumber) EditText mEdtPlateNumber;
    @BindView(R.id.radioGroupUserType) RadioGroup mRadioGroupUserType;
    @BindView(R.id.layoutPlate) LinearLayout mLayoutPlate;
    @BindView(R.id.tvTitleName) TextView mTvTitleName;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.spnRegisterPrefix) Spinner mSpnPrefixPhoneNumber;

    private User mCurrentUser = new User();
    private String mPrefixPhoneNumber;
    private RegisterPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new RegisterPresenter(this, this);
    }

    @OnClick(R.id.btnDone)
    public void onClick() {
        mPresenter.requestRegister(mEdtPlateNumber, mEdtPhoneNumber, mEdtNameRegister,
                mEdtPasswordRegister, mEdtPasswordConfirm, mPrefixPhoneNumber);
    }

    @Override
    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public int getActivityTitle() {
        return R.string.register;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void initViews() {
        // Config user type
        mCurrentUser.setRole(User.ROLE_SHIPPER);
        mRadioGroupUserType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioShipper) {
                    mLayoutPlate.setVisibility(View.VISIBLE);
                    mTvTitleName.setText(getString(R.string.name));
                    mCurrentUser.setRole(User.ROLE_SHIPPER);
                } else {
                    mLayoutPlate.setVisibility(View.GONE);
                    mTvTitleName.setText(R.string.name_shop);
                    mCurrentUser.setRole(User.ROLE_SHOP);
                }
            }
        });
        // Setup Spinner
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this,
                        R.array.prefix_phone_number,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnPrefixPhoneNumber.setAdapter(adapter);
        mSpnPrefixPhoneNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mPrefixPhoneNumber = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mPrefixPhoneNumber = "";
            }
        });
    }
}
