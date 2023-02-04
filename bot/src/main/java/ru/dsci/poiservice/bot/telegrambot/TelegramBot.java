package ru.dsci.poiservice.bot.telegrambot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.dsci.poiservice.bot.commands.CommandHelp;
import ru.dsci.poiservice.bot.commands.CommandStart;
import ru.dsci.poiservice.bot.services.BotShelterService;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramBot extends TelegramLongPollingCommandBot {

    private final CommandStart commandStart;

    private final CommandHelp commandHelp;

    private final Constants constants;

    private final Keyboard keyboard;

    private final ContentHelper contentHelper;

    @Value("${telegram_bot.username}")
    private String botUserName;

    @Value("${telegram_bot.token}")
    private String botToken;

    @PostConstruct
    private void init() {
        register(commandStart);
        register(commandHelp);
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        try {
            if (update.hasMessage()) {
                Long chatId = update.getMessage().getChatId();
                Message message = update.getMessage();
                if (update.getMessage().hasLocation() && update.getMessage().getLocation() != null) {
                    processLocation(update);
                } else {
                    if (message.getText().equals(Keyboard.BUTTONS.MAPS.getTitle())) {
                        processMaps(message);
                    } else if (message.getText().equals(Keyboard.BUTTONS.HOW_TO_VIDEO.getTitle())) {
                        processHowTo(message);
                    } else
                        helpReply(chatId);
                }

            }
        } catch (RuntimeException e) {
            log.error(e.getMessage());
        }
    }

    private void processMaps(Message message) {
        try {
            SendMessage sendMessage = SendMessage.builder().chatId(message.getChatId()).text("Карты укрытий:").build();
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void processHowTo(Message message) {
        try {
            SendVideo sendVideo = SendVideo.builder().chatId(message.getChatId()).video(constants.getHowToVideo()).build();
            execute(sendVideo);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void processLocation(Update update) {
        Long chatId;
        StringBuilder text = new StringBuilder();

        String userName;
        Message message = update.getMessage();
        chatId = message.getChatId();
        userName = String.format("%s %s",
                message.getChat().getFirstName(),
                message.getChat().getLastName());
        Location location = message.getLocation();
        try {
            if (!message.hasLocation() || message.getLocation() == null) {
                throw new RuntimeException("Location is missing");
            }
            log.info("#{} ({}) location: [{},{}]", chatId, userName, location.getLatitude(), location.getLongitude());
            text.append(contentHelper.getSheltersTextByLocation(location));
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            text.append(constants.getErrorMessage());
        } finally {
            try {
                log.info("#{} ({}) response: {}", chatId, userName, text);
                SendMessage sendMessage = SendMessage.builder().chatId(chatId).text(text.toString()).parseMode("HTML").build();
                sendMessage.setReplyMarkup(keyboard.getStaticKeyboard());
                execute(sendMessage);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }
    }

    private void helpReply(Long chatId) {
        try {
            SendMessage sendMessage = SendMessage
                    .builder()
                    .chatId(chatId)
                    .text(CommandHelp.HELP_MESSAGE)
                    .replyMarkup(keyboard.getStaticKeyboard())
                    .build();
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

}
