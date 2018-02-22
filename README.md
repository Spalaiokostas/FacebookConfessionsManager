# FacebookConfessionsManager
A tool to simplify processing and posting of anonymous user confessions from google forms in a facebook group. These groups exist for entertainment purposes, usually communities within universities etc.

DESCRIPTION:

There are some communities that have a facebook page in order to allow their followers to anonymously post their confessions
or secrets that they want to share. An example of that page is "Ανομολογητα Πανεπηστιμιου Θεσσαλονικης". Typically these pages
provide a link to a google form so that users can post their confessions there, as a result these confessions are return to the 
owner of the google forms in a spreadsheet located in google drive of the owner. From there the moderator of the facebook page
manually goes through the responses and posts the confessions that he deems appropriate and entertaining. The process descriped 
above can be rather time consuming because the moderator has to locate in the shreadsheet the confession that he chooses to 
post and then manually process and copy it as a facebook status for the page.
This project created here automates that process by downloading the confession in a database locally, presenting them to the 
moderator and giving him the option to post or to reject the confession, posting is also handled by the program. Additionaly, there
are some extra features like selecting a hashtag to go with the confessions, storing hashtags for future use, previewing the 
facebook status to be posted and some options for viewing confessions by categories (posted, rejected, unprocessed)

Technical Overview:

To obtain persistance of the object such as Confessions and Hashtag we used Hibernate framework with a local hsql database.
The Data Access Object (DAO) patter was employed in conjuction with Hiberante to seperate the different logical compartments of the program.
Also the whole tool is designed in MVC design approach with ease of addition possible future features.
Javafx was used for the GUI.

----------------------------------------

CONFIGURATION:

The configuration file is located at FacebookConfessionsManager/src/main/resources/properties/config.properties
 An example of the properties in the config file is

confessionNumber = 1</br>
confessionNumberLimit = 100</br>
hashtagRefresh = 100</br>
currentHashTagID = 1</br>
formType = GoogleForms</br>
formName = </br>
facebookAppID = 721442258005468</br>
facebookAppSecret = 1b2da0c158b451f040ef958f9c359817</br>
facebookPageID = 360367807645076</br>
facebookPageAccessToken =</br>
GoogleServiceKeyFilePath = /home/delmanel/GoogleServiceKey.txt</br>

From the above properties you need to fill the facebookAppID and the facebookAppSecret, you can find more info about those here: https://developers.facebook.com/docs/apps/register
also, the facbookPageID which is the ID of the page that you are posting, and form name is the name of the form that users submit their confessions in,
and finally, the GoogleServiceKeyFilePath is the path of the google service key that you have stored locally in your pc (more info about how to obtain the key here: https://developers.google.com/maps/documentation/javascript/get-api-key

------------------------------------------

USAGE:

When you execute the program your first action should be to connect to facebook and google SpreadSheets, to achive that go to
Connectors → Connect to Facebook menu and Connetors → Connect to Google Sheets menu. After the connection is established the 
processing of the confessions is trivial.
