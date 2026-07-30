package com.mini.timecapsule.config;

import com.mini.timecapsule.dao.ContentFilterRepository;
import com.mini.timecapsule.model.ContentFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 비속어 필터 기본 키워드 시드.
 *
 * <p>예전엔 {@code src/main/resources/data.sql} 이 담당했는데, 그 스크립트는
 * {@code keyword}/{@code created_at} 컬럼에 INSERT 했다. 엔티티가 매핑하는
 * 이름은 {@code filter_keyword}/{@code created_date} 라 한 번도 성공한 적이
 * 없다. 결과적으로 필터 테이블은 늘 비어 있었고 {@code LetterService} 의
 * 비속어 검사는 항상 0건을 세며 통과시켰다.
 *
 * <p>H2 가 파일 모드(PVC)로 바뀌어 재시작해도 행이 남기 때문에 이 시더는
 * 반드시 멱등해야 한다. 이미 존재하는 키워드는 {@code used} 값이 무엇이든
 * 건드리지 않는다 — 운영자가 일부러 INACTIVE 로 꺼둔 것을 되살리면 안 된다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ContentFilterSeeder implements ApplicationRunner {

    static final List<String> DEFAULT_KEYWORDS = List.of("바보", "멍청이", "나쁜말");

    private final ContentFilterRepository contentFilterRepository;

    @Transactional
    @Override
    public void run(ApplicationArguments args) {
        seed();
    }

    /**
     * 멱등한 시드 로직. 트랜잭션은 {@link #run} 이 proxy 를 통해 진입할 때 열린다.
     * 이 메서드에 직접 {@code @Transactional} 을 붙이면 같은 빈 안의 호출이라
     * proxy 를 타지 않아 어노테이션이 무력화된다.
     */
    void seed() {
        Set<String> existing = new HashSet<>();
        for (ContentFilter filter : contentFilterRepository.findAll()) {
            existing.add(filter.getFilterKeyword());
        }

        List<ContentFilter> missing = DEFAULT_KEYWORDS.stream()
                .filter(keyword -> !existing.contains(keyword))
                .map(keyword -> ContentFilter.newContentFilter(keyword, ContentFilter.Used.ACTIVE))
                .toList();

        if (missing.isEmpty()) {
            return;
        }

        contentFilterRepository.saveAll(missing);
        log.info("비속어 필터 기본 키워드 {}건을 추가했습니다.", missing.size());
    }
}
