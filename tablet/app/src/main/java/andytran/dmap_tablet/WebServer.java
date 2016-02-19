package andytran.dmap_tablet;

import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Andy Tran on 2/18/2016.
 */
public class WebServer extends NanoHTTPD {

    public WebServer(){
        super(8080);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String msg = "<html><body><h1>Hello server</h1>\n";
        Map<String, String> parms = session.getParms();
        if (parms.get("username") == null) {
            msg += "<form action='?' method='get'>\n  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
        } else {
            msg += "<p>Hello, " + parms.get("username") + "!</p>";
        }
        return newFixedLengthResponse(msg + "</body></html>\n");
    }

}