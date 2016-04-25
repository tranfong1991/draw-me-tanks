package andytran.dmap_phone;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import timothy.dmap_phone.InstructionalGraphic;
import timothy.dmap_phone.InstructionalGraphicTimer;

public class TestUploadActivity extends ImageManagerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_upload);

        ip = "http://192.168.43.75";
        port = "8080";
        token = "s3kKLfxsUvkh1qhs2K3q";

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
                //String dest = copyFileToPhone(image_refs.get(image_refs.size() - 1));
                //Log.d("REF", dest);
                //imageView.setImageURI(Utils.refToUri(TestUploadActivity.this, dest));

                InstructionalGraphic ig = new InstructionalGraphic("thing");
                ig.setInterval(3000);
                submitImages(ig);
            }
        });
        btnCheckDB = (Button)findViewById(R.id.check_db);
        btnCheckDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DB Size", String.valueOf(db.getNumberOfGraphics()));
                InstructionalGraphic igg = db.getGraphicByName("thing");
                //Log.d("Graphic:",db.getGraphicByName("thing").getName());
                //Log.d("Graphic:",String.valueOf(db.getGraphicByName("thing").numOfFrames()));
                Log.d("Graphic", igg.idAt(0).toString());
                Log.d("Graphic", igg.idAt(1).toString());
                Log.d("Graphic", igg.imageRefAt(0).toString());

                InstructionalGraphicTimer timer = new InstructionalGraphicTimer(TestUploadActivity.this, ip, port, token, igg);
                timer.start();

            }
        });

        imageView = (ImageView)findViewById(R.id.imageview_test);
    }

    private Button btnTry;
    private Button btnFlush;
    private Button btnCheckDB;
    private ImageView imageView;
}
