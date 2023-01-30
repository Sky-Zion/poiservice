package ru.dsci.poiservice.bot.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

@Slf4j
@Component
public class CommandHelp implements IBotCommand {

    public final static String HELP_MESSAGE =
            "\u261DЧат-бот поможет найти ближайшие укрытия.\n\n" +

                    "Использование:\n" +
                    "\uD83D\uDCCCдля поиска укрытий нажмите кнопку \"Найти укрытия\" или отправьте геопозицию, " +
                    "в ответном сообщении вы получите список ближайших укрытий;\n" +
                    "\uD83D\uDCCCдля просмотра интерактивных карт укрытий нажмите кнопку \"Карты укрытий\"," +
                    "в ответном сообщении вы получите список интерактивных карт,\n" +
                    "чобы просмотреть карту, нажмите на соответствующий населенный пункт;\n" +
                    "\uD83D\uDCCCпросмотрите обучающий ролик \"как вести себя при обстреле, для этого\"\n" +
                    "нажмите кнопку с соответствующим названием.\n\n" +

                    "\u2757 Проинспектируйте укрытия, изготовьте ключи, продумайте маршрут.\n\n" +

                    "\uD83D\uDE0EРазработчик: @SkyZion\n" +
                    "буду рад сотрудничеству";

    public final static String HELP_LOCATION =
            "\u261DНеобходимо отправить боту вашу текущую геопозицию.";

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
                .build();
        try {
            absSender.execute(sendAnimation);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

}
