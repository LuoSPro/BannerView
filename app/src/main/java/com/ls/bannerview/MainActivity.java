package com.ls.bannerview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.ls.bannerview.banner.BannerAdapter;
import com.ls.bannerview.banner.BannerView;
import com.ls.bannerview.banner.BannerViewPager;
import com.ls.bannerview.bean.BookListResult;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private BannerView mBannerView;

    private OkHttpClient mOkHttpClient;
    private Handler mHandler = new Handler();
    private List<BookListResult.DataBean.AlbumsBean> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBannerView = findViewById(R.id.banner_view);
        mOkHttpClient = new OkHttpClient();
        requestListData();
    }

    /**
     * 请求列表数据
     */
    private void requestListData(){
        //利用okhttp去获取网络数据
        Request.Builder builder = new Request.Builder();
        builder.url("http://mobile.ximalaya.com/subscribe/v2/subscribe/recommend/unlogin?pageId=1&pageSize=30");
        mOkHttpClient.newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull Response response) throws IOException {
                //后台返回的json字符串
                String result = response.body().string();
                Log.d(TAG, "onResponse: result --> " + result);
                //Gson解析成对象
                BookListResult listResult = new Gson().fromJson(result, BookListResult.class);
                //获取列表数据
                final List<BookListResult.DataBean.AlbumsBean> list = listResult.getData().getAlbums();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showListData(list);
                    }
                });
            }

            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.d(TAG, "onFailure: e --> " + e);
            }
        });
    }

    private void showListData(List<BookListResult.DataBean.AlbumsBean> list) {
        mData.clear();
        mData.addAll(list);

        mBannerView.setAdapter(new BannerAdapter() {
            @Override
            public View getView(int position, View convertView) {
                ImageView imageView = null;
                if (convertView == null){
                    imageView = new ImageView(MainActivity.this);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                }else{
                    imageView = (ImageView) convertView;
                }
                String imagePath = mData.get(position).getCoverMiddle();
                Glide.with(MainActivity.this).load(imagePath)
                        .placeholder(R.drawable.ic_launcher_foreground)//加载占位图（默认图片）
                        .into(imageView);
                return imageView;
            }

            @Override
            public int getCount() {
                return 5;
            }

            @Override
            public String getBannerDesc(int position) {
                return mData.get(position).getTitle();
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
