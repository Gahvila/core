/*
 * MIT License
 *
 * Copyright (c) 2019 Ruinscraft, LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.gahvila.gahvilacore.Panilla.API.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PTranslations {

    private final String languageKey;
    private final Map<String, String> translations;

    public PTranslations(String languageKey, Map<String, String> translations) {
        this.languageKey = languageKey;
        this.translations = translations;
    }

    public static PTranslations get(String languageKey) throws IOException {
        try (InputStream inputStream = PTranslations.class.getResourceAsStream("/messages_" + languageKey + ".properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);

            Map<String, String> translations = new HashMap<>();

            for (Object key : properties.keySet()) {
                String value = properties.getProperty(key.toString());
                translations.put(key.toString(), value);
            }

            return new PTranslations(languageKey, translations);
        }
    }

    public String getLanguageKey() {
        return languageKey;
    }

    public String getTranslation(String key, String... replacements) {
        String unformatted = translations.get(key);
        if (unformatted == null) {
            return "unknown translation: " + key;
        }
        return String.format(translations.get(key), (Object) replacements);
    }

}
