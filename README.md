##Kjscompiler
Makes compilation of multiple JavaScript files with *Google Closure Compiler* application in right order.

###Illustration
Goal is to convert layout at input:

![from](https://github.com/knyga/kjscompiler/blob/master/examples/external/diagramFrom.jpg "From")
In to minifed layout:

![to](https://github.com/knyga/kjscompiler/blob/master/examples/external/diagramTo.jpg "To")
With respect to internal and external dependencies.

###Requirements
Requires Java Runtime Environment version 7.

###How to run
`java -jar kjscompile.jar`

###Kjscompile.json
Instead of kjscompile.json you can specify your own path to configuration file with `--settings %path%` attribute.

`basedir` - directory with JavaScript files;

`output` - output JavaScript file name;

`pattern` - rule for filename (*.js);

`level` - level of optimization (WHITESPACE_ONLY, SIMPLE_OPTIMIZATIONS, ADVANCED_OPTIMIZATIONS).

###Annotating JavaScript files
Kjscompiler can use information about JavaScript file to build right compiling chain.

| Tag        | Example           | Description  |
| ------------- |-------------| -----|
| @depends     | @depends {somescript.js} | Specifies dependency on other file. |
| @ignore | @ignore      |    File with this mark will be ignored. |
| @external      | @external     |   File with this mark will be considered as external. It will not be compiled, but it will give no unknown variable error during the compilation |

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
 * kjscompilerannotation
 * @external
 */
```

###Debug
To debug add `--debug true` in call line.

###Copyright
Oleksandr Knyga, 2014
