package com.ld.peach.bird;


import ai.djl.modality.rl.env.RlEnv;
import lombok.extern.slf4j.Slf4j;

/**
 * Train Bird
 *
 * @author ld
 */
@Slf4j
public class Train {

    /**
     * gameSteps to observe before training
     */
    public static final int OBSERVE = 1000;
    /**
     * frames over which to anneal epsilon
     */
    public static final int EXPLORE = 3000000;
    /**
     * save model every 100,000 step
     */
    public static final int SAVE_EVERY_STEPS = 100000;
    /**
     * number of previous transitions to remember
     */
    public static final int REPLAY_BUFFER_SIZE = 50000;
    /**
     * decay rate of past observations
     */
    public static final float REWARD_DISCOUNT = 0.9f;
    public static final float INITIAL_EPSILON = 0.01f;
    public static final float FINAL_EPSILON = 0.0001f;
    public static final String PARAMS_PREFIX = "dqn-trained";

    /**
     * An environment to use for reinforcement learning.
     */
    static RlEnv.Step[] batchSteps;

    private Train() {
    }

    public static void main(String[] args) {

    }
}
