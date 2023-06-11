package com.ivettepe.main;

import com.ivettepe.display.Display;
import com.ivettepe.game.Game;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.AbstractSet;


// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        Game pong = new Game();
        pong.start();
    }
}