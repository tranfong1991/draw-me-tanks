package andytran.dmap_phone;

import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.synnapps.carouselview.ImageListener;

import timothy.dmap_phone.InstructionalGraphic;

public class ModifyInstructionalGraphicActivity extends Activity implements NumberPicker.OnValueChangeListener {

    private InstructionalGraphic ig;
    private InstructionalGraphicChangeRecord cr;

    CarouselView carousel;
    ImageListener image_listener;
    Button preview_button;
    Button ok_button;
    Button delete_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_instructional_graphic);

        setInstructionalGraphicAndChangeRecord();
        setNumberPicker();
        setEditText();
        setCarouselContainer();
        setPreviewButton();
        setOkButton();
        setDeleteButton();
    }

    /**
     * set the carousel for displaying the instructional graphic images
     * @return
     */
    private void setCarouselContainer() {
        initializeCarousel();
        initializeImageListener();
        carousel.setImageListener(image_listener);
    }

    private void initializeImageListener() {
        image_listener = new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView image_view) {
                image_view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                if(position == ig.numOfFrames()) {
                    image_view.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            addToGraphic();
                        }
                    });
                }

                Picasso.with(getApplicationContext())
                        .load(getImage(position))
                        .fit()
                        .centerInside()
                        .into(image_view);
            }

            private int getImage(int position) {
                if(position < ig.numOfFrames()) {
                    return Integer.parseInt(ig.imageRefAt(position));
                } else {
                    return R.drawable.dragicon;
                }
            }
        };
    }

    private void initializeCarousel() {
        carousel = (CarouselView) findViewById(R.id.carousel);
        if(ig.numOfFrames() >= 15) {
            carousel.setPageCount(ig.numOfFrames());    // Test phone could hold at most 17 frames on one page
        } else {
            carousel.setPageCount(ig.numOfFrames() + 1);
        }
    }

    private void setNumberPicker() {
        final NumberPicker np = (NumberPicker) findViewById(R.id.modig_cycle_value);
        np.setMaxValue(99);
        np.setMinValue(1);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
        if(ig.getInterval()/1000 <= np.getMaxValue()) {
            np.setValue(ig.getInterval() / 1000);
        }
        if(ig.numOfFrames() < 2) {
            np.setEnabled(false);
        }
    }

    private void setEditText() {
        EditText editText = (EditText)findViewById(R.id.modig_instructional_graphic_title);
        editText.setText(ig.getName(), TextView.BufferType.EDITABLE);
    }

    private void setPreviewButton() {
        preview_button = (Button) findViewById(R.id.modig_preview_button);
        preview_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("clicked", "preview");
            }
        });
    }

    private void setOkButton() {
        ok_button = (Button) findViewById(R.id.modig_ok_button);
        ok_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("clicked", "ok");
                carousel.setCurrentItem(1);
            }
        });
    }

    private void setDeleteButton() {
        delete_button = (Button) findViewById(R.id.modig_delete_button);
        delete_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("clicked", "delete");
                removeFromGraphic();
            }
        });
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        Log.i("value is", "" + newVal);
        cr.setInterval(newVal);
    }

    /**
     * set instructional graphic to display and the current change record
     * @return
     */
    private void setInstructionalGraphicAndChangeRecord() {
        Intent intent = getIntent();
        setChangeRecord(intent);
        setInstructionalGraphic();
    }

    private void setInstructionalGraphic() {
        ig = cr.getCurrentInstructionalGraphic();
        if(ig.numOfFrames() == 0) {
            setEditText();
            addToGraphic();
        }
    }

    private void setChangeRecord(Intent intent) {
        String cr_string = InstructionalGraphicChangeRecord.class.getName();
        cr = (InstructionalGraphicChangeRecord) intent.getSerializableExtra(cr_string);
    }

    private void removeFromGraphic() {
        cr.removeGraphic();
        getData();
        this.recreate();
    }

    /**
     * TODO REPLACE LATER
     */
    private void addToGraphic() {
        cr.addGraphic((new MockGraphicDatabase()).get_random_graphic());
        getData();
        this.recreate();
    }

    private void getInterval() {
        final NumberPicker np = (NumberPicker) findViewById(R.id.modig_cycle_value);
        Integer interval = np.getValue()*1000;
        cr.setInterval(interval);
    }

    private void getName() {
        EditText edit_text   = (EditText)findViewById(R.id.modig_instructional_graphic_title);
        String name = edit_text.getText().toString();
        cr.setName(name);
    }

    private void getData() {
        getInterval();
        getName();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        cr.cancel();
        finish();

    }
}
