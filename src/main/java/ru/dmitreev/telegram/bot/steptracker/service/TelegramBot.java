package ru.dmitreev.telegram.bot.steptracker.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.dmitreev.telegram.bot.steptracker.config.TelegramBotConfig;

import java.util.ArrayList;
import java.util.List;

import static ru.dmitreev.telegram.bot.steptracker.repo.TextMessage.*;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    TelegramBotConfig config;

    @Value("${bot.token}")
    private String botToken;
    @Value("${bot.name}")
    private String botName;

    int month;
    int day;
    int steps;

    private int purposeSteps = 10000;
    private int[][] monthToData;

    private Converter converter = new Converter();

    StepTracker stepTracker = new StepTracker();

    TelegramBot(TelegramBotConfig config) {
        this.config = config;
        monthToData = new int[12][30];
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            parseMessage(message, chatId);
            saveSteps(chatId, message);
            saveMonthAndDay(chatId, 0, message);

        } else if (update.hasCallbackQuery()) {
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            String message = update.getCallbackQuery().getData();
            sendMessage(chatId, message);
            parseMessage(message, chatId);
            saveMonthAndDay(chatId, messageId, message);
        }
    }

    private void saveMonthAndDay(long chatId, long messageId, String message) {
        for (int i = 1; i < 13; i++) {
            if (message.equals("Выбран месяц " + i)) {
                month = i;
                selectDay(chatId, messageId);
            } else if (message.toLowerCase().equals("месяц " + i)) {
                month = i;
                statistics(chatId);
            }
        }

        for (int i = 1; i < 31; i++) {
            if (message.equals("Выбран день " + i)) {
                day = i;
                sendMessage(chatId, ENTERING_STEPS.getMessage());
            }
        }
    }

    private void saveSteps(long chatId, String message) {
        for (int i = 1; i < 100000; i++) {
            if (message.equals(i + " шагов")) {
                steps = i;
                sendMessage(chatId, "Выбран месяц " + month + ". День " + day + ". Количество шагов " + steps + ". \n\n" +
                        "Нажмите /menu чтобы вернуться в главное меню. \n" +
                        "Нажмите /steps чтобы добавить новые данные о шагах.");
                monthToData[month][day] = steps;

            } else if (message.toLowerCase().equals("цель " + i)) {
                purposeSteps = i;
                sendMessage(chatId, "Ваша новая цель шагов : " + purposeSteps + ". \n\n" +
                        "Нажмите /menu чтобы вернуться в главное меню.");
            }
        }
    }

    private void printMenu(long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton but1 = new InlineKeyboardButton();
        InlineKeyboardButton but2 = new InlineKeyboardButton();
        InlineKeyboardButton but3 = new InlineKeyboardButton();
        InlineKeyboardButton but4 = new InlineKeyboardButton();

        but1.setText("1");
        but1.setCallbackData(ONE_BUTTON.getMessage());
        but2.setText("2");
        but2.setCallbackData(TWO_BUTTON.getMessage());
        but3.setText("3");
        but3.setCallbackData(THREE_BUTTON.getMessage());
        but4.setText("0");
        but4.setCallbackData(ZERO_BUTTON.getMessage());

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();

        keyboardButtonsRow1.add(but1);
        keyboardButtonsRow1.add(but2);
        keyboardButtonsRow2.add(but3);
        keyboardButtonsRow2.add(but4);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);

        inlineKeyboardMarkup.setKeyboard(rowList);
        sendMessageWithKeyboard(chatId, MENU_TEXT.getMessage(), inlineKeyboardMarkup);
    }

    private void parseMessage(String messageText, long chatId) {
        switch (messageText) {
            case "/start":
            case "/menu":
                printMenu(chatId);
                break;
            case "Вы нажали кнопку - 1":
            case "/steps":
                selectMonth(chatId, CHOOSE_MONTH.getMessage());
                break;
            case "Вы нажали кнопку - 2":
            case "/stats":
                sendMessage(chatId, STATS_FOR_MONTH.getMessage());
                break;
            case "Вы нажали кнопку - 3":
                sendMessage(chatId, NEW_GOAL.getMessage());
                break;
            case "Вы нажали кнопку - 0":
                sendMessage(chatId, HELP_TEXT.getMessage());
                break;
            default:
                //sendMessage(chatId, NO_SUCH_COMMAND.getMessage());
        }
    }

    private void selectMonth(long chatId, String text) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton[] buttons = new InlineKeyboardButton[13];

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();

        for (int i = 1; i < 13; i++) {
            buttons[i] = new InlineKeyboardButton("i");
        }

        for (int j = 1; j < 7; j++) {
            buttons[j].setText(String.valueOf(j));
            buttons[j].setCallbackData("Выбран месяц " + j);
            keyboardButtonsRow1.add(buttons[j]);
        }

        for (int j = 7; j < 13; j++) {
            buttons[j].setText(String.valueOf(j));
            buttons[j].setCallbackData("Выбран месяц " + j);
            keyboardButtonsRow2.add(buttons[j]);
        }

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);

        inlineKeyboardMarkup.setKeyboard(rowList);
        sendMessageWithKeyboard(chatId, text, inlineKeyboardMarkup);
    }

    private void selectDay(long chatId, long messageId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton[] buttons = new InlineKeyboardButton[31];

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow4 = new ArrayList<>();

        for (int i = 1; i < 31; i++) {
            buttons[i] = new InlineKeyboardButton("i");
        }

        for (int j = 1; j < 9; j++) {
            buttons[j].setText(String.valueOf(j));
            buttons[j].setCallbackData("Выбран день " + j);
            keyboardButtonsRow1.add(buttons[j]);
        }

        for (int j = 8; j < 17; j++) {
            buttons[j].setText(String.valueOf(j));
            buttons[j].setCallbackData("Выбран день " + j);
            keyboardButtonsRow2.add(buttons[j]);
        }

        for (int j = 16; j < 25; j++) {
            buttons[j].setText(String.valueOf(j));
            buttons[j].setCallbackData("Выбран день " + j);
            keyboardButtonsRow3.add(buttons[j]);
        }

        for (int j = 24; j < 31; j++) {
            buttons[j].setText(String.valueOf(j));
            buttons[j].setCallbackData("Выбран день " + j);
            keyboardButtonsRow4.add(buttons[j]);
        }

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);
        rowList.add(keyboardButtonsRow4);

        inlineKeyboardMarkup.setKeyboard(rowList);
        executeEditMessageText(CHOOSE_DAY.getMessage(), chatId, messageId, inlineKeyboardMarkup);
    }

    private void statistics(long chatId) {
        int sum = 0;
        int maxSteps = 0;
        int count = 0;
        int maxSeries = 0;

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
        }
        sendMessage(chatId, "Общее количество шагов за месяц: " + sum + "\n" +
                "Максимальное количество шагов: " + maxSteps + "\n" +
                "Среднее количество шагов за месяц: " + (sum / 30) + "\n" +
                "Количество пройденных киллометров: " + Math.round(converter.convertInKilometres(sum)) + "\n" +
                "Количество сожженных килокалорий: " + Math.round(converter.convertInKilokalories(sum)) + "\n" +
                "Лучшая серия из дней превышающих целевое количество шагов " + maxSeries + ". \n\n" +
                "Нажмите /menu чтобы вернуться в главное меню. \n" +
                "Нажмите /stats чтобы посмотреть статистику за другой месяц.");
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        executeMessage(message);
    }

    private void sendMessageWithKeyboard(long chatId, String textToSend, InlineKeyboardMarkup ikm) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        message.setReplyMarkup(ikm);

        executeMessage(message);
    }

    private void executeEditMessageText(String text, long chatId, long messageId, InlineKeyboardMarkup ikm) {
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId((int) messageId);
        message.setReplyMarkup(ikm);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred:" + e.getMessage());
        }
    }

    public void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred:" + e.getMessage());
        }
    }
}
