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

import confessionsManager.FormType;
import java.util.List;


public class Confession {

    private long id;
    private String timestamp;
    private String message;
    private String faculty;
    private boolean published;
    private boolean processed;

    /* Empty constractor method used by Hibernate */
    public Confession() {

    }
    
    /*This constructor is only used for testing */
    public Confession(String message, String faculty, String date) {
        this.message = message;
        this.faculty = faculty;
        this.timestamp = date;
    }

    public Confession(List<String> fields, FormType formtype) {
        switch (formtype) {
            case TYPE_GOOGLE_FORMS:
                this.timestamp      = fields.get(0);
                this.message        = fields.get(1);
                this.faculty        = fields.get(2);
                break;
            case TYPE_123CONTACT_FORMS:
                //TODO finish this
                break;
        }
        this.published = false;
        this.processed = false;
    }
    

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getFaculty() {
        return faculty;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean b) {
        this.published = b;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean b) {
        this.processed = b;
    }

    public boolean isPosted() {
        return (processed && published);
    }

    public void setPosted() {
        this.setPublished(true);
        this.setProcessed(true);
    }
    
    public void setRejected() {
        setProcessed(true);
        setPublished(false);
    }
}
