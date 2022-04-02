package org.genericsutils;

import lombok.SneakyThrows;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * GenericsUtils provides tools for using Reflection with classes that contain generics.
 */
public class GenericsUtils {

    /**
     * Find the generic class for any class that implements a specified class with generics.
     * Example: ClassA extends ClassB<String, Integer>, a call to getGenericClassFromClass(ClassA.class, ClassB.class, 1) will return Integer.class
     *
     * @param fromClass
     * @param baseClassWithGeneric
     * @param genericPosition
     * @return
     */
    public static Class<?> getGenericClassFromClass(Class<?> fromClass, Class<?> baseClassWithGeneric, int genericPosition) {
        Type type = getGenericTypeFromClass(fromClass, baseClassWithGeneric, genericPosition);
        if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            if (!(rawType instanceof ParameterizedType)) {
                return getClass(rawType);
            } else {
                throw new RuntimeException("Placeholder until I can determine if this path is even possible.");
            }
        } else {
            return getClass(type);
        }
    }

    public static Type getGenericTypeFromClass(Class<?> fromClass, Class<?> baseClassWithGeneric, int genericPosition) {
        return getGenericTypeFromClass((Type) fromClass, baseClassWithGeneric, genericPosition);
    }

    public static Type getGenericTypeFromClass(Type fromType, Class<?> baseClassWithGeneric, int genericPosition) {
        Collection<Type> fromTypeImplements = implementsInterfaces(fromType);
        Collection<Type> fromTypeExtends = extendsClassesAndInterfaces(fromType);
        Collection<Type> allSuperTypes = Stream.concat(fromTypeImplements.stream(), fromTypeExtends.stream()).collect(Collectors.toList());
        Type typeFoundInAllSuperTypes = findClassIn(baseClassWithGeneric, allSuperTypes);
        if (typeFoundInAllSuperTypes != null) {
            if (typeFoundInAllSuperTypes instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) typeFoundInAllSuperTypes;
                return getGenericActualTypeArgument(parameterizedType, genericPosition);
            } else {
                throw new RuntimeException("Placeholder until I can determine if this path is even possible.");
            }
        }
        for (Type superType : allSuperTypes) {
            Type foundClass = getGenericTypeFromClass(superType, baseClassWithGeneric, genericPosition);
            if (foundClass != null) {
                return foundClass;
            }
        }
        return null;
    }

    public static Type findClassIn(Class<?> clazz, Collection<Type> types) {
        for (Type type : types) {
            if (classMatchesType(clazz, type)) {
                return type;
            }
        }
        return null;
    }

    private static boolean classMatchesType(Class<?> clazz, Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            if (clazz.equals(parameterizedType.getRawType())) {
                return true;
            }
        } else if (type instanceof Class<?>) {
            if (clazz.equals(type)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInterfaceClass(Class<?> clazz) {
        return clazz.isInterface();
    }

    public static boolean isInterfaceClass(Type type) {
        return isInterfaceClass((Class<?>) type);
    }

    public static boolean isAbstractClass(Class<?> clazz) {
        int mod = clazz.getModifiers();
        return (Modifier.isAbstract(mod) && !isInterfaceClass(clazz));
    }

    public static boolean isAbstractClass(Type type) {
        return isAbstractClass((Class<?>) type);
    }

    public static boolean isClass(Class<?> clazz) {
        return (!isInterfaceClass(clazz) && !isAbstractClass(clazz));
    }

    public static boolean isClass(Type type) {
        return isClass((Class<?>) type);
    }

    public static Collection<Type> extendsClassesAndInterfaces(Type type) {
        if (type instanceof ParameterizedType) {
            return extendsClassesAndInterfaces((ParameterizedType) type);
        } else {
            Class<?> clazz = getClass(type);
            return extendsClassesAndInterfaces(clazz);
        }
    }

    public static Collection<Type> extendsClassesAndInterfaces(Class<?> clazz) {
        if (clazz.isInterface()) {
            Type[] genericInterfaces = clazz.getGenericInterfaces();
            return Arrays.asList(genericInterfaces);
        } else {
            Type genericSuperclass = clazz.getGenericSuperclass();
            if ((genericSuperclass instanceof Class<?>) && ((Class<?>) genericSuperclass) == Object.class) {
                return new ArrayList<Type>();
            } else {
                return Collections.singleton(genericSuperclass);
            }
        }
    }

    public static Collection<Type> extendsClassesAndInterfaces(ParameterizedType parameterizedType) {
        return getImplementsAndExtends(parameterizedType);
    }

    public static Collection<Type> implementsInterfaces(Type type) {
        if (type instanceof ParameterizedType) {
            return implementsInterfaces((ParameterizedType) type);
        } else {
            Class<?> clazz = getClass(type);
            Type[] implementedInterfacesArray = clazz.getGenericInterfaces();
            return Arrays.asList(implementedInterfacesArray);
        }
    }

    public static Collection<Type> implementsInterfaces(Class<?> clazz) {
        return implementsInterfaces((Type) clazz);
    }

    public static Collection<Type> implementsInterfaces(ParameterizedType parameterizedType) {
        return getImplementsAndExtends(parameterizedType);
    }

    private static Collection<Type> getImplementsAndExtends(ParameterizedType parameterizedType) {
        Type rawType = parameterizedType.getRawType();
        if (!(rawType instanceof Class<?>)) {
            throw new RuntimeException("Unexpected RawType = " + rawType.getTypeName());
        }
        Class<?> rawTypeClass = (Class<?>) rawType;

        Map<String, Type> mapOfNamesAndTypes = extractGenericTypeVariableNameAndValueMap(parameterizedType);

        Type[] genericInterfaces = rawTypeClass.getGenericInterfaces();
        if (genericInterfaces.length == 0) {
            Class<?> superclass = rawTypeClass.getSuperclass();
            return Collections.singleton(superclass);
        }
        Type[] reconstructedGenericInterfaces = new Type[genericInterfaces.length];
        for (int i = 0; i <= genericInterfaces.length - 1; i++) {
            Type genericInterface = genericInterfaces[i];
            assert genericInterface instanceof ParameterizedType;
            ParameterizedType genericInterfaceParameterizedType = (ParameterizedType) genericInterface;
            ParameterizedType reconstructedGenericInterface = constructGenericInterface(genericInterfaceParameterizedType, mapOfNamesAndTypes);
            reconstructedGenericInterfaces[i] = reconstructedGenericInterface;
        }

        return Arrays.asList(reconstructedGenericInterfaces);
    }

    private static Map<String, Type> extractGenericTypeVariableNameAndValueMap(ParameterizedType parameterizedType) {
        Type rawType = parameterizedType.getRawType();
        if (!(rawType instanceof Class<?>)) {
            throw new RuntimeException("Unexpected RawType = " + rawType.getTypeName());
        }
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        Class<?> rawTypeClass = (Class<?>) rawType;
        Type[] typeParameters = rawTypeClass.getTypeParameters();
        if (actualTypeArguments.length != typeParameters.length) {
            throw new RuntimeException("Number of generic TypeParameters do not match.");
        }
        Map<String, Type> mapOfNamesAndTypes = new HashMap<>();
        for (int i = 0; i <= actualTypeArguments.length - 1; i++) {
            String typeVariableName = typeParameters[i].getTypeName();
            mapOfNamesAndTypes.put(typeVariableName, actualTypeArguments[i]);
        }

        return mapOfNamesAndTypes;
    }

    private static ParameterizedType constructGenericInterface(ParameterizedType genericInterface, Map<String, Type> mapOfNamesAndTypes) {
        Type[] actualTypeArguments = genericInterface.getActualTypeArguments();
        Type[] newActualTypeArguments = new Type[actualTypeArguments.length];
        for (int i = 0; i <= actualTypeArguments.length - 1; i++) {
            Type actualTypeArgument = actualTypeArguments[i];
            Type newActualTypeArgument = null;
            if (actualTypeArgument instanceof ParameterizedType) {
                // TODO This is a recursive reconstruction of the generic types, need to build more tests to see if the
                //  map of name and types needs to be reconstructed at each hop
                ParameterizedType parameterizedType = (ParameterizedType) actualTypeArgument;
                newActualTypeArgument = constructGenericInterface(parameterizedType, mapOfNamesAndTypes);
            } else if (actualTypeArgument instanceof TypeVariable) {
                TypeVariable<?> typeVariable = (TypeVariable<?>) actualTypeArgument;
                newActualTypeArgument = mapOfNamesAndTypes.get(typeVariable.getName());
            } else {
                throw new RuntimeException("Unexpected type of ActualTypeArgument " + actualTypeArgument.getTypeName());
            }
            newActualTypeArguments[i] = newActualTypeArgument;
        }
        ParameterizedType respone = ParameterizedTypeImpl.make((Class<?>) genericInterface.getRawType(), newActualTypeArguments, genericInterface.getOwnerType());
        return respone;
    }

    private static Object getGenericActualTypeArgument(ParameterizedType parameterizedType, String typeVariableName) {
        int genericPosition = getGenericTypeVariablePosition(parameterizedType, typeVariableName);
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        return actualTypeArguments[genericPosition];
    }

    private static Type getGenericActualTypeArgument(ParameterizedType parameterizedType, int genericPosition) {
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        if (genericPosition > actualTypeArguments.length - 1) {
            throw new RuntimeException("Generic Position " + genericPosition + " exceeds the number of generics in " + actualTypeArguments);
        }
        return actualTypeArguments[genericPosition];
    }

    private static String getGenericTypeVariableName(ParameterizedType parameterizedType, int typeVariablePosition) {
        Type rawType = parameterizedType.getRawType();
        Class<?> rawClass = (Class<?>) rawType;
        TypeVariable<? extends Class<?>>[] typeVariables = rawClass.getTypeParameters();
        return typeVariables[typeVariablePosition].getName();
    }

    private static int getGenericTypeVariablePosition(ParameterizedType parameterizedType, String typeVariableName) {
        Type rawType = parameterizedType.getRawType();
        Class<?> rawClass = (Class<?>) rawType;
        TypeVariable<? extends Class<?>>[] typeVariables = rawClass.getTypeParameters();
        for (int i = 0; i <= typeVariables.length - 1; i++) {
            TypeVariable<? extends Class<?>> typeVariable = typeVariables[i];
            if (typeVariable.getName().equals(typeVariableName)) {
                return i;
            }
        }
        throw new RuntimeException("TypeVariable name" + typeVariableName + " not found in Type " + parameterizedType.getTypeName());
    }

    @SneakyThrows
    private static Class<?> getClass(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else {
            return Class.forName(type.getTypeName());
        }
    }

}
