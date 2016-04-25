package andytran.dmap_phone;

import android.content.SharedPreferences;
import android.graphics.Color;
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

public class MainActivity extends AppCompatActivity {
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
        prefToken = getResources().getString(R.string.pref_token);
        prefIp = getResources().getString(R.string.pref_ip);
        prefPort = getResources().getString(R.string.pref_port);
        prefFirstUse = getResources().getString(R.string.pref_first_use);

        SharedPreferences pref = getSharedPreferences(prefName, 0);
        token = pref.getString(prefToken, null);
        hostIp = pref.getString(prefIp, null);
        hostPort = pref.getInt(prefPort, 0);

        boolean isFirstTime = pref.getBoolean(prefFirstUse, true);
        if(isFirstTime) {
            getDefaultGraphics();

            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean(prefFirstUse, false);
            editor.apply();
        }

        buildListView();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View c = list.getChildAt(0);
                int scrolly = -c.getTop() + list.getFirstVisiblePosition() * c.getHeight();
                int topIndex = list.getFirstVisiblePosition();

                Log.d("scrolly", Integer.toString(scrolly));
                Log.d("topIndex", Integer.toString(topIndex));
                Log.d("position",Integer.toString(position));
                for (int i = 0; i < list.getChildCount(); i++) {
                    if(position-topIndex == i ){
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
//                if (position != listPosition) //if user clicks different IG, then reset click counter
//                    clicks = 0;
//                clicks++;
//                if (clicks > 0 && clicks % 2 == 0)
//                    timer.stop();
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
        //adapter.setSelectedItem(1);
        list.setAdapter(adapter); //build the listview with the adapted
    }

    private void getDefaultGraphics(){

    }
}
