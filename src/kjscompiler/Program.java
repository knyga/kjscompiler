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

import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.WarningLevel;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import org.json.simple.*;
import org.json.simple.parser.ParseException;

/**
 * 
 * @author agnynk
 */
public class Program {

	private static String settingsPath = "kjscompiler.json";
	private static final String settingsArgName = "settings", debugArgName = "debug";
	private static Boolean isDebug = false;

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {

		ArgScanner as = new ArgScanner(args);

		if (as.checkValue(settingsArgName)) {
			settingsPath = as.getValue(settingsArgName);
		}

		if (as.checkValue(debugArgName)) {
			isDebug = true;
		}

		try {
			run();
		} catch (ParseException ex) {

			System.out.println("Parse Exception: " + ex.getMessage());

			if (isDebug) {
				ex.printStackTrace(new PrintStream(System.out));
			}

			System.exit(-1);
		} catch (IOException ex) {
			System.out.println("IO Exception: " + ex.getMessage());

			if (isDebug) {
				ex.printStackTrace(new PrintStream(System.out));
			}

			System.exit(-2);
		} catch (NullPointerException ex) {
			System.out.println("NullPointer Exception: " + ex.getMessage());

			if (isDebug) {
				ex.printStackTrace(new PrintStream(System.out));
			}

			System.exit(-3);
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());

			if (isDebug) {
				ex.printStackTrace(new PrintStream(System.out));
			}

			System.exit(-100);
		}

		System.exit(1);
	}

	private static void run() throws ParseException, IOException,
			NullPointerException {
		Settings settings = new Settings(settingsPath);

		System.out.println("Searching for " + settings.getPattern() + " in "
				+ settings.getBaseDir());

		JSONArray Base = settings.getBaseDir();
		List<String> files = new ArrayList<String>();
		for (int i = 0; i < Base.size(); i++) {
			files.addAll(FileScanner.run((String) Base.get(i), settings.getPattern(), settings.getProjectPath()));
		}
		System.out.println("Found " + files.size() + " files");

		List<FileInfo> fiPrimaries = new ArrayList<FileInfo>();
		List<String> pExternals = new ArrayList<String>();

		Hashtable<String, Integer> fiHtPrimaries = new Hashtable<String, Integer>();

		for (String fullpath : files) {
			FileInfo fi = new FileInfo(fullpath);

			if (!fi.getIsIgnore()) {

				if (fi.getIsExternal()) {
					pExternals.add(fi.getFilename());
				} else {
					fiPrimaries.add(fi);
				}
			}
		}

		System.out.println("Primaries: " + fiPrimaries.size() + " files");
		System.out.println("Externals: " + pExternals.size() + " files");

		OrderDeps od = new OrderDeps(fiPrimaries);
		List<FileInfo> ordered = od.getOrderedFiles();
		List<String> oErrors = od.getErrors();

		for (int i = 0, length = oErrors.size(); i < length; i++) {
			String err = oErrors.get(i);
			System.out.println("Order Error #" + (i + 1) + ": " + err);
		}

		System.out.println("Primaries ordered");

		List<String> pPrimaries = new ArrayList<String>();

		for (int i = 0, length = ordered.size(); i < length; i++) {
			pPrimaries.add(ordered.get(i).getFilename());
		}

		String compilationLevel = settings.getLevel();

		GoogleClosureCompiler compiler = new GoogleClosureCompiler(pPrimaries,
				settings.getOutput(), compilationLevel, pExternals);
		compiler.setWrapper(settings.getWrapper());
		compiler.setWarningLevel(WarningLevel.VERBOSE);
		compiler.run();
		System.out.println("Compiled file: " + settings.getOutput());

		JSError[] errors = compiler.getErrors();
		for (int i = 0, length = errors.length; i < length; i++) {
			JSError err = errors[i];
			System.out.println("Error #" + (i + 1) + ": " + err.description
					+ " in " + err.sourceName + " (" + err.lineNumber + ")");
		}
		if (errors.length == 0) {
			JSError[] warnings = compiler.getWarnings();
			for (int i = 0, length = warnings.length; i < length; i++) {
				JSError warn = warnings[i];
				System.out.println("Warning #" + (i + 1) + ": "
						+ warn.description + " in " + warn.sourceName + " ("
						+ warn.lineNumber + ")");
			}
		}
		
		
		System.out.println("\n\n Thank you for using kJScompiler!\n Who said JavaScript can't be compiled?\n");

		System.exit(0);
	}

}
