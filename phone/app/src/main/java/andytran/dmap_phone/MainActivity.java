package andytran.dmap_phone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import core.db.InstructionalGraphicDbAccess;
import core.db.InstructionalGraphicDbHelper;
import timothy.dmap_phone.InstructionalGraphicTimer;

import timothy.dmap_phone.InstructionalGraphic;

public class MainActivity extends ImageManagerActivity {
    static private final int MODIFY_IG_REQUEST_CODE = 10;
    static public InstructionalGraphicTimer timer;

    private String prefName;
    private String prefToken;
    private String prefIp;
    private String prefPort;
    private String prefFirstUse;
    private String token;
    private String hostIp;
    private int hostPort;

    private ListView list;
    private GraphicAdapter adapter;
    private ArrayList<InstructionalGraphic> igs;

/*  Creation
 *  ==============================================================================================*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefName = getResources().getString(R.string.pref_name);
//        prefToken = getResources().getString(R.string.pref_token);
//        prefIp = getResources().getString(R.string.pref_ip);
//        prefPort = getResources().getString(R.string.pref_port);
//        prefFirstUse = getResources().getString(R.string.pref_first_use);
//
        SharedPreferences pref = getSharedPreferences(prefName, 0);
//        token = pref.getString(prefToken, null);
//        hostIp = pref.getString(prefIp, null);
//        hostPort = pref.getInt(prefPort, 0);

        list = (ListView)findViewById(R.id.listView);
        boolean isFirstTime = pref.getBoolean(prefFirstUse, true);
        if(isFirstTime) {
            loadDefaultGraphics();

            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean(prefFirstUse, false);
            editor.apply();
        }

        buildListView();
        list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int curPosition = list.getCheckedItemPosition();

                Log.d("ListView", String.valueOf(curPosition));
                if(curPosition == AdapterView.INVALID_POSITION){
                    Log.d("Listview", "if 1");
                    list.setItemChecked(position, true);
                    view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                } else if(curPosition == position) {
                    Log.d("Listview", "if 2");
                    list.setItemChecked(position, false);
                    view.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                } else {
                    Log.d("Listview", "if 3");
                    list.setItemChecked(curPosition, false);
                    list.setItemChecked(position, true);
                    list.getChildAt(curPosition).setBackgroundColor(getResources().getColor(R.color.colorWhite));
                    view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }

//                InstructionalGraphic ig = igs.get(position);
//                if (timer != null) try {
//                    //if there's already a timer, stop it first
//                    timer.stop();
//                } catch (Error err) {
//                    Utils.error(MainActivity.this, err.getMessage()).show();
//                }
//
//                timer = new InstructionalGraphicTimer(MainActivity.this, ip, port, token, ig);
//                final int forPosition = position;
//                timer.setOnSendSuccess(new IntegerCallback() {
//                    @Override
//                    public void run(Integer n) {
//                        setImageRefFor(forPosition, n); // preview current frame in ListView
//                    }
//                });
//                timer.setOnStopSuccess(new VoidCallback() {
//                    @Override
//                    public void run() {
//                        setImageRefFor(forPosition, 0);
//                    }
//                });
//                timer.start();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timer != null) try {
                    timer.stop();
                    sendModifyIntent(new InstructionalGraphic("Test Name"));
                } catch (Error err) {
                    Utils.error(MainActivity.this, err.getMessage()).show();
                }
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
        return super.onOptionsItemSelected(item);
    }

/*  Private Methods
 *  ==============================================================================================*/
    private void buildListView() {
        InstructionalGraphicDbAccess db = new InstructionalGraphicDbAccess(this); //initialize database
        igs = db.getOrderedGraphicList(); // get all InstructionalGraphics in database
        adapter = new GraphicAdapter(this, igs);
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
                            .into(imageView);
                }
            });
        }
    }
}
