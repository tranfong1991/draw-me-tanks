package andytran.dmap_tablet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ImageView graphicView;
    private Map<Integer, String> mapping;
    private GraphicDbHelper dbHelper = new GraphicDbHelper(this);
    private BroadcastReceiver receiver = new DMAPBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        graphicView = (ImageView)findViewById(R.id.graphic_view);
        mapping = new HashMap<>();
        populateMappingFromDb();

        LocalBroadcastManager.
                getInstance(this).
                registerReceiver(receiver, new IntentFilter(DMAPServer.PACKAGE_NAME));
    }

    private void populateMappingFromDb(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                GraphicContract.GraphicEntry._ID,
                GraphicContract.GraphicEntry.COLUMN_GRAPHIC_PATH
        };
        Cursor cursor = db.query(GraphicContract.GraphicEntry.TABLE_NAME, projection, null, null, null, null, null);

        //check if table is empty
        if(!cursor.moveToFirst())
            return;

        do{
            int id = cursor.getInt(cursor.getColumnIndex(GraphicContract.GraphicEntry._ID));
            String path = cursor.getString(cursor.getColumnIndex(GraphicContract.GraphicEntry.COLUMN_GRAPHIC_PATH));

            mapping.put(id, path);
        }while(cursor.moveToNext());
    }

    private class DMAPBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extra = intent.getExtras();

            if (extra.getString(DMAPServer.EXTRA_ACTION).equals("play")) {

            } else {
                String fileName = extra.getString(DMAPServer.EXTRA_GRAPHIC_NAME);

                File file = new File(getFilesDir(), fileName);
                Ion.with(MainActivity.this).load(file).intoImageView(graphicView);
            }
        }
    }
}
