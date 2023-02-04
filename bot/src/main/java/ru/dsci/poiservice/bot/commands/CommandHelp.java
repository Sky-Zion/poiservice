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
public class CommandHelp implements IBotCommand {

    private final Keyboard keyboard;

    public final static String HELP_MESSAGE =
            "\u261DЧат-бот поможет:\n" +
                    "\uD83D\uDCCCнайти ближайшие укрытия;\n" +
                    "\uD83D\uDCCCпросмотреть карту укрытий;\n" +
                    "\uD83D\uDCCCпроложить маршрут к укрытию;\n" +
                    "\uD83D\uDCCCузнать какие действия необходимо предпринять в случае возникновения чрезвычайной ситуации.\n\n" +

                    "Использование:\n" +
                    "\uD83D\uDCCCдля поиска укрытий нажмите кнопку \"Найти укрытия\" , " +
                    "в ответном сообщении вы получите список ближайших укрытий;\n" +
                    "\uD83D\uDCCCчтобы проложить маршрут, необходимо нажать на ссылку с адресом укрытия\n" +
                    "\uD83D\uDCCCдля просмотра карты укрытий нажмите кнопку \"Карта укрытий\";\n" +
                    "\uD83D\uDCCCпросмотрите обучающий ролик \"как вести себя при обстреле, для этого\"\n" +
                    "нажмите кнопку с соответствующим названием.\n\n" +

                    "\u2757 Проинспектируйте укрытия, изготовьте ключи, продумайте маршрут.\n\n" +

                    "\uD83D\uDE0EРазработчик: @SkyZion\n" +
                    "буду рад сотрудничеству";

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
                .text(HELP_MESSAGE)
                .replyMarkup(keyboard.getStaticKeyboard())
                .build();
        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

}
