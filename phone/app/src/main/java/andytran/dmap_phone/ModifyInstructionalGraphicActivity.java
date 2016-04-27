package andytran.dmap_phone;

import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.synnapps.carouselview.ViewListener;

import timothy.dmap_phone.InstructionalGraphic;

public class ModifyInstructionalGraphicActivity extends ImageManagerActivity implements NumberPicker.OnValueChangeListener {

    public static final String isNewIntentCode = "IsNewIntent";

    private InstructionalGraphic ig;
    private InstructionalGraphicChangeRecord cr;
    private Context context;

    private Boolean new_graphic;

    private CarouselView carousel;
    private ViewListener view_listener;
    private Button preview_button;
    private Button ok_button;
    private TextView editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_instructional_graphic);
        context = this;

        Intent intent = getIntent();
        if(cr == null) {
            setIfNewGraphic(intent);
            setChangeRecord(intent);
            setInstructionalGraphic();
        }
        setNumberPicker();
        setStaticText();
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
        if(position < cr.getIndexFirstNewGraphic()) {
            return Utils.refToUri(context, ig.imageRefAt(position));
        } else if(position < ig.numOfFrames()) {
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
        Log.d("new graphic?", String.valueOf(new_graphic));
        editText = (TextView)findViewById(R.id.modig_instructional_graphic_title_editable);
        if(!new_graphic) {
            editText.setText(ig.getName(), TextView.BufferType.EDITABLE);
            editText.setVisibility(View.INVISIBLE);
            editText.setEnabled(false);
        } else {
            editText.setVisibility(View.VISIBLE);
            editText.setEnabled(true);
        }
    }

    private void setStaticText() {
        editText = (TextView)findViewById(R.id.modig_instructional_graphic_title_static);
        editText.setText(ig.getName(), TextView.BufferType.NORMAL);
        if(new_graphic) {
            editText.setVisibility(View.INVISIBLE);
        } else {
            editText.setVisibility(View.VISIBLE);
        }
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
        intent.putExtra(PreviewModificationActivity.startOfGalleryGraphicCode, String.valueOf(cr.getIndexFirstNewGraphic()));
        startActivity(intent);
        return;
    }

    private void setOkButton() {
        ok_button = (Button) findViewById(R.id.modig_ok_button);
        ok_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("clicked", "ok");

                getData();
                if (editText.getText().toString().isEmpty()) {
                    Toast.makeText(context, "Please name your new instruction", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (db.isGraphicInDatabase(editText.getText().toString()) && new_graphic) {
                    Toast.makeText(context, "Sorry, you already have an instruction with this name", Toast.LENGTH_SHORT).show();
                    return;
                }

                image_refs = cr.getUris(context);
                cr.finalizeChanges(new_graphic);
                submitImages(cr.getOriginalInstructionalGraphic(), new VoidCallback() {
                    @Override
                    public void run() {
                        myOkFinish();
                    }
                });
            }
        });
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        Log.i("value is", "" + newVal);
        cr.setInterval(newVal * 1000);
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

    private void setIfNewGraphic(Intent intent) {
        String string_bool = (String) intent.getSerializableExtra(isNewIntentCode);
        Log.d("String bool", string_bool);
        new_graphic = Boolean.parseBoolean(string_bool);
        Log.d("first new graphic", String.valueOf(new_graphic));
    }

    private void removeFromGraphic() {
        cr.removeGraphic();
        getData();
        this.recreate();
    }

    private void addToGraphic() {
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
        EditText edit_text = (EditText)findViewById(R.id.modig_instructional_graphic_title_editable);
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
        myCancelledFinish();
    }

    @Override
    protected void onActivityResult(int request_code, int result_code, Intent data) {
        super.onActivityResult(request_code, result_code, data);

        if(result_code == RESULT_OK) {
            cr.addGraphic(image_refs.get(image_refs.size() - 1).toString());
            getData();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                        finish();
                        startActivity(getIntent());
                    } else recreate();
                }
            }, 1);

        } else if(result_code == RESULT_CANCELED && ig.numOfFrames() == 0) {
            myCancelledFinish();
        }
    }

    public void myOkFinish() {
        if (getParent() == null) {
            setResult(Activity.RESULT_OK, getIntent());
        } else {
            getParent().setResult(Activity.RESULT_OK, getIntent());
        }
        finish();
    }

    public void myCancelledFinish() {
        if (getParent() == null) {
            setResult(Activity.RESULT_CANCELED, getIntent());
        } else {
            getParent().setResult(Activity.RESULT_CANCELED, getIntent());
        }
        finish();
    }
}
