<template>
  <transition name="letter-fade">
    <div class="letter-overlay">
      <!-- ?. 가 아니라 || 인 이유. prop 의 default 는 undefined 에만 적용된다 —
           null 이나 "" 는 default 를 거치지 않고 그대로 통과하는데, 그때
           null?.toLowerCase() 는 undefined 라 클래스가 아예 안 붙고 편지지 배경이
           통째로 사라진다.
           지금 서버 경로에서 그런 값이 오지는 않는다: CapsuleResponse 가
           @JsonInclude(NON_NULL) 이라 letterPaperType 이 null 이면 키 자체를 빼고
           내려주고 → JS 에서 undefined → prop default 가 정상 동작한다
           (호출부는 myCapsule.vue 한 곳뿐이다).
           즉 아래 || 는 그 계약이 깨질 때(NON_NULL 제거, "" 저장, 테스트에서 DOM 직접
           주입)를 위한 방어다. myCapsule.vue 의 capsuleClass() 도 같은 이유로 || 를 쓴다. -->
      <div class="letter-container" :class="(letterType || 'LETTER').toLowerCase()">
        <div class="letter-body">
          <div class="content-wrapper">
            <span class="letter-text">{{ contents }}</span>
          </div>
        </div>
        <div class="letter-footer">
          <button class="pixel-btn primary" @click="close">닫기</button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script>
export default {
  name: "LetterView",
  props : {
    contents : { type : String, default: "" },
    letterType : { type : String, default : "LETTER" }
  },
  methods : {
    close () {
      this.$emit('popup_res');
    }
  }
}
</script>

<style scoped>
.letter-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.8);
  z-index: 10000;
  display: flex;
  justify-content: center;
  backdrop-filter: blur(4px);

  /* ⚠️ align-items:center 로 되돌리지 말 것, overflow-y 도 지우지 말 것.
   * 이 오버레이는 position:fixed 라 문서 스크롤 영역이 0 이다. 편지가 뷰포트보다
   * 커지는 순간 넘친 부분은 어떤 방법으로도 볼 수 없고, 그 넘치는 부분이 하필
   * "닫기" 버튼이라 캡슐을 닫지 못해 새로고침 말고는 빠져나갈 수가 없었다.
   * 실측: 360x400 에서 닫기 버튼이 y=388..432 로 48px 밖에 있었다.
   * align-items:center 는 넘칠 때 위아래로 동시에 삐져나가 스크롤해도 윗부분에
   * 닿지 못하는 알려진 함정이라, 대신 .letter-container 의 margin:auto 로 센터링한다
   * (여유가 있으면 중앙, 넘치면 auto 가 0 이 되어 위부터 스크롤). */
  align-items: flex-start;
  overflow-y: auto;
  padding: 16px 0;
}

