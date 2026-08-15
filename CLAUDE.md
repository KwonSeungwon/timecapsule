# CLAUDE.md - timecapsule

> harness init이 생성한 골격이며, `/harness-survey`로 2026-08-15 실측 채움을 마쳤다.
> 이 파일은 생성 이후 harness가 건드리지 않는다 — 프로젝트 소유이니 이어서 갱신할 때도
> 기존 서술은 지우지 말고 보강할 것. 남은 TODO(vue 유닛 테스트 러너 갭)는 실제로 아직
> 해결되지 않은 항목이라 의도적으로 남겨뒀다.

## Project Overview

TimeCapsule("사이버 타임캡슐")은 회원가입 없이 이메일만으로 쓰는 온라인 타임캡슐 편지
서비스다. 사용자가 편지(캡슐)를 쓰고 개봉 예정일·선택적 비밀번호를 지정하면, 그 시각이
지난 뒤 스케줄러가 이메일로 개봉 알림을 보낸다. 백엔드 Spring Boot 3 WAR 한 개가 REST API와
Vue 3 SPA 정적 자원을 함께 서빙하며, K3s에 `timecapsule.chatflow.ai.kr`로 배포된다
(chat_flow와 자매 프로젝트 — 별도 리포, 같은 개인 K3s 인스턴스/Cloudflare 존 공유).

## Architecture

- **단일 WAR 서빙**: `war` 플러그인(+`bootWar`)으로 패키징되는 Spring Boot 3.2.5(Java 17)
  앱 하나가 `/api/v1/capsule/**` REST와 Vue 3 SPA 정적 자원(`src/main/resources/static`)을
  같은 origin에서 함께 낸다. `HomeController`가 `/` → `forward:/index.html`로 넘겨
  vue-router history 모드 라우팅을 서버에서 받아준다(`SpaRoutingTest`가 검증).
  `tasks.named('war') { enabled = false }`로 plain war는 꺼서 `bootWar` 산출물 하나만
  남긴다(build.gradle 주석: Dockerfile이 `build/libs/*.war`를 글롭으로 집기 때문에
  둘 다 있으면 COPY가 엉뚱한 war를 집을 수 있다).
- **패키지 구조**: `com.mini.timecapsule` 아래 `controller`(2개: `HomeController`,
  `LetterController`) / `service` / `dao` / `model` / `dto` / `config` / `configurations` /
  `exception` / `utils`. 전체 컨트롤러/서비스 목록은 `find src/main/java -iname
  "*Controller*.java"` 등으로 확인.
- **API 표면**: `LetterController`(`/api/v1/capsule`) — `POST` 캡슐 생성,
  `GET /list?email=` 이메일별 목록, `POST /{id}/open` 개봉(비밀번호 검증), `GET /public`
  공개 캡슐 탐색. 인증이 없는 비회원 서비스라 `@CrossOrigin("*")`을 다시 붙이지 말 것
  (LetterController.java 주석 — 이메일만 알면 `/list` 응답이 털린다).
- **영속성**: dev/test는 `jdbc:h2:mem:testdb`, prod는 `jdbc:h2:file:/data/timecapsule` +
  PVC `timecapsule-data`(1Gi, `local-path`) 파일 모드(`k8s/deployment.yaml`). RWO 볼륨 +
  단일 replica라 Deployment `strategy`는 반드시 `Recreate`. `application.yml` 상단 경고:
  `application.properties`를 같은 위치에 다시 만들지 말 것 — `.properties`가 `.yml`보다
  나중에 로드되어 값을 덮어쓴다(과거 `ddl-auto=create` 잔재로 운영 데이터가 날아간 사고).
  참고: `build.gradle`에 `mariadb-java-client` 런타임 의존성이 남아 있지만, 리포 전체
  검색 결과(`grep -rn mariadb`) 활성 datasource 설정은 없다 — 현재 실제 DB는 H2뿐이다
  (README의 MariaDB 배지는 이 상태와 어긋난다 — 확인된 사실만 적는다).
- **메일/알림**: `@EnableScheduling` + `TimecapsuleScheduler`가
  `timecapsule.notification.cron`(기본 매시 정각, prod는 env로 매분) 주기로 개봉일이
  지난 미발송 캡슐을 찾아 `CapsuleNotificationMailer` → Spring Mail로 발송한다. 실패해도
  `isNotified`는 `false`로 남고 `notifyAttempts`만 올려 재시도하며 `max-attempts`(기본
  10)에서 포기한다. SMTP 타임아웃 3종(connect/read/write, 기본 10s)을 명시 설정 —
  JavaMail 기본값이 무한이라 방화벽이 SYN을 DROP하면 스케줄러 스레드가 통째로 멈춘다
  (`application.yml` 주석). `management.health.mail.enabled: false` — SMTP 도달성으로
  readiness를 판정하면 메일 장애가 사이트 전체 다운으로 번지므로 의도적으로 끔.
