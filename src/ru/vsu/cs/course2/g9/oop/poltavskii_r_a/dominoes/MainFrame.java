package ru.vsu.cs.course2.g9.oop.poltavskii_r_a.dominoes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

// вариант игры, в котором 4 игрока, у каждого по 7 костей, первым ходит тот, у кого кость (1, 1)
public class MainFrame extends Frame {
    private Graphics graphics;
    private Color backgroundColor;
    private final static int PLAYERS_COUNT = 4;
    private final static int MAX_BONES_COUNT = 7;
    private final static int MAX_BONE_VALUE = 6;
    private int currentPlayerIdx;
    private int passesCount = 0;
    private int[] placeJ = new int[2];
    private int[] placeK = new int[2];
    private int[] rotationJ = new int[2];
    private int[] rotationK = new int[2];
    private byte[] endPoints = new byte[2];
    private ArrayList<Bone>[] playersBones = new ArrayList[PLAYERS_COUNT];
    private ArrayList<Bone> bonesOnTheDesk;
    private boolean selected;
    private int selectedIdx;
    private boolean gameStarted;
    private boolean isHandling;
    private boolean isChoosingBone;
    private int selectedOnBoard;
    public MainFrame() {
        initComponents();
        graphics = this.getGraphics();
        backgroundColor = getBackground();
    }
    /**
     * Строка, выводящая кто сейчас играет
     */
    private String getCurrentPlayer() {
        return "Текущий игрок: " + ("Игрок №" + (currentPlayerIdx + 1));
    }
    // внизу 1, слева 2, вверху 3, справа 4
    /**
     * Обновление заголовка
     */
    private void updateAppTitle() {
        setTitle("Домино. " + getCurrentPlayer());
    }

