package cn.fizzo.hub.fitness.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.open.androidtvwidget.adapter.BaseTabTitleAdapter;
import com.open.androidtvwidget.view.TextViewWithTTF;

import java.util.ArrayList;
import java.util.List;

import cn.fizzo.hub.fitness.R;

/**
 * Created by Raul.fan on 2017/8/25 0025.
 */

public class MainOpenTabAdapter extends BaseTabTitleAdapter {

    private List<String> titleList = new ArrayList<String>();

    public MainOpenTabAdapter(Context context) {
        titleList.add(context.getResources().getString(R.string.main_menu_tab_sport));
        titleList.add(context.getResources().getString(R.string.main_menu_tab_report));
//        titleList.add(context.getResources().getString(R.string.main_menu_tab_bind));
        titleList.add(context.getResources().getString(R.string.main_menu_tab_setting));
    }

    @Override
    public int getCount() {
        return titleList.size();
    }

    /**
     * 为何要设置ID标识。<br>
     * 因为PAGE页面中的ITEM如果向上移到标题栏， <br>
     * 它会查找最近的，你只需要在布局中设置 <br>
     * android:nextFocusUp="@+id/title_bar1" <br>
     * 就可以解决焦点问题哦.
     */
    private List<Integer> ids = new ArrayList<Integer>() {
        {
            add(R.id.main_menu_tab_title_sport);
            add(R.id.main_menu_tab_title_report);
//            add(R.id.main_menu_tab_title_bind);
            add(R.id.main_menu_tab_title_setting);
        }
    };

    @Override
    public Integer getTitleWidgetID(int pos) {
        return ids.get(pos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        parent.getContext();
        String title = titleList.get(position);
        if (convertView == null) {
            convertView = newTabIndicator(parent.getContext(), title, false);
            convertView.setId(ids.get(position)); // 设置ID.
        } else {
            // ... ...
        }
        return convertView;
    }

    /**
     * 标题栏.
     */
    private View newTabIndicator(Context context, String tabName, boolean focused) {
        final String name = tabName;
        View viewC = View.inflate(context, R.layout.item_main_menu_tab, null);
        TextViewWithTTF view = (TextViewWithTTF) viewC.findViewById(R.id.tv_tab_indicator);
        view.setText(name);

        return viewC;
    }
}
