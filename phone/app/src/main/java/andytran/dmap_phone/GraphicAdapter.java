package andytran.dmap_phone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.ImageButton;
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
    static class ViewHolder{
        ImageButton deleteBtn;
        ImageButton editBtn;
        ImageView graphicImage;
        TextView graphicText;
    }

    private Context context;
    ArrayList<InstructionalGraphic> igs;

    public GraphicAdapter(Context context, ArrayList<InstructionalGraphic> igs) {
        super(context, -1, igs);
        this.context = context;
        this.igs = igs;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.sample1;
    }

    @Override
    public int getCount(){
        return igs.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        SwipeLayout rowView = (SwipeLayout)convertView;

        if(rowView == null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView =  (SwipeLayout) inflater.inflate(R.layout.graphic_item, null, true);

            ViewHolder holder = new ViewHolder();
            holder.deleteBtn = (ImageButton) rowView.findViewById(R.id.delete);
            holder.editBtn = (ImageButton) rowView.findViewById(R.id.edit);
            holder.graphicImage = (ImageView) rowView.findViewById(R.id.instruction_image);
            holder.graphicText = (TextView) rowView.findViewById(R.id.instruction_name);

            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(context)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Deleting Instruction")
                            .setMessage("Are you sure you want to delete the instruction?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    InstructionalGraphic graphic = igs.get(position);
                                    HashMap<String, String> params = new HashMap<>();
                                    String url = null;
                                    for(int i = 0; i<graphic.numOfFrames(); i++){
                                        params.put("id", String.valueOf(graphic.idAt(i)));
//                                    url = Utils.buildURL();
                                        Utils.sendPackage(context, Request.Method.DELETE, url, null, null);
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

            holder.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MainActivity.timer != null) try {
                        MainActivity.timer.stop();

                        MainActivity activity = (MainActivity)context;
                        activity.sendModifyIntent(igs.get(position));
                    } catch (Error err) {
                        Utils.error(context, err.getMessage()).show();
                    }
                }
            });
            rowView.setShowMode(SwipeLayout.ShowMode.LayDown);
            rowView.addDrag(SwipeLayout.DragEdge.Right, rowView.findViewById(R.id.bottom_wrapper));
            rowView.addSwipeListener(new SwipeLayout.SwipeListener() {
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

            rowView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder)rowView.getTag();

        holder.graphicText.setText(igs.get(position).getName());
        Picasso.with(context)
                .load(Utils.refToUri(context, igs.get(position).imageRefAt(0)))
                .into(holder.graphicImage);

        return rowView;
    }
}
