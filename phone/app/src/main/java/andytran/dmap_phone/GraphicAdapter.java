package andytran.dmap_phone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
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

import core.db.InstructionalGraphicDbAccess;
import timothy.dmap_phone.InstructionalGraphic;

class GraphicAdapter extends ArraySwipeAdapter<InstructionalGraphic> {
    private final Activity context;
    private SwipeLayout swipeLayout;
    ArrayList<InstructionalGraphic> igs;
    private int selectedItem;

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

    public void setSelectedItem(int position) {
        selectedItem = position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        swipeLayout =  (SwipeLayout) inflater.inflate(R.layout.graphic_item, null, true);
        //set show mode.
        ImageButton deleteButton = (ImageButton) swipeLayout.findViewById(R.id.delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Deleting Instruction")
                        .setMessage("Are you sure you want to delete the instruction?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                InstructionalGraphicDbAccess db = new InstructionalGraphicDbAccess(context);
                                db.removeGraphicAt(position);
                                igs.remove(position);
                                if(MainActivity.timer != null) {
                                    MainActivity.timer.stop();
                                }
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        ImageButton editButton = (ImageButton)swipeLayout.findViewById(R.id.edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendModifyIntent(igs.get(position));
                Log.d("msg1", "hello");
            }

            private void sendModifyIntent(InstructionalGraphic ig) {
                InstructionalGraphicChangeRecord record = new InstructionalGraphicChangeRecord(ig);
                Intent intent = new Intent(context, ModifyInstructionalGraphicActivity.class);
                intent.putExtra(InstructionalGraphicChangeRecord.class.getName(), record);
                intent.putExtra(ModifyInstructionalGraphicActivity.isNewIntentCode, String.valueOf(false));

                context.startActivityForResult(intent, 10);
                return;
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
        Picasso.with(context)
                .load(Utils.refToUri(context, igs.get(position).imageRefAt(0)))
                .into(imageView1);
        return swipeLayout;
    }
}
