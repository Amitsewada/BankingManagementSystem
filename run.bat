@echo off
echo ========================================================
echo   Compiling Banking Management System (with SQLite DB)
echo ========================================================

rem Create output directory if it doesn't exist
if not exist "out" mkdir out

rem Compile all java files
javac -cp "lib/*" -d out src/com/banking/model/enums/*.java src/com/banking/exception/*.java src/com/banking/model/*.java src/com/banking/util/*.java src/com/banking/service/*.java src/com/banking/main/*.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed.
    pause
    exit /b %ERRORLEVEL%
)

echo [SUCCESS] Compilation complete.
echo.
echo ========================================================
echo   Starting Banking Management System
echo ========================================================
echo.

rem Run the main application with lib folder in classpath
java -cp "out;lib/*" com.banking.main.BankingApplication

pause
