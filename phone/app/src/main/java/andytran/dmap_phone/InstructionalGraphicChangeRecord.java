package andytran.dmap_phone;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import java.io.Serializable;
import java.util.ArrayList;

import timothy.dmap_phone.InstructionalGraphic;

/**
 * Created by Natalie Rawle on 4/4/2016.
 */
@SuppressWarnings("serial")

/**
 * TODO
 * Overwrite the image_refs with URI versions getRefs (image_refs will have way extra)
 * Then pass original_ig to submitImages
 * @Override onActivityResult
 * extend imagemanageractivity
 */
public class InstructionalGraphicChangeRecord implements Serializable {
    private InstructionalGraphic original_ig;
    private InstructionalGraphic working_ig;

    Integer count = -1;

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
        } else {
            ++number_original_graphics_deleted;
        }
        working_ig.removeImage();
    }

    public void addGraphic(String ref) {
        working_ig.addImage(count--, ref);
        ++number_graphics_added;
    }

    public void setName(String name) {
        working_ig.setName(name);
    }

    public void setInterval(Integer interval) {
        working_ig.setInterval(interval);
    }

    public Integer getIndexFirstNewGraphic() {
        return original_ig.numOfFrames() - number_original_graphics_deleted;
    }

    public ArrayList<String> getRefs() {
        ArrayList<String> new_refs = new ArrayList();
        Integer displacement = getIndexFirstNewGraphic();
        for(int i = 0; i < number_graphics_added; ++i) {
            Integer index = displacement + i;
            new_refs.add(working_ig.imageRefAt(index));
        }
        return new_refs;
    }

    public ArrayList<Uri> getUris(Context context) {
        ArrayList<String> refs = getRefs();
        ArrayList<Uri> uris = new ArrayList<Uri>();
        for(int i = 0; i < refs.size(); ++i) {
            uris.add(Uri.parse(refs.get(i)));
        }
        return uris;
    }

    public Integer getNumberDeleted() {
        return number_original_graphics_deleted;
    }

    public InstructionalGraphic cancel() {
        return original_ig;
    }

    public InstructionalGraphic finalizeChanges() {
        Log.i("message", "finalized");
        for(int i = 0; i < number_original_graphics_deleted; ++i) {
            original_ig.removeImage();
        }
        original_ig.setInterval(working_ig.getInterval());
        return original_ig;
    }

    public InstructionalGraphic getCurrentInstructionalGraphic() {
        return working_ig;
    }

    public InstructionalGraphic getOriginalInstructionalGraphic() { return original_ig; }

}