.letter-container {
  width: 90%;
  max-width: 320px;
  display: flex;
  flex-direction: column;
  align-items: center;
  animation: slideUp 0.4s ease-out;
  /* 위 오버레이 주석 참고 — align-items:center 대신 쓰는 센터링이다. */
  margin: auto 0;

  /* 편지지 박스의 단일 소스. .letter-body 의 width / height / padding 이 전부 여기서
   * 파생되므로, 크기를 바꾸려면 이 두 줄만 고치면 된다. 예전엔 width 가 height 의
   * min(400px, 70vh) 를 손으로 복붙해 둔 상태라 한쪽만 고치면 종이 비율이 깨졌고,
   * padding 은 아예 커플링이 없는 고정값이었다. 307x404 는 편지지 이미지 4종의
   * 공통 원본 크기 — 아래 .letter-body 주석 참고.
   *
   * 280px 상한은 비율보다 우선한다(의도한 것). 아래 두 임계값은 전부 **폭 311px
   * 이상**, 즉 min() 의 100% 가지가 지지 않는 구간의 이야기다:
   *   - 뷰포트 높이 526.4px(= 280 * 404/307 / 0.7) 초과부터 min() 의 280px 가지가
   *     이겨서 비율이 깨지기 시작한다. 박스는 280 x 70vh.
   *   - 뷰포트 높이 571.4px(= 400 / 0.7) 이상에서 --letter-h 도 400px 로 고정돼
   *     박스가 280x400(0.700)이 된다.
   * 그 차이는 background-size:contain 이 위아래 레터박스로 흡수한다 —
   * 571.4px 이상에서 그림 280x368.47, 위아래 15.77px 씩. 526~571px 구간에서는
   * 0 에서 15.77px 로 서서히 커진다(예: 360x560 은 박스 280x392, 레터박스 11.77px).
   * 폭이 311px 미만이면 100% 가지가 이겨 박스가 더 좁아지고 레터박스는 더 커진다
   * (280x653 → 박스 252x400, 비율 0.630, 레터박스 34.19px). 이미지 비율 0.760 에서
   * 가장 많이 어긋나는 건 280x400 이 아니라 여기다.
   * 본문은 어느 구간에서도 종이 안에 들어오므로 무해하다 — "비율이 안 맞네" 하고
   * 280px 를 걷어내면 넓은 화면에서 편지지만 커진다.
   *
   * min() 의 100% 가지는 반대쪽 사고를 막는다. 이게 없으면 좁고 긴 화면에서
   * .letter-body 가 부모 .letter-container(width:90%)보다 넓어진다 — 갤럭시 폴드
   * 외부화면 280x653 에서 컨테이너 252px 대 본체 280px 로 28px.
   * 조건은 min(400px,70vh) * 307/404 > 0.9 * 뷰포트폭 이다. 폭 311px 미만이 필수고
   * (0.9 * 311.11 = 280), 높이 임계값은 폭을 따라 움직인다 — 폭 280px 이면 474px,
   * 폭 311px 이면 526px. "높이 571px 이상"이 아니다: 실측으로 300x560 은 10px,
   * 280x480 은 3.32px 넘친다.
   * 다만 280x653 에서 가로 스크롤바가 생기지는 않았다. .letter-overlay 가
   * justify-content:center 라 컨테이너가 좌우 14px 씩 여백을 두고 가운데 서고,
   * align-items:center 인 본체가 그 여백으로 대칭으로 삐져나가 정확히 [0,280] 에
   * 맞아떨어진다. 실제로 화면 밖으로 나가려면 뷰포트 폭이 280px 미만이어야 한다
   * (실측 250x653: 본체 left -15, 가로 스크롤바 발생). 그러니 280px 대에서 고친 건
   * "종이가 제 컨테이너보다 넓다"는 겉보기 문제고, 그 아래에서야 진짜 잘린다. */
  --letter-h: min(400px, 70vh);
  --letter-w: min(280px, calc(var(--letter-h) * 307 / 404), 100%);
}

