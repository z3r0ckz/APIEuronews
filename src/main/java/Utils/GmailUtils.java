package Utils;

import aquality.selenium.core.utilities.ISettingsFile;
import aquality.selenium.core.utilities.JsonSettingsFile;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Base64;
import javax.naming.AuthenticationException;
import java.util.List;

public class GmailUtils {

    private static final ISettingsFile configData = new JsonSettingsFile("ConfigData.json");
    private static final ISettingsFile apiConfig = new JsonSettingsFile("GmailApiConfig.json");
    protected static ISettingsFile apiKey = new JsonSettingsFile("key.json");
    private final static String token = apiKey.getValue("/token").toString();

    protected static Logger logger = LogManager.getLogger(GmailUtils.class);
    private static HttpRequest getBaseRequestSpecification() {
        return Unirest.get(apiConfig.getValue("/headerHostValue").toString())
                .header("Authorization", "Bearer " + token);
    }

    public static String getUrlToConfirmSubscription() {
        try {
            String messageBody = getBodyMessageOfId(getIdOfUnreadEuronewsEmails().get(0));
            return StringUtils.extractUrlFromHtml(messageBody);
        } catch (AuthenticationException e) {
            logger.error(e);
            return "error get url confirm subscription";
        }
    }


    private static String getBodyMessageOfId(String mailId) {
        getBaseRequestSpecification();
        HttpResponse<String> response = Unirest.get(apiConfig.getValue("/allMessagesEndPoint").toString() + mailId)
                .header("Authorization", "Bearer " + configData.getValue("/accessToken").toString())
                .asString();

        JSONObject responseJson = new JSONObject(response.getBody());
        String encodedString = responseJson.getJSONObject("payload")
                .getJSONArray("parts").getJSONObject(0).getJSONObject("body").getString("data");

        return new String(Base64.getUrlDecoder().decode(encodedString));
    }

    private static List<String> getIdOfUnreadEuronewsEmails() throws AuthenticationException {
        getBaseRequestSpecification();
        HttpResponse<String> response = Unirest.get(apiConfig.getValue("/messagesQueryEndPoint").toString())
                .header("Authorization", "Bearer " + configData.getValue("/accessToken").toString())
                .asString();

        JSONObject jsonResponse = new JSONObject(response.getBody());
        if (!jsonResponse.has("resultSizeEstimate")) {
            throw new AuthenticationException("Please provide correct values of authorization code and access token to the ConfigData.json file");
        }

        JSONArray messages = jsonResponse.getJSONArray("messages");
        List<String> messageIds = new ArrayList<>();
        for (int i = 0; i < messages.length(); i++) {
            JSONObject message = messages.getJSONObject(i);
            messageIds.add(message.getString("id"));
        }

        return messageIds;
    }

    public static Integer markAllEmailsAsRead() {
        try {
            List<String> unreadMessagesIds = getIdOfUnreadEuronewsEmails();
            for (String id : unreadMessagesIds) {
                markMessageAsRead(id);
            }
            return unreadMessagesIds.size();
        } catch (AuthenticationException e) {
           logger.error(e);
            return 0;
        }
    }

    private static void markMessageAsRead(String id) {
        getBaseRequestSpecification();
        HttpResponse<String> response = Unirest.post(apiConfig.getValue("/allMessagesEndPoint").toString() + id + "/modify")
                .header("Authorization", "Bearer " + token)
                .body(apiConfig.getValue("/markMessagesAsReadRequestBody").toString())
                .asString();
    }

    public static boolean isNewMailExists() {
        int numberOfSecondsToWait = Integer.parseInt(configData.getValue("/numberOfSecondsToWaitForNewMail").toString());
        for (int i = 0; i < numberOfSecondsToWait; i++) {
            try {
                HttpResponse<String> response = Unirest.get(apiConfig.getValue("/messagesQueryEndPoint").toString())
                        .header("Authorization", "Bearer " + token)
                        .asString();
                getBaseRequestSpecification();
                int numberOfUnreadEmails = new JSONObject(response.getBody()).getInt("resultSizeEstimate");

                if (numberOfUnreadEmails > 0) {
                    return true;
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }
        return false;
    }


}
