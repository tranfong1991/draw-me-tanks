package andytran.dmap_phone;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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


/**
 *  ImageManagerActivity class
 *
 *  This class is meant to be extended by other activites.
 */
//public class ImageManagerActivity extends AppCompatActivity implements View.OnClickListener {
public class ImageManagerActivity extends AppCompatActivity {
/**
 *  ID used for the event in which an image is selected from a gallery
 */
    protected static final int PICK_IMAGE = 0;

/**
 *  Some kind of list.  not sure what this is for -- Timothy
 */
    protected ArrayList<String> images;

/**
 *  Contains a current list of image uris that have not yet been sent to the tablet or copied
 *  to the phone's personal directory.  Images that are selected from the gallery are stored
 *  here until submitImages() is called.
 */
    protected ArrayList<Uri> image_refs;

/**
 *  A reference to the database.
 */
    protected InstructionalGraphicDbAccess db;

    public String ip;
    public String port;
    public String token;

/**
 *  @inheritDoc
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_add_to_app_db);

        images = new ArrayList<>();
        image_refs = new ArrayList<>();
        db = new InstructionalGraphicDbAccess(this);

        ip = "10.201.158.37";
        port = "8080";
        token = "abc";
//        imageToUpload = (ImageView) findViewById(R.id.imageToUpload);
//        uploadButton = (Button) findViewById(R.id.uploadButton);
//        pathURI = (TextView) findViewById(R.id.path);
////
//        imageToUpload.setOnClickListener(this);
    }

    /*
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
    */

/*  Public Methods
 *  ==============================================================================================*/
/**
 *  Opens the image gallery for the user to select an image.  Upon completion, onActivityResult
 *  will fire.
 */
    public void openImageGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        this.startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

/**
 *  Copies the image at uri to the phone and returns a string representing the new destination
 *  @param uri For the image
 *  @return null if a failure occurred; otherwise, the file path to the copied image
 */
    public String copyFileToPhone(Uri uri){
        String dest_path_name = "graphic_" + Utils.generateRandomString(10);
        try {
            copyFile(uri, dest_path_name);
        }
        catch (IOException e){
            System.err.println("Caught IOException: " + e.getMessage());
            return null;
        }
        return dest_path_name;
    }

/**
 *  Takes the current list of images in image_refs and both copies them to the phone and sends
 *  them to the tablet.  Once both actions are complete, the images are submitted to the passed
 *  InstructionalGraphic (in the order of image_refs) and the database is updated.
 *  @param ig The InstructionalGraphic to update
 */
    public void submitImages(InstructionalGraphic ig){
        ArrayList<NameValuePair> toTabletList = new ArrayList<>();
        for(Uri imageUri : image_refs) {
            String dest = copyFileToPhone(imageUri);
            toTabletList.add(new BasicNameValuePair(dest, dest));
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("token", token);
        UploadGraphicAsyncTask toTabletTask = new UploadGraphicAsyncTask(
                Utils.buildURL(ip, port, "graphic", params),
                toTabletList,
                ig
        );
        toTabletTask.execute();
    }

/*  Protected Methods
 *  ==============================================================================================*/
/**
 *  Called automatically when the image gallery is finished.  If the user selected an image,
 *  then the URI of the image will be added to the current list of pending images.
 *  @param requestCode
 *  @param resultCode
 *  @param data
 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            image_refs.add(selectedImage);
        }
    }

/*  Private Methods
 *  ==============================================================================================*/
/**
 *  @private
 *  Copies the file at src to the dst location.
 *  @param src URI with an actual file
 *  @param dst String path where we want the new file
 *  @throws IOException
 */
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

/**
 *  Updates the graphic with the given ids and image references.  Updates the database as well.
 *  @param graphic
 *  @param ids Array of integer ids
 *  @param refs Array of String image paths
 */
    private void appendIdsAndRefsToGraphic(InstructionalGraphic graphic, ArrayList<Integer> ids, ArrayList<String> refs) {
        if(ids.size() != refs.size())
            throw new Error("ERROR in ImageManagerActivity appendIdsAndRefsToGraphic: ids and refs array sizes mismatch");

    //  Karrie, I put your stuff here - Timothy
        Boolean isIGNew = !db.isGraphicInDatabase(graphic);
        //if (graphic.numOfFrames() == 0)
         //   isIGNew = true;

        for (int i = 0; i < ids.size(); i++)
            graphic.addImage(ids.get(i), refs.get(i));

        if (isIGNew)
            db.addGraphicToEnd(graphic);
        else
            db.updateGraphicByName(graphic.getName(), graphic);
    }

/**
 *  UploadGraphicAsyncTask Private Class
 *  This class is used to upload a new image to the phone.
 */
    private class UploadGraphicAsyncTask extends AsyncTask<Void, Void, Void> {
        private String url;
        private List<NameValuePair> nameValuePairs;
        private InstructionalGraphic graphic;

        public UploadGraphicAsyncTask(String url, List<NameValuePair> nameValuePairs, InstructionalGraphic graphic){
            this.url = url;
            this.nameValuePairs = nameValuePairs;
            this.graphic = graphic;
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

                appendIdsAndRefsToGraphic(graphic, getIdsFromResponse(handler.handleResponse(r)), getRefsFromNameValuePairs());
                //return handler.handleResponse(r);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        private ArrayList<String> getRefsFromNameValuePairs() {
            ArrayList<String> refs = new ArrayList<>();
            for(NameValuePair pair : nameValuePairs)
                refs.add(pair.getValue());
            return refs;
        }

        private ArrayList<Integer> getIdsFromResponse(String json) {
            ArrayList<Integer> ids = new ArrayList<>();
            try {
                JSONObject obj = new JSONObject(json);
                int status = obj.getInt("status");
            /*
                if(status != 301)
                    return null;
             */
                JSONArray idsJson = obj.getJSONArray("ids");
                for(int i = 0; i < idsJson.length(); ++i)
                    ids.add((Integer)idsJson.get(i));
            }
            catch(JSONException err) {
                Log.d("STATUS", err.getMessage());
            }
            return ids;
        }

//        protected void onPostExecute(String result) {
//            Toast.makeText(ImageManagerActivity.this, result, Toast.LENGTH_LONG).show();
//        }
    }

    //Send
    //@TODO: FINISH THIS
    //@deprecated
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
