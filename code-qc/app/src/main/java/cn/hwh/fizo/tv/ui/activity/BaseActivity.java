package cn.hwh.fizo.tv.ui.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.xutils.common.Callback;

import butterknife.ButterKnife;
import cn.hwh.fizo.tv.ActivityStackManager;

/**
 * Created by Administrator on 2016/7/18.
 */
public abstract class BaseActivity extends AppCompatActivity {


    protected Typeface tfNormal;
    protected Typeface tfNum;

    protected Callback.Cancelable mCancelable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            savedInstanceState.putParcelable("android:support:fragments", null);
        }
        super.onCreate(savedInstanceState);
        ActivityStackManager.getAppManager().addActivity(this);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        tfNormal = Typeface.createFromAsset(getAssets(), "fonts/tvNormal.TTF");
        tfNum = Typeface.createFromAsset(getAssets(), "fonts/tvNum.otf");
        initData();
        initViews();
        doMyCreate();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        causeGC();
        if (mCancelable != null) {
            mCancelable.cancel();
        }
        ActivityStackManager.getAppManager().finishActivity(this);
    }


    protected abstract int getLayoutId();

    protected void startActivity(Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), cls);
        startActivity(intent);
    }

    protected void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), cls);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void startActivityForResult(Class<?> cls, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), cls);
        startActivityForResult(intent, requestCode);
    }

    protected void startActivityForResult(Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), cls);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    //初始化数据
    protected abstract void initData();

    //初始化页面
    protected abstract void initViews();

    //执行初始化后的事情
    protected abstract void doMyCreate();

    //释放内存
    protected abstract void causeGC();


}
