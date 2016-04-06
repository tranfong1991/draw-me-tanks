package core.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class InstructionalGraphicDbAccess {

/*  TMP
 *  ==============================================================================================*/
    private class InstructionalGraphic {}

/*  Constructor
 *  ==============================================================================================*/
    public InstructionalGraphicDbAccess(Context context) {
        this.dbHelper = new InstructionalGraphicDbHelper(context);
    }

/*  Public Methods
 *  ==============================================================================================*/
    public InstructionalGraphic getGraphicAt(Integer index) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] proj = InstructionalGraphicDbContract.GraphicEntry.all();
        String selection = InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_INDEX + " =?";
        String[] selectionArgs = { String.valueOf(index) };

        Cursor c = db.query(
            InstructionalGraphicDbContract.GraphicEntry.TABLE_NAME,
            proj,
            selection,
            selectionArgs,
            null, null, null
        );

        if(!c.moveToFirst())
            throw new Error("ERROR in InstructionalGraphicDbAccess getGraphicAt: Index does not exist");

        return createGraphicsFromCursor(c).get(0);
    }

/*  Private Members
 *  ==============================================================================================*/
    private InstructionalGraphicDbHelper dbHelper;

/*  Private Methods
 *  ==============================================================================================*/
    private ArrayList<InstructionalGraphic> createGraphicsFromCursor(Cursor c) {
        ArrayList<InstructionalGraphic> graphics = new ArrayList<>();

        while(!c.isAfterLast()) {
            String  name = c.getString(c.getColumnIndex(InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_NAME));
            Integer inteval = c.getInt(c.getColumnIndex(InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_INTERVAL));
            Integer index = c.getInt(c.getColumnIndex(InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_INDEX));
            String  idStr = c.getString(c.getColumnIndex(InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_IDSTR));

            InstructionalGraphic graphic = new InstructionalGraphic(name);
            graphic.setInteval(interval);

            Map<Integer, String> imgRefMap = queryImageIds(idStr.split(","));  // @TODO Covert string[] to int[]

            //  @TODO FINISH THIS HERE

            c.moveToNext();
        }

        return graphics;
    }

    private Map<Integer, String> queryImageIds(Integer[] ids) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] proj = InstructionalGraphicDbContract.ImageMapEntry.all();
        String selection = "";
        String[] selectionArgs = new String[ids.length];
        for(int i = 0; i < ids.length; ++i) {
            selection += InstructionalGraphicDbContract.ImageMapEntry.COLUMN_NAME_IMAGE_ID + " = ?";
            if(i < ids.length - 1)
                selection += " OR ";
            selectionArgs[i] = String.valueOf(ids[i]);
        }

        Cursor c = db.query(
                InstructionalGraphicDbContract.ImageMapEntry.TABLE_NAME,
                proj,
                selection,
                selectionArgs,
                null, null, null
        );

        Map<Integer, String> map = new HashMap<>();
        c.moveToFirst();
        while(!c.isAfterLast()) {
            map.put(
                    c.getInt(c.getColumnIndex(InstructionalGraphicDbContract.ImageMapEntry.COLUMN_NAME_IMAGE_ID)),
                    c.getString(c.getColumnIndex(InstructionalGraphicDbContract.ImageMapEntry.COLUMN_NAME_PATH))
            );
            c.moveToNext();
        }

        return map;
    }
}
