@echo off

echo Creating JAR-File %1% ...
cd ..\src\%1%\jar
jar cmf ../MANIFEST.MF ../../../out/%1%.jar *.*
cd ..\..\..\build

