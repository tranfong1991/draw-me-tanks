package matchers.dmap_phone;

import org.hamcrest.Description;
import static org.hamcrest.Matchers.*;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import timothy.dmap_phone.InstructionalGraphic;

/**
 * Created by Kiri on 4/21/2016.
 */
public class InstructionalGraphicIsEqual extends TypeSafeDiagnosingMatcher<InstructionalGraphic> {
    InstructionalGraphic other;

    @Override
    public boolean matchesSafely(InstructionalGraphic ig, Description mismatch_description) {
        boolean matches = true;
        ArrayList<Integer> ig_ids = makeIds(ig);
        ArrayList<String> ig_image_refs = makeImageRefs(ig);
        mismatch_description.appendText("{");
        if(!name.matches(ig.getName())) {
            reportMismatch("name", name, ig.getName(), mismatch_description, matches);
            matches = false;
        }
        if(!interval.matches(ig.getInterval())) {
            reportMismatch("interval", interval, ig.getInterval(), mismatch_description, matches);
            matches = false;
        }
        if(!numOfFrames.matches(ig.numOfFrames())) {
            reportMismatch("number of frames", numOfFrames, ig.numOfFrames(), mismatch_description, matches);
            matches = false;
        }
        if(!ids.matches(ig_ids)) {
            reportMismatch("ids", ids, ig_ids, mismatch_description, matches);
            matches = false;
        }
        if(!imageRefs.matches(ig_image_refs)) {
            reportMismatch("image refs", imageRefs, ig_image_refs, mismatch_description, matches);
            matches = false;
        }
        mismatch_description.appendText("}");
        return matches;
    }

    private final Matcher<? super String> name;
    private final Matcher<? super Integer> interval;
    private final Matcher<? super Integer> numOfFrames;
    private final TotalArrayMatcher<? super Integer> ids;
    private final TotalArrayMatcher<? super String> imageRefs;

    public void describeTo(Description description) {
        description.appendText("{instructional graphic: ")
                .appendText("\n\t\t\tname ")
                .appendDescriptionOf(name)
                .appendText("\n\t\t\tinterval ")
                .appendDescriptionOf(interval)
                .appendText("\n\t\t\tnumber of frames ")
                .appendDescriptionOf(numOfFrames)
                .appendText("\n\t\t\tids ")
                .appendDescriptionOf(ids)
                .appendText("\n\t\t\timage refs ")
                .appendDescriptionOf(imageRefs);
    }

    public void reportMismatch(String name, Matcher<?> matcher, Object item,
                               Description mismatch_description, boolean first_mismatch) {
        if(! first_mismatch) {
            mismatch_description.appendText("\n\t\t\t");
        }

        mismatch_description.appendText(name).appendText(" ");
        matcher.describeMismatch(item, mismatch_description);
    }

    public InstructionalGraphicIsEqual(InstructionalGraphic ig) {
        name = is(equalTo(ig.getName()));
        interval = is(equalTo(ig.getInterval()));
        numOfFrames = is(equalTo(ig.numOfFrames()));
        ids = new TotalArrayMatcher<>(makeIds(ig));
        imageRefs = new TotalArrayMatcher<>(makeImageRefs(ig));
    }

    private ArrayList<Matcher<? extends Integer>> makeIdsMatchers(InstructionalGraphic ig) {
        ArrayList<Matcher<? extends Integer>> ids = new ArrayList<Matcher<? extends Integer>>();
        for(int i = 0; i < ig.numOfFrames(); ++i) {
            ids.add(is(equalTo(ig.idAt(i))));
        }
        return ids;
    }

    private ArrayList<Matcher<? extends String>> makeImageRefsMatchers(InstructionalGraphic ig) {
       ArrayList<Matcher<? extends String>> imageRefs = new ArrayList<Matcher<? extends String>>();
        for(int i = 0; i < ig.numOfFrames(); ++i) {
            imageRefs.add(is(equalTo(ig.imageRefAt(i))));
        }
        return imageRefs;
    }

    private ArrayList<Integer> makeIds(InstructionalGraphic ig) {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for(int i = 0; i < ig.numOfFrames(); ++i) {
            ids.add(ig.idAt(i));
        }
        return ids;
    }

    private ArrayList<String> makeImageRefs(InstructionalGraphic ig) {
        ArrayList<String> imageRefs = new ArrayList<String>();
        for(int i = 0; i < ig.numOfFrames(); ++i) {
            imageRefs.add(ig.imageRefAt(i));
        }
        return imageRefs;
    }
}
