package com.readapt.backend;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import net.sourceforge.tess4j.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OcrController {

    @PostMapping(value = "/ocr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> ocr(@RequestParam("image") MultipartFile image) {
        Map<String, Object> resp = new HashMap<>();
        try {
            ITesseract tesseract = new Tesseract();
            // Set the path to your tessdata directory (not the executable)
            tesseract.setDatapath("D:/OCR/");
            // Optionally: tesseract.setLanguage("eng");
            BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
            if (bufferedImage == null) {
                resp.put("error", "Could not read image file for OCR.");
                return ResponseEntity.badRequest().body(resp);
            }
            String result = tesseract.doOCR(bufferedImage);
            // Replace literal '\n' with real newlines for proper display
            String formattedResult = result.trim().replace("\\n", "\n").replace("\r\n", "\n");
            resp.put("text", formattedResult);
        } catch (Exception e) {
            resp.put("error", "OCR failed: " + e.getMessage());
        }
        return ResponseEntity.ok(resp);
    }
}