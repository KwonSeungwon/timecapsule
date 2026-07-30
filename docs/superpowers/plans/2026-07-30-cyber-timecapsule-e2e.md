# Cyber TimeCapsule — 전 기능 정상화 + 배포 + QA 구현 계획

> **For agentic workers:** REQUIRED SUB-SKILL: `superpowers:subagent-driven-development`. Steps use checkbox (`- [ ]`) syntax.

**Goal:** 사이버 타임캡슐(비회원이 좌표·바이옴이 부여된 캡슐을 미래 날짜로 매립 → 개봉일에 이메일 알림 → 비밀번호로 개봉)의 모든 기능이 prod에서 실제로 동작하게 만들고, 배포 후 QA로 증명한다.

**Architecture:** Spring Boot 3.2.5 WAR (Java 17) 단일 컨테이너가 Vue 3 SPA를 `src/main/resources/static`에서 서빙하고 `/api/v1/capsule/**` REST를 제공한다. K3s 단일 노드 + Traefik ingress + Cloudflare. 데이터는 H2, 메일은 `JavaMailSender`.

**Tech Stack:** Spring Boot 3.2.5, Spring Security 6, JPA/H2, Thymeleaf(메일 템플릿), Vue 3 + vue-router(history mode), K3s, Traefik, Mailpit(SMTP sink).

---

## 현장 조사 결과 (2026-07-30, prod 실측)

| # | 증상 | 근거 | 심각도 |
|---|---|---|---|
| 1 | `/write`, `/find`, `/my-capsule`, `/tutorial` 전부 **403** | `curl http://timecapsule.chatflow.ai.kr/write` → 403 | **P0 — 랜딩 외 전 기능 불능** |
| 2 | 재시작하면 **모든 캡슐 소실** | `DB_URL=jdbc:h2:mem:timecapsule`, PVC 없음, volumes 없음 | **P0** |
| 3 | **메일이 한 통도 안 나감** | `timecapsule-mail-secret` 부재 → `MAIL_HOST=localhost:25` → connect refused → catch 로 삼킴 | **P0** |
| 4 | 개봉일 지난 캡슐이 `isNotified:false` 로 방치 | `/api/v1/capsule/public` 응답 (openable:true, isNotified:false) | P0 (3의 증상) |
| 5 | 메일 링크가 `http://.../my-capsule?email=` → 403 | 스케줄러 본문 하드코딩 + 증상 1 | P0 |
| 6 | 매립 성공 카드(좌표/바이옴)가 절대 안 뜸 | `WriteLetter.vue` `submitLetter()` 성공 시 `$router.replace('/')` | P1 |
| 7 | 스케줄러가 **1분마다** 실행 | `cron` 기본값 `0 * * * * *` | P1 |
| 8 | 날짜가 UTC 자정으로 저장 | `new Date('YYYY-MM-DD').toISOString()` — KST 기준 하루 밀림 | P1 |
| 9 | `/public` 응답이 **수신자 이메일을 공개** | `CapsuleResponse.from()` 이 `receiverEmail` 무조건 포함 | P1 (개인정보) |
| 10 | 공개 캡슐 탐색 UI 없음 | `/api/v1/capsule/public` 는 있는데 프론트 호출처 0 | P2 |
| 11 | health probe 없음 | Deployment 에 readiness/liveness 미설정 | P2 |
| 12 | 죽은 코드 | `Target.vue`, `SelectCapsule.vue`, `SelectLetterPaper.vue`, `LetterService.list()`(→null), `StartController` | P2 |

### 증상 1의 근본 원인

`WebConfig`와 `MvcConfiguration` **두 개의 `WebMvcConfigurer`가 같은 `/**` 패턴**에 리소스 핸들러를 중복 등록한다. `WebConfig`는 `classpath:/static/`만 보고 SPA fallback이 없다. 게다가 그 이전에 `SpringSecurity`의 `.anyRequest().authenticated()`가 `/write` 같은 SPA 경로를 잡아 **403으로 끊는다**. vue-router는 `createWebHistory`(history mode)라 딥링크가 반드시 서버를 거친다.

### 메일 실발송에 대한 결정 (중요)

실제 SMTP 자격증명이 리포/시크릿/클러스터 어디에도 없다. 따라서:

- **기본값은 in-cluster SMTP sink(Mailpit)** — SMTP 핸드셰이크·수신·렌더링까지 전 경로가 실제로 동작하고 검증 가능하다. 단 **실제 수신함으로는 가지 않는다.**
- `DEPLOY_MAIL_HOST` 등을 `scripts/.env.deploy`에 채우면 그대로 실 SMTP로 전환된다(코드 변경 0).
- 어느 모드인지 **부팅 로그에 크게 찍는다**. sink 모드를 "정상 발송"으로 착각하지 않게 하는 게 목적이다.
- Mailpit UI는 **ingress로 노출하지 않는다.** 캡슐 본문이 담긴 메일 뷰어를 공개하면 안 된다. QA는 port-forward + Mailpit REST API로 한다.

---

## File Structure

**Backend — 생성**
- `src/main/java/com/mini/timecapsule/config/SpaForwardingConfig.java` — SPA 딥링크 fallback 전담
- `src/main/java/com/mini/timecapsule/service/CapsuleNotificationMailer.java` — 메일 본문 조립 + 발송 전담
- `src/main/resources/templates/mail/capsule-opened.html` — Thymeleaf HTML 메일 템플릿
- `src/test/java/com/mini/timecapsule/controller/SpaRoutingTest.java`
- `src/test/java/com/mini/timecapsule/service/CapsuleNotificationMailerTest.java`

**Backend — 수정**
- `src/main/java/com/mini/timecapsule/configurations/SpringSecurity.java` — SPA 경로/actuator permitAll
- `src/main/java/com/mini/timecapsule/configurations/MvcConfiguration.java` — 삭제(SpaForwardingConfig로 대체)
- `src/main/java/com/mini/timecapsule/config/WebConfig.java` — 삭제(중복 `/**` 핸들러)
- `src/main/java/com/mini/timecapsule/service/TimecapsuleScheduler.java` — 발송 위임 + 시도 횟수 상한
- `src/main/java/com/mini/timecapsule/model/LetterPaper.java` — `notifyAttempts` 컬럼
- `src/main/java/com/mini/timecapsule/dto/CapsuleResponse.java` — `receiverEmail` 노출 제어
- `src/main/java/com/mini/timecapsule/controller/LetterController.java` — `/public` 는 이메일 제외
- `src/main/java/com/mini/timecapsule/service/LetterService.java` — 죽은 `list()` 제거
- `src/main/resources/application.yml` — cron 시간단위, base-url, actuator

**Frontend — 수정/생성**
- `vue/src/components/WriteLetter.vue` — 성공 카드 복구 + 타임존
- `vue/src/components/Explore.vue` — 신규(공개 캡슐 탐색)
- `vue/src/router/index.js` — `/explore` 라우트
- `vue/src/components/Title.vue` — 탐색 진입점
- 삭제: `Target.vue`, `SelectCapsule.vue`, `SelectLetterPaper.vue`

**Infra — 수정/생성**
- `k8s/deployment.yaml` — PVC + volumeMount + Recreate + probes + DB_URL 파일모드
- `k8s/mail-sink.yaml` — 신규 Mailpit
- `scripts/deploy-k3s.sh` — 메일 시크릿 항상 생성(기본 sink), mail-sink 적용
- `docs/DEPLOYMENT.md` — 갱신

---

## Task 1: SPA 딥링크 라우팅 복구 (P0)

