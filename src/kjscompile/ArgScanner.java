/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kjscompile;

import java.util.Hashtable;

/**
 *
 * @author agnynk
 */
public class ArgScanner {
    private String[] args;
    private Hashtable<String, String> table;
    
    private String prefix = "--";
    
    public ArgScanner(String[] args) {
        this.table = new Hashtable<String, String>();
        
        String name = "",
                value = "";
        for(int i = 0, length = args.length; i < length; i++) {
            
            if(0 == i%2 || 0 == i) {
                name = args[i];
            } else {
                value = args[i];
                table.put(name, value);
            }
            
        }
    }
    
    public Boolean checkValue(String name) {
        return this.table.containsKey(prefix+name);
    }
    
    public String getValue(String name) {
        return this.table.get(prefix+name);
    }
}
