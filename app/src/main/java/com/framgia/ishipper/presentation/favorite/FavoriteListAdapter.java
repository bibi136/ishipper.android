package com.framgia.ishipper.presentation.favorite;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.EmptyData;
import com.framgia.ishipper.widget.dialog.ConfirmDialog;
import com.framgia.ishipper.util.CommonUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vuduychuong1994 on 9/15/16.
 */
public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUserList;


    public FavoriteListAdapter(Context context, List<User> userList) {
        mContext = context;
        mUserList = userList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_black_list_shipper, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = mUserList.get(position);
        holder.mTextName.setText(user.getName());

    }

    public void add(User user) {
        mUserList.add(0, user);
        notifyItemInserted(0);
    }

    @Override
    public int getItemCount() {
        if (mUserList != null) return mUserList.size();

        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvUserName) TextView mTextName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.btnRemove)
        public void onClick() {
            new ConfirmDialog(mContext)
                    .setMessage(mContext.getString(R.string.dialog_remove_user_from_favorite))
                    .setTitle(mContext.getString(R.string.dialog_delete_message))
                    .setIcon(R.drawable.ic_delete_white_24dp)
                    .setButtonCallback(new ConfirmDialog.ConfirmDialogCallback() {
                        @Override
                        public void onPositiveButtonClick(ConfirmDialog confirmDialog) {
                            deleteUserFromList(getAdapterPosition());
                            confirmDialog.cancel();
                        }

                        @Override
                        public void onNegativeButtonClick(ConfirmDialog confirmDialog) {
                            confirmDialog.cancel();
                        }
                    }).show();
        }

        private void deleteUserFromList(final int position) {
            User currentUser = Config.getInstance().getUserInfo(mContext);
            final Dialog loadingDialog = CommonUtils.showLoadingDialog(mContext);
            API.deleteUserFavorite(
                    currentUser.getAuthenticationToken(),
                    currentUser.getUserType(),
                    mUserList.get(position).getFavoriteListId(),
                    new API.APICallback<APIResponse<EmptyData>>() {
                        @Override
                        public void onResponse(APIResponse<EmptyData> response) {
                            loadingDialog.dismiss();
                            mUserList.remove(position);
                            notifyItemRemoved(position);
                        }

                        @Override
                        public void onFailure(int code, String message) {
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();
                        }
                    }
            );
        }
    }
}
