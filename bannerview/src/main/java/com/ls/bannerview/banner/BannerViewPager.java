package com.ls.bannerview.banner;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BannerViewPager extends ViewPager {

    private static final String TAG = "BannerViewPager";

    //1.自定义Adapter
    private BannerAdapter mAdapter;

    //2.实现自动轮播 -- 发送消息的messageWhat
    private final int SCROLL_MSG = 0X0011;

    //2.实现自动轮播 -- 页面切换间隔时间（默认值）
    private int mCutDownTime = 2500;

    private Handler mHandler = null;

    // 改变ViewPager切换的速率 - 自定义页面切换Scroller
    private BannerScroller mScroller;

    //复用
    private List<View> mConvertView;
    //监听回调
    private BannerItemClickListener mListener;
    //Activity
    private Activity mActivity;


    public BannerViewPager(@NonNull Context context) {
        this(context,null);
    }

    public BannerViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initHandler();
        //这里的Context就是Activity
        mActivity = (Activity)getContext();

        //改变ViewPager切换的速率，两种方式
        //1. duration 持续时间，但他是局部变量
        //2. 改变mScroller，但是这个属性是private的，所以通过反射去设置
        try {
            //获取属性
            Field field = ViewPager.class.getDeclaredField("mScroller");
            mScroller = new BannerScroller(context);
            //设置为强制改变private，不然可能提示我们不能修改私有属性
            field.setAccessible(true);
            //设置参数 第一个object当前属性在哪个类  第二个参数代表要设置的值
            field.set(this, mScroller);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        mConvertView = new ArrayList<>();
    }

    private void initHandler() {
        mHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                //每个多少秒X秒切换到下一页
                //切换到下一页
                setCurrentItem(getCurrentItem() + 1);
                //不断循环执行
                startRoll();
            }
        };
    }

    /**
     * 1. 自定义Adapter
     * @param adapter
     */
    public void setAdapter( BannerAdapter adapter) {
        this.mAdapter = adapter;
        //设置父类 ViewPager的adapter
        //这里设置Adapter之后，会不断的循环调用instantiateItem()方法，去增加ItemView
        setAdapter(new BannerPagerAdapter());
        //管理Activity的生命周期
        mActivity.getApplication().registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
    }


    /**
     * 实现自动轮播
     */
    public void startRoll(){
        if (mHandler != null){
            //清除消息，防止被多次调用时，间隔时间就没有2500了
            mHandler.removeMessages(SCROLL_MSG);
            //参数： 消息，延迟时间
            //需求：让用户自定义，但也要有个默认值  2500
            mHandler.sendEmptyMessageDelayed(SCROLL_MSG,mCutDownTime);
            //Log.d(TAG, "startRoll: handler is running.....");
        }
    }

    /**
     * 销毁Handler停止发送  解决内存泄漏
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //解除绑定
        mActivity.getApplication().unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
        if (mHandler != null){//判空，避免空指针异常
            mHandler.removeMessages(SCROLL_MSG);
            mHandler = null;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        if (mAdapter != null) {
            initHandler();
            startRoll();
            // 管理Activity的生命周期
            mActivity.getApplication().registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
        }
        super.onAttachedToWindow();
        try {
            Field mFirstLayout = ViewPager.class.getDeclaredField("mFirstLayout");
            mFirstLayout.setAccessible(true);
            mFirstLayout.set(this,false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 给ViewPager设置适配器
     */
    public class  BannerPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            //为了实现无限循环
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            //官方推荐这里这么写
            //因为在ViewPager的addNewItem方法中，回调新增ItemView的时候ii.object = mAdapter.instantiateItem(this, position);
            //返回的就是object对象，所以这里直接用view == object
            return view == object;
        }

        /**
         * 创建ViewPager条目回调的方法
         * ii.object = mAdapter.instantiateItem(this, position);
         * @param container 就是我们的ViewPager,上面的this就是传过来的container，就是ViewPager
         * @param position
         * @return 这里返回Object对象，和上面的isViewFromObject里面的逻辑对应起来了
         */
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            //Adapter设计模式为了完全让用户自定义
            // position 的变化 0 -> 2^31 会溢出，所以我们对总数据进行求模运算
            View bannerItemView;
            bannerItemView = mAdapter.getView(position%mAdapter.getCount(),getConvertView());
            //设置监听
            bannerItemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(position%mAdapter.getCount());
                }
            });
            //让用户去添加，实现用户的自定义
            // 添加ViewPager里面
            container.addView(bannerItemView);
            return bannerItemView;
        }

        /**
         * 销毁条目回调的方法
         * @param container
         * @param position
         * @param object
         */
        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View)object);
            mConvertView.add((View) object);
        }
    }

    /**
     * 获取复用界面
     * @return
     */
    private View getConvertView() {
        for (int i = 0; i < mConvertView.size(); i++) {
            //获取没有添加ViewPager里面的
            if (mConvertView.get(i).getParent() != null){
                return mConvertView.get(i);
            }
        }
        return null;
    }

    /**
     * 设置监听
     */
    public void setBannerItemClickListener(BannerItemClickListener listener){
        this.mListener = listener;
    }

    /**
     * 监听回调
     */
    public interface BannerItemClickListener{
        void onItemClick(int position);
    }

    //管理Activity的生命周期
    Application.ActivityLifecycleCallbacks mActivityLifecycleCallbacks = new DefaultActivityLifecycleCallbacks(){
        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            //注意监听的是不是当前Activity的生命周期，因为我们这里监听的是所有的Activity的生命周期
            //Log.d(TAG, "onActivityResumed: current activity --> " + activity);
            //Log.d(TAG, "onActivityResumed: current getContext() --> " + getContext());
            if (activity == mActivity){
                //开启轮播
                mHandler.sendEmptyMessageDelayed(mCutDownTime,SCROLL_MSG);
            }
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
            if (activity == mActivity){
                //停止轮播
                mHandler.removeMessages(SCROLL_MSG);
            }
        }
    };
}
