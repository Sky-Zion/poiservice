package ru.dsci.poiservice.bot.telegrambot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import javax.annotation.PostConstruct;
import java.io.File;

@Component
public class BotMedia {

    @Value("${resources.media}")
    private String mediaPath;

    private String howToPath;
    private String helpPath;

    private InputFile help;
    private InputFile howToVideo;

    @PostConstruct
    private void init() {
        howToPath = String.format("%s/how_to.mp4", mediaPath);
        helpPath = String.format("%s/help.mp4", mediaPath);
        help = new InputFile(new File(helpPath));
        howToVideo = new InputFile(new File(howToPath));
    }

    public String getMediaPath() {
        return mediaPath;
    }

    public InputFile getHelpVideo() {
        return help;
    }

    public InputFile getHowToVideo() {
        return howToVideo;
    }

}
