package ru.dsci.poiservice.bot.telegrambot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.dsci.poiservice.bot.commands.CommandHelp;
import ru.dsci.poiservice.bot.commands.CommandStart;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramBot extends TelegramLongPollingCommandBot {

    private final CommandStart commandStart;

    private final CommandHelp commandHelp;

    private final Constants constants;

    private final Keyboard keyboard;

    private final MessageHandler messageHandler;

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
            if (messageHandler.isUpdateHasLocation(update))
                processLocation(update);
            else {
                messageHandler.checkUpdateHasMessage(update);
                if (update.getMessage().getText().equals(Keyboard.BUTTONS.MAPS.getTitle()))
                    processMaps(update.getMessage());
                else if (update.getMessage().getText().equals(Keyboard.BUTTONS.HOW_TO_VIDEO.getTitle())) {
                    processHowTo(update.getMessage().getChat());
                } else
                    sendHelpMessage(update.getMessage().getChat());
            }
        } catch (
                RuntimeException e) {
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

    private void processHowTo(Chat chat) {
        sendVideo(chat, constants.getHowToVideo(), constants.getHowToVideoCaption());
    }

    private void processLocation(Update update) {
        StringBuilder text = new StringBuilder();
        try {
            messageHandler.checkUpdateHasLocation(update);
            Location location = update.getMessage().getLocation();
            logInfoMessage(update.getMessage().getChat(), String.format("location: [%f,%f]", location.getLatitude(), location.getLongitude()));
            text.append(messageHandler.getSheltersTextByLocation(location));
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            text.append(constants.getErrorText());
        } finally {
            sendMessage(update.getMessage().getChat(), text.toString());
        }
    }

    private void logInfoMessage(Chat chat, String message) {
        String userName;
        Long chatId = chat.getId();
        userName = String.format("%s %s",
                chat.getFirstName(),
                chat.getLastName());
        log.info("#{} ({}): {}", chatId, userName, message);
    }

    public void sendMessage(Chat chat, String text) {
        try {
            logInfoMessage(chat, String.format("SEND MESSAGE: %s", text));
            execute(messageHandler.getSendMessage(chat, text));
        } catch (TelegramApiException e) {
            log.error("SEND MESSAGE ERROR: {}", e.getMessage());
        }
    }

    public void sendVideo(Chat chat, InputFile file, String caption) {
        try {
            logInfoMessage(chat, String.format("SEND VIDEO: %s (%s)", file.getMediaName(), caption));
            execute(messageHandler.getSendVideo(chat, file, caption));
        } catch (TelegramApiException e) {
            log.error("SEND VIDEO ERROR: {}", e.getMessage());
        }
    }

    public void sendHelpMessage(Chat chat) {
        try {
            logInfoMessage(chat, "SEND HELP MESSAGE");
            execute(messageHandler.getSendMessageHelp(chat));
        } catch (TelegramApiException e) {
            log.error("SEND HELP MESSAGE ERROR: {}", e.getMessage());
        }
    }

}