**Files:**
- Create: `src/main/java/com/mini/timecapsule/config/SpaForwardingConfig.java`
- Delete: `src/main/java/com/mini/timecapsule/config/WebConfig.java`, `src/main/java/com/mini/timecapsule/configurations/MvcConfiguration.java`
- Modify: `src/main/java/com/mini/timecapsule/configurations/SpringSecurity.java`
- Test: `src/test/java/com/mini/timecapsule/controller/SpaRoutingTest.java`

- [ ] **Step 1: 실패하는 테스트 작성**

```java
package com.mini.timecapsule.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SpaRoutingTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void spaDeepLinksServeIndexHtml() throws Exception {
        for (String path : new String[]{"/write", "/find", "/my-capsule", "/tutorial", "/explore"}) {
            mockMvc.perform(get(path))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith("text/html"));
        }
    }

    @Test
    void deepLinkWithQueryStringIsServed() throws Exception {
        mockMvc.perform(get("/my-capsule").param("email", "someone@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));
    }

    @Test
    void rootIsServed() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
    }

    @Test
    void apiRoutesAreNotSwallowedBySpaFallback() throws Exception {
        mockMvc.perform(get("/api/v1/capsule/public"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }

    @Test
    void missingStaticAssetReturns404NotIndexHtml() throws Exception {
        mockMvc.perform(get("/js/does-not-exist.js")).andExpect(status().isNotFound());
    }

    @Test
    void actuatorHealthIsPublic() throws Exception {
        mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }
}
```

- [ ] **Step 2: 테스트 실패 확인**

Run: `./gradlew test --tests 'com.mini.timecapsule.controller.SpaRoutingTest' -x buildVue`
Expected: FAIL — `/write` 등이 403.

- [ ] **Step 3: `WebConfig.java`, `MvcConfiguration.java` 삭제**

두 파일 모두 `/**` 에 리소스 핸들러를 등록해 서로를 덮어쓴다. 하나로 합친다.

```bash
git rm src/main/java/com/mini/timecapsule/config/WebConfig.java
git rm src/main/java/com/mini/timecapsule/configurations/MvcConfiguration.java
```

- [ ] **Step 4: `SpaForwardingConfig` 작성**

```java
package com.mini.timecapsule.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * Vue Router 가 createWebHistory(history mode) 라 /write · /my-capsule 같은 딥링크가
 * 반드시 서버를 거친다. 정적 자원이 있으면 그대로 주고, 없으면 SPA 셸(index.html)로 넘긴다.
 *
 * 주의: 이 프로젝트에는 예전에 WebConfig 와 MvcConfiguration 이 **둘 다** "/**" 를
 * 등록해 서로를 덮어쓰고 fallback 이 사라져 모든 딥링크가 깨져 있었다.
 * 리소스 핸들러는 이 클래스 하나로만 유지할 것.
 */
@Configuration
public class SpaForwardingConfig implements WebMvcConfigurer {

    /** SPA 셸로 fallback 하면 안 되는 경로 — 없으면 404 가 맞다. */
    private static final String[] NON_SPA_PREFIXES = {
            "api/", "actuator/", "js/", "css/", "img/", "fonts/", "images/", "h2-console/"
    };

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requested = location.createRelative(resourcePath);
                        if (requested.exists() && requested.isReadable()) {
                            return requested;
                        }
                        if (isNonSpaPath(resourcePath)) {
                            return null;
                        }
                        return new ClassPathResource("/static/index.html");
                    }
                });
    }

    private boolean isNonSpaPath(String resourcePath) {
        for (String prefix : NON_SPA_PREFIXES) {
            if (resourcePath.startsWith(prefix)) {
                return true;
            }
        }
        // 확장자가 있는 요청(파일)인데 실물이 없으면 404 가 맞다.
        int lastSlash = resourcePath.lastIndexOf('/');
        return resourcePath.indexOf('.', lastSlash + 1) >= 0;
    }
}
```

- [ ] **Step 5: `SpringSecurity` 에서 SPA 경로 permitAll**

`authorizeHttpRequests` 블록을 아래로 교체한다.

```java
            .authorizeHttpRequests(auth -> auth
                // SPA 셸과 정적 자원 — 비회원 서비스라 인증 대상이 아니다.
                .requestMatchers(
                        "/", "/index.html", "/favicon.ico",
                        "/write", "/find", "/my-capsule", "/tutorial", "/explore",
                        "/static/**", "/css/**", "/js/**", "/img/**", "/images/**", "/fonts/**"
                ).permitAll()
                .requestMatchers("/api/v1/**", "/api/timecapsule/**", "/h2-console/**").permitAll()
                // k8s probe 용 — health/info 만 연다.
                .requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info").permitAll()
                .anyRequest().authenticated()
            );
```

- [ ] **Step 6: `application.yml` 에 actuator 노출 설정 추가**

기본(dev) 문서 최상단 `spring:` 블록 **밖**, 파일 끝의 `timecapsule:` 블록 앞에 추가한다(모든 프로필 공통).

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      probes:
        enabled: true
      show-details: never
```

- [ ] **Step 7: 테스트 통과 확인**

Run: `./gradlew test --tests 'com.mini.timecapsule.controller.SpaRoutingTest' -x buildVue`
Expected: PASS (6 tests)

- [ ] **Step 8: 전체 테스트 회귀 확인**

Run: `./gradlew test -x buildVue`
Expected: BUILD SUCCESSFUL

- [ ] **Step 9: 커밋**

```bash
git add -A
git commit -F - <<'EOF'
fix(web): restore SPA deep-link routing — /write·/my-capsule 등 전 경로 403 해소

WebConfig 와 MvcConfiguration 이 둘 다 "/**" 에 리소스 핸들러를 등록해
서로를 덮어썼고, 살아남은 쪽엔 SPA fallback 이 없었다. 거기에
SecurityConfig 의 anyRequest().authenticated() 가 딥링크를 403 으로 끊어
prod 에서 랜딩(/) 외 모든 화면이 열리지 않았다. 리소스 핸들러를
SpaForwardingConfig 하나로 합치고 SPA 경로를 permitAll 로 연다.

Constraint: vue-router 가 createWebHistory 라 딥링크가 서버를 반드시 거친다
Rejected: vue-router 를 hash mode 로 전환 | 이미 배포된 메일 링크·북마크가 깨지고 URL 이 지저분해짐
Confidence: high
Scope-risk: moderate
Directive: 리소스 핸들러("/**")는 SpaForwardingConfig 에만 둘 것 — 두 번째 WebMvcConfigurer 를 추가하면 fallback 이 조용히 사라진다
Not-tested: Traefik 이 앞단에서 경로를 재작성하는 경우

Co-Authored-By: Claude Opus 4.8 (1M context) <noreply@anthropic.com>
EOF
```

---

## Task 2: 이메일 발송 파이프라인 정상화 (P0)

**Files:**
- Create: `src/main/java/com/mini/timecapsule/service/CapsuleNotificationMailer.java`
- Create: `src/main/resources/templates/mail/capsule-opened.html`
- Create: `src/test/java/com/mini/timecapsule/service/CapsuleNotificationMailerTest.java`
- Modify: `src/main/java/com/mini/timecapsule/service/TimecapsuleScheduler.java`
- Modify: `src/main/java/com/mini/timecapsule/model/LetterPaper.java`
- Modify: `src/main/java/com/mini/timecapsule/dao/LetterPaperRepository.java`
- Modify: `src/main/resources/application.yml`
- Modify: `src/test/java/com/mini/timecapsule/service/TimecapsuleSchedulerTest.java`

- [ ] **Step 1: `LetterPaper` 에 발송 시도 횟수 추가**

`isNotified` 필드 바로 아래에 삽입:

```java
    /**
     * 알림 발송 시도 횟수. SMTP 가 계속 실패하는 캡슐이 매 스케줄마다
     * 영원히 재시도되는 것을 막는다.
     */
    private Integer notifyAttempts = 0;
