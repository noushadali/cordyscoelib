# Microsoft Developer Studio Generated NMAKE File, Based on sysprocj.dsp
!IF "$(CFG)" == ""
CFG=sysprocj - Win32 Debug
!MESSAGE No configuration specified. Defaulting to sysprocj - Win32 Debug.
!ENDIF 

!IF "$(CFG)" != "sysprocj - Win32 Release" && "$(CFG)" != "sysprocj - Win32 Debug"
!MESSAGE Invalid configuration "$(CFG)" specified.
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "sysprocj.mak" CFG="sysprocj - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "sysprocj - Win32 Release" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE "sysprocj - Win32 Debug" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE 
!ERROR An invalid configuration is specified.
!ENDIF 

!IF "$(OS)" == "Windows_NT"
NULL=
!ELSE 
NULL=nul
!ENDIF 

!IF  "$(CFG)" == "sysprocj - Win32 Release"

OUTDIR=.\..\output
INTDIR=.\../intermediate
# Begin Custom Macros
OutDir=.\..\output
# End Custom Macros

ALL : "$(OUTDIR)\sysprocj.dll"


CLEAN :
	-@erase "$(INTDIR)\StdAfx.obj"
	-@erase "$(INTDIR)\sysprocj.obj"
	-@erase "$(INTDIR)\sysprocj.pch"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(OUTDIR)\sysprocj.dll"
	-@erase "$(OUTDIR)\sysprocj.exp"
	-@erase "$(OUTDIR)\sysprocj.lib"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

"$(INTDIR)" :
    if not exist "$(INTDIR)/$(NULL)" mkdir "$(INTDIR)"

CPP=cl.exe
CPP_PROJ=/nologo /MT /W3 /GX /O2 /I "C:\j2sdk1.4.2\include" /I "C:\j2sdk1.4.2\include\win32" /I "C:\Program Files\Cordys\WCP\include" /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "SYSPROCJ_EXPORTS" /Fp"$(INTDIR)\sysprocj.pch" /Yu"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 

