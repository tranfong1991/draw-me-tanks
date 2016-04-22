package andytran.dmap_phone;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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



public class ImageManagerActivity extends AppCompatActivity implements View.OnClickListener {

    static ArrayList<String> images = new ArrayList<>();
    static ArrayList<Integer> test_ints = new ArrayList<>();
    static ArrayList<Uri> image_refs = new ArrayList<>();


    protected static final int PICK_IMAGE = 0;

//    ImageView imageToUpload;
//    Button uploadButton;
//    TextView pathURI;
    InstructionalGraphicDbAccess db = new InstructionalGraphicDbAccess(this); //initialize database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_app_db);
//        imageToUpload = (ImageView) findViewById(R.id.imageToUpload);
//        uploadButton = (Button) findViewById(R.id.uploadButton);
//        pathURI = (TextView) findViewById(R.id.path);
////
//        imageToUpload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageToUpload:
//                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);

                openImageGallery();


                break;
            case R.id.uploadButton:
                break;
        }
    }


    public void openImageGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        this.startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            image_refs.add(selectedImage);
        }
    }


    private void copyFile(Uri src, String dst) throws IOException {
        InputStream in = getContentResolver().openInputStream(src);
        OutputStream out = this.openFileOutput(dst, Context.MODE_PRIVATE);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public String copyFileToPhone(Uri uri){
        String dest_path_name = "grahic_" + Utils.generateRandomString(10);
        try {
            copyFile(uri, dest_path_name);
        }
        catch (IOException e){
            System.err.println("Caught IOException: " + e.getMessage());
            return null;
        }
        return dest_path_name;
    }


    private void submitImages(){
        //

        ArrayList<String> imageDests = new ArrayList<>();
        ArrayList<NameValuePair> toTabletList = new ArrayList<>();
        for(Uri imageUri : image_refs) {
            String dest = copyFileToPhone(imageUri);
            imageDests.add(dest);
            toTabletList.add(new BasicNameValuePair(dest, dest));
        }

//        new UploadGraphicAsyncTask("http://" + ip + ":8080/graphic?token=" + tokenText.getText(), list).execute();

        HashMap<String, String> params = new HashMap<>();
        params.put("token", "");
        UploadGraphicAsyncTask toTabletTask = new UploadGraphicAsyncTask(
                Utils.buildURL("", "8080", "graphic", params),
                toTabletList
        );
        toTabletTask.execute();


        // The tablet should send an array<ids> back
        // TODO: TABLET MAGIC
        ArrayList<Integer> resultsArray = new ArrayList<Integer>();

        //This will be good for adding a COMPLETELY NEW InstructionalGraphic
        //TODO: Find out if this is a new instruction graphic
        InstructionalGraphic ig = new InstructionalGraphic("ig");
        //^^ The above line is used as a placeholder right now

        Boolean isIGNew = false;
        if (ig.numOfFrames() == 0)
            isIGNew = true;

        for (int i = 0; i < resultsArray.size(); i++)
            ig.addImage(resultsArray.get(i), images.get(i));

        if (isIGNew)
            db.addGraphicToEnd(ig);
//        else
//            db.updateGraphicAt(GET INDEX OF THE IG TO MODIFY);

    }



    private class UploadGraphicAsyncTask extends AsyncTask<Void, Void, Void> {
        private String url;
        private List<NameValuePair> nameValuePairs;

        public UploadGraphicAsyncTask(String url, List<NameValuePair> nameValuePairs){
            this.url = url;
            this.nameValuePairs = nameValuePairs;
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            try {
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                for(int i=0; i < nameValuePairs.size(); i++) {
                    String name = nameValuePairs.get(i).getName();
                    String value = nameValuePairs.get(i).getValue();

                    if(name.contains("graphic"))
                        builder.addPart(name, new FileBody(new File(value)));
//                    else builder.addPart(name, new StringBody(value, ContentType.TEXT_PLAIN));
                }

                httpPost.setEntity(builder.build());
                HttpResponse r = httpClient.execute(httpPost);
                ResponseHandler<String> handler = new BasicResponseHandler();


                //return handler.handleResponse(r);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

//        protected void onPostExecute(String result) {
//            Toast.makeText(ImageManagerActivity.this, result, Toast.LENGTH_LONG).show();
//        }
    }

    //Send
    //@TODO: FINISH THIS
//    public void copyFileFromResID(int src, File dst) throws IOException {
//        InputStream in = new FileInputStream(src);
//        OutputStream out = new FileOutputStream(dst);
//
//        // Transfer bytes from in to out
//        byte[] buf = new byte[1024];
//        int len;
//        while ((len = in.read(buf)) > 0) {
//            out.write(buf, 0, len);
//        }
//        in.close();
//        out.close();
//    }





}