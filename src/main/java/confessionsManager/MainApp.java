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
package confessionsManager;

import confessionsManager.config.ConfigProperties;
import confessionsManager.connectors.facebook.FacebookConnector;
import confessionsManager.connectors.googleSpreadSheets.GoogleSpreadSheetsConnector;
import confessionsManager.model.Confession;
import confessionsManager.dao.ConfessionDAO;
import confessionsManager.model.HashTag;
import confessionsManager.view.HashtagListController;
import confessionsManager.view.ConfessionsController;
import confessionsManager.view.RootLayoutController;

import java.io.IOException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.util.ArrayList;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;


public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private ObservableList<Confession> confessions;
    private HashTag currentHashTag;
    
    private FacebookConnector facebookConnector = null;
    private GoogleSpreadSheetsConnector googleConnector = null;
    private ConfessionsController responsesController;
    
    //properties
    private final ConfigProperties properties = ConfigProperties.getInstance();

    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;
        this.primaryStage.setResizable(false);
        this.primaryStage.setTitle("Manage Facebook Posts from Anonymous Followers");

        ConfessionDAO responses = new ConfessionDAO();

        //fetch confessions that are not processed
        confessions = FXCollections.observableList(responses.getUnprocessedConfessions(0, 20));

        initRootLayout();
        showConfessionsOverview();
        showHashTagFooter();

        //if form properties are not cofigured, show Config dialog
        if (properties.getFormType() == null || properties.getFormName().isEmpty()) {
            showPickFormTypeDialog();
        }
    }

    public void initRootLayout() {
        try {
            // Load response overview
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/fxml/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            

            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showConfessionsOverview() {
        try {
            // Load response overview
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/fxml/ResponsesOverview.fxml"));
            Pane confessionsOverview = (Pane) loader.load();

            // Set response overview into the center of root layout
            rootLayout.setCenter(confessionsOverview);

            this.responsesController = loader.getController();
            this.responsesController.setMainApp(this);

        } catch (IOException /*|ServiceException*/ e) {
            e.printStackTrace();
        }
    }

    public void showHashTagFooter() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/fxml/hashtagList.fxml"));
            AnchorPane hashTagFooter = (AnchorPane) loader.load();

            HashtagListController controller = loader.getController();
            controller.setMainApp(this);
            controller.displayCurrentHashTag();
            
            rootLayout.setBottom(hashTagFooter);
        } catch (IOException /*|ServiceException*/ e) {
            e.printStackTrace();
        }
    }

    public void showPickFormTypeDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/fxml/InitConfigFormProperties.fxml"));
            DialogPane dialog = (DialogPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Configure Input");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(dialog);
            dialogStage.setScene(scene);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

        } catch (IOException e) {
            // Exception gets thrown if the fxml file could not be loaded
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public ObservableList<Confession> getConfessions() {
        return confessions;
    }
    
    public void setConfessions(ArrayList<Confession> responses) {
        this.confessions = FXCollections.observableList(responses);
    }
    
    public void addConfessions(ArrayList<Confession> responses) {
        ArrayList<Confession> temp = new ArrayList<>();
        temp.addAll(this.confessions);
        temp.addAll(responses);
        this.confessions = FXCollections.observableList(temp);
        this.responsesController.setTableConfessionData();
    }
    
    public FacebookConnector getFacebookConnector() {
        return facebookConnector;
    }
    
    public void setFacebookConnector(FacebookConnector facebookConnector) {
        this.facebookConnector = facebookConnector;
    }
    
    public GoogleSpreadSheetsConnector getGoogleSpreadSheetsConnector() {
        return googleConnector;
    }
    
    public void setGoogleSpreadSheetsConnector(GoogleSpreadSheetsConnector googleConnector) {
        this.googleConnector = googleConnector;
    }
    
    public HashTag getCurrentHashTag() {
        return currentHashTag;
    }
    
    public void setCurrentHashTag(HashTag hashTag) {
        this.currentHashTag = hashTag;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