```

그리고 클래스 하단 `remove()` 아래에 도메인 메서드를 추가:

```java
    /** 알림 발송 성공 처리 */
    public void markNotified() {
        this.isNotified = true;
        this.updatedAt = LocalDateTime.now();
    }

    /** 알림 발송 실패 1회 기록. 상한에 도달하면 true(=포기) 를 돌려준다. */
    public boolean recordNotifyFailure(int maxAttempts) {
        this.notifyAttempts = (this.notifyAttempts == null ? 0 : this.notifyAttempts) + 1;
        this.updatedAt = LocalDateTime.now();
        return this.notifyAttempts >= maxAttempts;
    }
```

- [ ] **Step 2: 리포지토리 쿼리에 시도 상한 반영**

`LetterPaperRepository` 의 기존 메서드 아래에 추가:

```java
    @Query("""
            select l from LetterPaper l
            where l.openAt < :now
              and l.isNotified = false
              and l.status = :status
              and (l.notifyAttempts is null or l.notifyAttempts < :maxAttempts)
            """)
    List<LetterPaper> findDueForNotification(@Param("now") ZonedDateTime now,
                                             @Param("status") LetterPaper.LetterStatus status,
                                             @Param("maxAttempts") int maxAttempts);
```

임포트 추가: `org.springframework.data.jpa.repository.Query`, `org.springframework.data.repository.query.Param`.

- [ ] **Step 3: 메일 템플릿 작성**

`src/main/resources/templates/mail/capsule-opened.html`:

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head><meta charset="UTF-8"><title>타임캡슐 개봉 알림</title></head>
<body style="margin:0;padding:0;background:#f4f1e8;font-family:'Apple SD Gothic Neo',sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="background:#f4f1e8;padding:24px 0;">
    <tr><td align="center">
      <table role="presentation" width="480" cellpadding="0" cellspacing="0"
             style="background:#fff;border:4px solid #000;box-shadow:8px 8px 0 #5d7a54;">
        <tr><td style="padding:28px 28px 12px;">
          <p style="margin:0;font-size:13px;letter-spacing:2px;color:#5d7a54;">TIME CAPSULE</p>
          <h1 style="margin:8px 0 0;font-size:22px;color:#111;">캡슐이 열렸습니다</h1>
        </td></tr>
        <tr><td style="padding:0 28px;">
          <p style="font-size:15px;color:#333;line-height:1.7;margin:16px 0;">
            <strong th:text="${senderName}">누군가</strong>님이 묻어둔 타임캡슐
            <strong th:text="${title}">제목</strong>의 개봉일이 되었습니다.
          </p>
          <table role="presentation" width="100%" cellpadding="0" cellspacing="0"
                 style="background:#fffbe6;border:2px dashed #5d7a54;margin:20px 0;">
            <tr><td style="padding:16px;text-align:center;">
              <div style="font-size:30px;" th:text="${biomeEmoji}">📍</div>
              <div style="font-size:17px;font-weight:bold;color:#111;margin-top:6px;"
                   th:text="${locationName}">고요한 바다</div>
              <div style="font-size:12px;color:#666;margin-top:4px;"
                   th:text="'좌표 (' + ${xCoord} + ', ' + ${yCoord} + ')'">좌표 (0, 0)</div>
            </td></tr>
          </table>
          <p style="font-size:13px;color:#666;line-height:1.7;margin:16px 0;">
            매립할 때 정한 <strong>비밀번호</strong>를 입력하면 내용을 볼 수 있습니다.
          </p>
        </td></tr>
        <tr><td style="padding:8px 28px 32px;" align="center">
          <a th:href="${capsuleUrl}" target="_blank"
             style="display:inline-block;background:#ffe24a;border:3px solid #000;
                    padding:13px 30px;font-size:15px;font-weight:bold;color:#111;text-decoration:none;">
            캡슐 열어보기
          </a>
          <p style="font-size:11px;color:#999;margin:16px 0 0;word-break:break-all;"
             th:text="${capsuleUrl}">https://timecapsule.chatflow.ai.kr/my-capsule</p>
        </td></tr>
      </table>
    </td></tr>
  </table>
</body>
</html>
```

- [ ] **Step 4: 실패하는 메일러 테스트 작성**

`src/test/java/com/mini/timecapsule/service/CapsuleNotificationMailerTest.java`:

```java
package com.mini.timecapsule.service;

import com.mini.timecapsule.model.LetterPaper;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.ByteArrayOutputStream;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CapsuleNotificationMailerTest {

    private TemplateEngine templateEngine;

    @BeforeEach
    void setUp() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);
    }

    private LetterPaper capsule() {
        return LetterPaper.newEntity(
                "보낸이", "10년 뒤의 나에게", "receiver@example.com", "enc",
                120, 880, "DESERT", "붉은 사막",
                LetterPaper.LetterPaperType.LETTER, LetterPaper.CapsuleType.BOTTLE,
                "내용", null, null, ZonedDateTime.now().minusMinutes(1), false);
    }

    @Test
    void buildsHtmlMessageWithCapsuleDetailsAndHttpsLink() throws Exception {
        CapsuleNotificationMailer mailer = new CapsuleNotificationMailer(
                new JavaMailSenderImpl(), templateEngine,
                "https://timecapsule.chatflow.ai.kr", "noreply@example.com");

        MimeMessage message = mailer.compose(capsule());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        message.writeTo(out);
        String raw = out.toString("UTF-8");
        String body = mailer.renderBody(capsule());

        assertThat(message.getAllRecipients()[0].toString()).isEqualTo("receiver@example.com");
        assertThat(raw).contains("text/html");
        assertThat(body).contains("붉은 사막");
        assertThat(body).contains("보낸이");
        assertThat(body).contains("10년 뒤의 나에게");
        assertThat(body).contains("120");
        assertThat(body).contains("880");
        assertThat(body).contains("https://timecapsule.chatflow.ai.kr/my-capsule?email=receiver%40example.com");
        assertThat(body).doesNotContain("http://timecapsule");
    }

    @Test
    void escapesEmailInLinkQueryString() {
        CapsuleNotificationMailer mailer = new CapsuleNotificationMailer(
                new JavaMailSenderImpl(), templateEngine, "https://x.test", "noreply@example.com");
        LetterPaper c = capsule();
        c.setReceiverEmail("a+b@example.com");
        assertThat(mailer.renderBody(c)).contains("a%2Bb%40example.com");
    }

    @Test
    void sinkModeIsReportedWhenHostIsTheInClusterSink() {
        assertThat(CapsuleNotificationMailer.describeMailMode("timecapsule-mailpit", 1025))
                .contains("SINK");
        assertThat(CapsuleNotificationMailer.describeMailMode("smtp.gmail.com", 587))
                .contains("SMTP");
    }
}
```

- [ ] **Step 5: 테스트 실패 확인**

Run: `./gradlew test --tests 'com.mini.timecapsule.service.CapsuleNotificationMailerTest' -x buildVue`
Expected: FAIL — `CapsuleNotificationMailer` 클래스 없음(컴파일 에러).

- [ ] **Step 6: `CapsuleNotificationMailer` 구현**

