package sample.serialize;

import sample.ClassParser;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.stream.Collectors;

public class YamlSerializer implements Serializer {

    private String toYaml(Object object) {
        String answer = "!" + object.getClass().getName();
        Field[] fields = ClassParser.getAllFields(object.getClass());
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.getType().isPrimitive()) {
                    answer += "\r\n" + field.getName() + ": " + field.get(object);
                } else if (String.class.equals(field.getType())) {
                    answer += "\r\n" + field.getName() + ": \"" + field.get(object) + "\"";
                } else if (field.getType().isEnum()) {
                    answer += "\r\n" + field.getName() + ":" +
                            "\r\n\t" + "!" + field.getType().getName() +
                            "\r\n\t" + "value: " + field.get(object);
                } else if (!field.getType().isPrimitive()) {
                    if (field.get(object) != null) {
                        String subanswer = toYaml(field.get(object));
                        answer += "\r\n" + field.getName() + ": \r\n\t" +
                                subanswer.replaceAll("\r\n", "\r\n\t");
                    } else {
                        answer += "\r\n" + field.getName() + ": null";
                    }
                }
            }
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return answer;
    }

    @Override
    public void serialize(Object[] objects, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(Arrays.stream(objects)
                    .map(this::toYaml).collect(Collectors.joining("\n---\n")));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Object parsePrimitive(String value, Class fieldType) {
        if (byte.class.equals(fieldType)) {
            return Byte.parseByte(value);
        } else if (short.class.equals(fieldType)) {
            return Short.parseShort(value);
        } else if (int.class.equals(fieldType)) {
            return Integer.parseInt(value);
        } else if (long.class.equals(fieldType)) {
            return Long.parseLong(value);
        } else if (float.class.equals(fieldType)) {
            return Float.parseFloat(value);
        } else if (double.class.equals(fieldType)) {
            return Double.parseDouble(value);
        } else if (char.class.equals(fieldType)) {
            return value.charAt(0);
        } else if (boolean.class.equals(fieldType)) {
            return Boolean.parseBoolean(value);
        } else {
            return null;
        }
    }

    private Object fromYaml(ArrayDeque<String> lines) {
        try {
            String className = lines.poll().substring(1);
            Class c = Class.forName(className);
            if (c.isEnum()) {
                return Enum.valueOf(c, lines.poll().split(" ")[1]);
            }
            Object[] params = new Object[ClassParser.getFieldsCount(c)];
            int i = 0;
            while (!lines.isEmpty()) {
                String field = lines.poll();
                if (field.split(" ").length == 1) {
                    ArrayDeque<String> sublines = new ArrayDeque<>();
                    while ((lines.peek() != null) && lines.peek().startsWith("\t")) {
                        sublines.add(lines.poll().substring(1));
                    }
                    params[i] = fromYaml(sublines);
                } else if (field.split(" ")[1].equals("null")) {
                    params[i] = null;
                } else if (field.split(" ")[1].startsWith("\"")) {
                    String argument = field.split(" ")[1];
                    params[i] = argument.substring(1, argument.length() - 1);
                } else {
                    String value = field.split(" ")[1];
                    Class fieldType = ClassParser.getAllFields(c)[i].getType();
                    params[i] = parsePrimitive(value, fieldType);
                }
                i++;
            }
            return ClassParser.getFullConstructor(c).newInstance(params);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                InvocationTargetException | IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object[] deserialize(File file) {
        try (FileReader reader = new FileReader(file)) {
            String[] strings = new BufferedReader(reader)
                    .lines()
                    .collect(Collectors.joining("\n"))
                    .split("\n---\n");
            return Arrays.stream(strings)
                    .map(string -> fromYaml(new ArrayDeque<>(Arrays.asList(string.split("\n")))))
                    .toArray(Object[]::new);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
