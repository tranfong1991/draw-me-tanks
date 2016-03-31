package andytran.dmap_tablet;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Andy Tran on 2/18/2016.
 * Basic web server for tablet
 */
public class DMAPServer extends NanoHTTPD {
    public enum Action{
        GO_TO_LOAD,
        STOP_NSD,
        PLAY_GRAPHIC,
        STOP_GRAPHIC
    }

    private static final int TOKEN_LENGTH = 20;
    private static final int FILE_NAME_LENGTH = 10;
    private static final String TAG = "DMAPServer";

    public static final String PREF_NAME = "DMAP_PREF";
    public static final String PREF_TOKEN = "DMAP_TOKEN";
    public static final String PACKAGE_NAME = "andytran.dmap_tablet";
    public static final String EXTRA_GRAPHIC_NAME = "EXTRA_GRAPHIC_NAME";
    public static final String EXTRA_ACTION = "EXTRA_ACTION";
    public static final int PORT = 8080;
    public static final int HTTP_OK = 200;
    public static final int HTTP_CREATED = 201;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int HTTP_NOT_FOUND = 404;

    private String token;
    private Context context;
    private GraphicDbHelper dbHelper;
    private Map<Long, String> mapping;  //for faster entry access instead of querying database every time

    public DMAPServer(Context context)throws IOException{
        super(PORT);

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
        this.token = pref.getString(PREF_TOKEN, null);

        this.context = context;
        this.dbHelper = new GraphicDbHelper(context);
        this.mapping = new HashMap<>();
        populateMappingFromDb();

        start();
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        Method method = session.getMethod();
        Map<String, String> params = session.getParms();

        //when the phone first connects to the tablet
        if(method == Method.GET && uri.equals("/generate"))
            return generateToken();

        //check if token is not present or doesn't match
        if(params.get("token") == null || !params.get("token").equals(token))
            return newFixedLengthResponse("{\"status\":" + HTTP_UNAUTHORIZED + "}");

        switch(method){
            case GET:
                return doGet(session);
            case POST:
                return doPost(session);
            case PUT:
                return doPut(session);
            case DELETE:
                return doDelete(session);
        }

        return newFixedLengthResponse("{\"status\":" + HTTP_NOT_FOUND + "}");
    }

    private Response doGet(IHTTPSession session){
        String uri = session.getUri();
        switch(uri){
            case "/play":
                return playGraphic(session);
            case "/stop":
                return stopGraphic();
            case "/ping":
                return pingServer();
            default:
                return newFixedLengthResponse("{\"status\":" + HTTP_NOT_FOUND + "}");
        }
    }

    private Response doPost(IHTTPSession session){
        String uri = session.getUri();
        switch(uri){
            case "/graphic":
                return postGraphic(session);
            default:
                return newFixedLengthResponse("{\"status\":" + HTTP_NOT_FOUND + "}");
        }
    }

    private Response doPut(IHTTPSession session){
        return null;
    }

    private Response doDelete(IHTTPSession session){
        String uri = session.getUri();
        switch(uri){
            case "/graphic":
                return deleteGraphic(session);
            case "/deactivate":
                return deactivateToken();
            default:
                return newFixedLengthResponse("{\"status\":" + HTTP_NOT_FOUND + "}");
        }
    }

    //there might be more than one phone trying to obtain the access token, thus use synchronized
    private synchronized Response generateToken(){
        //device already has a token
        if(token != null)
            return newFixedLengthResponse("{\"status\":" + HTTP_UNAUTHORIZED + "}");

        this.token = Utils.generateRandomString(TOKEN_LENGTH);

        //save token to shared preferences
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_TOKEN, this.token);
        editor.apply();

