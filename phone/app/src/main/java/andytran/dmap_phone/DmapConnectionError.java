package andytran.dmap_phone;

public class DmapConnectionError extends Error {
    public DmapConnectionError() {  super(); }
    public DmapConnectionError(String err) {  super(err); }

    @Override public String getMessage() {
        return "The phone is not properly connected to the tablet.  " + super.getMessage();
    }
}
