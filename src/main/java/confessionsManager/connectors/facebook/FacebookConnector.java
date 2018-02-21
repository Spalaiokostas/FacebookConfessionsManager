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

import confessionsManager.config.ConfigProperties;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.exception.FacebookException;
import com.restfb.exception.FacebookOAuthException;
import com.restfb.types.FacebookType;
import com.restfb.types.Page;
import com.restfb.types.User;


public class FacebookConnector {

    private String pageAccessToken;

    private FacebookClient fbClient;
    private User FBuser = null;
    private Page FBpage = null;

    private boolean authenticated;

    public FacebookConnector(String pageAccessToken) {
        this.pageAccessToken = pageAccessToken;
        try {
            fbClient = new DefaultFacebookClient(pageAccessToken, Version.VERSION_2_8);
            FBuser = fbClient.fetchObject("me", User.class);
        } catch (FacebookOAuthException e) {
            authenticated = false;
            fbClient = null;
        } finally {
            authenticated = true;
        }
    }

    public String getPageAccessToken() {
        return pageAccessToken;
    }

    public void setPageAccessToken(String pageAccessToken) {
        this.pageAccessToken = pageAccessToken;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean auth) {
        authenticated = auth;
    }

    public boolean post(String message) {
        boolean result = false;
        if (authenticated && (fbClient != null)) {
            try {
                fbClient.publish(ConfigProperties.getInstance().getFacebookPageID() + "/feed", FacebookType.class, Parameter.with("message", message));
                result = true;
            } catch (FacebookException ex) {
                ex.printStackTrace(System.err);
                throw new RuntimeException("Something went wrong while posting!");
            }
        }
        return result;
    }
}
