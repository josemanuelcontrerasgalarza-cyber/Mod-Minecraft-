@echo off
chcp 65001 >nul
title Compilar Kratos Arsenal
echo ==================================================
echo    KRATOS ARSENAL - Compilador automatico
echo ==================================================
echo.
echo Esto puede tardar varios minutos la primera vez
echo (descarga Minecraft y Fabric). Necesitas Java JDK 17.
echo.

call gradlew.bat build
if errorlevel 1 (
    echo.
    echo ==================================================
    echo    ERROR al compilar.
    echo    Comprueba que tienes instalado Java JDK 17.
    echo    Descargalo en: https://adoptium.net/
    echo ==================================================
    pause
    exit /b 1
)

if not exist "MOD_COMPILADO" mkdir MOD_COMPILADO
copy /Y "build\libs\kratos-arsenal-1.0.0.jar" "MOD_COMPILADO\" >nul

echo.
echo ==================================================
echo    LISTO!  El mod ya esta compilado.
echo.
echo    Lo encontraras en la carpeta:  MOD_COMPILADO
echo    Archivo:  kratos-arsenal-1.0.0.jar
echo.
echo    Copialo a tu carpeta .minecraft\mods\
echo    junto con Fabric API (para Minecraft 1.20.1).
echo ==================================================
echo.
pause
