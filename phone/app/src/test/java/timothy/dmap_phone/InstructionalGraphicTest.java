package timothy.dmap_phone;

import org.junit.Test;

import java.util.ArrayList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;

/**
 * Created by Kiri on 3/29/2016.
 */
public class InstructionalGraphicTest {
    public static final String ig_name = "Test Graphic";
    public static final Integer ig_interval = 9;
    public static ArrayList<String> ig_image_ids;
    public static ArrayList<String> ig_image_refs;
    public static InstructionalGraphic ig;

    @Before
    public void initializeEverything() {
        initializeArrays();
        initializeInstructionalGraphic();
        fillInstructionalGraphic();
    }

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

    public void initializeInstructionalGraphic() {
        ig = new InstructionalGraphic(ig_name);
        ig.setInterval(ig_interval);
    }

    public void fillInstructionalGraphic() {
        for(int i = 0; i < ig_image_ids.size(); ++i) {
            ig.addImage(Integer.valueOf(ig_image_ids.get(i)), ig_image_refs.get(i));
        }
    }

    @Test
    public void instructionalGraphic_ComparingToItself_ReturnsTrue() {
        assertTrue(ig.equals(ig));
    }

    @Test
    public void instructionalGraphic_ComparingToDifferent_ReturnsFalse() {
        InstructionalGraphic other = new InstructionalGraphic(ig.getName());
        assertFalse(ig.equals(other));
    }

    @Test
    public void emptyInstructionalGraphic_Comparing_DoesNotThrowError() {
        initializeInstructionalGraphic();
        assertTrue(ig.equals(ig));
        fillInstructionalGraphic();
    }

    @Test
    public void copyConstructor_CreatesDeepCopy() {
        InstructionalGraphic new_ig = new InstructionalGraphic(ig);
        assertTrue(new_ig.equals(ig));
        new_ig.addImage(11, "56689");
        assertFalse(new_ig.equals(ig));

        new_ig = new InstructionalGraphic(ig);
        assertTrue(new_ig.equals(ig));
        new_ig.setName("New Name");
        assertFalse(new_ig.equals(ig));
    }
}