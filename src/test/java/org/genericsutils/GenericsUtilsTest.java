package org.genericsutils;

import org.junit.jupiter.api.Test;
import sun.reflect.generics.reflectiveObjects.TypeVariableImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class GenericsUtilsTest {

    @Test
    void isInterfaceClass() {
        assertTrue(GenericsUtils.isInterfaceClass(IClass1.class));
        assertTrue(GenericsUtils.isInterfaceClass(IClass2.class));
        assertTrue(GenericsUtils.isInterfaceClass(IClass3.class));
        assertFalse(GenericsUtils.isInterfaceClass(AbstractClass2.class));
        assertFalse(GenericsUtils.isInterfaceClass(Class2String.class));
        assertFalse(GenericsUtils.isInterfaceClass(AbstractClass3.class));
        assertFalse(GenericsUtils.isInterfaceClass(Class3StringInteger.class));
        assertFalse(GenericsUtils.isInterfaceClass(Class2StringExtended.class));
        assertFalse(GenericsUtils.isInterfaceClass(Class2StringExtendedAgainWithInteger.class));
        assertFalse(GenericsUtils.isInterfaceClass(ExtenededClass2StringExtendedAgainWithInteger.class));
    }

    @Test
    void isAbstractClass() {
        assertFalse(GenericsUtils.isAbstractClass(IClass1.class));
        assertFalse(GenericsUtils.isAbstractClass(IClass2.class));
        assertFalse(GenericsUtils.isAbstractClass(IClass3.class));
        assertTrue(GenericsUtils.isAbstractClass(AbstractClass2.class));
        assertFalse(GenericsUtils.isAbstractClass(Class2String.class));
        assertTrue(GenericsUtils.isAbstractClass(AbstractClass3.class));
        assertFalse(GenericsUtils.isAbstractClass(Class3StringInteger.class));
        assertFalse(GenericsUtils.isAbstractClass(Class2StringExtended.class));
        assertFalse(GenericsUtils.isAbstractClass(Class2StringExtendedAgainWithInteger.class));
        assertFalse(GenericsUtils.isAbstractClass(ExtenededClass2StringExtendedAgainWithInteger.class));
    }

    @Test
    void isClass() {
        assertFalse(GenericsUtils.isClass(IClass1.class));
        assertFalse(GenericsUtils.isClass(IClass2.class));
        assertFalse(GenericsUtils.isClass(IClass3.class));
        assertFalse(GenericsUtils.isClass(AbstractClass2.class));
        assertTrue(GenericsUtils.isClass(Class2String.class));
        assertFalse(GenericsUtils.isClass(AbstractClass3.class));
        assertTrue(GenericsUtils.isClass(Class3StringInteger.class));
        assertTrue(GenericsUtils.isClass(Class2StringExtended.class));
        assertTrue(GenericsUtils.isClass(Class2StringExtendedAgainWithInteger.class));
        assertTrue(GenericsUtils.isClass(ExtenededClass2StringExtendedAgainWithInteger.class));
    }

    @Test
    void implementsClasses() throws Exception {
        Collection<Type> class1Interfaces = GenericsUtils.implementsInterfaces(Class1.class);
        assertEquals(1, class1Interfaces.size());
        assertTrue(class1Interfaces.contains(IClass1.class));

        Collection<Type> class2StringInterfaces = GenericsUtils.implementsInterfaces(Class2String.class);
        assertEquals(0, class2StringInterfaces.size());

        Collection<Type> extendsClass2StringInterfaces = GenericsUtils.implementsInterfaces(ExtendsClass2String.class);
        assertEquals(0, extendsClass2StringInterfaces.size());

        Type abstractClass2ParameterizedType = Class2String.class.getGenericSuperclass();
        Collection<Type> abstractClass2StringInterfaces = GenericsUtils.implementsInterfaces(abstractClass2ParameterizedType);
        assertEquals(1, abstractClass2StringInterfaces.size());
        Collection<Class<?>> abstractClass2StringInterfacesRawClasses = abstractClass2StringInterfaces.stream().map(type -> (Class<?>)((ParameterizedType)type).getRawType()).collect(Collectors.toList());
        assertTrue(abstractClass2StringInterfacesRawClasses.contains(IClass2.class));

        Collection<Type> iClassABParameterizedTypes = GenericsUtils.implementsInterfaces(ClassABStringInteger.class);
        assert (iClassABParameterizedTypes.size()==1);
        Type iClassABParameterizedType = iClassABParameterizedTypes.stream().findAny().get();
        Collection<Type> iClassABInterfaces = GenericsUtils.implementsInterfaces(iClassABParameterizedType);
        assertEquals(2, iClassABInterfaces.size());
        Collection<Class<?>> iClassABInterfacesRawClasses = iClassABInterfaces.stream().map(type -> (Class<?>)((ParameterizedType)type).getRawType()).collect(Collectors.toList());
        assertTrue(iClassABInterfacesRawClasses.contains(IClassA.class));
        assertTrue(iClassABInterfacesRawClasses.contains(IClassB.class));

        Collection<Type> iClassSTUPParameterizedType = GenericsUtils.implementsInterfaces(ClassSTU.class);
        Collection<Type> iClassSTUPInterfaces = GenericsUtils.implementsInterfaces(iClassSTUPParameterizedType.stream().findFirst().get());
        Map<Class<?>, Collection<Class<?>>> genericInterfacesClasses = extractGenericInterfacesClasses(iClassSTUPInterfaces);
        assertEquals(2, genericInterfacesClasses.keySet().size());
        assertTrue(genericInterfacesClasses.keySet().contains(IClassZ.class));
        assertTrue(genericInterfacesClasses.get(IClassZ.class).contains(Integer.class));
        assertTrue(genericInterfacesClasses.keySet().contains(IClassVW.class));
        assertTrue(genericInterfacesClasses.get(IClassVW.class).contains(String.class));
        assertTrue(genericInterfacesClasses.get(IClassVW.class).contains(Boolean.class));

        Type abstractClass3ParameterizedType = Class3StringInteger.class.getGenericSuperclass();
        Collection<Type> abstractClass3Interfaces = GenericsUtils.implementsInterfaces(abstractClass3ParameterizedType);
        Map<Class<?>, Collection<Class<?>>> abstractClass3InterfacesClasses = extractGenericInterfacesClasses(abstractClass3Interfaces);
        assertEquals(1, abstractClass3InterfacesClasses.keySet().size());
        assertTrue(abstractClass3InterfacesClasses.containsKey(IClass3.class));
        assertTrue(abstractClass3InterfacesClasses.get(IClass3.class).contains(String.class));
        assertTrue(abstractClass3InterfacesClasses.get(IClass3.class).contains(Integer.class));

        Collection<Type> implementsAndExtendsInterfaces = GenericsUtils.implementsInterfaces(ImplementsAndExtends.class);
        assertEquals(2, implementsAndExtendsInterfaces.size());
        assertTrue(implementsAndExtendsInterfaces.contains(IClass1A.class));
        assertTrue(implementsAndExtendsInterfaces.contains(IClass1B.class));

        Collection<Type> implementsAndExtends3WithString = GenericsUtils.implementsInterfaces(ImplementsAndExtends3WithString.class);
        assertEquals(2, implementsAndExtends3WithString.size());
        assertTrue(implementsAndExtends3WithString.contains(IClass1A.class));
        assertTrue(implementsAndExtends3WithString.contains(IClass1B.class));

    }
    @Test
    public void extendsClassesAndInterfaces() throws Exception {
        Collection<Type> implementsAndExtends3WithString = GenericsUtils.extendsClassesAndInterfaces(ImplementsAndExtends3WithString.class);
        Map<Class<?>, Collection<Class<?>>> implementsAndExtends3WithStringExtendedClasses = extractGenericInterfacesClasses(implementsAndExtends3WithString);
        assertEquals(1, implementsAndExtends3WithStringExtendedClasses.keySet().size());
        assertTrue(implementsAndExtends3WithStringExtendedClasses.containsKey(AbstractClass2.class));
        assertTrue(implementsAndExtends3WithStringExtendedClasses.get(AbstractClass2.class).contains(String.class));

        Collection<Type> iClass3ExtendedInterfaces = GenericsUtils.extendsClassesAndInterfaces(IClass3.class);
        assertEquals(1, iClass3ExtendedInterfaces.size());
        Type iClass3ExtendedInterfacesType = iClass3ExtendedInterfaces.stream().findFirst().get();
        assertTrue(iClass3ExtendedInterfacesType instanceof ParameterizedType);
        Type iClass3ExtendedInterfacesRawType = ((ParameterizedType)iClass3ExtendedInterfacesType).getRawType();
        assertEquals(IClass2.class, iClass3ExtendedInterfacesRawType);
        Type[] iClass3ExtendedInterfacesActualTypeArguments = ((ParameterizedType)iClass3ExtendedInterfacesType).getActualTypeArguments();
        assertEquals(1, iClass3ExtendedInterfacesActualTypeArguments.length);
        assertEquals("A", iClass3ExtendedInterfacesActualTypeArguments[0].getTypeName());

        Collection<Type> iClass3StringIntegerInterfaces = GenericsUtils.extendsClassesAndInterfaces(IClass3StringInteger.class);
        Map<Class<?>, Collection<Class<?>>> iClass3StringIntegerInterfacesClasses = extractGenericInterfacesClasses(iClass3StringIntegerInterfaces);
        assertEquals(1, iClass3StringIntegerInterfacesClasses.keySet().size());
        assertTrue(iClass3StringIntegerInterfacesClasses.containsKey(IClass3.class));
        assertEquals(2, iClass3StringIntegerInterfacesClasses.get(IClass3.class).size());
        assertTrue(iClass3StringIntegerInterfacesClasses.get(IClass3.class).contains(String.class));
        assertTrue(iClass3StringIntegerInterfacesClasses.get(IClass3.class).contains(Integer.class));

        Collection<Type> class2StringExtendedAgainWithIntegerInterfaces = GenericsUtils.extendsClassesAndInterfaces(Class2StringExtendedAgainWithInteger.class);
        Map<Class<?>, Collection<Class<?>>> class2StringExtendedAgainWithIntegerInterfacesClasses = extractGenericInterfacesClasses(class2StringExtendedAgainWithIntegerInterfaces);
        assertEquals(1, class2StringExtendedAgainWithIntegerInterfacesClasses.keySet().size());
        assertTrue(class2StringExtendedAgainWithIntegerInterfacesClasses.containsKey(Class2StringExtended.class));
        assertEquals(1, class2StringExtendedAgainWithIntegerInterfacesClasses.get(Class2StringExtended.class).size());
        assertTrue(class2StringExtendedAgainWithIntegerInterfacesClasses.get(Class2StringExtended.class).contains(Integer.class));

        Collection<Type> extendsAbstractClass2WithStringInterfaces = GenericsUtils.extendsClassesAndInterfaces(ExtendsAbstractClass2WithString.class);
        Map<Class<?>, Collection<Class<?>>> extendsAbstractClass2WithStringInterfacesClasses = extractGenericInterfacesClasses(extendsAbstractClass2WithStringInterfaces);
        assertEquals(1, extendsAbstractClass2WithStringInterfacesClasses.keySet().size());
        assertTrue(extendsAbstractClass2WithStringInterfacesClasses.containsKey(AbstractClass2.class));
        assertEquals(1, extendsAbstractClass2WithStringInterfacesClasses.get(AbstractClass2.class).size());
        assertTrue(extendsAbstractClass2WithStringInterfacesClasses.get(AbstractClass2.class).contains(String.class));

        Collection<Type> classSTUExtends = GenericsUtils.extendsClassesAndInterfaces(ClassSTU.class);
        assertEquals(0, classSTUExtends.size());

        Collection<Type> class2StringExtends = GenericsUtils.extendsClassesAndInterfaces(Class2String.class);
        Map<Class<?>, Collection<Class<?>>> class2StringExtendsClasses = extractGenericInterfacesClasses(class2StringExtends);
        assertEquals(1, class2StringExtendsClasses.keySet().size());
        assertTrue(class2StringExtendsClasses.containsKey(AbstractClass2.class));
        assertEquals(1, class2StringExtendsClasses.get(AbstractClass2.class).size());
        assertTrue(class2StringExtendsClasses.get(AbstractClass2.class).contains(String.class));

        Collection<Type> extendsAbstractClass2WithStringExtends = GenericsUtils.extendsClassesAndInterfaces(ExtendsAbstractClass2WithString.class);
        Map<Class<?>, Collection<Class<?>>> extendsAbstractClass2WithStringExtendsClasses = extractGenericInterfacesClasses(extendsAbstractClass2WithStringExtends);
        assertEquals(1, extendsAbstractClass2WithStringExtendsClasses.keySet().size());
        assertTrue(extendsAbstractClass2WithStringExtendsClasses.containsKey(AbstractClass2.class));
        assertEquals(1, extendsAbstractClass2WithStringExtendsClasses.get(AbstractClass2.class).size());
        assertTrue(extendsAbstractClass2WithStringExtendsClasses.get(AbstractClass2.class).contains(String.class));

        Collection<Type> iClassSTUExtends = GenericsUtils.extendsClassesAndInterfaces(IClassSTU.class);
        assertEquals(2, iClassSTUExtends.size());
        Type[] iClassSTUExtendsTypes = (Type[])iClassSTUExtends.toArray();
        ParameterizedType iClassSTUExtendsType0 = (ParameterizedType)iClassSTUExtendsTypes[0];
        ParameterizedType iClassSTUExtendsType1 = (ParameterizedType)iClassSTUExtendsTypes[1];
        assertEquals(IClassZ.class, iClassSTUExtendsType0.getRawType());
        assertEquals(IClassVW.class, iClassSTUExtendsType1.getRawType());
        Type[] iClassSTUExtendsType0ActualTypeArguments = iClassSTUExtendsType0.getActualTypeArguments();
        Type[] iClassSTUExtendsType1ActualTypeArguments = iClassSTUExtendsType1.getActualTypeArguments();
        assertEquals(1, iClassSTUExtendsType0ActualTypeArguments.length);
        assertEquals("T", iClassSTUExtendsType0ActualTypeArguments[0].getTypeName());
        assertEquals(2, iClassSTUExtendsType1ActualTypeArguments.length);
        assertEquals("U", iClassSTUExtendsType1ActualTypeArguments[0].getTypeName());
        assertEquals("S", iClassSTUExtendsType1ActualTypeArguments[1].getTypeName());

        Type class2StringExtendedAgainWithIntegerParameterizedType = Class2StringExtendedAgainWithInteger.class.getGenericSuperclass();
        Collection<Type> class2StringExtendedAgainWithIntegerParameterizedTypeExtends = GenericsUtils.extendsClassesAndInterfaces(class2StringExtendedAgainWithIntegerParameterizedType);
        assertEquals(1, class2StringExtendedAgainWithIntegerParameterizedTypeExtends.size());
        assertEquals(Class2String.class, class2StringExtendedAgainWithIntegerParameterizedTypeExtends.stream().findFirst().get());

    }

    @Test
    void getGenericClassFromClass() {
        Type class2StringExtendedAgainWithInteger = GenericsUtils.getGenericTypeFromClass(Class2StringExtendedAgainWithInteger.class, Class2StringExtended.class, 0);
        assertEquals(Integer.class, class2StringExtendedAgainWithInteger);

        Type class2StringExtendedAgainWithInteger2 = GenericsUtils.getGenericTypeFromClass(Class2StringExtendedAgainWithInteger.class, AbstractClass2.class, 0);
        assertEquals(String.class, class2StringExtendedAgainWithInteger2);

        Type class2String = GenericsUtils.getGenericTypeFromClass(Class2String.class, IClass2.class, 0);
        assertEquals(String.class, class2String);

        Type class2StringExtendedAgainWithInteger3 = GenericsUtils.getGenericTypeFromClass(Class2StringExtendedAgainWithInteger.class, IClass2.class, 0);
        assertEquals(String.class, class2StringExtendedAgainWithInteger3);

        Type extenededClass2StringExtendedAgainWithInteger = GenericsUtils.getGenericTypeFromClass(ExtenededClass2StringExtendedAgainWithInteger.class, IClass2.class, 0);
        assertEquals(String.class, extenededClass2StringExtendedAgainWithInteger);

        Type class3StringInteger = GenericsUtils.getGenericTypeFromClass(Class3StringInteger.class, IClass2.class, 0);
        assertEquals(String.class, class3StringInteger);

        Type findClassFInClassG = GenericsUtils.getGenericTypeFromClass(ClassG.class, IClassF.class, 0);
        assertTrue(findClassFInClassG instanceof ParameterizedType);
        ParameterizedType findClassFInClassGParameterizedType = (ParameterizedType) findClassFInClassG;
        assertEquals(IClassE.class, findClassFInClassGParameterizedType.getRawType());
        Type[] findClassFInClassGActualTypeArguments = findClassFInClassGParameterizedType.getActualTypeArguments();
        assertEquals(1, findClassFInClassGActualTypeArguments.length);
        assertEquals(Boolean.class.getTypeName(), findClassFInClassGActualTypeArguments[0].getTypeName());

        Type findGenericClassEInClassH = GenericsUtils.getGenericTypeFromClass(ClassH.class, IClassE.class, 0);
        assertEquals(String.class, findGenericClassEInClassH);

        Type findGenericInClassGInIClassH = GenericsUtils.getGenericTypeFromClass(ClassH.class, IClassH.class, 1);
        assertEquals(Integer.class, findGenericInClassGInIClassH);

    }


    private static Map<Class<?>, Collection<Class<?>>> extractGenericInterfacesClasses(Collection<Type> implementedInterfaces) throws Exception {
        Map<Class<?>, Collection<Class<?>>> response = new HashMap<>();
        for (Type implementedInterface : implementedInterfaces) {
            if (implementedInterface instanceof ParameterizedType) {
                ParameterizedType implementedInterfaceParameterizedType = (ParameterizedType) implementedInterface;
                Type implementedInterfaceRawType = implementedInterfaceParameterizedType.getRawType();
                Class<?> implementedInterfaceClass = (Class<?>) implementedInterfaceRawType;

                Type[] actualTypeArguments = implementedInterfaceParameterizedType.getActualTypeArguments();
                Collection<Class<?>> actualTypeArgumentsClasses = new ArrayList<>();
                for (int i = 0; i <= actualTypeArguments.length - 1; i++) {
                    Type actualTypeArgument = actualTypeArguments[i];
                    if (actualTypeArgument instanceof Class<?>) {
                        Class<?> actualTypeArgumentClass = Class.forName(actualTypeArgument.getTypeName());
                        actualTypeArgumentsClasses.add(actualTypeArgumentClass);
                    } else if (actualTypeArgument instanceof TypeVariable) {
                        actualTypeArgumentsClasses.add(TypeVariableImpl.class); // TODO: This is just a hack since we're looking to test classes found rather than the generic type variable names
                    } else {
                        throw new Exception("Placeholder to see if this is a possible path.");
                    }
                }
                response.put(implementedInterfaceClass, actualTypeArgumentsClasses);
            } else {
                throw new Exception("Placeholder to see if this is a possible path.");
            }
        }
        return response;
    }


    public interface IClass1 {}
    public class Class1 implements IClass1 {}
    public class ExtendsClass2 extends Class1 {}
    public interface IClass1A {}
    public interface IClass1B {}
    public interface IClass2<A> extends IClass1 {}
    public abstract class AbstractClass2<A> implements IClass2<A> {}
    public abstract class ExtendsAbstractClass2WithString extends AbstractClass2<String> {}
    public class Class2String extends AbstractClass2<String> {}
    public class ExtendsClass2String extends Class2String {}
    public interface IClass3<A, B> extends IClass2<A> {}
    public interface IClass3StringInteger extends IClass3<String, Integer> {}
    public abstract class AbstractClass3<C, D> implements IClass3<C, D> {}
    public class Class3StringInteger extends AbstractClass3<String, Integer> {}
    public class Class2StringExtended<C> extends Class2String {}
    public class Class2StringExtendedAgainWithInteger extends Class2StringExtended<Integer> {}
    public class ExtenededClass2StringExtendedAgainWithInteger extends Class2StringExtendedAgainWithInteger {}
    public class ImplementsAndExtends extends AbstractClass2<String> implements IClass1A, IClass1B {}
    public class ImplementsAndExtends3WithString extends AbstractClass2<String> implements IClass1A, IClass1B {} //, IClass2<String> {}

    public interface IClassA<A> {}
    public interface IClassB<B> {}
    public interface IClassAB<D,C> extends IClassA<D>, IClassB<C> {}
    public class ClassABStringInteger implements IClassAB<String, Integer> {}

    public interface IClassX<X> {}
    public interface IClassY<Y> {}
    public interface IClassZ<Z> {}
    public interface IClassVW<V, W> extends IClassY<W>, IClassX<V> {}
    public interface IClassSTU<S, T, U> extends IClassZ<T>, IClassVW<U, S> {}
    public class ClassSTU implements IClassSTU<String, Integer, Boolean> {}

    public interface IClassD<D> {}
    public interface IClassE<E> {}
    public interface IClassF<F> {}
    public interface IClassG<G> extends IClassF<IClassE<G>> {}
    public class ClassG implements IClassG<Boolean> {}
    public interface IClassH<Z, Y> extends IClassG<IClassD<Y>>, IClassE<Z> {}
    public class ClassH implements IClassH<String, Integer> {}

}