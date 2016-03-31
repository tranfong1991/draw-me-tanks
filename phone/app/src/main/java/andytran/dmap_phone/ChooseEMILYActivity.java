package andytran.dmap_phone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class ChooseEMILYActivity extends AppCompatActivity {
    public String ipAddress = "10.0.2.15";
    public int port = 8080;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_emily);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_load_screen, menu);
        return true;
    }

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

        Intent intent = new Intent(this, MainActivity.class);
        EditText ipAddressText = (EditText)findViewById(R.id.ip_field);
        EditText portNumberText  = (EditText)findViewById(R.id.port_field);
        ipAddress = ipAddressText.getText().toString();
        port = Integer.parseInt(portNumberText.getText().toString());

        startActivity(intent);


    }
}
