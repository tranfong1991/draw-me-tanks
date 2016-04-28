package timothy.dmap_phone;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import andytran.dmap_phone.DmapConnectionError;
import andytran.dmap_phone.IntegerCallback;
import andytran.dmap_phone.Utils;
import andytran.dmap_phone.VoidCallback;

/** InstructionalGraphicTimer Class
 *  @author Timothy Foster, Karrie Cheng
 *
 *  This class is responsible for actually sending ids to the tablet for display.  This is a timer,
 *  representing Box [ID HERE] in the IDEF0.  Given a graphic, the timer will consult the graphic
 *  for ids to display.  The timer, meanwhile, is fully responsible for knowing which id to send
 *  and when.  The InstructionalGraphic class, therefore, only keeps a storage of the graphic and
 *  knows nothing about the tablet.
 *
 *  @usage
 *  InstructionalGraphicTimer timer = new InstructionalGraphicTimer(graphic);
 *  timer.start();
 *  timer.cancel(); // stops the timer
 *
 *  @TODO
 *  ***********************************************************************************************/
public class InstructionalGraphicTimer extends Timer {
    private static final String TAG = InstructionalGraphicTimer.class.getSimpleName();

    public static final String EMPTY_GRAPHIC_MESSAGE = "You cannot create a timer for an empty graphic";

/*  Constructor
 *  ==============================================================================================*/
/**
 *  Constructs the timer given a graphic.  You must explicitly call start() for this object to start sending messages.
 *  @param context The current activity
 *  @param ip The IP to send to
 *  @param port The Port number
 *  @param token The access token
 *  @param graphic The graphic this timer is responsible for
 */
    public InstructionalGraphicTimer(Context context, String ip, String port, String token, InstructionalGraphic graphic) {
        if(graphic.numOfFrames() <= 0)
            throw new IllegalArgumentException(EMPTY_GRAPHIC_MESSAGE);
        this.context = context;
        this.ip = ip;
        this.port = port;
        this.token = token;
        this.graphic = graphic;
        started = false;

        setOnSendSuccess(new IntegerCallback() {  @Override public void run(Integer n) {} });
        setOnStopSuccess(new VoidCallback() {  @Override public void run() {} });
    }

/*  Public Methods
 *  ==============================================================================================*/
/**
 *  Commences a schedule which periodically sends ids to the tablet.
 */
    public void start() {
        if(!started) {
            initialize();
            if (graphic.getInterval() == 0)
                sendIdToTablet(graphic.idAt(0));
            else {
                this.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        sendIdToTablet(nextId());
                    }
                }, 0, graphic.getInterval());
                started = true;
            }
        }
    }

    public void halt() {
        if (started) {
            this.cancel();
            started = false;
            onStopSuccess.run();
        }
    }

/**
 *  Stops the graphic from sending requests to the timer, and sends a stop request so the
 *  tablet stops displaying its current image
 */
    public void stop() {
        if (started) {
            this.cancel();
            started = false;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("token", token);
        Utils.sendPackage(
                context,
                Request.Method.GET,
                Utils.buildURL(ip, port, "stop", params),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        onStopSuccess.run();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.error(context, new DmapConnectionError("Stop message could not be sent to tablet").getMessage()).show();
                    }
                }
        );
    }


    public Integer getCurrentFrame(){
        return currentFrame;
    }

    public void setOnSendSuccess(IntegerCallback cb) {
        onSendSuccess = cb;
    }

    public void setOnStopSuccess(VoidCallback cb) {
        onStopSuccess = cb;
    }

/*  Private Members
 *  ==============================================================================================*/
    private Context context;
    private String ip;
    private String port;
    private String token;
    private InstructionalGraphic graphic;
    private Integer prevFrame;
    private Integer currentFrame;
    private Boolean started;

    private IntegerCallback onSendSuccess;
    private VoidCallback onStopSuccess;

/*  Private Methods
 *  ==============================================================================================*/
/**
 *  @private
 *  Connects with the Tablet and sends the id as payload.
 *  @param id The id to send
 */
    private void sendIdToTablet(Integer id) {
        final Integer frameSentOn = prevFrame;
        HashMap<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("id", id.toString());

        Utils.sendPackage(
                context,
                Request.Method.GET,
                Utils.buildURL(ip, port, "play", params),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        onSendSuccess.run(frameSentOn);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (started) {
                            cancel();
                            started = false;
                        }
                        Utils.error(context, new DmapConnectionError("Unable to call image on tablet").getMessage()).show();
                    }
                }
        );
    }


/**
 *  Prepares the graphic for display.
 */
    private void initialize() {
        prevFrame = 0;
        currentFrame = 0;
    }

/**
 *  Retrieves the next id to send to the tablet.  Auto-updates itself, so it will always return the next id with subsequent calls.
 */
    private Integer nextId() {
        prevFrame = currentFrame;
        currentFrame = (currentFrame + 1) % graphic.numOfFrames();
        return graphic.idAt(prevFrame);
    }
}
