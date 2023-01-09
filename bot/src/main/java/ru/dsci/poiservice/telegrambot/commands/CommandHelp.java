package ru.dsci.poiservice.telegrambot.commands;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public class CommandHelp implements IBotCommand {

    public final static String HELP_MESSAGE = "\u261D Чат-бот служит для поиска укрытий\n" +
            "Поможет сориентироваться в ситуации во время обстрела\n\n" +
            "\uD83D\uDCA5Использование:\n" +
            "отправьте боту вашу текущую геопозицию (предпочтительнее) или адрес (с указанием города)\n" +
            "В ответ будет направлен список ближайших укрытий\n\n" +
            "Надеюсь, этот бот Вам не пригодится\n" +
            "\uD83D\uDE0EРазработчик: @SkyZion";

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
        message.setText(HELP_MESSAGE);
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
