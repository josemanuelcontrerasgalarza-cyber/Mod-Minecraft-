# 🔫 Kratos Arsenal

Mod profesional de **armas modernas y futuristas** para **Minecraft 1.20.1** (Fabric, Java 17).
Incluye 10 armas, accesorios, munición por tipo, balística, headshots, retroceso, HUD,
recarga, modos de disparo, materiales y recetas equilibradas para supervivencia.

---

## 📦 Contenido

### Armas (10)
| Arma | Tipo | Munición | Modos |
|------|------|----------|-------|
| Glock 19 | Pistola | 9mm | Semi |
| Desert Eagle | Pistola pesada | .50 AE | Semi |
| MP5 | Subfusil | 9mm | Auto / Ráfaga / Semi |
| AK-47 | Rifle de asalto | 7.62mm | Auto / Semi |
| M4A1 | Rifle de asalto | 5.56mm | Auto / Ráfaga / Semi |
| Barrett M82 | Rifle de francotirador | .50 BMG | Semi |
| SPAS-12 | Escopeta | Cartuchos | Semi (8 perdigones) |
| Minigun | Ametralladora pesada | 7.62mm | Auto |
| Rifle de Plasma | Energía futurista | Célula de energía | Auto / Semi (incendia) |
| Lanzacohetes | Explosivos | Cohete | Semi (explosión) |

### Accesorios (6)
Silenciador, Mira holográfica, Mira x4, Mira x8, Empuñadura, Láser táctico.
Se montan con **Shift + clic derecho** sosteniendo el accesorio mientras se lleva el arma en la otra mano.

### Munición por tipo (8)
9mm · .50 AE · 7.62mm · 5.56mm · .50 BMG · Cartuchos · Célula de energía · Cohete

### Materiales y bloques
Acero, Titanio (lingote y bruto), Componentes electrónicos, Pólvora avanzada, Piezas de armas.
Bloques: Mineral de titanio (normal y de pizarra, se genera bajo tierra), Bloque de titanio, Bloque de acero.

---

## 🎮 Controles

| Acción | Control por defecto |
|--------|---------------------|
| Disparar | **Clic derecho** (mantener para automático) |
| Recargar | **R** |
| Cambiar modo de disparo | **B** |
| Apuntar / Zoom (ADS) | **Ctrl izquierdo** (mantener) |
| Montar accesorio | **Shift + clic derecho** |

Todas las teclas son reconfigurables en *Opciones → Controles → Kratos Arsenal*.

---

## ⚙️ Sistemas implementados

- **Balística hitscan** con dispersión gaussiana, alcance y caída de daño (*falloff*) por distancia.
- **Headshots**: daño multiplicado al impactar en la cabeza, con sonido y partículas propias.
- **Retroceso** vertical/horizontal sincronizado al cliente que dispara.
- **Sistema de cargadores y munición** guardado en el NBT del arma; la recarga consume munición del inventario.
- **Modos de disparo**: semiautomático, ráfaga y automático según el arma.
- **Mira con zoom** (mixin sobre el FOV) dependiente del accesorio montado (holo / x4 / x8).
- **Partículas**: fogonazo, humo, casquillos expulsados e impactos.
- **Sonidos personalizados** por arma (+ silenciado, recarga, disparo en seco, headshot).
- **HUD**: munición (cargador/reserva), nombre del arma, modo de disparo e indicador de recarga.
- **Compatibilidad cliente + servidor** (lógica autoritativa en el servidor).

---

## 🛠️ Compilación

Requisitos: **JDK 17** (o superior) y conexión a internet la primera vez.

```bash
# Compilar el mod
./gradlew build

# Probar en un cliente de desarrollo
./gradlew runClient

# Probar en un servidor de desarrollo
./gradlew runServer
```

El `.jar` final se genera en `build/libs/kratos-arsenal-1.0.0.jar`.
Cópialo a la carpeta `mods/` junto con **Fabric API** y **Fabric Loader 0.15+**.

> En Windows usa `gradlew.bat` en lugar de `./gradlew`.

---

## 🎨 Recursos (texturas / sonidos)

Las texturas (PNG 16×16) y los sonidos (OGG) incluidos son **placeholders de ejemplo**
generados de forma reproducible por el script:

```bash
python3 tools/gen_assets.py
```

Este script regenera **todas** las texturas, modelos, blockstates, idiomas (es/en),
recetas, tablas de botín, generación de mundo, etiquetas y sonidos de ejemplo.
Sustituye los archivos de `src/main/resources/assets/kratos_arsenal/textures` y
`.../sounds` por los tuyos definitivos manteniendo los mismos nombres.

> El script de sonidos usa `soundfile`/`numpy` (`pip install soundfile numpy`). Si no
> están disponibles, los `.ogg` se omiten pero el resto de recursos se generan igual.

---

## 📁 Estructura del proyecto

```
Mod-Minecraft-/
├── build.gradle / settings.gradle / gradle.properties   # Configuración de Fabric Loom
├── gradlew / gradlew.bat / gradle/wrapper/               # Gradle wrapper 8.7
├── tools/gen_assets.py                                   # Generador de recursos
└── src/main/
    ├── java/com/kratos/arsenal/
    │   ├── KratosArsenal.java          # Entrypoint común
    │   ├── gun/                        # GunType, FireMode, AmmoType, AttachmentType, GunData, GunEngine
    │   ├── item/                       # GunItem, AttachmentItem
    │   ├── registry/                   # ModItems, ModBlocks, ModSounds, ModItemGroups
    │   ├── network/                    # ModNetworking (paquetes C2S/S2C)
    │   ├── world/                      # ModWorldGen (mineral de titanio)
    │   ├── client/                     # KratosArsenalClient, HUD, teclas, zoom, estado
    │   ├── mixin/                      # GameRendererMixin (zoom de FOV)
    │   └── util/                       # InventoryHelper
    └── resources/
        ├── fabric.mod.json
        ├── kratos_arsenal.client.mixins.json
        ├── assets/kratos_arsenal/      # texturas, modelos, lang, sonidos
        └── data/kratos_arsenal/        # recetas, loot, worldgen, tags
```

---

## 🧪 Notas de diseño

- Las explosiones del lanzacohetes respetan la regla de juego `mobGriefing` (no destruyen
  bloques si está desactivada), ideal para supervivencia multijugador.
- En modo creativo la munición es ilimitada.
- Añadir un arma nueva es tan sencillo como añadir una entrada al enum `GunType` y su
  textura/sonido/receta correspondientes.

## 📜 Licencia
MIT — ver [LICENSE](LICENSE).
