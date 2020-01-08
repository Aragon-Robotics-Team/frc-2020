package art840.frc2020.util;

import java.lang.reflect.InvocationTargetException;
import org.mockito.Mockito;

public class Mock {
    public static final <T, S> T createMockable(Class<T> clazz, S param, boolean isReal) {
        if (isReal) {
            try {
                return clazz.getConstructor(param.getClass()).newInstance(param);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException
                    | InvocationTargetException ex) {
                ex.printStackTrace();
                throw new IllegalArgumentException();
            }
        } else {
            return mock(clazz);
        }
    }

    // Why does Java force autoboxing to Integer?
    public static final <T> T createMockable(Class<T> clazz, int param, boolean isReal) {
        if (isReal) {
            try {
                return clazz.getConstructor(int.class).newInstance(param);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException
                    | InvocationTargetException ex) {
                ex.printStackTrace();
                throw new IllegalArgumentException();
            }
        } else {
            return mock(clazz);
        }
    }

    public static final <T, S> T createMockable(Class<T> clazz, S param) {
        boolean isReal = param != null;
        return createMockable(clazz, param, isReal);
    }

    public static final <T> T mock(Class<T> classToMock) {
        System.out.println("Mocking class: " + classToMock.getName());
        return Mockito.mock(classToMock,
                Mockito.withSettings().stubOnly().defaultAnswer(Mockito.RETURNS_DEEP_STUBS));
    }
}