- **프론트엔드**: Vue **3**(`^3.2.37`) + `vue-router` 4(history 모드) + `@vue/cli-service
  ~5.0.0`, `vue/` 하위. 라우트: `/`, `/write`, `/find`, `/my-capsule`, `/tutorial`,
  `/explore` + catch-all(`vue/src/router/index.js`). *(참고: 이 리포는 Vue 2가 아니라
  Vue 3다 — `vue/package.json`의 `"vue": "^3.2.37"`, `"vue-router": "^4.0.3"`으로 실측
  확인. `@vue/cli-service` 메이저 버전과 Vue 자체 메이저 버전은 별개다.)*
- **죽은 코드/디렉터리**: 루트의 `front/`는 `node_modules`만 있고 git에 추적된 파일이
  0개(`git ls-files front/`) — 실제 프론트엔드는 `vue/`뿐이다(`.gitignore` 주석에도
  명시: "front/에 21,271개(~102MB)가 추적되고 있었다"). 루트 `node_modules/`도 통째로
  무시되지만 `node_modules/.package-lock.json` 1개 파일은 여전히 git에 추적 중이다
  (실측: `git ls-files node_modules/`).

## Build & Run Commands

- `./gradlew test` — 루트에서 그대로 동작 (실측 완료, 재검증: 2026-08-15 —
  116 tests, 0 failures/0 errors/0 skipped, 순수 테스트 실행 시간 합계 2.82s,
  `--rerun`로 강제 재실행한 전체 빌드 wall time 13s. `build/test-results/test/TEST-*.xml`
  23개 파일 집계.)
- `./gradlew check` — 루트에서 그대로 동작, 현재 `test`만 aggregate (실측 완료,
  재검증: `:check UP-TO-DATE`, 별도 verification 태스크 없음)
- `(cd vue && npm run lint)` — vue 앱이 루트가 아니라 `vue/` 하위라 반드시 cd 필요
  (실측 완료 — 단, 현재 실패 상태다. 아래 Gotchas 참고)
- vue에는 `npm test`가 없다 — `vue/package.json`에 test 스크립트 자체가 미도입(jest/vitest 없음).
  pre-push 훅에서도 이 줄은 뺐다.
- **`./gradlew test`/`check`도 태스크 그래프에 `:buildVue`(→ `npm run build`)가 포함된다**
  (`processResources { dependsOn buildVue }`, `test`가 `main` 소스셋 output을 통해
  `processResources`를 끌어옴). `--dry-run`과 실제 실행 모두로 확인(`:buildVue`가 그래프에
  나타남). 단, `vue/src`·`vue/public`·`package.json`·`vue.config.js`·`babel.config.js`가
  안 바뀐 상태라면 `:buildVue UP-TO-DATE`로 스킵되어 `npm run build`가 실제로는 돌지
  않는다 — 이번 서베이에서 `test`/`check`를 실행해도 매번 `:buildVue UP-TO-DATE`만
  찍혔다(`static/`은 그대로). **vue/ 변경 직후에 `./gradlew test`를 돌리면 이 태스크가
  실제로 실행되어 `npm run build`가 도는 셈**이니 그 상태에서는 이 커맨드도 build류로
  취급할 것.

TODO: vue 유닛 테스트 러너를 붙이면 위 gap을 채워라.

## Conventions

- 커밋: Conventional Commits + 트레일러 프로토콜 (`.claude/skills/` 및
  kit `core/conventions/commit-trailers.md` 참고)
- 시크릿: 공유 지침은 이 파일, 시크릿·접속정보는 `.claude/CLAUDE.local.md`(ignored) —
  값은 소스·생성물에 절대 쓰지 않는다
- 하네스: `.claude/`가 소스, `.codex/`·`.agents/`는 `harness render` 생성물(손편집 금지),
  `harness doctor`가 드리프트·시크릿을 감시한다

## Deploy

전체 절차는 `docs/DEPLOYMENT.md` 참고 — 여기는 실측 요약만.

