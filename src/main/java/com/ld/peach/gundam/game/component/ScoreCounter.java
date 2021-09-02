package com.ld.peach.gundam.game.component;

import com.ld.peach.gundam.game.PeachBird;

import java.util.concurrent.atomic.AtomicLong;

/**
 * ScoreCounter
 *
 * @author LD
 **/
public class ScoreCounter {

    private static final ScoreCounter INSTANCE = new ScoreCounter();

    private final AtomicLong score = new AtomicLong(0);

    private ScoreCounter() {
    }

    public static ScoreCounter getInstance() {
        return INSTANCE;
    }

    public void score(Bird bird) {
        if (!bird.isDead()) {
            PeachBird.setCurrentReward(1f);
            score.incrementAndGet();
        }
    }

    public long getCurrentScore() {
        return score.longValue();
    }

    public void reset() {
        score.set(0L);
    }
}
