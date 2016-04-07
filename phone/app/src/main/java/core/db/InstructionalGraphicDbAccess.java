package core.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import timothy.dmap_phone.InstructionalGraphic;

/** InstructionalGraphicDbAccess Class
 *  @author Timothy Foster, Shane Scott
 *
 *  This class grants access to the database using a high-level interface.  You must create an
 *  instance of this class within a Context, such as an activity. For example:
 *
 *  @usage
 *  InstructionalGraphicDbAccess db = new InstructionalGraphicDbAccess(getContext());
 *  InstructionalGraphic g = db.getGraphicByName("Sit Down");
 *
 *  @TODO Using shared preferences, we can obsolete the need for the Index field and use an array of Graphic _IDs instead
 */
public class InstructionalGraphicDbAccess {

/*  Constructor
 *  ==============================================================================================*/
/**
 *  Creates an access point for the database of graphics
 *  @param context Context of the activity
 */
    public InstructionalGraphicDbAccess(Context context) {
        this.dbHelper = new InstructionalGraphicDbHelper(context);
    }

/*  READ Methods
 *  ==============================================================================================*/
/**
 *  Returns the graphic at the specified index
 *  @param index Integer index greater than 0, less than number of graphics in database
 */
    public InstructionalGraphic getGraphicAt(Integer index) {
        return getSingleGraphicWhere(
            InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_INDEX + " =?",
            new String[]{String.valueOf(index)}
        );
    }

/**
 *  Returns the graphic with the specified name
 *  @param name
 */
    public InstructionalGraphic getGraphicByName(String name) {
        return getSingleGraphicWhere(
            InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_NAME + " =?",
            new String[]{name}
        );
    }

/**
 *  Returns the total number of graphics in the database
 */
    public Integer getNumberOfGraphics() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String proj[] = { InstructionalGraphicDbContract.GraphicEntry._ID };
        Cursor c = db.query(InstructionalGraphicDbContract.GraphicEntry.TABLE_NAME, proj, null, null, null, null, null);

        return c.getCount();
    }

/**
 *  Returns all instructional graphics ordered by their index.  Useable for things like
 *  generating a list view.
 */
    public ArrayList<InstructionalGraphic> getOrderedGraphicList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String proj[] = InstructionalGraphicDbContract.GraphicEntry.all();
        String orderBy = InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_INDEX + " ASC";

        Cursor c = db.query(
            InstructionalGraphicDbContract.GraphicEntry.TABLE_NAME,
            proj,
            null, null,
            null, null,
            orderBy
        );

        c.moveToFirst();
        return createGraphicsFromCursor(c);
    }

