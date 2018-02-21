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
package confessionsManager.view;

import confessionsManager.MainApp;
import confessionsManager.config.ConfigProperties;
import confessionsManager.connectors.facebook.ConnectorUtils;
import confessionsManager.connectors.facebook.FacebookConnector;
import confessionsManager.connectors.facebook.FacebookLoginFLowHandler;

import java.net.CookieManager;
import java.net.CookieHandler;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 */
public class FacebookLoginController implements Initializable {

    private FacebookLoginFLowHandler connector = new FacebookLoginFLowHandler(this);
    private MainApp mainApp;

    @FXML
    private StackPane root;
    
    @FXML
    private WebView webView;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        webView = new WebView();
        CookieHandler.setDefault(new CookieManager());
        WebEngine webEngine = webView.getEngine();
        webEngine.load(ConnectorUtils.buildFacebookLoginUrl("manage_pages"));

        root.getChildren().add(webView);

        webEngine.locationProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldLocation, String newLocation) {
                if (ConnectorUtils.isValidFacebookSignInURL(newLocation)) {
                    connector.handleURL(newLocation);
                }
            }
        });
    }

    public void onSuccess(String pageAccessToken) {
        ConfigProperties.getInstance().setPageAccessToken(pageAccessToken);
        FacebookConnector fbConnector = new FacebookConnector(pageAccessToken);
        if (fbConnector.isAuthenticated()) {
            mainApp.setFacebookConnector(fbConnector);
        } else {
            Modals.showErrorModal(mainApp.getPrimaryStage(),
                    "Facebook Connection Error", 
                    "",
                    "Please try to log in to Facebook again. Thank you");
        }
    }

    public void onError(Map<String, String> errorMap) {
        String text = "Error: "+errorMap.get("error")+System.getProperty("line.separator")+
                "Reason: "+errorMap.get("readon")+System.getProperty("line.separator")+
                "Description: "+errorMap.get("description");
        Modals.showErrorModal(mainApp.getPrimaryStage(),"Facebook Connection Error", "", text);
        //close login dialog
        closeBrowser();
    }

    public void onCancel() {
        //close login dialog
        closeBrowser();
    }

    private void closeBrowser() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
    
    public MainApp getMainApp() {
        return mainApp;
    }

}
