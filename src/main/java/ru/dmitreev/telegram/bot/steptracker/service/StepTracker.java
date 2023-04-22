package ru.dmitreev.telegram.bot.steptracker.service;

import java.util.Scanner;

public class StepTracker {
    private int[][] monthToData;
    private int purposeSteps = 10000;
    private Scanner scanner;
    private Converter converter = new Converter();

    public StepTracker(Scanner scanner) {
        monthToData = new int[12][30];
        this.scanner = scanner;
    }

    public void saveSteps() {
        System.out.println("За какой месяц вы хотите ввести шаги?");
        monthSelection();
        int month = this.scanner.nextInt() - 1;
        if (month < 0 || month > 11) {
            System.out.println("Неверный формат. Введите значение от 1 до 12.");
        } else {
            System.out.println("За какой день вы хотите ввести шаги?");
            System.out.println("Введите день в котором хотите сохранить количество шагов (от 1 до 30) :");
            int day = this.scanner.nextInt() - 1;
            if (day < 0 || day > 29) {
                System.out.println("Значение не корректно, введите число от 1 до 30.");
            } else {
                System.out.println("Введите пройденное количество шагов за день:");
                int steps = this.scanner.nextInt();
                if (steps < 0) {
                    System.out.println("Количество шагов не может быть отрицательным .");
                } else {
                    System.out.println("Выбран месяц " + (month + 1) + ". День " + (day + 1) + ". Количество шагов " + steps + ".");
                    monthToData[month][day] = steps;
                }
            }
        }
    }

    public void statistics() {
        int sum = 0;
        int maxSteps = 0;
        int count = 0;
        int maxSeries = 0;


        System.out.println("За какой месяц вы хотите посмотреть статистику?");
        monthSelection();
        int month = this.scanner.nextInt() - 1;
        if (month < 0) {
            System.out.println("Неверный формат. Введите значение от 1 до 12.");
            return;
        } else if (month > 11) {
            System.out.println("Неверный формат. Введите значение от 1 до 12.");
            return;
        } else {
            for (int i = 0; i < 30; i++) {

                sum += monthToData[month][i];

                if (monthToData[month][i] > maxSteps) {
                    maxSteps = monthToData[month][i];
                }
                if (monthToData[month][i] >= purposeSteps) {
                    count++;
                    if (count > maxSeries) {
                        maxSeries = count;
                    }
                } else {
                    count = 0;
                }
                System.out.print((i + 1) + " День : " + monthToData[month][i] + ", ");
            }
        }

        System.out.println();
        System.out.println("Общее количество шагов за месяц: " + sum);
        System.out.println("Максимальное количество шагов: " + maxSteps + ".");
        System.out.println("Среднее количество шагов за месяц: " + (sum / 30));
        System.out.println("Количество пройденных киллометров: " + Math.round(converter.convertInKilometres(sum)));
        System.out.println("Количество сожженных килокалорий: " + Math.round(converter.convertInKilokalories(sum)));
        System.out.println("Лучшая серия из дней превышающих целевое количество шагов " + maxSeries);


    }

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

    public void monthSelection() {
        System.out.println("Введите номер месяца в формате : 1-Январь, 2-Февраль, 3-Март, 4-Апрель, 5-Май, 6-Июнь, 7-Июль, 8-Август, 9-Сентябрь, 10-Октябрь, 11-Ноябрь, 12-Декабрь. ");
    }
}