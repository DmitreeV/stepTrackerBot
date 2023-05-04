package ru.dmitreev.telegram.bot.steptracker.repo;

public enum TextMessage {

    MENU_TEXT("Приветствую, что вы хотите сделать? \n" +
            "1 - Ввести количество шагов за определённый день \n" +
            "2 - Напечатать статистику за определённый месяц \n" +
            "3 - Изменить цель по количеству шагов в день \n" +
            "0 - Получить информацию о использовании бота"),

    HELP_TEXT("Чтобы использовать данного бота нужно учесть : \n" +
            " - Цель шагов не может быть отрицательной и не может превышать 100000 \n" +
            " - Количество шагов не может быть отрицательным и не может превышать 100000 \n" +
            " - Количество дней в месяце варьируется строго от 1 до 30 \n\n" +
            "Нажмите /menu чтобы вернуться к главному меню"),

    ONE_BUTTON("Вы нажали кнопку - 1"),

    TWO_BUTTON("Вы нажали кнопку - 2"),

    THREE_BUTTON("Вы нажали кнопку - 3"),

    ZERO_BUTTON("Вы нажали кнопку - 0"),

    CHOOSE_MONTH("За какой месяц вы хотите ввести шаги? \n" +
            "Выберите номер месяца : \n" +
            "1-Январь, 2-Февраль, 3-Март, \n" +
            "4-Апрель, 5-Май, 6-Июнь, \n" +
            "7-Июль, 8-Август, 9-Сентябрь, \n" +
            "10-Октябрь, 11-Ноябрь, 12-Декабрь."),

    CHOOSE_DAY("За какой день вы хотите ввести шаги? \n" +
            "Выберите номер дня : "),

    ENTERING_STEPS("Введите пройденное количество шагов за день в формате \n" +
            "< n шагов > :"),

    STATS_FOR_MONTH("За какой месяц вы хотите посмотреть статистику? \n" +
            "Введите номер месяца в формате \n" +
            "< месяц n > :"),

    NEW_GOAL("Введите новую цель шагов в формате \n" +
            "< цель n > :"),

    NO_SUCH_COMMAND("Извините, такой команды пока нет.");

    private final String message;

    TextMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
