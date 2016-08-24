package dungeonrunner.player;

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

import dungeonrunner.system.di.Inject;
import dungeonrunner.system.manager.AbstractManager;
import dungeonrunner.system.manager.ManagerException;
import dungeonrunner.system.util.Lambda;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author Michael Lieshoff
 */
public class CharacterManager extends AbstractManager {

    @Inject
    private CharacterDao _characterDao;

    public Character login(final Player player) throws ManagerException {
        return doInManager(new Lambda<Character>() {
            @Override
            public Character exec(Object... params) throws Exception {
                UUID uuid = player.getUniqueId();
                Character character = _characterDao.find(uuid);
                if (character == null) {
                    character = _characterDao.register(player);
                }
                return character;
            }
        });
    }

}
