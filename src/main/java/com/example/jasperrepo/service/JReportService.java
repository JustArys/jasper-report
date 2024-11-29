package com.example.jasperrepo.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class JReportService {

    // Получаем путь к папке отчетов из переменной окружения или используем путь по умолчанию
    private static final String REPORT_DIRECTORY = System.getenv("REPORT_DIRECTORY") != null ?
            System.getenv("REPORT_DIRECTORY") : "C:\\Users\\Asus\\Downloads\\jasper-reports-confg";

    public void exportJasperReport(String name, Map<String, Object> data, HttpServletResponse response) throws JRException, IOException {
        try {
            // Оборачиваем данные в список, так как JRMapCollectionDataSource ожидает коллекцию
            List<Map<String, ?>> dataList = Collections.singletonList(data);
            JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(dataList);

            // Загружаем файл отчета из внешней директории
            String reportPath = REPORT_DIRECTORY + File.separator + name + ".jrxml";
            InputStream reportStream;
            try {
                reportStream = new FileInputStream(reportPath);
            } catch (Exception e) {
                throw new JRException("Failed to load report from path: " + reportPath + ". Error: " + e.getMessage());
            }

            // Компиляция отчета
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            // Заполнение отчета параметрами и источником данных
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, data, dataSource);

            // Установка заголовков ответа и экспорт в PDF
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=" + name + "_report.pdf");
            response.setHeader("Cache-Control", "no-cache");

            // Экспорт в PDF-поток
            JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
            response.getOutputStream().flush();
            response.getOutputStream().close();

        } catch (Exception e) {
            // Обработка ошибок перед отправкой ответа
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Report generation error: " + e.getMessage());
            }
            e.printStackTrace();
        }
    }
}