        //tells the NSDBroadcastActivity to stop nsd and switch to Main Activity once a token is generated
        Intent intent = new Intent(PACKAGE_NAME);
        intent.putExtra(EXTRA_ACTION, Action.STOP_NSD);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        return newFixedLengthResponse("{\"token\" : \"" + this.token + "\"}");
    }

    private Response deactivateToken(){
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_TOKEN, null);
        editor.apply();

        this.token = null;

        Intent intent = new Intent(PACKAGE_NAME);
        intent.putExtra(EXTRA_ACTION, Action.GO_TO_LOAD);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        return newFixedLengthResponse("{\"status\" : " + HTTP_OK + "}");
    }

    private Response pingServer(){
        return newFixedLengthResponse("{\"status\" : " + HTTP_OK + "}");
    }

    //this generates an ID for the new graphic and sends the ID back
    private Response postGraphic(IHTTPSession session){
        StringBuffer buffer;
        try {
            Map<String, String> files = new HashMap<>();
            session.parseBody(files);

            ArrayList<Long> ids = new ArrayList<>();
            Set<String> keys = files.keySet();
            for(String key: keys){
                String fileName = Utils.generateRandomString(FILE_NAME_LENGTH);
                long id = addEntryToDb(fileName);

                ids.add(id);
                mapping.put(id, fileName);
                saveFile(files.get(key), fileName);
            }

            //append graphic ids to the json
            buffer = new StringBuffer();
            buffer.append("{\"status\" : ");
            buffer.append(HTTP_CREATED);
            buffer.append(", \"ids:\"[");
            for(int i = 0; i<ids.size(); i++){
                buffer.append(ids.get(i));
                if(i != ids.size() - 1)
                    buffer.append(",");
            }
            buffer.append("]}");
        } catch (IOException | ResponseException e) {
            e.printStackTrace();
            return newFixedLengthResponse("{\"status\" : " + HTTP_NOT_FOUND + "}");
        }

        return newFixedLengthResponse(buffer.toString());
    }

    private Response deleteGraphic(IHTTPSession session){
        Map<String, String> params = session.getParms();
        String graphicId = params.get("id");

        if(graphicId == null)
            return newFixedLengthResponse("{\"status\" : " + HTTP_NOT_FOUND + "}");

        if(!deleteFileWithId(Long.parseLong(graphicId)))
            return newFixedLengthResponse("{\"status\" : " + HTTP_NOT_FOUND + "}");

        mapping.remove(Long.parseLong(graphicId));
        deleteEntryFromDb(Long.parseLong(graphicId));

        return newFixedLengthResponse("{\"status\" : " + HTTP_OK + "}");
    }

    private Response playGraphic(IHTTPSession session){
        Map<String, String> params = session.getParms();
        String graphicId = params.get("id");

        String fileName = mapping.get(Long.parseLong(graphicId));
        if(fileName == null)
            return newFixedLengthResponse("{\"status\" : " + HTTP_NOT_FOUND + "}");

        Intent intent = new Intent(PACKAGE_NAME);
        intent.putExtra(EXTRA_GRAPHIC_NAME, fileName);
        intent.putExtra(EXTRA_ACTION, Action.PLAY_GRAPHIC);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        return newFixedLengthResponse("{\"status\" : " + HTTP_OK + "}");
    }

    private Response stopGraphic(){
        Intent intent = new Intent(PACKAGE_NAME);
        intent.putExtra(EXTRA_ACTION, Action.STOP_GRAPHIC);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        return newFixedLengthResponse("{\"status\" : " + HTTP_OK + "}");
    }

    private void saveFile(String cacheLocation, String fileName){
        File src = new File(cacheLocation);

        FileInputStream in;
        FileOutputStream out;
        try{
            in = new FileInputStream(src);
            out = context.openFileOutput(fileName, Context.MODE_PRIVATE);

            byte[] buf = new byte[65536];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private boolean deleteFileWithId(long id){
        String fileName = mapping.get(id);

        if(fileName == null)
            return false;

        context.deleteFile(fileName);
        return true;
    }

    private void populateMappingFromDb(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                GraphicContract.GraphicEntry._ID,
                GraphicContract.GraphicEntry.COLUMN_GRAPHIC_NAME
        };
        Cursor cursor = db.query(GraphicContract.GraphicEntry.TABLE_NAME, projection, null, null, null, null, null);

        //check if table is empty
        if(!cursor.moveToFirst()) {
            cursor.close();
            return;
        }

        do{
            long id = cursor.getLong(cursor.getColumnIndex(GraphicContract.GraphicEntry._ID));
            String fileName = cursor.getString(cursor.getColumnIndex(GraphicContract.GraphicEntry.COLUMN_GRAPHIC_NAME));

            mapping.put(id, fileName);
        }while(cursor.moveToNext());

        cursor.close();
    }

    //return row id or -1 if error
    private long addEntryToDb(String fileName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GraphicContract.GraphicEntry.COLUMN_GRAPHIC_NAME, fileName);

        return db.insert(GraphicContract.GraphicEntry.TABLE_NAME, null, values);
    }

    private void deleteEntryFromDb(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(GraphicContract.GraphicEntry.TABLE_NAME,
                GraphicContract.GraphicEntry._ID + " = " + id,
                null);
    }
}