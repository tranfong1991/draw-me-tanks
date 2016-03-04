package timothy.dmap_phone;

import java.util.Timer;
import java.util.TimerTask;


public class SlideshowTimer extends Timer {
    private Slideshow slideshow;

    public SlideshowTimer(Slideshow slideshow){
        this.slideshow = slideshow;
    }

    public void start(){

        this.schedule(new TimerTask() {

            @Override
            public void run() {
                sendIDToTablet(slideshow.nextId());
            }
        }, 0, slideshow.getInterval());
    }

    private void sendIDToTablet(Integer id){
        ;//
        //Andy
    }
}
