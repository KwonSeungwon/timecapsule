<template>
  <div class="game-field wizard-container">
    <div class="field-title">
      <h2 class="pixel-text">{{ stepTitle }}<span v-if="currentStep < 6"> ({{ currentStep }}/5)</span></h2>
    </div>

    <!-- 단계별 전환 애니메이션 -->
    <transition name="step-fade" mode="out-in">
      <div :key="currentStep" class="step-content">
        
        <!-- Step 1: 기본 정보 입력 -->
        <div v-if="currentStep === 1" class="form-container">
          <div class="input-group">
            <label>📌 기억의 제목</label>
            <input type="text" v-model="form.title" class="pixel-input" placeholder="캡슐의 이름을 지어주세요">
          </div>
          <div class="input-group">
            <label>👤 보낸이</label>
            <input type="text" v-model="form.senderName" class="pixel-input" placeholder="당신의 이름">
          </div>
          <div class="input-group">
            <label>📧 수신 이메일</label>
            <input type="email" v-model="form.receiverEmail" class="pixel-input" placeholder="알림을 받을 메일 주소">
          </div>
          <div class="input-group">
            <label>🔑 개봉 암호</label>
            <input type="password" v-model="form.password" class="pixel-input" placeholder="나중에 열 때 필요해요">
          </div>
        </div>

        <!-- Step 2: 날짜 선택 (NEW) -->
        <div v-else-if="currentStep === 2" class="date-selection-container">
          <div class="calendar-icon">📅</div>
          <h3 class="step-subtitle">언제 이 기억을 배달할까요?</h3>
          
          <input type="date" v-model="openDate" class="pixel-input date-input" :min="minDate">
          
          <div class="quick-date-buttons">
            <button class="pixel-btn small" @click="setQuickDate(30)">1달 후</button>
            <button class="pixel-btn small" @click="setQuickDate(180)">6달 후</button>
            <button class="pixel-btn small" @click="setQuickDate(365)">1년 후</button>
          </div>
          
          <div class="d-day-display" v-if="openDate">
            <span class="d-day-text">✨ {{ calculatedDDay }}일 후에 도착합니다</span>
          </div>
        </div>

        <!-- Step 3: 캡슐 외형 선택 -->
        <div v-else-if="currentStep === 3" class="selection-grid">
          <div v-for="c in capsules" :key="c.id" 
               class="select-item" :class="{ active: form.capsuleType === c.id }"
               @click="form.capsuleType = c.id">
            <div class="icon-box">
              <img :src="require(`@/assets/images/common/capsule/${c.img}`)" class="pixelated">
            </div>
            <p>{{ c.name }}</p>
          </div>
        </div>

        <!-- Step 4: 편지지 디자인 선택 -->
        <div v-else-if="currentStep === 4" class="selection-grid">
          <div v-for="p in papers" :key="p.id" 
               class="select-item" :class="{ active: form.letterPaperType === p.id }"
               @click="form.letterPaperType = p.id">
            <div class="icon-box paper">
              <img :src="require(`@/assets/images/common/letters/small/${p.img}`)" class="pixelated">
            </div>
            <p>{{ p.name }}</p>
          </div>
        </div>

        <!-- Step 5: 내용 작성 -->
        <div v-else-if="currentStep === 5" class="form-container">
          <div class="input-group">
            <label>📝 소중한 마음을 담아보세요</label>
            <textarea v-model="form.content" class="pixel-input content-area" 
                      placeholder="미래의 나 혹은 소중한 사람에게 전할 말을 10자 이상 적어주세요."></textarea>
          </div>
          <div class="paper-preview" :class="form.letterPaperType.toLowerCase()">
            <span>💌 {{ paperName }} 편지지가 선택되었습니다.</span>
          </div>
          <!-- 공개는 반드시 사용자가 켜야 한다. 켜면 보낸이 이름과 제목이 탐색 지도에 뜬다. -->
          <label class="public-toggle">
            <input type="checkbox" v-model="form.isPublic">
            <span>
              🗺️ 공개 캡슐 탐색 지도에 올리기
              <em>보낸이 이름 · 제목 · 매립 장소가 공개됩니다. 편지 내용과 이메일은 공개되지 않아요.</em>
            </span>
          </label>
        </div>

        <!-- 저장 성공 결과 -->
        <div v-else-if="currentStep === 6" class="success-container">
          <div class="result-card">
            <div class="biome-icon">{{ biomeEmoji }}</div>
            <h3 class="pixel-text">캡슐 매립 완료!</h3>
            <div class="location-details">
              <p class="biome-name">{{ result.locationName }}</p>
              <p class="coords">📍 위도 {{ result.xCoord }} / 경도 {{ result.yCoord }}</p>
              <p class="location-desc">"{{ result.title }}"</p>
            </div>
            <p class="notify-note">
              📮 개봉일이 되면 <strong>{{ form.receiverEmail }}</strong> 로 알림 메일이 도착합니다.
            </p>
          </div>
          <button class="pixel-btn primary full-width" @click="goHome">🏠 홈으로 돌아가기</button>
        </div>

      </div>
    </transition>

    <!-- 하단 액션 버튼 -->
    <div class="footer-actions" v-if="currentStep < 6">
      <button class="pixel-btn" @click="prevStep">{{ currentStep === 1 ? '취소' : '이전' }}</button>
      <button class="pixel-btn primary" :disabled="!isCurrentStepValid || isLoading" @click="nextStep">
        <template v-if="isLoading">⏳ 처리 중...</template>
        <template v-else>{{ currentStep === 5 ? '✨ 캡슐 묻기' : '다음' }}</template>
      </button>
    </div>

  </div>

  <Popup v-if="popup.open"
         :one-button="true"
         confirm="확인"
         :contents="popup.content"
         v-on:popup_res="popup.open = false"></Popup>
