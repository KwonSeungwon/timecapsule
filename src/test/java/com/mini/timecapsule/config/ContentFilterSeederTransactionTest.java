package com.mini.timecapsule.config;

import com.mini.timecapsule.dao.ContentFilterRepository;
import com.mini.timecapsule.model.ContentFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * 시드가 <b>정말로</b> 트랜잭션 안에서 도는지 확인한다.
 *
 * <p>{@code ContentFilterSeederTest} 는 {@code @InjectMocks} 로 만든 날것의 인스턴스에
 * 대고 {@code seed()} 를 직접 호출한다 — Spring 컨텍스트도, AOP 프록시도 없다.
 * 그래서 이 코드가 원래 고치려던 버그를 재현하지 못한다. 어노테이션을
 * {@code run()} 에서 {@code seed()} 로 되돌려도 그 테스트들은 전부 통과한다.
 *
 * <p>고쳐진 버그는 <b>Spring 자기호출(self-invocation) 함정</b>이다:
 * <pre>
 * &#64;Transactional void seed() { ... }        // ← 예전 위치
 * public void run(ApplicationArguments a) {
 *     seed();   // this.seed() — 프록시를 거치지 않는다 → 트랜잭션이 안 열린다
 * }
 * </pre>
 * 어노테이션은 붙어 있는데 트랜잭션은 없는, 가장 알아채기 어려운 상태가 된다.
 * {@code saveAll} 이 중간에 실패하면 일부 키워드만 들어간 채로 남는다.
 *
 * <p>그래서 여기서는 <b>컨테이너가 준 프록시</b>를 통해 {@code run()} 을 호출하고,
 * 리포지토리가 불리는 그 순간 실제 트랜잭션이 열려 있는지를 본다.
 * 어노테이션을 {@code seed()} 로 옮기면 이 테스트는 즉시 실패한다.
 */
@SpringBootTest
class ContentFilterSeederTransactionTest {

    @Autowired
    private ContentFilterSeeder seeder;

    /**
     * 리포지토리를 가로채 "지금 트랜잭션이 열려 있는가" 를 기록한다.
     * 트랜잭션 경계 안쪽을 관측할 방법은 이것뿐이다 — 밖에서는 보이지 않는다.
     */
    @MockBean
    private ContentFilterRepository contentFilterRepository;

    @Test
    @DisplayName("run() 을 프록시로 호출하면 리포지토리 접근 시점에 트랜잭션이 열려 있다")
    void runOpensATransactionAroundTheSeeding() {
        AtomicBoolean transactionWasActive = new AtomicBoolean(false);

        when(contentFilterRepository.findAll()).thenAnswer(invocation -> {
            transactionWasActive.set(TransactionSynchronizationManager.isActualTransactionActive());
            return List.<ContentFilter>of();
        });

        seeder.run(null);

        assertTrue(transactionWasActive.get(),
                "run() 안에서 트랜잭션이 열리지 않았다. @Transactional 이 seed() 로 옮겨졌는지 확인할 것 — "
                        + "run() 이 this.seed() 로 호출하기 때문에 프록시를 타지 않아 "
                        + "어노테이션이 조용히 무력화된다");
    }
}
