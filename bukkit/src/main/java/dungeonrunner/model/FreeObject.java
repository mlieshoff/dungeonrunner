package dungeonrunner.model;

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
public class FreeObject<T> {

    private final T object;
    private final int id;
    private final boolean mustCreate;

    public FreeObject(int id) {
        this(null, id);
    }

    public FreeObject(T object, int id) {
        this.object = object;
        this.id = id;
        mustCreate = object == null;
    }

    public T getObject() {
        return object;
    }

    public int getId() {
        return id;
    }

    public boolean isMustCreate() {
        return mustCreate;
    }

}