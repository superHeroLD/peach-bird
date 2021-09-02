package com.ld.peach.gundam.game;

import ai.djl.modality.rl.ActionSpace;
import ai.djl.modality.rl.LruReplayBuffer;
import ai.djl.modality.rl.ReplayBuffer;
import ai.djl.modality.rl.env.RlEnv;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDArrays;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import com.ld.peach.gundam.game.component.Bird;
import com.ld.peach.gundam.game.component.Ground;
import com.ld.peach.gundam.utils.GameUtils;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Queue;

import static com.ld.peach.gundam.utils.Constant.*;

/**
 * 游戏实现类
 * 为强化学习提供环境
 *
 * @author LD
 **/
@Slf4j
public class PeachBird extends Frame implements RlEnv {

    private static final long serialVersionUID = 1L;

    private static int gameState;
    public static final int GAME_START = 1;
    public static final int GAME_OVER = 2;

    private Ground ground;
    private Bird bird;
    private GameElementLayer gameElement;
    private boolean withGraphics;

    private final NDManager manager;
    private final ReplayBuffer replayBuffer;
    private BufferedImage currentImg;
    private NDList currentObservation;
    private ActionSpace actionSpace;

    public static int gameStep = 0;
    public static int trainStep = 0;
    private static boolean currentTerminal = false;
    private static float currentReward = 0.2f;
    private String trainState = "observe";

    /**
     * Constructs for PeachBird
     *
     * @param manager          the manager for creating the game in
     * @param batchSize        the number of steps to train on per batch
     * @param replayBufferSize the number of steps to hold in the buffer
     */
    public PeachBird(NDManager manager, int batchSize, int replayBufferSize, boolean withGraphics) {
        this(manager, new LruReplayBuffer(batchSize, replayBufferSize));
        this.withGraphics = withGraphics;
        if (this.withGraphics) {
            initFrame();
            this.setVisible(true);
        }
        actionSpace = new ActionSpace();
        actionSpace.add(new NDList(manager.create(DO_NOTHING)));
        actionSpace.add(new NDList(manager.create(FLAP)));

        currentImg = new BufferedImage(FRAME_WIDTH, FRAME_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
        currentObservation = createObservation(currentImg);
        ground = new Ground();
        gameElement = new GameElementLayer();
        bird = new Bird();
        setGameState(GAME_START);
    }


    public PeachBird(NDManager manager, ReplayBuffer replayBuffer) {
        this.manager = manager;
        this.replayBuffer = replayBuffer;
    }

    /**
     * 图像帧队列，只存4帧
     */
    private final Queue<NDArray> imgQueue = new ArrayDeque<>(4);

    /**
     * 将图像转换为 CNN 输入。
     * 复制初始帧图像，堆栈到NDList，
     * 然后用当前帧替换第四帧，保证批量图片连续
     *
     * @param currentImg the image of current frame
     * @return the CNN input
     */
    public NDList createObservation(BufferedImage currentImg) {
        NDArray observation = GameUtils.imgPreprocess(currentImg);
        if (imgQueue.isEmpty()) {
            for (int i = 0; i < 4; i++) {
                imgQueue.offer(observation);
            }
            return new NDList(NDArrays.stack(new NDList(observation, observation, observation, observation), 1));
        } else {
            imgQueue.remove();
            imgQueue.offer(observation);
            NDArray[] buf = new NDArray[4];
            int i = 0;
            for (NDArray nd : imgQueue) {
                buf[i++] = nd;
            }
            return new NDList(NDArrays.stack(new NDList(buf[0], buf[1], buf[2], buf[3]), 1));
        }
    }

    /**
     * 初始化游戏窗口
     */
    private void initFrame() {
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setTitle(GAME_TITLE);
        setLocation(FRAME_X, FRAME_Y);
        //这里能否调整窗口大小
        setResizable(true);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    @Override
    public void reset() {

    }

    @Override
    public NDList getObservation() {
        return null;
    }

    @Override
    public ActionSpace getActionSpace() {
        return null;
    }

    @Override
    public Step step(NDList action, boolean training) {
        return null;
    }

    @Override
    public Step[] getBatch() {
        return new Step[0];
    }

    @Override
    public void close() {

    }
}
