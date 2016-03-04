package timothy.dmap_phone;

import java.util.ArrayList;
import java.util.Iterator;


public class Slideshow implements Iterable<String> {
    public static final int DEFAULT_INTERVAL = 3000;
    public static final int DEFAULT_FRAME = 0;

    private ArrayList<String> imageRefs;
    private ArrayList<Integer> ids;
    private Integer interval;
    private String name;
    private Integer currentFrame;

    public Slideshow(String name) {
        this.imageRefs = new ArrayList();
        this.ids = new ArrayList();
        this.interval = new Integer(DEFAULT_INTERVAL);
        this.name = name;
        this.currentFrame = DEFAULT_FRAME;
    }

    public void initialize(){
        //this should start
        currentFrame = 0;
    }

    public void addImage(String imageRef, Integer id){
        imageRefs.add(imageRef);
        ids.add(id);
    }

    public void deleteImage(int frame){
        imageRefs.remove(frame);
        ids.remove(frame);
    }

    public Integer nextId(){
        currentFrame = (currentFrame + 1 ) % ids.size();
        return ids.get(currentFrame.intValue());
    }

    public Integer getInterval(){
        return interval;
    }

    /**
     *  Allows `for(img : slideshow)` to cycle through image references
     */
    public Iterator<String> iterator() {
        return imageRefs.iterator();
    }

}
