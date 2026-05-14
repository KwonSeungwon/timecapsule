<template>
  <div class="game-field">
    <div class="field-title">
      <h2 class="pixel-text">📖 사용 가이드 ({{ currentStep + 1 }}/3)</h2>
    </div>

    <div class="tutorial-content">
      <transition name="fade" mode="out-in">
        <div :key="currentStep" class="step-container">
          <div class="step-icon">{{ steps[currentStep].icon }}</div>
          <h3 class="step-title">{{ steps[currentStep].title }}</h3>
          <ul class="step-list">
            <li v-for="(desc, i) in steps[currentStep].descriptions" :key="i">
              {{ desc }}
            </li>
          </ul>
        </div>
      </transition>
    </div>

    <div class="pagination-dots">
      <span v-for="(s, i) in steps" :key="i" class="dot" :class="{ active: i === currentStep }"></span>
    </div>

    <div class="footer-actions">
      <button class="pixel-btn" @click="prevStep" :disabled="currentStep === 0">이전</button>
      <button v-if="currentStep < steps.length - 1" class="pixel-btn primary" @click="nextStep">다음</button>
      <button v-else class="pixel-btn primary" @click="close">시작하기</button>
    </div>
  </div>
</template>

<script>
export default {
  name: "TutorialView",
  data() {
    return {
      currentStep: 0,
      steps: [
        {
          icon: '📫',
          title: '캡슐 묻기',
          descriptions: [
            '보내는 이와 수신 이메일을 입력하세요.',
            '비밀번호를 설정하고 내용을 작성합니다.',
            '캡슐은 한국의 랜덤 좌표에 묻힙니다.'
          ]
        },
        {
          icon: '📧',
          title: '알림 및 배달',
          descriptions: [
            '설정한 날짜가 되면 수신자에게 메일이 발송됩니다.',
            '메일에는 캡슐을 열 수 있는 링크가 포함됩니다.',
            '개봉일 전까지는 절대 내용을 볼 수 없어요.'
          ]
        },
        {
          icon: '🎒',
          title: '캡슐 찾기',
          descriptions: [
            '수신자 이메일로 묻힌 캡슐을 조회할 수 있어요.',
            '비밀번호를 입력하여 캡슐을 열어보세요.',
            '과거의 소중한 기억을 다시 만날 수 있습니다.'
          ]
        }
      ]
    }
  },
  methods: {
    nextStep() {
      if (this.currentStep < this.steps.length - 1) this.currentStep++;
    },
    prevStep() {
      if (this.currentStep > 0) this.currentStep--;
    },
    close() {
      this.$router.push('/');
    }
  }
}
</script>

<style scoped>
.tutorial-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  margin: 20px 0;
}

.step-container {
  text-align: center;
}

.step-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.step-title {
  font-size: 22px;
  color: #000;
  margin-bottom: 20px;
}

.step-list {
  text-align: left;
  list-style: none;
  padding: 0;
  margin: 0;
}

.step-list li {
  font-size: 15px;
  color: #333;
  margin-bottom: 12px;
  padding-left: 24px;
  position: relative;
  line-height: 1.4;
}

.step-list li::before {
  content: '▶';
  position: absolute;
  left: 0;
  font-size: 12px;
  top: 2px;
}

.pagination-dots {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-bottom: 20px;
}

.dot {
  width: 10px;
  height: 10px;
  background: #ccc;
  border: 2px solid #000;
}

.dot.active {
  background: #000;
}

.footer-actions {
  display: flex;
  gap: 12px;
}

.footer-actions .pixel-btn {
  flex: 1;
}

/* Animations */
.fade-enter-active, .fade-leave-active {
  transition: all 0.3s ease;
}
.fade-enter-from { opacity: 0; transform: translateX(20px); }
.fade-leave-to { opacity: 0; transform: translateX(-20px); }
</style>
