package core.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InstructionalGraphicDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "InstructionalGraphic.db";

/*  Constructor
 *  ==============================================================================================*/
    public InstructionalGraphicDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

/*  Overwriting
 *  ==============================================================================================*/
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_GRAPHICS);
        db.execSQL(SQL_CREATE_IMAGEMAP);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_GRAPHICS);
        db.execSQL(SQL_DELETE_IMAGEMAP);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

/*  SQL Commands
 *  ==============================================================================================*/
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE  = " INTEGER";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_GRAPHICS =
        "CREATE TABLE " + InstructionalGraphicDbContract.GraphicEntry.TABLE_NAME + " (" +
        InstructionalGraphicDbContract.GraphicEntry._ID + " INTEGER PRIMARY KEY," +
        InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_NAME     + TEXT_TYPE + COMMA_SEP +
        InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_INTERVAL + INT_TYPE  + COMMA_SEP +
        InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_INDEX    + INT_TYPE  + COMMA_SEP +
        InstructionalGraphicDbContract.GraphicEntry.COLUMN_NAME_IDSTR    + TEXT_TYPE +
    " )";

    private static final String SQL_DELETE_GRAPHICS =
        "DROP TABLE IF EXISTS " + InstructionalGraphicDbContract.GraphicEntry.TABLE_NAME;

    private static final String SQL_CREATE_IMAGEMAP =
        "CREATE TABLE " + InstructionalGraphicDbContract.ImageMapEntry.TABLE_NAME + " (" +
        InstructionalGraphicDbContract.ImageMapEntry._ID + " INTEGER PRIMARY KEY," +
        InstructionalGraphicDbContract.ImageMapEntry.COLUMN_NAME_IMAGE_ID + INT_TYPE  + COMMA_SEP +
        InstructionalGraphicDbContract.ImageMapEntry.COLUMN_NAME_PATH     + TEXT_TYPE +
    " )";

    private static final String SQL_DELETE_IMAGEMAP =
        "DROP TABLE IF EXISTS " + InstructionalGraphicDbContract.ImageMapEntry.TABLE_NAME;
}
