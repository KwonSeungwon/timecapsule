#!/usr/bin/env bash
set -euo pipefail

# TimeCapsule K3s 배포 스크립트 (개인 서버 최적화 버전)
PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"

# .env.deploy 로드
ENV_FILE="$PROJECT_ROOT/scripts/.env.deploy"
if [ -f "$ENV_FILE" ]; then
  source "$ENV_FILE"
fi

# 필수 환경 변수 검증
: "${K3S_HOST:?K3S_HOST를 설정하세요}"
: "${K3S_USER:?K3S_USER를 설정하세요}"
: "${NAMESPACE:?NAMESPACE를 설정하세요}"
: "${KUBECONFIG:?KUBECONFIG 명령어를 설정하세요}"

ssh_k3s() {
  ssh -o StrictHostKeyChecking=no "$K3S_USER@$K3S_HOST" "$@"
}

scp_k3s() {
  scp -o StrictHostKeyChecking=no "$@" "$K3S_USER@$K3S_HOST:~/"
}

step_infra() {
  echo "=== Step 1: Deploy Infrastructure (Namespace) ==="
  ssh_k3s "$KUBECONFIG create namespace $NAMESPACE --dry-run=client -o yaml | $KUBECONFIG apply -f -"
}

step_secrets() {
  echo "=== Step 2: Create Secrets ==="
  ssh_k3s "$KUBECONFIG -n $NAMESPACE create secret generic timecapsule-db-secret \
    --from-literal=DB_PASSWORD='${DEPLOY_DB_PASSWORD:-timecapsule_pw}' \
    --dry-run=client -o yaml | $KUBECONFIG apply -f -"
  
  ssh_k3s "$KUBECONFIG -n $NAMESPACE create secret generic timecapsule-jwt-secret \
    --from-literal=JWT_SECRET='${DEPLOY_JWT_SECRET:-secret}' \
    --dry-run=client -o yaml | $KUBECONFIG apply -f -"

  # 메일 시크릿은 **항상** 만든다. DEPLOY_MAIL_HOST 가 없으면 in-cluster sink(mailpit)를
  # 가리킨다 — 예전처럼 시크릿이 아예 없으면 앱이 조용히 localhost:25 로 떨어져
  # 메일이 한 통도 안 나가는데 로그도 조용한 상태가 된다.
  if [ -n "${DEPLOY_MAIL_HOST:-}" ]; then
    MAIL_HOST="$DEPLOY_MAIL_HOST"
    MAIL_PORT="${DEPLOY_MAIL_PORT:-587}"
    MAIL_SMTP_AUTH="${DEPLOY_MAIL_SMTP_AUTH:-true}"
    MAIL_STARTTLS="${DEPLOY_MAIL_SMTP_STARTTLS_ENABLE:-true}"
  else
    MAIL_HOST="timecapsule-mailpit.${NAMESPACE}.svc.cluster.local"
    MAIL_PORT="1025"
    MAIL_SMTP_AUTH="false"
    MAIL_STARTTLS="false"
    echo "[!] DEPLOY_MAIL_HOST 미설정 — in-cluster sink($MAIL_HOST:$MAIL_PORT)로 붙습니다."
    echo "[!] 메일은 실제 수신함이 아니라 mailpit 에 쌓입니다:"
    echo "[!]   kubectl -n $NAMESPACE port-forward svc/timecapsule-mailpit 8025:8025"
  fi

  ssh_k3s "$KUBECONFIG -n $NAMESPACE create secret generic timecapsule-mail-secret \
    --from-literal=MAIL_HOST='${MAIL_HOST}' \
    --from-literal=MAIL_PORT='${MAIL_PORT}' \
    --from-literal=MAIL_USERNAME='${DEPLOY_MAIL_USERNAME:-}' \
    --from-literal=MAIL_PASSWORD='${DEPLOY_MAIL_PASSWORD:-}' \
    --from-literal=MAIL_FROM='${DEPLOY_MAIL_FROM:-noreply@timecapsule.chatflow.ai.kr}' \
    --from-literal=MAIL_SMTP_AUTH='${MAIL_SMTP_AUTH}' \
    --from-literal=MAIL_SMTP_STARTTLS_ENABLE='${MAIL_STARTTLS}' \
    --dry-run=client -o yaml | $KUBECONFIG apply -f -"
}

step_images() {
  echo "=== Step 3: Build & Transfer Image ==="
  cd "$PROJECT_ROOT"
  
  # Spring Boot 빌드
  ./gradlew clean bootWar --no-daemon

  # Docker 빌드 (linux/amd64)
  IMAGE_NAME="timecapsule:latest"
  docker build --platform linux/amd64 -t $IMAGE_NAME .

  # 이미지 전송 (gzip 스트림 방식)
  echo "Transferring and Importing image directly..."
  docker save $IMAGE_NAME | gzip | ssh "$K3S_USER@$K3S_HOST" "sudo k3s ctr images import -"
}

step_deploy() {
  echo "=== Step 4: Kubernetes Deploy ==="
  # 메일 sink 를 먼저 올린다 — 앱이 부팅하며 SMTP 대상을 잡을 때 이미 떠 있어야 한다.
  # 실제 SMTP 를 쓰더라도 남아 있어서 해로울 게 없는 리소스다(ingress 노출 안 함).
  ssh_k3s "$KUBECONFIG apply -f -" < "$PROJECT_ROOT/k8s/mail-sink.yaml"

  # PVC / Ingress / Deployment 적용
  ssh_k3s "$KUBECONFIG apply -f -" < "$PROJECT_ROOT/k8s/deployment.yaml"

  echo "Restarting deployment to apply new image..."
  ssh_k3s "$KUBECONFIG rollout restart deployment/timecapsule-deployment -n $NAMESPACE"
  # Recreate + PVC 바인딩이라 롤링보다 느리다. 2분은 빠듯하다.
  ssh_k3s "$KUBECONFIG rollout status deployment/timecapsule-deployment -n $NAMESPACE --timeout=5m"
}

case "${1:-all}" in
  infra)   step_infra ;;
  secrets) step_secrets ;;
  images)  step_images ;;
  deploy)  step_deploy ;;
  all)
    step_infra
    step_secrets
    step_images
    step_deploy
    echo "=== TimeCapsule deployment complete! ==="
    ssh_k3s "$KUBECONFIG get all -n $NAMESPACE"
    ssh_k3s "$KUBECONFIG get ingress -n $NAMESPACE"
    ;;
  *) echo "Usage: $0 {infra|secrets|images|deploy|all}" ;;
esac