- **스크립트**: `scripts/deploy-k3s.sh {infra|secrets|images|deploy|all}` (실측:
  `case` 서브커맨드 4개 + `all`. `all`은 네 단계를 순서대로 실행)
  - `infra`: namespace 생성/apply
  - `secrets`: DB 시크릿 + 메일 시크릿 생성. `timecapsule-mail-secret`은 **항상**
    만든다 — `DEPLOY_MAIL_HOST` 미설정 시 in-cluster Mailpit
    (`timecapsule-mailpit.<namespace>.svc.cluster.local:1025`)로 자동 폴백한다
    (스크립트 주석: 예전처럼 시크릿 자체가 없으면 앱이 조용히 `localhost:25`로
    떨어져 메일이 한 통도 안 나가는데 로그도 조용해진다)
  - `images`: `./gradlew clean bootWar --no-daemon` → `docker build --platform
    linux/amd64` → `docker save | gzip | ssh ... k3s ctr images import -`
  - `deploy`: `k8s/mail-sink.yaml` → `k8s/deployment.yaml` apply, 이어서
    `rollout restart` + `rollout status --timeout=5m`
- **네임스페이스**: `timecapsule` (`k8s/deployment.yaml`에 하드코딩된 값으로 실측)
- **도메인**: `timecapsule.chatflow.ai.kr` (`k8s/deployment.yaml`의 Ingress host,
  traefik ingressClass, `ssl-redirect: "false"` — TLS는 Cloudflare가 앞단에서 종단)
- **메일 2모드**: 기본은 in-cluster Mailpit sink(`k8s/mail-sink.yaml`,
  `axllent/mailpit:v1.20`) — **ingress로 노출하지 않는다**(메일 뷰어에 캡슐 본문이
  그대로 들어있어 공개하면 새어나간다). 확인은 `kubectl -n timecapsule port-forward
  svc/timecapsule-mailpit 8025:8025`로만. 실 SMTP 전환은 `scripts/.env.deploy`의
  `DEPLOY_MAIL_HOST` 등을 채우고 `secrets` 재실행 + 파드 재시작.
- **데이터 백업**: 노드의 `/var/lib/rancher/k3s/storage/*timecapsule-data*/`를 직접
  cp(`docs/DEPLOYMENT.md` 2절 명령 참고) — PVC라 스냅샷 API가 따로 없다.
- **CI/CD 없음**: `.github/` 디렉터리 자체가 존재하지 않는다(실측: `ls -la .github`
  → No such file or directory). 배포는 전부 수동 — `scripts/deploy-k3s.sh` 또는
  `docs/DEPLOYMENT.md` 5절의 수동 4단계.
- **Cloudflare 캐시 퍼지**: `docs/DEPLOYMENT.md:138`에 "프론트엔드를 배포했다면
  Cloudflare 캐시 퍼지 필수(토큰은 `.claude/CLAUDE.md` 참고)"라고 이미 적혀 있다 —
  프론트 변경 배포 후 퍼지가 필요하다는 것 자체는 이 리포 안에서 근거가 있다. 다만
  "존을 chat_flow(`app.chatflow.ai.kr`)와 공유하므로 `purge_everything`을 쓰지 말고
  변경된 URL만 퍼지하라"는 제약은 이 리포 파일에서는 확인되지 않았다 — 팀 관례로만
  적어둔다(출처: chat_flow 쪽 배포 지침).
- **배포 서버/계정, 실제 시크릿 값**: 이 문서에 옮기지 않는다. 서버·네임스페이스 등
  운영 메모는 이미 `docs/DEPLOYMENT.md`에 커밋되어 있고, DB 비밀번호·메일
  자격증명 등 진짜 시크릿은 `scripts/.env.deploy`(gitignored, 템플릿은
  `scripts/.env.deploy.example`)에만 둔다. `.gitignore` 주석에 남아있는 과거
  사고 기록: 이 파일이 한때 "gitignored"라고 문서에만 적혀 있었을 뿐 실제로는
  git에 추적되어 DB 비밀번호와 배포 서버 계정이 저장소에 노출된 적이 있다 —
  `git check-ignore -v scripts/.env.deploy`로 지금은 제대로 무시되는지 재확인
  가능(실측: 현재는 정상적으로 ignore됨).

## Gotchas

- **`npm run build`가 Spring `static/`을 통째로 갈아엎는다.** `vue/vue.config.js`의
  `outputDir`이 `vue/dist`가 아니라 `../src/main/resources/static`을 직접 가리킨다
  (vue.config.js:7, 파일 안에 이미 경고 주석 있음). `vue-cli-service build`는
  outputDir을 기본으로 비우고 시작하므로 여기 손으로 정적 파일을 두면 다음 빌드에
  사라진다. **이 서베이에서도 `npm run build`/`./gradlew build`류는 실행하지
  않았다** — 대신 `./gradlew test`/`check`만 실행했고 `:buildVue`가
  `UP-TO-DATE`임을 매번 확인했다(위 Build & Run Commands 참고).
