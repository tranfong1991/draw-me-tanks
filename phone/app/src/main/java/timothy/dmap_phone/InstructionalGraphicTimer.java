package timothy.dmap_phone;

import java.util.Timer;
import java.util.TimerTask;

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
 *  ***********************************************************************************************/
public class InstructionalGraphicTimer extends Timer {
    public static final String EMPTY_GRAPHIC_MESSAGE = "You cannot create a timer for an empty graphic";

/*  Constructor
 *  ==============================================================================================*/
/**
 *  Constructs the timer given a graphic.  You must explicitly call start() for this object to start sending messages.
 *  @param graphic The graphic this timer is responsible for
 */
    public InstructionalGraphicTimer(InstructionalGraphic graphic) {
        if(graphic.numOfFrames() <= 0)
            throw new IllegalArgumentException(EMPTY_GRAPHIC_MESSAGE);
        this.graphic = graphic;
    }

/*  Public Methods
 *  ==============================================================================================*/
/**
 *  Commences a schedule which periodically sends ids to the tablet.
 */
    public void start() {
        if(!started) {
            initialize();
            this.schedule(new TimerTask() {
                @Override public void run() {
                    sendIdToTablet(nextId());
                }
            }, 0, graphic.getInterval());

            started = true;
        }
    }

/*  Private Members
 *  ==============================================================================================*/
    private InstructionalGraphic graphic;
    private Integer currentFrame;
    private Boolean started;

/*  Private Methods
 *  ==============================================================================================*/
/**
 *  @private
 *  Connects with the Tablet and sends the id as payload.
 *  @param id The id to send
 */
    private void sendIdToTablet(Integer id) {
    //  @TODO andy
    }

/**
 *  Prepares the graphic for display.
 */
    private void initialize() {
        currentFrame = 0;
    }

/**
 *  Retrieves the next id to send to the tablet.  Auto-updates itself, so it will always return the next id with subsequent calls.
 */
    private Integer nextId() {
        Integer frame = currentFrame;
        currentFrame = (currentFrame + 1) % graphic.numOfFrames();
        return graphic.idAt(frame);
    }
}
