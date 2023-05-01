package ru.dmitreev.telegram.bot.steptracker.service;

import java.util.Scanner;

public class StepTracker {
    private int purposeSteps = 10000;
    private Scanner scanner;

    public int newPurposeSteps() {

        System.out.println("Ваша дневная цель шагов : " + purposeSteps);
        System.out.println("Введите новую цель шагов : ");
        int goalSteps = scanner.nextInt();

        if (goalSteps < 0) {
            System.out.println("Цель шагов не может быть отрицательной, введите корректное значение!");
        } else {
            purposeSteps = goalSteps;
            System.out.println("Ваша новая цель шагов : " + purposeSteps + ".");

        }
        return purposeSteps;
    }
}