# ⏺ TimeCapsule K3s 배포 가이드

## 0. 전제 (Prerequisites)
| 항목 | 값 |
| :--- | :--- |
| 배포 서버 | ksw@node.chatflow.ai.kr (SSH 키 인증) |
| K3s kubeconfig | /etc/rancher/k3s/k3s.yaml (sudo 필요) |
| Namespace | timecapsule |
| Image tag | :latest (pullPolicy: Never) |
| 도메인 | timecapsule.chatflow.ai.kr |

## 1. 빌드 및 배포 명령어
```bash
# 1. 빌드 (Java 17 + Vue 통합)
./gradlew clean bootWar --no-daemon

# 2. Docker 이미지 빌드 (amd64)
docker build --platform linux/amd64 -t timecapsule:latest .

# 3. 이미지 전송 및 K3s 주입
docker save timecapsule:latest | gzip > /tmp/timecapsule.tar.gz
scp -i /Users/seungwon-kwon/web-app-key.pem /tmp/timecapsule.tar.gz ksw@node.chatflow.ai.kr:~/
ssh -i /Users/seungwon-kwon/web-app-key.pem ksw@node.chatflow.ai.kr "sudo k3s ctr images import ~/timecapsule.tar.gz"

# 4. K8s 리소스 적용 및 롤아웃
ssh -i /Users/seungwon-kwon/web-app-key.pem ksw@node.chatflow.ai.kr "kubectl apply -f -" < k8s/deployment.yaml
ssh -i /Users/seungwon-kwon/web-app-key.pem ksw@node.chatflow.ai.kr "kubectl rollout restart deployment/timecapsule-deployment -n timecapsule"
```
