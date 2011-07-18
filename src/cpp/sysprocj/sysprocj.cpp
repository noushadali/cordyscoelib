/**
 * © 2003 Cordys R&D B.V. All rights reserved.     The computer program(s) is
 * the proprietary information of Cordys R&D B.V.     and provided under the
 * relevant License Agreement containing restrictions     on use and
 * disclosure. Use is subject to the License Agreement.
 */

#include "stdafx.h"
#include "sysprocj.h"
#include <stdio.h>
#include <Winbase.h>
#include "Tlhelp32.h"
#include <malloc.h>
#include <stdlib.h>
#include "./../headers/com_cordys_coe_util_system_processes_SystemProcess.h"
#include "./../headers/com_cordys_coe_util_system_processes_SystemProcessList.h"

//Globals

/**
 * DllMain.
 */
BOOL APIENTRY DllMain( HANDLE hModule, 
                       DWORD  ul_reason_for_call, 
                       LPVOID lpReserved
					 )
{
    switch (ul_reason_for_call)
	{
		case DLL_PROCESS_ATTACH:
			EnablePrivilege();
			break;
		case DLL_THREAD_ATTACH:
		case DLL_THREAD_DETACH:
		case DLL_PROCESS_DETACH:
			break;
    }
    return TRUE;
}

/**
 * This method creates a new instance of the SystemProcess-class and fills 
 * the object with all the passed on data.
 */
jobject CreateNewSystemProcessObject(JNIEnv *jpEnv, char *cpPID, char *cpModule, int iCntThreads, char *cpParentPID, int iPriority, char *cpExeName)
{
	jobject joReturn = NULL;

	//Get the Class-object for the system-process.
	jclass cSysProc = jpEnv->FindClass("com/cordys/coe/util/system/processes/SystemProcess");
	if (cSysProc == NULL)
	{
		printf("Class SystemProcess not found.\n");
	}
	else
	{
		jmethodID miConstructor = jpEnv->GetMethodID(cSysProc, "<init>", "()V");
		if (miConstructor == NULL)
		{
			printf("Default constructor not found for SystemProcess...\n");
		}
		else
		{
			joReturn = jpEnv->NewObject(cSysProc, miConstructor);
			if (joReturn == NULL)
			{
				printf("New object failed...\n");
			}
			else
			{
				//Get the method-id's for the methods that need to be called.
				jmethodID miSetPID = jpEnv->GetMethodID(jpEnv->GetObjectClass(joReturn)
					, "setProcessID", "(Ljava/lang/String;)V");
				jmethodID miSetModule = jpEnv->GetMethodID(jpEnv->GetObjectClass(joReturn)
					, "setModuleID", "(Ljava/lang/String;)V");
				jmethodID miSetThreads = jpEnv->GetMethodID(jpEnv->GetObjectClass(joReturn)
					, "setCntThreads", "(I)V");
				jmethodID miSetParentPID = jpEnv->GetMethodID(jpEnv->GetObjectClass(joReturn)
					, "setParentPID", "(Ljava/lang/String;)V");
				jmethodID miSetPriority = jpEnv->GetMethodID(jpEnv->GetObjectClass(joReturn)
					, "setPriority", "(I)V");
				jmethodID miSetExeName = jpEnv->GetMethodID(jpEnv->GetObjectClass(joReturn)
					, "setExeName", "(Ljava/lang/String;)V");

				if ((miSetPID != NULL) && (miSetModule != NULL) && (miSetThreads != NULL) &&
					(miSetParentPID != NULL) && (miSetPriority != NULL) && (miSetExeName != NULL))
				{
					//Set the PID
					jstring jsTemp = jpEnv->NewStringUTF(cpPID);
					jpEnv->CallVoidMethod(joReturn, miSetPID, jsTemp);

					//Set the Module
					jsTemp = jpEnv->NewStringUTF(cpModule);
					jpEnv->CallVoidMethod(joReturn, miSetModule, jsTemp);

					//Set the CntThreads
					jint jiTemp = iCntThreads;
					jpEnv->CallVoidMethod(joReturn, miSetThreads, jiTemp);

					//Set the ParentPID
					jsTemp = jpEnv->NewStringUTF(cpParentPID);
					jpEnv->CallVoidMethod(joReturn, miSetParentPID, jsTemp);

					//Set the Priority
					jiTemp = iPriority;
					jpEnv->CallVoidMethod(joReturn, miSetPriority, jiTemp);

					//Set the ExeName
					jsTemp = jpEnv->NewStringUTF(cpExeName);
					jpEnv->CallVoidMethod(joReturn, miSetExeName, jsTemp);
				}
				else
				{
					printf("One of the methods was not found.\n");
				}
			}
		}
	}

	return joReturn;
}//CreateNewSystemProcessObject


