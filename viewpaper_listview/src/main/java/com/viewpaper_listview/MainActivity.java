package com.viewpaper_listview;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;

import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.layout_main);
        RefreshListeView listView1 =new RefreshListeView(mContext,null);
        RefreshListeView listView2 =new RefreshListeView(mContext,null);
        RefreshListeView listView3 =new RefreshListeView(mContext,null);

        Vector<View> papes = new Vector<View>();
        papes.add(listView1);
        papes.add(listView2);
        papes.add(listView3);

        ViewPager vp =(ViewPager)findViewById(R.id.viewpaper);
        vp.setAdapter(new CustomPagerAdapter(mContext,papes));

        listView1.setAdapter(new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_1,new String[]{"A1","A1","A1","A1"}));
        listView2.setAdapter(new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_1,new String[]{"B1","B1","B1","B1"}));
        listView3.setAdapter(new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_1,new String[]{"C1","C1","C1","C1"}));

    }

}
