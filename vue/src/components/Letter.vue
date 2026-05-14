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
  align-items: center;
  backdrop-filter: blur(4px);
}

.letter-container {
  width: 90%;
  max-width: 320px;
  display: flex;
  flex-direction: column;
  align-items: center;
  animation: slideUp 0.4s ease-out;
}

.letter-body {
  width: 280px;
  height: 400px;
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