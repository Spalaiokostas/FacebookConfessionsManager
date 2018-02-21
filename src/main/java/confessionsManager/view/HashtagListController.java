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
import confessionsManager.dao.HashTagDAO;
import confessionsManager.model.HashTag;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 */
public class HashtagListController implements Initializable {

    private MainApp mainApp;
    private HashTagDAO hashTags = new HashTagDAO();

    @FXML
    private TextField hashTagTextField;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        hashTagTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            HashTag newHashTag = new HashTag(newValue, 0);
            hashTags.saveHashTag(newHashTag);
            mainApp.setCurrentHashTag(newHashTag);
        });

    }
    
    public void displayCurrentHashTag() {
        if (mainApp.getCurrentHashTag() == null) {
            hashTagTextField.setText("");
        } else {
            hashTagTextField.setText(mainApp.getCurrentHashTag().getHashTagName());
        }
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

}
