package core.db;

import android.provider.BaseColumns;

public final class DatabaseContract {
    public DatabaseContract() {}

    public static abstract class GraphicEntry implements BaseColumns {
        public static final String TABLE_NAME = "graphics";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_INTERVAL = "interval";
        public static final String COLUMN_NAME_IDSTR = "idstr";
    }

    public static abstract class ImageMapEntry implements BaseColumns {
        public static final String TABLE_NAME = "imagemap";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_PATH = "path";
    }
}
