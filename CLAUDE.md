# CLAUDE.md - timecapsule

> harness init이 생성한 골격이다. TODO를 채워라 — `/harness-survey` 스킬을 쓰면
> 에이전트가 코드베이스를 훑어 채워준다. 이 파일은 생성 이후 harness가 건드리지 않는다.

## Project Overview

TODO: 이 프로젝트가 무엇인지 2-3문장.

## Architecture

TODO: 구성요소와 데이터 흐름. (감지된 preset: spring, vue)

## Build & Run Commands

- `./gradlew test` — 루트에서 그대로 동작 (실측 완료)
- `./gradlew check` — 루트에서 그대로 동작, 현재 `test`만 aggregate (실측 완료)
- `(cd vue && npm run lint)` — vue 앱이 루트가 아니라 `vue/` 하위라 반드시 cd 필요 (실측 완료)
- vue에는 `npm test`가 없다 — `vue/package.json`에 test 스크립트 자체가 미도입(jest/vitest 없음).
  pre-push 훅에서도 이 줄은 뺐다.

TODO: vue 유닛 테스트 러너를 붙이면 위 gap을 채워라.

## Conventions

- 커밋: Conventional Commits + 트레일러 프로토콜 (`.claude/skills/` 및
  kit `core/conventions/commit-trailers.md` 참고)
- 시크릿: 공유 지침은 이 파일, 시크릿·접속정보는 `.claude/CLAUDE.local.md`(ignored) —
  값은 소스·생성물에 절대 쓰지 않는다
- 하네스: `.claude/`가 소스, `.codex/`·`.agents/`는 `harness render` 생성물(손편집 금지),
  `harness doctor`가 드리프트·시크릿을 감시한다

## Deploy

TODO: 배포 절차 (없으면 섹션 삭제).

## Gotchas

TODO: 이 리포에서 데인 함정들 — 다음 사람이 같은 데서 데이지 않게.
