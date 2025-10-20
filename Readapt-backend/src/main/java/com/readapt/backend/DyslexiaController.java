package com.readapt.backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.ClassPathResource;

import ai.onnxruntime.*;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RestController
@RequestMapping("/api")
public class DyslexiaController {

    private final OrtEnvironment env;
    private final OrtSession session;

    public DyslexiaController() throws Exception {
        env = OrtEnvironment.getEnvironment();

        // Load ONNX model from resources/ml/model.onnx even when packaged in a jar
        // Fix: If IR version error, export your model with ONNX IR version ≤ 9
        ClassPathResource resource = new ClassPathResource("ml/model.onnx");
        InputStream in = resource.getInputStream();
        // Copy resource to a temporary file, which ONNX Runtime can read
        Path temp = Files.createTempFile("model", ".onnx");
        Files.copy(in, temp, StandardCopyOption.REPLACE_EXISTING);
        session = env.createSession(temp.toString(), new OrtSession.SessionOptions());
    }

    @PostMapping("/predict-dyslexia")
    public ResponseEntity<Map<String, Object>> predict(@RequestBody Map<String, Object> payload) {
        try {
            List<?> answersRaw = (List<?>) payload.get("answers");
            Number timeSecondsNum = (Number) payload.get("time");
            float timeSeconds = timeSecondsNum == null ? 30f : timeSecondsNum.floatValue();

            // Parse answers to int[]
            int[] a = new int[answersRaw.size()];
            for (int i = 0; i < answersRaw.size(); i++) {
                a[i] = ((Number)answersRaw.get(i)).intValue();
            }

            float[] features = computeFeatures(a, timeSeconds);

            // ONNX expects float[][] shape [1,6]
            OnnxTensor input = OnnxTensor.createTensor(env, new float[][]{features});
            String inputName = session.getInputNames().iterator().next();
            OrtSession.Result result = session.run(Collections.singletonMap(inputName, input));
            long[] labelArr = (long[]) result.get(0).getValue();
            int label = (int) labelArr[0];

            String debugLog = "---- Dyslexia Feature Computation ----\n"
                    + "Raw answers: " + Arrays.toString(a) + "\n"
                    + "time_seconds: " + timeSeconds + "\n"
                    + "Features: " + Arrays.toString(features) + "\n"
                    + "Model prediction output: " + label + "\n";

            Map<String, Object> resp = new HashMap<>();
            resp.put("label", label);

            List<Double> featureList = new ArrayList<>();
            for (float f : features) featureList.add((double)f);
            resp.put("features", featureList);
            resp.put("debug_log", debugLog);
            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    // Java version of your Python compute_features
    private float[] computeFeatures(int[] a, float time_seconds) {
        float Language_vocab = (a[0]+a[1]+a[2]+a[3]+a[4]+a[5]+a[7])/14f;
        float Memory = (a[1]+a[8])/4f;
        float Speed = 1.0f - Math.min(1.0f, Math.max(0.0f, (time_seconds-15f)/45f));
        float Visual_discrimination = (a[0]+a[2]+a[3]+a[5])/8f;
        float Audio_Discrimination = (a[6]+a[9])/4f;
        float Survey_Score = 0f;
        for (int v : a) Survey_Score += v;
        Survey_Score /= 40f;
        return new float[]{
            Language_vocab, Memory, Speed, Visual_discrimination, Audio_Discrimination, Survey_Score
        };
    }
}