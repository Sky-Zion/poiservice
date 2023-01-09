package ru.dsci.poiservice.telegrambot;

import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.dsci.poiservice.core.mappers.PoiMapper;

import java.util.Properties;


@Configuration
@ComponentScan(basePackages = {"ru.dsci.poiservice.core"})
@EnableJpaRepositories("ru.dsci.poiservice.core.repositories")
@EntityScan({"ru.dsci.poiservice.core.entities"})
public class BotConfig {

    @Bean
    TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new PoiMapper();
    }

}
