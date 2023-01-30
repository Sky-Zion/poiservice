package ru.dsci.poiservice.bot.telegrambot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultLocation;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.dsci.poiservice.bot.dtos.PoiDistanceList;
import ru.dsci.poiservice.core.entities.Map;
import ru.dsci.poiservice.core.services.MapService;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class BotKeyboard {

    private final MapService mapService;

    public enum BUTTONS {

        GET_SHELTERS("\uD83D\uDD0DНайти укрытия"),
        MAPS("\uD83C\uDF10Карты укрытий"),
        HOW_TO_VIDEO("\uD83C\uDFA6Как вести себя при обстреле");

        private final String title;

        public String getTitle() {
            return title;
        }

        BUTTONS(String title) {
            this.title = title;
        }

    }

    ReplyKeyboardMarkup staticKeyboard = new ReplyKeyboardMarkup();

    InlineKeyboardMarkup mapsInlineKeyboard = new InlineKeyboardMarkup();

    public ReplyKeyboardMarkup getStaticKeyboard() {
        return staticKeyboard;
    }

    public InlineKeyboardMarkup getMapsInlineKeyboard() {
        return mapsInlineKeyboard;
    }

    private void initStaticKeyboard() {
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        Stream.of(BUTTONS.values()).forEach(b ->
        {
            KeyboardButton keyboardButton = new KeyboardButton(b.getTitle());
            keyboardButton.setText(b.getTitle());
            if (b == BUTTONS.GET_SHELTERS)
                keyboardButton.setRequestLocation(true);
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(keyboardButton);
            keyboardRows.add(keyboardRow);
        });
        staticKeyboard.setKeyboard(keyboardRows);
        staticKeyboard.setSelective(true);
        staticKeyboard.setResizeKeyboard(true);
        staticKeyboard.setOneTimeKeyboard(false);
    }

    private void initMapsInlineKeyboard() {
        List<Map> maps = mapService.getAll();
        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        maps.forEach(m -> {
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setCallbackData(m.getCode());
            button.setText(m.getTitle());
            button.setUrl(m.getUrl());
            buttons.add(button);
            keyboardRows.add(buttons);
        });
        mapsInlineKeyboard.setKeyboard(keyboardRows);
    }

    public InlineKeyboardMarkup getPoiInlineKeyboard(PoiDistanceList poiDistanceList) {
        InlineKeyboardMarkup poiKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        poiDistanceList.getPoiDistanceList().forEach(p -> {
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            String address = p.getPoi().getAddress();
            String [] addressChunks = address.split(",");
            if (addressChunks.length > 2) {
                StringBuilder addressBuilder = new StringBuilder();
                for (int i = 2; i < addressChunks.length; i++) {
                    addressBuilder.append(addressChunks[i] + " ");
                }
                address = addressBuilder.toString();
            }
            InlineQueryResultLocation location = new InlineQueryResultLocation();
            location.setLatitude(p.getPoi().getGeoLat().floatValue());
            location.setLatitude(p.getPoi().getGeoLon().floatValue());
            location.setTitle(String.format("%s (%d м)", address, (int) p.getDistance()));
            button.setText(String.format("%s (%d м)", address, (int) p.getDistance()));
            button.setCallbackData(String.format("%f,%f", p.getPoi().getGeoLat(), p.getPoi().getGeoLon()));
            buttons.add(button);
            keyboardRows.add(buttons);
            poiKeyboard.setKeyboard(keyboardRows);
        });
        return poiKeyboard;
    }

    @PostConstruct
    private void init() {
        initStaticKeyboard();
        initMapsInlineKeyboard();
    }

}
