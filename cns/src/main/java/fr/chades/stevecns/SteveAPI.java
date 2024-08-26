package fr.chades.stevecns;

import fi.iki.elonen.NanoHTTPD;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SteveAPI extends NanoHTTPD {
    public SteveAPI(int port) throws IOException {
        super(port);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        SteveCNS.LOGGER.info("API >> HTTP Server running on port " + port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        Method method = session.getMethod();
        Map<String, List<String>> params = session.getParameters();

        if (uri.equals("/api/data") && method == Method.GET) {
            if (params.containsKey("message") && !params.get("message").isEmpty()) {
                String message = params.get("message").getFirst();
                if (Minecraft.getInstance().player != null && message != null) {
                    Minecraft.getInstance().player.connection.sendChat("I eared: " + message);
                }
            }
            return newFixedLengthResponse(Response.Status.OK, "application/json", "{\"message\": \"Message received :)!\"}");
        }

        // Add more endpoints and logic as needed
        return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not Found");
    }


}
