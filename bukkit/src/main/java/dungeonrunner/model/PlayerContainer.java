package dungeonrunner.model;

import dungeonrunner.system.util.CollectionUtils;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

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

/**
 * @author Michael Lieshoff
 */
public class PlayerContainer {

    private AtomicInteger count = new AtomicInteger(0);

    private int id;

    public PlayerContainer(int id) {
        this.id = id;
    }

    public int count() {
        return count.get();
    }

    public int increment() {
        return count.incrementAndGet();
    }

    public int decrement() {
        return count.decrementAndGet();
    }

    public int getId() {
        return id;
    }

    protected <T extends PlayerContainer> FreeObject<T> findFree(int max, Map<Integer, T> objects) {
        Set<Integer> foundIds = new HashSet<>();
        Set<Integer> maximalIds = new HashSet<>();
        for (int i = 0; i < max; i ++) {
            maximalIds.add(i + 1);
        }
        Set<T> sorted = new TreeSet<>(new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return Integer.compare(o1.count(), o2.count());
            }
        });
        for (Map.Entry<Integer, T> entry : objects.entrySet()) {
            int id = entry.getKey();
            foundIds.add(id);
            sorted.add(entry.getValue());
        }
        FreeObject<T> freeObject;
        if (objects.size() == 0) {
            freeObject = new FreeObject<>(1);
        } else {
            T first = sorted.iterator().next();
            freeObject = new FreeObject<>(first, first.getId());
        }
        if (freeObject.isMustCreate()) {
            Collection<Integer> freeIds = CollectionUtils.subtract(maximalIds, foundIds);
            if (freeIds.size() == 0) {
                return null;
            } else {
                int id = CollectionUtils.first(freeIds);
                freeObject = new FreeObject<>(id);
            }
        }
        return freeObject;
    }

    public Set<PlayerContainer> destroy() {
        Set<PlayerContainer> set = new HashSet<>();
        return set;
    }

    public boolean isEmpty() {
        return count.get() == 0;
    }

    @Override
    public String toString() {
        return "PlayerContainer{" +
                "class=" + getClass().getSimpleName() +
                ", count=" + count +
                ", id=" + id +
                '}';
    }
}
