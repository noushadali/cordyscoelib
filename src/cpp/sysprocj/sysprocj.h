/**
 * © 2003 Cordys R&D B.V. All rights reserved.     The computer program(s) is
 * the proprietary information of Cordys R&D B.V.     and provided under the
 * relevant License Agreement containing restrictions     on use and
 * disclosure. Use is subject to the License Agreement.
 */


// The following ifdef block is the standard way of creating macros which make exporting 
// from a DLL simpler. All files within this DLL are compiled with the SYSPROCJ_EXPORTS
// symbol defined on the command line. this symbol should not be defined on any project
// that uses this DLL. This way any other project whose source files include this file see 
// SYSPROCJ_API functions as being imported from a DLL, wheras this DLL sees symbols
// defined with this macro as being exported.
#ifdef SYSPROCJ_EXPORTS
#define SYSPROCJ_API __declspec(dllexport)
#else
#define SYSPROCJ_API __declspec(dllimport)
#endif

#include "stdafx.h"

/**
 * Initializes the list of all running processes
 */
extern SYSPROCJ_API GetAllProcesses();

/**
 * This method enables the ability to kill all processes for this process
 */
ULONG EnablePrivilege();
/**
 * This method disables the ability to kill all processes for this process
 */
ULONG DisablePrivilege();