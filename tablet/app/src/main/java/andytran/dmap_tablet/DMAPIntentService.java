package andytran.dmap_tablet;

import android.app.IntentService;
import android.content.Intent;

import java.io.IOException;

/**
 * Created by Andy Tran on 2/19/2016.
 * Use IntentService because it runs on a worker thread as opposed to Service which runs on main thread by default
 */
public class DMAPIntentService extends IntentService {
    private DMAPServer server;

    public DMAPIntentService(){
        super("DMAPIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            server = new DMAPServer(this);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
