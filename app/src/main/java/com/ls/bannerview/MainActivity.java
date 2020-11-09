package com.ls.bannerview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.ls.bannerview.banner.BannerAdapter;
import com.ls.bannerview.banner.BannerView;
import com.ls.bannerview.banner.BannerViewPager;

public class MainActivity extends AppCompatActivity {

    private BannerView mBannerView;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBannerView = findViewById(R.id.banner_view);
        mHandler = new Handler(Looper.myLooper());
        //必要的工作
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showListData();
            }
        },50);
    }

    private void showListData() {
        mBannerView.setAdapter(new BannerAdapter() {
            @Override
            public View getView(int position, View convertView) {//支持用户自定义轮播的View
                ImageView imageView = null;
                //covertView：缓存的View
                if (convertView == null){
                    imageView = new ImageView(MainActivity.this);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                }else{
                    imageView = (ImageView) convertView;
                }
                imageView.setImageResource(R.drawable.ic_launcher_foreground);
                return imageView;
            }

            //支持用户自定义指示器的圆点个数
            @Override
            public int getCount() {
                return 5;
            }

            //支持用户自定义指示器每个ItemView的描述
            @Override
            public String getBannerDesc(int position) {
                return "哈哈哈哈";
            }
        });
        //开启自动滚动
        mBannerView.startRoll();
        //设置监听
        mBannerView.setBannerItemClickListener(new BannerViewPager.BannerItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(MainActivity.this,"current position " + position,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
