package andytran.dmap_phone;

import java.util.Random;
import java.lang.Math;

/**
 * Created by Natalie on 3/29/2016.
 *
 * This is a mock database containing images representing those a user would put into an
 * InstructionalGraphic.
 *
 * TODO:
 *      Add references to non-square images (tall and wide)
 *
 */
public class MockGraphicDatabase {

    /**
     * Most tests should use this constructor. Note that the tester should save the seed
     * each session to ensure that they can recreate any issues for later testing.
     */
    public MockGraphicDatabase() {
        seed = System.currentTimeMillis();
        rand = new Random(seed);
    }

    /**
     * Allows more control of tests for targeted bug fixes.
     * @param custom_seed The seed for randomization preferred by the user.
     */
    public MockGraphicDatabase(long custom_seed) {
        seed = custom_seed;
        rand = new Random(seed);
    }

    /**
     * @return A reference to a randomly selected image.
     */
    public String get_random_graphic() {
        int index = rand.nextInt()%graphics.length;
        return String.valueOf(graphics[Math.abs(index)]);
    }

    /**
     * For testing. Allows developer to get same sequence of images if desired.
     * @return The seed used in a particular session.
     */
    public long get_seed() {
        return seed;
    }

    private Random rand;
    private long seed;

    private int[] graphics = {
            R.drawable.calm,
            R.drawable.cleat_finished,
            R.drawable.dontstand,
            R.drawable.dosit,
            R.drawable.leftboat,
            R.drawable.rightboat
    };
}
