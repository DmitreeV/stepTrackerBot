package ru.dmitreev.telegram.bot.steptracker.repo;

public class TextMessage {

    public static String MENU_TEXT = "Приветствую, что вы хотите сделать? \n" +
            "1 - Ввести количество шагов за определённый день \n" +
            "2 - Напечатать статистику за определённый месяц \n" +
            "3 - Изменить цель по количеству шагов в день \n" +
            "0 - Получить информацию о использовании бота";

    public static String HELP_TEXT = "Чтобы использовать данного бота нужно учесть : \n" +
            " - Цель шагов не может быть отрицательной \n" +
            " - Количество шагов не может быть отрицательным \n" +
            " - Количество дней в месяце варьируется строго от 1 до 30 \n\n" +
            "Нажмите /menu чтобы вернуться к главному меню";

    public static String ONE_BUTTON = "Вы нажали кнопку - 1";
    public static String TWO_BUTTON = "Вы нажали кнопку - 2";
    public static String THREE_BUTTON = "Вы нажали кнопку - 3";
    public static String ZERO_BUTTON = "Вы нажали кнопку - 0";

}
