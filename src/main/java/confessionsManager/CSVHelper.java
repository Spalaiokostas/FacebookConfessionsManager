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

import confessionsManager.model.Confession;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class CSVHelper {
    
    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';
        
    public static void writeLine(Writer w, List<String> values) 
        throws Exception
    {
        boolean firstVal = true;
        for (String val : values)  {
            if (!firstVal) {
                w.write(",");
            }
            w.write('"');
            for (int i=0; i<val.length(); i++) {
                char ch = val.charAt(i);
                if (ch=='"') {
                    w.write('"');  //extra quote
                }
                w.write(ch);
            }
            w.write('"');
            firstVal = false;
        }
        w.write("n");
    }
 
    /**
    * Returns a null when the input stream is empty
     * @param file
    */
    
    public static List<String> readLineFromCSV(File file) throws Exception {

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                List<String> line = parseLine(scanner.nextLine());
                return line;
            }
        }
        return null;

    }

    public static List<String> parseLine(String cvsLine) {
        return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String cvsLine, char separators) {
        return parseLine(cvsLine, separators, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String cvsLine, char separators, char customQuote) {

        List<String> result = new ArrayList<>();

        //if empty, return!
        if (cvsLine == null && cvsLine.isEmpty()) {
            return result;
        }

        if (customQuote == ' ') {
            customQuote = DEFAULT_QUOTE;
        }

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuffer curVal = new StringBuffer();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray();

        for (char ch : chars) {

            if (inQuotes) {
                startCollectChar = true;
                if (ch == customQuote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else {

                    //Fixed : allow "" in custom quote enclosed
                    if (ch == '\"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(ch);
                    }

                }
            } else {
                if (ch == customQuote) {

                    inQuotes = true;

                    //Fixed : allow "" in empty quote enclosed
                    if (chars[0] != '"' && customQuote == '\"') {
                        curVal.append('"');
                    }

                    //double quotes in column will hit this!
                    if (startCollectChar) {
                        curVal.append('"');
                    }

                } else if (ch == separators) {

                    result.add(curVal.toString());

                    curVal = new StringBuffer();
                    startCollectChar = false;

                } else if (ch == '\r') {
                    //ignore LF characters
                    continue;
                } else if (ch == '\n') {
                    //the end, break!
                    break;
                } else {
                    curVal.append(ch);
                }
            }

        }

        result.add(curVal.toString());

        return result;
    }
    
    public static void writeToCsvFile(File savefile,List<Confession> postedResponses, boolean includePostedField, boolean includeProcessedFiled) {
        String COMMA_DELIMITER = ",";
        String NEW_LINE_SEPARATOR = System.getProperty("line.separator");
        String FILE_HEADER = "id,timestamp,responseBody,faculty";
        if (includePostedField) {
            FILE_HEADER += ",published";
        } 
        if (includeProcessedFiled) {
            FILE_HEADER += ",processed";
        }

        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(savefile);
            fileWriter.append(FILE_HEADER);
            fileWriter.append(NEW_LINE_SEPARATOR);
            for (Confession response : postedResponses) {
                
                String line = String.valueOf(response.getId())+COMMA_DELIMITER+
                        "test time"+COMMA_DELIMITER+response.getMessage()+
                        COMMA_DELIMITER+response.getFaculty();
                if (includePostedField) {
                    line += COMMA_DELIMITER+(response.isPublished() ? "true" : "false");
                } 
                if (includeProcessedFiled) {
                    line += COMMA_DELIMITER+(response.isPublished() ? "true" : "false");
                }
                fileWriter.append(line+NEW_LINE_SEPARATOR);
            }
        } catch (IOException e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {
            try {
                if (fileWriter != null)
                    fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }
        }
    }

}
