package Utils;

import aquality.selenium.core.utilities.ISettingsFile;
import aquality.selenium.core.utilities.JsonSettingsFile;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


public class ConnectAPI {

    protected static Logger logger = LogManager.getLogger(ConnectAPI.class);
    protected static ISettingsFile apiKey = new JsonSettingsFile("key.json");
    private final static String clientId = apiKey.getValue("/client_id").toString();
    private final static String clientSecret = apiKey.getValue("/client_secret").toString();
    private final static String redirectUri = apiKey.getValue("/redirect_uris").toString();
    private final static String code = apiKey.getValue("/code").toString();
    private final static String scope = apiKey.getValue("/scope").toString();
    public static void connectGmailAPI() {

        try {
            // Realiza una solicitud POST a la URL de autenticación de Google
            HttpResponse<JsonNode> response = Unirest.post("https://accounts.google.com/o/oauth2/token")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("code", code)
                    .field("client_id", clientId)
                    .field("client_secret", clientSecret)
                    .field("redirect_uri", redirectUri)
                    .field("grant_type", "authorization_code")
                    .asJson();
            // Verifica si la solicitud fue exitosa
            if (response.getStatus() == 200) {
                // El token de acceso se encuentra en la respuesta
                String accessToken = response.getBody().getObject().getString("access_token");
                logger.info("Token access obtain: " + accessToken);
                JsonUtils.saveAccessTokenToFile(accessToken);
            } else {
                logger.error("Error obtaining access token: " + response.getBody().getObject());
            }
        } catch (Exception e) {
            logger.error(e);
        }

    }

    public static void getAuthorizationCode() {
        try {
            String authorizationUrl = "https://accounts.google.com/o/oauth2/auth" +
                    "?client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8) +
                    "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                    "&scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8) +
                    "&response_type=code";

            // Open the authorization URL in a browser
            openBrowser(authorizationUrl);
        } catch (Exception e) {
            logger.error(e);
        }
    }
    private static void openBrowser(String url) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                logger.error(e);
            }
        }
    }

    public static class JsonUtils {

        protected static Logger logger = LogManager.getLogger(JsonUtils.class);
        public static void saveAccessTokenToFile(String accessToken) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("access_token", accessToken);

            try (FileWriter file = new FileWriter("key.json")) {
                file.write(jsonObject.toString());
                logger.info("The token is saved !");
            } catch (IOException e) {
               logger.error("Error saving access token: " + e.getMessage());
            }
        }
    }
}
