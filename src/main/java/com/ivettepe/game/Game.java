package com.ivettepe.game;

import com.ivettepe.IO.Input;
import com.ivettepe.display.Display;
import com.ivettepe.utils.Time;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.security.Key;
import java.util.Random;

public class Game implements Runnable {
    private boolean render = false;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 500;
    public static final String TITLE = "Pong";
    public static final int CLEAR_COLOR = 0xff000000;
    public static final int NUM_BUFFERS = 3;

    public static final float UPDATE_RATE = 60.0f;
    public static final float UPDATE_INTERVAL = Time.SECOND / UPDATE_RATE;
    public static final long IDLE_TIME = 1;
    public static final int NEED_TO_WIN = 5;

    private boolean running;
    private Thread gameTread;
    private Graphics2D graphics;
    private Input input;

    private int xPointsMiddleLine[] = {((int) (WIDTH / 2) - 1), ((int) (WIDTH / 2) + 1), ((int) (WIDTH / 2) + 1), ((int) (WIDTH / 2) - 1)};
    private int yPointsMiddleLine[] = {0, 0, HEIGHT, HEIGHT};
    private int xPointsFirstPlayer[] = {25, 30, 30, 25};
    private int yPointsFirstPlayer[] = {HEIGHT / 2 + 50, HEIGHT / 2 + 50, HEIGHT / 2 - 50, HEIGHT / 2 - 50};

    private int xPointsSecondPlayer[] = {WIDTH - 25, WIDTH - 30, WIDTH - 30, WIDTH - 25};
    private int yPointsSecondPlayer[] = {HEIGHT / 2 + 50, HEIGHT / 2 + 50, HEIGHT / 2 - 50, HEIGHT / 2 - 50};
    private int ballWidth = 10, ballHeight = 10, xVector, yVector;
    private int firstPlayerScore = 0, secondPlayerScore = 0;
    private int xBall, yBall;
    private int GOAL = 0;
    private float speed = 5;
    private int npoints = 4;


    public Game() {
        running = false;
        setVector();
        Display.create(WIDTH, HEIGHT, TITLE, CLEAR_COLOR, NUM_BUFFERS);
        graphics = Display.getGraphics();
        input = new Input();
        Display.addInputListener(input);
    }

    private void endGame() {
        xPointsMiddleLine[0] = yPointsMiddleLine[0] = xPointsFirstPlayer[0] = yPointsFirstPlayer[0] = xPointsSecondPlayer[0] = yPointsSecondPlayer[0] = 0;
        xPointsMiddleLine[1] = yPointsMiddleLine[1] = xPointsFirstPlayer[1] = yPointsFirstPlayer[1] = xPointsSecondPlayer[1] = yPointsSecondPlayer[1] = 0;
        xPointsMiddleLine[2] = yPointsMiddleLine[2] = xPointsFirstPlayer[2] = yPointsFirstPlayer[2] = xPointsSecondPlayer[2] = yPointsSecondPlayer[2] = 0;
        xPointsMiddleLine[3] = yPointsMiddleLine[3] = xPointsFirstPlayer[3] = yPointsFirstPlayer[3] = xPointsSecondPlayer[3] = yPointsSecondPlayer[3] = 0;
        ballWidth = ballHeight = 0;
    }

    public synchronized void start() {
        if(running)
            return;
        running = true;
        setStartPosition();
        gameTread = new Thread(this);
        gameTread.start();
    }

