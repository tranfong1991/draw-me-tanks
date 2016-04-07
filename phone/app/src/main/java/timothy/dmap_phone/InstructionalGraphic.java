package timothy.dmap_phone;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/** InstructionalGraphic Class
 *  @author Timothy Foster, Karrie Cheng
 *
 *  Internal representation of a set of frames used in a graphic.
 *  ***********************************************************************************************/
public class InstructionalGraphic {
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

/*  Accessors
 *  ==============================================================================================*/
/**
 *  Returns the name of the graphic.
 */
    public String getName() {  return name; }

/**
 *  Gets the timing interval for the graphic.  For single-image graphics, the interval is set to maximum value since there is no need for periodicity.
 */
    public Integer getInterval() {
        return ids.size() > 1 ? interval : Integer.MAX_VALUE;
    }

/**
 *  Sets the inverval, in milliseconds.
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
        return ids.get(frame);
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
        ids.remove(frame);
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

/*  Private Members
 *  ==============================================================================================*/
    private String name;
    private Integer interval;
    private ArrayList<Integer> ids;
    private Map<Integer, String> imageRefs;

}
