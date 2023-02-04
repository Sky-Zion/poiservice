package ru.dsci.poiservice.bot.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.dsci.poiservice.bot.telegrambot.Keyboard;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandStart implements IBotCommand {

    private final Keyboard keyboard;

    @Override
    public String getCommandIdentifier() {
        return "start";
    }

    @Override
    public String getDescription() {
        return "Старт";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {

        SendMessage sendMessage = SendMessage
                .builder()
                .chatId(message.getChatId())
                .text(CommandHelp.HELP_MESSAGE)
                .replyMarkup(keyboard.getStaticKeyboard())
                .build();
        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

}
