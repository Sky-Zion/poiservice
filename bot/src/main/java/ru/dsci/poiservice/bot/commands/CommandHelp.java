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
public class CommandHelp implements IBotCommand {

    public final static String HELP_MESSAGE =
            "\u261DЧат-бот служит для поиска укрытий\n" +
                    "Поможет сориентироваться в ситуации во время обстрела.\n\n" +
                    "\uD83D\uDCA5Использование:\n" +
                    "просто отправьте боту вашу текущую геопозицию,\n" +
                    "в ответ будет направлен список ближайших укрытий.\n\n" +
                    "\uD83D\uDE0EРазработчик: @SkyZion";

    public final static String HELP_LOCATION =
            "\u261DНеобходимо отправить боту вашу текущую геопозицию.";

    public final static String HELP_VIDEO_PATH = "bot/src/main/resources/media/help.mp4";

    public final static InputFile HELP_LOCATION_VIDEO = new InputFile(new File(HELP_VIDEO_PATH));


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
        SendAnimation sendAnimation = SendAnimation
                .builder()
                .chatId(message.getChatId())
                .caption(HELP_MESSAGE)
                .animation(HELP_LOCATION_VIDEO)
                .build();
        try {
            absSender.execute(sendAnimation);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

}
