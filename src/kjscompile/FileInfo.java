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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author agnynk
 */
public class FileInfo implements Comparable<FileInfo>{
    private List<String> dependencies;
    private String filename;
    private String name;
    private Boolean isIgnore = false;
    private Boolean isExternal = false;
    
    public Hashtable<String, Integer> children = new Hashtable<String, Integer>();
    private int watchedDeps = 0;
    private Boolean isWatched = false;
    
    public FileInfo(String filename) {
        
        try {
			this.filename = new File(filename).getCanonicalPath();
		} catch (IOException e) {
			throw new RuntimeException("Unable to get canonical path for " + filename + ": " + e.getMessage());
		}
        this.dependencies = new ArrayList<String>();
        
        File file = new File(filename);
        String parentDirName = file.getParent() + "\\";
        String data = this.readFile(file);
        
        Pattern pDependency = Pattern.compile("@depends\\s+\\{(.+)\\}"),
                pName = Pattern.compile("@name\\s+\\{(.+)\\}"),
                pIgnore = Pattern.compile("@ignore"),
                pExternal = Pattern.compile("@external");
        
        Matcher mDependency = pDependency.matcher(data),
                mName = pName.matcher(data),
                mIgnore = pIgnore.matcher(data),
                mExternal = pExternal.matcher(data);
        
        while (mDependency.find()) {
        	String dependency = parentDirName + mDependency.group(1).replaceAll("/", "\\\\");
        	try {
        		// Resolve relative paths
        		dependency = new File(dependency).getCanonicalPath();
        	} catch (IOException e) {
        		throw new RuntimeException("Unable to get canonical path for dependency " + dependency + " in " + file.getAbsolutePath() + ": " + e.getMessage());
        	}
    		this.dependencies.add(dependency);        		            
        }
        
        if(mName.find()) {
            this.name = mName.group(1);
        }
        
        if(mIgnore.find()) {
            this.isIgnore = true;
        }
        
        if(mExternal.find()) {
            this.isExternal = true;
        }
    }
    
    private String readFile(File file)
    {
       String content = null;
       try {
           FileReader reader = new FileReader(file);
           char[] chars = new char[(int) file.length()];
           reader.read(chars);
           content = new String(chars);
           reader.close();
       } catch (IOException e) {
           e.printStackTrace();
       }
       return content;
    }

    /**
     * @return the dependencies
     */
    public List<String> getDependencies() {
        return dependencies;
    }

    /**
     * @param dependencies the dependencies to set
     */
    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    /**
     * @return the isExternal
     */
    public Boolean getIsExternal() {
        return isExternal;
    }

    /**
     * @param isExternal the isExternal to set
     */
    public void setIsExternal(Boolean isExternal) {
        this.isExternal = isExternal;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(FileInfo o) {
        return o.filename == this.filename ? 1 : -1;
    }

    /**
     * @return the isWatched
     */
    public Boolean getIsWatched() {
        return isWatched;
    }

    /**
     * @param isWatched the isWatched to set
     */
    public void setIsWatched(Boolean isWatched) {
        this.isWatched = isWatched;
    }

    /**
     * @return the watchedDeps
     */
    public int getWatchedDeps() {
        return watchedDeps;
    }

    /**
     * @param watchedDeps the watchedDeps to set
     */
    public void setWatchedDeps(int watchedDeps) {
        this.watchedDeps = watchedDeps;
    }

    /**
     * @return the isIgnore
     */
    public Boolean getIsIgnore() {
        return isIgnore;
    }

    /**
     * @param isIgnore the isIgnore to set
     */
    public void setIsIgnore(Boolean isIgnore) {
        this.isIgnore = isIgnore;
    }
}
