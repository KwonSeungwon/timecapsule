package com.mini.timecapsule.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * D-Day 는 <b>날짜</b> 차이지 경과 시간이 아니다.
 *
 * <p>사용자는 달력에서 "8월 29일"을 고르고, 프론트는 그 날 KST 자정을 openAt 으로 보낸다.
 * 그러니 남은 일수도 KST 달력 기준이어야 한다.
 *
 * <p>예전 구현은 {@code ChronoUnit.DAYS.between(now, openAt)} 이라 남은 시간을 잘라버렸다.
 * 그래서 7월 30일 18시에 8월 29일 캡슐을 보면 화면 두 곳이 서로 다른 말을 했다 —
 * 작성 화면은 "30일 후에 도착합니다", 보관함은 "D-29". 더 나쁜 건 개봉 <b>전날</b>
 * 저녁에 이미 D-0 이 떠서 오늘 열리는 것처럼 보였다는 점이다.
 */
class LetterPaperDDayTest {

    /** 2026-08-29 KST 자정에 열리는 캡슐. 프론트가 실제로 보내는 형태다. */
    private LetterPaper capsuleOpeningOn29th() {
        LetterPaper capsule = new LetterPaper();
        capsule.setOpenAt(ZonedDateTime.of(
                LocalDateTime.of(2026, 8, 29, 0, 0), LetterPaper.SERVICE_ZONE));
        return capsule;
    }

    private ZonedDateTime kst(int month, int day, int hour) {
        return ZonedDateTime.of(LocalDateTime.of(2026, month, day, hour, 0), LetterPaper.SERVICE_ZONE);
    }

    @Test
    @DisplayName("7/30 저녁에 본 8/29 캡슐은 D-30 이다 (D-29 가 아니라)")
    void countsCalendarDaysNotElapsedHours() {
        assertEquals(30, capsuleOpeningOn29th().getDDay(kst(7, 30, 18)));
    }

    @Test
    @DisplayName("개봉 전날 저녁은 아직 D-1 이다")
    void theEveningBeforeIsStillOneDayAway() {
        assertEquals(1, capsuleOpeningOn29th().getDDay(kst(8, 28, 23)));
    }

    @Test
    @DisplayName("개봉일 새벽은 D-0")
    void openingDayIsZero() {
        assertEquals(0, capsuleOpeningOn29th().getDDay(kst(8, 29, 0)));
    }

    @Test
    @DisplayName("개봉일이 지나면 음수 — 화면은 openable 로 분기하므로 값 자체는 그대로 둔다")
    void afterOpeningDayItGoesNegative() {
        assertEquals(-1, capsuleOpeningOn29th().getDDay(kst(8, 30, 9)));
    }

    @Test
    @DisplayName("UTC 로 들어온 openAt 도 KST 달력으로 환산한다")
    void normalisesOtherZonesToServiceZone() {
        LetterPaper capsule = new LetterPaper();
        // 2026-08-28T15:00Z == 2026-08-29T00:00+09:00. DB 에서 UTC 로 돌아오는 형태다.
        capsule.setOpenAt(ZonedDateTime.parse("2026-08-28T15:00:00Z"));

        assertEquals(30, capsule.getDDay(kst(7, 30, 18)));
    }

    @Test
    @DisplayName("openAt 이 없으면 0")
    void noOpenAtMeansZero() {
        assertEquals(0, new LetterPaper().getDDay(kst(7, 30, 18)));
    }
}
