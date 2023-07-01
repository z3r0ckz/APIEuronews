package Utils;
import kong.unirest.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;

public class JsonUtils {

    protected static Logger logger = LogManager.getLogger(JsonUtils.class);

    public static void saveAccessTokenToFile(String accessToken) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("token", accessToken);

        try (FileWriter file = new FileWriter("src/main/resources/key.json")) {
            file.write(jsonObject.toString());
            logger.info("The token is saved !");
        } catch (IOException e) {
            logger.error("Error saving access token: " + e.getMessage());
        }
    }
}
