# 🔥 Lava Particles | More

[![License](https://img.shields.io/github/license/stepqq/lava-particles-more?style=flat-square&color=blue)](LICENSE)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1%20%7C%201.21.1-1f8b4c?style=flat-square&logo=minecraft&logoColor=white)](#)
[![Loaders](https://img.shields.io/badge/Loaders-Fabric%20%7C%20Forge%20%7C%20NeoForge-df5926?style=flat-square)](#)

**Lava Particles | More** is an immersive client-side Minecraft mod that completely overhauls the visual and audio atmosphere around lava. It introduces dynamic magma geysers, bubbling surface effects, realistic droplet physics, and boiling sizzle particles.
https://modrinth.com/mod/lava-particles-more

---

## ✨ Features

* **🌋 Lava Geysers & Eruptions**: Large lava lakes erupt with undulating columns of heat-distortion waves, rising bubbles, and low-frequency volcanic rumbling.
* **🫧 Magma Bubbling**: Surface lava pools bubble actively with tiny fizzing pops, while deep magma bubbles rise slowly and pop dramatically.
* **☄️ Fluid Splash Physics**: Magma droplets thrown into the air land on blocks, slide, and cool down from bright red to basalt. They fizz when falling into water.
* **💨 Realistic Combustion**: Stepping or submerging in lava creates dense smoke, sparks, bubbles, and sizzling audio.
* **⚙️ Advanced Configuration**: A premium, tabbed in-game GUI powered by Cloth Config to customize all visuals, audio, and gameplay settings.

---

## 🛠️ Project Structure & Building

This repository uses a multi-platform Gradle setup supporting:
* **Fabric** (1.20.1 & 1.21.1)
* **Forge** (1.20.1 & 1.21.1)
* **NeoForge** (1.20.1 & 1.21.1)

All platforms share common logic located in the `/common` directory.

### Requirements
* Java 21 (required for Gradle and building modern versions)

### How to Build
To compile and package the mod for all platforms:

1. Clone the repository:
   ```bash
   git clone https://github.com/YOUR_USERNAME/lava-particles-more.git
   cd lava-particles-more
   ```
2. Build the project using the Gradle wrapper:
   ```bash
   ./gradlew clean build -x test
   ```
3. Find the compiled `.jar` files in the build directory of each subproject:
   * `fabric_1_20_1/build/libs/`
   * `fabric_1_21_1/build/libs/`
   * `forge_1_20_1/build/libs/`
   * `forge_1_21_1/build/libs/`
   * `neoforge_1_20_1/build/libs/`
   * `neoforge_1_21_1/build/libs/`

---

## 📄 License

This project is licensed under the **GNU Lesser General Public License v3.0** - see the [LICENSE](LICENSE) file for details.
