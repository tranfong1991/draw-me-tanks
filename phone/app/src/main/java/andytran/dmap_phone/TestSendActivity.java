package andytran.dmap_phone;

import android.app.Activity;
import android.os.Bundle;

import timothy.dmap_phone.InstructionalGraphic;
import timothy.dmap_phone.InstructionalGraphicTimer;

public class TestSendActivity extends Activity {

    private InstructionalGraphicTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_send);

        InstructionalGraphic g = new InstructionalGraphic("test");
        g.addImage(1, "test");
        g.addImage(2, "test");
        g.addImage(3, "test");
        g.setInterval(2000);

        timer = new InstructionalGraphicTimer(this, "10.201.148.200", "8080", "abc", g);

        //findViewById();
    }
}
