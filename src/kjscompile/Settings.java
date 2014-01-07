/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kjscompile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author agnynk
 */
public class Settings {
    private String baseDir,
            output,
            level,
            pattern;
    
    public Settings(String path) throws ParseException, FileNotFoundException, IOException {
        JSONParser parser=new JSONParser();
        File file = new File(path);
        FileReader fr = new FileReader(file);
        char[] fileData = new char[(int)file.length()];
        fr.read(fileData);
        String content = new String(fileData);
        JSONObject obj = (JSONObject)parser.parse(content);

        this.baseDir = (String)obj.get("basedir");
        this.output = (String)obj.get("output");
        this.level = (String)obj.get("level");
        this.pattern = (String)obj.get("pattern");
    }

    /**
     * @return the baseDir
     */
    public String getBaseDir() {
        return baseDir;
    }

    /**
     * @return the output
     */
    public String getOutput() {
        return output;
    }

    /**
     * @return the level
     */
    public String getLevel() {
        return level;
    }

    /**
     * @return the pattern
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * @param pattern the pattern to set
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
    
}
