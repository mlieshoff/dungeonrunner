package dungeonrunner.system.transaction;

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

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Transaction;
import dungeonrunner.system.util.Log;

/**
 * @author Michael Lieshoff
 */
public class TransactionContext {

    private EbeanServer ebeanServer;

    private static final TransactionContext INSTANCE = new TransactionContext();

    private static final ThreadLocal<Transaction> CACHE = new ThreadLocal<Transaction>(){
        @Override
        protected Transaction initialValue() {
            return INSTANCE.ebeanServer.beginTransaction();
        }
    };

    public static void init(EbeanServer ebeanServer) {
        INSTANCE.ebeanServer = ebeanServer;
    }

    public static Transaction joinOrCreate() {
        Log.info(INSTANCE, "joinOrCreate", "join or create transaction");
        return CACHE.get();
    }

    public static void commit() {
        Log.info(INSTANCE, "commit", "commit transaction");
        CACHE.get().commit();
    }

    public static void rollback() {
        Log.info(INSTANCE, "rollback", "rollback transaction");
        CACHE.get().rollback();
    }

    public static void release() {
        Log.info(INSTANCE, "release", "release context");
        CACHE.remove();
    }

}
