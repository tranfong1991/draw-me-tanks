package andytran.dmap_phone;

import android.content.Intent;
import android.media.Image;
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
    private ProgressBar prog_bar;
    private CountDownTimer countdown_timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_modification);

        setInstructionalGraphic();
        setCarousel();
        setProgressBar();
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

    private void setProgressBar() {

    }

    private void setCarousel() {
        carousel = (CarouselView) findViewById(R.id.preview_modification_carousel);
        carousel.setPageCount(ig.numOfFrames());

        carousel.setImageListener(imageListener);
        if(ig.numOfFrames() > 1) {
            carousel.setSlideInterval(ig.getInterval());
        }
    }

    private void setInstructionalGraphic() {
        Intent intent = getIntent();
        String cr_string = InstructionalGraphic.class.getName();
        ig = (InstructionalGraphic) intent.getSerializableExtra(cr_string);
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView image_view) {
            image_view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            Picasso.with(getApplicationContext())
                    .load(Integer.parseInt(ig.imageRefAt(position)))
                    .fit()
                    .centerInside()
                    .into(image_view);
        }
    };

}
