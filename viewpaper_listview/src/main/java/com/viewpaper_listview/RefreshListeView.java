package com.viewpaper_listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2017/03/27.
 */

public class RefreshListeView extends ListView implements AbsListView.OnScrollListener {

    private OnRefreshListener mOnRefershListener;
    private View headerView; // 头布局的对象
    private ProgressBar mProgressBar; // 头布局的进度条
    private TextView tvLastUpdateTime; // 头布局的最后更新时间
    private int downY; // 按下时y轴的偏移量
    private int headerViewHeight; // 头布局的高度
    private int firstVisibleItemPosition; // 屏幕显示在第一个的item的索引
    //初始化动画
    Animation UpAnimation =null;
    Animation DownAnimation = null;

    private View footerView; // 脚布局的对象
    private int footerViewHeight; // 脚布局的高度
    private boolean isLoadingMore = false; // 是否正在加载更多中
    private boolean isScrollToBottom; // 是否滑动到底部

    private final int DOWN_PULL_REFRESH = 0; // 下拉刷新状态
    private final int RELEASE_REFRESH = 1; // 松开刷新
    private final int REFRESHING = 2; // 正在刷新中
    private int currentState = DOWN_PULL_REFRESH; // 头布局的状态: 默认为下拉刷新状态

    private TextView tvState; // 头布局的状态
    private ImageView ivArrow; // 头布局的剪头

    public RefreshListeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderView();
        initFooterView();
        this.setOnScrollListener(this);
    }

    //初始化头布局
    private void initHeaderView(){
        headerView = View.inflate(getContext(),R.layout.listview_header,null);
        ivArrow = (ImageView)headerView.findViewById(R.id.iv_listview_header_arrow);
        mProgressBar = (ProgressBar)headerView.findViewById(R.id.pb_listview_header);
        tvState  = (TextView)headerView.findViewById(R.id.tv_listview_header_state);
        tvLastUpdateTime = (TextView)headerView.findViewById(R.id.tv_listview_header_last_update_time);

        tvLastUpdateTime.setText("最后刷新时间："+getLastUpdateTime());
        //系统测量出headerview的高度
        headerView.measure(0,0);
        headerViewHeight = headerView.getMeasuredHeight();
        headerView.setPadding(0,-headerViewHeight,0,0);
        this.addHeaderView(headerView);
        initAnimation();
    }

    //初始化脚布局
    private void initFooterView(){
        footerView = View.inflate(getContext(),R.layout.listview_footer,null);
        footerView.measure(0, 0);
        footerViewHeight = footerView.getMeasuredHeight();
        footerView.setPadding(0, -footerViewHeight, 0, 0);
        this.addFooterView(footerView);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if(scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_FLING){
            //判断当前是否已经到了底部
            if(isScrollToBottom && !isLoadingMore){
                isLoadingMore =true;
                //当前到底部
                footerView.setPadding(0,0,0,0);
                this.setSelection(this.getCount());
                if(mOnRefershListener!=null){
                    mOnRefershListener.onLoadingMore();
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            firstVisibleItemPosition = firstVisibleItem;
        if(getLastVisiblePosition()==(totalItemCount-1)){
            isScrollToBottom =true;
        }
        else{
            isScrollToBottom =false;
        }
    }

    //获取系统最新事件
    public String getLastUpdateTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(System.currentTimeMillis());
    }


    private void initAnimation(){
        UpAnimation = new RotateAnimation(0f,180f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        UpAnimation.setDuration(500);
        UpAnimation.setFillAfter(true); //动画结束后停在结束位置

        DownAnimation = new RotateAnimation(180f,360f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        DownAnimation.setDuration(500);
        DownAnimation.setFillAfter(true);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                downY = (int)ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveY = (int)ev.getY();
                int diff = (moveY - downY)/2;
                int paddingTop = -headerViewHeight + diff;
                if(firstVisibleItemPosition ==0&&-headerViewHeight<paddingTop){
                    if(paddingTop>0 && currentState == DOWN_PULL_REFRESH){
                        currentState = RELEASE_REFRESH;
                        refreshHeaderView();
                    }
                    else if(paddingTop <0 && currentState == RELEASE_REFRESH){
                        currentState = DOWN_PULL_REFRESH;
                        refreshHeaderView();
                    }
                    headerView.setPadding(0,paddingTop,0,0);
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if(currentState == RELEASE_REFRESH){
                    headerView.setPadding(0,0,0,0); // 把头布局设置为完全显示状态
                    // 进入到正在刷新中状态
                    currentState = REFRESHING;
                    refreshHeaderView();
                    if(mOnRefershListener!=null){
                        mOnRefershListener.onDownPullRefresh(); // 调用使用者的监听方法
                    }
                }
                else if(currentState == DOWN_PULL_REFRESH){
                    //隐藏头布局
                    headerView.setPadding(0,-headerViewHeight,0,0);
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void refreshHeaderView(){
        switch(currentState){
            case DOWN_PULL_REFRESH:
                tvState.setText("下拉刷新");
                ivArrow.startAnimation(DownAnimation);
                break;
            case RELEASE_REFRESH:
                tvState.setText("松开刷新");
                ivArrow.startAnimation(UpAnimation);
                break;
            case REFRESHING:
                ivArrow.clearAnimation();
                ivArrow.setVisibility(View.GONE);
                tvState.setText("正在刷新中...");
                mProgressBar.setVisibility(View.VISIBLE);
            default:break;
        }
    }

    public void setOnRefreshListener(OnRefreshListener listener){
        mOnRefershListener = listener;
    }

    //隐藏头布局
    public void hideHeaderView(){
        headerView.setPadding(0,-headerViewHeight,0,0);
        ivArrow.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        tvState.setText("下拉刷新");
        tvLastUpdateTime.setText("最后刷新时间："+getLastUpdateTime());
        currentState = DOWN_PULL_REFRESH;
    }

    public void hideFooterView(){
        footerView.setPadding(0, -footerViewHeight, 0, 0);
        isLoadingMore = false;
    }

}
