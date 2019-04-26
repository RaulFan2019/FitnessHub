package cn.fizzo.hub.fitness.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.xutils.common.Callback;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import cn.fizzo.hub.fitness.ActivityManager;
import cn.fizzo.hub.fitness.LocalApp;
import cn.fizzo.hub.fitness.data.DBDataCache;
import cn.fizzo.hub.fitness.entity.db.CacheDE;
import cn.fizzo.hub.fitness.utils.AppU;
import cn.fizzo.hub.fitness.utils.TimeU;

/**
 * Created by Administrator on 2016/7/18.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected Callback.Cancelable mCancelable;

    protected MyHandler mHandler;

    protected class MyHandler extends Handler {
        private WeakReference<BaseActivity> mOuter;

        private MyHandler(BaseActivity activity){
            mOuter = new WeakReference<BaseActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BaseActivity outer = mOuter.get();
            if(outer != null) {
                myHandleMsg(msg);
            }
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            savedInstanceState.putParcelable("android:support:fragments", null);
        }
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ActivityManager.getAppManager().addActivity(this);
        ButterKnife.bind(this);
        mHandler = new MyHandler(this);
        initData();
        initViews();
        doMyCreate();
    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            JSONArray cacheArray = new JSONArray();
            JSONObject cacheObj = new JSONObject();
            cacheObj.put("serialno", LocalApp.getInstance().getCpuSerial());
            cacheObj.put("app_versioncode", AppU.getVersionCode(this));
            cacheObj.put("eventtime", TimeU.NowTime(TimeU.FORMAT_TYPE_1));
            cacheObj.put("blocktype", 1);
            cacheObj.put("blockname", this.getClass().getSimpleName());
            cacheObj.put("event", 1);
            cacheObj.put("state", "");
            cacheArray.add(cacheObj);
            CacheDE cacheDE = new CacheDE(CacheDE.TYPE_PAGE_TOTAL,cacheArray.toJSONString());
            DBDataCache.save(cacheDE);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            JSONArray cacheArray = new JSONArray();
            JSONObject cacheObj = new JSONObject();
            cacheObj.put("serialno", LocalApp.getInstance().getCpuSerial());
            cacheObj.put("app_versioncode", AppU.getVersionCode(this));
            cacheObj.put("eventtime", TimeU.NowTime(TimeU.FORMAT_TYPE_1));
            cacheObj.put("blocktype", 1);
            cacheObj.put("blockname", this.getClass().getSimpleName());
            cacheObj.put("event", 2);
            cacheObj.put("state", "");
            cacheArray.add(cacheObj);
            CacheDE cacheDE = new CacheDE(CacheDE.TYPE_PAGE_TOTAL,cacheArray.toJSONString());
            DBDataCache.save(cacheDE);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getAppManager().finishActivity(this);
        mHandler.removeCallbacksAndMessages(null);
        if (mCancelable != null) {
            mCancelable.cancel();
        }
        causeGC();
    }




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


    //获取布局文件ID
    protected abstract int getLayoutId();

    //消息管理
    protected abstract void myHandleMsg(Message msg);

    //初始化数据
    protected abstract void initData();

    //初始化页面
    protected abstract void initViews();

    //执行初始化后的事情
    protected abstract void doMyCreate();

    //释放内存
    protected abstract void causeGC();



}
