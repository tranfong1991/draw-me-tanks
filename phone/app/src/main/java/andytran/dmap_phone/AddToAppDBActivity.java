package andytran.dmap_phone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import core.db.InstructionalGraphicDbAccess;
import timothy.dmap_phone.InstructionalGraphic;

//This module is responsible for storing raw image data so image retrieval is simplified for both the phone and tablet apps.  This module does not interact with InstructionalGraphics.  Its only responsibility is the storage of data on both devices.
//
//        Inputs:
//        File: Raw image data, which could be of either the PNG or GIF data types.  This will use Java’s File type.
//
//        Outputs:
//        File reference: A string representing the path to the file.  All other modules interact with the image via the file reference produced by this module.
//
//        Structures:
//        Android File data structure: Android natively has a data structure meant for handling files.  This data structure accounts for the file type (PNG or GIF) automatically.


//public class AddToAppDBActivity extends AppCompatActivity{
public class AddToAppDBActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int RESULT_LOAD_IMAGE = 1;
    ImageView imageToUpload;
    Button uploadButton;
    TextView pathURI;
    InstructionalGraphicDbAccess db = new InstructionalGraphicDbAccess(this); //initialize database
    InstructionalGraphic ig = new InstructionalGraphic("ig1");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_app_db);

        imageToUpload = (ImageView)findViewById(R.id.imageToUpload);
        uploadButton = (Button)findViewById(R.id.uploadButton);
        pathURI = (TextView)findViewById(R.id.path);
//
        imageToUpload.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.imageToUpload:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                break;
            case R.id.uploadButton:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//
//        InstructionalGraphicDbAccess db = new InstructionalGraphicDbAccess(this); //initialize database
//        InstructionalGraphic ig = new InstructionalGraphic("ig1");
//        ig.addImage(1,Integer.toString(R.drawable.images));
//        db.addGraphicToEnd(ig);
//        igs = db.getOrderedGraphicList(); // get all InstructionalGraphics in database
//        adapter = new GraphicAdapter(this, igs);
//        list.setAdapter(adapter); //build the listview with the adapter
        try {
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
                String dest_path_name = Utils.generateRandomString(10);
                Uri selectedImage = data.getData();
                final File file = new File(selectedImage.getPath());
                File dest_file = new File(dest_path_name);

                pathURI.setText(selectedImage.getPath());
                //            imageToUpload.setImageURI(selectedImage);

                if (file.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    imageToUpload.setImageBitmap(myBitmap);
                }

                copyFile(file, dest_file);

                db.addGraphicToEnd(ig);

                ig.addImage(1, dest_file.getAbsolutePath());
                //@TODO: ask how to add
                //make a copy of the File
                //save the File
                //Save into app/folder (?!)
                //add to the app database
            }
        }
        catch (IOException e){
            System.err.println("Caught IOException: " +  e.getMessage());
        }

    }

    public void copyFile(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
