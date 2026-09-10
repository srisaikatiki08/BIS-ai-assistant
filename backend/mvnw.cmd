@REM ----------------------------------------------------------------------------
@REM Maven Wrapper Script for Windows
@REM ----------------------------------------------------------------------------

@IF "%DEBUG%" == "" @ECHO OFF
@SETLOCAL

SET "MAVEN_CMD=%USERPROFILE%\.m2\apache-maven-3.9.6\bin\mvn.cmd"

IF EXIST "%MAVEN_CMD%" (
    "%MAVEN_CMD%" %*
) ELSE (
    mvn %*
)