.c{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cpp{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cxx{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.c{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cpp{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cxx{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

MTL=midl.exe
MTL_PROJ=/nologo /D "NDEBUG" /mktyplib203 /win32 
RSC=rc.exe
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\sysprocj.bsc" 
BSC32_SBRS= \
	
LINK32=link.exe
LINK32_FLAGS=kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib javastd.lib jvm.lib /nologo /dll /incremental:no /pdb:"$(OUTDIR)\sysprocj.pdb" /machine:I386 /out:"$(OUTDIR)\sysprocj.dll" /implib:"$(OUTDIR)\sysprocj.lib" /libpath:"..\libs\bcp4_1fp1" /libpath:"..\libs\java1_4_2" 
LINK32_OBJS= \
	"$(INTDIR)\StdAfx.obj" \
	"$(INTDIR)\sysprocj.obj"

"$(OUTDIR)\sysprocj.dll" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

!ELSEIF  "$(CFG)" == "sysprocj - Win32 Debug"

OUTDIR=.\../dbgoutput
INTDIR=.\../intermediate
# Begin Custom Macros
OutDir=.\../dbgoutput
# End Custom Macros

ALL : "$(OUTDIR)\sysprocj.dll" "$(OUTDIR)\sysprocj.bsc"


CLEAN :
	-@erase "$(INTDIR)\StdAfx.obj"
	-@erase "$(INTDIR)\StdAfx.sbr"
	-@erase "$(INTDIR)\sysprocj.obj"
	-@erase "$(INTDIR)\sysprocj.pch"
	-@erase "$(INTDIR)\sysprocj.sbr"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(INTDIR)\vc60.pdb"
	-@erase "$(OUTDIR)\sysprocj.bsc"
	-@erase "$(OUTDIR)\sysprocj.dll"
	-@erase "$(OUTDIR)\sysprocj.exp"
	-@erase "$(OUTDIR)\sysprocj.ilk"
	-@erase "$(OUTDIR)\sysprocj.lib"
	-@erase "$(OUTDIR)\sysprocj.pdb"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

"$(INTDIR)" :
    if not exist "$(INTDIR)/$(NULL)" mkdir "$(INTDIR)"

CPP=cl.exe
CPP_PROJ=/nologo /MTd /W3 /Gm /GX /ZI /Od /I "C:\j2sdk1.4.2\include" /I "C:\j2sdk1.4.2\include\win32" /I "C:\Program Files\Cordys\WCP\include" /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "SYSPROCJ_EXPORTS" /FR"$(INTDIR)\\" /Fp"$(INTDIR)\sysprocj.pch" /Yu"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /GZ /c 

.c{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cpp{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cxx{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.c{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cpp{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cxx{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

MTL=midl.exe
MTL_PROJ=/nologo /D "_DEBUG" /mktyplib203 /win32 
RSC=rc.exe
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\sysprocj.bsc" 
BSC32_SBRS= \
	"$(INTDIR)\StdAfx.sbr" \
	"$(INTDIR)\sysprocj.sbr"

"$(OUTDIR)\sysprocj.bsc" : "$(OUTDIR)" $(BSC32_SBRS)
    $(BSC32) @<<
  $(BSC32_FLAGS) $(BSC32_SBRS)
<<

LINK32=link.exe
LINK32_FLAGS=kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib javastd.lib jvm.lib /nologo /dll /incremental:yes /pdb:"$(OUTDIR)\sysprocj.pdb" /debug /machine:I386 /out:"$(OUTDIR)\sysprocj.dll" /implib:"$(OUTDIR)\sysprocj.lib" /pdbtype:sept /libpath:"..\libs" 
LINK32_OBJS= \
	"$(INTDIR)\StdAfx.obj" \
	"$(INTDIR)\sysprocj.obj"

"$(OUTDIR)\sysprocj.dll" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

!ENDIF 


!IF "$(NO_EXTERNAL_DEPS)" != "1"
!IF EXISTS("sysprocj.dep")
!INCLUDE "sysprocj.dep"
!ELSE 
!MESSAGE Warning: cannot find "sysprocj.dep"
!ENDIF 
!ENDIF 


!IF "$(CFG)" == "sysprocj - Win32 Release" || "$(CFG)" == "sysprocj - Win32 Debug"
SOURCE=.\StdAfx.cpp

!IF  "$(CFG)" == "sysprocj - Win32 Release"

CPP_SWITCHES=/nologo /MT /W3 /GX /O2 /I "C:\j2sdk1.4.2\include" /I "C:\j2sdk1.4.2\include\win32" /I "C:\Program Files\Cordys\WCP\include" /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "SYSPROCJ_EXPORTS" /Fp"$(INTDIR)\sysprocj.pch" /Yc"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 

"$(INTDIR)\StdAfx.obj"	"$(INTDIR)\sysprocj.pch" : $(SOURCE) "$(INTDIR)"
	$(CPP) @<<
  $(CPP_SWITCHES) $(SOURCE)
<<


!ELSEIF  "$(CFG)" == "sysprocj - Win32 Debug"

CPP_SWITCHES=/nologo /MTd /W3 /Gm /GX /ZI /Od /I "C:\j2sdk1.4.2\include" /I "C:\j2sdk1.4.2\include\win32" /I "C:\Program Files\Cordys\WCP\include" /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "SYSPROCJ_EXPORTS" /FR"$(INTDIR)\\" /Fp"$(INTDIR)\sysprocj.pch" /Yc"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /GZ /c 

"$(INTDIR)\StdAfx.obj"	"$(INTDIR)\StdAfx.sbr"	"$(INTDIR)\sysprocj.pch" : $(SOURCE) "$(INTDIR)"
	$(CPP) @<<
  $(CPP_SWITCHES) $(SOURCE)
<<


!ENDIF 

SOURCE=.\sysprocj.cpp

!IF  "$(CFG)" == "sysprocj - Win32 Release"


"$(INTDIR)\sysprocj.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\sysprocj.pch"


!ELSEIF  "$(CFG)" == "sysprocj - Win32 Debug"


"$(INTDIR)\sysprocj.obj"	"$(INTDIR)\sysprocj.sbr" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\sysprocj.pch"


!ENDIF 


!ENDIF 

