package ru.dsci.poiservice.bot.commands;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

@Slf4j
public class CommandStart implements IBotCommand {

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
                .animation(new InputFile(new File(CommandHelp.HELP_VIDEO_PATH)))
                .build();
        try {
            absSender.execute(sendAnimation);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

}
