##Kjscompiler
Makes compilation of multiple JavaScript files with *Google Closure Compiler* application in right order.

Goal is to convert layout at input:

![from](https://raw.github.com/knyga/kjscompiler/master/examples/external/diagramFrom.jpg "From")

In to minifed layout:

![to](https://raw.github.com/knyga/kjscompiler/master/examples/external/diagramTo.jpg "To")

With respect to internal and external dependencies.

###Requirements
Requires Java Runtime Environment version 7.

###How to run
`java -jar kjscompiler.jar`

###Kjscompiler.json
Instead of kjscompile.json you can specify your own path to configuration file with `--settings %path%` attribute.

`basedir` - directory or array of directories with JavaScript files;

`output` - output JavaScript file name;

`pattern` - rule for filename (*.js);

`level` - level of optimization (WHITESPACE_ONLY, SIMPLE_OPTIMIZATIONS, ADVANCED_OPTIMIZATIONS).

`wrapper` - setting to wrap your output. The place holder is "%output%".

Example:
```javascript
{
	"basedir": "js",
	"output": "js.min/all.js",
	"level": "SIMPLE_OPTIMIZATIONS",
	"pattern": "*.js"
}
```
Example:
```javascript
{
	"basedir": ["lib/javascript", "lib/ecmascript"],
	"output": "js.min/all.js",
	"level": "SIMPLE_OPTIMIZATIONS",
	"pattern": "*.js",
	"wrapper" : "(function(){ %output% }());"
}
```

Note:
Any path will be relative to the settings file. If none provided it will be relative to the current working directory ( the directory where you launched kjscompiler ).


###Annotating JavaScript files
Kjscompiler can use information about JavaScript file to build right compiling chain.

| Tag        | Example           | Description  |
| ------------- |-------------| -----|
| @depends     | @depends {somescript.js} | Specifies dependency on other file. |
| @ignore | @ignore      |    File with this mark will be ignored. |
| @external      | @external     |   File with this mark will be considered as external. It will not be compiled, but it will give no unknown variable error during the compilation. |

```javascript
/**
 * kjscompiler annotation
 * @depends {somescript}
 * @depends {dir/otherscript}
 */
```

```javascript
/**
 * kjscompiler annotation
 * @ignore
 */
```

```javascript
/**
 * kjscompiler annotation
 * @external
 */
```

###Debug
To debug add `--debug true` in call line.

###Licence
kjscompiler

Copyright (C) 2014  Oleksandr Knyga, oleksandrknyga@gmail.com

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
