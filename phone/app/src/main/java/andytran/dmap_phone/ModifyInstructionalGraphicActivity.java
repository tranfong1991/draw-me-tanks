package andytran.dmap_phone;

import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.synnapps.carouselview.ViewListener;

import timothy.dmap_phone.InstructionalGraphic;

public class ModifyInstructionalGraphicActivity extends ImageManagerActivity implements NumberPicker.OnValueChangeListener {

    private InstructionalGraphic ig;
    private InstructionalGraphicChangeRecord cr;

    CarouselView carousel;
    ViewListener view_listener;
    Button preview_button;
    Button ok_button;

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
    }

    /**
     * set the carousel for displaying the instructional graphic images
     * @return
     */
    private void setCarouselContainer() {
        initializeCarousel();
        initializeViewListener();
        carousel.setViewListener(view_listener);
    }

    private void initializeViewListener() {
        view_listener = new ViewListener() {
            @Override
            public View setViewForPosition(int position) {
                View custom_view;
                if(position < ig.numOfFrames() - 1) {
                    custom_view = getDisplayGraphicView(position);
                } else if(position == ig.numOfFrames() - 1) {
                    custom_view = getDeleteGraphicView(position);
                } else {
                    custom_view = getAddGraphicView();
                }
                return custom_view;
            }

            @NonNull
            private View getAddGraphicView() {
                View custom_view = getLayoutInflater().inflate(R.layout.content_modig_carousel_add, null);
                FloatingActionButton add_button = (FloatingActionButton) custom_view.findViewById(R.id.modig_carousel_add_button);
                add_button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        addToGraphic();
                    }
                });
                return custom_view;
            }

            private View getDisplayGraphicView(int position) {
                View custom_view = getLayoutInflater().inflate(R.layout.content_modig_carousel, null);
                setImage(position, custom_view);
                return custom_view;
            }

            @NonNull
            private View getDeleteGraphicView(int position) {
                View custom_view = getLayoutInflater().inflate(R.layout.content_modig_carousel_delete, null);

                setImage(position, custom_view);
                setDeleteButton(custom_view);

                return custom_view;
            }

            private void setDeleteButton(View custom_view) {
                ImageButton delete_button = (ImageButton) custom_view.findViewById(R.id.modig_carousel_delete_button);
                delete_button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Log.i("clicked", "delete");
                        removeFromGraphic();
                    }
                });
            }

            private void setImage(int position, View custom_view) {
                ImageView image_view = (ImageView) custom_view.findViewById(R.id.modig_carousel_image_view);
                Picasso.with(getApplicationContext())
                        .load(getImage(position))
                        .fit()
                        .centerInside()
                        .into(image_view);
            }
        };
    }

    private Uri getImage(int position) {
        if(position < ig.numOfFrames()) {
            Log.i(String.valueOf(position), ig.imageRefAt(position).substring(6) + ", " + ig.idAt(position));
            return Uri.parse(ig.imageRefAt(position));
        } else {
            throw new IndexOutOfBoundsException("Modify Graphic UI attempted to load an invalid image");
        }
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
        if (ig.numOfFrames() < 2) {
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
                getData();
                sendPreviewIntent();
            }
        });
    }

    private void sendPreviewIntent() {
        Intent intent = new Intent(this, PreviewModificationActivity.class);
        intent.putExtra(InstructionalGraphic.class.getName(), cr.getCurrentInstructionalGraphic());
        startActivity(intent);
        return;
    }

    private void setOkButton() {
        ok_button = (Button) findViewById(R.id.modig_ok_button);
        ok_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("clicked", "ok");
                image_refs = cr.getUris();
                submitImages(cr.getOriginalInstructionalGraphic());
                finish();
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
        if(cr == null) {
            Intent intent = getIntent();
            setChangeRecord(intent);
            setInstructionalGraphic();
        }
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

    private void addToGraphic() {
        // TODO Test this for if someone cancels selecting from their gallery!!
        openImageGallery();
    }

    private void getInterval() {
        final NumberPicker np = (NumberPicker) findViewById(R.id.modig_cycle_value);
        Integer interval = np.getValue()*1000;
        if(interval > 0) {
            cr.setInterval(interval);
        }
    }

    private void getName() {
        EditText edit_text   = (EditText)findViewById(R.id.modig_instructional_graphic_title);
        String name = edit_text.getText().toString();
        if(name.length() > 0) {
            cr.setName(name);
        }
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

    @Override
    protected void onActivityResult(int request_code, int result_code, Intent data) {
        super.onActivityResult(request_code, result_code, data);
        if(result_code == RESULT_OK) {
            cr.addGraphic(image_refs.get(image_refs.size() - 1).toString());
            getData();
            this.recreate();
        }
    }
}