```java
package com.mini.timecapsule.service;

import com.mini.timecapsule.model.LetterPaper;
import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 개봉일이 된 타임캡슐의 알림 메일을 조립하고 보낸다.
 *
 * 발송 실패는 삼키지 않는다 — 예외를 그대로 올려 호출자(스케줄러)가
 * 재시도 횟수를 기록하게 한다. 예전엔 여기서 catch 로 삼켜서
 * "메일이 안 나가는데 로그만 조용한" 상태가 오래 방치됐다.
 */
@Slf4j
@Component
public class CapsuleNotificationMailer {

    private static final Map<String, String> BIOME_EMOJI = Map.of(
            "BEACH", "🌊", "MOUNTAIN", "⛰️", "DESERT", "🏜️", "FOREST", "🌲");

    private static final String SUBJECT_PREFIX = "[TimeCapsule] 캡슐이 열렸습니다 — ";

    private final JavaMailSender mailSender;
    private final ITemplateEngine templateEngine;
    private final String baseUrl;
    private final String fromAddress;

    public CapsuleNotificationMailer(JavaMailSender mailSender,
                                     ITemplateEngine templateEngine,
                                     @Value("${timecapsule.notification.base-url}") String baseUrl,
                                     @Value("${timecapsule.notification.from:}") String fromAddress) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.fromAddress = fromAddress;
    }

    /**
     * 부팅 시 메일 모드를 한 줄로 크게 남긴다.
     * in-cluster sink 는 SMTP 왕복까지는 진짜로 동작하지만 실제 수신함엔 도달하지 않는다.
     * 이걸 로그에 남기지 않으면 "정상 발송"으로 오독된다.
     */
    @PostConstruct
    void logMailMode() {
        if (mailSender instanceof JavaMailSenderImpl impl) {
            log.warn("=== TimeCapsule mail mode: {} ===", describeMailMode(impl.getHost(), impl.getPort()));
        }
    }

    static String describeMailMode(String host, int port) {
        if (host == null || host.isBlank() || "localhost".equals(host)) {
            return "UNCONFIGURED (host=" + host + ":" + port + ") — 메일이 발송되지 않습니다";
        }
        if (host.contains("mailpit") || host.contains("mail-sink")) {
            return "SINK (" + host + ":" + port + ") — SMTP 경로는 동작하지만 실제 수신함으로는 가지 않습니다";
        }
        return "SMTP (" + host + ":" + port + ")";
    }

    /** 메일 본문(HTML)만 렌더링한다. 테스트에서 본문 검증용으로도 쓴다. */
    public String renderBody(LetterPaper capsule) {
        Context ctx = new Context();
        ctx.setVariable("senderName", StringUtils.hasText(capsule.getSenderName()) ? capsule.getSenderName() : "누군가");
        ctx.setVariable("title", StringUtils.hasText(capsule.getTitle()) ? capsule.getTitle() : "제목 없는 캡슐");
        ctx.setVariable("locationName", StringUtils.hasText(capsule.getLocationName()) ? capsule.getLocationName() : "알 수 없는 곳");
        ctx.setVariable("biomeEmoji", BIOME_EMOJI.getOrDefault(capsule.getBiome(), "📍"));
        ctx.setVariable("xCoord", capsule.getXCoord());
        ctx.setVariable("yCoord", capsule.getYCoord());
        ctx.setVariable("capsuleUrl", capsuleUrl(capsule.getReceiverEmail()));
        return templateEngine.process("mail/capsule-opened", ctx);
    }

    private String capsuleUrl(String email) {
        return baseUrl + "/my-capsule?email=" + URLEncoder.encode(email == null ? "" : email, StandardCharsets.UTF_8);
    }

    /** 발송 직전의 MimeMessage 를 조립한다. */
    public MimeMessage compose(LetterPaper capsule) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            if (StringUtils.hasText(fromAddress)) {
                helper.setFrom(fromAddress);
            }
            helper.setTo(capsule.getReceiverEmail());
            helper.setSubject(SUBJECT_PREFIX + (StringUtils.hasText(capsule.getTitle()) ? capsule.getTitle() : "제목 없는 캡슐"));
            helper.setText(renderBody(capsule), true);
        } catch (jakarta.mail.MessagingException e) {
            throw new IllegalStateException("알림 메일 조립 실패: capsuleId=" + capsule.getId(), e);
        }
        return message;
    }

    /** 발송. 실패하면 예외가 그대로 올라간다 — 절대 삼키지 않는다. */
    public void send(LetterPaper capsule) {
        mailSender.send(compose(capsule));
    }
}
```

`URLEncoder.encode` 는 `@` 를 `%40`, `+` 를 `%2B` 로 바꾸지 않는다는 점에 주의 — `encode` 는 공백을 `+` 로 만들고 `@` 는 그대로 둔다. 테스트가 `%40`/`%2B` 를 요구하므로 아래 헬퍼로 대체한다(위 `capsuleUrl` 를 이걸로 교체):

```java
    private String capsuleUrl(String email) {
        String encoded = URLEncoder.encode(email == null ? "" : email, StandardCharsets.UTF_8)
                .replace("+", "%2B")   // encode() 는 공백을 '+' 로 만든다. 여기선 '+' 를 리터럴로 취급해야 한다.
                .replace("%40", "%40");
        // '@' 는 encode() 가 남겨두므로 명시적으로 인코딩한다.
        encoded = encoded.replace("@", "%40");
        return baseUrl + "/my-capsule?email=" + encoded;
    }
```

- [ ] **Step 7: 테스트 통과 확인**

Run: `./gradlew test --tests 'com.mini.timecapsule.service.CapsuleNotificationMailerTest' -x buildVue`
Expected: PASS (3 tests)

- [ ] **Step 8: 스케줄러를 메일러에 위임하도록 재작성**

`TimecapsuleScheduler.java` 전체 교체:

```java
package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.LetterPaperRepository;
import com.mini.timecapsule.model.LetterPaper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimecapsuleScheduler {

    private final LetterPaperRepository letterPaperRepository;
    private final CapsuleNotificationMailer mailer;

    @Value("${timecapsule.notification.max-attempts:10}")
    private int maxAttempts;

    /**
     * 개봉일이 지난 캡슐에 알림 메일을 보낸다.
     * 기본 매시 정각. 실패한 캡슐은 isNotified 가 false 로 남아 다음 주기에 재시도되고,
     * max-attempts 회 실패하면 쿼리 대상에서 빠진다(무한 재시도 방지).
     */
    @Scheduled(cron = "${timecapsule.notification.cron:0 0 * * * *}")
    @Transactional
    public void checkAndNotifyOpenCapsules() {
        List<LetterPaper> dueCapsules = letterPaperRepository.findDueForNotification(
                ZonedDateTime.now(), LetterPaper.LetterStatus.UNOPENED, maxAttempts);

        if (dueCapsules.isEmpty()) {
            return;
        }
        log.info("개봉 알림 대상 캡슐 {}건", dueCapsules.size());

        for (LetterPaper capsule : dueCapsules) {
            if (!StringUtils.hasText(capsule.getReceiverEmail())) {
                log.warn("수신 이메일이 없어 알림을 건너뜁니다: capsuleId={}", capsule.getId());
                capsule.markNotified();
                continue;
            }
            try {
                mailer.send(capsule);
                capsule.markNotified();
                log.info("개봉 알림 발송 완료: capsuleId={}", capsule.getId());
            } catch (Exception e) {
                boolean giveUp = capsule.recordNotifyFailure(maxAttempts);
                if (giveUp) {
                    log.error("개봉 알림 발송 {}회 실패 — 재시도를 중단합니다: capsuleId={}",
                            maxAttempts, capsule.getId(), e);
                } else {
                    log.error("개봉 알림 발송 실패({}/{}) — 다음 주기에 재시도합니다: capsuleId={}",
                            capsule.getNotifyAttempts(), maxAttempts, capsule.getId(), e);
                }
            }
        }
        letterPaperRepository.saveAll(dueCapsules);
    }
}
```

