package com.sk89q.worldguard.mod;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Localización de los mensajes que el mod envía por chat (no la de worldguard-core, que
 * reporta sus propios errores en el idioma que trae la librería). Soporta los idiomas
 * pedidos: inglés, portugués y español con variantes por región (España, México, Chile,
 * Argentina). El idioma se detecta a partir de {@link ServerPlayer#clientInformation()},
 * que expone el código de idioma que el propio cliente de Minecraft ya tiene configurado
 * (p. ej. "es_ar", "en_us", "pt_br") — no requiere que el jugador configure nada aparte.
 */
public final class WorldGuardLang {
    private static final Logger LOGGER = LogManager.getLogger("WorldGuard");
    private static final String DEFAULT_LOCALE = "es_es";
    private static final String[] SUPPORTED_LOCALES = {"en_us", "es_es", "es_mx", "es_cl", "es_ar", "pt_br"};

    private static final Map<String, Map<String, String>> BUNDLES = new HashMap<>();

    static {
        Gson gson = new Gson();
        java.lang.reflect.Type mapType = new TypeToken<Map<String, String>>() {}.getType();
        for (String locale : SUPPORTED_LOCALES) {
            String path = "/lang/" + locale + ".json";
            try (InputStream in = WorldGuardLang.class.getResourceAsStream(path)) {
                if (in == null) {
                    LOGGER.warn("No se encontró el archivo de idioma " + path);
                    BUNDLES.put(locale, Collections.emptyMap());
                    continue;
                }
                Map<String, String> bundle = gson.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), mapType);
                BUNDLES.put(locale, bundle != null ? bundle : Collections.emptyMap());
            } catch (Exception e) {
                LOGGER.error("Error cargando el archivo de idioma " + path, e);
                BUNDLES.put(locale, Collections.emptyMap());
            }
        }
    }

    private WorldGuardLang() {
    }

    /**
     * Devuelve el mensaje localizado para "key" según el idioma del cliente de {@code player}
     * (o el idioma por defecto si {@code player} es null, p. ej. para la consola), aplicando
     * {@link String#format} con {@code args} si se pasaron. Si la clave no existe en el idioma
     * detectado, cae al español (idioma histórico del port) y, si tampoco existe ahí, devuelve
     * la propia clave para que el fallo sea visible en vez de silencioso.
     */
    public static String get(ServerPlayer player, String key, Object... args) {
        String locale = normalizeLocale(player);
        String template = BUNDLES.getOrDefault(locale, Collections.emptyMap()).get(key);
        if (template == null) {
            template = BUNDLES.getOrDefault(DEFAULT_LOCALE, Collections.emptyMap()).get(key);
        }
        if (template == null) {
            return key;
        }
        return args.length > 0 ? String.format(template, args) : template;
    }

    private static String normalizeLocale(ServerPlayer player) {
        String raw = null;
        if (player != null) {
            try {
                raw = player.clientInformation().language();
            } catch (Exception ignored) {
                // Muy temprano en la conexión, clientInformation podría no estar listo aún.
            }
        }
        if (raw == null || raw.isEmpty()) {
            return DEFAULT_LOCALE;
        }
        raw = raw.toLowerCase(java.util.Locale.ROOT).replace('-', '_');
        if (raw.startsWith("en")) return "en_us";
        if (raw.startsWith("pt")) return "pt_br";
        if (raw.equals("es_mx")) return "es_mx";
        if (raw.equals("es_cl")) return "es_cl";
        if (raw.equals("es_ar")) return "es_ar";
        if (raw.startsWith("es")) return "es_es";
        return DEFAULT_LOCALE;
    }
}
