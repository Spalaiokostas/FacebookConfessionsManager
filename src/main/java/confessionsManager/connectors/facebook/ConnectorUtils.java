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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class ConnectorUtils {

    public static boolean isValidFacebookSignInURL(String url) {
        try {
            URL urlObj = new URL(url);
            if (!urlObj.getProtocol().equals("https")
                    || !urlObj.getAuthority().equals("www.facebook.com")
                    || !urlObj.getAuthority().equals("www.facebook.com")
                    || !urlObj.getHost().equals("www.facebook.com")
                    || !urlObj.getPath().equals("/connect/login_success.html")) {
                return false;
            }
        } catch (MalformedURLException e) {

        }
        return successfulFacebookLogin(url) || unsuccessfulFacebookLogin(url);
    }

    public static boolean successfulFacebookLogin(String url) {
        try {
            URL urlObj = new URL(url);
            String ref = urlObj.getRef();
            if (ref == null) {
                return false;
            }
            if (!ref.contains("access_token=") || !ref.contains("expires_in=")) {
                return false;
            }
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }

    public static boolean unsuccessfulFacebookLogin(String url) {
        try {
            URL urlObj = new URL(url);
            String query = urlObj.getQuery();
            if (query == null) {
                return false;
            }
            if (!query.contains("error=")
                    || !query.contains("error_code=")
                    || !query.contains("error_description=")
                    || !query.contains("error_reason=")) {
                return false;
            }
        } catch (MalformedURLException e) {

        }
        return true;
    }

    public static ArrayMap<String, String> parseQuery(String query) throws UnsupportedEncodingException {
        ArrayMap<String, String> params = new ArrayMap<>();
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            String key = URLDecoder.decode(pair[0], "UTF-8");
            String value = URLDecoder.decode(pair[1], "UTF-8");
            params.put(key, value);
        }
        return params;
    }

    public static Map<String, String> getLoginError(URL urlObj) {
        Map<String, String> map = new HashMap<>();
        try {
            ArrayMap<String, String> params = ConnectorUtils.parseQuery(urlObj.getRef());
            map.put("reason", params.get("error_reason"));
            map.put("error", params.get("error"));
            map.put("description", params.get("error_description"));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static boolean isCanceledLogin(Map<String, String> errorMap) {
        return errorMap.get("reason").equals("user_denied")
                && errorMap.get("error").equals("access_denied")
                && errorMap.get("description").equals("The+user+denied+your+request");
    }

    public static String buildFacebookLoginUrl(String permissions) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("https://www.facebook.com/dialog/oauth?client_id=");
        stringBuffer.append(ConfigProperties.getInstance().getFacebookAppID());
        stringBuffer.append("&redirect_uri=https://www.facebook.com/connect/login_success.html&scope=");
        stringBuffer.append(permissions);
        stringBuffer.append("&response_type=token");
        return stringBuffer.toString();
    }

    public static String buildFacebookExtendAccessTokenUrl(String UserAccessToken) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("https://graph.facebook.com/oauth/access_token?grant_type=fb_exchange_token&client_id=");
        stringBuffer.append(ConfigProperties.getInstance().getFacebookAppID());
        stringBuffer.append("&client_secret=");
        stringBuffer.append(ConfigProperties.getInstance().getFacebookAppSecret());
        stringBuffer.append("&fb_exchange_token=");
        stringBuffer.append(UserAccessToken);
        return stringBuffer.toString();
    }

}
