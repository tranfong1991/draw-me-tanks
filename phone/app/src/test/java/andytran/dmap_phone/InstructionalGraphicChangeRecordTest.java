package andytran.dmap_phone;

import junit.framework.TestCase;

import org.hamcrest.Matcher;
import static org.hamcrest.core.IsNot.not;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Random;

import matchers.dmap_phone.InstructionalGraphicIsEqual;
import matchers.dmap_phone.TotalArrayMatcher;
import timothy.dmap_phone.InstructionalGraphic;

/**
 * Created by Kiri on 4/12/2016.
 */
public class InstructionalGraphicChangeRecordTest extends TestCase {

    public static final String def_name = "Test Graphic";
    public static final Integer def_interval = 9;
    public static ArrayList<String> ig_image_ids;
    public static ArrayList<String> ig_image_refs;

    public Random rand = new Random(System.currentTimeMillis());

    public InstructionalGraphic newIg(int amount) {
        initializeArrays(amount);
        InstructionalGraphic ig = initializeInstructionalGraphic();
        fillInstructionalGraphic(ig);
        return ig;
    }

    public void initializeArrays(int amount) {
        ig_image_ids = new ArrayList<String>();
        ig_image_refs = new ArrayList<String>();

        for(int i = 0; i < amount; ++i) {
            ig_image_refs.add(String.valueOf(i));
            ig_image_ids.add(String.valueOf(i));
        }
    }

    public InstructionalGraphic initializeInstructionalGraphic() {
        InstructionalGraphic ig = new InstructionalGraphic(def_name);
        ig.setInterval(def_interval);
        return ig;
    }

    public void fillInstructionalGraphic(InstructionalGraphic ig) {
        for (int i = 0; i < ig_image_ids.size(); ++i) {
            ig.addImage(Integer.valueOf(ig_image_ids.get(i)), ig_image_refs.get(i));
        }
    }

    public void addToGraphic(Integer times, InstructionalGraphicChangeRecord cr) {
        for(int i = 0; i < times; ++i) {
            cr.addGraphic(String.valueOf(i));
        }
    }

    public void removeFromGraphic(Integer times, InstructionalGraphicChangeRecord cr) {
        for(int i = 0; i < times; ++i) {
            cr.removeGraphic();
        }
    }

    public Integer getNumberImages() {
        return Math.abs(rand.nextInt()%15) + 1;
    }

    public void addToGraphic(Integer times, InstructionalGraphic ig) {
        for(int i = 0; i < times; ++i) {
            ig.addImage(-1*(i + 1), String.valueOf(i));
        }
    }

    public ArrayList<String> getNewRefs(Integer number) {
        ArrayList<String> ids = new ArrayList<>();
        for(int i = 0; i < number; ++i) {
            ids.add(String.valueOf(i));
        }
        return ids;
    }

    public void testRemoveOriginalGraphic() throws Exception {
        Integer control_number = getNumberImages();
        Integer test_number = getNumberImages() + control_number;

        InstructionalGraphic control = newIg(control_number);
        InstructionalGraphicChangeRecord cr = new InstructionalGraphicChangeRecord(newIg(test_number));

        assertThat(cr.getCurrentInstructionalGraphic(), is(not(equalTo(control))));

        removeFromGraphic(test_number - control_number, cr);
        assertThat(cr.getCurrentInstructionalGraphic(), is(equalTo(control)));
    }

    public void testAddGraphic() throws Exception {
        Integer number_to_add = getNumberImages();
        Integer base_number = getNumberImages();

        InstructionalGraphic control = newIg(base_number);
        InstructionalGraphicChangeRecord cr = new InstructionalGraphicChangeRecord(newIg(base_number));

        addToGraphic(number_to_add, control);
        assertThat(cr.getCurrentInstructionalGraphic(), is(not(equalTo(control))));

        addToGraphic(number_to_add, cr);
        assertThat(cr.getCurrentInstructionalGraphic(), is(equalTo(control)));
    }

    public void testRemoveNewGraphics() {
        Integer control_number = getNumberImages();
        Integer test_number = getNumberImages();

        InstructionalGraphic control = newIg(control_number);
        InstructionalGraphicChangeRecord cr = new InstructionalGraphicChangeRecord(control);

        assertThat(cr.getCurrentInstructionalGraphic(), is(equalTo(control)));

        addToGraphic(test_number, cr);
        assertThat(cr.getCurrentInstructionalGraphic(), is(not(equalTo(control))));

        removeFromGraphic(test_number, cr);
        assertThat(cr.getCurrentInstructionalGraphic(), is(equalTo(control)));
    }

    public void testAddRemoveThenAdd() {
        InstructionalGraphic control = newIg(4);
        InstructionalGraphicChangeRecord cr = new InstructionalGraphicChangeRecord(control);

        assertThat(cr.getCurrentInstructionalGraphic(), is(equalTo(control)));

        control.addImage(-2, "again");
        assertThat(cr.getCurrentInstructionalGraphic(), is(not(equalTo(control))));

        cr.addGraphic("test");
        assertThat(cr.getCurrentInstructionalGraphic(), is(not(equalTo(control))));

        cr.removeGraphic();
        assertThat(cr.getCurrentInstructionalGraphic(), is(not(equalTo(control))));

        cr.addGraphic("again");
        assertThat(cr.getCurrentInstructionalGraphic(), is(equalTo(control)));
    }