/*  WRITE Methods
 *  ==============================================================================================*/
    public void addGraphicToEnd(InstructionalGraphic graphic) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_NAME, graphic.getName());
        values.put(InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_INTERVAL, graphic.getInterval());
        values.put(InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_INDEX, getNumberOfGraphics());

    //  Inserting ids is hard
        StringBuilder idStr = new StringBuilder();
        for(int i = 0; i < graphic.numOfFrames(); ++i) {
            if(!isIdRegistered(graphic.idAt(i)))
                registerId(graphic.idAt(i), graphic.imageRefAt(i));

            idStr.append(graphic.idAt(i));
            if(i < graphic.numOfFrames() - 1)
                idStr.append(",");
        }

        values.put(InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_IDSTR, idStr.toString());

        db.insert(
                InstructionalGraphicDbContract.GraphicEntry.TABLE_NAME,
                null,
                values
        );
    }

    public void addGraphicAt(InstructionalGraphic graphic, Integer index) {
        if(index < 0 || index > getNumberOfGraphics())
            throw new DbAccessError("addGraphicAt: Attempted to insert at invalid index");

        Integer from = getNumberOfGraphics();
        addGraphicToEnd(graphic);
        moveGraphic(from, index);
    }

    public void removeGraphicFromEnd() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_INDEX + " =?";
        String[] selectionArgs = { String.valueOf(getNumberOfGraphics() - 1) };

        db.delete(
                InstructionalGraphicDbContract.GraphicEntry.TABLE_NAME,
                selection, selectionArgs
        );
    }

    public void removeGraphicAt(Integer index) {
        if(index < 0 || index >= getNumberOfGraphics())
            throw new DbAccessError("removeGraphicAt: Attempted to remove from invalid index");

        moveGraphic(index, getNumberOfGraphics() - 1);
        removeGraphicFromEnd();
    }

    public void removeGraphic(InstructionalGraphic graphic) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] proj = InstructionalGraphicDbContract.GraphicEntry.all();
        String selection = InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_NAME + " =?";
        String[] selectionArgs = { graphic.getName() };

        Cursor c = db.query(
            InstructionalGraphicDbContract.GraphicEntry.TABLE_NAME,
            proj,
            selection,
            selectionArgs,
            null, null, null
        );

        Integer index = c.getInt(c.getColumnIndex(InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_INDEX));

        c.close();

        removeGraphicAt(index);
    }

    public void updateGraphic(InstructionalGraphic graphic) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_NAME, graphic.getName());
        values.put(InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_INTERVAL, graphic.getInterval());

        StringBuilder idStr = new StringBuilder();
        for(int i = 0; i < graphic.numOfFrames(); ++i) {
            if(!isIdRegistered(graphic.idAt(i)))
                registerId(graphic.idAt(i), graphic.imageRefAt(i));

            idStr.append(graphic.idAt(i));
            if(i < graphic.numOfFrames() - 1)
                idStr.append(",");
        }

        values.put(InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_IDSTR, idStr.toString());

        String selection = InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_NAME + " =?";
        String[] selectionArgs = { graphic.getName() };

        db.update(
            InstructionalGraphicDbContract.GraphicEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs
        );
    }

/**
 *  Moves the graphic from the given index to the other index.  Updates all of the indices of
 *  the graphics between the two given indices as well.
 *
 *  @TODO we can remove this if we use Shared Preferences
 *
 *  @param fromIndex
 *  @param toIndex
 */
    public void moveGraphic(Integer fromIndex, Integer toIndex) {
        if(fromIndex < 0 || fromIndex >= getNumberOfGraphics() || toIndex < 0 || toIndex >= getNumberOfGraphics())
            throw new DbAccessError("moveGraphic: Attempted to move from or to invalid index");

        if(fromIndex.equals(toIndex))
            return;

        ArrayList<InstructionalGraphic> graphicList = getOrderedGraphicList();
        Integer direction = (int)Math.signum(fromIndex - toIndex);

        Integer smaller = fromIndex.compareTo(toIndex) < 0 ? fromIndex : toIndex;
        Integer larger  = fromIndex.compareTo(toIndex) < 0 ? toIndex : fromIndex;
        for(int i = smaller + 1; i < larger; ++i)
            updateIndex(graphicList.get(i).getName(), i + direction);
        updateIndex(graphicList.get(toIndex).getName(), toIndex + direction);
        updateIndex(graphicList.get(fromIndex).getName(), toIndex);
    }

/*  Private Members
 *  ==============================================================================================*/
    private InstructionalGraphicDbHelper dbHelper;

/*  Private Methods
 *  ==============================================================================================*/
/**
 *  Retrieves exactly one graphic under the specified condition
 *  @param selection A SQL WHERE clause
 *  @param selectionArgs The values of the WHERE clause
 *  @return A single InstructionalGraphic object
 */
    private InstructionalGraphic getSingleGraphicWhere(String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] proj = InstructionalGraphicDbContract.GraphicEntry.all();

        Cursor c = db.query(
                InstructionalGraphicDbContract.GraphicEntry.TABLE_NAME,
                proj,
                selection,
                selectionArgs,
                null, null, null
        );

        if(!c.moveToFirst())
            throw new DbAccessError("In getSingleGraphicWhere: Graphic does not exist where " + selection);

        return createGraphicsFromCursor(c).get(0);
    }