</template>

<script>
import Popup from "@/components/Popup";
import axios from 'axios';

// 이 서비스의 "하루" 경계는 KST 다 (백엔드 LetterPaper.SERVICE_ZONE).
// 서버는 개봉 가능 여부를 openAt 인스턴트로, D-Day 를 KST 달력 날짜로 계산한다.
// 그래서 날짜 계산을 브라우저 로컬 시간대로 하면 KST 밖 사용자에게 둘이 어긋난다 —
// 화면엔 D-0 이라고 떠 있는데 열어보면 403 "아직 개봉할 수 없습니다" 가 난다.
// 날짜는 전부 'YYYY-MM-DD' 문자열로, 서버로 보낼 때만 KST 자정 인스턴트로 바꾼다.
// KST 는 서머타임이 없어 고정 오프셋으로 충분하다.
const SERVICE_UTC_OFFSET = '+09:00';
const MS_PER_DAY = 24 * 60 * 60 * 1000;

// 'YYYY-MM-DD' → 그 날 KST 자정의 ISO 인스턴트.
function toServiceZoneInstant(dateString) {
  return new Date(`${dateString}T00:00:00${SERVICE_UTC_OFFSET}`).toISOString();
}

// 오늘 날짜를 KST 달력 기준 'YYYY-MM-DD' 로. en-CA 로케일이 YYYY-MM-DD 를 준다.
function serviceToday() {
  return new Intl.DateTimeFormat('en-CA', { timeZone: 'Asia/Seoul' }).format(new Date());
}

// 'YYYY-MM-DD' 에서 n 일 뒤. 두 값 모두 UTC 자정으로 파싱해 빼므로
// 로컬 시간대나 서머타임의 영향을 받지 않는다.
function addDays(dateString, days) {
  return new Date(Date.parse(`${dateString}T00:00:00Z`) + days * MS_PER_DAY)
      .toISOString()
      .slice(0, 10);
}

// 달력 날짜 사이의 일수. 서버의 D-Day(ChronoUnit.DAYS.between) 와 같은 정의다.
function daysBetween(fromDateString, toDateString) {
  return Math.round(
      (Date.parse(`${toDateString}T00:00:00Z`) - Date.parse(`${fromDateString}T00:00:00Z`)) / MS_PER_DAY);
}