- [ ] **Step 9: 스케줄러 테스트 갱신**

`TimecapsuleSchedulerTest.java` 를 열어 기존 테스트가 `JavaMailSender` 를 목킹하고 있으면 `CapsuleNotificationMailer` 목으로 바꾸고, 아래 두 케이스를 **추가**한다.

```java
    @Test
    void 발송실패시_isNotified는_false로_남고_시도횟수가_증가한다() {
        LetterPaper capsule = dueCapsule();
        given(letterPaperRepository.findDueForNotification(any(), any(), anyInt()))
                .willReturn(new java.util.ArrayList<>(java.util.List.of(capsule)));
        willThrow(new org.springframework.mail.MailSendException("connect refused"))
                .given(mailer).send(capsule);

        scheduler.checkAndNotifyOpenCapsules();

        assertThat(capsule.getIsNotified()).isFalse();
        assertThat(capsule.getNotifyAttempts()).isEqualTo(1);
    }

    @Test
    void 수신이메일이_없으면_발송하지않고_알림완료로_표시한다() {
        LetterPaper capsule = dueCapsule();
        capsule.setReceiverEmail(null);
        given(letterPaperRepository.findDueForNotification(any(), any(), anyInt()))
                .willReturn(new java.util.ArrayList<>(java.util.List.of(capsule)));

        scheduler.checkAndNotifyOpenCapsules();

        then(mailer).should(never()).send(any());
        assertThat(capsule.getIsNotified()).isTrue();
    }
```

`dueCapsule()` 헬퍼는 `LetterPaper.newEntity(... openAt = ZonedDateTime.now().minusMinutes(1) ...)` 로 만든다.
`maxAttempts` 는 `ReflectionTestUtils.setField(scheduler, "maxAttempts", 10)` 로 주입한다.

- [ ] **Step 10: `application.yml` 알림 설정 갱신**

파일 끝 `timecapsule:` 블록을 교체:

```yaml
timecapsule:
  notification:
    # 매시 정각. 예전 기본값은 1분마다(0 * * * * *)였는데 테스트용 설정이 그대로 굳은 것이다.
    cron: ${TIMECAPSULE_NOTIFICATION_CRON:0 0 * * * *}
    from: ${MAIL_FROM:${MAIL_USERNAME:noreply@timecapsule.chatflow.ai.kr}}
    # 메일 본문 링크의 기준 URL. https 여야 한다 — Cloudflare 가 앞단에서 TLS 를 종단한다.
    base-url: ${TIMECAPSULE_BASE_URL:https://timecapsule.chatflow.ai.kr}
    max-attempts: ${TIMECAPSULE_NOTIFICATION_MAX_ATTEMPTS:10}
```

- [ ] **Step 11: 전체 테스트**

Run: `./gradlew test -x buildVue`
Expected: BUILD SUCCESSFUL

- [ ] **Step 12: 커밋**

```bash
git add -A
git commit -F - <<'EOF'
feat(mail): make capsule open notifications actually deliverable

메일 본문을 SimpleMailMessage 평문에서 Thymeleaf HTML 로 바꾸고
캡슐 제목·보낸이·좌표·바이옴을 담는다. 링크는 http → https 로 고치고
이메일을 쿼리스트링에 안전하게 인코딩한다. 발송 실패를 삼키던 catch 를
걷어내고 시도 횟수를 기록해 10회 실패 시 재시도를 멈춘다.
스케줄러 주기는 1분 → 매시 정각.

Constraint: 실제 SMTP 자격증명이 없어 기본 대상은 in-cluster sink 다
Rejected: 발송 실패 시 즉시 isNotified=true | 일시적 SMTP 장애로 알림이 영구 유실됨
Confidence: high
Scope-risk: moderate
Directive: CapsuleNotificationMailer.send() 는 예외를 삼키지 않는다 — 호출자가 재시도 회계를 한다. try/catch 를 안으로 옮기지 말 것
Not-tested: 실 SMTP(Gmail/SES) 대상 인증·TLS 핸드셰이크
Confidence: high

Co-Authored-By: Claude Opus 4.8 (1M context) <noreply@anthropic.com>
EOF
```

---

## Task 3: 공개 캡슐 응답에서 수신자 이메일 제거 (P1, 개인정보)

**Files:**
- Modify: `src/main/java/com/mini/timecapsule/dto/CapsuleResponse.java`
- Modify: `src/main/java/com/mini/timecapsule/controller/LetterController.java`
- Test: `src/test/java/com/mini/timecapsule/controller/LetterControllerPrivacyTest.java` (create)

- [ ] **Step 1: 실패하는 테스트**

```java
package com.mini.timecapsule.controller;

import com.mini.timecapsule.dto.CapsuleCreateRequest;
import com.mini.timecapsule.model.LetterPaper;
import com.mini.timecapsule.service.LetterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LetterControllerPrivacyTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private LetterService letterService;

    @Test
    void publicListingDoesNotLeakReceiverEmail() throws Exception {
        letterService.sendLetter(CapsuleCreateRequest.builder()
                .senderName("보낸이").title("공개캡슐").receiverEmail("secret@example.com")
                .password("pw").content("내용입니다").openAt(ZonedDateTime.now().plusDays(1))
                .isPublic(true)
                .letterPaperType(LetterPaper.LetterPaperType.LETTER)
                .capsuleType(LetterPaper.CapsuleType.BOTTLE)
                .build());

        mockMvc.perform(get("/api/v1/capsule/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("공개캡슐"))
                .andExpect(jsonPath("$[0].receiverEmail").doesNotExist());
    }

    @Test
    void ownerListingStillIncludesReceiverEmail() throws Exception {
        letterService.sendLetter(CapsuleCreateRequest.builder()
                .senderName("보낸이").title("내캡슐").receiverEmail("owner@example.com")
                .password("pw").content("내용입니다").openAt(ZonedDateTime.now().plusDays(1))
                .isPublic(false)
                .letterPaperType(LetterPaper.LetterPaperType.NOTE)
                .capsuleType(LetterPaper.CapsuleType.EGG)
                .build());

        mockMvc.perform(get("/api/v1/capsule/list").param("email", "owner@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].receiverEmail").value("owner@example.com"));
    }
}
```

- [ ] **Step 2: 실패 확인**

Run: `./gradlew test --tests '*LetterControllerPrivacyTest' -x buildVue`
Expected: FAIL — `receiverEmail` 이 `/public` 응답에 존재.

- [ ] **Step 3: `CapsuleResponse` 에 public 뷰 팩토리 추가**

`CapsuleResponse` 클래스에 필드 위 `@JsonInclude(JsonInclude.Include.NON_NULL)` 를 붙이고(임포트 `com.fasterxml.jackson.annotation.JsonInclude`), 아래 팩토리를 추가:

