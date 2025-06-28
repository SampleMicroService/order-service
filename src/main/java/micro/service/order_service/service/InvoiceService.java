package micro.service.order_service.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import micro.service.order_service.entity.Order;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class InvoiceService {

    @Value("${invoice.path.file}")
    private String invoicePath;

    // JRXML template and logo resources on classpath
    @Value("classpath:jasper/invoice.jrxml")
    private Resource invoiceTemplate;

    @Value("classpath:images/logo.png")
    private Resource logo;

    public String generateInvoice(Order order, String customerName) 
            throws JRException, IOException {

        // 1. Compile the JRXML template
        JasperReport jasperReport;
        try (var templateStream = invoiceTemplate.getInputStream()) {
            jasperReport = JasperCompileManager.compileReport(templateStream);
        }

        // 2. Prepare report parameters
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("logo", logo.getInputStream());
        parameters.put("customerName", customerName);

        // 3. Create data source (Jasper expects a collection)
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(List.of(order));

        // 4. Fill the report
        JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport,
                parameters,
                dataSource
        );

        // 5. Ensure output directory exists
        Path outputDir = Path.of(invoicePath);
        Files.createDirectories(outputDir);

        // 6. Export report to PDF file
        String fileName = String.format("invoice_%s.pdf", order.getId());
        String outputPath = outputDir.resolve(fileName).toString();
        JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);

        return outputPath;
    }

}
