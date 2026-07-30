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
   * 280px 상한은 비율보다 우선한다(의도한 것). 아래 두 임계값은 전부 **폭 2800/9px
   * (≈311.11) 이상**, 즉 min() 의 100% 가지가 지지 않는 구간의 이야기다(컨테이너가
   * 0.9 * 뷰포트폭 이라 폭 311px 에서는 아직 279.9px 고 100% 가지가 여전히 이긴다.
   * 소수점 아래를 반올림해 "311.12px 이상"으로 적으면 그 사이가 거짓이 된다):
   *   - 뷰포트 높이 161600/307px(= 280 * 404/307 / 0.7, ≈526.3844) 초과부터 280px 가지가
   *     이겨서 비율이 깨지기 시작한다. 박스는 280 x 70vh.
   *   - 뷰포트 높이 ≈571.43px(= 400 / 0.7) 이상에서 --letter-h 도 400px 로 고정돼
   *     박스가 280x400(0.700)이 된다.
   * 그 차이는 background-size:contain 이 위아래 레터박스로 흡수한다 —
   * ≈571.43px 이상에서 그림 280x368.47, 위아래 15.77px 씩. 526~571px 구간에서는
   * 0 에서 15.77px 로 서서히 커진다(예: 360x560 은 박스 280x392, 레터박스 11.77px).
   * 폭이 2800/9px 미만이라고 100% 가지가 이기는 건 아니다. 0.9 * 뷰포트폭 이 비율
   * 가지(min(400px,70vh) * 307/404)보다도 작아야, 즉 화면이 충분히 높기도 해야
   * 이긴다. 그때만 박스가 더 좁아지고 레터박스가 더 커진다(280x653 → 박스 252x400,
   * 비율 0.630, 레터박스 34.19px). 폭만 보고 판정하지 말 것 — 250x400 은 폭이
   * 250px 로 2800/9 보다 한참 아래인데도 비율 가지가 이겨 레터박스가 0 이다.
   * 높이를 653px 로 고정해 두고 폭만 좁히면 어긋남은 단조롭게 커진다 — 폭 280 은
   * 0.630, 250 은 0.5625, 240 은 0.540 으로, 위에 적은 박스 280x400(0.700)보다
   * 이미지 비율 307/404(≈0.7599)에서 더 멀다(이 문단에서 280x400 만 박스 크기고,
   * 나머지 세 개는 뷰포트다).
   *
   * ⚠️ 이게 해로운지는 폭만으로 판정할 수 없다. 2차원 조건이다.
   * 본문 padding-top(= 폭 * 40/280)이 레터박스보다 작아지는 순간 첫 줄이 그림 위로
   * 올라가는데, 정리하면 min(400px, 70vh) > 뷰포트폭 * 15489/10745 일 때만 해롭다
   *   (= 3442/2149 * 9/10 ≈ 1.4415. 아래 두 줄의 임계값은 전부 여기서 나온다.
   *    계수를 1.4415 로 반올림해 적지 말 것 — 참값보다 작아서 ">" 와 함께 쓰면
   *    "…일 때만"(필요조건)으로 읽을 때만 참이고, 충분조건이나 필요충분으로
   *    바꿔 읽는 순간 (1.4415, 15489/10745] 구간에서 거짓이 된다).
   *   - 높이 ≈571.43px 이상: --letter-h 가 400 으로 포화하므로
   *     폭 400 / (3442/2149 * 9/10) ≈ 277.487px 미만부터 (실측 250x653 은 19.81px,
   *     240x653 은 27.02px 위로 벗어난다. 280x653 은 1.81px 여유로 들어온다).
   *   - 그보다 낮으면 높이가 같이 줄어 레터박스도 줄어든다. 조건이 뷰포트높이 >
   *     ≈2.0593 * 뷰포트폭 로 바뀌어, 폭 250px 이면 높이 7744500/15043px(≈514.8242) 부터다
   *     (250x500 은 5.19px 여유로 무해, 250x560 은 15.81px 벗어남).
   *   - 그래서 폭이 좁아도 낮으면 멀쩡하다: 250x400 과 240x400 은 100% 가지가 아니라
   *     비율 가지가 이겨 박스가 212.77x280 이고, 비율이 정확히 307/404 라 레터박스가
   *     아예 0 이다. "폭 278px 미만이면 깨진다" 식의 1차원 규칙으로 적지 말 것.
   * "비율이 안 맞네" 하고 280px 상한을 걷어내면 넓은 화면에서 편지지만 커진다.
   * 아래 .letter-body 주석이 말하는 "본문이 종이 밖으로" 가 이 경우다.
   *
   * min() 의 100% 가지는 반대쪽 사고를 막는다. 이게 없으면 좁고 긴 화면에서
   * .letter-body 가 부모 .letter-container(width:90%)보다 넓어진다 — 갤럭시 폴드
   * 외부화면 280x653 에서 컨테이너 252px 대 본체 280px 로 28px.
   * ⚠️ 이 문단에서 "얼마나 넘쳤나" 를 나타내는 수치 — 28px, 아래 두 높이 임계값,
   * 10px, 3.33px, left -15 — 는 100% 가지를 걷어낸 복제 하네스에서 잰 값이다. 지금
   * 코드 그대로 브라우저에서 재면 본체가 컨테이너에 붙어 있어 전부 0 으로 나온다 —
   * "실측" 을 문자 그대로 읽고 주석이 틀렸다고 결론 내지 말 것. 나머지(컨테이너
   * 252 / 279.9 / 288px, 좌우 여백 14px 등)는 지금 코드에서 그대로 재현된다.
   * 조건은 min(280px, min(400px,70vh) * 307/404) > 0.9 * 뷰포트폭 이다.
   * 바깥 280px 상한을 빼먹고 적지 말 것 — 그러면 폭 2800/9 ~ 307000/909px
   * (≈311.11~337.73) 구간을 넘친다고 잘못 읽는다. 경계를 311~338 로 반올림해 적으면
   * 폭 311px 이 잘못 빠진다 — 거기선 컨테이너가 279.9px 라 실제로 0.1px 넘치므로
   * 오독이 아니라 사실이다(320x653 은 본체 280px 대 컨테이너 288px 라 안 넘친다).
   * 그래서 폭 2800/9px 미만이 필수고, 높이 임계값은 폭을 따라 움직인다 — 폭 280px 이면
   * 145440/307px(≈473.7459), 폭 311px 이면 1130796/2149px(≈526.1964) 부터 넘치기
   * 시작한다("473.75 부터" 로 반올림해 적으면 그 사이 구간이 거짓이 된다).
   * "높이 571px 이상"이 아니다: 300x560 은 10px, 280x480 은 3.33px 넘친다.
   * 다만 280x653 에서 가로 스크롤바가 생기지는 않았다. .letter-overlay 가
   * justify-content:center 라 컨테이너가 좌우 14px 씩 여백을 두고 가운데 서고,
   * 컨테이너 쪽 align-items:center 가 넘치는 본체를 그 여백으로 대칭으로 흘려보내
   * 정확히 [0,280] 에 맞아떨어진다(본체 자신은 align-items:flex-start 다).
   * 실제로 화면 밖으로 나가려면 뷰포트 폭이 280px 미만이어야 한다
   * (실측 250x653: 본체 left -15). 그때 생기는 스크롤바는 position:fixed 인
   * .letter-overlay 쪽이지 문서 전체가 아니다. 그러니 280px 대에서 고친 건
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
   *   --letter-w 안의 100% 는 예외다. 지우지 말 것 — 가로 쓰기 모드에서는 width 의 %
   *   와 padding 의 % 가 같은 값(컨테이닝 블록의 폭 = 인라인 크기)으로 풀려서 min() 이
   *   양쪽에서 같은 가지를 고른다. 엄밀히는 width% 는 물리적 폭, padding% 는 **컨테이닝
   *   블록(= .letter-container)** 의 인라인 크기 기준이라 갈라질 수 있는데, 그러려면
   *   .letter-container 쪽을 세로 쓰기로 돌려야 한다 — .letter-body 자신만 돌려서는
   *   컨테이닝 블록이 그대로라 아무것도 안 갈라진다. 이 리포엔 writing-mode 선언이 없다.
   *   실측 280x653: 폭 252px, padding 36/27px = 252*40/280, 252*30/280. */
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