package andytran.dmap_phone;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.util.Log;

import java.lang.reflect.Array;

class CustomAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] arr;
    private final Integer[] imgid;
//    Context c;
//    String[] items;
//    int [] images;
//    int layoutResourceId;
//    LayoutInflater inflater;
    public CustomAdapter(Activity context, String[] arr, Integer[] imgid) {
        super(context, R.layout.item, arr);
       // inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.arr = arr;
        this.imgid = imgid;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.item, null, true);

        TextView textView = (TextView) rowView.findViewById(R.id.textView1);
        ImageView imageView1 = (ImageView) rowView.findViewById(R.id.imageView1);
        ImageView imageView2 = (ImageView) rowView.findViewById(R.id.imageView2);


        textView.setText(arr[position]);
        imageView1.setImageResource(imgid[position]);
        return rowView;


        //View customView = inflater.inflate(R.layout.item, parent, false);

//        String singleItem = getItem(position);
//        TextView text = (TextView) customView.findViewById(R.id.textView1);
//        ImageView image1 = (ImageView) customView.findViewById(R.id.imageView1);
//        ImageView image2 = (ImageView) customView.findViewById(R.id.imageView2);
//
//        text.setText("Hello");

        //return customView;
    }
}
