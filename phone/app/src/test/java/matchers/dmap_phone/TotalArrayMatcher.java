package matchers.dmap_phone;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.ArrayList;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Created by Kiri on 4/21/2016.
 */
public class TotalArrayMatcher<T> extends TypeSafeDiagnosingMatcher<ArrayList<T>> {
    public boolean matchesSafely(ArrayList<T> list, Description mismatch_description) {
        boolean matches = true;
        mismatch_description.appendText("was [");
        int length = list.size() > matchers.size() ? list.size() : matchers.size();
        boolean has_not_printed_before = true;
        for(int i = 0; i < length; ++i) {
            if(matchers.size() <= i) {
                reportMismatch(i, this, list.get(i), mismatch_description, has_not_printed_before);
                matches = false;
            } else if(list.size() <= i) {
                matches = false;
            } else {
                if (!matchers.get(i).matches(list.get(i))) {
                    reportMismatch(i, this, list.get(i), mismatch_description, has_not_printed_before);
                    matches = false;
                } else {
                    reportMismatch(i, this, list.get(i), mismatch_description, has_not_printed_before);
                }
            }
            has_not_printed_before = false;
        }
        mismatch_description.appendText("]");
        return matches;
    }

    public void describeTo(Description description) {
        description.appendText("[");
        for(int i = 0; i < array.size() - 1; ++i) {
            description.appendText(array.get(i).toString()).appendText(", ");
        }
        if(array.size() > 0) {
            description.appendText(array.get(array.size() - 1).toString());
        }
        description.appendText("]");
    }

    ArrayList<Matcher<? extends T>> matchers;
    TotalArrayMatcher<T> matcher;
    ArrayList<T> array;

    public TotalArrayMatcher(ArrayList<T> arr) {
        array = arr;
        matchers = new ArrayList<>();
        for(int i = 0; i < array.size(); ++i) {
            matchers.add(is(equalTo(array.get(i))));
        }
    }

    public void reportMismatch(Integer number, Matcher<?> matcher, Object item,
                               Description mismatch_description, boolean first_mismatch) {
        if(! first_mismatch) {
            mismatch_description.appendText(", ");
        }

        mismatch_description.appendText(item.toString());
    }
}