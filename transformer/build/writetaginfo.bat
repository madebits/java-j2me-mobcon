@echo off

echo Writing JAR-info %1% ...
cd ..\src\%1%\jar

SET PARAMETER=%1%.tag
SET PARAMETER=%PARAMETER% %TRANSFORMERROOT%\txt\%1%.txt

java mobcon.util.TagDicParser %PARAMETER%
cd ..\..\..\build