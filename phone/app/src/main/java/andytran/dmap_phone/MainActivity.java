package andytran.dmap_phone;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.content.Intent;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.util.Log;
public class MainActivity extends AppCompatActivity {
    Button addImage;
    private ArrayAdapter<String> lsAdapter;
    ListView list;
    String[] items = {"Stay Calm", "Do not stand up", "Follow Me", "Keep your life jacket on"};
    Integer[] imgid = {R.drawable.images,R.drawable.sitdown,R.drawable.images,R.drawable.images};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Log.v("LstAdapter", "Inside LstAdapter");
        CustomAdapter adapter = new CustomAdapter(this,items, imgid);
        list = (ListView)findViewById(R.id.listView);
        list.setAdapter(adapter);

//        ListView listView = (ListView) findViewById(R.id.listView); this is the code that works
//        ArrayList<String> itemList = new ArrayList<String>();
//        itemList.addAll(Arrays.asList(items));
//        lsAdapter = new ArrayAdapter<String>(this,R.layout.item, R.id.textView1, items);  this is the code that works
//        listView.setAdapter(lsAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //populateListView();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

//    private void populateListView(){
//        String[] myItems = {"blue","red"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item, myItems);
//        ListView list = (ListView) findViewById(R.id.image)
//    }



//    public void addImage() {
//        addImage = (Button) findViewById(R.id.addImage);
//        addImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(MainActivity.this, AddImage.class);
//                startActivity(i);
//            }
//        });
//    }

}
