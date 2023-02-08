package ru.dsci.poiservice.bot.telegrambot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import javax.annotation.PostConstruct;
import java.io.File;

@Component
public class Constants {

    private final static String YOUTUBE_HELP_URL = "https://youtu.be/GTkHMddiGoo";

    private final static String BLANK_GMAP_A_TAG = "<a href=\"https://maps.google.com/maps/place/%f+%f/@%f\" target=\"_blank\" rel=\"noopener noreferrer\" data-entity-type=\"MessageEntityUrl\">%s</a>";

    private final static String HELP_MESSAGE =
            "\u261DЧат-бот поможет:\n" +
                    "\uD83D\uDCCCнайти ближайшие укрытия;\n" +
                    "\uD83D\uDCCCпросмотреть карту укрытий;\n" +
                    "\uD83D\uDCCCпроложить маршрут к укрытию;\n" +
                    "\uD83D\uDCCCузнать какие действия необходимо предпринять в случае возникновения чрезвычайной ситуации.\n\n" +

                    "Использование:\n" +
                    "\uD83D\uDCCCдля поиска укрытий нажмите кнопку \"Найти укрытия\" , " +
                    "в ответном сообщении вы получите список ближайших укрытий;\n" +
                    "\uD83D\uDCCCчтобы проложить маршрут, нажмите на ссылку с адресом укрытия\n" +
                    "\uD83D\uDCCCдля просмотра карты укрытий нажмите кнопку \"Карта укрытий\";\n" +
                    "\uD83D\uDCCCпросмотрите обучающий ролик \"как вести себя при обстреле\", для этого" +
                    "нажмите кнопку с соответствующим названием.\n\n" +

                    "\u2757 Проинспектируйте укрытия, изготовьте ключи, продумайте маршрут.\n\n" +

                    "\uD83D\uDE0EРазработчик: @SkyZion\n" +
                    "буду рад сотрудничеству\n\n" +
                    "Видео-инструкция: " + YOUTUBE_HELP_URL;

    private final static String ERROR_MESSAGE = "\u2757Очень сожалею, произошла непредвиденная ошибка \n" +
            "Скорее всего мы просто обновили приложение и чтобы обновления вступили в силу, необходимо перезапустить бот" +
            "<a data-entity-type=\"MessageEntityBotCommand\">/start</a>\n\n" +
            "Если перезапукск не помог, то мы точно накосячили, свжитесь с разработчиком @SkyZion";

    private final static String HOW_TO_VIDEO_CAPTION = "Как вести себя во время обстрела";

    @Value("${resources.media_path}")
    private String mediaPath;

    @Value("${resources.map_url}")
    private String mapUrl;

    private String howToPath;

    private InputFile howToVideo;

    @PostConstruct
    private void init() {
        howToPath = String.format("%s/how_to.mp4", getMediaPath());
        howToVideo = new InputFile(new File(howToPath));
    }

    public String getMediaPath() {
        return mediaPath;
    }

    public InputFile getHowToVideo() {
        return howToVideo;
    }

    public String getHowToVideoCaption() {
        return HOW_TO_VIDEO_CAPTION;
    }

    public String getMapUrl() {
        return mapUrl;
    }

    public String getBlankGmapATag() {
        return BLANK_GMAP_A_TAG;
    }

    public String getErrorText() {
        return ERROR_MESSAGE;
    }

    public String getHelpText() {
        return HELP_MESSAGE;
    }


}