    public synchronized void stop() {
        if(!running)
            return;
        running = false;
        try {
            gameTread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cleanUp();
    }

    private void update() throws InterruptedException {
        if(input.getKey(KeyEvent.VK_A)) {
            if(yPointsFirstPlayer[0] > 100) {
                yPointsFirstPlayer[0] -= speed;
                yPointsFirstPlayer[1] -= speed;
                yPointsFirstPlayer[2] -= speed;
                yPointsFirstPlayer[3] -= speed;
            }
        }
        if(input.getKey(KeyEvent.VK_Z)) {
            if(yPointsFirstPlayer[3] < HEIGHT - 100) {
                yPointsFirstPlayer[0] += speed;
                yPointsFirstPlayer[1] += speed;
                yPointsFirstPlayer[2] += speed;
                yPointsFirstPlayer[3] += speed;
            }
        }
        if(input.getKey(KeyEvent.VK_K)) {
            if(yPointsSecondPlayer[0] > 100) {
                yPointsSecondPlayer[0] -= speed;
                yPointsSecondPlayer[1] -= speed;
                yPointsSecondPlayer[2] -= speed;
                yPointsSecondPlayer[3] -= speed;
            }
        }
        if(input.getKey(KeyEvent.VK_M)) {
            if(yPointsSecondPlayer[3] < HEIGHT - 100) {
                yPointsSecondPlayer[0] += speed;
                yPointsSecondPlayer[1] += speed;
                yPointsSecondPlayer[2] += speed;
                yPointsSecondPlayer[3] += speed;
            }
        }
        if((xBall == xPointsFirstPlayer[1] && (yBall <= yPointsFirstPlayer[1] && yBall >= yPointsFirstPlayer[2]))) xVector = -xVector;
        else if((xBall == xPointsSecondPlayer[0] - ballWidth * 2) && (yBall <= yPointsSecondPlayer[0] && yBall >= yPointsSecondPlayer[2])) xVector = -xVector;
        xBall += xVector;
        if(yBall == 0 || yBall == HEIGHT - ballHeight * 2) yVector = -yVector;
        yBall += yVector;

        if(xBall == 0) {
            setStartPosition();
            xVector = yVector = 0;
            GOAL = 1;
            secondPlayerScore += 1;
        } else if(xBall == WIDTH - ballWidth * 2) {
            setStartPosition();
            xVector = yVector = 0;
            GOAL = 1;
            firstPlayerScore += 1;
        }
        if(GOAL == 1 && input.getKey((KeyEvent.VK_SPACE))) {
            GOAL = 0;
            setVector();
        }
        if(firstPlayerScore == NEED_TO_WIN) {
            endGame();
            GOAL = -1;
        } else if(secondPlayerScore == NEED_TO_WIN) {
            endGame();
            GOAL = -1;
        }
        if(GOAL == -1 && input.getKey((KeyEvent.VK_SPACE))) {
            cleanUp();
        }
    }

    private void render() {
        Display.clear();
        graphics.setColor(Color.white);
        graphics.fillPolygon(xPointsMiddleLine, yPointsMiddleLine, npoints);
        graphics.fillOval((int) (xBall), (int) yBall, (int) ballWidth * 2, (int) ballHeight * 2);
        graphics.fillPolygon(xPointsFirstPlayer, yPointsFirstPlayer, npoints);
        graphics.fillPolygon(xPointsSecondPlayer, yPointsSecondPlayer, npoints);
        Display.switchBuffers();
    }

    private void setVector() {
        Random random = new Random();
        int num = random.nextInt(400000);
        if(num >= 0 && num <= 100000) {
            xVector = -(int)(speed);
            yVector = -(int)(speed);
        } else if (num > 100000 && num <= 200000) {
            xVector = -(int)(speed);
            yVector = (int)(speed);
        } else if (num > 200000 && num <= 300000) {
            xVector = (int)(speed);
            yVector = -(int)(speed);
        } else {
            xVector = (int)(speed);
            yVector = -(int)(speed);
        }
    }




    @Override
    public void run() {
        int fps = 0, upd = 0, updl = 0;

        long count = 0;

        float delta = 0;

        long lastTime = Time.get();
        while(running) {
            long now = Time.get();
            long spentTime = now - lastTime;
            lastTime = now;

            count += spentTime;

            delta += (spentTime / UPDATE_INTERVAL);
            while(delta > 1) {
                try {
                    update();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                upd++;
                delta--;
                if(render)
                    updl++;
                else
                    render = true;
            }
            if(render) {
                render();
                fps++;
            } else {
                try {
                    Thread.sleep(IDLE_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if(count >= Time.SECOND && GOAL == 0) {
//                Display.setTitle(TITLE + " || FPS: " + fps + " || UPD: " + upd + " || UPDL: " + updl);
                Display.setTitle(TITLE + " || SCORE: " + firstPlayerScore + " : " + secondPlayerScore + " || ");
                upd = 0;
                fps = 0;
                updl = 0;
                count -= Time.SECOND;
            } else if(GOAL == 1) {
                Display.setTitle("PRESS SPACE TO CONTINUE");
            } else if(GOAL == -1) {
                Display.setTitle("PRESS SPACE TO EXIT");
            }

        }
    }

    private void setStartPosition() {
        xBall = WIDTH / 2 - ballWidth;
        yBall = HEIGHT / 2 - ballHeight;
        xPointsFirstPlayer[0] = xPointsFirstPlayer[3] = 25;
        xPointsFirstPlayer[1] = xPointsFirstPlayer[2] = 30;
        yPointsFirstPlayer[0] = yPointsFirstPlayer[1] = HEIGHT / 2 + 50;
        yPointsFirstPlayer[2] = yPointsFirstPlayer[3] = HEIGHT / 2 - 50;
        xPointsSecondPlayer[0] = xPointsSecondPlayer[3] = WIDTH - 25;
        xPointsSecondPlayer[1] = xPointsSecondPlayer[2] = WIDTH - 30;
        yPointsSecondPlayer[0] = yPointsSecondPlayer[1] = HEIGHT / 2 + 50;
        yPointsSecondPlayer[2] = yPointsSecondPlayer[3] = HEIGHT / 2 - 50;

    }

    private void cleanUp() {
        Display.destroy();
    }

}
