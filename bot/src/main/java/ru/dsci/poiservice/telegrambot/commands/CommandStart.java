package ru.dsci.poiservice.telegrambot.commands;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
        message.setText(CommandHelp.HELP_MESSAGE);
        SendMessage sendMessage = SendMessage
                .builder()
                .chatId(message.getChatId().toString())
                .text(message.getText())
                .build();
        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
