package andytran.dmap_phone;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.daimajia.swipe.SwipeLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = (ListView)findViewById(R.id.listView);
        //Log.v("LstAdapter", "Inside LstAdapter");
        GraphicAdapter adapter = new GraphicAdapter(this,items, imgid);
        list.setAdapter(adapter);

//        ListView listView = (ListView) findViewById(R.id.listView); this is the code that works
//        ArrayList<String> itemList = new ArrayList<String>();
//        itemList.addAll(Arrays.asList(items));
//        lsAdapter = new ArrayAdapter<String>(this,R.layout.graphic_item, R.id.textView1, items);  this is the code that works
//        listView.setAdapter(lsAdapter);

        //populateListView();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

}