```java
    /**
     * 공개 탐색(/public)용 뷰. 수신자 이메일과 본문은 절대 넣지 않는다.
     * 누구나 조회 가능한 엔드포인트라 이메일이 나가면 그대로 수집당한다.
     */
    public static CapsuleResponse publicView(LetterPaper letter) {
        CapsuleResponse full = from(letter, false);
        return CapsuleResponse.builder()
                .id(full.getId())
                .senderName(full.getSenderName())
                .title(full.getTitle())
                .xCoord(full.getXCoord())
                .yCoord(full.getYCoord())
                .biome(full.getBiome())
                .locationName(full.getLocationName())
                .letterPaperType(full.getLetterPaperType())
                .capsuleType(full.getCapsuleType())
                .status(full.getStatus())
                .createdAt(full.getCreatedAt())
                .openAt(full.getOpenAt())
                .isPublic(full.getIsPublic())
                .dday(full.getDday())
                .openable(full.isOpenable())
                .build();
    }
```

`getDday()` 가 Lombok `@Getter` 로 `long dday` 에 대해 생성되는 이름인지 확인하고(`getDday()`), 맞지 않으면 실제 생성 이름을 쓴다.

- [ ] **Step 4: 컨트롤러 `/public` 을 `publicView` 로 교체**

```java
    @GetMapping("/public")
    public ResponseEntity<List<CapsuleResponse>> listPublic() {
        return ResponseEntity.ok(letterService.findPublicCapsules().stream()
                .map(CapsuleResponse::publicView)
                .toList());
    }
```

- [ ] **Step 5: 통과 확인 + 커밋**

Run: `./gradlew test -x buildVue`

```bash
git add -A
git commit -F - <<'EOF'
fix(api): stop leaking receiverEmail from the public capsule listing

GET /api/v1/capsule/public 은 인증 없이 누구나 호출 가능한데
수신자 이메일을 그대로 실어 보내고 있었다. 공개 탐색에 필요한 필드만
담는 publicView() 를 만들어 이메일·본문·이미지를 제외한다.

Confidence: high
Scope-risk: narrow
Directive: /public 응답에 필드를 추가할 때 개인 식별 정보가 아닌지 먼저 확인할 것
Not-tested: 기존에 이미 수집된 이메일에 대한 사후 조치

Co-Authored-By: Claude Opus 4.8 (1M context) <noreply@anthropic.com>
EOF
```

---

## Task 4: 프론트엔드 — 매립 성공 카드 + 타임존 (P1)

**Files:**
- Modify: `vue/src/components/WriteLetter.vue`

- [ ] **Step 1: `submitLetter` 성공 분기를 6단계로**

`submitLetter()` 안의 `self.$router.replace('/');` 를 `self.currentStep = 6;` 으로 교체한다. 성공 카드(6단계)는 이미 템플릿에 존재하지만 지금은 도달 자체가 불가능하다.

- [ ] **Step 2: 날짜를 로컬(KST) 자정으로 해석**

`submitLetter()` 의 `this.form.openAt = new Date(this.openDate).toISOString();` 을 교체:

```js
      // 'YYYY-MM-DD' 를 그냥 new Date() 에 넣으면 **UTC 자정**으로 파싱된다.
      // KST(UTC+9)에선 사용자가 고른 날짜보다 하루 앞선 시각이 되어버린다.
      // 'T00:00:00' 을 붙이면 로컬 자정으로 파싱된다.
      this.form.openAt = new Date(this.openDate + 'T00:00:00').toISOString();
```

- [ ] **Step 3: `minDate` / `setQuickDate` 도 로컬 기준으로**

`toISOString().split('T')[0]` 은 UTC 날짜라 KST 오전 9시 이전엔 전날이 나온다. 아래 헬퍼를 `methods` 최상단에 추가하고 두 곳에서 쓴다.

```js
    toLocalDateString(date) {
      // toISOString() 은 UTC 기준이라 KST 오전 9시 이전엔 하루 전 날짜가 나온다.
      const y = date.getFullYear();
      const m = String(date.getMonth() + 1).padStart(2, '0');
      const d = String(date.getDate()).padStart(2, '0');
      return `${y}-${m}-${d}`;
    },
```

`setQuickDate`:
```js
    setQuickDate(days) {
      const target = new Date();
      target.setDate(target.getDate() + days);
      this.openDate = this.toLocalDateString(target);
    },
```

`minDate` computed 는 `methods` 를 쓸 수 없으므로 인라인으로:
```js
    minDate() {
      const t = new Date();
      t.setDate(t.getDate() + 1);
      const y = t.getFullYear();
      const m = String(t.getMonth() + 1).padStart(2, '0');
      const d = String(t.getDate()).padStart(2, '0');
      return `${y}-${m}-${d}`;
    },
```

- [ ] **Step 4: 빌드 확인**

Run: `cd vue && npm run build`
Expected: 성공, `../src/main/resources/static` 갱신.

- [ ] **Step 5: 커밋**

```bash
git add -A
git commit -F - <<'EOF'
fix(web): show the burial result card and stop shifting the open date by a day

매립 성공 시 6단계(좌표·바이옴 결과 카드)로 가야 하는데 홈으로
replace 해버려서 이 서비스의 핵심 연출이 한 번도 보인 적이 없었다.
그리고 'YYYY-MM-DD' 를 new Date() 에 넣으면 UTC 자정으로 파싱돼
KST 사용자가 고른 날짜보다 하루 앞서 저장됐다.

Confidence: high
Scope-risk: narrow
Directive: 날짜 문자열을 new Date() 에 직접 넣지 말 것 — 'T00:00:00' 을 붙여 로컬 자정으로 파싱해야 한다
Not-tested: KST 외 타임존 브라우저

Co-Authored-By: Claude Opus 4.8 (1M context) <noreply@anthropic.com>
EOF
```

---

## Task 5: 공개 캡슐 탐색 화면 (P2)

**Files:**
- Create: `vue/src/components/Explore.vue`
- Modify: `vue/src/router/index.js`, `vue/src/components/Title.vue`

- [ ] **Step 1: `Explore.vue` 작성**

`/api/v1/capsule/public` 을 GET 해서 바이옴별로 묶어 보여준다. 각 카드에 제목·보낸이·좌표·D-day·개봉 가능 여부를 표시하고, 개봉 가능한 캡슐은 `/my-capsule` 로 유도한다. 기존 컴포넌트의 픽셀아트 톤(4px 검정 테두리, `#ffe24a` 포인트, `box-shadow: 4px 4px 0`)을 따른다. 로딩/빈 상태/에러 상태를 모두 처리한다. 빈 상태 문구: "아직 공개된 캡슐이 없습니다."

- [ ] **Step 2: 라우트 등록**

`vue/src/router/index.js` 의 `import` 에 `import Explore from '@/components/Explore'` 를 추가하고, catch-all **앞에** 라우트를 넣는다:

```js
  {
    path: '/explore',
    name: 'explore',
    component: Explore
  },
```

- [ ] **Step 3: `Title.vue` 에 진입 버튼 추가**

기존 버튼들과 같은 마크업/클래스로 "공개 캡슐 탐색" 버튼을 추가하고 `/explore` 로 라우팅한다.

- [ ] **Step 4: 빌드 + 커밋**

Run: `cd vue && npm run build`

```bash
git add -A
git commit -F - <<'EOF'
feat(web): add the public capsule explore screen

/api/v1/capsule/public 은 있었지만 호출하는 화면이 없어 공개 캡슐 기능이
사실상 존재하지 않았다. 바이옴별로 묶어 보여주는 탐색 화면을 추가한다.

Confidence: high
Scope-risk: narrow
Not-tested: 공개 캡슐이 수백 건일 때의 렌더 성능(페이지네이션 없음)

Co-Authored-By: Claude Opus 4.8 (1M context) <noreply@anthropic.com>
EOF
```

---

## Task 6: 죽은 코드 제거 (P2)

