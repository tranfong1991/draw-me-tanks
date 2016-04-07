package andytran.dmap_phone;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.EditText;

import timothy.dmap_phone.InstructionalGraphic;

public class ModifyInstructionalGraphicActivity extends Activity implements NumberPicker.OnValueChangeListener {

    private static final float NUMBER_INITIAL_ITEMS = 1.5F;

    private InstructionalGraphic ig;
    private InstructionalGraphicChangeRecord cr;
    private LinearLayout mCarouselContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_instructional_graphic);

        setInstructionalGraphicandChangeRecord();
        setNumberPicker();
        setCarouselContainer();
    }

    /**
     * set the carousel for displaying the instructional graphic images
     * @return
     */
    private void setCarouselContainer() {
        mCarouselContainer = (LinearLayout) findViewById(R.id.modig_carousel);
    }

    private void setNumberPicker() {
        final NumberPicker np = (NumberPicker) findViewById(R.id.modig_cycle_value);
        np.setMaxValue(99);
        np.setMinValue(1);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        Log.i("value is", "" + newVal);
    }

    /**
     * set instructional graphic to display and the current change record
     * @return
     */
    private void setInstructionalGraphicandChangeRecord() {
        Intent intent = getIntent();
        String ig_string = InstructionalGraphic.class.getName();
        String cr_string = InstructionalGraphicChangeRecord.class.getName();
        ig = (InstructionalGraphic) intent.getSerializableExtra(ig_string);
        cr = (InstructionalGraphicChangeRecord) intent.getSerializableExtra(cr_string);
    }

    /**
     * construct view
     * @param savedInstanceState
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if(ig.numOfFrames() == 0) {
            initializeGraphic();
        }

        createCarousel();
    }

    /**
     * TODO REPLACE LATER
     */
    private void initializeGraphic() {
        cr.addGraphic((new MockGraphicDatabase()).get_random_graphic());
    }

    /**
     * construct carousel part of view
     */
    private void createCarousel() {
        final int imageWidth = getImageWidth();

        for(int i = 0; i < ig.numOfFrames(); ++i) {
            ImageView imageItem = getImageView(imageWidth, i);
            mCarouselContainer.addView(imageItem);
        }
    }

    /**
     * create image item with image at index based on given size
     * @param imageWidth
     * @param index
     * @return
     */
    @NonNull
    private ImageView getImageView(int imageWidth, int index) {
        ImageView imageItem;
        imageItem = new ImageView(this);
        imageItem.setImageResource(ig.idAt(index));
        imageItem.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageWidth));
        return imageItem;
    }

    /**
     * Compute width of carousel item based on screen dimensions and initial item count
     * @return
     */
    private int getImageWidth() {
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return (int) (displayMetrics.widthPixels/NUMBER_INITIAL_ITEMS);
    }
}
