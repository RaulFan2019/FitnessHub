package cn.fizzo.hub.fitness.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NormalTextView;
import cn.fizzo.hub.fitness.ui.widget.fizzo.SpacingTextView;

/**
 * Created by Raul.fan on 2018/1/9 0009.
 */

public class DialogAskUpdate {

    public Dialog mDialog;


    SpacingTextView tvTitle;
    NormalTextView tvVersionInfo;

    Button btnConfirm;
    Button btnCancel;

    onBtnClickListener mListener;


    public interface onBtnClickListener {
        void onConfirmBtnClick();

        void onCancelBtnClick();
    }

    /**
     * 初始化
     *
     * @param context
     */
    public DialogAskUpdate(Context context) {
        mDialog = new Dialog(context, R.style.DialogTheme);
        mDialog.setContentView(R.layout.dialog_ask_update);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);

        tvTitle = (SpacingTextView) mDialog.findViewById(R.id.tv_vision_title);
        tvVersionInfo = (NormalTextView) mDialog.findViewById(R.id.tv_version_info);
        btnConfirm = (Button) mDialog.findViewById(R.id.btn_confirm);
        btnCancel = (Button) mDialog.findViewById(R.id.btn_cancel);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onConfirmBtnClick();
                    mDialog.dismiss();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onCancelBtnClick();
                    mDialog.dismiss();
                }
            }
        });
    }

    /**
     * 显示
     */
    public void show(final String title, final String versionInfo) {
        tvTitle.setText(title);
        tvVersionInfo.setText(versionInfo);
        mDialog.show();
        btnConfirm.requestFocus();
    }


    /**
     * 设置监听器
     *
     * @param listener
     */
    public void setListener(onBtnClickListener listener) {
        mListener = listener;
    }

}
