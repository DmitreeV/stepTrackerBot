package ru.dmitreev.telegram.bot.steptracker.service;

public class Converter {
    private final double STEP_KM = 0.00075;
    private final double STEP_CAL = 0.50;


    public double convertInKilometres(int sum) {

        return sum * STEP_KM;
    }

    public double convertInKilokalories(int sum) {

        return sum * STEP_CAL;
    }
}