**Files:** delete `vue/src/components/{Target,SelectCapsule,SelectLetterPaper}.vue`; modify `LetterService.java`; delete `StartController.java`

- [ ] **Step 1: 참조 0 재확인**

Run:
```bash
grep -rn "Target\b\|SelectCapsule\|SelectLetterPaper" vue/src --include="*.vue" --include="*.js" | grep -v "^vue/src/components/Target.vue" | grep -v "^vue/src/components/SelectCapsule.vue" | grep -v "^vue/src/components/SelectLetterPaper.vue"
grep -rn "letterService.list(\|\.list(letterDto\|/start" src/main/java
```
Expected: 출력 없음. 출력이 있으면 그 항목은 **삭제하지 말 것**.

- [ ] **Step 2: 삭제**

```bash
git rm vue/src/components/Target.vue vue/src/components/SelectCapsule.vue vue/src/components/SelectLetterPaper.vue
git rm src/main/java/com/mini/timecapsule/controller/StartController.java
```

`LetterService` 에서 `list(LetterDto)` 메서드 전체를 삭제한다(항상 `null` 을 돌려주는 TODO 스텁). `LetterDto` 임포트가 다른 곳(`readLetter`/`removeLetter`)에서 여전히 쓰이면 임포트는 남긴다.

- [ ] **Step 3: 빌드/테스트 + 커밋**

Run: `./gradlew test -x buildVue && (cd vue && npm run build)`

```bash
git add -A
git commit -F - <<'EOF'
chore: remove dead components, the always-null LetterService.list(), and StartController

라우터에서 참조되지 않는 Vue 컴포넌트 3개와, 항상 null 을 돌려주던
LetterService.list(), 존재하지 않는 뷰를 반환하던 StartController 를 제거한다.

Confidence: high
Scope-risk: narrow

Co-Authored-By: Claude Opus 4.8 (1M context) <noreply@anthropic.com>
EOF
```

---

## Task 7: 영속성 + 메일 인프라 + 배포 스크립트

**Files:**
- Modify: `k8s/deployment.yaml`
- Create: `k8s/mail-sink.yaml`
- Modify: `scripts/deploy-k3s.sh`, `docs/DEPLOYMENT.md`

- [ ] **Step 1: PVC + 파일모드 H2 + probes + Recreate**

`k8s/deployment.yaml` 맨 앞에 PVC 를 추가한다:

```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: timecapsule-data
  namespace: timecapsule
spec:
  accessModes: ["ReadWriteOnce"]
  storageClassName: local-path
  resources:
    requests:
      storage: 1Gi
---
```

Deployment `spec` 에 추가:
```yaml
  strategy:
    type: Recreate   # RWO 볼륨 + 단일 replica — 롤링 업데이트는 새 파드가 볼륨을 못 잡는다
```

`DB_URL` 을 파일 모드로 교체:
```yaml
        - name: DB_URL
          # 예전엔 jdbc:h2:mem: 이라 파드가 재시작될 때마다 캡슐이 전부 사라졌다.
          # 타임캡슐 서비스에서 이건 치명적이다. 파일 모드 + PVC 로 고정한다.
          value: "jdbc:h2:file:/data/timecapsule;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
```

컨테이너에 volumeMount / probes / resources 추가:
```yaml
        volumeMounts:
        - name: data
          mountPath: /data
        readinessProbe:
          httpGet: { path: /actuator/health/readiness, port: 3000 }
          initialDelaySeconds: 20
          periodSeconds: 10
          failureThreshold: 6
        livenessProbe:
          httpGet: { path: /actuator/health/liveness, port: 3000 }
          initialDelaySeconds: 60
          periodSeconds: 20
          failureThreshold: 6
        resources:
          requests: { cpu: 100m, memory: 384Mi }
          limits:   { cpu: "1",  memory: 768Mi }
```

`spec.template.spec` 에 volume 추가:
```yaml
      volumes:
      - name: data
        persistentVolumeClaim:
          claimName: timecapsule-data
```

- [ ] **Step 2: `k8s/mail-sink.yaml` 작성**

```yaml
# in-cluster SMTP sink.
# 실제 SMTP 자격증명이 없을 때 메일 경로 전체(조립 → SMTP 왕복 → 렌더)를
# 실제로 동작시키고 눈으로 확인하기 위한 것이다.
#
# ⚠️ ingress 로 노출하지 않는다. 캡슐 본문이 담긴 메일 뷰어를 공개하면
#    비밀번호 없이 내용이 새어나간다. 확인은 port-forward 로 한다:
#    kubectl -n timecapsule port-forward svc/timecapsule-mailpit 8025:8025
apiVersion: apps/v1
kind: Deployment
metadata:
  name: timecapsule-mailpit
  namespace: timecapsule
  labels: { app: timecapsule-mailpit }
spec:
  replicas: 1
  selector:
    matchLabels: { app: timecapsule-mailpit }
  template:
    metadata:
      labels: { app: timecapsule-mailpit }
    spec:
      containers:
      - name: mailpit
        image: axllent/mailpit:v1.20
        ports:
        - { name: smtp, containerPort: 1025 }
        - { name: http, containerPort: 8025 }
        env:
        - { name: MP_MAX_MESSAGES, value: "500" }
        - { name: MP_SMTP_AUTH_ACCEPT_ANY, value: "1" }
        - { name: MP_SMTP_AUTH_ALLOW_INSECURE, value: "1" }
        readinessProbe:
          httpGet: { path: /readyz, port: 8025 }
          initialDelaySeconds: 5
          periodSeconds: 10
        resources:
          requests: { cpu: 10m, memory: 32Mi }
          limits:   { cpu: 200m, memory: 128Mi }
---
apiVersion: v1
kind: Service
metadata:
  name: timecapsule-mailpit
  namespace: timecapsule
spec:
  type: ClusterIP
  selector: { app: timecapsule-mailpit }
  ports:
  - { name: smtp, port: 1025, targetPort: 1025 }
  - { name: http, port: 8025, targetPort: 8025 }
```

- [ ] **Step 3: 배포 스크립트 — 메일 시크릿을 항상 만든다**

`scripts/deploy-k3s.sh` 의 `step_secrets` 안 `if [ -n "${DEPLOY_MAIL_HOST:-}" ]; then ... fi` 블록을 교체:

```bash
  # 메일 시크릿은 **항상** 만든다.
  # DEPLOY_MAIL_HOST 가 없으면 in-cluster sink(mailpit)를 가리킨다 —
  # 예전처럼 시크릿이 아예 없으면 앱이 조용히 localhost:25 로 떨어져
  # 메일이 한 통도 안 나가는데 로그도 조용한 상태가 된다.
  MAIL_HOST="${DEPLOY_MAIL_HOST:-timecapsule-mailpit.timecapsule.svc.cluster.local}"
  if [ -n "${DEPLOY_MAIL_HOST:-}" ]; then
    MAIL_PORT="${DEPLOY_MAIL_PORT:-587}"; MAIL_AUTH="${DEPLOY_MAIL_SMTP_AUTH:-true}"; MAIL_TLS="${DEPLOY_MAIL_SMTP_STARTTLS_ENABLE:-true}"
  else
    echo "  [!] DEPLOY_MAIL_HOST 미설정 — in-cluster sink(mailpit)로 붙습니다. 실제 수신함으로는 가지 않습니다."
    MAIL_PORT="${DEPLOY_MAIL_PORT:-1025}"; MAIL_AUTH="false"; MAIL_TLS="false"
  fi

  ssh_k3s "$KUBECONFIG -n $NAMESPACE create secret generic timecapsule-mail-secret \
    --from-literal=MAIL_HOST='${MAIL_HOST}' \
    --from-literal=MAIL_PORT='${MAIL_PORT}' \
    --from-literal=MAIL_USERNAME='${DEPLOY_MAIL_USERNAME:-}' \
    --from-literal=MAIL_PASSWORD='${DEPLOY_MAIL_PASSWORD:-}' \
    --from-literal=MAIL_FROM='${DEPLOY_MAIL_FROM:-noreply@timecapsule.chatflow.ai.kr}' \
    --from-literal=MAIL_SMTP_AUTH='${MAIL_AUTH}' \
    --from-literal=MAIL_SMTP_STARTTLS_ENABLE='${MAIL_TLS}' \
    --dry-run=client -o yaml | $KUBECONFIG apply -f -"
```

