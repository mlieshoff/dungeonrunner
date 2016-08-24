package dungeonrunner.system.manager;

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


import dungeonrunner.system.transaction.TransactionContext;
import dungeonrunner.system.util.Lambda;

/**
 * @author Michael Lieshoff
 */
public abstract class AbstractManager implements Manager {

    public <T> T doInManager(Lambda<T> lambda) throws ManagerException {
        try {
            TransactionContext.joinOrCreate();
            T t = lambda.exec();
            TransactionContext.commit();
            return t;
        } catch (Exception e) {
            TransactionContext.rollback();
            throw new ManagerException(e);
        } finally {
            TransactionContext.release();
        }
    }

}
