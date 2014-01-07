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

import com.google.javascript.jscomp.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author agnynk
 */
public class GoogleClosureCompiler {
    private String compilationLevel;
    private String output;
    private List<String> externals;
    private List<String> primaries;
    
    private Level loggingLevel = Level.OFF;
    private WarningLevel warningLevel = WarningLevel.QUIET;
    
    private JSError[] warnings;
    private JSError[] errors;
    
    private String compileNotes = "/** \n" +
            "@ignore\n" +
            "**/";
            

    /**
     * @return the compilationLevel
     */
    public String getCompilationLevel() {
        return compilationLevel;
    }

    /**
     * @param compilationLevel the compilationLevel to set
     */
    public void setCompilationLevel(String compilationLevel) {
        this.compilationLevel = compilationLevel;
    }

    /**
     * @return the output
     */
    public String getOutput() {
        return output;
    }

    /**
     * @param output the output to set
     */
    public void setOutput(String output) {
        this.output = output;
    }

    /**
     * @return the externals
     */
    public List<String> getExternals() {
        return externals;
    }

    /**
     * @param externals the externals to set
     */
    public void setExternals(List<String> externals) {
        this.externals = externals;
    }

    /**
     * @return the primaries
     */
    public List<String> getPrimaries() {
        return primaries;
    }

    /**
     * @param primaries the primaries to set
     */
    public void setPrimaries(List<String> primaries) {
        this.primaries = primaries;
    }

    /**
     * @return the loggingLevel
     */
    public Level getLoggingLevel() {
        return loggingLevel;
    }

    /**
     * @param loggingLevel the loggingLevel to set
     */
    public void setLoggingLevel(Level loggingLevel) {
        this.loggingLevel = loggingLevel;
    }
    
    /**
     * @return the warningLevel
     */
    public WarningLevel getWarningLevel() {
        return warningLevel;
    }

    /**
     * @param warningLevel the warningLevel to set
     */
    public void setWarningLevel(WarningLevel warningLevel) {
        this.warningLevel = warningLevel;
    }

    /**
     * @return the warnings
     */
    public JSError[] getWarnings() {
        return warnings;
    }

    /**
     * @return the errors
     */
    public JSError[] getErrors() {
        return errors;
    }
    
    public GoogleClosureCompiler(List<String> primaries, String output, 
            String compilationLevel, List<String> externals) {
        this.primaries = primaries;
        this.output = output;
        this.compilationLevel = compilationLevel;
        this.externals = externals;
    }
    
    public void run() throws IOException {
        com.google.javascript.jscomp.Compiler.
                setLoggingLevel(this.loggingLevel);
        com.google.javascript.jscomp.Compiler compiler = 
                new com.google.javascript.jscomp.Compiler();
        
        CompilerOptions options = new CompilerOptions();
       
        CompilationLevel compilationLevel =
                CompilationLevel .SIMPLE_OPTIMIZATIONS;
        
        switch(this.compilationLevel.toUpperCase().trim()) {
            case "WHITESPACE_ONLY":
                compilationLevel = CompilationLevel.WHITESPACE_ONLY;
                break;
                
            case "ADVANCED_OPTIMIZATIONS":
                compilationLevel = CompilationLevel.ADVANCED_OPTIMIZATIONS;
                break;
                
            default:
            case "SIMPLE_OPTIMIZATIONS":
                compilationLevel = CompilationLevel.SIMPLE_OPTIMIZATIONS;
                break;
        }

        compilationLevel.setOptionsForCompilationLevel(options);
        this.getWarningLevel().setOptionsForWarningLevel(options);
        
        List<JSSourceFile> externalJavascriptFiles = 
                new ArrayList<JSSourceFile>();
        for (String filename : this.externals)
        {
          externalJavascriptFiles.add(JSSourceFile.fromFile(filename));
        }

        List<JSSourceFile> primaryJavascriptFiles = 
                new ArrayList<JSSourceFile>();
        for (String filename : this.primaries)
        {
            primaryJavascriptFiles.add(JSSourceFile.fromFile(filename));
        }

        compiler.compile(externalJavascriptFiles, primaryJavascriptFiles,
                options);
        
        this.warnings = compiler.getWarnings();
        this.errors = compiler.getErrors();
        
        this.write(compiler, this.output);
    }
    
    private void write(com.google.javascript.jscomp.Compiler compiler,
            String path) throws IOException {
        
        File f = new File(path);
        File parent = f.getParentFile();
        
        if(null != parent) {
            parent.mkdirs();
        }
        
        FileWriter outputFile;
        outputFile = new FileWriter(path);
        outputFile.write(compiler.toSource());
        outputFile.write("\n");
        outputFile.write(this.compileNotes);
        outputFile.close();
    }
}
