package core;

//import InstructionalGraphic from whereever

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 *  The DataHolder class is a singleton object which holds data.  Duh.
 *
 *  Basically, this class is used to hold complex data which ought to be shared and accessible
 *  to all activities in the app.
 *  ***********************************************************************************************/
public class DataHolder {

/*  TMP
 *  ==============================================================================================*/
    private class InstructionalGraphic {}

/*  Instance
 *  ==============================================================================================*/
    private static DataHolder instance_ = null;

    public static DataHolder instance() {
        if(instance_ == null)
            instance_ = new DataHolder();
        return instance_;
    }

/*  Public Methods
 *  ==============================================================================================*/
    public InstructionalGraphic getGraphicAt(Integer index) {
        return graphicsList.get(index);
    }

    public InstructionalGraphic getGraphicByName(String name) {
        return graphicsMap.get(name);
    }

    public Iterator<InstructionalGraphic> getGraphicsListIterator() {
        return graphicsList.iterator();
    }

/*  Constructor
 *  ==============================================================================================*/
    private DataHolder() {
        //  get the data from storage
        //  process the data into graphicsList and grpahicsMap


    }

/*  Private Members
 *  ==============================================================================================*/

/**
 *  The array of graphics, to preserve ordering
 */
    private ArrayList<InstructionalGraphic> graphicsList;

/**
 *  Map of graphic name to the graphic itself
 */
    private Map<String, InstructionalGraphic> graphicsMap;

}