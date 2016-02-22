package andytran.dmap_tablet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Enumeration;

public class ServerSocketTestActivity extends AppCompatActivity {
    private TextView portText;
    private TextView ipText;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_socket_test);
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

        portText = (TextView)findViewById(R.id.txt_port);
        ipText = (TextView)findViewById(R.id.txt_ip);
        statusText = (TextView)findViewById(R.id.txt_status);

        ServerSocketTestActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ipText.setText(getIpAddress());
            }
        });

        Intent intent = new Intent(this, DMAPIntentService.class);
        startService(intent);

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                statusText.setText("I receive it.");
            }
        }, new IntentFilter("testActivity"));

//        try {
//            DMAPServer server = new DMAPServer(this);
//        }catch(IOException e){
//            e.printStackTrace();
//        }
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        if(DMAPServer != null) {
//            DMAPServer.stop();
//            Toast.makeText(this, "Web DMAPServer stopped.", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        try {
//            DMAPServer.start();
//        } catch (IOException e) {
//            Toast.makeText(this, "Unable to restart DMAPServer.", Toast.LENGTH_LONG).show();
//        }
//    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e) {
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }
}
