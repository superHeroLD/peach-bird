package com.ld.peach.gundam;


import ai.djl.MalformedModelException;
import ai.djl.Model;
import ai.djl.modality.rl.env.RlEnv;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Activation;
import ai.djl.nn.Blocks;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.convolutional.Conv2d;
import ai.djl.nn.core.Linear;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.Trainer;
import com.ld.peach.gundam.game.PeachGundam;
import com.ld.peach.gundam.utils.Arguments;
import com.ld.peach.gundam.utils.Constant;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Let's start training the Bird！
 *
 * @author LD
 */
@Slf4j
public class TrainGundam {

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

    private TrainGundam() {
    }

    public static void main(String[] args) throws Exception {
        log.info("Welcome to Peach-Gundam");
        Arguments arguments = Arguments.parseArgs(args);
        Model model = createOrLoadModel(arguments);
        if (arguments.isTesting()) {
            test(model);
        } else {
            log.info("I haven't implemented this feature yet!");
        }
    }

    public static void test(Model model) {
        PeachGundam game = new PeachGundam(NDManager.newBaseManager(), 1, 1, true);
        DefaultTrainingConfig config = setupTrainingConfig();
        try (Trainer trainer = model.newTrainer(config)) {
            RlAgent agent = new QAgent(trainer, REWARD_DISCOUNT);
            while (true) {
                game.runEnvironment(agent, false);
            }
        }
    }

    /**
     * 创建神经元网络模型
     *
     * @param arguments 接收命令行出入的入参
     */
    public static Model createOrLoadModel(Arguments arguments) throws IOException, MalformedModelException {
        Model model = Model.newInstance("QNetwork");
        model.setBlock(getBlock());
        if (arguments.isPreTrained()) {
            model.load(Paths.get(Constant.MODEL_PATH), PARAMS_PREFIX);
        }
        return model;
    }

    /**
     * 构建训练模型
     * 返回链式模块，每个模块的输出是下一个子模块的输入
     *
     * @return SequentialBlock
     */
    public static SequentialBlock getBlock() {
        // conv -> conv -> conv -> fc -> fc
        return new SequentialBlock()
                .add(Conv2d.builder()
                        .setKernelShape(new Shape(8, 8))
                        .optStride(new Shape(4, 4))
                        .optPadding(new Shape(3, 3))
                        .setFilters(4).build())
                .add(Activation::relu)
                .add(Conv2d.builder()
                        .setKernelShape(new Shape(4, 4))
                        .optStride(new Shape(2, 2))
                        .setFilters(32).build())
                .add(Activation::relu)
                .add(Conv2d.builder()
                        .setKernelShape(new Shape(3, 3))
                        .optStride(new Shape(1, 1))
                        .setFilters(64).build())
                .add(Activation::relu)
                .add(Blocks.batchFlattenBlock())
                .add(Linear.builder()
                        .setUnits(512).build())
                .add(Activation::relu)
                .add(Linear.builder()
                        .setUnits(2).build());
    }
}