/**
 * JNI method. Adds the stuff.
 */
JNIEXPORT void JNICALL Java_com_cordys_coe_util_system_processes_SystemProcessList_nativeGetSystemProcesses
  (JNIEnv *jpEnv, jobject jInstance, jobject oVector)

{
	printf("In getSystemProcesses\n");
	
	//Get the methodID for the add-method of the vector
	jmethodID miVectorAddElement = jpEnv->GetMethodID(jpEnv->GetObjectClass(oVector)
													  , "addElement"
													  , "(Ljava/lang/Object;)V" );
	if (miVectorAddElement == NULL)
	{
		printf("Vector.addElement not found\n");
	}
	else
	{
		//Now make the snapshot of the system processes.
		HANDLE hSnapShot = CreateToolhelp32Snapshot (TH32CS_SNAPALL,NULL);

		PROCESSENTRY32 pEntry;
		pEntry.dwSize =sizeof(pEntry);
		
		//Get first process
		Process32First (hSnapShot,&pEntry);

		//Itterate thruh all the processes.
		while (Process32Next (hSnapShot,&pEntry) == TRUE)
		{
			//ProcessID
			char acProcessID[255];
			sprintf(acProcessID, "%d",pEntry.th32ProcessID);
			//ModuleID
			char acModuleID[255];
			sprintf(acModuleID, "%d",pEntry.th32ModuleID);
			//ParentPID
			char acParentPID[255];
			sprintf(acParentPID, "%d",pEntry.th32ParentProcessID);
			//Priority
			char acPriority[255];
			sprintf(acPriority, "%d",pEntry.pcPriClassBase);
			//Threads
			char acCntThreads[255];
			sprintf(acCntThreads, "%d",pEntry.cntThreads);

			//Now we have all the data, so now we can add the process to the list.
			jobject oSysProc = CreateNewSystemProcessObject(jpEnv, acProcessID, acModuleID, atoi(acCntThreads), acParentPID, atoi(acPriority), pEntry.szExeFile);
			if (oSysProc == NULL)
			{
				printf("Error creating new SystemProcess.\n");
			}
			else
			{
				jpEnv->CallVoidMethod(oVector, miVectorAddElement, oSysProc);
			}
		}
	}
}//Java_com_cordys_coe_util_system_processes_SystemProcessList_getSystemProcesses

/**
 * This method enables the possibility to kill processes.
 */
JNIEXPORT void JNICALL Java_com_cordys_coe_util_system_processes_SystemProcessList_nativeEnableKill
  (JNIEnv *jpEnv, jobject oThis)
{
	EnablePrivilege();
}//Java_com_cordys_coe_util_system_processes_SystemProcessList_nativeEnableKill

/**
 * This method disables the possibility to kill processes.
 */
JNIEXPORT void JNICALL Java_com_cordys_coe_util_system_processes_SystemProcessList_nativeDisableKill
  (JNIEnv *jpEnv, jobject oThis)
{
	DisablePrivilege();
}//Java_com_cordys_coe_util_system_processes_SystemProcessList_nativeDisableKill

/**
 * This method kills the passed on SystemProcess.
 */
