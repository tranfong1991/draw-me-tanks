package andytran.dmap_phone;

import android.app.Activity;
import android.os.Bundle;

public class ModigMainActivity extends Activity {

    int[] mResources = {
            R.drawable.calm,
            R.drawable.cleat_finished,
            R.drawable.dontstand,
            R.drawable.dosit,
            R.drawable.leftboat,
            R.drawable.rightboat
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modig_main);
    }
}
