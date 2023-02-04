package ru.dsci.poiservice.bot.telegrambot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class Keyboard {

    private final Constants constants;

    public enum BUTTONS {

        GET_SHELTERS("\uD83D\uDD0DНайти укрытия"),
        MAPS("\uD83C\uDF10Карта укрытий"),
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

    public ReplyKeyboardMarkup getStaticKeyboard() {
        return staticKeyboard;
    }

    private void initStaticKeyboard() {
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        Stream.of(BUTTONS.values()).forEach(b ->
        {
            KeyboardButton keyboardButton = new KeyboardButton(b.getTitle());
            keyboardButton.setText(b.getTitle());
            if (b == BUTTONS.GET_SHELTERS)
                keyboardButton.setRequestLocation(true);
            else if (b == BUTTONS.MAPS) {
                keyboardButton.setWebApp(new WebAppInfo(constants.getMapUrl()));
            }
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(keyboardButton);
            keyboardRows.add(keyboardRow);
        });
        staticKeyboard.setKeyboard(keyboardRows);
        staticKeyboard.setSelective(true);
        staticKeyboard.setResizeKeyboard(true);
        staticKeyboard.setOneTimeKeyboard(false);
    }

    @PostConstruct
    private void init() {
        initStaticKeyboard();
    }

}
