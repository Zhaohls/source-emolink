package com.emolink.util;

import java.util.concurrent.ThreadLocalRandom;

public final class AnonymousNameUtil {

    private static final String[] ADJECTIVES = {
            "温暖的", "快乐的", "勇敢的", "善良的", "可爱的",
            "安静的", "闪亮的", "温柔的", "自由的", "明亮的"
    };

    private static final String[] NOUNS = {
            "云朵", "星星", "花朵", "月亮", "海浪",
            "微风", "阳光", "小鹿", "蝴蝶", "雨滴"
    };

    private AnonymousNameUtil() {
    }

    public static String generate() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        String adjective = ADJECTIVES[random.nextInt(ADJECTIVES.length)];
        String noun = NOUNS[random.nextInt(NOUNS.length)];
        int number = random.nextInt(1000, 10000);
        return adjective + noun + "#" + number;
    }
}