export default {
  name: "WriteLetter",
  components : {Popup},
  data () {
    return {
      currentStep: 1,
      openDate: addDays(serviceToday(), 7),
      form: {
        title: '',
        senderName: '',
        receiverEmail: '',
        password: '',
        content: '',
        openAt: '',
        // 기본은 비공개. 예전엔 true 로 박혀 있고 UI 도 없어서, 사용자가 동의한 적 없는
        // 보낸이 이름과 제목이 전부 /explore 지도에 올라갔다.
        isPublic: false,
        letterPaperType: 'LETTER',
        capsuleType: 'BAMBOO'
      },
      capsules: [
        { id: 'BAMBOO', name: '죽통', img: 'bamboo.png' },
        { id: 'BOTTLE', name: '유리병', img: 'bottle.png' },
        { id: 'EGG', name: '달걀', img: 'egg.png' },
        { id: 'CANDYBOX', name: '캔디통', img: 'candybox.png' }
      ],
      papers: [
        { id: 'LETTER', name: '평범한 편지지', img: 'letter_small.png' },
        { id: 'NOTE', name: '연습장', img: 'note_small.png' },
        { id: 'SHEEPSKIN', name: '양피지', img: 'sheepskin_small.png' },
        { id: 'POLAROID', name: '폴라로이드', img: 'polaroid_small.png' }
      ],
      isLoading: false,
      result: {
        title: '',
        locationName: '',
        xCoord: 0,
        yCoord: 0,
        biome: ''
      },
      popup : {
        open : false,
        content : ''
      }
    }
  },
  computed: {
    stepTitle() {
      const titles = ['기본 정보', '배달 날짜', '캡슐 고르기', '편지지 고르기', '내용 적기', '매립 완료!'];
      return titles[this.currentStep - 1];
    },
    paperName() {
      const p = this.papers.find(p => p.id === this.form.letterPaperType);
      return p ? p.name : '';
    },
    isCurrentStepValid() {
      if (this.currentStep === 1) {
        return this.form.title && this.form.senderName && this.form.receiverEmail && this.form.password;
      }
      if (this.currentStep === 2) {
        return this.openDate && this.calculatedDDay > 0;
      }
      if (this.currentStep === 5) {
        return this.form.content.length >= 10;
      }
      return true;
    },
    calculatedDDay() {
      if (!this.openDate) return 0;
      return daysBetween(serviceToday(), this.openDate);
    },
    minDate() {
      return addDays(serviceToday(), 1);
    },
    biomeEmoji() {
      const biomeMap = { 'BEACH': '🌊', 'MOUNTAIN': '⛰️', 'DESERT': '🏜️', 'FOREST': '🌲' };
      return biomeMap[this.result.biome] || '📍';
    }
  },
  methods : {
    setQuickDate(days) {
      this.openDate = addDays(serviceToday(), days);
    },
    nextStep() {
      if (this.currentStep < 5) {
        this.currentStep++;
      } else if (this.currentStep === 5) {
        this.submitLetter();
      }
    },
    prevStep() {
      if (this.currentStep > 1) {
        this.currentStep--;
      } else {
        this.$router.replace('/');
      }
    },
    // ... submitLetter, goHome ...
    submitLetter() {
      if (this.isLoading) return;
      this.isLoading = true;
      
      this.form.openAt = toServiceZoneInstant(this.openDate);

      const self = this;
      axios.post('/api/v1/capsule', this.form)
      .then(function (response){
        const data = response.data;
        self.result = {
          title: data.title,
          locationName: data.locationName,
          xCoord: data.xCoord,
          yCoord: data.yCoord,
          biome: data.biome
        };
        // 6단계 = 좌표·바이옴이 찍힌 매립 결과 카드. 이 서비스의 핵심 연출인데
        // 예전엔 여기서 홈으로 replace 해버려서 한 번도 보인 적이 없었다.
        self.currentStep = 6;
      }).catch(function (error){
        self.popup.content = '전송에 실패했습니다: ' + (error.response?.data?.message || '알 수 없는 오류');
        self.popup.open = true;
      }).finally(() => {
        self.isLoading = false;
      });
    },
    goHome() {
      this.$router.replace('/');
    }
  }
}
</script>

