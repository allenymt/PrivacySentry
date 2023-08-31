package com.yl.lib.privacysentry;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Scanner;

/**
 * @author yulun
 * @since 2023-08-29 18:04
 */
public class ReflexObjectUtil {

    public static void test(String className) {
        try {
            Class cl = Class.forName(className);
            Class supercl = cl.getSuperclass();
            String modifiers = Modifier.toString(cl.getModifiers());
            if (modifiers.length() > 0) System.out.print(modifiers + " ");
            System.out.print("class " + className);
            if (supercl != null && supercl != Object.class) {
                System.out.print("extends " + supercl.getName());
            }
            System.out.print("\n{\n");

            printFields(cl);
            System.out.println();
            printConstructors(cl);
            System.out.println();
            printMethods(cl);
            System.out.print("}\n");

        } catch (Exception e) {

            // TODO: handle exception
        }
    }

    public static void printConstructors(Class cl) {
        Constructor[] constructors = cl.getConstructors();
        for (Constructor c : constructors) {
            System.out.print("  ");
            String name = c.getName();
            String modifiers = Modifier.toString(c.getModifiers());
            if (modifiers.length() > 0) System.out.print(modifiers + " ");
            System.out.print(name + " (");
            Class[] paramType = c.getParameterTypes();
            for (int j = 0; j < paramType.length; j++) {
                if (j > 0) System.out.print(", ");
                System.out.print(paramType[j].getName());
            }
            System.out.print(")");
        }
        System.out.println();
    }

    public static void printMethods(Class cl) {
        Method[] methods = cl.getDeclaredMethods();
        for (Method m : methods) {
            System.out.print("  ");
            String name = m.getName();
            String modifiers = Modifier.toString(m.getModifiers());
            if (modifiers.length() > 0) System.out.print(modifiers + " ");
            Class returnType = m.getReturnType();
            System.out.print(returnType.getName() + " ");
            System.out.print(name + " (");
            Class[] paramType = m.getParameterTypes();
            for (int j = 0; j < paramType.length; j++) {
                if (j > 0) System.out.print(", ");
                System.out.print(paramType[j].getName());
            }
            System.out.print(")");
            System.out.println();
        }
        System.out.println();
    }

    public static void printFields(Class cl) {
        Field[] fields = cl.getDeclaredFields();
        for (Field f : fields) {
            System.out.print("  ");
            String name = f.getName();
            Class type = f.getType();
            String modifiers = Modifier.toString(f.getModifiers());
            if (modifiers.length() > 0) System.out.print(modifiers + " ");
            System.out.println(type.getName() + " " + name + ";");
        }
    }

}
