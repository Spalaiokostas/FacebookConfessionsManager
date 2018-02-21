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
import confessionsManager.connectors.facebook.FacebookConnector;
import confessionsManager.dao.ConfessionDAO;
import confessionsManager.model.Confession;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Popup;

/**
 * FXML Controller class
 */
public class ConfessionsController {

    private final ConfessionDAO responseDAO = new ConfessionDAO();
    private final ConfigProperties properties = ConfigProperties.getInstance();

    @FXML
    private TableView<Confession> confessionsTable;

    @FXML
    private TableColumn<Confession, Long> confessionID;

    @FXML
    private TableColumn<Confession, String> confession;

    @FXML
    private TextArea messageText;

    @FXML
    private TextField dateLabel;
    
    @FXML
    private Button nextConfession;

    @FXML
    private Button previousConfession;

    private MainApp mainApp;

    public ConfessionsController() {
    }

    /**
     * initialize the controller class.
     */
    @FXML
    public void initialize() {

        confessionID.setCellValueFactory((TableColumn.CellDataFeatures<Confession, Long> p)
                -> new SimpleLongProperty(p.getValue().getId()).asObject());
        confession.setCellValueFactory((TableColumn.CellDataFeatures<Confession, String> p)
                -> new ReadOnlyStringWrapper(p.getValue().getMessage()));

        //clear message details
        showMessageDetails(null);

        confessionsTable.getSelectionModel().selectedItemProperty().addListener((new ChangeListener<Confession>() {
            @Override
            public void changed(ObservableValue<? extends Confession> observable, Confession oldValue, Confession newValue) {
                showMessageDetails(newValue);
            }
        })
        );
        
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        // Add observable list data to the table
        setTableConfessionData();
    }

    public void showMessageDetails(Confession response) {
        if (response != null) {
            messageText.setText(response.getMessage());
            dateLabel.setText(response.getDate());
        } else {
            messageText.setText("");
            dateLabel.setText("");
        }
    }
    
    public void setTableConfessionData() {
        confessionsTable.setItems(mainApp.getConfessions());
        confessionsTable.getSelectionModel().selectFirst();
    }

    @FXML
    protected void handlePostButton(ActionEvent e) {
        int selectedIndex = confessionsTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            //do post anomologitko
            Confession response = confessionsTable.getSelectionModel().getSelectedItem();
            FacebookConnector connector = mainApp.getFacebookConnector();
            String anomologitoNum = properties.getConfessionNumber();
            String message = "#" + mainApp.getCurrentHashTag().getHashTagName() + anomologitoNum + ": " + response.getMessage();
            //post message if connected
            if ((connector != null) ? !connector.post(message) : true) {
                Modals.showErrorModal(mainApp.getPrimaryStage(), "Posting Message Error", "Counld not post message to Facebook ",
                        "You are not connected to Facebook. Please connect from menu Connectors->Connect to Facebook !");
            } else {
                response.setPosted();
                responseDAO.updateToPosted(response.getId());
                //update anomologitoNumer 
                properties.updateConfessionNumber();
                //remove anomologito from data list
                this.mainApp.getConfessions().remove(response);
                
                //show new list
                setTableConfessionData();
            }
        } else {
            // Nothing selected.
            Modals.showErrorModal(mainApp.getPrimaryStage(), "No Selection", "No Response loaded",
                    "Please load Responses from menu Tools->Load Responses.");
        }

    }

    @FXML
    protected void handleRejectButton(ActionEvent e) {
        int selectedIndex = confessionsTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            //do reject anomologito
            Confession response = confessionsTable.getSelectionModel().getSelectedItem();
            responseDAO.updateToRejected(response.getId());
            response.setRejected();

            //remove anomologito from data list
            this.mainApp.getConfessions().remove(response);
            
            //show new list
            setTableConfessionData();
        } else {
            // Nothing selected.
            Modals.showErrorModal(mainApp.getPrimaryStage(), "No Selection", "No Response loaded",
                    "Please load Responses from menu Tools->Load Responses.");
        }
    }

    @FXML
    protected void handleNextButton(ActionEvent e) {
        int selectedIndex = confessionsTable.getSelectionModel().getSelectedIndex();
        int nextIndex = selectedIndex + 1;
        if (nextIndex == (this.mainApp.getConfessions().size() - 1)) {
            nextConfession.setDisable(true);
            /*GoogleSpreadSheetsConnector googleConnector = mainApp.getGoogleSpreadSheetsConnector();
            if ((googleConnector == null) ? false : googleConnector.isAuthenticated()) {
                try {
                    GoogleResponsesDAO googleDAO = new GoogleResponsesDAO(googleConnector);
                    ArrayList<Response> responsesList = googleDAO.FetchResponses();
                    //delete from spreadsheet 
                    googleDAO.deleteResponsesFromSpreadSheets(responsesList);
                    mainApp.setResponseData(responsesList);
                } catch (ServiceException | IOException ex) {
                    Logger.getLogger(RootLayoutController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }*/
        }
        confessionsTable.getSelectionModel().select(nextIndex);
        
        //re enable previous button if it was disabled
        if (previousConfession.isDisabled()) {
            previousConfession.setDisable(false);
        }
    }

    @FXML
    protected void handlePreviousButton(ActionEvent e) {
        int selectedIndex = confessionsTable.getSelectionModel().getSelectedIndex();
        int previousIndex = selectedIndex - 1;
        if (selectedIndex == 0) {
            previousConfession.setDisable(true);
        }
        confessionsTable.getSelectionModel().select(previousIndex);
        if (nextConfession.isDisabled()) {
            nextConfession.setDisable(false);
        }
    }

    @FXML
    protected void handleShowPreview(ActionEvent e) {

        Confession response = confessionsTable.getSelectionModel().getSelectedItem();
        String new_line = System.getProperty("line.separator");
        if (mainApp.getCurrentHashTag() != null && response != null) {
            String hashTag = "#" + mainApp.getCurrentHashTag().getHashTagName()
                    + ConfigProperties.getInstance().getConfessionNumber();
            String message = ": " + response.getMessage()
                    + new_line
                    + new_line
                    + response.getFaculty();

            TextFlow flow = new TextFlow();
            Text text1 = new Text(hashTag);
            text1.setStyle("-fx-font-weight: bold");
            Text text2 = new Text(message);
            text2.setStyle("-fx-font-weight: regular");
            flow.getChildren().addAll(text1, text2);

            flow.setPrefWidth(300);
            flow.setPrefHeight(200);
            Button close = new Button("Close");

            VBox popUpVBox = new VBox();
            popUpVBox.getChildren().add(flow);
            popUpVBox.getChildren().add(close);
            flow.setStyle("-fx-border-style: solid inside;"
                    + "-fx-border-width: 2;"
                    + "-fx-border-radius: 5;"
                    + "-fx-padding: 3;"
                    + "-fx-border-color: blue;"
                    + "-fx-background-color: white");

            final Popup popup = new Popup();
            popup.setAutoFix(true);
            popup.setHideOnEscape(true);
            popup.getContent().addAll(popUpVBox);

            popup.show(mainApp.getPrimaryStage());

            close.setOnAction((ActionEvent t) -> {
                popup.hide();
            });
        } else {
            Modals.showErrorModal(mainApp.getPrimaryStage(), "No Selection", "No Response loaded",
                    "Please load Responses from menu Tools->Load Responses.");
        }

    }

}
