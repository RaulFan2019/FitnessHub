package cn.fizzo.hub.fitness.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.ui.widget.fizzo.NormalButton;

/**
 * Created by Raul on 2018/5/16.
 */

public class DialogEditHIIT {

    public Dialog mDialog;

    NormalButton btnEditName;
    NormalButton btnDelete;

    onBtnClickListener mListener;

    public interface onBtnClickListener {
        void onEditNameClick();

        void onDeleteClick();
    }

    /**
     * 初始化
     *
     * @param context
     */
    public DialogEditHIIT(Context context) {
        mDialog = new Dialog(context, R.style.DialogTheme);
        mDialog.setContentView(R.layout.dialog_edit_hiit);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);

        btnEditName = (NormalButton) mDialog.findViewById(R.id.btn_edit_name);
        btnDelete = (NormalButton) mDialog.findViewById(R.id.btn_delete);

        btnEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onEditNameClick();
                    mDialog.dismiss();
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onDeleteClick();
                    mDialog.dismiss();
                }
            }
        });
    }

    /**
     * 显示
     */
    public void show() {
        mDialog.show();
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
