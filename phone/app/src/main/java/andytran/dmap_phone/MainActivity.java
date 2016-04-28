package andytran.dmap_phone;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import core.db.InstructionalGraphicDbAccess;
import timothy.dmap_phone.InstructionalGraphicTimer;

import timothy.dmap_phone.InstructionalGraphic;

public class MainActivity extends ImageManagerActivity implements ChangeIPDiaglogFragment.ChangeIPListener{
    static private final int MODIFY_IG_REQUEST_CODE = 10;
    static public InstructionalGraphicTimer timer;

    private ListView list;
    private GraphicAdapter adapter;
    private ArrayList<InstructionalGraphic> igs;

    private int clicks;
    private int listPosition;

/*  Creation
 *  ==============================================================================================*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView)findViewById(R.id.listView);

        SharedPreferences pref = getSharedPreferences(prefName, 0);
        boolean isFirstTime = pref.getBoolean(prefFirstUse, true);
        if(isFirstTime) {
            loadDefaultGraphics();

            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean(prefFirstUse, false);
            editor.apply();
        }

        buildListView();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int clickedPosition = 0;
                for (int i = 0; i < list.getChildCount(); i++) {
                    if (position == i) {
                        adapter.setColor(list.getChildAt(i).findViewById(R.id.surface_layout), true);
                    } else{
                        adapter.setColor(list.getChildAt(i).findViewById(R.id.surface_layout), false);
                    }
                }
//                InstructionalGraphic ig = igs.get(position);
//                if (timer != null){ //if there's already a timer, stop it first
//                    timer.stop();
//
//                }
//
//                timer = new InstructionalGraphicTimer(MainActivity.this, "10.201.149.221", "8080", "abc", ig);
//                timer.start();
//                if (position != listPosition) //if user clicks different IG, then reset click counter
//                    clicks = 0;
//                clicks++;
//                if (clicks > 0 && clicks % 2 == 0){
//                    list.getChildAt(clickedPosition).setBackgroundColor(Color.TRANSPARENT);
//                    timer.stop();
//                }

                InstructionalGraphic ig = igs.get(position);
                if (timer != null) try { //if there's already a timer, stop it first
                    timer.stop();
                } catch (Error err) {
                    Utils.error(MainActivity.this, err.getMessage()).show();
                }

                //  TIMER
                timer = new InstructionalGraphicTimer(MainActivity.this, ip, port, token, ig);
                final int forPosition = position;
                timer.setOnSendSuccess(new IntegerCallback() {
                    @Override
                    public void run(Integer n) {
                        setImageRefFor(forPosition, n); // preview current frame in ListView
                    }
                });
                timer.setOnStopSuccess(new VoidCallback() {
                    @Override
                    public void run() {
                        setImageRefFor(forPosition, 0);
                    }
                });
                timer.start();
                //  END TIMER

                if (position != listPosition) //if user clicks different IG, then reset click counter
                    clicks = 0;
                clicks++;
                if (clicks > 0 && clicks % 2 == 0) {
                    //list.getChildAt(clickedPosition).findViewById(R.id.surface_layout).setBackgroundResource(R.drawable.white_rectangle);
                    adapter.setColor(list.getChildAt(clickedPosition).findViewById(R.id.surface_layout), false);
                    if (timer != null) try {
                        timer.stop();
                    } catch (Error err) {
                        Utils.error(MainActivity.this, err.getMessage()).show();
                    }
                }
                listPosition = position;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timer != null) try {
                    timer.stop();
                } catch (Error err) {
                    Utils.error(MainActivity.this, err.getMessage()).show();
                }
                sendModifyIntent(new InstructionalGraphic("Test Name"));
            }
        });
    }

    public void sendModifyIntent(InstructionalGraphic ig) {
        InstructionalGraphicChangeRecord record = new InstructionalGraphicChangeRecord(ig);
        Intent intent = new Intent(this, ModifyInstructionalGraphicActivity.class);
        intent.putExtra(ModifyInstructionalGraphicActivity.isNewIntentCode, String.valueOf(Boolean.TRUE));
        intent.putExtra(InstructionalGraphicChangeRecord.class.getName(), record);

        startActivityForResult(intent, MODIFY_IG_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        igs = new InstructionalGraphicDbAccess(this).getOrderedGraphicList();
        adapter.igs = igs;
        adapter.notifyDataSetChanged();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    finish();
                    startActivity(getIntent());
                } else {
                    recreate();
                }
            }
        }, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_change_ip: {
                DialogFragment dialog = new ChangeIPDiaglogFragment();
                dialog.show(getFragmentManager(), "Change IP");
                return true;
            }
            case R.id.action_request_token: {
                String url = Utils.buildURL(ip, port, "generate", null);
                Utils.sendPackage(this, Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            String t = json.getString("token");

                            token = t;

                            SharedPreferences pref = getSharedPreferences(prefName, 0);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString(prefToken,t);
                            editor.apply();

                            Toast.makeText(MainActivity.this, "Successfully retreived token.", Toast.LENGTH_SHORT).show();
                        }catch(JSONException e){
                            Toast.makeText(MainActivity.this, "Cannot request token.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, null);
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

/*  Private Methods
 *  ==============================================================================================*/
    private void buildListView() {
        InstructionalGraphicDbAccess db = new InstructionalGraphicDbAccess(this); //initialize database
        igs = db.getOrderedGraphicList(); // get all InstructionalGraphics in database
        adapter = new GraphicAdapter(this, igs, ip, port, token);
        list.setAdapter(adapter); //build the listview with the adapted
    }

