#ifndef CRYLIBRARY_H__
#define CRYLIBRARY_H__

/*!
	CryLibrary
	
	Convenience-Macros which abstract the use of DLLs/shared libraries in a platform independent way.
	A short explanation of the different macros follows:
	
	CrySharedLibraySupported:
		This macro can be used to test if the current active platform support shared library calls. The default
		value is false. This gets redefined if a certain platform (WIN32 or LINUX) is desired.

	CrySharedLibrayExtension:
		The default extension which will get appended to library names in calls to CryLoadLibraryDefExt
		(see below).

	CryLoadLibrary(libName):
		Loads a shared library.

	CryLoadLibraryDefExt(libName):
		Loads a shared library. The platform-specific default extension is appended to the libName. This allows
		writing of somewhat platform-independent library loading code and is therefore the function which should
		be used most of the time, unless some special extensions are used (e.g. for plugins).
	
	CryGetProcAddress(libHandle, procName):
		Import function from the library presented by libHandle.
		
	CryFreeLibrary(libHandle):
		Unload the library presented by libHandle.
	
	HISTORY:
		03.03.2004 MarcoK
			- initial version
			- added to CryPlatform
*/

#include <stdio.h>

#if defined(WIN32)
	#include <windows.h>

	#define CrySharedLibraySupported true
	#define CrySharedLibrayExtension ".dll"
	#define CryLoadLibrary(libName) ::LoadLibrary(libName)
	#define CryGetProcAddress(libHandle, procName) ::GetProcAddress((HMODULE)libHandle, procName)
	#define CryFreeLibrary(libHandle) ::FreeLibrary(libHandle)
#elif defined(LINUX)
	#include <dlfcn.h>
	#include <stdlib.h>
	#include "platform.h"

	// for compatibility with code written for windows
	#define CrySharedLibraySupported true
	#define CrySharedLibrayExtension ".so"
	#define CryGetProcAddress(libHandle, procName) ::dlsym(libHandle, procName)
	#define CryFreeLibrary(libHandle) ::dlclose(libHandle)

	#define HMODULE void*
	static const char* gEnvName("MODULE_PATH");

	static const char* GetModulePath()
	{
		return getenv(gEnvName);
	}

	static void SetModulePath(const char* pModulePath)
	{
		setenv(gEnvName, pModulePath?pModulePath:"",true);
	}

	static HMODULE CryLoadLibrary(const char* libName, const bool cAppend = true, const bool cLoadLazy = false)
	{
		const char* pModPath = GetModulePath();
		string newLibName = "";
		if (pModPath && strlen(pModPath) > 0)
		{
			newLibName = pModPath;
			if (newLibName.back() != '/')
				newLibName += "/";
			newLibName += libName;
		}
		HMODULE h = NULL;
		if (!newLibName.empty())
		{
			h = ::dlopen(newLibName.c_str(), cLoadLazy?(RTLD_LAZY | RTLD_GLOBAL):(RTLD_NOW | RTLD_GLOBAL));
		}
		if (!h)
		{
			h = ::dlopen(libName, cLoadLazy?(RTLD_LAZY | RTLD_GLOBAL):(RTLD_NOW | RTLD_GLOBAL));
		}
		if (!h && libName)
		{
			// Fallback: try converting Foo.dll to libFoo.so
			string altName = libName;
			size_t lastSlash = altName.find_last_of('/');
			string file = (lastSlash != string::npos) ? altName.substr(lastSlash + 1) : altName;

			if (file.length() > 4 && file.substr(file.length() - 4) == ".dll")
				file = file.substr(0, file.length() - 4) + ".so";
			if (file.rfind("lib", 0) != 0 && file.find(".so") != string::npos)
				file = "lib" + file;

			if (pModPath && strlen(pModPath) > 0)
			{
				string modCandidate = string(pModPath) + "/" + file;
				h = ::dlopen(modCandidate.c_str(), cLoadLazy?(RTLD_LAZY | RTLD_GLOBAL):(RTLD_NOW | RTLD_GLOBAL));
			}
			if (!h)
			{
				h = ::dlopen(file.c_str(), cLoadLazy?(RTLD_LAZY | RTLD_GLOBAL):(RTLD_NOW | RTLD_GLOBAL));
			}
		}
		return h;
	}


#else
#define CrySharedLibraySupported false
#define CrySharedLibrayExtension ""
#define CryLoadLibrary(libName) NULL
#define CryLoadLibraryDefExt(libName) CryLoadLibrary(libName CrySharedLibrayExtension)
#define CryGetProcAddress(libHandle, procName) NULL
#define CryFreeLibrary(libHandle)
#endif 

#endif //CRYLIBRARY_H__
