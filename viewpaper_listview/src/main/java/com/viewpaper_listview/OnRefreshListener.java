package com.viewpaper_listview;

/**
 * Created by Administrator on 2017/03/27.
 */

public interface OnRefreshListener {
    /**
     * 下拉刷新
     */
    void onDownPullRefresh();

    /**
     * 上拉加载更多
     */
    void onLoadingMore();
}
