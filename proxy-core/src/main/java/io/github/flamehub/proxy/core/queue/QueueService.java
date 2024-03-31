package io.github.flamehub.proxy.core.queue;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class QueueService {
    private final Map<String, LinkedList<String>> queues = new ConcurrentHashMap<>();

    public void add(String queue, String entry) {
        queues.computeIfAbsent(queue, k -> new LinkedList<>()).add(entry);
    }

    public void remove(String entry) {
        queues.forEach((key, value) -> value.remove(entry));
        System.out.println("usunieto " + entry);
    }

    public void remove(String queue, String entry) {
        LinkedList<String> queueList = queues.get(queue);
        if (queueList != null) {
            queueList.remove(entry);
        }

        System.out.println("usunieto " + entry  + " z " + queue);
    }

    public List<String> findPlayersFromQueue(String queue) {
        return new ArrayList<>(queues.getOrDefault(queue, new LinkedList<>()));
    }

    public boolean isInQueue(String queue, String entry) {
        LinkedList<String> queueList = queues.get(queue);
        return queueList != null && queueList.contains(entry);
    }

    public List<String> getAllPlayers(String queue) {
        return findPlayersFromQueue(queue);
    }

    public int getPlace(String queue, String player) {
        List<String> players = findPlayersFromQueue(queue);
        return players.indexOf(player);
    }

    public Map<String, LinkedList<String>> getQueues() {
        return queues;
    }
}