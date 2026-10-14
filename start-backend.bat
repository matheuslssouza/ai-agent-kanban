@echo off
title Kanban Spring Boot Backend (Port 8085)
echo ========================================================
echo Starting Spring Boot Kanban Backend on http://localhost:8085
echo ========================================================
cd /d "%~dp0backend"
"C:\Users\tizo\.m2\maven-3.9.6\bin\mvn.cmd" spring-boot:run
pause
