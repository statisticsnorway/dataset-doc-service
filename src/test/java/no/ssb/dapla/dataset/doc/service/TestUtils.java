package no.ssb.dapla.dataset.doc.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TestUtils {

    private static final ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    public static String load(String fileName) {
        try {
            return new String(getInputStream(fileName).readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static InputStream getInputStream(String fileName) throws FileNotFoundException {
        InputStream stream = classloader.getResourceAsStream(fileName);
        if (stream == null) {
            throw new FileNotFoundException(fileName);
        }
        return stream;
    }

}

