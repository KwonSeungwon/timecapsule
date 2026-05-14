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
  # Ingress 및 Deployment 적용
  ssh_k3s "$KUBECONFIG apply -f -" < "$PROJECT_ROOT/k8s/deployment.yaml"
  
  echo "Restarting deployment to apply new image..."
  ssh_k3s "$KUBECONFIG rollout restart deployment/timecapsule-deployment -n $NAMESPACE"
  ssh_k3s "$KUBECONFIG rollout status deployment/timecapsule-deployment -n $NAMESPACE --timeout=2m"
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
