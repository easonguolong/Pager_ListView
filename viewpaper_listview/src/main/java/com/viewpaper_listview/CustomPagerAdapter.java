package com.viewpaper_listview;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Administrator on 2017/04/06.
 */

public class CustomPagerAdapter extends PagerAdapter {
    private List<String> titleList = new ArrayList<String>();//viewpager的标题
    private Context mContext;
    private Vector<View> pages;



    public CustomPagerAdapter(Context context, Vector<View> pages) {
        this.mContext = context;
        this.pages = pages;
        titleList.add("eason");
        titleList.add("jy");
        titleList.add("jh");
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View page = pages.get(position);
        container.addView(page);
        return page;
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }
}
