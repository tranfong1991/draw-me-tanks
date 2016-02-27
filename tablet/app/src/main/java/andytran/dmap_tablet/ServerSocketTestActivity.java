package andytran.dmap_tablet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Enumeration;

public class ServerSocketTestActivity extends AppCompatActivity {
    private TextView portText;
    private TextView ipText;
    private ImageView testImage;
    private BroadcastReceiver receiver = new DMAPBroadcastReceiver();

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
        testImage = (ImageView)findViewById(R.id.test_image);

        ServerSocketTestActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ipText.setText(getIpAddress());
                portText.setText(String.valueOf(DMAPServer.PORT));
            }
        });

        Intent intent = new Intent(this, DMAPIntentService.class);
        startService(intent);

        LocalBroadcastManager.
                getInstance(this).
                registerReceiver(receiver, new IntentFilter(DMAPServer.PACKAGE_NAME));
    }

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
                        ip += "Site Local Address: "
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

    private class DMAPBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extra = intent.getExtras();

            if (extra.getString(DMAPServer.EXTRA_ACTION).equals("play")) {
                Intent i = new Intent(ServerSocketTestActivity.this, TestActivity.class);
                startActivity(i);
            } else {
                String fileName = extra.getString(DMAPServer.EXTRA_GRAPHIC_NAME);

                File file = new File(getFilesDir(), fileName);
                Ion.with(ServerSocketTestActivity.this).load(file).intoImageView(testImage);
            }
        }
    }
}
