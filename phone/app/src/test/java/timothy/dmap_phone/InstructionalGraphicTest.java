package timothy.dmap_phone;

import org.json.JSONArray;
import org.junit.Test;

import java.util.ArrayList;
import java.util.regex.Pattern;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.json.JSONException;
import org.json.JSONObject;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import andytran.dmap_phone.BuildConfig;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk=21)

/**
 * Created by Kiri on 3/29/2016.
 */
public class InstructionalGraphicTest {
    public static final String ig_name = "Test Graphic";
    public static final Integer ig_interval = 9;
    public static ArrayList<String> ig_image_ids;
    public static ArrayList<String> ig_image_refs;
    public static InstructionalGraphic ig;
    public static JSONObject ig_json;

    @Before
    public void initializeEverything() {
        initializeArrays();
        initializeInstructionalGraphic();
        initializeJSON();
    }

    public void initializeArrays() {
        ig_image_ids = new ArrayList<String>();
        ig_image_refs = new ArrayList<String>();

        int i = 0;
        for(; i < 4; ++i) {
            ig_image_ids.add(String.valueOf(i));
        }
        for(; i < 8; ++i) {
            ig_image_refs.add(String.valueOf(i));
        }
    }

    public void initializeInstructionalGraphic() {
        ig = new InstructionalGraphic(ig_name);
        ig.setInterval(ig_interval);
        for(int i = 0; i < ig_image_ids.size(); ++i) {
            ig.addImage(Integer.valueOf(ig_image_ids.get(i)), ig_image_refs.get(i));
        }
    }

    public JSONObject makeImagesJSON() throws JSONException {
        JSONObject j = new JSONObject();
        for(int i = 0; i < ig_image_ids.size(); ++i) {
            j.put(String.valueOf(ig_image_ids.get(i)), ig_image_refs.get(i));
        }
        return j;
    }

    public void initializeJSON() {
        ig_json = new JSONObject();
        try {
        JSONObject images = makeImagesJSON();
            ig_json.put("name", ig_name);
            ig_json.put("interval", ig_interval);
            ig_json.put("images", images);
        } catch(JSONException je) {
            // TODO: handle this!
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
    public void testGetJSONObject() throws Exception {
        JSONObject json = ig.getJSONObject();
        String json_string = json.toString();
        String ig_json_string = ig_json.toString();
        assertTrue(json_string.equals(ig_json_string));
    }

    @Test
    public void testParseJSON() throws Exception {
        InstructionalGraphic other = InstructionalGraphic.parseJSON(ig_json);
        assertTrue(other.equals(ig));
    }
}