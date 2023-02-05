package ru.dsci.poiservice.bot.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.dsci.poiservice.bot.telegrambot.Constants;
import ru.dsci.poiservice.bot.telegrambot.Keyboard;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandHelp implements IBotCommand {

    private final Keyboard keyboard;

    private final Constants constants;

    @Override
    public String getCommandIdentifier() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "помощь";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        SendMessage sendMessage = SendMessage
                .builder()
                .chatId(message.getChatId())
                .text(constants.getHelpText())
                .parseMode("HTML")
                .replyMarkup(keyboard.getStaticKeyboard())
                .build();
        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

}
