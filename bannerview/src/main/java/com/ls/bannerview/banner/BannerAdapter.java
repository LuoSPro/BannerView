package com.ls.bannerview.banner;

import android.view.View;

public abstract class BannerAdapter{

    /**
     * 根据位置获取ViewPager里面的子View
     * @param position
     * @param convertView
     * @return
     */
    public abstract View getView(int position, View convertView);

    /**
     * 获取轮播的数量
     * @return
     */
    public abstract int getCount();

    /**
     * 根据位置，获取广告位的描述
     * 不一定全部都有广告描述
     * @return
     */
    public String getBannerDesc(int position){
        return "";
    }
}
