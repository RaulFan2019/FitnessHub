package cn.fizzo.hub.fitness.ui.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 主页面的VP适配器
 * Created by Raul.fan on 2018/1/26 0026.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class MainMenuPagerAdapter extends PagerAdapter {

    private List<View> listVps;

    public MainMenuPagerAdapter(List<View> vps) {
        this.listVps = vps;
    }

    @Override
    public int getCount() {
        return listVps.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(listVps.get(position));
    }

    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(listVps.get(position));
        return listVps.get(position);
    }
}
