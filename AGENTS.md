# Repository Guidelines

## Project Structure & Module Organization

This repository is a Spring Boot 3 application with a Vue 3 frontend, packaged together as a
single WAR (verified: `com.mini.timecapsule` has no `jwt` package — a JWT-based login endpoint
existed historically but was removed entirely once the service went non-member; see
`SpringSecurity.java` / `TimecapsuleScheduler.java` comments). Backend code lives under
`src/main/java/com/mini/timecapsule`, organized by role: `controller` (2 classes:
`HomeController`, `LetterController`), `service`, `dao` (JPA repository interfaces, still named
with a `*Repository` suffix despite the package name), `model`, `dto`, `config` +
`configurations` (two separate config packages — `config` holds `ContentFilterSeeder` /
`SpaForwardingConfig`, `configurations` holds `SpringSecurity`), `exception`, and `utils`
(+ `utils/bind`). Resources are in `src/main/resources`: `application.yml` (single file —
do not add `application.properties` back, it loads after `.yml` and silently overrides it),
Thymeleaf templates (only `templates/mail/capsule-opened.html` is actually rendered, via
`CapsuleNotificationMailer` → `ITemplateEngine.process("mail/capsule-opened", ...)`, for the
notification email body — `templates/home.html` exists but is not referenced from any
controller), and the built Vue static output in `static/`. Tests live in
`src/test/java/com/mini/timecapsule` and mirror production packages. The editable Vue app is in
`vue/src` (Vue **3.2.37**, not Vue 2 — `vue/package.json`); deployment files are in `Dockerfile`,
`k8s/`, `scripts/`, and `docs/` (`docs/DEPLOYMENT.md` is the authoritative deploy guide). The
root-level `front/` directory is dead weight — it holds only an untracked `node_modules/`, zero
files are git-tracked under it, and the real frontend is `vue/` (see `.gitignore` comment).

## Build, Test, and Development Commands

- `./gradlew bootRun`: run the Spring Boot app locally.
- `./gradlew test`: run JUnit 5 and Spring test suites.
- `./gradlew build`: compile Java, test, build Vue, and package the WAR.
- `cd vue && npm install`: install frontend dependencies.
- `cd vue && npm run serve`: start the Vue dev server on `localhost:3030`.
- `cd vue && npm run build`: build Vue assets for Spring static hosting.
- `cd vue && npm run lint`: run ESLint for Vue and JavaScript files.

Gradle `processResources` runs the Vue build, so backend builds expect `vue/node_modules` to exist.

⚠️ `vue/vue.config.js`'s `outputDir` points directly at `../src/main/resources/static` (not
`vue/dist`), and `vue-cli-service build` empties `outputDir` before writing — so `npm run build`
(and any Gradle task that reaches `processResources`, including `test`/`check`/`bootRun`/`build`)
can overwrite the built static assets. In practice `:buildVue` is Gradle-cached and reports
`UP-TO-DATE` as long as `vue/src`, `vue/public`, `package.json`, `package-lock.json`,
`vue.config.js`, and `babel.config.js` haven't changed (verified: `./gradlew test`/`check` both
kept `:buildVue UP-TO-DATE` and left `static/` untouched) — but right after editing anything
under `vue/`, the next Gradle command that touches `processResources` will really run
`npm run build`. Never add a task that copies from `vue/dist` into `static/` — it has been tried
twice and both times shipped a WAR missing the whole frontend, because `vue/dist` is never
produced (see `build.gradle` comments and commits `15efa69d`, `aabdc8c0`). Full detail:
`CLAUDE.md` → Build & Run Commands / Gotchas.

## Coding Style & Naming Conventions

Use Java 17 and Spring conventions. Keep classes in package paths matching their responsibilities, with `*Controller`, `*Service`, `*Repository`, `*Dto`/`*DTO`, and `*Request` suffixes. Prefer constructor injection for new Spring components. Use 4-space Java indentation and keep Lombok usage consistent with nearby models and DTOs. Vue files use PascalCase component filenames where possible and the ESLint rules in `vue/package.json`.

## Testing Guidelines

Backend tests use JUnit Platform via `spring-boot-starter-test` and `spring-security-test`. Name tests after the unit under test, for example `LetterServiceTest` or `LocationMapperTest`, and place them in the matching package under `src/test/java`. Add focused service or utility tests for business logic changes. Run `./gradlew test` before opening a pull request.

## Commit & Pull Request Guidelines

Recent commits follow Conventional Commits with a scope, e.g. `fix(assets): ...`,
`feat(deploy): ...`, `chore(build): ...` (verified via `git log --oneline`), and non-trivial
changes carry a structured trailer block — `Constraint:`, `Rejected:`, `Confidence:`,
`Scope-risk:`, `Directive:`, `Not-tested:` (see an example in commit `1895734f`). See
`CLAUDE.md` → Conventions and `.claude/skills/` / the kit's `core/conventions/commit-trailers.md`
for the full protocol. Keep each commit focused. Pull requests should describe behavior changes,
list verification commands, link related issues, and include screenshots for visible Vue UI
changes.

## Security & Configuration Tips

Do not commit credentials, mail passwords, database secrets, or production host values. (Note:
this app has no JWT secret to protect — the JWT-based `/api/timecapsule/login` endpoint was
removed entirely, it was issuing signed tokens without password verification; see
`SpringSecurity.java`.) Real deploy secrets (DB password, SMTP credentials) belong only in
`scripts/.env.deploy` (gitignored — template: `scripts/.env.deploy.example`); this file was
previously tracked by git and leaked a DB password plus the deploy server account, so re-check
with `git check-ignore -v scripts/.env.deploy` if anything about `.gitignore` changes. Keep
local overrides out of version control and document new required environment variables in
`docs/`.
