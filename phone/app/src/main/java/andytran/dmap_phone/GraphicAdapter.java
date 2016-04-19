package andytran.dmap_phone;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.ArraySwipeAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import timothy.dmap_phone.InstructionalGraphic;

class GraphicAdapter extends ArraySwipeAdapter<InstructionalGraphic> {
    private final Activity context;
    private SwipeLayout swipeLayout;
    ArrayList<InstructionalGraphic> igs;

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.sample1;
    }

    public GraphicAdapter(Activity context, ArrayList<InstructionalGraphic> igs) {
        super(context, R.layout.graphic_item);
        this.context = context;
        this.igs = igs;
    }

    @Override
    public int getCount(){
        return igs.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        swipeLayout =  (SwipeLayout) inflater.inflate(R.layout.graphic_item, null, true);
        //set show mode.
        ImageButton imgButton = (ImageButton) swipeLayout.findViewById(R.id.delete);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("msg3", "What up Karrie");
            }
        });
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


        TextView textView = (TextView) swipeLayout.findViewById(R.id.instruction_name);
        ImageView imageView1 = (ImageView) swipeLayout.findViewById(R.id.instruction_image);

        textView.setText(igs.get(position).getName());
        imageView1.setImageResource(Integer.parseInt(igs.get(position).imageRefAt(0)));
        return swipeLayout;
    }
}
