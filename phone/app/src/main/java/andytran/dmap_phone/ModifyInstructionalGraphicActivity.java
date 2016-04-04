package andytran.dmap_phone;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;

import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;

/* import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.support.v4.view.ViewPager; */

public class ModifyInstructionalGraphicActivity extends Activity {

    private static final float NUMBER_INITIAL_ITEMS = 1.5F;

    private LinearLayout mCarouselContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_instructional_graphic);
        mCarouselContainer = (LinearLayout) findViewById(R.id.carousel);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Compute width of carousel item based on screen dimensions and initial item count
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int imageWidth = (int) (displayMetrics.widthPixels/NUMBER_INITIAL_ITEMS);

        /*ImageView imageItem;
        for(int i = 0; i < mockGraphicDatabase.length; ++i) {
            imageItem = new ImageView(this);
            imageItem.setImageResource(mockGraphicDatabase[i]);
            imageItem.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageWidth));

            mCarouselContainer.addView(imageItem);
        }*/
    }

    /*
    public class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mResources.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView.setImageResource(mResources[position]);

            container.addView(itemView);

            return itemView;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    private GoogleApiClient client;

    int[] mResources = {
            R.drawable.calm,
            R.drawable.cleat_finished,
            R.drawable.dontstand,
            R.drawable.dosit,
            R.drawable.leftboat,
            R.drawable.rightboat
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_instructional_graphic);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        CustomPagerAdapter mCustomPagerAdapter = new CustomPagerAdapter(this);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mCustomPagerAdapter);
    }

    public void doStuff(View view) {


    }*/


}
