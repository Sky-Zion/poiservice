package ru.dsci.poiservice.bot.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.dsci.poiservice.bot.telegrambot.BotKeyboard;

import java.io.File;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandStart implements IBotCommand {

    private final BotKeyboard keyboard;

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

        SendAnimation sendAnimation = SendAnimation
                .builder()
                .chatId(message.getChatId())
                .caption(CommandHelp.HELP_MESSAGE)
                .replyMarkup(keyboard.getStaticKeyboard())
                .build();
        try {
            absSender.execute(sendAnimation);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

}
