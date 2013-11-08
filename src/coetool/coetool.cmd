@echo off
set JAVA_OPTIONS=-Xmx1024M

set OS_ARCH=32
rem Detect 64/32 bit OS automatically
Set RegQry=HKLM\Hardware\Description\System\CentralProcessor\0
REG.exe Query %RegQry% > checkOS.txt
Find /i "x86" < CheckOS.txt > StringCheck.txt
 
If %ERRORLEVEL% == 0 (
    set OS_ARCH=32
) ELSE (
    set OS_ARCH=64
)

set PATH=%CD%;%CD%\swt\%OS_ARCH%bit;%CD%\platform\%OS_ARCH%bit;C:\WINDOWS\system32

if NOT "%1"=="-d" goto :Normal
echo Classpath: %JARMENUCP%
echo Current directory: %CD%
echo Path: %PATH%
echo Full command: java.exe -cp "%JARMENUCP%" %JAVA_OPTIONS% com.cordys.coe.jarmenu.JarMenu
%JAVA_HOME%\bin\java.exe %JAVA_OPTIONS% -cp coetool.jar com.cordys.coe.jarmenu.JarMenu 
pause
goto :Finished

:Normal
start %JAVA_HOME%\bin\javaw.exe %JAVA_OPTIONS% -cp coetool.jar com.cordys.coe.jarmenu.JarMenu

:Finished