package is.yarr.qilletni.lang.internal.adapter;

import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.types.StaticEntityType;
import is.yarr.qilletni.lang.exceptions.NoTypeAdapterException;
import is.yarr.qilletni.api.lang.types.QilletniType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class TypeAdapterInvoker {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TypeAdapterInvoker.class);
    
    private final TypeAdapterRegistrar typeAdapterRegistrar;

    public TypeAdapterInvoker(TypeAdapterRegistrar typeAdapterRegistrar) {
        this.typeAdapterRegistrar = typeAdapterRegistrar;
    }

    public QilletniType invokeMethod(Object instance, Method method, List<QilletniType> params) throws IllegalAccessException, InvocationTargetException {
        // params defined in the class
        var javaParams = method.getParameterTypes();
        
        // params being sent to the java method, must match up with javaParams types
        var invokingParams = new Object[params.size()];

        for (int i = 0; i < javaParams.length; i++) {
            var javaParam = javaParams[i];
            var qilletniParam = params.get(i);
            
            if (javaParam.isAssignableFrom(qilletniParam.getClass())) {
                LOGGER.debug("Normal param {}", qilletniParam.typeName());
                invokingParams[i] = qilletniParam;
            } else {
                LOGGER.debug("Adapting param from {} to {}", javaParam.getSimpleName(), qilletniParam.getClass().getSimpleName());
                
                var typeAdapter = typeAdapterRegistrar.findTypeAdapter(javaParam, qilletniParam.getClass())
                        .orElseThrow(() -> {
                            if (StaticEntityType.class.isAssignableFrom(javaParam) && EntityType.class.isAssignableFrom(qilletniParam.getClass())) {
                                return new NoTypeAdapterException(javaParam, qilletniParam.getClass(), "Native function defined as a static function, but was invoked as an instance function");
                            }
                            
                            if (EntityType.class.isAssignableFrom(javaParam) && StaticEntityType.class.isAssignableFrom(qilletniParam.getClass())) {
                                return new NoTypeAdapterException(javaParam, qilletniParam.getClass(), "Native function defined as an instance function, but was invoked as a static function");
                            }
                            
                            return new NoTypeAdapterException(javaParam, qilletniParam.getClass());
                        });
                
                invokingParams[i] = typeAdapter.convertCastedType(qilletniParam);
            }
        }

        var invokedResult = method.invoke(instance, invokingParams);
        
        if (invokedResult == null) {
            return null;
        }

        // All native methods must return a QilletniType
        if (invokedResult instanceof QilletniType qilletniResult) {
            LOGGER.debug("Not adapting result {}", qilletniResult);
            return qilletniResult;
        }

        var typeAdapter = typeAdapterRegistrar.findAnyTypeAdapter(invokedResult.getClass())
                .orElseThrow(() -> new NoTypeAdapterException(invokedResult.getClass()));
        
        var adapted = typeAdapter.convertCastedType(invokedResult);
        LOGGER.debug("Adapting result from {} to {}", invokedResult.getClass().getSimpleName(), adapted.getClass().getSimpleName());
        
        return adapted;
    }
    
}
