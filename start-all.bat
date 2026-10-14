@echo off
title Kanban Fullstack Launcher
echo ========================================================
echo Starting Kanban Monolith (Spring Boot Backend + Angular Frontend)
echo ========================================================
start "Kanban Backend (8085)" cmd /c "%~dp0start-backend.bat"
start "Kanban Frontend (4200)" cmd /c "%~dp0start-frontend.bat"
echo Both services are starting!
echo Backend will be at:  http://localhost:8085
echo Swagger UI at:       http://localhost:8085/swagger-ui.html
echo Frontend will be at: http://localhost:4200
echo ========================================================
