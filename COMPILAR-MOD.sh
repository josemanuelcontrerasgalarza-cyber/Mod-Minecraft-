#!/usr/bin/env bash
# ==================================================
#   KRATOS ARSENAL - Compilador automatico (Mac/Linux)
# ==================================================
set -e

echo "=================================================="
echo "   KRATOS ARSENAL - Compilando..."
echo "   (la primera vez tarda varios minutos)"
echo "   Necesitas Java JDK 17."
echo "=================================================="
echo

chmod +x ./gradlew 2>/dev/null || true

if ! ./gradlew build; then
    echo
    echo "=================================================="
    echo "   ERROR al compilar."
    echo "   Comprueba que tienes Java JDK 17 instalado."
    echo "   Descargalo en: https://adoptium.net/"
    echo "=================================================="
    exit 1
fi

mkdir -p MOD_COMPILADO
cp -f build/libs/kratos-arsenal-1.0.0.jar MOD_COMPILADO/

echo
echo "=================================================="
echo "   LISTO!  El mod ya esta compilado."
echo
echo "   Lo encontraras en la carpeta:  MOD_COMPILADO"
echo "   Archivo:  kratos-arsenal-1.0.0.jar"
echo
echo "   Copialo a tu carpeta .minecraft/mods/"
echo "   junto con Fabric API (para Minecraft 1.20.1)."
echo "=================================================="
