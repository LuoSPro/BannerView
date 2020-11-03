package com.ls.bannerview.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.ls.bannerview.R;


public class BannerView extends RelativeLayout {

    //轮播的ViewPager
    private BannerViewPager mBannerVp;
    //指示器上的描述
    private TextView mBannerDescTv;
    //指示器上的点的容器
    private LinearLayout mDotContainer;
    //自定义BannerAdapter
    private BannerAdapter mAdapter;
    //Context，用于在BannerView里面创建圆点的View
    private Context mContext;
    //初始化点的指示器——点选中的Drawable
    private Drawable mIndicatorFocusDrawable;
    //初始化点的指示器——点默认的Drawable
    private Drawable mIndicatorNormalDrawable;
    //当前选中的位置
    private int mCurrentPosition = 0;
    //指示器的位置，默认左边
    private int mDotGravity = -1;
    //点的个数
    private int mDotSize = 8;
    //点的距离
    private int mDotDistance = 8;
    //底部容器
    private View mBottomBv;
    //底部颜色
    private int mBottomColor = Color.TRANSPARENT;
    //宽高比(8:3 较为合适)
    private float mWidthProportion,mHeightProportion;

    public BannerView(Context context) {
        this(context,null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        //把布局加载到View这个里面
        inflate(context, R.layout.ui_banner_layout,this);
        //初始化自定义属性
        initAttribute(attrs);
        //初始化View
        initView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //先测量，后面才能获取数据
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    /**
     * 初始化自定义属性
     * @param attrs
     */
    private void initAttribute(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.BannerView);

        //点的位置
        mDotGravity = typedArray.getInt(R.styleable.BannerView_dotGravity, mDotGravity);
        //点的颜色
        mIndicatorFocusDrawable = typedArray.getDrawable(R.styleable.BannerView_dotIndicatorFocus);
        if (mIndicatorFocusDrawable == null){
            //如果在布局文件中没有配置点的颜色，有一个默认值
            mIndicatorFocusDrawable = new ColorDrawable(Color.RED);
        }
        mIndicatorNormalDrawable = typedArray.getDrawable(R.styleable.BannerView_dotIndicatorNormal);
        if (mIndicatorNormalDrawable == null){
            //如果在布局文件中没有配置点的颜色，有一个默认值
            mIndicatorNormalDrawable = new ColorDrawable(Color.WHITE);
        }
        //获取点的大小和距离
        mDotSize = (int) typedArray.getDimension(R.styleable.BannerView_dotSize, dip2px(mDotSize));
        mDotDistance = typedArray.getDimensionPixelSize(R.styleable.BannerView_dotDistance, dip2px(mDotDistance));
        //底部容器颜色
        mBottomColor = typedArray.getColor(R.styleable.BannerView_bottomColor, mBottomColor);
        //获取宽高比
        mWidthProportion = typedArray.getFloat(R.styleable.BannerView_withProportion, mWidthProportion);
        mHeightProportion = typedArray.getFloat(R.styleable.BannerView_heightProportion, mHeightProportion);
        typedArray.recycle();
    }

    /**
     * 初始化View
     */
    private void initView() {
        mBannerVp = findViewById(R.id.banner_vp);
        mBannerDescTv = findViewById(R.id.banner_desc_tv);
        mDotContainer = findViewById(R.id.dot_container);
        mBottomBv = findViewById(R.id.bottom_banner_view);
        mBottomBv.setBackgroundColor(mBottomColor);
    }

    /**
     * 设置Adapter
     * @param adapter
     */
    public void setAdapter(BannerAdapter adapter) {
        mAdapter = adapter;
        mBannerVp.setAdapter(adapter);
        //初始化点的指示器
        initDotIndicator();
        //初始化广告的描述,默认第一条
        String bannerDesc = mAdapter.getBannerDesc(mCurrentPosition);
        mBannerDescTv.setText(bannerDesc);

        //动态指定高度
        //防止后面除数为0
        if (mWidthProportion == 0 || mHeightProportion == 0){
            return;
        }

        //动态计算宽高，计算高度
        int width = getMeasuredWidth();
        int height = (int)(width*mHeightProportion/mWidthProportion);
        //指定宽高
        getLayoutParams().height = height;
//        setMeasuredDimension(width,height);
    }

    /**
     * 点击监听
     * @param listener
     */
    public void setBannerItemClickListener(BannerViewPager.BannerItemClickListener listener){
        mBannerVp.setBannerItemClickListener(listener);
    }

    /**
     * 页面切换的回调
     * @param position
     */
    private void pageSelect(int position) {
        //把之前选中状态的点改为正常
        DotIndicatorView oldIndicatorView = (DotIndicatorView) mDotContainer.getChildAt(mCurrentPosition);
        oldIndicatorView.setDrawable(mIndicatorNormalDrawable);
        //更新当前的位置，先更新，再设置
        mCurrentPosition = position%mAdapter.getCount();
        //把当前位置的点，改成选中状态  position:  0 --> 2^31
        DotIndicatorView currentIndicatorView = (DotIndicatorView) mDotContainer.getChildAt(mCurrentPosition);
        currentIndicatorView.setDrawable(mIndicatorFocusDrawable);


        //设置广告描述
        String bannerDesc = mAdapter.getBannerDesc(mCurrentPosition);
        mBannerDescTv.setText(bannerDesc);
    }

    private void initDotIndicator() {
        int count = mAdapter.getCount();
        //让点的位置在轮播图的右边
        mDotContainer.setGravity(getDotGravity());

        for (int i = 0; i < count; i++) {
            //不断的往点的指示器添加圆点
            DotIndicatorView indicatorView = new DotIndicatorView(mContext);
            //设置大小
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dip2px(mDotSize), dip2px(mDotSize));
            indicatorView.setLayoutParams(params);
            //设置左右间距
            params.leftMargin = params.rightMargin = dip2px(mDotDistance);
            if (i == 0){
                //选中位置
                indicatorView.setDrawable(mIndicatorFocusDrawable);
            }else{
                //未选中的
                indicatorView.setDrawable(mIndicatorNormalDrawable);
            }
            mDotContainer.addView(indicatorView);
        }

        //监听
        mBannerVp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //监听当前选中的位置
                pageSelect(position);
            }
        });
    }

    private int getDotGravity() {
        switch (mDotGravity){
            case 0:
                return Gravity.CENTER;
            case -1:
                return Gravity.START;
            default:
                return Gravity.END;
        }
    }

    /**
     * 把dip转成px
     * @param dip
     * @return
     */
    private int dip2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dip,getResources().getDisplayMetrics());
    }

    /**
     * 开始滚动
     */
    public void startRoll() {
        mBannerVp.startRoll();
    }
}
