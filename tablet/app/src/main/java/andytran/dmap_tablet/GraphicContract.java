package andytran.dmap_tablet;

import android.provider.BaseColumns;

/**
 * Created by Andy Tran on 3/4/2016.
 * Table containing 2 columns: _ID, graphic file name
 */
public class GraphicContract {
    public GraphicContract(){}

    public static abstract class GraphicEntry implements BaseColumns{
        public static final String TABLE_NAME = "graphics";
        public static final String COLUMN_GRAPHIC_NAME = "name";
    }
}
