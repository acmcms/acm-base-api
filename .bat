javac -g -verbose -source 1.3 -target 1.1 -d ..\jar -classpath ..\jar -sourcepath .\ .\ru\myx\sg\*.java
cd ..\jar\
jar cf ..\statsApplet.jar -C . *.*
cd ..\src\
copy /B /Y ..\statsApplet.jar Q:\ws_web\rt3\src\mwm\base\levels\applets\