- **`processResources { dependsOn buildVue }` 후 static → 별도 복사 태스크를 만들지
  말 것.** build.gradle 주석: 두 번 만들었고 두 번 다 프론트엔드가 통째로 빠진
  WAR를 냈다(`vue/dist`에서 복사하는 태스크는 항상 0개 복사되는 no-op — `npm run
  build`가 이미 최종 위치에 직접 쓰기 때문). 실제 커밋 이력에도 이 문제로 인한
  수정이 있다: `15efa69d fix(build): copyVueBuildFiles를 제거한다 — 순정
  ./gradlew build가 프론트엔드를 지우고 있었다`, `aabdc8c0 chore(build): Vue 번들
  복사 전에 static을 비운다 — 옛 해시 번들이 WAR에 누적되던 문제`.
- **`@vue/cli-service` 5는 `CaseSensitivePathsPlugin`을 무조건 주입한다**
  (`vue/node_modules/@vue/cli-service/lib/config/base.js:206-207`, 실측 확인).
  대소문자만 다른 파일명(예: 소스는 `bamboo.png`인데 git 인덱스엔 `bamboo.PNG`)이
  있으면 대소문자 무시 파일시스템(macOS 기본)에서는 로컬 빌드가 조용히 통과하지만,
  클린 체크아웃(또는 case-sensitive FS)에서는 "Failed to compile" 로 죽는다 —
  실제 사례: 커밋 `1895734f`(bamboo/candybox/egg 3개 이미지). 이미지 추가 시
  확장자를 소문자로 통일하고, 대소문자만 바꿔야 할 때는 임시 이름을 거친 `git mv`
  두 번으로 할 것(맥에서 한 번에 rename하면 git 인덱스가 안 따라온다).
- **`vue lint`가 현재 실패 상태다.** `(cd vue && npm run lint)` 실행 결과(재확인,
  2026-08-15): `Explore.vue:76:9  error  Component name "Explore" should always be
  multi-word  vue/multi-word-component-names` — 1 error, 0 warnings로 실패.
  `scripts/hooks/pre-push`가 이 lint를 포함하고 있으므로, **pre-push 훅
  (`git config core.hooksPath scripts/hooks`)을 활성화하기 전에 이 위반부터
  고쳐야 한다** — 지금 활성화하면 관련 없는 push까지 전부 막힌다.
- **`front/`(루트)는 죽은 디렉터리다.** node_modules만 있고 git 추적 파일 0개
  (`git ls-files front/`) — 실제 Vue 앱은 `vue/`뿐이다. 헷갈려서 잘못 건드리지
  말 것.
- **prod 영속성 = H2 file-mode + PVC, 단일 replica만 가능.** `jdbc:h2:file:/data
  /timecapsule` + `DB_CLOSE_DELAY=10;DB_CLOSE_ON_EXIT=TRUE` 조합을 함부로 바꾸지
  말 것 — `DB_CLOSE_DELAY=-1`과 `DB_CLOSE_ON_EXIT=FALSE`를 같이 쓰면 DB가 영영
  안 닫힌다(`k8s/deployment.yaml` 주석). RWO 볼륨이라 Deployment `strategy`를
  `Recreate`가 아닌 값으로 바꾸면 새 파드가 볼륨을 못 잡고 배포가 교착된다.
- **메일 SMTP 타임아웃 3종을 지우지 말 것.** JavaMail 기본 타임아웃은 무한이라,
  egress 방화벽이 SMTP를 REJECT가 아니라 DROP하면(클라우드에서 흔함) `send()`가
  응답 없이 영원히 매달린다. 스케줄러 스레드가 하나뿐이라 그 순간 알림 파이프라인
  전체가 멈추는데 예외도 없고 health의 mail 인디케이터도 꺼져 있어(의도적으로)
  파드는 계속 Ready로 보인다(`application.yml` 주석).
  `management.health.mail.enabled: false`도 같은 이유로 켜지 말 것 — SMTP 도달성
  으로 readiness를 판정하면 메일 장애가 사이트 전체 다운이 된다.
- **CI 없음.** `.github/` 디렉터리가 존재하지 않는다 — 회귀 방지용 clean checkout
  빌드(위 CaseSensitivePathsPlugin 건 포함)를 걸 CI 자체가 없다는 뜻이므로, 이런
  종류의 사고는 로컬 작업 트리에서는 재현되지 않고 클린 체크아웃에서만 터진다는
  점을 기억할 것.
- **`build.gradle`의 `mariadb-java-client` 의존성은 죽은 코드로 보인다.** 리포
  전체에서 MariaDB 관련 활성 설정을 찾지 못했다(`grep -rn -i mariadb` — gradle
  의존성 선언 외 0건). README.md의 MariaDB 배지도 실제 배포 구성(H2)과 어긋난다.
  제거 여부는 이번 서베이 범위 밖이라 판단하지 않는다 — 사실만 기록.
