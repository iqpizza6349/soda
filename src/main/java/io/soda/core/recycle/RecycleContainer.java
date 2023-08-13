package io.soda.core.recycle;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This class management a recyclable classes
 * @see Recyclable
 */
public class RecycleContainer {
    private static final Logger log = LoggerFactory.getLogger(RecycleContainer.class);

    private final Reflections reflections = new Reflections("");

    private final Map<String, Object> recycleComponents = new HashMap<>();

    private static RecycleContainer container;

    public static RecycleContainer getInstance() {
        if (container == null) {
            container = new RecycleContainer();
        }
        return container;
    }

    private String getRecyclableName(Class<?> clazz) {
        String original = clazz.getSimpleName();
        if (original.length() == 1) {
            return original.toLowerCase();
        }

        return String.format("%s%s",
                original.substring(0, 1).toLowerCase(),
                original.substring(1));
    }

    public Set<String> getRecyclableNames() {
        return recycleComponents.keySet();
    }

    public Object getRecyclable(String name) {
        return recycleComponents.get(name);
    }

    public Collection<Object> getRecyclables() {
        return recycleComponents.values();
    }

    public boolean isTypeMatch(String name, Class<?> qualifiedType) {
        Class<?> recyclableClass = getRecyclable(name).getClass();
        return isTypeMatch(recyclableClass, qualifiedType);
    }

    private boolean isTypeMatch(Class<?> origin, Class<?> qualified) {
        if (origin == Object.class) {
            return false;
        }
        else if (origin == qualified) {
            return true;
        }
        return isTypeMatch(origin.getSuperclass(), qualified);
    }

    private Optional<String> getQualifiedRecyclableName(Class<?> qualifiedType) {
        Set<String> names = getRecyclableNames();
        for (String recyclableName : names) {
            if (isTypeMatch(recyclableName, qualifiedType)) {
                return Optional.of(recyclableName);
            }
        }
        return Optional.empty();
    }

    private void registerRecyclables(Class<?> recyclable) {
        if (recyclable.isAnnotation() || recyclable.isInterface()) {
            return;
        }

        Class<?>[] params = recyclable.getConstructors()[0].getParameterTypes();
        String[] args = new String[params.length];

        for (int i = 0; i < params.length; i++) {
            Class<?> type = params[i];
            int finalI = i;
            getQualifiedRecyclableName(type)
                    .ifPresent(qualifiedRecyclable -> args[finalI] = qualifiedRecyclable);
        }

        register(recyclable, getRecyclableName(recyclable), args);
    }

    private void register(Class<?> recyclable, String name, String[] params) {
        // and find all arguments of instances, and put it all to instance a `recyclable`
        Object[] args = new Object[params.length];
        for (int i = 0; i  < params.length; i++) {
            args[i] = getRecyclable(params[i]);
        }

        Object instance;
        try {
            instance = recyclable.getConstructors()[0].newInstance(args);
        } catch (ReflectiveOperationException e) {
            log.error("Exception occurs when recyclable component to make instance", e);
            throw new RuntimeException(e);
        }

        recycleComponents.put(name, instance);
    }

    public void processRegister() {
        Set<Class<?>> recyclableClasses = reflections.getTypesAnnotatedWith(Recyclable.class);
        for (Class<?> recyclable : recyclableClasses) {
            registerRecyclables(recyclable);
        }

        log.debug("{} Recyclable Components are successfully registered into recycling 'bean'",
                recyclableClasses.size());
    }
}
