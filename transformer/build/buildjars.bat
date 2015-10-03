call ..\..\setEnv.bat

@echo off

echo Creating ALL JAR-Files...
call createjar.bat dp
call createjar.bat image
call createjar.bat log
call createjar.bat screen
call createjar.bat session


SET CLASSPATH=%CLASSPATH%;%FRAMEWORKROOT%\out\mobcon.jar
echo Creating ALL INFO-Files...
call writetaginfo.bat dp
call writetaginfo.bat image
call writetaginfo.bat log
call writetaginfo.bat screen
call writetaginfo.bat session

pause