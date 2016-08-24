package dungeonrunner.system.di;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import dungeonrunner.system.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Michael Lieshoff
 */
public class MiniDI {

    private static final MiniDI INSTANCE = new MiniDI();

    private final Map<Class<?>, Object> instances = new ConcurrentHashMap<>();

    public static void register(Class<?>... classes) {
        try {
            INSTANCE.registerIntern(classes);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static void register(Class<?> clazz, Object bean) {
        try {
            INSTANCE.registerIntern(clazz, bean);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void registerIntern(Class<?>... classes) throws IllegalAccessException, InstantiationException {
        for (Class<?> clazz : classes) {
            instances.put(clazz, clazz.newInstance());
            Log.info(this, "registerIntern", "create bean: %s", clazz.getName());
        }
        for (Class<?> clazz : classes) {
            Log.info(this, "registerIntern", "checking properties from %s", clazz.getName());
            Object instance = instances.get(clazz);
            for (Field field : clazz.getDeclaredFields()) {
                Log.info(this, "registerIntern", "    checking field %s", field.getName());
                Annotation inject = field.getAnnotation(Inject.class);
                if (inject != null) {
                    Class<?> fieldClass = field.getType();
                    Object object = instances.get(fieldClass);
                    if (object == null) {
                        Log.error(this, "registerIntern", "cannot set property %s in bean: %s", field.getName(),
                                clazz.getName());
                    } else {
                        field.setAccessible(true);
                        field.set(instance, object);
                        Log.info(this, "registerIntern", "set property %s in bean %s to %s", field.getName(),
                                clazz.getName(), object);
                    }
                }
            }
        }
    }

    private void registerIntern(Class<?> clazz, Object bean) throws IllegalAccessException, InstantiationException {
        instances.put(clazz, bean);
    }

    public static <T> T get(Class<T> observerClass) {
        return (T) INSTANCE.instances.get(observerClass);
    }

}
