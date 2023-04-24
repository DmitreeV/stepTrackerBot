package ru.dmitreev.telegram.bot.steptracker.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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

    StepTracker stepTracker = new StepTracker();

    TelegramBot(TelegramBotConfig config) {
        this.config = config;
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

        } else if (update.hasCallbackQuery()) {
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            String message = update.getCallbackQuery().getData();
            sendMessage(chatId, message);
            parseMessage(message, chatId);
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
                saveSteps(chatId);
                break;
            case "Вы нажали кнопку - 2":
                stepTracker.statistics();
                break;
            case "Вы нажали кнопку - 3":
                stepTracker.newPurposeSteps();
                break;
            case "Вы нажали кнопку - 0":
                sendMessage(chatId, HELP_TEXT.getMessage());
                break;
            default:
                sendMessage(chatId, NO_SUCH_COMMAND.getMessage());
        }
    }

    private void saveSteps(long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton[] buttons = new InlineKeyboardButton[13];

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();

        for (int i = 1; i < 13; i++) {
            buttons[i] = new InlineKeyboardButton("i");
        }

        for (int j = 1; j < 7; j++) {
            buttons[j].setText(String.valueOf(j));
            buttons[j].setCallbackData(String.valueOf(j));
            keyboardButtonsRow1.add(buttons[j]);
        }

        for (int j = 7; j < 13; j++) {
            buttons[j].setText(String.valueOf(j));
            buttons[j].setCallbackData(String.valueOf(j));
            keyboardButtonsRow2.add(buttons[j]);
        }

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);

        inlineKeyboardMarkup.setKeyboard(rowList);
        sendMessageWithKeyboard(chatId, CHOOSE_MONTH.getMessage(), inlineKeyboardMarkup);

//            System.out.println("За какой день вы хотите ввести шаги?");
//            System.out.println("Введите день в котором хотите сохранить количество шагов (от 1 до 30) :");
//            int day = this.scanner.nextInt() - 1;
//            if (day < 0 || day > 29) {
//                System.out.println("Значение не корректно, введите число от 1 до 30.");
//            } else {
//                System.out.println("Введите пройденное количество шагов за день:");
//                int steps = this.scanner.nextInt();
//                if (steps < 0) {
//                    System.out.println("Количество шагов не может быть отрицательным .");
//                } else {
//                    System.out.println("Выбран месяц " + (month + 1) + ". День " + (day + 1) + ". Количество шагов " + steps + ".");
//                    monthToData[month][day] = steps;
//                }
//            }
//        }
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

    public void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred:" + e.getMessage());
        }
    }
}