JNIEXPORT void JNICALL Java_com_cordys_coe_util_system_processes_SystemProcessList_nativeKillProcess
  (JNIEnv *jpEnv, jobject oThis, jobject oSysProc)
{
	//Get the processid
	jmethodID miGetProcessID = jpEnv->GetMethodID(jpEnv->GetObjectClass(oSysProc)
		, "getProcessID", "()Ljava/lang/String;");
	if (miGetProcessID != NULL)
	{
		jstring jsPID = (jstring) jpEnv->CallObjectMethod(oSysProc, miGetProcessID);
		const char *cpPID = jpEnv->GetStringUTFChars(jsPID, NULL);
		if (cpPID != NULL)
		{
			printf("Going to kill %s\n", cpPID);
			//Convert the string to a DWORD
			DWORD dProcID = atol(cpPID);

			//Find the process
			HANDLE hProcess = OpenProcess( PROCESS_TERMINATE, 0, dProcID );

			if (hProcess == NULL)
			{
				printf("Process not found.\n");
			}
			else
			{
				//Code 9 means terminate immediately.
				BOOL ret = TerminateProcess( hProcess, 9 );
				if (ret == FALSE)
				{
					printf("Error killing process.\n");
				}
			}
		}
		else
		{
			printf("PID is empty\n");
		}
		jpEnv->ReleaseStringUTFChars(jsPID, cpPID);

	}
	else
	{
		printf("Could not find getProcessID-method.\n");
	}
}//Java_com_cordys_coe_util_system_processes_SystemProcessList_nativeKillProcess

/**
 * This method returns the processid of the current process.
 *
 * @return A long containing the processID of the current process
 */
JNIEXPORT jlong JNICALL Java_com_cordys_coe_util_system_processes_SystemProcessList_getCurrentProcessID
  (JNIEnv *jpEnv, jclass oThis)
{
	return GetCurrentProcessId();
}//Java_com_cordys_coe_util_system_processes_SystemProcessList_getCurrentProcessID

/**************************************************************/
/***** Functions below are to set the privilege to be able ****/
/***** to kill all processes, including services.          ****/
/**************************************************************/
BOOL SetPrivilege(
    HANDLE hToken,          // token handle
    LPCTSTR Privilege,      // Privilege to enable/disable
    BOOL bEnablePrivilege   // TRUE to enable.  FALSE to disable
    )
{
    TOKEN_PRIVILEGES tp;
    LUID luid;
    TOKEN_PRIVILEGES tpPrevious;
    DWORD cbPrevious=sizeof(TOKEN_PRIVILEGES);

    if(!LookupPrivilegeValue( NULL, Privilege, &luid )) 
		return FALSE;

    // 
    // first pass.  get current privilege setting
    // 
    tp.PrivilegeCount           = 1;
    tp.Privileges[0].Luid       = luid;
    tp.Privileges[0].Attributes = 0;

    AdjustTokenPrivileges(
            hToken,
            FALSE,
            &tp,
            sizeof(TOKEN_PRIVILEGES),
            &tpPrevious,
            &cbPrevious
            );

    if (GetLastError() != ERROR_SUCCESS) 
		return FALSE;

    // 
    // second pass.  set privilege based on previous setting
    // 
    tpPrevious.PrivilegeCount       = 1;
    tpPrevious.Privileges[0].Luid   = luid;

    if(bEnablePrivilege) 
	{
        tpPrevious.Privileges[0].Attributes |= (SE_PRIVILEGE_ENABLED);
    }
    else 
	{
        tpPrevious.Privileges[0].Attributes ^= (SE_PRIVILEGE_ENABLED &
            tpPrevious.Privileges[0].Attributes);
    }

    AdjustTokenPrivileges(
            hToken,
            FALSE,
            &tpPrevious,
            cbPrevious,
            NULL,
            NULL
            );

    if (GetLastError() != ERROR_SUCCESS) 
		return FALSE;

    return TRUE;
}

// Set SE_DEBUG privilige for TaskMgr process
// This will make it able to kill services as well
ULONG EnablePrivilege()
{

	HANDLE hToken;

	if ( ! OpenProcessToken( 
				GetCurrentProcess(),
				TOKEN_ADJUST_PRIVILEGES | TOKEN_QUERY, 
				&hToken ))

		return 1;


    if ( ! SetPrivilege( hToken, SE_DEBUG_NAME, TRUE ))
    {
        CloseHandle(hToken);
        return 2;
    }

    CloseHandle(hToken);
    return 0;
}

/**
 * This method disables the ability to kill all processes for this process
 */
ULONG DisablePrivilege()
{

	HANDLE hToken;

	if ( ! OpenProcessToken( 
				GetCurrentProcess(),
				TOKEN_ADJUST_PRIVILEGES | TOKEN_QUERY, 
				&hToken ))

		return 1;


    if ( ! SetPrivilege( hToken, SE_DEBUG_NAME, FALSE ))
    {
        CloseHandle(hToken);
        return 2;
    }

    CloseHandle(hToken);
    return 0;
}

