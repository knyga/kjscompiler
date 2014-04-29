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

package kjscompiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * 
 * @author agnynk
 */
public class Settings {
	private String output, level, pattern, wrapper, projectPath;
	private JSONArray baseDir;

	public Settings(String path) throws ParseException, FileNotFoundException,
			IOException {
		JSONParser parser = new JSONParser();
		File file = new File(path);
                String parent = file.exists()?file.getParent():null;
		File dir = new File(null == parent || parent.isEmpty()?".":parent);
		FileReader fr = new FileReader(file);
		char[] fileData = new char[(int) file.length()];
		fr.read(fileData);
		String content = new String(fileData);
		JSONObject obj = (JSONObject) parser.parse(content);
		if (obj.get("basedir") instanceof JSONArray) {
			this.baseDir = (JSONArray) obj.get("basedir");
		} else {
			JSONArray arr = new JSONArray();
			arr.add(obj.get("basedir"));
			this.baseDir = arr;
		}
		this.output = (String) obj.get("output");
		this.level = (String) obj.get("level");
		this.pattern = (String) obj.get("pattern");
		this.wrapper = obj.get("wrapper") != null?(String) obj.get("wrapper"): "";
		this.projectPath = dir.getCanonicalPath();
		
		dir = new File(this.projectPath, this.output);
		this.output = dir.getCanonicalPath();
		
		fr.close();
	}

	/**
	 * @return the baseDir
	 */
	public JSONArray getBaseDir() {
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
	 * @param pattern
	 *            the pattern to set
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	/**
	 * @return the output
	 */
	public String getWrapper() {
		return wrapper;
	}
	
	/**
	 * @return the directory of the configuration file
	 */
	public String getProjectPath(){
		return projectPath;
	}
}
