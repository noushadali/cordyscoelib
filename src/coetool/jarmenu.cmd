@echo off
set PATH=%CD%;C:\WINDOWS\system32

set JAVA_OPTIONS=-Xmx1024M

set JARMENUCP=%CD%\jcom.jar
set JARMENUCP=%JARMENUCP%;%CD%\coelib.jar
set JARMENUCP=%JARMENUCP%;%CD%\coelib-full.jar
set JARMENUCP=%JARMENUCP%;%CD%\log4j-1.2.15.jar
set JARMENUCP=%JARMENUCP%;%CD%\xbean.jar

rem Eclipse SWT libs
set JARMENUCP=%JARMENUCP%;%CD%\org.eclipse.core.commands_3.2.0.I20060605-1400.jar
set JARMENUCP=%JARMENUCP%;%CD%\org.eclipse.swt.win32.win32.x86_3.2.0.v3232m.jar
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
set JARMENUCP=%JARMENUCP%;%CD%\commons-codec-1.3.jar
set JARMENUCP=%JARMENUCP%;%CD%\commons-collections-3.2.jar
set JARMENUCP=%JARMENUCP%;%CD%\commons-configuration-1.3.jar
set JARMENUCP=%JARMENUCP%;%CD%\commons-httpclient-3.0.1.jar
set JARMENUCP=%JARMENUCP%;%CD%\commons-httpclient-contrib-3.0.jar
set JARMENUCP=%JARMENUCP%;%CD%\commons-lang-2.2.jar
set JARMENUCP=%JARMENUCP%;%CD%\commons-logging.jar
set JARMENUCP=%JARMENUCP%;%CD%\bcprov-jdk16-141.jar

rem Otherr jars
set JARMENUCP=%JARMENUCP%;%CD%\org-netbeans-swing-outline.jar

if NOT "%1"=="-d" goto :Normal
echo Classpath: %JARMENUCP%
echo Current directory: %CD%
echo Path: %PATH%
echo Full command: java.exe -cp "%JARMENUCP%" %JAVA_OPTIONS% com.cordys.coe.jarmenu.JarMenu
java.exe -cp "%JARMENUCP%" %JAVA_OPTIONS% com.cordys.coe.jarmenu.JarMenu
pause
goto :Finished

:Normal
start javaw.exe -cp "%JARMENUCP%" %JAVA_OPTIONS% com.cordys.coe.jarmenu.JarMenu

:Finished