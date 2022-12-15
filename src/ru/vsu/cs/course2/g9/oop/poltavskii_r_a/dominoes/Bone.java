package ru.vsu.cs.course2.g9.oop.poltavskii_r_a.dominoes;

import java.awt.*;
// класс, который задает кость как графически, так и логически
public class Bone {
    static final byte width = 30;
    static final byte height = width * 2;
    static final byte pointSize = 6;
    static final Color colorMarked = Color.RED; // обводка
    static final Color colorBack = Color.WHITE; // костяшка
    static final Color colorPoint = Color.BLACK; // точки
    private byte points1, points2;
    private int X, Y;
    private byte dirX, dirY;
    static byte[][][] pointCoords = { // точки на костяшках
            {},
            {{0, 0}},
            {{-1, -1}, {1, 1}},
            {{-1, -1}, {0, 0}, {1, 1}},
            {{-1, -1}, {1, -1}, {-1, 1}, {1, 1}},
            {{-1, -1}, {1, -1}, {-1, 1}, {1, 1}, {0, 0}},
            {{-1, -1}, {1, -1}, {-1, 1}, {1, 1}, {0, -1}, {0, 1}}
    };
    private boolean isMarked;
    Bone(byte p1, byte p2) {
        points1 = p1;
        points2 = p2;
        isMarked = false;
    }
    int getX() {
        return X;
    }
    int getY() {
        return Y;
    }
    /**
     * Получение размер по X
     */
    int getSizeX() {
        if (dirX != 0) {
            return height;
        } else {
            return width;
        }
    }
    Bone setColorMarked() {
        isMarked = true;
        return this;
    }
    Bone setColorUsual() {
        isMarked = false;
        return this;
    }
    /**
     * Получение размер по Y
     */
    int getSizeY() {
        if (dirX != 0) {
            return width;
        } else {
            return height;
        }
    }
    /**
     * Получение очков со стороны
     */
    byte points(int side) {
        if (side == 0) {
            return points1;
        }
        if (side == 1) {
            return points2;
        }
        return -1;
    } /**
     * Получение размера костяшки на экране для опеределения области прикосновения
     */
    int[] getBounds() {
        int sx = getSizeX(), sy = getSizeY();
        return new int[]{X - sx / 2, X + sx / 2, Y - sy / 2, Y + sy / 2};
    }
    /**
     * Отображение костяшки
     */
    void show(Graphics G) {
        int sx = getSizeX(), sy = getSizeY();
        G.setColor(colorBack);
        G.fillRect(X - sx / 2, Y - sy / 2, sx, sy);
        if (isMarked) {
            G.setColor(colorMarked);
        } else {
            G.setColor(colorPoint);
        }
        G.drawRect(X - sx / 2, Y - sy / 2, sx - 1, sy - 1);
        G.setColor(colorPoint);
        if (dirX != 0) {
            G.drawLine(X, Y - sy / 2 + 3, X, Y + sy / 2 - 4);
        } else {
            G.drawLine(X - sx / 2 + 3, Y, X + sx / 2 - 4, Y);
        }
        int x1 = X - dirX * sx / 4, y1 = Y - dirY * sy / 4;
        for (int s = 1; s <= 2; s++) {
            int p = points1;
            if (s == 2) p = points2;
            for (int i = 0; i < p; i++) {
                int dx = pointCoords[p][i][0] * width / 4;
                int dy = pointCoords[p][i][1] * width / 4;
                G.fillOval(x1 + dx - pointSize / 2, y1 + dy - pointSize / 2,
                        pointSize, pointSize);
            }
            x1 += dirX * sx / 2;
            y1 += dirY * sy / 2;
        }
    }
    /**
     * Спрятать кость
     */
    void hide(Graphics G, Color back) {
        G.setColor(back);
        int sx = getSizeX(), sy = getSizeY();
        G.fillRect(X - sx / 2, Y - sy / 2, sx, sy);
    }
    /**
     * Перемещение костяшки
     */
    void moveTo(int x, int y, Graphics G, Color back) {
        hide(G, back);
        this.X = x;
        this.Y = y;
        show(G);
    }
    /**
     * Поворот костяшки
     */
    void rotate(int dirX, int dirY, Graphics G, Color back) {
        assert dirX >= -1 && dirX <= +1 && dirY >= -1 && dirY <= +1;
        hide(G, back);
        this.dirX = (byte) dirX;
        this.dirY = (byte) dirY;
        show(G);
    }
    /**
     * Плавное перемещение костяшки
     */
    void moveSliding(int x2, int y2, int time, Graphics G, Color back) {
        int x1 = getX(), y1 = getY();
        int dt = 1000 / 25;
        int n = time / dt;
        for (int p = 1; p <= n; p++) {
            int xp = x1 + (x2 - x1) * p / n;
            int yp = y1 + (y2 - y1) * p / n;
            moveTo(xp, yp, G, back);
            long t = System.currentTimeMillis();
            do {
            } while (System.currentTimeMillis() - t < dt);
        }
    }
}

