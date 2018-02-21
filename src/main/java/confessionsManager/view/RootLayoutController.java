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

import com.google.gdata.util.ServiceException;
import confessionsManager.CSVHelper;
import confessionsManager.MainApp;
import confessionsManager.config.ConfigProperties;
import confessionsManager.connectors.googleSpreadSheets.GoogleSpreadSheetsConnector;
import confessionsManager.dao.ConfessionDAO;
import confessionsManager.model.Confession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RootLayoutController {

    private MainApp mainApp;
    private final ConfessionDAO responses = new ConfessionDAO();
    private final ConfigProperties properties = ConfigProperties.getInstance();
    
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void handleExportPostedConfessions(ActionEvent event) {
        File savefile = getSaveFile();
        if (savefile != null) {
            //write to the file
            CSVHelper.writeToCsvFile(savefile, responses.getPublishedConfessions(), false, false);
        }
    }
    
    public void handleExportRejectedConfessions(ActionEvent event) {
        File savefile = getSaveFile();
        if (savefile != null) {
            //write to the file
            CSVHelper.writeToCsvFile(savefile, responses.geRejectedConfessions(), false, false);
        }
    }
    
    public void handleExportAllConfessions(ActionEvent event) {
        File savefile = getSaveFile();
        if (savefile != null) {
            //write to the file
            CSVHelper.writeToCsvFile(savefile, responses.getAllConfessions(), true, false);
        }
    }
    
    public void handleExportUnprocessedConfessions(ActionEvent event) {
        File savefile = getSaveFile();
        if (savefile != null) {
            //write to the file
            Integer start = null, limit = null; // do not limit query
            CSVHelper.writeToCsvFile(savefile, responses.getUnprocessedConfessions(start,limit), false, true);
        }
    }
    
    
    
    private File getSaveFile() {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);

        //show dialog and return selected file
        return fileChooser.showSaveDialog(mainApp.getPrimaryStage());
    }
    
    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));
        
        File selectedFile = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
        if (selectedFile != null) {
            parseCSVfile(selectedFile);
        }
    }
    
    private List<Confession> parseCSVfile(File selectedFile) {
        List<Confession> responsesList = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(selectedFile));
            String line;
            int rowNum = 0;
            while ((line = br.readLine()) != null) {
                Confession response = new Confession(CSVHelper.parseLine(line), properties.getFormType());
                rowNum++;
                responsesList.add(response);
            }
        } catch (IOException e ) {
             e.printStackTrace();
        }
        
        
        return responsesList;
    } 
    
    public void handleConfigProperties(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/fxml/ConfigProperties.fxml"));
            DialogPane dialog = (DialogPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Properties");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(mainApp.getPrimaryStage());
            Scene scene = new Scene(dialog);
            dialogStage.setScene(scene);
            
            ConfigPropertiesController controller = loader.getController();
            controller.setMainApp(mainApp);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
            
        } catch (IOException e) {
            // Exception gets thrown if the fxml file could not be loaded
            e.printStackTrace();
        }
    }
    
    public void handleFacebookLogin(ActionEvent event) {      
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/fxml/Login.fxml"));
            StackPane loginDialog = (StackPane) loader.load();
            Stage LoginStage = new Stage();
            LoginStage.setTitle("Facebook Login");
            
            
            LoginStage.initModality(Modality.WINDOW_MODAL);
            LoginStage.initOwner(mainApp.getPrimaryStage());
            Scene scene = new Scene(loginDialog);
            LoginStage.setScene(scene);
            
            FacebookLoginController controller = loader.getController();
            controller.setMainApp(mainApp);

            // Show the dialog and wait until the user closes it
            LoginStage.showAndWait();
            
        } catch (IOException e) {
            // Exception gets thrown if the fxml file could not be loaded
            e.printStackTrace();
        }
    }
    
    public void handleAuthGoogleSpreadSheets(ActionEvent event) {
        String filePath = ConfigProperties.getInstance().getGoogleServiceKeyFilePath();
        GoogleSpreadSheetsConnector googleConnector;
        if (filePath != null) {
            try {
                googleConnector = new GoogleSpreadSheetsConnector(filePath);
                googleConnector.authenticate();
                mainApp.setGoogleSpreadSheetsConnector(googleConnector);
                Modals.showInformationModal(mainApp.getPrimaryStage(), "Google SpreadSheets Login", 
                        "Successfull Login!", "You can load Responses from menu Tools->Load Responses from GoogleSpreadSheets");
            } catch (IOException  ex ) {
                Logger.getLogger(RootLayoutController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (GeneralSecurityException e) {
                Modals.showErrorModal(mainApp.getPrimaryStage(), "Google SpreadSheets Login Error", "Google SpreadSheets Key file not found", 
                        "Please check your config");
            }
        }
    }
    
    public void handleLoadConfessions(ActionEvent event) {
        GoogleSpreadSheetsConnector googleConnector = mainApp.getGoogleSpreadSheetsConnector();
        if ( (googleConnector == null)? true : !googleConnector.isAuthenticated() ) {
             Modals.showErrorModal(mainApp.getPrimaryStage(), "Google SpreadSheets Error", "Could not load Responses ",
                        "You are not connected to Google SpreadSheets. Please connect from menu Connectors->Connect to GoogleSpreadSheets !");
        } else {            
            try {
                ArrayList<Confession> responsesList = googleConnector.FetchResponses();
                //delete from spreadsheet 
                if (responsesList.isEmpty()) {
                    Modals.showErrorModal(mainApp.getPrimaryStage(), "Google SpreadSheets Error", "There are no new Responses",
                        "Google SpreadSheet file that contains the Responses is empty. Try again later. Thank you!");
                } else {
                    //save confessions to database
                    responsesList.forEach((response) -> {
                        responses.saveConfession(response);
                    });                    
                    mainApp.addConfessions(responsesList);
                    googleConnector.deleteResponsesFromSpreadSheets(responsesList);
                }
            } catch (ServiceException | IOException ex) {
                Logger.getLogger(RootLayoutController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }       
    }
}
