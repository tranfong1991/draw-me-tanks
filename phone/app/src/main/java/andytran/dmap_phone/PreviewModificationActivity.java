package andytran.dmap_phone;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import timothy.dmap_phone.InstructionalGraphic;

public class PreviewModificationActivity extends AppCompatActivity {

    private CarouselView carousel;
    private InstructionalGraphic ig;
    private Integer start_of_gallery_graphics;
    private Context context = this;

    public static final String startOfGalleryGraphicCode = "StartOfGalleryGraphics";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_modification);

        Intent intent = getIntent();
        setInstructionalGraphic(intent);
        setStartOfGalleryGraphic(intent);
        setCarousel();
        setImageView();
    }

    private void setImageView() {
        ImageView iv = (ImageView) findViewById(R.id.preview_modification_click_blocker);
        iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    private void setCarousel() {
        carousel = (CarouselView) findViewById(R.id.preview_modification_carousel);
        carousel.setPageCount(ig.numOfFrames());

        carousel.setImageListener(imageListener);
        if(ig.numOfFrames() > 1) {
            carousel.setSlideInterval(ig.getInterval());
        }
    }

    private void setStartOfGalleryGraphic(Intent intent) {
        String start_of_gallery_graphics_string = (String) intent.getSerializableExtra(startOfGalleryGraphicCode);
        start_of_gallery_graphics = Integer.valueOf(start_of_gallery_graphics_string);
    }

    private void setInstructionalGraphic(Intent intent) {
        String cr_string = InstructionalGraphic.class.getName();
        ig = (InstructionalGraphic) intent.getSerializableExtra(cr_string);
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView image_view) {
            image_view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            Uri image;
            if(position < start_of_gallery_graphics) {
                image = Utils.refToUri(context, ig.imageRefAt(position));
            } else {
                image = Uri.parse(ig.imageRefAt(position));
            }
            Picasso.with(getApplicationContext())
                    .load(image)
                    .fit()
                    .centerInside()
                    .into(image_view);
        }
    };

}
