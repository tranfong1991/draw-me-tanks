package timothy.dmap_phone;

import org.junit.Test;
/**
 * Created by karriecheng on 4/3/16.
 */
public class DisplayImageTest {

    public static final String GraphicName = "Call Karrie";
    //
    //grab a database from somewhere
    public static InstructionalGraphic ig = new InstructionalGraphic(GraphicName);

    @Test
    public void testValidIndexSingleImages() throws Exception {
        //ig1
        //ig_db

        //get the image id of ig1
        //search for the current image in the db
        //assertTrue(image id of ig1 < ig_db size, is(true));

    }

    @Test
    public void testValidIndexMultipleImages() throws Exception {
        //ig2

        //ig_db

        //randomly generate a frame for ig2
        //can be >#frames, it just turns into the modulus
        //get the image id of current frame in ig2 (using modulus if need me)
        //search for the current image in the db

    }

    @Test
    public void testInvalidIndex() throws Exception {
        //ig_db

        //test outside range of indices
        //AssertTrue(image
    }



    @Test
    public void testDisplayImage() throws Exception {

        //ig1
        //send ig1 request to tablet
        //receive response from tablet
        //AssertTrue(image from id == response image from tablet)
        //otherwise throw FileNotFound()
    }

}

//This takes an InstructionalGraphic and locates the image id of the image corresponding to the specified index. It prepares the image for display on the phone while returning the id for tablet communication. This must test both valid and invalid ID requests (invalid being those which fall beyond the range of indices).
//
//        Overhead: This requires a populated testing image database.
//
//        Expected Results: This must test an InstuctionalGraphic containing a single image as well as one containing multiple images. The single image case must always return the id of that image as found within the InstructionalGraphic in the argument, regardless of the index value passed. The multiple-image case must return the id corresponding to the image at the argument index in the given InstructionalGraphic. This should hold even when the index is out of range by performing modular arithmetic.
//
//        Meets Requirements: 3.1.4
