package cn.hwh.fizo.tv.ui.activity.guide;

import android.widget.TextView;

import butterknife.BindView;
import cn.hwh.fizo.tv.R;
import cn.hwh.fizo.tv.data.DBDataStore;
import cn.hwh.fizo.tv.data.SPDataStore;
import cn.hwh.fizo.tv.entity.db.StoreDE;
import cn.hwh.fizo.tv.ui.activity.BaseActivity;
import cn.hwh.fizo.tv.ui.widget.common.SpacingTextView;
import cn.hwh.fizo.tv.utils.SerialU;

/**
 * Created by Raul.fan on 2017/7/11 0011.
 */

public class GuideAboutUsActivity extends BaseActivity {


    @BindView(R.id.tv_tip_wechart_1)
    TextView tvTipWechart1;
    @BindView(R.id.tv_tip_wechart_2)
    TextView tvTipWechart2;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_content)
    SpacingTextView tvContent;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_guide_about_us;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initViews() {
        tvTipWechart1.setTypeface(tfNormal);
        tvTipWechart2.setTypeface(tfNormal);
        tvTitle.setTypeface(tfNormal);
        tvVersion.setTypeface(tfNormal);
        tvContent.setTypeface(tfNormal);

        int storeId = SPDataStore.getStoreId(GuideAboutUsActivity.this);
        StoreDE storeDE = DBDataStore.getStoreInfo(storeId);
        String cpu = SerialU.getCpuSerial();
        cpu = cpu.substring(cpu.length() - 8, cpu.length());
        tvVersion.setText("HUB名称：" + storeDE.hubName + "\n\nHUB编号:：" + cpu);
    }

    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void causeGC() {

    }

}
