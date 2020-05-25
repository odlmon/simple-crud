package sample;

import org.reflections.Reflections;
import sample.annotation.Entity;

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
        Stream<Field> stream = Stream.empty();
        stream = Stream.concat(Arrays.stream(c.getDeclaredFields()), stream);
        Class superClass = c;
        while (!(superClass = superClass.getSuperclass()).equals(Object.class)) {
            stream = Stream.concat(Arrays.stream(superClass.getDeclaredFields()), stream);
        }
        return stream.toArray(Field[]::new);
    }

    public static Class[] getAllTypesOfFields(Class c) {
        return Arrays.stream(getAllFields(c)).map(Field::getType).toArray(Class[]::new);
    }

    public static Constructor getFullConstructor(Class c) {
        Class[] forCheck = Arrays.stream(getAllFields(c)).map(Field::getType).toArray(Class[]::new);
        return Arrays.stream(c.getConstructors()).filter(constructor -> isFull(constructor,forCheck)).findFirst().get();
    }

    public static String[] getAllClassesInPackage(String packageName) {
        return new Reflections(packageName)
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

    public static int getFieldsCount(Class c) {
        return getAllFields(c).length;
    }
}
