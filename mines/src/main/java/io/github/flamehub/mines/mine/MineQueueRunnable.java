package io.github.flamehub.mines.mine;

import java.util.ArrayDeque;
import java.util.Deque;

public final class MineQueueRunnable implements Runnable {

    private static final long BLOCKS_PER_TICK = 5_000L;
    private final Deque<Mine> minesQueue;

    public MineQueueRunnable() {
        this.minesQueue = new ArrayDeque<>();
    }

    public void queue(Mine mine) {
        this.minesQueue.offer(mine);
    }

    @Override
    public void run() {
        long blocksUpdated = 0L;

        while (!minesQueue.isEmpty() && blocksUpdated <= BLOCKS_PER_TICK) {
            Mine mine = minesQueue.poll();
            if (mine == null) break;

            blocksUpdated += mine.getTotalBlocks();
            mine.regenerate();
        }
    }
}