.letter-body {
  /* 편지지 이미지 4종 모두 원본이 307x404 다. background-size:contain 은 비율을
   * 유지하므로, 박스 비율이 307/404 에서 벗어나면 그림이 박스 안에서 작아지고
   * 남는 쪽에 여백이 생긴다. --letter-h 가 70vh 로 줄어드는 짧은 화면에서
   * width 를 280px 로 고정해 두면 박스가 가로로 납작해져서, 그림은 높이에 맞춰
   * 줄어들고 좌우에 빈 띠가 남는다 — 그 위에 얹힌 본문은 종이 밖으로 삐져나온다.
   * 그래서 가로도 같은 비율로 따라 줄인다(307/404 = 0.7599). 계산식은 위
   * .letter-container 의 --letter-w / --letter-h 에 모아 뒀다. */
  /* var() 의 두 번째 인자(280px / 400px)는 장식이 아니다. .letter-body 가 어쩌다
   * .letter-container 밖으로 나가면 커스텀 프로퍼티가 안 내려와 선언 자체가 무효가
   * 되는데(invalid at computed-value time), 폴백이 없으면 width/height 는 auto,
   * padding 은 0 이 된다 → .content-wrapper 의 height:100% 가 auto 부모를 물어
   * 안쪽 스크롤이 죽는다. 폴백이 있으면 그 경우에도 예전 고정 크기로 떨어질 뿐이다. */
  width: var(--letter-w, 280px);
  /* --letter-h 가 400px 고정이 아닌 이유: 고정이면 세로가 짧은 화면(가로모드 폰)에서
   * 닫기 버튼을 밖으로 밀어낸다. content-wrapper 가 height:100% 로 안쪽 스크롤을
   * 잡고 있으므로 height 를 auto 로 바꾸지 말 것(100% 가 auto 로 풀려 스크롤이 죽는다). */
  height: var(--letter-h, 400px);
  /* padding 도 박스를 따라 줄어야 한다. 40px/30px 로 고정해 두면 박스가 207px 까지
   * 작아지는 화면(844x390)에서 좌우 패딩만 14.5% 를 먹어 본문 폭이 147px 로 눌린다.
   * 아래 식은 설계 기준폭 280px 에서의 40px/30px 을 비율(14.3% / 10.7%)로 환산한 것 —
   * 280px 일 때는 정확히 40px/30px 이라 넓은 화면에서는 이전과 동일하다.
   * ※ padding 을 % 로 쓰면 안 된다. % padding 은 자기 폭이 아니라 **부모(.letter-container,
   *   최대 320px)** 의 폭을 기준으로 잡히므로 --letter-w 와 따로 논다.
   *   --letter-w 안의 100% 는 예외다. 지우지 말 것 — width 의 % 와 padding 의 % 는
   *   둘 다 같은 containing block 의 인라인 크기로 풀리므로 min() 이 양쪽에서 같은
   *   값을 고른다. 실측 280x653: 폭 252px, padding 36/27px = 252*40/280, 252*30/280. */
  padding: calc(var(--letter-w, 280px) * 40 / 280) calc(var(--letter-w, 280px) * 30 / 280);
  background-size: contain;
  background-repeat: no-repeat;
  background-position: center;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  margin-bottom: 20px;
}

/* Backgrounds for different letter types
 *
 * ⚠️ 접두사는 .letter 가 아니라 .letter-container 다. 템플릿이 붙이는 클래스는
 * `letter-container` + letterType.toLowerCase() 딱 두 개라서, .letter.note 처럼 쓰면
 * "letter 와 note 를 **둘 다** 가진 요소"를 찾게 되고 그런 요소는 존재하지 않는다.
 * LETTER 타입만 우연히 살아 있었다 — .letter.letter 는 같은 클래스를 두 번 쓴 것이라
 * 실질적으로 .letter 한 개와 같고, 그게 letter-container 의 `letter` 와 맞아떨어졌다.
 * 그래서 note / polaroid / sheepskin 3종은 편지지 배경이 통째로 안 나왔다. */
.letter-container.letter .letter-body { background-image: url(../assets/images/common/letters/large/letter_large.png); }
.letter-container.note .letter-body { background-image: url(../assets/images/common/letters/large/note_large.png); }
.letter-container.polaroid .letter-body { background-image: url(../assets/images/common/letters/large/polaroid.png); }
.letter-container.sheepskin .letter-body { background-image: url(../assets/images/common/letters/large/sheepskin_large.png); }

.content-wrapper {
  width: 100%;
  height: 100%;
  overflow-y: auto;
  padding-right: 5px;
}

/* Custom Scrollbar */
.content-wrapper::-webkit-scrollbar { width: 4px; }
.content-wrapper::-webkit-scrollbar-thumb { background: #888; }

.letter-text {
  color: #333;
  font-size: 16px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-all;
}

/* 위 배경 규칙과 같은 이유로 접두사는 .letter-container 다. */
.letter-container.note .letter-text { line-height: 1.8; color: #2c3e50; }
.letter-container.sheepskin .letter-text { font-family: 'serif'; color: #4a3728; }

.letter-footer {
  width: 100%;
  display: flex;
  justify-content: center;
}

/* Animations */
@keyframes slideUp {
  from { transform: translateY(50px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

.letter-fade-enter-active, .letter-fade-leave-active {
  transition: opacity 0.3s;
}
.letter-fade-enter-from, .letter-fade-leave-to {
  opacity: 0;
}
</style>