package andytran.dmap_phone;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class ClientSocketTestActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 0;

    private ImageView chosenImage;
    private Button chooseButton;
    private Button uploadButton;
    private EditText nameText;
    private EditText ipText;
    private String filePath;
    private ClientNSDHelper nsdHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_socket_test);

        nsdHelper = new ClientNSDHelper(this);
        nsdHelper.initializeNsd();
        nsdHelper.discoverServices();

        chosenImage = (ImageView) findViewById(R.id.img_chosen_pic);
        nameText = (EditText) findViewById(R.id.txt_name);
        ipText = (EditText) findViewById(R.id.txt_ip);
        chooseButton = (Button) findViewById(R.id.btn_choose);
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        uploadButton = (Button) findViewById(R.id.btn_upload);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<NameValuePair> list = new ArrayList<>();
                list.add(new BasicNameValuePair("name", nameText.getText().toString()));
                list.add(new BasicNameValuePair("graphic", filePath));

                String ip = ipText.getText().toString();

                new UploadGraphicAsyncTask("http://" + ip + ":8080/graphic?token=NPXM7UR2197QN375DW1T", list).execute();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            filePath = getRealPathFromURI(selectedImage);
            Ion.with(this).load(filePath).intoImageView(chosenImage);
        }
    }

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        String path = cursor.getString(idx);

        cursor.close();
        return path;
    }

    private class UploadGraphicAsyncTask extends AsyncTask<Void, Void, String>{
        private String url;
        private List<NameValuePair> nameValuePairs;

        public UploadGraphicAsyncTask(String url, List<NameValuePair> nameValuePairs){
            this.url = url;
            this.nameValuePairs = nameValuePairs;
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            try {
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                for(int i=0; i < nameValuePairs.size(); i++) {
                    String name = nameValuePairs.get(i).getName();
                    String value = nameValuePairs.get(i).getValue();

                    if(name.equalsIgnoreCase("graphic"))
                        builder.addPart(name, new FileBody(new File(value)));
                    else builder.addPart(name, new StringBody(value, ContentType.TEXT_PLAIN));
                }

                httpPost.setEntity(builder.build());
                HttpResponse r = httpClient.execute(httpPost);
                ResponseHandler<String> handler = new BasicResponseHandler();
                return handler.handleResponse(r);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String result) {
            Toast.makeText(ClientSocketTestActivity.this, result, Toast.LENGTH_LONG).show();
        }
    }
}
