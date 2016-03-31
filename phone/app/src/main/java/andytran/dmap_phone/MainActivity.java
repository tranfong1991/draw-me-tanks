package andytran.dmap_phone;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import core.DataHolder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

/*  TMP
 *  ==============================================================================================*/
    private class InstructionalGraphic {}


    Button addImage;
    private ArrayAdapter<String> lsAdapter; // @deprecated
    ListView list;
    //String[] items = {"Stay Calm", "Do not stand up", "Follow Me", "Keep your life jacket on"};
    //Integer[] imgid = {R.drawable.images,R.drawable.sitdown,R.drawable.images,R.drawable.images};

/*  Creation
 *  ==============================================================================================*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = (ListView)findViewById(R.id.listView);

        buildListView();

        // below is @deprecated
        //Log.v("LstAdapter", "Inside LstAdapter");
        //GraphicAdapter adapter = new GraphicAdapter(this,items, imgid);
        //list.setAdapter(adapter);

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

/*  Private Methods
 *  ==============================================================================================*/
    private void buildListView() {
        ArrayList<String> names;
        ArrayList<Integer> imageIds;

        for(InstructionalGraphic graphic : DataHolder.instance().getGraphicsListIterator()) {
            names.add(graphic.getName());
            imageIds.add(graphic.idAt(0));
        }

        list.setAdapter(new GraphicAdapter(this, names.toArray(new String[names.size()]), imageIds.toArray(new Integer[imageIds.size()])));
    }

//    private void populateListView(){
//        String[] myItems = {"blue","red"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.graphic_item, myItems);
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
