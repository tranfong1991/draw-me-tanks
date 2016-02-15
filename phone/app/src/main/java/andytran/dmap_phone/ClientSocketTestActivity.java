package andytran.dmap_phone;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.net.Socket;

public class ClientSocketTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_socket_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                Socket socket = null;
                try{
                    socket = new Socket("10.0.2.15", 8080);
                    if(socket.isConnected()){
                        final TextView successText = (TextView)findViewById(R.id.txt_success);
                        ClientSocketTestActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                successText.setText("Success");
                            }
                        });
                    }
                }catch (Exception e){
                    e.printStackTrace();
                } finally {
                    try {
                        if (socket != null)
                            socket.close();
                    } catch(IOException e){
                        e.printStackTrace();
                    }
                }
                return null;
            }
        }.execute();
    }

}
