package com.example.jasperrepo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@Component
@Getter
public class ReportConfig {

    private Map<String, Object> config;

    @PostConstruct
    public void loadConfig() throws IOException {
        // Получаем путь из переменной окружения или задаем путь по умолчанию
        String configPath = System.getenv("CONFIG_PATH");
        if (configPath == null || configPath.isEmpty()) {
            configPath = "C:\\Users\\Asus\\Downloads\\jasper-reports-confg\\config.json"; // Путь по умолчанию для локальной машины
        }

        // Проверяем существование файла конфигурации
        File configFile = new File(configPath);
        if (!configFile.exists()) {
            throw new IOException("Configuration file not found at path: " + configPath);
        }

        // Загружаем файл конфигурации
        ObjectMapper objectMapper = new ObjectMapper();
        config = objectMapper.readValue(configFile, Map.class);
    }
}
