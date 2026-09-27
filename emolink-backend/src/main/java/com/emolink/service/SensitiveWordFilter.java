package com.emolink.service;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SensitiveWordFilter {

    @Value("${sensitive.words:}")
    private String sensitiveWords;

    private SensitiveWordBs sensitiveWordBs;

    @PostConstruct
    public void init() {
        sensitiveWordBs = SensitiveWordBs.newInstance()
                .ignoreCase(true)
                .ignoreWidth(true)
                .ignoreChineseStyle(true)
                .ignoreEnglishStyle(true)
                .init();

        List<String> customWords = Arrays.stream(sensitiveWords.split(","))
                .map(String::trim)
                .filter(word -> !word.isEmpty())
                .distinct()
                .collect(Collectors.toList());
        if (!customWords.isEmpty()) {
            sensitiveWordBs.addWord(customWords);
        }
    }

    @PreDestroy
    public void destroy() {
        if (sensitiveWordBs != null) {
            sensitiveWordBs.destroy();
        }
    }

    public boolean containsSensitiveWord(String content) {
        return content != null && !content.isBlank() && sensitiveWordBs != null && sensitiveWordBs.contains(content);
    }
}
