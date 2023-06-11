package com.ivettepe.display;

import com.ivettepe.IO.Input;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.BufferedReader;
import java.nio.Buffer;
import java.util.Arrays;
import javax.swing.*;



public abstract class Display {
    private static boolean created = false;
    private static JFrame window;
    private static Canvas content;
    private static BufferedImage bufferedImage;
    private static int[] bufferedData;
    private static Graphics bufferGraphics;
    private static int clearColor;
    private static BufferStrategy bufferStrategy;


    public static void create(int width, int heigth, String title, int _clearColor, int numBuffers) {
        if(created)
            return;
        window = new JFrame(title);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        content = new Canvas();

        Dimension size = new Dimension(width, heigth);
        content.setPreferredSize(size);

        window.setResizable(false);
        window.getContentPane().add(content);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        bufferedImage = new BufferedImage(width, heigth, BufferedImage.TYPE_INT_ARGB);
        bufferedData = ((DataBufferInt)bufferedImage.getRaster().getDataBuffer()).getData();
        bufferGraphics = bufferedImage.getGraphics();
        ((Graphics2D)bufferGraphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        clearColor = _clearColor;

        content.createBufferStrategy(numBuffers);
        bufferStrategy = content.getBufferStrategy();

        created = true;
    }
    public static void clear() {
        Arrays.fill(bufferedData, clearColor);
    }


    public static void switchBuffers() {
        Graphics graphics = bufferStrategy.getDrawGraphics();
        graphics.drawImage(bufferedImage, 0, 0, null);
        bufferStrategy.show();
    }

    public static Graphics2D getGraphics() {
        return (Graphics2D) bufferGraphics;
    }

    public static void setTitle(String title) {
        window.setTitle(title);
    }

    public static void addInputListener(Input inputListener) {
        window.add(inputListener);
    }

    public static void destroy() {
        if(!created)
            return;
        else
            window.dispose();
    }
}
