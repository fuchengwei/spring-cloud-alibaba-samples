package com.ichinae.samples.common.utils;

import java.util.Random;

/**
 * @author fuchengwei
 * @date 2020/12/18 4:34 下午
 * @description 随机账号生成
 */
public class AccountUtil {
    private static final int LENGTH = 7;

    public static String getRandomAccount() {
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < LENGTH; i++) {
            stringBuilder.append(random.nextInt(10));
        }
        return stringBuilder.toString();
    }
}
