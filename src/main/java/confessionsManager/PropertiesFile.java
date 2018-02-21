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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class PropertiesFile {
    
    private static final String propertyFilename = "properties/config.properties";
    
    public static String readProperty(String propertyName) {
        Properties prop = new Properties();
        InputStream input = null;
        String property = null;
        try {
            input = MainApp.class.getClassLoader().getResourceAsStream(propertyFilename);
            if (input == null) {
                System.out.println("Sorry, unable to find " + propertyFilename);
                return property;
            }
            //load a properties file from class path, inside static method
            prop.load(input);

            //get the property value
            property =  prop.getProperty(propertyName);
            
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return property;
    }
    
    public static void writeProperty(String propertyName, String value) {
        Properties prop = new Properties();
        OutputStream output = null;
        try {
            File fl = new File(MainApp.class.getClassLoader().getResource(propertyFilename).getPath());
            output = new FileOutputStream(fl, true);
            prop.setProperty(propertyName, value);
            prop.store(output, null);
            output.close();           
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