`step_deploy` 의 `apply` 앞에 mail-sink 적용을 추가:

```bash
  ssh_k3s "$KUBECONFIG apply -f -" < "$PROJECT_ROOT/k8s/mail-sink.yaml"
```

`step_deploy` 의 rollout timeout 은 `--timeout=5m` 으로 늘린다(Recreate + PVC 바인딩).

- [ ] **Step 4: `docs/DEPLOYMENT.md` 갱신**

메일 모드 2가지(실 SMTP / in-cluster sink), Mailpit port-forward 확인 절차, PVC 로 데이터가 보존된다는 점, 그리고 **Mailpit 을 ingress 로 노출하지 말 것**을 명시한다.

- [ ] **Step 5: YAML 검증 + 커밋**

Run: `python3 -c "import yaml,sys; [list(yaml.safe_load_all(open(f))) for f in ['k8s/deployment.yaml','k8s/mail-sink.yaml']]; print('ok')"`

```bash
git add -A
git commit -F - <<'EOF'
fix(k8s): persist capsules on a PVC and always provision the mail secret

prod DB_URL 이 jdbc:h2:mem: 이라 파드가 재시작될 때마다 모든 캡슐이
사라지고 있었다. 타임캡슐 서비스에서 이건 제품 자체의 부정이다.
1Gi PVC + H2 파일 모드로 바꾸고 전략을 Recreate 로 고정한다.
timecapsule-mail-secret 은 조건부 생성이라 실제로 만들어진 적이 없어
앱이 조용히 localhost:25 로 떨어졌다 — 항상 만들되 자격증명이 없으면
in-cluster sink 를 가리킨다.

Constraint: 실제 SMTP 자격증명이 리포/클러스터 어디에도 없음
Rejected: 네임스페이스에 PostgreSQL 추가 | 노드 메모리가 이미 73%, 취미 규모에 과함
Confidence: high
Scope-risk: broad
Directive: Mailpit 을 ingress 로 노출하지 말 것 — 캡슐 본문이 담긴 메일 뷰어가 공개된다
Not-tested: PVC 가 가득 찼을 때의 H2 동작, 노드 교체 시 local-path 볼륨 이전

Co-Authored-By: Claude Opus 4.8 (1M context) <noreply@anthropic.com>
EOF
```

---

## Task 8: 빌드 · 배포 · QA

- [ ] **Step 1: 전체 빌드**

Run: `./gradlew clean bootWar --no-daemon`
Expected: BUILD SUCCESSFUL, `build/libs/*.war` 생성

- [ ] **Step 2: 로컬 스모크 (선택, prod 프로필 아님)**

Run: `SPRING_PROFILES_ACTIVE=dev java -jar build/libs/*.war` 후 `curl -sf localhost:3000/write -o /dev/null -w '%{http_code}\n'` → 200

- [ ] **Step 3: 배포**

Run: `./scripts/deploy-k3s.sh all`
Expected: rollout 성공, PVC Bound, mailpit Running

- [ ] **Step 4: QA — 아래 "QA 체크리스트" 전 항목 수행**

- [ ] **Step 5: 최종 리뷰어 서브에이전트 디스패치** (자기 승인 금지)

---

## QA 체크리스트 (배포 후 필수)

### A. 라우팅
- [ ] `curl -o /dev/null -w '%{http_code}' https://timecapsule.chatflow.ai.kr/` → 200
- [ ] `/write`, `/find`, `/my-capsule`, `/tutorial`, `/explore` 각각 → 200 text/html
- [ ] `/my-capsule?email=test@example.com` → 200
- [ ] `/js/nope.js` → 404 (index.html 이 아니라)
- [ ] `/actuator/health` → 200, `/actuator/env` → 401/403

### B. 캡슐 생성 → 조회 → 개봉
- [ ] `POST /api/v1/capsule` (openAt = 지금+2분) → 200, `xCoord/yCoord/biome/locationName` 포함
- [ ] `GET /api/v1/capsule/list?email=<수신자>` → 방금 만든 캡슐 포함
- [ ] `POST /{id}/open` (틀린 비밀번호) → 400
- [ ] `POST /{id}/open` (맞는 비밀번호, 개봉일 전) → 200, `content: null`
- [ ] 개봉일 경과 후 `POST /{id}/open` → 200, `content` 포함, `status: OPENED`

### C. 이메일 (핵심)
- [ ] 부팅 로그에 `=== TimeCapsule mail mode: ... ===` 가 찍힌다
- [ ] `TIMECAPSULE_NOTIFICATION_CRON` 을 1분으로 임시 오버라이드하거나 스케줄 주기를 기다린 뒤,
      개봉일이 지난 캡슐의 `isNotified` 가 `true` 로 바뀐다
- [ ] `kubectl -n timecapsule port-forward svc/timecapsule-mailpit 8025:8025` 후
      `curl -s localhost:8025/api/v1/messages | jq` 에 메일 1건
- [ ] 그 메일의 HTML 본문에 캡슐 제목·보낸이·좌표·바이옴명이 들어있다
- [ ] 본문 링크가 `https://timecapsule.chatflow.ai.kr/my-capsule?email=...` 이고 **그 링크가 200 을 준다**
- [ ] 메일 스크린샷을 증거로 남긴다

### D. 영속성
- [ ] 캡슐 생성 → `kubectl rollout restart` → 재기동 후 `GET /list?email=` 에 **그대로 남아있다**
- [ ] `kubectl -n timecapsule get pvc timecapsule-data` → Bound

### E. 프론트엔드 (Playwright)
- [ ] `/write` 5단계 위저드를 끝까지 진행 → **6단계 결과 카드에 좌표·바이옴이 표시된다**
- [ ] 오늘+3일을 고르면 저장된 `openAt` 이 그 날짜의 **KST 자정**이다 (하루 밀리지 않음)
- [ ] `/my-capsule` 에서 이메일로 조회 → 목록 표시 → 비밀번호 입력 → 개봉
- [ ] `/explore` 에 공개 캡슐이 보인다
- [ ] 데스크톱(1280)·모바일(390) 스크린샷에 레이아웃 깨짐 없음
- [ ] 콘솔 에러 없음

### F. 개인정보
- [ ] `GET /api/v1/capsule/public` 응답에 `receiverEmail` 이 **없다**
- [ ] Mailpit UI 가 ingress 로 노출되지 않는다 (`curl https://timecapsule.chatflow.ai.kr/mailpit` → SPA 셸 또는 404, Mailpit UI 아님)

### G. 마무리
- [ ] Cloudflare 캐시 퍼지 (프론트 변경 포함이므로 필수)
- [ ] 퍼지 후 재확인: 브라우저에서 하드 리로드 → 새 번들이 로드된다
