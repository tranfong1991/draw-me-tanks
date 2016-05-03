package andytran.dmap_phone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;

import com.android.volley.Request;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.ArraySwipeAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import core.db.InstructionalGraphicDbAccess;
import timothy.dmap_phone.InstructionalGraphic;

class GraphicAdapter extends ArraySwipeAdapter<InstructionalGraphic> {
    private final Activity context;
    private SwipeLayout swipeLayout;
    ArrayList<InstructionalGraphic> igs;
    private int selectedItem;

    private String ip;
    private String port;
    private String token;

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.sample1;
    }

    public GraphicAdapter(Activity context, ArrayList<InstructionalGraphic> igs, String ip, String port, String token) {
        super(context, R.layout.graphic_item);
        this.context = context;
        this.igs = igs;
        this.selectedItem = -1;
        this.ip = ip;
        this.port = port;
        this.token = token;
    }

    @Override
    public int getCount(){
        return igs.size();
    }

    public void setSelectedItem(int position) {
        selectedItem = position;
    }

    public void setIp(String ip){
        this.ip = ip;
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
                if (MainActivity.timer != null) try {
                    MainActivity.timer.stop();
                } catch (Error err) {
                    Utils.error(context, err.getMessage()).show();
                }
                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Deleting " + igs.get(position).getName())
                        .setMessage("Are you sure you want to delete this instruction?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                InstructionalGraphic graphic = igs.get(position);
                                HashMap<String, String> params = new HashMap<>();
                                params.put("token", token);

                                String url;
                                for(int i = 0; i<graphic.numOfFrames(); i++){
                                    params.put("id", String.valueOf(graphic.idAt(i)));
                                    url = Utils.buildURL(ip, port, "graphic", params);
                                    Utils.sendPackage(context.getApplicationContext(), Request.Method.DELETE, url, null, null);
                                }

                                InstructionalGraphicDbAccess db = new InstructionalGraphicDbAccess(context);
                                db.removeGraphicAt(position);
                                igs.remove(position);
                                if (MainActivity.timer != null) try {
                                    MainActivity.timer.stop();
                                } catch (Error err) {
                                    Utils.error(context, err.getMessage()).show();
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
                if (MainActivity.timer != null) try {
                    MainActivity.timer.stop();
                } catch (Error err) {
                    Utils.error(context, err.getMessage()).show();
                }
                sendModifyIntent(igs.get(position));
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
                .resize(100,100)
                .centerInside()
                .onlyScaleDown()
                .into(imageView1);

        if(selectedItem == position)
            setColor(swipeLayout.findViewById(R.id.surface_layout), true);//swipeLayout.setBackgroundResource(R.drawable.selected_rectangle); //ContextCompat.getColor(parent.getContext(), R.color.colorPrimary));
        return swipeLayout;
    }

    void setColor(View view, Boolean selected) {
        if(selected) {
            view.setBackgroundResource(R.drawable.selected_rectangle);
        } else {
            view.setBackgroundResource(R.drawable.white_rectangle);
        }
        view.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        Integer dim = pixelToDp(10);
        view.setPadding(dim, dim, dim, dim);
    }

    int pixelToDp(Integer pixel) {
        return (int) (context.getResources().getDisplayMetrics().density*pixel + 0.5f);
    }
}