    /**
     * Инициализация
     */
    private void initComponents() {
        Button buttonStart = new Button();
        Button buttonStop = new Button();
        setBackground(new Color(29, 102, 0)); // фон поля
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setLocationRelativeTo(null);
        setResizable(false);
        updateAppTitle();
        selected = false;
        isHandling = false;
        addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowClosing(WindowEvent evt) {
                exitForm(evt);
            }
            public void windowOpened(WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
// перехватываем действия игрока
        addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
            }
            public void mousePressed(MouseEvent e) {
            }
            public synchronized void mouseReleased(MouseEvent e) {
                if (isChoosingBone) {
                    int x = e.getX();
                    int y = e.getY();
                    selectedOnBoard = selectOnBoard(x, y);
                    doMove();
                } else if (!isHandling && gameStarted && !selected) {
                    isHandling = true;
                    int x = e.getX();
                    int y = e.getY();
                    selectedIdx = selectBone(x, y);
                    selected = (selectedIdx != -1);
                    doMove();
                    isHandling = false;
                }
            }
            public void mouseEntered(MouseEvent e) {
            }
            public void mouseExited(MouseEvent e) {
            }
        });
        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        buttonStart.setLabel("Начать");
        buttonStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                gameStarted = true;
                startButtonListener(evt);
            }
        });
        buttonStop.setLabel("Стоп");
        buttonStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                gameStarted = false;
                stopButtonListener(evt);
            }
        });
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup( layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(buttonStart, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonStop, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(355, Short.MAX_VALUE))
        ); layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(buttonStop, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
                                .addComponent(buttonStart, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(323, Short.MAX_VALUE))
        );
        pack();
    }
    private int selectOnBoard(int x, int y) {
        int[] bounds = (bonesOnTheDesk.get(0).getBounds());
        if (x > bounds[0] && x < bounds[1] && y > bounds[2] && y < bounds[3]) {
            return 0;
        }
        bounds = (bonesOnTheDesk.get(bonesOnTheDesk.size() - 1).getBounds());
        if (x > bounds[0] && x < bounds[1] && y > bounds[2] && y < bounds[3]) {
            return bonesOnTheDesk.size();
        }
        return -1;
    }
    // попытка взять кость игрока по данным координатам
    private int selectBone(int x, int y) {
        for (int i = 0; i < playersBones[currentPlayerIdx].size(); i++) {
            Bone bone = playersBones[currentPlayerIdx].get(i);
            int[] bounds = (bone.getBounds());
            if (x > bounds[0] && x < bounds[1] && y > bounds[2] && y < bounds[3]) {
                return i;
            }
        }
        return -1;
    }
    private void exitForm(WindowEvent evt) {
        System.exit(0);
    }
    private void formWindowOpened(WindowEvent evt) {
    }
    private void formWindowActivated(WindowEvent evt) {
    }
    private void formComponentShown(ComponentEvent evt) {
    }
    // инициализация костей и раздача их игрокам
    private void initBones() {
        ArrayList<Bone> bonesPool = new ArrayList<Bone>();
        bonesPool.clear();
        for (byte p = 0; p <= MAX_BONE_VALUE; p++) {
            for (byte q = 0; q <= p; q++) {
                bonesPool.add(new Bone(p, q));
            }
        }
        for (int i = 0; i < PLAYERS_COUNT; i++) {
            playersBones[i] = new ArrayList<Bone>();
        }
        bonesOnTheDesk = new ArrayList<Bone>();
        for (int i = 0; i < MAX_BONES_COUNT; i++) {
            for (int p = 0; p < PLAYERS_COUNT; p++) {
                int k = (int) (Math.random() * bonesPool.size());
                playersBones[p].add(bonesPool.get(k));
                bonesPool.remove(k);
            }
        }
    }
    // старт
    private void startButtonListener(ActionEvent evt) {
        graphics.clearRect(0, 0, getWidth(), getHeight());
// Инициализация костяшек и раздача
        initBones();
// Размещение костяшек на экране
        for (int p = 0; p < PLAYERS_COUNT; p++) {
            for (int i = 0; i < MAX_BONES_COUNT; i++) {
                Bone bone = playersBones[p].get(i);
                int x = 0, y = 0;
                int dx = 0, dy = 0;
                switch (p) {
                    case 0:
                        x = this.getWidth() / 2 - Bone.width * 7;
                        y = this.getHeight() - Bone.width;
                        dx = (Bone.height + 10);
                        dy = 0;
                        bone.rotate((byte) 1, (byte) 0, graphics, backgroundColor);
                        break;
                    case 1:
                        x = Bone.width;
                        y = 120;
                        dx = 0;
                        dy = Bone.height + 10;
                        bone.rotate((byte) 0, (byte) 1, graphics, backgroundColor);
                        break;
                    case 2:
                        x = this.getWidth() / 2 - Bone.width * 7;
                        y = 30 + Bone.width;
                        dx = (Bone.height + 10);
                        dy = 0;
                        bone.rotate((byte) -1, (byte) 0, graphics, backgroundColor);
                        break;
                    case 3:
                        x = this.getWidth() - Bone.width;
                        y = 120;
                        dx = 0;
                        dy = Bone.height + 10;
                        bone.rotate((byte) 0, (byte) -1, graphics, backgroundColor);
                        break;
                }
                bone.moveTo(x + i * dx, y + i * dy, graphics, backgroundColor);
            }
        }
        int idxOfFirstPlayingPlayer = -1;
        for (int n = 1; n <= MAX_BONE_VALUE; n++) {
            for (int p = 0; p < PLAYERS_COUNT; p++) {
                for (int i = 0; i < playersBones[p].size(); i++) {
                    Bone bone = playersBones[p].get(i);
                    if (bone.points(0) == n && bone.points(1) == n) {
                        currentPlayerIdx = p;
                        idxOfFirstPlayingPlayer = i;
                    }
                }
            }
            if (currentPlayerIdx >= 0) break;
        }
        int pause = 2000;
        long t = System.currentTimeMillis();
        do {
        } while (System.currentTimeMillis() - t < pause);
        Bone bone = playersBones[currentPlayerIdx].get(idxOfFirstPlayingPlayer);
        bone.rotate(1, 0, graphics, backgroundColor);
        bone.moveSliding(getWidth() / 2, getHeight() / 2, 500, graphics, backgroundColor);
        bonesOnTheDesk.add(bone);
        playersBones[currentPlayerIdx].remove(idxOfFirstPlayingPlayer);
        currentPlayerIdx = (currentPlayerIdx + 1) % PLAYERS_COUNT;
        updateAppTitle();
        placeJ[0] = bone.getX();
        placeJ[1] = bone.getX();
        placeK[0] = bone.getY();
        placeK[1] = bone.getY();
        rotationJ[0] = -1;
        rotationJ[1] = +1;
        rotationK[0] = 0;
        rotationK[1] = 0;
        endPoints[0] = bone.points(0);
        endPoints[1] = bone.points(1);
        isChoosingBone = false;
        doMove();
    }
    private synchronized void doMove() {
        int x2, y2;
        Bone bone = null;
        int side = -1;
        boolean isShouldReturn;
        if (isChoosingBone) {
            isChoosingBone = false;
            isShouldReturn = false;
            bone = playersBones[currentPlayerIdx].get(selectedIdx);
            highlightBones(bone, false);
            if (selectedOnBoard == -1) {
                return;
            } else {
                bonesOnTheDesk.add(selectedOnBoard, bone);
                playersBones[currentPlayerIdx].remove(selectedIdx);
                side = (selectedOnBoard == 0) ? 0 : 1;
                selected = false;
            }
        } else if (!hasMoves()) {
            passesCount++;
            currentPlayerIdx = (currentPlayerIdx + 1) % PLAYERS_COUNT;
            getToolkit().beep();
            doMove();
            updateAppTitle();
            return;
        } else if (!selected) {
            isShouldReturn = true;
        } else {
            bone = playersBones[currentPlayerIdx].get(selectedIdx);
            isShouldReturn = true;
            if ((endPoints[0] != endPoints[1]) && (bone.points(0) == endPoints[0] && bone.points(1) == endPoints[1]) || (bone.points(1) == endPoints[0] && bone.points(0) == endPoints[1])) {
                highlightBones(bone, true);
                isChoosingBone = true;
                selected = false;
                return;
            }
            for (side = 0; side <= 1; side++) {
                if (bone.points(0) == endPoints[side] ||
                        bone.points(1) == endPoints[side]) {
                    if (side == 0) {
                        bonesOnTheDesk.add(0, bone);
                    } else {
                        bonesOnTheDesk.add(bonesOnTheDesk.size(), bone);
                    }
                    playersBones[currentPlayerIdx].remove(selectedIdx);
                    isShouldReturn = false;
                    break;
                }
            }
            selected = false;
        }
        if (isShouldReturn) {
            return;
        }
        boolean stop = false;
        if (bone.points(0) == endPoints[side]) {
            bone.rotate(+rotationJ[side], +rotationK[side], graphics, backgroundColor);
            endPoints[side] = bone.points(1);
        } else {
            bone.rotate(-rotationJ[side], -rotationK[side], graphics, backgroundColor);
            endPoints[side] = bone.points(0);
        }
        placeJ[side] += rotationJ[side] * Bone.height;
        placeK[side] += rotationK[side] * Bone.height;
        x2 = placeJ[side];
        y2 = placeK[side];
        if (rotationJ[side] == -1 && placeJ[side] < Bone.height * 3) {
            rotationJ[side] = 0;
            rotationK[side] = -1;
            placeJ[side] -= Bone.width / 2;
            placeK[side] += Bone.width / 2;
        }
        if (rotationK[side] == -1 && placeK[side] < Bone.height * 3) {
            rotationJ[side] = +1;
            rotationK[side] = 0;
            placeJ[side] -= Bone.width / 2;
            placeK[side] -= Bone.width / 2;
        }
        if (rotationJ[side] == +1 && placeJ[side] > getWidth() - Bone.height * 3) {
            rotationJ[side] = 0;
            rotationK[side] = +1;
            placeJ[side] += Bone.width / 2;
            placeK[side] -= Bone.width / 2;
        }
        if (rotationK[side] == +1 && placeK[side] > getHeight() / 2 * (side + 1) - Bone.height * 2) {
            rotationJ[side] = -1;
            rotationK[side] = 0;
            placeJ[side] += Bone.width / 2;
            placeK[side] += Bone.width / 2;
        }
        bone.moveSliding(x2, y2, 500, graphics, backgroundColor);
        for (Bone aBonesOnTheDesk : bonesOnTheDesk) {
            aBonesOnTheDesk.show(graphics);
        }
        for (int p = 0; p < PLAYERS_COUNT; p++) {
            for (int i = 0; i < playersBones[p].size(); i++) {
                playersBones[p].get(i).show(graphics);
            }
        }
        passesCount = 0;
        if (playersBones[currentPlayerIdx].size() == 0) {
            stop = true;
        }
        if (passesCount == PLAYERS_COUNT) {
            JOptionPane.showMessageDialog(this, "Рыба! Победил игрок :" + getWinnerName());
            return;
        } else if (stop) {
            JOptionPane.showMessageDialog(this, "Победил игрок № : " + + (currentPlayerIdx + 1));
            return;
        }
        currentPlayerIdx = (currentPlayerIdx + 1) % PLAYERS_COUNT;
        updateAppTitle();
        doMove();
    }
    private void highlightBones(Bone bone, boolean isHighlight) {
        Bone begin = bonesOnTheDesk.get(0);
        Bone end = bonesOnTheDesk.get(bonesOnTheDesk.size() - 1);
        if (isHighlight) {
            bone.setColorMarked().show(graphics);
            begin.setColorMarked().show(graphics);
            end.setColorMarked().show(graphics);
        } else {
            bone.setColorUsual().show(graphics);
            begin.setColorUsual().show(graphics);
            end.setColorUsual().show(graphics);
        }
    }
    private boolean hasMoves() { // проверка возможности хода
        for (int i = 0; i < playersBones[currentPlayerIdx].size(); i++) {
            Bone bone = playersBones[currentPlayerIdx].get(i);
            for (int side = 0; side <= 1; side++) {
                if (bone.points(0) == endPoints[side] || bone.points(1) == endPoints[side]) {
                    return true;
                }
            }
        }
        return false;
    }


    private String getWinnerName() { // получение имени победителя
        int winnerIdx = 0;
        int min = 99999; // рандомное число, которое больше суммы всех цифр на костяшках
        for (int p = 0; p < PLAYERS_COUNT; p++) {
            int curMin = 0;
            for (Bone bone : playersBones[p]) {
                curMin += bone.points(0) + bone.points(1);
            }
            if (curMin < min) {
                min = curMin;
                winnerIdx = p;
            }
        }
        return "№ " + (winnerIdx + 1);
    }

    // прекратить игру
    private void stopButtonListener(ActionEvent evt) {
        graphics.clearRect(0, 0, getWidth(), getHeight());
    }

}

