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
package confessionsManager.connectors.googleSpreadSheets;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;
import confessionsManager.FormType;
import confessionsManager.config.ConfigProperties;
import confessionsManager.model.Confession;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GoogleSpreadSheetsConnector {

    private final GoogleCredential credential;
    private final SpreadsheetService service;
    private boolean authenticated;

    private ListFeed feed = null;
    private WorksheetEntry workSheet;

    public GoogleSpreadSheetsConnector(String keyFilePath) throws FileNotFoundException, IOException, GeneralSecurityException {
        JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        this.credential = GoogleCredential.fromStream(new FileInputStream(keyFilePath), httpTransport, JSON_FACTORY)
                .createScoped(Collections.singleton("https://spreadsheets.google.com/feeds"));

        service = new SpreadsheetService("RobotikosAnomologitos-v1");
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean auth) {
        this.authenticated = auth;
    }

    public boolean authenticate() {
        service.setOAuth2Credentials(credential);
        this.setAuthenticated(true);
        return true;
    }

    private ListFeed loadWorkSheetFeed() throws MalformedURLException, IOException, ServiceException {
        // Load sheet
        //Get a list of our spreadsheets and find the one specified in config.properties file
        //if not authenticated exit
        if (!isAuthenticated()) {
            System.err.println("Probably not connected to google spread sheets");
        } else {
            URL metafeedUrl = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");
            final SpreadsheetFeed spreadsheetFeed = service.getFeed(metafeedUrl, SpreadsheetFeed.class);
            final List<SpreadsheetEntry> spreadsheets = spreadsheetFeed.getEntries();
            for (final SpreadsheetEntry sheet : spreadsheets) {
                if (ConfigProperties.getInstance().getFormName().equals(sheet.getTitle().getPlainText())) {
                    workSheet = ((WorksheetEntry) sheet.getWorksheets().get(0));
                    URL listFeedURL = workSheet.getListFeedUrl();
                    feed = (ListFeed) service.getFeed(listFeedURL, ListFeed.class);
                    return feed;

                }
            }

        }
        return null;
    }

    public ListEntry getRow(int number) {
        return feed.getEntries().get(number);
    }

    public void deleteWorkSheet() {
        /*try {
            workSheet.delete();           
        } catch (final IOException | ServiceException e) {
            throw new RuntimeException(e);
        }*/
    }
    
    public ArrayList<Confession> FetchResponses() throws IOException, MalformedURLException, ServiceException {

        ListFeed listfeed = this.loadWorkSheetFeed();
        if (listfeed == null) {
            return null;
        }
        ArrayList<Confession> responseList = new ArrayList<>();

        List<String> columns = new ArrayList<>();
        // we keep the row number in which the entry is
        int rowNum = 0;
        //Loaded 'em up!
        for (ListEntry row : listfeed.getEntries()) {
            for (String tag : row.getCustomElements().getTags()) {
                columns.add(row.getCustomElements().getValue(tag));
            }
            Confession r = new Confession(columns, FormType.TYPE_GOOGLE_FORMS);
            responseList.add(r);
            columns.clear();
            rowNum++;
        }
        return responseList;
    }

    public void deleteResponsesFromSpreadSheets(ArrayList<Confession> responseList) throws IOException, MalformedURLException, ServiceException {
       this.deleteWorkSheet();
    }

}
