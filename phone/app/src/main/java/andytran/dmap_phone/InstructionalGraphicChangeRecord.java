package andytran.dmap_phone;

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
            removeGraphicFromDatabase(working_ig.idAt(working_ig.numOfFrames() - 1));
        } else {
            ++number_original_graphics_deleted;
        }
        working_ig.removeImage();
    }

    public void addGraphic(Integer id) {
        working_ig.addImage(id, "false");
        ++number_graphics_added;
    }

    public ArrayList<Integer> getNewIds() {
        ArrayList<Integer> new_ids = new ArrayList();
        for(int i = 0; i < number_graphics_added; ++i) {
            Integer index = original_ig.numOfFrames() - number_original_graphics_deleted + i;
            new_ids.add(working_ig.idAt(index));
        }
        return new_ids;
    }

    public Integer getNumberDeleted() {
        return number_original_graphics_deleted;
    }

    public InstructionalGraphic cancel() {
        return original_ig;
    }

    public InstructionalGraphic getCurrentInstructionalGraphic() {
        return working_ig;
    }

    private void removeGraphicFromDatabase(Integer image_id) {
        // TODO
    }
}
