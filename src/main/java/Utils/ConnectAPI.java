package Utils;

import aquality.selenium.core.utilities.ISettingsFile;
import aquality.selenium.core.utilities.JsonSettingsFile;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
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
    private final static String tokenUrl = apiKey.getValue("/tokenUrl").toString();
    public static void getToken() {

        try {
            // Realiza una solicitud POST a la URL de autenticación de Google
            HttpResponse<JsonNode> response = Unirest.post(tokenUrl)
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

    public static String getAuthorizationCode() {
        String autoUrl = null;
        String authorizationUrl = null;
        try {
            authorizationUrl = String.format("https://accounts.google.com/o/oauth2/auth?client_id=%s&redirect_uri=%s&scope=%s&response_type=code",
                    URLEncoder.encode(clientId, StandardCharsets.UTF_8),
                    URLEncoder.encode(redirectUri, StandardCharsets.UTF_8),
                    URLEncoder.encode(scope, StandardCharsets.UTF_8));

        // Open the authorization URL in a browser
            openBrowser(authorizationUrl);
            autoUrl =   authorizationUrl;
        } catch (Exception e) {
            logger.error(e);
        }
        return autoUrl;
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


}
