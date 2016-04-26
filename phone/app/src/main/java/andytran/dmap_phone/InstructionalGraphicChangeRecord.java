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

    private void removeGraphicFromPhone(InstructionalGraphic ig, Integer index) {
        removeGraphicFromDatabase(ig.imageRefAt(index));
        return;
    }

    public void removeGraphic() {
        if(number_graphics_added > 0) {
            --number_graphics_added;
            removeGraphicFromPhone(working_ig, working_ig.numOfFrames() - 1);
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
        Log.i("message", "cancelled");
        for(int i = 0; i < number_graphics_added; ++i) {
            removeGraphicFromPhone(working_ig, working_ig.numOfFrames() - 1);
        }
        return original_ig;
    }

    @Deprecated
    public InstructionalGraphic finalizeChanges() {
        Log.i("message", "finalized");
        for(int i = 0; i < number_original_graphics_deleted; ++i) {
            removeFromTablet(original_ig.idAt(original_ig.numOfFrames() - 1));
            removeGraphicFromPhone(original_ig, original_ig.numOfFrames() - 1);
        }
        ArrayList<String> refs = getRefs();
        for(int i = 0; i < refs.size(); ++i) {
            Log.i("adding", "image " + i);
            Integer id = sendToTablet(refs.get(i));
            original_ig.addImage(id, refs.get(i));
        }
        return original_ig;
    }

    public void finalizeAttributes() {
        original_ig.setInterval(working_ig.getInterval());
        //original_ig.setName(working_ig.getName());
    }

    public InstructionalGraphic getCurrentInstructionalGraphic() {
        return working_ig;
    }

    public InstructionalGraphic getOriginalInstructionalGraphic() { return original_ig; }

    private void removeFromTablet(Integer id) {
        // TODO: sent remove request to tablet and get response
    }

    private Integer sendToTablet(String ref) {
        // TODO: send to tablet and get response
        Log.i("sending", " to tablet");
        return 0;
    }

    private void removeGraphicFromDatabase(String image_ref) {
        Log.i("message", "removed from database");
        // TODO call graphic database and tell to delete
    }
}
