package timothy.dmap_phone;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static org.junit.Assert.assertTrue;

//import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.assertFalse;
/**
 * Created by karriecheng on 4/10/16.
 */

//5.2.10 StopInstructionalGraphic
//
//        This must cancel the timer and initiate communication with the tablet for stopping image display.
//
//        Overhead: This does not require a database or a wifi connection.
//
//        Expected Results: The timer must halt during the call and the test must return a GET request for the tablet with the code for a blank screen.

//
//        Meets Requirements: 3.1.5



//This takes an InstructionalGraphic and runs a timer based on the interval specified in that InstructionalGraphic,
// returning the index of the image currently displayed
// as well as a timer which controls image signals. The test must include both an InstructionalGraphic with a MAX_INT interval (the default time) and an InstructionalGraphic with an interval greater than zero but less than MAX_INT. Because InstructionalGraphics cannot contain intervals less than zero, it does not need to test this case.
//
//        Overhead: This does not require a database.
//
//        Expected Results: The timer must iterate by the number of seconds specified in the interval, and the image ID must correspond to the image requested by the timer.
//
//        Meets Requirements: 3.1.4, 3.2.2

    /*
    * Test the current image by:
    * Creating a new instructionalGraphic.
    * start the timer for that
    * start a different timer
    * assert that image at timer - 1  and the current image is the same
    *
    *
    * */

public class InstructionalGraphicTimerTest {
    /*public static final String ig_name = "Test Graphic";
    public static final Integer ig_interval_submax = 997;
    //997 is a prime number, so it is unlikely that intervals will overlap
    public static ArrayList<String> ig_image_ids;
    public static ArrayList<String> ig_image_refs;
    public static InstructionalGraphic ig_default;
    public static InstructionalGraphic ig_short;

    public Timer test_timer = new Timer();
    public Integer test_frame = 0;
    public TimerTask testTimerAction = new TimerTask(){
        @Override
        public void run() {
            test_frame++;
        }
    };


    public void resetFrames(){
        test_frame = 0;
        //get the number of frames
    }

    @Before
    public void initializeEverything() {
        initializeArrays();

        initializeDefaultInstructionalGraphic();
        initializeShortInstructionalGraphic();

        fillDefaultInstructionalGraphic();
        fillShortInstructionalGraphic();
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

    public void initializeDefaultInstructionalGraphic() {
        ig_default = new InstructionalGraphic(ig_name);
    }
    public void fillDefaultInstructionalGraphic() {
        for(int i = 0; i < ig_image_ids.size(); ++i) {
            ig_default.addImage(Integer.valueOf(ig_image_ids.get(i)), ig_image_refs.get(i));
        }
    }
    public void initializeShortInstructionalGraphic() {
        ig_short = new InstructionalGraphic(ig_name);
        ig_short.setInterval(ig_interval_submax);
    }
    public void fillShortInstructionalGraphic() {
        for(int i = 0; i < ig_image_ids.size(); ++i) {
            ig_short.addImage(Integer.valueOf(ig_image_ids.get(i)), ig_image_refs.get(i));
        }
    }

    @Test
    public void testStartIntDefault() throws Exception {
        InstructionalGraphicTimer igt = new InstructionalGraphicTimer(ig_default);
        Integer currentFrame = 0;

        igt.start();
        Thread.sleep(ig_interval_submax);
        assertTrue(igt.getCurrentFrame() == 0);
    }

    @Test
    public void testStartSubIntMaxFrame() throws Exception {
        InstructionalGraphicTimer igt = new InstructionalGraphicTimer(ig_short);
        Integer currentFrame = 0;

        igt.start();
        assertTrue(igt.getCurrentFrame() == 0);
    }

    @Test
    public void testIntSubMaxIncrementFrame() throws Exception {
        InstructionalGraphicTimer igt = new InstructionalGraphicTimer(ig_short);

        igt.start();

        resetFrames();
        test_timer.schedule(testTimerAction, 0, ig_interval_submax);

        Thread.sleep(ig_interval_submax);
        assertTrue(test_frame == igt.getCurrentFrame());
        assertTrue(igt.getCurrentFrame() == 1);
        assertTrue(test_frame == 1);
    }

    @Test
    public void testIntSubMaxRestartFrame() throws Exception {
        InstructionalGraphicTimer igt = new InstructionalGraphicTimer(ig_short);

        igt.start();
        Thread.sleep(ig_interval_submax * ig_image_ids.size());
        assertTrue(igt.getCurrentFrame() == 0);
    }

    @Test
    public void testIntSubMaxTimer() throws Exception {
        InstructionalGraphicTimer igt = new InstructionalGraphicTimer(ig_short);

        igt.start();
        Thread.sleep(ig_interval_submax * ig_image_ids.size());
        assertTrue(igt.getCurrentFrame() == 0);
    }*/
}