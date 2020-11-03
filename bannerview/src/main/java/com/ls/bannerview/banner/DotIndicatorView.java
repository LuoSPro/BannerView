package com.ls.bannerview.banner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 轮播图的指示器中的点
 */

public class DotIndicatorView extends View {

    private Drawable mDrawable;

    public DotIndicatorView(Context context) {
        this(context,null);
    }

    public DotIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DotIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDrawable != null){
//            mDrawable.setBounds(0,0,getMeasuredWidth(),getMeasuredHeight());
//            mDrawable.draw(canvas);
            //从drawable中得到Bitmap
            Bitmap bitmap = drawableToBitmap(mDrawable);

            //把Bitmap变为圆的
            Bitmap circleBitmap = getCircleBitmap(bitmap);

            //把圆形的bitmap绘制到画布上
            canvas.drawBitmap(circleBitmap,0,0,null);
        }
    }

    /**
     * 把指示器变成圆形
     * @param bitmap
     * @return
     */
    private Bitmap getCircleBitmap(Bitmap bitmap) {
        //创建一个圆形的Bitmap
        Bitmap circleBitmap = Bitmap.createBitmap(getMeasuredWidth(),getMeasuredHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(circleBitmap);

        Paint paint = new Paint();
        //抗锯齿
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        //仿抖动
        paint.setDither(true);

        //在画布上面画个圆
        canvas.drawCircle(getMeasuredWidth()/2,getMeasuredHeight()/2,getMeasuredWidth()/2,paint);

        //设置model，取圆和bitmap矩阵的交集的模式  ---srcIn
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //再把原来的bitmap绘制到新的圆上面
        canvas.drawBitmap(bitmap,0,0,paint);

        //回收Bitmap
        bitmap.recycle();
        bitmap = null;
        return circleBitmap;
    }

    /**
     * 从drawable中得到Bitmap
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        //如果是BitmapDrawable类型
        if (drawable instanceof BitmapDrawable){
            return ((BitmapDrawable)drawable).getBitmap();
        }
        //如果是其他类型 ColorDrawable
        //创建一个什么也没有的bitmap
        Bitmap outBitmap = Bitmap.createBitmap(getMeasuredWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        //创建一个画布
        Canvas canvas = new Canvas(outBitmap);
        //把Drawable画到Bitmap上
        drawable.setBounds(0,0,getMeasuredWidth(),getMeasuredHeight());
        drawable.draw(canvas);
        return outBitmap;
    }

    /**
     * 设置Drawable
     * @param drawable
     */
    public void setDrawable(Drawable drawable) {
        this.mDrawable = drawable;
        //重新绘制View
        invalidate();
    }
}
