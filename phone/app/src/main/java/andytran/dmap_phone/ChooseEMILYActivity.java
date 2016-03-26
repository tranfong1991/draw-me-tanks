package andytran.dmap_phone;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;


public class ChooseEMILYActivity extends AppCompatActivity {
    public String ipAddress = "10.0.2.15";
    public int port = 8080;

    Handler updateBarHandler;
    ProgressDialog progressDialog;

    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_emily);

        frameLayout = (FrameLayout)findViewById(R.id.progressBarHolder);


        String[] tabletNames = {"Emily", "James", "Phong", "Clarissa", "Anthony"};
        ListAdapter titleAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tabletNames);
        ListView tabletListView = (ListView)findViewById(R.id.chooseEmilyListView);
        tabletListView.setAdapter(titleAdapter);

        tabletListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String name = String.valueOf(parent.getItemAtPosition(position));
//                        showMessage("Connecting to " + name, "When the connection is complete, the change will switch.");

                        frameLayout = (FrameLayout) findViewById(R.id.progressBarHolder);
                        frameLayout.setVisibility(View.VISIBLE);







                    }
                }
        );



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_load_screen, menu);
        return true;
    }
//
//    public void LaunchRingDialog(View view){
//        final ProgressDialog ringProgressDialog = ProgressDialog.show(ChooseEMILYActivity.this, "Please wait ...", "Downloading Image ...", true);
//}
//
//    public void showMessage(String title,String message){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCancelable(true);
//        builder.setTitle(title);
//        builder.setMessage((message));
//        builder.show();
//    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar graphic_item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void submitData(View view){
//
//        Intent intent = new Intent(this, HamActivity.class);
//        Button buttonText = (Button) findViewById(R.id.button);
//        String message = buttonText.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, message);
//        startActivity(intent);

//        Intent intent = new Intent(this, MainActivity.class);
//        EditText ipAddressText = (EditText)findViewById(R.id.ip_field);
//        EditText portNumberText  = (EditText)findViewById(R.id.port_field);
//        ipAddress = ipAddressText.getText().toString();
//        port = Integer.parseInt(portNumberText.getText().toString());
//
//        startActivity(intent);


    }
}
