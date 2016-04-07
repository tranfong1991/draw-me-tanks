package core;

// @TODO This is a dummy class.  Remove when merged with slideshow
public class InstructionalGraphic {
    public static final String NULL_NAME_MESSAGE = "Name given to Instructional Graphic is null";
    public static final String INVALID_INTERVAL_MESSAGE = "An invalid interval was given";

    public static final int DEFAULT_INTERVAL = 0;

    public InstructionalGraphic(String name) {}

    public String getName() { return ""; }
    public Integer getInterval() { return 0; }
    public void setInterval(Integer value) {}

    public Integer numOfFrames() { return 0; }
    public Integer idAt(Integer frame) { return 0; }
    public String imageRefAt(Integer frame) { return ""; }
    public void addImage(Integer id, String imageRef) {}
    public void addImageAt(Integer id, String imageRef, Integer position) {}
    public void removeImage() {}
    public void removeImageAt(Integer frame) {}
    public void removeImageRef(String imageRef) {}
}
