package ru.dsci.poiservice.bot.telegrambot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.dsci.poiservice.bot.commands.CommandHelp;
import ru.dsci.poiservice.bot.commands.CommandStart;
import ru.dsci.poiservice.bot.dtos.PoiDistance;
import ru.dsci.poiservice.bot.dtos.PoiDistanceList;
import ru.dsci.poiservice.bot.services.BotShelterService;
import ru.dsci.poiservice.core.entities.Poi;
import ru.dsci.poiservice.core.geomath.Point;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramBot extends TelegramLongPollingCommandBot {

    private final BotShelterService botShelterService;

    private final BotKeyboard botKeyboard;

    private final CommandStart commandStart;

    private final CommandHelp commandHelp;

    private final BotMedia botMedia;

    @Value("${telegram_bot.username}")
    private String botUserName;

    @Value("#{new Integer('${poi.max_results}')}")
    private Integer limit;

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
            if (update.hasMessage()) {
                Long chatId = update.getMessage().getChatId();
                Message message = update.getMessage();
                if (update.getMessage().hasLocation() && update.getMessage().getLocation() != null) {
                    processLocation(update);
                } else {
                    if (message.getText().equals(BotKeyboard.BUTTONS.MAPS.getTitle())) {
                        processMaps(message);
                    } else if (message.getText().equals(BotKeyboard.BUTTONS.HOW_TO_VIDEO.getTitle())) {
                        processHowTo(message);
                    } else
                        helpReply(chatId);
                }

            }
        } catch (RuntimeException e) {
            log.error(e.getMessage());
        }
    }

    private void processMaps(Message message) {
        try {
            SendMessage sendMessage = SendMessage.builder().chatId(message.getChatId()).text("Карты укрытий:").build();
            sendMessage.setReplyMarkup(botKeyboard.getMapsInlineKeyboard());
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void processHowTo(Message message) {
        try {
            SendVideo sendVideo = SendVideo.builder().chatId(message.getChatId()).video(botMedia.getHowToVideo()).build();
            execute(sendVideo);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void processLocation(Update update) {
        Long chatId;
        StringBuilder text = new StringBuilder();
        Poi nearestPoi = null;
        PoiDistanceList poiDistanceList;
        Point userLocation;
        String userName;
        Message message = update.getMessage();
        chatId = message.getChatId();
        userName = String.format("%s %s",
                message.getChat().getFirstName(),
                message.getChat().getLastName());
        Location location = message.getLocation();
        InlineKeyboardMarkup poiKeyboard = null;
        try {
            if (!message.hasLocation() || message.getLocation() == null) {
                throw new RuntimeException("Location is missing");
            }
            log.info("#{} ({}) location: [{},{}]", chatId, userName, location.getLatitude(), location.getLongitude());
            userLocation = new Point(location.getLatitude(), location.getLongitude());
            poiDistanceList = botShelterService.getAllNearLocation(userLocation, limit);
            if (poiDistanceList.size() > 0) {
                text.append(String.format("\uD83D\uDEA8Найдены укрытия (%d м):\n",
                        (int) poiDistanceList.get(poiDistanceList.size() - 1).getDistance()));

                for (int i = 0; i < poiDistanceList.size(); i++) {
                    PoiDistance poiDistance = poiDistanceList.get(i);
                    text.append(String.format("%d: %s (%d м)\n",
                            i + 1, poiDistance.getPoi().getAddress(), (int) (poiDistance.getDistance())));
                }
                nearestPoi = poiDistanceList.get(0).getPoi();
                text.append(String.format("\uD83D\uDCCDБлижайшее укрытие: %s", nearestPoi.getDescription()));
            } else {
                String response = "\uD83D\uDE16Укрытия поблизости не найдены";
                text.append(response);
            }
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            text.append("\u2757Ошибка определения местоположения");
        } finally {
            try {
                log.info("#{} ({}) response: {}", chatId, userName, text);
                SendMessage sendMessage = SendMessage.builder().chatId(chatId).text(text.toString()).build();
                sendMessage.setReplyMarkup(poiKeyboard);
                execute(sendMessage);
                if (nearestPoi != null) {
                    SendLocation sendLocation = SendLocation.builder().chatId(chatId).latitude(nearestPoi.getGeoLat().doubleValue()).longitude(nearestPoi.getGeoLon().doubleValue()).build();
                    execute(sendLocation);
                }
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }
    }

    private void helpReply(Long chatId) {
        try {
            SendAnimation sendAnimation = SendAnimation
                    .builder()
                    .chatId(chatId)
                    .caption(CommandHelp.HELP_LOCATION)
                    .build();
            execute(sendAnimation);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

}
