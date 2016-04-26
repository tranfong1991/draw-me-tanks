package timothy.dmap_phone;

import junit.framework.TestCase;
import java.util.ArrayList;

import static org.hamcrest.core.IsNot.not;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Random;

import matchers.dmap_phone.InstructionalGraphicIsEqual;
import matchers.dmap_phone.TotalArrayMatcher;
import timothy.dmap_phone.InstructionalGraphic;
import org.junit.Before;

/**
 * Created by Kiri on 3/29/2016.
 */
public class InstructionalGraphicTest extends TestCase {
    public static final String ig_name = "Test Graphic";
    public static final Integer ig_interval = 9;
    public static ArrayList<String> ig_image_ids;
    public static ArrayList<String> ig_image_refs;
    public static InstructionalGraphic ig;

    public void initializeArrays() {
        ig_image_ids = new ArrayList<String>();
        ig_image_refs = new ArrayList<String>();

        int i = 0;
        for(; i < 4; ++i) {
            ig_image_refs.add(String.valueOf(i));
        }
        for(; i < 8; ++i) {
            ig_image_ids.add(String.valueOf(i));
        }
    }

    public InstructionalGraphic initializeInstructionalGraphic() {
        InstructionalGraphic igg = new InstructionalGraphic(ig_name);
        igg.setInterval(ig_interval);
        initializeArrays();
        return igg;
    }

    public void fillInstructionalGraphic(InstructionalGraphic igg) {
        for(int i = 0; i < ig_image_ids.size(); ++i) {
            igg.addImage(Integer.valueOf(ig_image_ids.get(i)), ig_image_refs.get(i));
        }
    }

    public void testInstructionalGraphic_ComparingToItself_ReturnsTrue() {
        ig = initializeInstructionalGraphic();
        InstructionalGraphic og = initializeInstructionalGraphic();
        fillInstructionalGraphic(ig);
        fillInstructionalGraphic(og);
        assertThat(ig, is(equalTo(og)));
    }

    public void testInstructionalGraphic_ComparingToDifferent_ReturnsFalse() {
        ig = initializeInstructionalGraphic();
        InstructionalGraphic other = new InstructionalGraphic(ig_name);
        other.setInterval(78);
        assertThat(ig.equals(other), is(false));
    }

    public void testEmptyInstructionalGraphic_Comparing_DoesNotThrowError() {
        ig = initializeInstructionalGraphic();
        InstructionalGraphic og = initializeInstructionalGraphic();
        assertThat(ig, is(equalTo(og)));
        fillInstructionalGraphic(ig);
    }

    public void testCopyConstructor_CreatesDeepCopy() {
        ig = initializeInstructionalGraphic();
        InstructionalGraphic new_ig = new InstructionalGraphic(ig);
        assertThat(ig, is(equalTo(new_ig)));
        new_ig.addImage(11, "56689");
        assertThat(ig, is(not(equalTo(new_ig))));

        new_ig = new InstructionalGraphic(ig);
        assertThat(ig, is(equalTo(new_ig)));
        new_ig.setName("New Name");
        assertThat(ig, is(not(equalTo(new_ig))));
    }

    static InstructionalGraphicIsEqual equalTo(InstructionalGraphic ig) {
        return new InstructionalGraphicIsEqual(ig);
    }
}