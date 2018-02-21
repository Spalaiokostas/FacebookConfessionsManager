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

import confessionsManager.FormType;
import confessionsManager.MainApp;
import confessionsManager.config.ConfigProperties;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 */
public class ConfigPropertiesController implements Initializable {
    
    private MainApp mainApp;
    private final ConfigProperties properties = ConfigProperties.getInstance();
    
    @FXML
    private AnchorPane anomologitaAnchorPane;
    
    @FXML 
    private AnchorPane formAnchorPane;
    
    @FXML 
    private ChoiceBox formType;
    
    @FXML
    private TextField formName;
    
    @FXML 
    private TextField responseLimit;
    
    @FXML
    private TextField hashtagLimit;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formType.setItems(FXCollections.observableArrayList(
            FormType.values()
        ));
        
        formType.getSelectionModel().select(properties.getFormType());
        
        formName.setText(properties.getFormName());
        
        responseLimit.setText(Integer.toString(properties.getLimit()));
        
        hashtagLimit.setText(Integer.toString(properties.getHashTagLimit()));
    }  
    
    public void submitButton() {
        properties.setPersistantFormName(formName.getText());
        properties.setPersistantFormType(FormType.get(String.valueOf(formType.getSelectionModel().getSelectedItem())));
        properties.setLimit(Integer.parseInt(responseLimit.getText()));
        properties.setHashTagLimit(Integer.parseInt(hashtagLimit.getText()));
    }
    
    public void cancelButton() {
        Stage stage = (Stage) formType.getScene().getWindow();
        stage.close();
    }
    
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
    
}
