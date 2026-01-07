package com.lsptddi.silsp.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Map;

@Service
public class PdfService {

    @Autowired
    private TemplateEngine templateEngine;

    public byte[] generatePdf(String templateName, Map<String, Object> data) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            // 1. Render HTML dengan Data
            Context context = new Context();
            context.setVariables(data);
            String htmlContent = templateEngine.process(templateName, context);

            // 2. Konversi HTML ke PDF
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();

            // --- PERBAIKAN: Menangani Base URI ---
            // Kita ambil URL folder 'static' agar gambar logo bisa terbaca
            String baseUri = "";
            try {
                // Mencoba mengambil path folder static dari classpath
                URL staticUrl = getClass().getResource("/static/");
                if (staticUrl != null) {
                    baseUri = staticUrl.toExternalForm();
                } else {
                    // Fallback ke root jika folder static tidak terdeteksi langsung (jar mode)
                    URL rootUrl = getClass().getResource("/");
                    if (rootUrl != null) {
                        baseUri = rootUrl.toExternalForm();
                    }
                }
            } catch (Exception e) {
                // Ignore jika gagal ambil baseUri, gambar mungkin broken tapi PDF tetap tergenerate
                System.err.println("Warning: Gagal mengambil Base URI untuk PDF: " + e.getMessage());
            }

            // GANTI INI:
            // builder.useBaseUrl(baseUri);  <-- Ini yang bikin error
            // builder.withHtmlContent(htmlContent, null);

            // JADI INI (Gabungkan HTML dan BaseURI di satu method):
            builder.withHtmlContent(htmlContent, baseUri);

            builder.toStream(os);
            builder.run();

            return os.toByteArray();
        } catch (Exception e) {
            e.printStackTrace(); // Tampilkan error di console server
            throw new RuntimeException("Gagal membuat PDF: " + e.getMessage(), e);
        }
    }
}