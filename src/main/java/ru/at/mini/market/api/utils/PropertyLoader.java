package ru.at.mini.market.api.utils;

import com.google.common.base.Strings;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class PropertyLoader {

    private static final String PROPERTIES_FILE = "/" + System.getProperty("properties", "application.properties");
    private static final Properties PROPERTIES = getPropertiesInstance();

    private PropertyLoader() {
    }

    /**
     * Возвращает значение свойства по имени.
     * Сначала поиск в системных свойствах, если ничего не найдено, поиск в property файле
     * Если ничего не найдено, кидает исключение
     *
     * @param propertyName  имя свойства
     * @return              значение системного свойства/property свойства
     *                      если ничего не найдено будет выброшено исключение
     */
    public static String loadProperty(String propertyName) {
        String value = tryLoadProperty(propertyName);
        if (value == null) {
            throw new IllegalArgumentException("В файле properties не найдено значение по ключу: " + propertyName);
        }
        return value;
    }

    /**
     * Возвращает значение свойства по имени.
     * Сначала поиск в системных свойствах, если ничего не найдено, поиск в property файле
     * Если ничего не найдено, возвращает значение по умолчанию
     *
     * @param propertyName  имя свойства
     * @param defaultValue  значение по умолчанию
     * @return              значение системного свойства/property свойства/значение по умолчанию
     */
    public static String loadProperty(String propertyName, String defaultValue) {
        String value = tryLoadProperty(propertyName);
        return value != null ? value : defaultValue;
    }

    /**
     * Возвращает значение свойства по имени.
     * Сначала поиск в системных свойствах, если ничего не найдено, поиск в property файле
     * В случае, если ничего не найдено, вернется null
     *
     * @param propertyName  имя свойства
     * @return              значение системного свойства/property свойства/null
     */
    public static String tryLoadProperty(String propertyName) {
        String value = null;
        if (!Strings.isNullOrEmpty(propertyName)) {
            String systemProperty = loadSystemPropertyOrDefault(propertyName, propertyName);
            if (!propertyName.equals(systemProperty)) return systemProperty;
            value = PROPERTIES.getProperty(propertyName);
        }
        return value;
    }

    /**
     * Возвращает значение системного свойства по имени.
     * В случае, если ничего не найдено, вернется значение по умолчанию
     *
     * @param propertyName  имя свойства
     * @param defaultValue  значение по умолчанию
     * @return              значение системного свойства/property свойства/значение по умолчанию
     */
    public static String loadSystemPropertyOrDefault(String propertyName, String defaultValue) {
        String propValue = System.getProperty(propertyName);
        return propValue != null ? propValue : defaultValue;
    }

    /**
     * Вспомогательный метод, возвращает объект класса Properties из файла {@link #PROPERTIES_FILE}
     *
     * @return              объект класса Properties из файла {@link #PROPERTIES_FILE}
     */
    private static Properties getPropertiesInstance() {
        Properties instance = new Properties();
        try (
            InputStream resourceStream = PropertyLoader.class.getResourceAsStream(PROPERTIES_FILE);
            InputStreamReader inputStream = new InputStreamReader(resourceStream, StandardCharsets.UTF_8)
        ) {
            instance.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return instance;
    }

}
