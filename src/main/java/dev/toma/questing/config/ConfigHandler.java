package dev.toma.questing.config;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConfigHandler {

    private static final Map<String, Holder<?>> CONFIG_HOLDERS = new HashMap<>();

    public static <C> C loadConfig(Class<C> config) {
        try {
            C c = config.getDeclaredConstructor().newInstance();
            ConfigFile file = config.getAnnotation(ConfigFile.class);
            if (file == null) {
                throw new IllegalArgumentException("Cannot process config file without '@ConfigFile' annotation");
            }
            String id = file.value();
            Holder<C> holder = new Holder<>(id, c);
            CONFIG_HOLDERS.put(id, holder);
            IO.process(holder);
            return c;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    static class IO {

        static void process(Holder<?> holder) {
            File file = getFileForHolder(holder);
            try {
                if (!file.exists()) {
                    write(holder);
                } else {
                    read(holder);
                    write(holder);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        static void read(Holder<?> holder) throws IOException {
            File configFile = getFileForHolder(holder);
            Map<String, String> parsedValues = new HashMap<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] strings = line.split("=", 2);
                    String key = strings[0];
                    String value = strings[1];
                    parsedValues.put(key, value);
                }
            }
            try {
                Object config = holder.cfg;
                Class<?> cfgType = config.getClass();
                Field[] fields = cfgType.getDeclaredFields();
                for (Field field : fields) {
                    ConfigFile.Value value = field.getAnnotation(ConfigFile.Value.class);
                    if (value == null)
                        continue;
                    String id = value.value();
                    String parsedValue = parsedValues.get(id);
                    Class<?> valueType = field.getType();
                    TypeAdapter adapter = TypeAdapters.getAdapter(valueType);
                    if (adapter == null || parsedValue == null)
                        continue;
                    field.setAccessible(true);
                    Object data = adapter.getValueFromString(parsedValue);
                    field.set(config, data);
                }
            } catch (IllegalAccessException e) {

            }
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        static void write(Holder<?> holder) throws IOException {
            File configFile = getFileForHolder(holder);
            File dir = configFile.getParentFile();
            dir.mkdirs();
            if (!configFile.exists()) {
                configFile.createNewFile();
            }
            Class<?> type = holder.cfg.getClass();
            Field[] fields = type.getDeclaredFields();
            StringBuilder export = new StringBuilder();
            try {
                for (Field field : fields) {
                    ConfigFile.Value value = field.getAnnotation(ConfigFile.Value.class);
                    if (value == null)
                        continue;
                    String id = value.value();
                    Class<?> valueType = field.getType();
                    TypeAdapter adapter = TypeAdapters.getAdapter(valueType);
                    if (adapter == null)
                        continue;
                    field.setAccessible(true);
                    String string = adapter.getValueFromField(field, holder.cfg);
                    export.append(id).append("=").append(string).append("\n");
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            try (FileWriter writer = new FileWriter(configFile)) {
                writer.write(export.toString());
            }
        }

        static File getFileForHolder(Holder<?> holder) {
            return new File("./config/" + holder.id + ".properties");
        }
    }

    static class TypeAdapters {

        static final Set<Matcher> MATCHERS = new HashSet<>();

        static TypeAdapter getAdapter(Class<?> type) {
            for (Matcher matcher : MATCHERS) {
                TypeAdapter adapter = matcher.match(type);
                if (adapter != null) {
                    return adapter;
                }
            }
            return null;
        }

        static {
            MATCHERS.add(type -> type.equals(Integer.TYPE) ? Integer::parseInt : null);
            MATCHERS.add(type -> type.equals(String.class) ? text -> text : null);
        }
    }

    interface TypeAdapter {

        default String getValueFromField(Field field, Object config) throws IllegalAccessException {
            return field.get(config).toString();
        }

        Object getValueFromString(String raw);
    }

    @FunctionalInterface
    interface Matcher {
        TypeAdapter match(Class<?> type);
    }

    static class Holder<T> {

        final String id;
        final T cfg;

        Holder(String id, T cfg) {
            this.id = id;
            this.cfg = cfg;
        }
    }
}
