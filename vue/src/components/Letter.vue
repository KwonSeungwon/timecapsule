<template>
  <transition name="letter-fade">
    <div class="letter-overlay">
      <div class="letter-container" :class="letterType?.toLowerCase()">
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
}

.letter-body {
  width: 280px;
  /* 400px 고정이면 세로가 짧은 화면(가로모드 폰)에서 닫기 버튼을 밖으로 밀어낸다.
   * content-wrapper 가 height:100% 로 안쪽 스크롤을 잡고 있으므로 height 는
   * auto 로 바꾸지 말 것(auto 로 두면 100% 가 auto 로 풀려 안쪽 스크롤이 죽는다). */
  height: min(400px, 70vh);
  padding: 40px 30px;
  background-size: contain;
  background-repeat: no-repeat;
  background-position: center;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  margin-bottom: 20px;
}

/* Backgrounds for different letter types */
.letter.letter .letter-body { background-image: url(../assets/images/common/letters/large/letter_large.png); }
.letter.note .letter-body { background-image: url(../assets/images/common/letters/large/note_large.png); }
.letter.polaroid .letter-body { background-image: url(../assets/images/common/letters/large/polaroid.png); }
.letter.sheepskin .letter-body { background-image: url(../assets/images/common/letters/large/sheepskin_large.png); }

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

.letter.note .letter-text { line-height: 1.8; color: #2c3e50; }
.letter.sheepskin .letter-text { font-family: 'serif'; color: #4a3728; }

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