    private void loadDefaultGraphics(){
        InstructionalGraphic ig = new InstructionalGraphic("Tie Cleat Hitch");
        ig.addImage(1, copyFromDrawable(R.drawable.cleat0));
        ig.addImage(2, copyFromDrawable(R.drawable.cleat1));
        ig.addImage(3, copyFromDrawable(R.drawable.cleat2));
        ig.addImage(4, copyFromDrawable(R.drawable.cleat3));
        ig.addImage(5, copyFromDrawable(R.drawable.cleat4));
        ig.addImage(6, copyFromDrawable(R.drawable.cleat5));
        ig.addImage(7, copyFromDrawable(R.drawable.cleat6));
        ig.addImage(8, copyFromDrawable(R.drawable.cleat7));
        ig.addImage(9, copyFromDrawable(R.drawable.cleat8));

        ig.setInterval(2000);
        db.addGraphicToEnd(ig);

        InstructionalGraphic ig1 = new InstructionalGraphic("Don't Stand / Do Sit");
        ig1.addImage(10, copyFromDrawable(R.drawable.dontstand));
        ig1.addImage(11, copyFromDrawable(R.drawable.dosit));

        ig1.setInterval(2000);
        db.addGraphicToEnd(ig1);

        InstructionalGraphic ig2 = new InstructionalGraphic("Steer Left");
        ig2.addImage(12, copyFromDrawable(R.drawable.leftboat));
        db.addGraphicToEnd(ig2);

        InstructionalGraphic ig3 = new InstructionalGraphic("Steer Right");
        ig3.addImage(13, copyFromDrawable(R.drawable.rightboat));
        db.addGraphicToEnd(ig3);

        InstructionalGraphic ig4 = new InstructionalGraphic("Stop");
        ig4.addImage(14, copyFromDrawable(R.drawable.stop));
        db.addGraphicToEnd(ig4);
    }

    private void setImageRefFor(final int position, final Integer frame) {
        View slider = Utils.getViewByPosition(position, list);
        if(slider != null) {
            final ImageView imageView = (ImageView) slider.findViewById(R.id.instruction_image);
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Picasso.with(MainActivity.this)
                            .load(Utils.refToUri(MainActivity.this, igs.get(position).imageRefAt(frame)))
                            .resize(50,50)
                            .onlyScaleDown()
                            .into(imageView);
                }
            });
        }
    }

    @Override
    public void onChangeIP(String newIP) {
        this.ip = newIP;
    }
}
