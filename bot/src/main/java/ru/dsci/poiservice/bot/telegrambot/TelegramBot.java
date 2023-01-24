package ru.dsci.poiservice.bot.telegrambot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.dsci.poiservice.core.Constants;
import ru.dsci.poiservice.core.entities.Poi;
import ru.dsci.poiservice.core.entities.dtos.DtoOsmPoi;
import ru.dsci.poiservice.core.geomath.Point;
import ru.dsci.poiservice.core.services.OsmGeoService;
import ru.dsci.poiservice.bot.commands.CommandHelp;
import ru.dsci.poiservice.bot.commands.CommandStart;
import ru.dsci.poiservice.bot.dtos.PoiDistance;
import ru.dsci.poiservice.bot.dtos.PoiDistanceList;
import ru.dsci.poiservice.bot.services.BotShelterService;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramBot extends TelegramLongPollingCommandBot {

    private final BotShelterService botShelterService;
    private final OsmGeoService osmGeoService;

    @Value("${telegram_bot.username}")
    private String botUserName;

    @Value("${telegram_bot.token}")
    private String botToken;

    @PostConstruct
    private void init() {
        register(new CommandHelp());
        register(new CommandStart());
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
    public void processNonCommandUpdate(org.telegram.telegrambots.meta.api.objects.Update update) {
        Long chatId = update.getMessage().getChatId();
        StringBuilder text = new StringBuilder();
        Poi nearestPoi = null;
        PoiDistanceList poiDistanceList;
        Point userLocation;
        String userName;
        userName = String.format("%s %s",
                update.getMessage().getChat().getFirstName(),
                update.getMessage().getChat().getLastName());
        if (update.hasMessage() && update.getMessage() != null) try {
            if (update.getMessage().hasLocation() && update.getMessage().getLocation() != null) {
                Location location = update.getMessage().getLocation();
                log.info("#{} ({}) location: [{},{}]", chatId, userName, location.getLatitude(), location.getLongitude());
                userLocation = new Point(location.getLatitude(), location.getLongitude());
            } else {
                String address = update.getMessage().getText();
                log.info("#{} ({}) address: {}", chatId, userName, address);
                DtoOsmPoi osmPoi = osmGeoService.getByAddress(address);
                userLocation = new Point(osmPoi.getGeoLat().doubleValue(), osmPoi.getGeoLon().doubleValue());
                log.info("#{} ({}) location by address: [{},{}]", chatId, userName, userLocation.getLat(), userLocation.getLat());
            }
            poiDistanceList = botShelterService.getAllNearLocation(userLocation);
            if (poiDistanceList.size() > 0) {
                text.append(String.format("\uD83D\uDEA8Найдены укрытия (%d м):\n",
                        (int)poiDistanceList.get(poiDistanceList.size() - 1).getDistance()));
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
        } catch (Exception e) {
            log.error(e.getMessage());
            text.append("\u2757Ошибка определения местоположения");
        } finally {
            try {
                log.info("#{} ({}) response: {}", chatId, userName, text.toString());
                SendMessage sendMessage = SendMessage.builder().chatId(chatId).text(text.toString()).build();
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

}
