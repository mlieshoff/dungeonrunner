package dungeonrunner.observer;

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

import dungeonrunner.arena.ArenaManager;
import dungeonrunner.entrance.EnterEntranceTicket;
import dungeonrunner.entrance.EntranceManager;
import dungeonrunner.system.Inject;
import dungeonrunner.system.MiniDI;
import dungeonrunner.system.util.Log;
import org.bukkit.entity.Player;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author Michael Lieshoff
 */
public class Observer {

    @Inject
    private ArenaManager arenaManager;

    private Queue<Ticket> tickets = new ArrayDeque<>();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while(!running) {
                Log.info(this, "run", "checking...");

                Ticket ticket = tickets.poll();
                while(ticket != null) {
                    processTicket(ticket);
                    ticket = tickets.poll();
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
            Log.info(this, "run", "stopped...");
        }
    };

    private void processTicket(Ticket ticket) {
        Player player = ticket.getPlayer();

        if (ticket instanceof EnterArenaTicket) {
            EnterArenaTicket enterArenaTicket = (EnterArenaTicket) ticket;
            Log.info(this, "processTicket", "enterArenaTicket.player=%s", player.getName());
            MiniDI.get(ArenaManager.class).enterArena(player);
        } else if (ticket instanceof EnterEntranceTicket) {
            Log.info(this, "processTicket", "enterEntranceTicket.player=%s", player.getName());
            MiniDI.get(EntranceManager.class).enter(player);
        }
    }

    private Thread thread = new Thread(runnable);

    private volatile boolean running;

    public void start() {
        if (!running) {
            Log.info(this, "start", "starting...");
            thread.start();
        }
    }

    public void stop() {
        if (running) {
            Log.info(this, "stop", "stopping...");
            running = false;
        }
    }

    public void enterArena(Player player) {
        tickets.add(new EnterArenaTicket(player));
    }

    public void enterEntrance(Player player) {
        tickets.add(new EnterEntranceTicket(player));
    }

}