/**
 *  @private
 *  Generates an array of graphics from a Cursor object.  The Cursor is a result from a
 *  database query, and it therefore has a potentially unordered list of tuples encoding
 *  graphics information.  We can construct actual InstructionalGraphics using that information
 *  and return them to the calling function.
 *
 *  The caller should execute c.moveToFirst() before entering this method.
 *
 *  @param c Database query result
 *  @return Ordered list of InstructionalGraphics from the cursor
 */
    private ArrayList<InstructionalGraphic> createGraphicsFromCursor(Cursor c) {
        ArrayList<InstructionalGraphic> graphics = new ArrayList<>();

        while(!c.isAfterLast()) {
            String  name = c.getString(c.getColumnIndex(InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_NAME));
            Integer interval = c.getInt(c.getColumnIndex(InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_INTERVAL));
        //  Integer index = c.getInt(c.getColumnIndex(InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_INDEX));
            String  idStr = c.getString(c.getColumnIndex(InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_IDSTR));

            InstructionalGraphic graphic = new InstructionalGraphic(name);
            graphic.setInterval(interval);

        //  Order matters when it comes to ids, since the ordering represents the slideshow ordering
            Integer[] ids = idStrToIntegerArray(idStr);
            Map<Integer, String> imgRefMap = queryImageIds(ids);

            for(int i = 0; i < ids.length; ++i)
                graphic.addImage(ids[i], imgRefMap.get(ids[i]));

            c.moveToNext();
        }

        return graphics;
    }

/**
 *  @private
 *  Given a list of image ids, this method will return a map of the ids to the appropriate
 *  string image references.
 *  @param ids Array of integer ids
 *  @return Association of id to image reference
 */
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

        c.close();

        return map;
    }

/**
 *  @private
 *  Changes the index of the graphic at `from` to `to`
 *  @param name Name of the graphic whose index should change
 *  @param to New value of the index
 */
    private void updateIndex(String name, Integer to) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_INDEX, to);

        String selection = InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_NAME + " =?";
        String[] selectionArgs = { name };

        db.update(
                InstructionalGraphicDbContract.GraphicEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
    }

    private Boolean isIdRegistered(Integer id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] proj = { InstructionalGraphicDbContract.ImageMapEntry._ID };
        String selection = InstructionalGraphicDbContract.ImageMapEntry.COLUMN_NAME_IMAGE_ID + " =?";
        String[] selectionArgs = { id.toString() };

        Cursor c = db.query(
                InstructionalGraphicDbContract.ImageMapEntry.TABLE_NAME,
                proj,
                selection,
                selectionArgs,
                null, null, null
        );

        return c.getCount() > 0;
    }

    private void registerId(Integer id, String ref) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InstructionalGraphicDbContract.ImageMapEntry.COLUMN_NAME_IMAGE_ID, id);
        values.put(InstructionalGraphicDbContract.ImageMapEntry.COLUMN_NAME_PATH, ref);

        db.insert(
                InstructionalGraphicDbContract.ImageMapEntry.TABLE_NAME,
                null,
                values
        );
    }

/*  Helper Methods
 *  ==============================================================================================*/
/**
 *  @private
 *  Converts an id string into an array of integers.  This method exists solely for convenience
 *  @param idStr Ids in the form "id1,id2,id3,...,idn".  Notice no spaces!
 *  @return List of integers in the precise order of the string
 */
    private static Integer[] idStrToIntegerArray(String idStr) {
        String[] idStrs = idStr.split(",");
        Integer[] ids = new Integer[idStrs.length];

        for(int i = 0; i < idStrs.length; ++i)
            ids[i] = Integer.parseInt(idStrs[i]);

        return ids;
    }

/*  Error Class
 *  ==============================================================================================*/
    public class DbAccessError extends DatabaseError {
        public DbAccessError(String msg) {
            super("In InstructionalGraphicDbAccess: " + msg);
        }
    }
}
