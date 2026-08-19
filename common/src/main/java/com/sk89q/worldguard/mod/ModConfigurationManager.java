package com.sk89q.worldguard.mod;

import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.config.WorldConfiguration;
import com.sk89q.worldguard.config.YamlConfigurationManager;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Antes de la auditoría, esta clase no leía ningún archivo: extendía la ConfigurationManager
 * abstracta directamente y solo montaba el driver de regiones, así que config.yml no existía
 * ni tenía ningún efecto (hallazgo C2). worldguard-core ya trae toda la lógica de lectura de
 * YAML lista para reusar en YamlConfigurationManager (la misma clase de la que cuelga
 * BukkitConfigurationManager en el plugin original): solo hace falta la plataforma para decir
 * dónde vive la carpeta de datos y cómo craar el config.yml si falta.
 */
public class ModConfigurationManager extends YamlConfigurationManager {
    private final Map<String, ModWorldConfiguration> worldConfigs = new ConcurrentHashMap<>();

    @Override
    public File getDataFolder() {
        return new File("config/worldguard");
    }

    @Override
    public void copyDefaults() {
        ensureFileExists(new File(getDataFolder(), "config.yml"));
    }

    @Override
    public void unload() {
        worldConfigs.clear();
    }

    @Override
    public WorldConfiguration get(World world) {
        return worldConfigs.computeIfAbsent(world.getName(),
            name -> new ModWorldConfiguration(name, getDataFolder(), getConfig()));
    }

    @Override
    public void disableUuidMigration() {
        // No-op: un port nuevo no tiene datos heredados de nombres-sin-UUID que migrar.
    }

    /**
     * Crea un archivo vacío si no existe (el equivalente mínimo a
     * JavaPlugin#createDefaultConfiguration de Bukkit, que en el plugin original copia una
     * plantilla comentada empaquetada en el jar). Este port no reempaqueta la plantilla
     * original de EngineHub, así que el archivo arranca vacío; cada opción sigue funcionando
     * con su valor por defecto (documentado en la documentación pública de WorldGuard, ya que
     * las claves YAML son las mismas) hasta que el administrador la agregue a mano.
     */
    static void ensureFileExists(File file) {
        if (file.exists()) {
            return;
        }
        try {
            File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }
            file.createNewFile();
        } catch (IOException e) {
            org.apache.logging.log4j.LogManager.getLogger("WorldGuard")
                .error("No se pudo crear el archivo de configuración " + file, e);
        }
    }
}
