@echo off
REM 
chcp 65001 > nul

echo 🧪 Testing Auth Service...
cd auth-service
call mvnw test
if %errorlevel% neq 0 (
    echo ❌ Auth Service Tests Failed!
    exit /b %errorlevel%
)
cd ..

echo 🧪 Testing Identity Service...
cd identity-service
call mvnw test
if %errorlevel% neq 0 (
    echo ❌ Identity Service Tests Failed!
    exit /b %errorlevel%
)
cd ..

echo 🧪 Testing Matching Service...
cd matching-service
call mvnw test
if %errorlevel% neq 0 (
    echo ❌ Matching Service Tests Failed!
    exit /b %errorlevel%
)
cd ..

echo 🧪 Testing Chat Service...
cd chat-service
call mvnw test
if %errorlevel% neq 0 (
    echo ❌ Chat Service Tests Failed!
    exit /b %errorlevel%
)
cd ..

echo.
echo ✅ ALL TESTS PASSED!
pause