package com.ld.peach.gundam.game.component;

import com.ld.peach.gundam.utils.Constant;
import com.ld.peach.gundam.utils.GameUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * @author LD
 **/
public class Ground {

    private static final BufferedImage BACKGROUND_IMG;

    private final int velocity;
    private int layerX;

    public Ground() {
        this.velocity = Constant.GAME_SPEED;
        this.layerX = 0;
    }

    public static final int GROUND_HEIGHT;

    static {
        BACKGROUND_IMG = GameUtils.loadBufferedImage(Constant.BG_IMG_PATH);
        if (Objects.isNull(BACKGROUND_IMG)) {
            throw new RuntimeException(String.format("can't load Background img from %s", Constant.BG_IMG_PATH));
        }
        GROUND_HEIGHT = BACKGROUND_IMG.getHeight();
    }

    /**
     * 根据小鸟绘制图形
     */
    public void draw(Graphics g, Bird bird) {
        if (bird.isDead()) {
            return;
        }

        int imgWidth = Objects.requireNonNull(BACKGROUND_IMG).getWidth();

        // 根据窗口宽度得到图片的绘制次数
        int count = Constant.FRAME_WIDTH / imgWidth + 2;
        for (int i = 0; i < count; i++) {
            g.drawImage(BACKGROUND_IMG, imgWidth * i - layerX, Constant.FRAME_HEIGHT - GROUND_HEIGHT, null);
        }
        movement();
    }

    /**
     * 图形移动
     */
    private void movement() {
        layerX += velocity;
        if (layerX > Objects.requireNonNull(BACKGROUND_IMG).getWidth()) {
            layerX = 0;
        }
    }
}
