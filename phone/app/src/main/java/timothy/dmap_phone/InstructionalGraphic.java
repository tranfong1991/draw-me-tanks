package timothy.dmap_phone;

import android.nfc.tech.IsoDep;

import java.io.Serializable;
import java.text.ParseException;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** InstructionalGraphic Class
 *  @author Timothy Foster, Karrie Cheng
 *
 *  Internal representation of a set of frames used in a graphic.
 *  ***********************************************************************************************/
@SuppressWarnings("serial")
public class InstructionalGraphic implements Serializable {
    public static final String NULL_NAME_MESSAGE = "Name given to Instructional Graphic is null";
    public static final String INVALID_INTERVAL_MESSAGE = "An invalid interval was given";

    public static final int DEFAULT_INTERVAL = 0;

/*  Constructor
 *  ==============================================================================================*/
/**
 *  Constructs a new Instructional Graphic.  All graphics require a non-null name.
 *  @param name A unique name for the graphic.  Though it should be unique, InstructionalGraphic is not responsible for tracking uniqueness.
 */
    public InstructionalGraphic(String name) {
        if(name.length() <= 0)
            throw new IllegalArgumentException(NULL_NAME_MESSAGE);
        this.name = name;
        this.interval = DEFAULT_INTERVAL;
        this.ids = new ArrayList();
        this.imageRefs = new HashMap();
    }


    /**
     * Constructs a duplicate of another Instructional Graphic.
     * @param other Instructional Graphic to copy.
     */
    public InstructionalGraphic(InstructionalGraphic other) {
        this.name = other.name;
        this.interval = other.interval;
        this.ids = other.ids;
        this.imageRefs = other.imageRefs;
        this.ids = new ArrayList();
        this.imageRefs = new HashMap();
        for(int i = 0; i < other.numOfFrames(); ++i) {
            Integer id = new Integer(other.ids.get(i));
            this.ids.add(id);
            this.imageRefs.put(id, other.imageRefs.get(id));
        }
    }

/*  Accessors
 *  ==============================================================================================*/
/**
 *  Returns the name of the graphic.
 */
    public String getName() {  return name; }

    /**
     * Sets a new name for the graphic. Requires a non-null name.
     * @param new_name The name to replace the old.
     */
    public void setName(String new_name) {
        if(new_name.length() <= 0)
            throw new IllegalArgumentException(NULL_NAME_MESSAGE);
        name = new_name;
    }

/**
 *  Gets the timing interval for the graphic.  For single-image graphics, the interval is set to maximum value since there is no need for periodicity.
 */
    public Integer getInterval() {
        return ids.size() > 1 ? interval : Integer.MAX_VALUE;
    }

/**
 *  Sets the interval, in milliseconds.
 *  @param value The new interval in milliseconds.  Must be greater than or equal to 0.
 */
    public void setInterval(Integer value) {
        if(value < 0)
            throw new IllegalArgumentException(INVALID_INTERVAL_MESSAGE + ": " + value.toString());
        this.interval = value;
    }

/*  Public Methods
 *  ==============================================================================================*/
/**
 *  The number of ids stored in the graphic.
 */
    public Integer numOfFrames() {
        return ids.size();
    }

/**
 *  Returns the id at the specific frame
 *  @param frame Bounded by 0 and numOfFrames()
 */
    public Integer idAt(Integer frame) {
        return ids.get(frame.intValue());
    }

/**
 *  Returns the image reference at the specific frame
 *  @param frame Bounded by 0 and numOfFrames()
 */
    public String imageRefAt(Integer frame) {
        return imageRefs.get(idAt(frame));
    }

/**
 *  Adds an image to the end of the graphic.
 *  @param id The id received from the tablet
 *  @param imageRef The file reference of the image on the phone
 */
    public void addImage(Integer id, String imageRef) {
        addImageAt(id, imageRef, ids.size());
    }

/**
 *  Adds an image anywhere in the graphic.
 *  @param id The id received from the tablet
 *  @param imageRef The file reference of the image on the phone
 *  @param position Where to add the image, bounded by 0 and numOfFrames()
 */
    public void addImageAt(Integer id, String imageRef, Integer position) {
        ids.add(position, id);
        imageRefs.put(id, imageRef);
    }

/**
 *  Removes the last image in the graphic.
 */
    public void removeImage() {
        removeImageAt(ids.size() - 1);
    }

/**
 *  Removes the image at the frame location
 *  @param frame Frame location, bounded by 0 and numOfFrames()
 */
    public void removeImageAt(Integer frame) {
        imageRefs.remove(ids.get(frame));
        ids.remove(frame.intValue());
    }

/**
 *  Given an image reference, this will find the id and remove the image from the graphic.
 *  @param imageRef File reference of the image on the phone
 */
    public void removeImageRef(String imageRef) {
        Integer id = -1;
        for(Integer key : imageRefs.keySet()) {
            if(imageRefs.get(key).equals(imageRef)) {
                id = key;
                break;
            }
        }
        removeImageAt(ids.indexOf(id));
    }

    /*  Comparison methods
 *  ==============================================================================================*/

    /**
     * Compares two Instructional Graphics. Order does matter for the image ids.
     * @param o The Instructional Graphic to compare against
     * @return True if equivalent, false if not.
     */
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!InstructionalGraphic.class.isInstance(o)) return false;
        InstructionalGraphic ig = (InstructionalGraphic) o;
        if(!ig.name.equals(this.name)
                || !ig.interval.equals(this.interval)
                || ig.ids.size() != this.ids.size()) {
            return false;
        }
        for(int i = 0; i < this.ids.size(); ++i) {
            Integer id = this.ids.get(i);
            if(!ig.ids.get(i).equals(id)
                    || !ig.imageRefs.get(id).equals(this.imageRefs.get(id))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime*result + ((name == null) ? 0 : name.hashCode());
        result = prime*result + ((interval == null) ? 0 : interval.hashCode());
        result = prime*result + Arrays.deepHashCode(ids.toArray());
        result = prime*result + Arrays.deepHashCode(imageRefs.keySet().toArray());
        result = prime*result + Arrays.deepHashCode(imageRefs.values().toArray());
        return result;
    }

    /*  Private Members
 *  ==============================================================================================*/
    private String name;
    private Integer interval;
    private ArrayList<Integer> ids;
    private Map<Integer, String> imageRefs;

}
