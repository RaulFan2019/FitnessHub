package cn.hwh.fizo.tv.ui.activity.bind;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.hwh.fizo.tv.R;
import cn.hwh.fizo.tv.data.SPDataStore;
import cn.hwh.fizo.tv.ui.activity.BaseActivity;
import cn.hwh.fizo.tv.utils.QrCodeU;

/**
 * Created by Raul.fan on 2017/7/19 0019.
 */

public class BindWatchByPhoneActivity extends BaseActivity {

    @BindView(R.id.iv_code)
    ImageView ivCode;
    @BindView(R.id.tv_title)
    TextView tvTitle;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_bind_watch_by_phone;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initViews() {
        String code = "http://www.fizzo.cn/s/dbs/" + SPDataStore.getStoreId(BindWatchByPhoneActivity.this);
        ivCode.setImageBitmap(QrCodeU.create2DCode(code));
        tvTitle.setTypeface(tfNormal);
    }

    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void causeGC() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
