/*
 * kjscompiler
 * Copyright (C) 2014  Oleksandr Knyga, oleksandrknyga@gmail.com
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

package kjscompile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author agnynk
 */
public class OrderDeps {
    private List<FileInfo> orderedFiles;
    private List<FileInfo> files;
    
    private HashSet<Integer> unId;
    
    //To reduce consumed space
    private Hashtable<String, Integer> filesHash;
    
    private List<String> errors;
    
    public OrderDeps(List<FileInfo> files) {
        this.files = files;
        this.errors = new ArrayList<String>();
        this.filesHash = new Hashtable<String, Integer>();
        this.orderedFiles = new ArrayList<FileInfo>();
        this.unId = new HashSet<Integer>();
        
        //Sort files, with low number of dependencies goes first
        Collections.sort(this.files, new Comparator<FileInfo>(){
            public int compare(FileInfo f, FileInfo s) {
                int fSize = f.getDependencies().size(),
                        sSize = s.getDependencies().size();

                if(fSize>sSize) {
                    return 1;
                }

                if(fSize<sSize) {
                    return -1;
                }

                return 0;
            }
        });
        
        //Initialize hash
        for(int i = 0, length = this.files.size(); i < length; i++) {
            this.filesHash.put(this.files.get(i).getFilename().toLowerCase(), i);
        }
        
        //Create back links, to make children/parent adecvate
        for(int i = 0, ilength = this.files.size(); i < ilength; i++) {
            FileInfo fiCurrent = this.files.get(i);
            List<String> dependencies = fiCurrent.getDependencies();
            
            for(int j = 0, jlength = dependencies.size(); j < jlength; j++) {
                String depName = dependencies.get(j).toLowerCase();
                
                if(this.filesHash.containsKey(depName)) {
                    int fiDepId = this.filesHash.get(depName);
                    FileInfo dep = this.files.get(fiDepId);

                    if(!dep.children.contains(fiCurrent.getFilename())) {
                        //Add children
                        dep.children.put(fiCurrent.getFilename(), i);
                    }
                } else {
                    this.errors.add("Undefined dependency: " +
                            fiCurrent.getFilename() + " => " +
                            depName);
                }
            }
        }
                
        for(int i = 0, ilength = this.files.size(); i < ilength; i++) {
            this.order(i);
        }
        
        if(this.orderedFiles.size() < this.files.size()) {
            for(int i = 0, ilength = this.files.size(); i < ilength; i++) {
                
                if(!unId.contains(i)) {
                    this.add(i);
                }
            }            
        }

    }
    
    private void order(int id) {
        if(id >= this.files.size()) {
            return;
        }
        
        FileInfo fiCurrent = this.files.get(id);
        
        //If viewed, go to the next file
        if(fiCurrent.getIsWatched()) {
            order(id+1);
            return;
        }
        
        //If all parents viewed
        if(fiCurrent.getWatchedDeps() == fiCurrent.getDependencies().size()) {
            //Add to the ordered set
            add(id);
        } else {
            //Otherwise, take a look on the next file
            order(id+1);
        }
        
    }
    
    private void add(int id) {
        if(id >= this.files.size()) {
            return;
        }
        
        FileInfo fiCurrent = this.files.get(id);
        
        //If it is not in a ordered list уеt
        if(this.unId.add(id)) {
            this.orderedFiles.add(fiCurrent);
            //System.out.println(fiCurrent.getFilename());
            
            fiCurrent.setIsWatched(true);
            
            Iterator it = fiCurrent.children.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                int value = (int)pairs.getValue();
                FileInfo dep = this.files.get(value);
                dep.setWatchedDeps(dep.getWatchedDeps()+1);

                order(value);

                //avoids a ConcurrentModificationException
                it.remove(); 
            }
        }
    }

    /**
     * @return the orderedFiles
     */
    public List<FileInfo> getOrderedFiles() {
        return orderedFiles;
    }

    /**
     * @return the errors
     */
    public List<String> getErrors() {
        return errors;
    }

    /**
     * @param errors the errors to set
     */
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    
    
}
