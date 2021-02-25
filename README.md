# BannerView

BannerView是一个Android自定义轮播图控件，可以自定义放置轮播的内容、指示器中点的个数，轮播图Item的描述，还有点击监听事件等等。

BannerView可以自适应高度，只需要你根据图片的比例，去设置BannerView的withProportion和heightProportion属性。

```xml
app:withProportion="8"
app:heightProportion="3"
```

一般8:3能有一个较为常见的轮播图效果,但如果图片是正方形就不太适合这个比例

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
	implementation 'com.github.LuoSPro:BannerView:1.0.2'
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
##### kotlin用法

```kotlin
class MainActivity : BaseSkinActivity() {

    private lateinit var mHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mHandler = Handler(Looper.myLooper()!!)
        mHandler.postDelayed(Runnable { showListData() }, 50)
    }

    private fun showListData() {
        banner_view.setAdapter(object : BannerAdapter() {
            override fun getView(position: Int, convertView: View?): View {//支持用户自定义轮播的View
                val imageView: ImageView = if (convertView == null) {
                    ImageView(this@MainActivity)
                } else {
                    //covertView：缓存的View
                    convertView as ImageView
                }
                imageView.scaleType = ImageView.ScaleType.FIT_XY
                imageView.setImageResource(R.drawable.ic_launcher_background)
                return imageView
            }

            //支持用户自定义指示器的圆点个数
            override fun getCount() = 5

            //支持用户自定义指示器每个ItemView的描述
            override fun getBannerDesc(position: Int): String {
                return "哈哈哈哈"
            }
        })
        //开启自动滚动
        banner_view.startRoll()
        //设置监听
        banner_view.setBannerItemClickListener {
            Toast.makeText(this@MainActivity, "current position $it", Toast.LENGTH_SHORT)
                .show()
        }
    }
}

```

##### java用法

```java
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

```
### 注意：
使用BannerView的时候，一定要用Handler的postDelayed方法去开启，并且设置延时大于50毫秒以上

原因：因为View的绘制顺序和Activity的生命周期的问题，当Activity的`onCreate()`方法和`onResume()`方法执行的时候，View还没开始测量，又由于我们设置了BannerView为自适应高度（Layout布局里面设置的是`wrap_content`属性），在Activity的onCreate()方法去初始化BannerView的时候，去测量他的宽度的为0，导致BannerView的高度也会为0，所以就不能显示BannerView了。
详情请看我的这篇文章：[【Android源码】View的绘制流程](https://www.jianshu.com/p/1feb9ca20667)
