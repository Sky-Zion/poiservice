package ru.dsci.poiservice.telegrambot.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.LineIterator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.dsci.poiservice.core.entities.Poi;
import ru.dsci.poiservice.core.entities.dtos.DtoOsmPoi;
import ru.dsci.poiservice.core.geomath.Point;
import ru.dsci.poiservice.core.services.OsmGeoService;
import ru.dsci.poiservice.telegrambot.commands.CommandHelp;
import ru.dsci.poiservice.telegrambot.commands.CommandStart;
import ru.dsci.poiservice.telegrambot.dtos.PoiDistance;
import ru.dsci.poiservice.telegrambot.dtos.PoiDistanceList;
import ru.dsci.poiservice.telegrambot.services.BotShelterService;

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
        if (update.hasMessage() && update.getMessage() != null) try {
            if (update.getMessage().hasLocation() && update.getMessage().getLocation() != null) {
                Location location = update.getMessage().getLocation();
                userLocation = new Point(location.getLatitude(), location.getLongitude());
            } else {
                String address = update.getMessage().getText();
                DtoOsmPoi osmPoi = osmGeoService.getByAddress(address);
                if (osmPoi == null)
                    throw new EntityNotFoundException(String.format("ошибка определения местоположения: %s", address));
                userLocation = new Point(osmPoi.getGeoLat().doubleValue(), osmPoi.getGeoLon().doubleValue());
            }
            poiDistanceList = botShelterService.getAllNearLocation(userLocation);
            if (poiDistanceList.size() < 1)
                throw new EntityNotFoundException("\uD83D\uDE16укрытия поблизости не найдены");
            if (poiDistanceList.size() > 1) {
                text.append("\uD83D\uDEA8Найдены укрытия:\n");
                for (int i = 0; i < poiDistanceList.size(); i++) {
                    PoiDistance poiDistance = poiDistanceList.get(i);
                    text.append(String.format("%d: %s (%d м)\n",
                            i + 1, poiDistance.getPoi().getAddress(), (int) (poiDistance.getDistance())));
                }
            }
            nearestPoi = poiDistanceList.get(0).getPoi();
            text.append(String.format("\uD83D\uDCCDБлижайшее укрытие: %s", nearestPoi.getDescription()));
        } catch (EntityNotFoundException e) {
            text.append(String.format("%s", e.getMessage()));
        } finally {
            try {
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
