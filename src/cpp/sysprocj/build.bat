@echo off
call "%VC_HOME%vcvars32.bat"
echo "Cleaning..."
nmake /f "sysprocj.mak" CFG=%1 CLEAN
echo "Building..."
nmake /f "sysprocj.mak" CFG=%1 ALL
