package andytran.dmap_phone;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import core.db.InstructionalGraphicDbAccess;
import timothy.dmap_phone.InstructionalGraphicTimer;

import timothy.dmap_phone.InstructionalGraphic;

public class MainActivity extends ImageManagerActivity {
    private String prefName;
    private String prefToken;
    private String prefIp;
    private String prefPort;
    private String prefFirstUse;
    private String token;
    private String hostIp;
    private int hostPort;

    public static InstructionalGraphicTimer timer;
    private ListView list;
    private GraphicAdapter adapter;
    private ArrayList<InstructionalGraphic> igs;

    int clicks = 0;
    int listPosition = 0;

/*  Creation
 *  ==============================================================================================*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView)findViewById(R.id.listView);
        list.setCacheColorHint(Color.TRANSPARENT);

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
                View c = list.getChildAt(0);
                int topIndex = list.getFirstVisiblePosition();
                int clickedPosition = 0;
                for (int i = 0; i < list.getChildCount(); i++) {
                    if(position-topIndex == i ){
                        clickedPosition = position-topIndex;
                        list.getChildAt(i).setBackgroundColor(Color.BLUE);
                    }else{
                        list.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                    }
                }
                InstructionalGraphic ig = igs.get(position);
                if (timer != null){ //if there's already a timer, stop it first
                    timer.stop();

                }

                timer = new InstructionalGraphicTimer(MainActivity.this, "10.201.149.221", "8080", "abc", ig);
                timer.start();
                if (position != listPosition) //if user clicks different IG, then reset click counter
                    clicks = 0;
                clicks++;
                if (clicks > 0 && clicks % 2 == 0){
                    list.getChildAt(clickedPosition).setBackgroundColor(Color.TRANSPARENT);
                    timer.stop();
                }
                listPosition = position;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //natalie's code
            }
        });
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
        InstructionalGraphic ig = new InstructionalGraphic("img1");
        ig.addImage(1,Integer.toString(R.drawable.images));
//        db.addGraphicToEnd(ig);
//        db.addGraphicToEnd(ig);
//        db.addGraphicToEnd(ig);
//        db.addGraphicToEnd(ig);
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
}
