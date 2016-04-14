package core.db;

import android.provider.BaseColumns;

public final class InstructionalGraphicDbContract {
    public InstructionalGraphicDbContract() {}

    public static abstract class GraphicEntry implements BaseColumns {
        public static final String TABLE_NAME = "graphics";
        public static final String COLUMN_NAME_NAME = "graphic_name";
        public static final String COLUMN_NAME_INTERVAL = "graphic_interval";
        public static final String COLUMN_NAME_INDEX = "graphic_index";
        public static final String COLUMN_NAME_IDSTR = "graphic_idstr";

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
        public static final String COLUMN_NAME_IMAGE_ID = "image_imageid";
        public static final String COLUMN_NAME_PATH = "image_path";

        public static String[] all() {
            return new String[] {
                _ID,
                COLUMN_NAME_IMAGE_ID,
                COLUMN_NAME_PATH
            };
        }
    }
}
