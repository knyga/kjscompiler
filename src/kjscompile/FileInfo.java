/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
        
        this.filename = filename;
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
            this.dependencies.add(parentDirName + mDependency.group(1).replaceAll("/", "\\\\"));
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
