package cn.fizzo.hub.fitness.ui.activity.bind;

import android.os.Message;
import android.widget.ImageView;

import butterknife.BindView;
import cn.fizzo.hub.fitness.R;
import cn.fizzo.hub.fitness.data.SPDataConsole;
import cn.fizzo.hub.fitness.ui.activity.BaseActivity;
import cn.fizzo.hub.fitness.utils.QrCodeU;

/**
 * Created by Raul.fan on 2018/1/27 0027.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class BindByPhoneActivity extends BaseActivity {


    @BindView(R.id.iv_code)
    ImageView ivCode;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bind_by_phone;
    }

    @Override
    protected void myHandleMsg(Message msg) {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initViews() {
        String code = "http://www.fizzo.cn/s/dbs/" + SPDataConsole.getStoreId(BindByPhoneActivity.this);
        ivCode.setImageBitmap(QrCodeU.create2DCode(code));
    }

    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void causeGC() {

    }

}