<style scoped>
.wizard-container {
  min-height: 80vh;
  display: flex;
  flex-direction: column;
}

.step-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  margin: 20px 0;
}

.form-container {
  width: 100%;
}

.input-group {
  margin-bottom: 12px;
}

.input-group label {
  display: block;
  margin-bottom: 4px;
  font-size: 13px;
  font-weight: bold;
  color: #333;
}

.selection-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.select-item {
  border: 4px solid #000;
  background: #fff;
  padding: 15px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
}

.select-item:hover {
  background: #f0f0f0;
  transform: translateY(-2px);
}

.select-item.active {
  background: #ffe24a;
  border-color: #5d7a54;
  box-shadow: 4px 4px 0px #5d7a54;
}

.icon-box {
  width: 60px;
  height: 60px;
  margin: 0 auto 10px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.icon-box img {
  max-width: 100%;
  max-height: 100%;
}

.icon-box.paper {
  width: 80px;
  height: 80px;
}

.content-area {
  height: 150px;
  resize: none;
}

/* ... existing styles ... */
.date-selection-container {
  text-align: center;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.calendar-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.step-subtitle {
  font-size: 18px;
  color: #000;
  margin-bottom: 30px;
}

.date-input {
  max-width: 250px;
  margin: 0 auto 20px;
  text-align: center;
  font-size: 20px;
}

.quick-date-buttons {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-bottom: 30px;
}

.pixel-btn.small {
  padding: 5px 12px;
  font-size: 13px;
  box-shadow: 2px 2px 0px #000;
}

.d-day-display {
  margin-top: 10px;
}

.d-day-text {
  font-size: 16px;
  color: #5d7a54;
  font-weight: bold;
  background: rgba(93, 122, 84, 0.1);
  padding: 8px 15px;
  border-radius: 4px;
}

.paper-preview {
  margin-top: 10px;
  padding: 10px;
  text-align: center;
  font-size: 11px;
  background: rgba(0,0,0,0.05);
}

.public-toggle {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-top: 12px;
  padding: 10px;
  border: 2px solid #333;
  background: #fff;
  font-size: 11px;
  line-height: 1.5;
  cursor: pointer;
}

.public-toggle input {
  margin-top: 2px;
  width: 16px;
  height: 16px;
  flex-shrink: 0;
  cursor: pointer;
}

.public-toggle em {
  display: block;
  margin-top: 4px;
  font-style: normal;
  font-size: 10px;
  color: #666;
}

.footer-actions {
  display: flex;
  gap: 12px;
  margin-top: auto;
}

.footer-actions .pixel-btn {
  flex: 1;
}

.pixelated {
  image-rendering: pixelated;
}

/* Animations */
.step-fade-enter-active, .step-fade-leave-active {
  transition: all 0.3s ease;
}
.step-fade-enter-from { opacity: 0; transform: translateX(20px); }
.step-fade-leave-to { opacity: 0; transform: translateX(-20px); }

.success-container {
  text-align: center;
}

.result-card {
  background: #fff;
  border: 4px solid #000;
  padding: 20px;
  margin-bottom: 20px;
  animation: bounceIn 0.6s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

.biome-icon { font-size: 48px; }
.biome-name { font-size: 20px; font-weight: bold; color: #5d7a54; }
.location-desc { font-style: italic; font-size: 16px; margin-top: 10px; border-top: 1px dashed #ccc; padding-top: 10px;}
.notify-note { font-size: 13px; color: #666; margin-top: 16px; line-height: 1.6; word-break: break-all; }

.full-width { width: 100%; }

@keyframes bounceIn {
  0% { transform: scale(0.3); opacity: 0; }
  100% { transform: scale(1); opacity: 1; }
}
</style>
