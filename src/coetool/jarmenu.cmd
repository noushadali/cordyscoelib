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

set JARMENUCP=%CD%\jcom.jar
set JARMENUCP=%JARMENUCP%;%CD%\coelib.jar
set JARMENUCP=%JARMENUCP%;%CD%\coelib-full.jar
set JARMENUCP=%JARMENUCP%;%CD%\log4j-1.2.15.jar
set JARMENUCP=%JARMENUCP%;%CD%\xbean.jar
set JARMENUCP=%JARMENUCP%;%CD%\wsdl4j.jar
set JARMENUCP=%JARMENUCP%;%CD%\qname.jar

rem Eclipse SWT libs
set JARMENUCP=%JARMENUCP%;%CD%\org.eclipse.core.commands_3.2.0.I20060605-1400.jar
set JARMENUCP=%JARMENUCP%;%CD%\swt\%OS_ARCH%bit\swt.jar
set JARMENUCP=%JARMENUCP%;%CD%\org.eclipse.core.runtime_3.2.0.v20060603.jar
set JARMENUCP=%JARMENUCP%;%CD%\org.eclipse.jface_3.2.0.I20060605-1400.jar
set JARMENUCP=%JARMENUCP%;%CD%\org.eclipse.equinox.common_3.2.0.v20060603.jar

rem Cordys libs
set JARMENUCP=%JARMENUCP%;%CD%\esbclient.jar
set JARMENUCP=%JARMENUCP%;%CD%\esbserver.jar
set JARMENUCP=%JARMENUCP%;%CD%\eibxml.jar
set JARMENUCP=%JARMENUCP%;%CD%\basicutil.jar
set JARMENUCP=%JARMENUCP%;%CD%\managementlib.jar

rem Apache commons libs
set JARMENUCP=%JARMENUCP%;%CD%\commons-codec-1.6.jar
set JARMENUCP=%JARMENUCP%;%CD%\commons-collections-3.2.jar
set JARMENUCP=%JARMENUCP%;%CD%\commons-configuration-1.3.jar
set JARMENUCP=%JARMENUCP%;%CD%\fluent-hc-4.2.2.jar
set JARMENUCP=%JARMENUCP%;%CD%\httpclient-4.2.2.jar
set JARMENUCP=%JARMENUCP%;%CD%\httpclient-cache-4.2.2.jar
set JARMENUCP=%JARMENUCP%;%CD%\httpcore-4.2.2.jar
set JARMENUCP=%JARMENUCP%;%CD%\httpmime-4.2.2.jar
set JARMENUCP=%JARMENUCP%;%CD%\commons-lang-2.2.jar
set JARMENUCP=%JARMENUCP%;%CD%\commons-logging-1.1.1.jar
set JARMENUCP=%JARMENUCP%;%CD%\bcprov-jdk16-141.jar

rem Otherr jars
set JARMENUCP=%JARMENUCP%;%CD%\org-netbeans-swing-outline.jar
set JARMENUCP=%JARMENUCP%;%CD%\miglayout15-swing.jar

if NOT "%1"=="-d" goto :Normal
echo Classpath: %JARMENUCP%
echo Current directory: %CD%
echo Path: %PATH%
echo Full command: java.exe -cp "%JARMENUCP%" %JAVA_OPTIONS% com.cordys.coe.jarmenu.JarMenu
%JAVA_HOME%\bin\java.exe -cp "%JARMENUCP%" %JAVA_OPTIONS% com.cordys.coe.jarmenu.JarMenu
pause
goto :Finished

:Normal
start %JAVA_HOME%\bin\javaw.exe -cp "%JARMENUCP%" %JAVA_OPTIONS% com.cordys.coe.jarmenu.JarMenu

:Finished