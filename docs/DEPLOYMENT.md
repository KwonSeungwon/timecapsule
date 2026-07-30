# ⏺ TimeCapsule K3s 배포 가이드

## 0. 전제 (Prerequisites)
| 항목 | 값 |
| :--- | :--- |
| 배포 서버 | ksw@node.chatflow.ai.kr (SSH 키 인증) |
| K3s kubeconfig | /etc/rancher/k3s/k3s.yaml (sudo 필요) |
| Namespace | timecapsule |
| Image tag | :latest (pullPolicy: Never) |
| 도메인 | timecapsule.chatflow.ai.kr |

## 1. 한 방에 배포

```bash
./scripts/deploy-k3s.sh all
# 또는 단계별: infra | secrets | images | deploy
```

`scripts/.env.deploy` (gitignored) 에서 `K3S_HOST` / `K3S_USER` / `NAMESPACE` /
`KUBECONFIG` 와 아래 메일 변수를 읽는다. **실제 시크릿 값은 이 파일에만 두고
저장소에 커밋하지 않는다.**

## 2. 데이터 영속성 (PVC)

캡슐은 `PersistentVolumeClaim/timecapsule-data` (1Gi, `local-path`) 위의 H2
파일에 저장된다 — `jdbc:h2:file:/data/timecapsule`.

- 예전엔 `jdbc:h2:mem:` 이었다. 파드가 재시작되면 묻어둔 캡슐이 전부 사라졌다.
  타임캡슐 서비스에서 이건 제품 자체의 부정이라 파일 모드로 바꿨다.
- RWO 볼륨 + 단일 replica 조합이라 Deployment `strategy` 는 반드시
  **`Recreate`** 여야 한다. 롤링 업데이트로 바꾸면 새 파드가 볼륨을 못 잡고
  두 프로세스가 같은 H2 파일을 열려다 배포가 교착된다.
- 백업은 노드에서:
  ```bash
  ssh ksw@node.chatflow.ai.kr \
    "sudo cp -a /var/lib/rancher/k3s/storage/*timecapsule-data*/ ~/timecapsule-backup-$(date +%F)/"
  ```

## 3. 메일 발송 — 두 가지 모드

`timecapsule-mail-secret` 는 `deploy-k3s.sh secrets` 가 **항상** 만든다.
시크릿이 없으면 앱이 조용히 `localhost:25` 로 떨어져 메일이 한 통도 안 나가는데
로그도 조용한 상태가 된다 — 그래서 조건부 생성을 없앴다.

부팅 시 어느 모드인지 `CapsuleNotificationMailer` 가 WARN 로그로 찍는다:

```
=== TimeCapsule mail mode: SINK (timecapsule-mailpit...:1025) — ... ===
```

### 3-1. SINK 모드 (기본값)

`DEPLOY_MAIL_HOST` 를 설정하지 않으면 in-cluster Mailpit
(`k8s/mail-sink.yaml`) 으로 붙는다. 메일 조립 → SMTP 왕복 → HTML 렌더까지
실제 경로가 전부 동작하지만, 메일은 실제 수신함이 아니라 Mailpit 에 쌓인다.

확인은 **port-forward 로만** 한다:

```bash
ssh ksw@node.chatflow.ai.kr \
  "sudo kubectl -n timecapsule port-forward svc/timecapsule-mailpit 8025:8025 --address 127.0.0.1 \
   --kubeconfig /etc/rancher/k3s/k3s.yaml"
# 또는 로컬에서 SSH 터널: ssh -L 8025:localhost:8025 ...
```

> ⚠️ **Mailpit 을 ingress 로 노출하지 않는다.** 메일 뷰어에는 캡슐 본문이
> 그대로 들어 있다. 공개하면 비밀번호 없이 내용이 새어나간다.

### 3-2. SMTP 모드 (실제 발송)

`scripts/.env.deploy` 에 아래를 채우고 `./scripts/deploy-k3s.sh secrets` 후
파드를 재시작하면 코드 변경 없이 전환된다.

```bash
DEPLOY_MAIL_HOST=smtp.example.com
DEPLOY_MAIL_PORT=587
DEPLOY_MAIL_USERNAME=...
DEPLOY_MAIL_PASSWORD=...          # 앱 비밀번호. 저장소에 커밋 금지
DEPLOY_MAIL_FROM=noreply@timecapsule.chatflow.ai.kr
DEPLOY_MAIL_SMTP_AUTH=true
DEPLOY_MAIL_SMTP_STARTTLS_ENABLE=true
```

### 3-3. readiness 와 메일은 분리돼 있다

`application.yml` 에서 `management.health.mail.enabled: false`. SMTP 도달성으로
readiness 를 판정하면 메일 장애가 곧 사이트 전체 다운이 된다 — 되돌리지 말 것.

## 4. 알림 스케줄러

`TimecapsuleScheduler` 가 `timecapsule.notification.cron` (기본 매시 정각) 마다
개봉일이 지난 미발송 캡슐을 찾아 메일을 보낸다.

- 실패해도 `isNotified` 는 `false` 로 남고 `notifyAttempts` 만 올라간다 →
  다음 주기에 재시도.
- `timecapsule.notification.max-attempts` (기본 10) 회 실패하면 포기한다.
  SMTP 가 영영 안 되는 캡슐이 매 시간 큐를 붙잡는 걸 막기 위한 것.
- 수신 이메일이 비어 있으면 발송하지 않고 바로 알림완료 처리한다.

## 5. 수동 배포 (스크립트를 못 쓸 때)

```bash
# 1. 빌드 (Java 17 + Vue 통합)
./gradlew clean bootWar --no-daemon

# 2. Docker 이미지 빌드 (amd64)
docker build --platform linux/amd64 -t timecapsule:latest .

# 3. 이미지 전송 및 K3s 주입
docker save timecapsule:latest | gzip | \
  ssh ksw@node.chatflow.ai.kr "sudo k3s ctr images import -"

# 4. K8s 리소스 적용 및 롤아웃
ssh ksw@node.chatflow.ai.kr "sudo kubectl apply -f - --kubeconfig /etc/rancher/k3s/k3s.yaml" < k8s/mail-sink.yaml
ssh ksw@node.chatflow.ai.kr "sudo kubectl apply -f - --kubeconfig /etc/rancher/k3s/k3s.yaml" < k8s/deployment.yaml
ssh ksw@node.chatflow.ai.kr "sudo kubectl rollout restart deployment/timecapsule-deployment -n timecapsule --kubeconfig /etc/rancher/k3s/k3s.yaml"
ssh ksw@node.chatflow.ai.kr "sudo kubectl rollout status  deployment/timecapsule-deployment -n timecapsule --timeout=5m --kubeconfig /etc/rancher/k3s/k3s.yaml"
```

## 6. 배포 후 확인

```bash
# 메일 모드 로그
ssh ksw@node.chatflow.ai.kr "sudo kubectl logs -n timecapsule deploy/timecapsule-deployment \
  --kubeconfig /etc/rancher/k3s/k3s.yaml | grep 'mail mode'"

# 헬스
curl -sS http://timecapsule.chatflow.ai.kr/actuator/health

# 프론트엔드를 배포했다면 Cloudflare 캐시 퍼지 필수 (토큰은 .claude/CLAUDE.md 참고)
```
