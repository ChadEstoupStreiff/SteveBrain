package fr.chades.stevecns;

import net.minecraft.client.Minecraft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class SteveBrainSynapse {
    private static SteveBrainSynapse instace;
    private String selectedModel = null;

    public static SteveBrainSynapse getInstance() {
        if (instace == null) {
            instace = new SteveBrainSynapse();
        }
        return instace;
    }

    public String thinkAbout(String message) {

        return "";
    }

    public void setup() throws IOException, URISyntaxException {

        URI uri = new URI("http://localhost:11434/api/ps");
        URL url = uri.toURL();

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        // Get the response code
        int responseCode = con.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        // Read the response
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.connection.sendChat("Models: " + response);
        }
    }
}
