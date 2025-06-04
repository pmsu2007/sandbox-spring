package com.mingxoop.sandbox.global.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeUtils {

    /**
     * 현재 시각 기준으로 주어진 시각까지의 초(second) 단위 차이를 반환합니다.
     * 음수인 경우 0을 반환합니다 (이미 만료된 시간).
     *
     * @param targetTime 만료 시각
     * @return 초 단위 남은 시간
     */
    public static int secondsUntil(LocalDateTime targetTime) {
        return Math.max(0, (int) Duration.between(LocalDateTime.now(), targetTime).getSeconds());
    }
}
