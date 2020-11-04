# BannerView

BannerView是一个Android自定义轮播图控件，可以自定义放置轮播的内容、指示器中点的个数，轮播图Item的描述，还有点击监听事件等等。

BannerView可以自适应高度，只需要你根据图片的比例，去设置BannerView的withProportion和heightProportion属性。

```xml
app:withProportion="8"
app:heightProportion="3"
```

一般8:3能有一个较为常见的轮播图效果

![效果1](https://user-images.githubusercontent.com/52788705/98061473-e74d8400-1e86-11eb-96f1-04099edb6970.gif)
![效果2](https://user-images.githubusercontent.com/52788705/98061451-def54900-1e86-11eb-8d9f-f19b61abd745.gif)

## Download

#### gradle:

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

```
dependencies {
	implementation 'com.github.LuoSPro:BannerView:1.0.1'
}
```

#### layout:

```xml
<com.ls.bannerview.banner.BannerView
    android:id="@+id/banner_view"
    android:layout_width="match_parent"
    android:layout_height="0dp"
    app:dotSize="3dp"
    app:dotDistance="1dp"
    app:dotGravity="right"
    app:withProportion="8"
    app:heightProportion="3"
    app:bottomColor="@color/banner_bottom_bar_bg_day"
    app:dotIndicatorFocus="@color/dot_select_color"
    app:dotIndicatorNormal="@color/dot_unselect_color"/>
```

#### Activity:

```java
mBannerView.setAdapter(new BannerAdapter() {
    @Override
    public View getView(int position, View convertView) {//支持用户自定义轮播的View
        ImageView imageView = null;
        //covertView：缓存的View
        if (convertView == null){
            imageView = new ImageView(MainActivity.this);
            //设置图片的填充方式
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
    public int getCount() {//支持用户自定义指示器的圆点个数
        return 5;
    }

    @Override
    public String getBannerDesc(int position) {//支持用户自定义指示器每个ItemView的描述
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
```



## License

BannerView is released under the [Apache 2.0 license](https://github.com/google/gson/blob/master/LICENSE).

```
Copyright 2008 Google Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

