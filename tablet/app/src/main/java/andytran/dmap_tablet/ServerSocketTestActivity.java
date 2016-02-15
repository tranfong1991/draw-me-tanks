package andytran.dmap_tablet;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

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

        new ServerSocketAsync().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(serverSocket != null){
            try {
                serverSocket.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    private class ServerSocketAsync extends AsyncTask<Void, Void, Void>{
        static final int SocketServerPORT = 8080;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                serverSocket = new ServerSocket(SocketServerPORT);
                ServerSocketTestActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        portText.setText("Waiting for connection in " + serverSocket.getLocalPort());
                    }
                });

                while(true){
                    serverSocket.accept();
                    ServerSocketTestActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            statusText.setText("Success");
                        }
                    });
                }
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
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
