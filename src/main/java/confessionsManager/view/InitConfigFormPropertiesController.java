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
import confessionsManager.config.ConfigProperties;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 */
public class InitConfigFormPropertiesController implements Initializable {

    private final ConfigProperties properties = ConfigProperties.getInstance();

    @FXML
    private ChoiceBox formTypeList;

    @FXML
    private TextField formName;

    @FXML
    private Button chooseButton;

    @FXML
    private CheckBox remeberCheckBox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formTypeList.setItems(FXCollections.observableArrayList(
                    FormType.values()
            ));

        chooseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (remeberCheckBox.isSelected()) {
                    properties.setPersistantFormName(formName.getText());
                    properties.setPersistantFormType(FormType.get(String.valueOf(formTypeList.getSelectionModel().getSelectedItem())));
                } else {
                    properties.setTempFormName(formName.getText());
                    properties.setTempFormType(FormType.get(String.valueOf(formTypeList.getSelectionModel().getSelectedItem())));
                }
                //close stage
                Stage stage = (Stage) chooseButton.getScene().getWindow();
                stage.close();
            }
        });
    }

}
