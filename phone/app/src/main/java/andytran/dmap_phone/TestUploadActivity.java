package andytran.dmap_phone;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;

import timothy.dmap_phone.InstructionalGraphic;

public class TestUploadActivity extends ImageManagerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_upload);

        ip = "192.168.43.75";
        port = "8080";
        token = "abc";

        btnTry = (Button)findViewById(R.id.try_btn);
        btnTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageGallery();
            }
        });

        btnFlush = (Button)findViewById(R.id.flush_btn);
        btnFlush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dest = copyFileToPhone(image_refs.get(image_refs.size() - 1));
                Log.d("REF", dest);
                imageView.setImageURI(Utils.refToUri(TestUploadActivity.this, dest));

                submitImages(new InstructionalGraphic("thing"));
            }
        });

        imageView = (ImageView)findViewById(R.id.imageview_test);
    }

    private Button btnTry;
    private Button btnFlush;
    private ImageView imageView;
}
