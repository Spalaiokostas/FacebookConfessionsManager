/* 
 * Copyright (C) 2018 Spyros Palaiokostas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package confessionsManager.connectors.facebook;

import com.google.api.client.util.ArrayMap;
import confessionsManager.config.ConfigProperties;
import confessionsManager.view.FacebookLoginController;
import confessionsManager.view.Modals;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.apache.commons.io.IOUtils;

public class FacebookLoginFLowHandler {

    private FacebookLoginController callback;
    FacebookConnector facebookConnector;

    public FacebookLoginFLowHandler(FacebookLoginController controller) {
        this.callback = controller;
    }

    public void handleURL(String url) {
        if (ConnectorUtils.successfulFacebookLogin(url)) {
            handleSuccessfulLogin(url);
        } else if (ConnectorUtils.unsuccessfulFacebookLogin(url)) {
            handleUnsuccessfulLogin(url);
        }
    }

    private void handleSuccessfulLogin(String url) {
        try {
            URL urlObj = new URL(url);
            ArrayMap<String, String> params = ConnectorUtils.parseQuery(urlObj.getRef());
            String extndUserAccessToken = extendUserAccessToken(params.get("access_token"));
            String expires_in = params.get("expires_in");
            if (extndUserAccessToken != null) {
                String pageAccessToken = exchangePageAccessToken(extndUserAccessToken);
                if (pageAccessToken != null) {
                    callback.onSuccess(pageAccessToken);
                } else {
                    Modals.showErrorModal(callback.getMainApp().getPrimaryStage(),
                            "Facebook Connection Error", "Unable to connect to Facebook", 
                            "Error while exchanging UserAcces Token for PageAccess Token");
                }
            } else {
                Modals.showErrorModal(callback.getMainApp().getPrimaryStage(),
                        "Facebook Connection Error", 
                        "Unable to connect to facebook",
                        "Error while extending UserAcces Token");
            }
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void handleUnsuccessfulLogin(String url) {
        try {
            URL urlObj = new URL(url);
            Map<String, String> errorMap = ConnectorUtils.getLoginError(urlObj);
            if (errorMap != null) {
                if (ConnectorUtils.isCanceledLogin(errorMap)) {
                    callback.onCancel();
                }
                callback.onError(errorMap);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public String extendUserAccessToken(String userAccessToken) {
        String extendedUserAccesToken = null;
        try {
            URL url = new URL(ConnectorUtils.buildFacebookExtendAccessTokenUrl(userAccessToken));
            InputStream is = url.openStream();
            String access_token = IOUtils.toString(is, "UTF-8");
            Scanner scanner = new Scanner(access_token);
            scanner.useDelimiter("=");
            if (scanner.hasNext()) {
                //assumes the line has a certain structure
                String name = scanner.next();
                String value = scanner.next();
                if (name.equals("access_token")) {
                    extendedUserAccesToken = value;
                }
            } else {
                Modals.showErrorModal(callback.getMainApp().getPrimaryStage(),
                        "Facebook Connection Error", 
                        "Unable to connect to Facebook",
                        "Error while parsing UserAccess Token. Please contact the developer");
            }
        } catch (IOException ex) {
            Logger.getLogger(FacebookConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return extendedUserAccesToken;
    }

    public String exchangePageAccessToken(String extendedUserToken) {
        String urlString = "https://graph.facebook.com/me/accounts?access_token=" + extendedUserToken;
        String pageAccessToken = null;
        try {
            URL url = new URL(urlString);
            InputStream is = url.openStream();
            JsonReader rdr = Json.createReader(is);

            JsonObject obj = rdr.readObject();
            JsonArray results = obj.getJsonArray("data");
            for (JsonObject result : results.getValuesAs(JsonObject.class)) {
                String pageID = result.getString("id");
                if (ConfigProperties.getInstance().getFacebookPageID().equals(pageID)) {
                    pageAccessToken = result.getString("access_token");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(FacebookConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pageAccessToken;
    }
}
