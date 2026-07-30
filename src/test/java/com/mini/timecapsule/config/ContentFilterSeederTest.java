package com.mini.timecapsule.config;

import com.mini.timecapsule.dao.ContentFilterRepository;
import com.mini.timecapsule.model.ContentFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 비속어 필터 시드.
 *
 * <p>예전엔 data.sql 로 넣었는데 그 스크립트는 컬럼명이 엔티티와 달라
 * (keyword/created_at vs filter_keyword/created_date) 애초에 동작하지 않았다.
 * 그 결과 필터 테이블이 늘 비어 있었고 비속어 검사가 사실상 꺼진 상태였다.
 *
 * <p>H2 가 파일 모드로 바뀌어 재시작해도 데이터가 남으므로, 시더는 반드시
 * 멱등이어야 한다 — 안 그러면 부팅할 때마다 같은 키워드가 쌓인다.
 */
@ExtendWith(MockitoExtension.class)
class ContentFilterSeederTest {

    @Mock
    private ContentFilterRepository contentFilterRepository;

    @InjectMocks
    private ContentFilterSeeder seeder;

    @Test
    @DisplayName("필터 테이블이 비어 있으면 기본 키워드를 ACTIVE 로 넣는다")
    void seedsDefaultKeywordsIntoAnEmptyTable() {
        when(contentFilterRepository.findAll()).thenReturn(List.of());

        seeder.seed();

        ArgumentCaptor<List<ContentFilter>> captor = ArgumentCaptor.forClass(List.class);
        verify(contentFilterRepository).saveAll(captor.capture());

        List<String> saved = captor.getValue().stream().map(ContentFilter::getFilterKeyword).toList();
        assertEquals(ContentFilterSeeder.DEFAULT_KEYWORDS, saved);
        assertTrue(captor.getValue().stream()
                .allMatch(f -> f.getUsed() == ContentFilter.Used.ACTIVE));
    }

    @Test
    @DisplayName("이미 전부 들어 있으면 아무것도 저장하지 않는다 — 재기동해도 중복되지 않는다")
    void doesNotDuplicateOnRestart() {
        when(contentFilterRepository.findAll()).thenReturn(
                ContentFilterSeeder.DEFAULT_KEYWORDS.stream()
                        .map(k -> ContentFilter.newContentFilter(k, ContentFilter.Used.ACTIVE))
                        .toList());

        seeder.seed();

        verify(contentFilterRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("일부만 있으면 빠진 것만 채운다")
    void addsOnlyTheMissingKeywords() {
        String present = ContentFilterSeeder.DEFAULT_KEYWORDS.get(0);
        when(contentFilterRepository.findAll()).thenReturn(
                List.of(ContentFilter.newContentFilter(present, ContentFilter.Used.ACTIVE)));

        seeder.seed();

        ArgumentCaptor<List<ContentFilter>> captor = ArgumentCaptor.forClass(List.class);
        verify(contentFilterRepository).saveAll(captor.capture());

        List<String> saved = captor.getValue().stream().map(ContentFilter::getFilterKeyword).toList();
        assertEquals(ContentFilterSeeder.DEFAULT_KEYWORDS.size() - 1, saved.size());
        assertTrue(saved.stream().noneMatch(present::equals));
    }

    @Test
    @DisplayName("운영자가 INACTIVE 로 꺼둔 키워드를 다시 살려내지 않는다")
    void respectsKeywordsAnOperatorDeactivated() {
        when(contentFilterRepository.findAll()).thenReturn(
                ContentFilterSeeder.DEFAULT_KEYWORDS.stream()
                        .map(k -> ContentFilter.newContentFilter(k, ContentFilter.Used.INACTIVE))
                        .toList());

        seeder.seed();

        verify(contentFilterRepository, never()).saveAll(anyList());
    }
}
