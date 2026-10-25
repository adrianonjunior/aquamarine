@echo off
chcp 65001 >nul
title Servidor Aquamarine
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0iniciar-servidor.ps1"
echo.
pause
