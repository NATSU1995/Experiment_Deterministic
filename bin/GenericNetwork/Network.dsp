# Microsoft Developer Studio Project File - Name="Network" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 5.00
# ** DO NOT EDIT **

# TARGTYPE "Java Virtual Machine Java Project" 0x0809

CFG=Network - Java Virtual Machine Debug
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "Network.mak".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "Network.mak" CFG="Network - Java Virtual Machine Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "Network - Java Virtual Machine Release" (based on\
 "Java Virtual Machine Java Project")
!MESSAGE "Network - Java Virtual Machine Debug" (based on\
 "Java Virtual Machine Java Project")
!MESSAGE 

# Begin Project
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
JAVA=jvc.exe

!IF  "$(CFG)" == "Network - Java Virtual Machine Release"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir ""
# PROP BASE Intermediate_Dir ""
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 0
# PROP Output_Dir ""
# PROP Intermediate_Dir ""
# PROP Target_Dir ""
# ADD BASE JAVA /O
# ADD JAVA /O

!ELSEIF  "$(CFG)" == "Network - Java Virtual Machine Debug"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir ""
# PROP BASE Intermediate_Dir ""
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 1
# PROP Output_Dir ""
# PROP Intermediate_Dir ""
# PROP Target_Dir ""
# ADD BASE JAVA /g
# ADD JAVA /g

!ENDIF 

# Begin Target

# Name "Network - Java Virtual Machine Release"
# Name "Network - Java Virtual Machine Debug"
# Begin Source File

SOURCE=.\BasicArcInfo.java
# End Source File
# Begin Source File

SOURCE=.\BasicNetInfo.java
# End Source File
# Begin Source File

SOURCE=.\BasicNetLink.java
# End Source File
# Begin Source File

SOURCE=.\BasicNodeInfo.java
# End Source File
# Begin Source File

SOURCE=.\BasicNodeLink.java
# End Source File
# Begin Source File

SOURCE=.\GenericNetwork.java
# End Source File
# Begin Source File

SOURCE=..\MyNetwork\MyArcInfo.java
# End Source File
# Begin Source File

SOURCE=..\MyNetwork\MyNetInfo.java
# End Source File
# Begin Source File

SOURCE=..\MyNetwork\MyNetwork.java
# End Source File
# Begin Source File

SOURCE=..\MyNetwork\MyNetworkDisplay.java
# End Source File
# Begin Source File

SOURCE=..\MyNetwork\MyNetworkInterface.java
# End Source File
# Begin Source File

SOURCE=..\MyNetwork\MyNodeInfo.java
# End Source File
# Begin Source File

SOURCE="..\Network-eg\TestNetworkA.java"
# End Source File
# Begin Source File

SOURCE="..\Network-eg\TestNetworkB.java"
# End Source File
# Begin Source File

SOURCE="..\Network-eg\TestNetworkC.java"
# End Source File
# Begin Source File

SOURCE="..\Network-eg\TestNetworkD.java"
# End Source File
# End Target
# End Project
