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

/* Use sigleton design patter to ensure the existance of only one 
 * instance of this class.
 * Use Lazy Initialization as well (single threaded environment, we are OK)
*/

package confessionsManager.config;

import confessionsManager.FormType;
import confessionsManager.PropertiesFile;
import confessionsManager.dao.HashTagDAO;
import confessionsManager.model.HashTag;

public class ConfigProperties {
    
    private HashTag currentHashTag;
    private FormType formType;
    private int confessionNumber;
    private int limit;
    private int hashTagLimit;
    private String formName;
    private String facebookAppID;
    private String facebookAppSecret;
    private String facebookPageID;
    private String pageAccessToken;
    private String googleServiceKeyFilePath;

    private static ConfigProperties instance = null;
    
    private ConfigProperties() {
        this.initConfigProperties();
    }
    
    public static ConfigProperties getInstance() {
        if (instance == null) {
            instance = new ConfigProperties() ;
        }
        return instance;
    }
    
    public String getConfessionNumber() {
        return Integer.toString(confessionNumber);
    }

    public HashTag getCurrentHashTag() {
        return currentHashTag;
    }

    public FormType getFormType() {
        return formType;
    }
    
    public void setTempFormType(FormType type) {
        formType = type;
    }

    public void setPersistantFormType(FormType type) {
        PropertiesFile.writeProperty("formType", type.toString());
        formType = type;
    }
    
    public String getFormName() {
        return formName;
    }
    
    public void setTempFormName(String name) {
        this.formName = name;
    }
    
    public void setPersistantFormName(String name) {
        PropertiesFile.writeProperty("formName", name);
        this.formName = name;
    }
    
    public int getLimit() {
        return limit;
    }
    
    public void setLimit(int limit) {
        PropertiesFile.writeProperty("confessionNumberLimit", Integer.toString(limit));
        this.limit = limit;
    }
    
    public int getHashTagLimit() {
        return hashTagLimit;
    }
    
    public void setHashTagLimit(int limit) {
        PropertiesFile.writeProperty("hashtagRefresh", Integer.toString(limit));
        this.hashTagLimit = limit;
    }

    public void setCurrentHashTag(HashTag hashtag) {
        PropertiesFile.writeProperty("currentHashTagID", Integer.toString(hashtag.getId()));
        currentHashTag = hashtag;
    }

    public void updateConfessionNumber() {
        if (confessionNumber > limit) {
            confessionNumber = 0;
        } else {
            confessionNumber++;
        }
        PropertiesFile.writeProperty("confessionNumber", Integer.toString(confessionNumber));
    }
    
    public String getFacebookAppID() {
        return facebookAppID;
    }
    
    public void setFacebookAppID(String id) {
        PropertiesFile.writeProperty("facebookAppID", id);
        this.facebookAppID = id;
    }
    
    public String getFacebookPageID() {
        return facebookPageID;
    }
    
    public void setFacebookPageID(String id) {
        PropertiesFile.writeProperty("facebookPageID", id);
        this.facebookPageID = id;
    }
    
    public String getFacebookAppSecret() {
        return facebookAppSecret;
    }
    
    public void setFacebookAppSecret(String secret) {
        PropertiesFile.writeProperty("facebookAppSecret", secret);
        this.facebookAppSecret = secret;
    }
    
    public String getPageAccessToken() {
        return pageAccessToken;
    }
    
    public void setPageAccessToken(String token) {
        this.pageAccessToken = token;
    }
    
    public String getGoogleServiceKeyFilePath() {
        return googleServiceKeyFilePath;
    }
    
    public void setGoogleServiceKeyFilePath(String path) {
        PropertiesFile.writeProperty("GoogleServiceKeyFilePath", path);
        googleServiceKeyFilePath = path;
    }
    
    private void initConfigProperties() {
        //get the property value
        confessionNumber = Integer.parseInt(PropertiesFile.readProperty("confessionNumber"));
        limit = Integer.parseInt(PropertiesFile.readProperty("confessionNumberLimit"));
        hashTagLimit = Integer.parseInt(PropertiesFile.readProperty("hashtagRefresh"));
        currentHashTag = new HashTagDAO().getHashTagByID(Integer.parseInt(PropertiesFile.readProperty("currentHashTagID")));
        formName = PropertiesFile.readProperty("formName");
        facebookAppID = PropertiesFile.readProperty("facebookAppID");
        facebookAppSecret = PropertiesFile.readProperty("facebookAppSecret");
        facebookPageID = PropertiesFile.readProperty("facebookPageID");
        pageAccessToken = PropertiesFile.readProperty("facebookPageAccessToken");
        googleServiceKeyFilePath = PropertiesFile.readProperty("GoogleServiceKeyFilePath");
        try {
            formType = FormType.get(PropertiesFile.readProperty("formType"));
        } catch (IllegalArgumentException e) {
            formType = null;
        }
    }

   
}