    public void testRemoveMoreThanAdd() {
        Integer add_number = getNumberImages();
        Integer remove_number = getNumberImages() + add_number;
        Integer base_number = getNumberImages() + remove_number;

        InstructionalGraphic control = newIg(base_number - remove_number);
        InstructionalGraphicChangeRecord cr = new InstructionalGraphicChangeRecord(newIg(base_number - add_number));

        assertThat(cr.getCurrentInstructionalGraphic(), is(not(equalTo(control))));

        addToGraphic(add_number, cr);
        assertThat(cr.getCurrentInstructionalGraphic(), is(not(equalTo(control))));

        removeFromGraphic(remove_number, cr);
        assertThat(cr.getCurrentInstructionalGraphic(), is(equalTo(control)));
    }

    public void testGetNewIds() throws Exception {
        Integer base_number = getNumberImages();
        Integer add_number = getNumberImages();

        InstructionalGraphicChangeRecord cr = new InstructionalGraphicChangeRecord(newIg(base_number));
        addToGraphic(add_number, cr);
        ArrayList<String> new_refs = getNewRefs(add_number);

        assertThat(cr.getRefs(), is(equalTo(new_refs)));
    }

    public void testGetNoNewIds() {
        Integer add_number = getNumberImages();
        Integer remove_number = add_number + getNumberImages();
        Integer base_number = getNumberImages() + remove_number - add_number;

        InstructionalGraphicChangeRecord cr = new InstructionalGraphicChangeRecord(newIg(base_number));
        addToGraphic(add_number, cr);
        removeFromGraphic(remove_number, cr);
        ArrayList<String> new_refs = getNewRefs(0);

        assertThat(cr.getRefs(), is(equalTo(new_refs)));
    }

    public void testNewIdsAfterSomeDeleted() {
        Integer remove_number = getNumberImages();
        Integer add_number = remove_number + getNumberImages();
        Integer base_number = getNumberImages();

        InstructionalGraphicChangeRecord cr = new InstructionalGraphicChangeRecord(newIg(base_number));
        addToGraphic(add_number, cr);
        removeFromGraphic(remove_number, cr);
        ArrayList<String> new_refs = getNewRefs(add_number - remove_number);

        assertThat(cr.getRefs(), is(equalTo(new_refs)));
    }

    public void testGetNumberDeletedNoneAdded() throws Exception {
        Integer remove_number = getNumberImages();
        Integer base_number = getNumberImages() + remove_number;

        InstructionalGraphicChangeRecord cr = new InstructionalGraphicChangeRecord(newIg(base_number));
        removeFromGraphic(remove_number, cr);

        assertThat(cr.getNumberDeleted(), is(remove_number));
    }

    public void testGetNumberDeletedFromOnlyAdded() throws Exception {
        Integer remove_number = getNumberImages();
        Integer number_added = getNumberImages() + remove_number;
        Integer base_number = getNumberImages();

        InstructionalGraphicChangeRecord cr = new InstructionalGraphicChangeRecord(newIg(base_number));
        addToGraphic(number_added, cr);
        removeFromGraphic(remove_number, cr);

        assertThat(cr.getNumberDeleted(), is(0));
    }

    public void testGetNumberDeletedFromMoreThanAdded() throws Exception {
        Integer number_added = getNumberImages();
        Integer remove_number = getNumberImages() + number_added;
        Integer base_number = getNumberImages() + remove_number;

        InstructionalGraphicChangeRecord cr = new InstructionalGraphicChangeRecord(newIg(base_number));
        addToGraphic(number_added, cr);
        removeFromGraphic(remove_number, cr);

        assertThat(cr.getNumberDeleted(), is(remove_number - number_added));
    }

    public void testAddAddDelete() {
        Integer base_number = 6; //getNumberImages();

        InstructionalGraphic control = newIg(base_number);
        InstructionalGraphicChangeRecord cr = new InstructionalGraphicChangeRecord(newIg(base_number));

        addToGraphic(2, control);
        addToGraphic(3, cr);
        removeFromGraphic(1, cr);
        assertThat(cr.getCurrentInstructionalGraphic(), is(equalTo(control)));
        for(int i = 0; i < cr.getCurrentInstructionalGraphic().numOfFrames(); ++i) {
            assertThat(cr.getCurrentInstructionalGraphic().idAt(i), is(notNullValue()));
            assertThat(cr.getCurrentInstructionalGraphic().imageRefAt(i), is(notNullValue()));
        }
    }

    /*public void testCancel() throws Exception {

    }

    public void testGetCurrentInstructionalGraphic() throws Exception {

    }*/

    static InstructionalGraphicIsEqual equalTo(InstructionalGraphic ig) {
        return new InstructionalGraphicIsEqual(ig);
    }

    static TotalArrayMatcher<String> equalTo(ArrayList<String> strings) {
        return new TotalArrayMatcher<String>(strings);
    }
}