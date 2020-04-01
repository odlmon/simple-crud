package sample;

import org.reflections.Reflections;
import sample.attribute.Size;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.stream.Stream;

public class ClassParser {

    private static boolean isFull(Constructor constructor, Class[] forCheck) {
        return Arrays.equals(constructor.getParameterTypes(), forCheck);
    }

    public static Field[] getAllFields(Class c) {
        return Stream.concat(Arrays.stream(c.getSuperclass().getDeclaredFields()),
                Arrays.stream(c.getDeclaredFields())).toArray(Field[]::new);
    }

    public static Constructor getFullConstructor(Class c) {
        Constructor fullConstructor = null;
        Class[] forCheck = Arrays.stream(getAllFields(c)).map(Field::getType).toArray(Class[]::new);
        for (Constructor constructor : c.getConstructors()) {
            if (isFull(constructor, forCheck)) {
                fullConstructor = constructor;
                break;
            }
        }
        return fullConstructor;
    }

    public static String[] getAllClassesInPackage(String packageName) {
        return new Reflections("sample")
                .getTypesAnnotatedWith(Entity.class)
                .stream()
                .map(Class::getName)
                .toArray(String[]::new);
    }

    public static String[] getAllEnumValues(Class c) {
        try {
            return Arrays.stream((Object[]) c.getDeclaredMethod("values").invoke(null))
                    .map(Object::toString)
                    .toArray(String[]::new);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.err.println("Error to find or get access to values() method for Enum " + e);
            return null;
        }
    }
}