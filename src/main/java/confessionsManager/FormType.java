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

import java.util.HashMap;
import java.util.Map;


public enum FormType {
    TYPE_GOOGLE_FORMS("GoogleForms"),
    TYPE_123CONTACT_FORMS("123ContactForms");
    
    private String label;
    
    private static final Map<String, FormType> lookup = new HashMap<String, FormType>();
    
    static {
        for (FormType type : FormType.values()) {
            lookup.put(type.getLabel(), type);
        }
    }
    
    public String getLabel() {
        return label;
    }
    
    FormType(String label) {
        this.label = label;
    }
    
    public static FormType get(String label) {
        return lookup.get(label);
    }
    
    @Override 
    public String toString() {
       return label;
    }
}
