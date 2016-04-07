package andytran.dmap_phone;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ImageView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.ArraySwipeAdapter;

class GraphicAdapter extends ArraySwipeAdapter<String> {
    private final Activity context;
    private final String[] arr;
    private final Integer[] imgid;
    private SwipeLayout swipeLayout;

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.sample1;
    }

    public GraphicAdapter(Activity context, String[] arr, Integer[] imgid) {
        super(context, R.layout.graphic_item, arr);
        this.context = context;
        this.arr = arr;
        this.imgid = imgid;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        swipeLayout =  (SwipeLayout) inflater.inflate(R.layout.graphic_item, null, true);
        //set show mode.
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

        //add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
        swipeLayout.addDrag(SwipeLayout.DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper));
        swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                //when the SurfaceView totally cover the BottomView.
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                //you are swiping.
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {
                //when the BottomView totally show.
            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                //when user's hand released.
            }
        });

        TextView textView = (TextView) swipeLayout.findViewById(R.id.textView1);
        ImageView imageView1 = (ImageView) swipeLayout.findViewById(R.id.imageView1);

        textView.setText(arr[position]);
        imageView1.setImageResource(imgid[position]);
        return swipeLayout;
    }
}
