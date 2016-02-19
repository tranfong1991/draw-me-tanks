package andytran.dmap_tablet;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
    private ServerSocket serverSocket;
    private TextView portText;
    private TextView ipText;
    private TextView statusText;

    private Server server;

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

        try {
            server = new Server();
        } catch (IOException e){
            Toast.makeText(this, "Web server could not start.", Toast.LENGTH_LONG).show();
        }
        Toast.makeText(this, "Web server initialized.", Toast.LENGTH_LONG).show();
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        if(server != null) {
//            server.stop();
//            Toast.makeText(this, "Web server stopped.", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        try {
//            server.start();
//        } catch (IOException e) {
//            Toast.makeText(this, "Unable to restart server.", Toast.LENGTH_LONG).show();
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(server != null){
            server.stop();
            Toast.makeText(this, "Web server destroyed.", Toast.LENGTH_LONG).show();
        }
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
