/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kjscompile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author agnynk
 */
public class FileScanner {
    private static List<String> result;
    
    public static List<String> run(String rootPath, String search) {
        result = new ArrayList<String>();
        
        String ptext = "^" +
                (search.replace (".", "\\.")
                        .replace("*", ".*")
                ) +
                "$";
        
        File[] domains = new File(rootPath).listFiles();
        
        for (File domain : domains) {
            
            if (domain.isDirectory()) {
                File[] files = new File(domain.getAbsolutePath()).listFiles();

                for (File file : files) {
                    ProcessFile(file, ptext);
                }
            } else {
                ProcessFile(domain, ptext);
            }
        }
        
        return result;
    }
    
    private static void ProcessFile(File file, String ptext) {
        String absoultePath = file.getAbsolutePath();
        if (file.isFile() &&
                Pattern.matches(ptext, absoultePath)
            ) {
            result.add(absoultePath);
        }
    }
    
    
    
}
