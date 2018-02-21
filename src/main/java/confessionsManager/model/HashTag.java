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

package confessionsManager.model;


public class HashTag {
    
    private int id;
    private String hashTagName;
    private int usageCounter;
    
    /* Empty constractor method used by Hibernate */
    public HashTag() {
        
    }
    
    public HashTag(String name, int usage) {
        this.hashTagName = name;
        this.usageCounter = usage;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getHashTagName() {
        return hashTagName;
    }
    
    public void setHashTagName(String name) {
        this.hashTagName = name;
    }
    
    public int getUsageCounter() {
        return usageCounter;
    }
    
    public void setUsageCounter(int counter) {
        this.usageCounter = counter;
    }
    
    
    public void updateUsageCounter() {
        this.usageCounter++; 
    }
    
    @Override
    public String toString() {
        return "HashTag [id=" + id + ", hashTagName=" + hashTagName
                + ", usageCounter=" + usageCounter + "]";
    }
}
