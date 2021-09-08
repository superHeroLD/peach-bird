package com.ld.peach.gundam.game.component;

import com.ld.peach.gundam.game.PeachBird;
import com.ld.peach.gundam.utils.Constant;
import com.ld.peach.gundam.utils.GameUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 游戏元素层，水管的生成方法
 *
 * @author LD
 **/
public class GameElementLayer {
    /**
     * 水管的容器
     */
    private final List<Pipe> pipes;

    public GameElementLayer() {
        pipes = new ArrayList<>();
    }

    public void draw(Graphics g, Bird bird) {
        // 遍历水管容器，如果可见则绘制，不可见则归还
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            if (pipe.isVisible()) {
                pipe.draw(g, bird);
            } else {
                Pipe remove = pipes.remove(i);
                Pipe.PipePool.giveBack(remove);
                i--;
            }
        }
        bird.drawBirdImg(g);
        isCollideBird(bird);
        generatePipe(bird);
    }

    public static final int VERTICAL_INTERVAL = Constant.FRAME_HEIGHT >> 2;
    public static final int HORIZONTAL_INTERVAL = Constant.FRAME_HEIGHT >> 2;
    public static final int MIN_HEIGHT = Constant.FRAME_HEIGHT / 5;
    public static final int MAX_HEIGHT = Constant.FRAME_HEIGHT / 3;

    /**
     * 当容器中添加的最后一个水管完全显示到屏幕后，添加下一对；
     */
    private void generatePipe(Bird bird) {
        if (bird.isDead()) {
            return;
        }

        //若容器为空，则添加一对水管
        if (pipes.size() == 0) {
            //随机生成水管高度
            int topHeight = GameUtils.getRandomNumber(MIN_HEIGHT, MAX_HEIGHT + 1);

            Pipe top = Pipe.PipePool.get();
            top.setAttribute(Constant.FRAME_WIDTH, -Pipe.TOP_PIPE_LENGTHENING,
                    topHeight + Pipe.TOP_PIPE_LENGTHENING, Pipe.TYPE_TOP_NORMAL, true);

            Pipe bottom = Pipe.PipePool.get();
            bottom.setAttribute(Constant.FRAME_WIDTH, topHeight + VERTICAL_INTERVAL,
                    Constant.FRAME_HEIGHT - topHeight - VERTICAL_INTERVAL, Pipe.TYPE_BOTTOM_NORMAL, true);

            pipes.add(top);
            pipes.add(bottom);
        } else {
            // 判断最后一对水管是否完全进入游戏窗口，若进入则添加水管
            // 获得容器中最后一个水管
            Pipe lastPipe = pipes.get(pipes.size() - 1);
            // 小鸟和最后一根水管的距离
            int currentDistance = lastPipe.getX() - bird.getBirdX() + Bird.BIRD_WIDTH / 2;
            // 小于得分距离则得分
            final int SCORE_DISTANCE = Pipe.PIPE_WIDTH * 2 + HORIZONTAL_INTERVAL;
            if (pipes.size() >= Pipe.PipePool.FULL_PIPE
                    && currentDistance <= SCORE_DISTANCE + Pipe.PIPE_WIDTH * 3 / 2
                    && currentDistance > SCORE_DISTANCE + Pipe.PIPE_WIDTH * 3 / 2 - Constant.GAME_SPEED) {
                PeachBird.setCurrentReward(0.8f);
            }
            if (pipes.size() >= Pipe.PipePool.FULL_PIPE
                    && currentDistance <= SCORE_DISTANCE
                    && currentDistance > SCORE_DISTANCE - Constant.GAME_SPEED) {
                ScoreCounter.getInstance().score(bird);
            }
            if (lastPipe.isInFrame()) {
                addNormalPipe(lastPipe);
            }
        }
    }

    /**
     * 添加普通水管
     *
     * @param lastPipe 最后一根水管
     */
    private void addNormalPipe(Pipe lastPipe) {
        // 随机生成水管高度
        int topHeight = GameUtils.getRandomNumber(MIN_HEIGHT, MAX_HEIGHT + 1);
        // 新水管的x坐标 = 最后一对水管的x坐标 + 水管的间隔
        int x = lastPipe.getX() + HORIZONTAL_INTERVAL;

        Pipe top = Pipe.PipePool.get();
        top.setAttribute(x, -Pipe.TOP_PIPE_LENGTHENING, topHeight + Pipe.TOP_PIPE_LENGTHENING,
                Pipe.TYPE_TOP_NORMAL, true);

        Pipe bottom = Pipe.PipePool.get();
        bottom.setAttribute(x, topHeight + VERTICAL_INTERVAL, Constant.FRAME_HEIGHT - topHeight - VERTICAL_INTERVAL,
                Pipe.TYPE_BOTTOM_NORMAL, true);

        pipes.add(top);
        pipes.add(bottom);
    }

    /**
     * 判断元素和小鸟是否发生碰撞
     *
     * @param bird bird
     */
    public void isCollideBird(Bird bird) {
        if (bird.isDead()) {
            return;
        }
        for (Pipe pipe : pipes) {
            if (pipe.getPipeCollisionRect().intersects(bird.getBirdCollisionRect())) {
                bird.die();
                return;
            }
        }
    }

    public void reset() {
        for (Pipe pipe : pipes) {
            Pipe.PipePool.giveBack(pipe);
        }
        pipes.clear();
    }
}
