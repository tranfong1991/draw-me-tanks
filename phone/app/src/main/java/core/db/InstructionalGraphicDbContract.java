package core.db;

import android.provider.BaseColumns;

public final class InstructionalGraphicDbContract {
    public InstructionalGraphicDbContract() {}

    public static abstract class GraphicEntry implements BaseColumns {
        public static final String TABLE_NAME = "graphics";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_INTERVAL = "interval";
        public static final String COLUMN_NAME_INDEX = "index";
        public static final String COLUMN_NAME_IDSTR = "idstr";

        public static String[] all() {
            return new String[] {
                _ID,
                COLUMN_NAME_NAME,
                COLUMN_NAME_INTERVAL,
                COLUMN_NAME_INDEX,
                COLUMN_NAME_IDSTR
            };
        }
    }

    public static abstract class ImageMapEntry implements BaseColumns {
        public static final String TABLE_NAME = "imagemap";
        public static final String COLUMN_NAME_IMAGE_ID = "imageid";
        public static final String COLUMN_NAME_PATH = "path";

        public static String[] all() {
            return new String[] {
                _ID,
                COLUMN_NAME_IMAGE_ID,
                COLUMN_NAME_PATH
            };
        }
    }
}
