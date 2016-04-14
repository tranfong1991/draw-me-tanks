package andytran.dmap_phone;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

import timothy.dmap_phone.InstructionalGraphic;

/**
 * Created by Natalie Rawle on 4/4/2016.
 */
@SuppressWarnings("serial")
public class InstructionalGraphicChangeRecord implements Serializable {
    private InstructionalGraphic original_ig;
    private InstructionalGraphic working_ig;

    private int count = 3;  // used for id's to ensure they're unique

    private Integer number_original_graphics_deleted;
    private Integer number_graphics_added;

    public InstructionalGraphicChangeRecord(InstructionalGraphic base) {
        original_ig = base;
        working_ig = new InstructionalGraphic(base);
        number_original_graphics_deleted = 0;
        number_graphics_added = 0;
    }

    public void removeGraphic() {
        if(number_graphics_added > 0) {
            --number_graphics_added;
            removeGraphicFromDatabase(working_ig.imageRefAt(working_ig.numOfFrames() - 1));
        } else {
            ++number_original_graphics_deleted;
        }
        working_ig.removeImage();
    }

    public void addGraphic(String ref) {
        working_ig.addImage(count++, ref);
        ++number_graphics_added;
    }

    public void setName(String name) {
        working_ig.setName(name);
    }

    public void setInterval(Integer interval) {
        working_ig.setInterval(interval);
    }

    public ArrayList<String> getRefs() {
        ArrayList<String> new_refs = new ArrayList();
        for(int i = 0; i < number_graphics_added; ++i) {
            Integer index = original_ig.numOfFrames() - number_original_graphics_deleted + i;
            new_refs.add(working_ig.imageRefAt(index));
        }
        return new_refs;
    }

    public Integer getNumberDeleted() {
        return number_original_graphics_deleted;
    }

    public InstructionalGraphic cancel() {
        Log.i("message", "cancelled");
        return original_ig;
    }

    public InstructionalGraphic finalizeChanges() {
        Log.i("message", "finalized");
        return working_ig;
    }

    public InstructionalGraphic getCurrentInstructionalGraphic() {
        return working_ig;
    }

    public InstructionalGraphic getOriginalInstructionalGraphic() { return original_ig; }

    private void removeGraphicFromDatabase(String image_ref) {
        // TODO
    }
}
