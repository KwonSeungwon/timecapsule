<template>
  <div class="game-field">
    <div class="field-title">
      <h2 class="pixel-text">🎒 내 보관함</h2>
    </div>

    <div class="total-info">
      <p class="count-badge">전체 {{totalCount}}개</p>
      <p class="email-text">👤 {{ email }}</p>
    </div>

    <div class="capsule-grid-container">
      <transition :name="transitionName" mode="out-in">
        <div class="capsule-grid" :key="selectIdx">
          <template v-if="letters.length">
            <div v-for="(letter, i) in currentPageLetters" :key="letter.id" class="capsule-item">
              <div class="capsule-wrapper" :class="{'is-opened': letter.isOpened}" @click="promptPassword(letter)">
                <div class="capsule-icon" :class="letter.letterPaperType.toLowerCase()"></div>
                <span class="sender-name">{{letter.senderName}}</span>
                <div class="d-day" v-if="!isPast(letter.openAt)">D-{{ getDDay(letter.openAt) }}</div>
              </div>
            </div>
          </template>
          <template v-else>
            <div class="no-data">
              <div class="empty-icon">🌬️</div>
              <p>아직 묻힌 캡슐이 없어요.</p>
            </div>
          </template>
        </div>
      </transition>
    </div>

    <div class="pagination" v-if="pages > 1">
      <button class="pixel-btn arrow-btn" :disabled="selectIdx === 1" @click="movePage('prev')">◀</button>
      <span class="page-indicator">{{ selectIdx }} / {{ pages }}</span>
      <button class="pixel-btn arrow-btn" :disabled="selectIdx === pages" @click="movePage('next')">▶</button>
    </div>

    <div class="footer-actions">
      <button class="pixel-btn primary full-width" @click="goHome">🏠 홈으로</button>
    </div>

    <!-- 비밀번호 입력 팝업 -->
    <Popup v-if="passwordPopup.open"
           confirm="열기"
           cancel="취소"
           v-on:popup_res="handlePasswordResponse">
      <template #contents>
        <div class="password-prompt">
          <p class="prompt-title">"{{ selectLetter.senderName }}"님의 캡슐</p>
          <div class="input-with-icon">
            <input type="password" v-model="passwordPopup.input" class="pixel-input" placeholder="비밀번호 입력" @keyup.enter="handlePasswordResponse(true)">
          </div>
          <p class="unlock-date" v-if="!isPast(selectLetter.openAt)">
            📅 개봉일: {{ formatDate(selectLetter.openAt) }}
          </p>
        </div>
      </template>
    </Popup>

    <!-- 편지 상세 내용 -->
    <Letter v-if="openLetter"
            :letter-type="selectLetter.letterPaperType"
            :contents="selectLetter.content"
            v-on:popup_res="closeLetter">
    </Letter>
  </div>
</template>

<script>
import Letter from "@/components/Letter";
import Popup from "@/components/Popup";
import axios from 'axios';

export default {
  name: "MyCapsule",
  components : {Letter, Popup},
  data () {
    return {
      email: this.$route.query.email || '',
      totalCount : 0,
      transitionName : 'slide-fade',
      pages : 1,
      letters : [],
      selectIdx : 1,
      openLetter : false,
      selectLetter : {},
      passwordPopup: {
        open: false,
        input: '',
        selectedLetter: null
      }
    }
  },
  computed: {
    currentPageLetters() {
      const start = (this.selectIdx - 1) * 6;
      return this.letters.slice(start, start + 6);
    }
  },
  methods : {
    fetchLetters() {
      if (!this.email) return;
      const self = this;
      axios.get('/api/v1/capsule/list', { params: { email: this.email } })
      .then(response => {
        self.letters = response.data;
        self.totalCount = self.letters.length;
        self.pages = Math.ceil(self.totalCount / 6) || 1;
      })
      .catch(error => {
        console.error('Failed to fetch letters:', error);
      });
    },
    promptPassword(letter) {
      this.selectLetter = letter;
      this.passwordPopup.input = '';
      this.passwordPopup.open = true;
    },
    handlePasswordResponse(isConfirm) {
      if (isConfirm) {
        this.readLetter();
      } else {
        this.passwordPopup.open = false;
      }
    },
    readLetter() {
      const self = this;
      const id = this.selectLetter.id;
      axios.post(`/api/v1/capsule/${id}/open`, { password: this.passwordPopup.input })
      .then(response => {
        self.selectLetter = response.data;
        if (!self.selectLetter.content) {
            alert('아직 개봉할 수 없는 날짜입니다.');
            return;
        }
        self.openLetter = true;
        self.passwordPopup.open = false;
      })
      .catch(error => {
        alert('비밀번호가 틀렸거나 오류가 발생했습니다.');
        console.error(error);
      });
    },
    closeLetter() {
      this.openLetter = false;
      this.fetchLetters();
    },
    goHome() {
      this.$router.replace('/');
    },
    movePage(direction) {
      if (direction === 'prev' && this.selectIdx > 1) {
        this.transitionName = 'slide-prev';
        this.selectIdx--;
      } else if (direction === 'next' && this.selectIdx < this.pages) {
        this.transitionName = 'slide-next';
        this.selectIdx++;
      }
    },
    formatDate(dateStr) {
      return new Date(dateStr).toLocaleDateString();
    },
    isPast(dateStr) {
      return new Date(dateStr) <= new Date();
    },
    getDDay(dateStr) {
      const diff = new Date(dateStr) - new Date();
      return Math.ceil(diff / (1000 * 60 * 60 * 24));
    }
  },
  created() {
    this.fetchLetters();
  }
}
</script>

<style scoped>
.field-title {
  text-align: center;
  margin-bottom: 16px;
}

.total-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 24px;
  gap: 8px;
}

.count-badge {
  background: #000;
  color: #fff;
  padding: 4px 12px;
  font-size: 12px;
  border-radius: 0;
}

.email-text {
  font-size: 14px;
  color: #444;
}

.capsule-grid-container {
  flex: 1;
  min-height: 320px;
  display: flex;
  flex-direction: column;
}

.capsule-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  grid-template-rows: repeat(3, 1fr);
  gap: 20px;
  flex: 1;
}

.capsule-item {
  display: flex;
  justify-content: center;
  align-items: center;
}

.capsule-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: transform 0.2s;
  padding: 10px;
  border: 2px solid transparent;
}

.capsule-wrapper:hover {
  transform: scale(1.1);
  background: rgba(255,255,255,0.5);
  border-color: #000;
}

.capsule-icon {
  width: 64px;
  height: 64px;
  background-size: contain;
  background-repeat: no-repeat;
  background-position: center;
  margin-bottom: 8px;
}

.capsule-icon.letter { background-image: url(../assets/images/letter_a.png); }
.capsule-icon.note { background-image: url(../assets/images/letter_b.png); }
.capsule-icon.polaroid { background-image: url(../assets/images/letter_c.png); }

.is-opened .capsule-icon {
  opacity: 0.6;
  filter: grayscale(1);
}

.sender-name {
  font-size: 13px;
  color: #000;
  font-weight: bold;
  text-align: center;
}

.d-day {
  font-size: 10px;
  background: #e74c3c;
  color: #fff;
  padding: 1px 4px;
  margin-top: 4px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin: 20px 0;
}

.arrow-btn {
  padding: 5px 15px;
  box-shadow: 2px 2px 0px #000;
}

.no-data {
  grid-column: span 2;
  grid-row: span 3;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #888;
}

.empty-icon { font-size: 48px; margin-bottom: 10px; }

.footer-actions {
  margin-top: auto;
}

.full-width { width: 100%; }

/* Transitions */
.slide-next-enter-active, .slide-next-leave-active,
.slide-prev-enter-active, .slide-prev-leave-active {
  transition: all 0.3s ease;
}

.slide-next-enter-from { transform: translateX(30px); opacity: 0; }
.slide-next-leave-to { transform: translateX(-30px); opacity: 0; }

.slide-prev-enter-from { transform: translateX(-30px); opacity: 0; }
.slide-prev-leave-to { transform: translateX(30px); opacity: 0; }

/* Popup Styles */
.password-prompt {
  padding: 10px;
  text-align: center;
}

.prompt-title {
  font-size: 16px;
  margin-bottom: 15px;
  font-weight: bold;
}

.unlock-date {
  font-size: 12px;
  color: #e74c3c;
  margin-top: 10px;
}
</style>

<style scoped>
.field-holder {
  position: relative;
  /*touch-action: pan-y;*/
}
.field {
  position: absolute;
  left: 17px;
  margin: 20% auto;
  width: 341px;
  height: 652px;
  transition: all .5s;
}
.field.next {
  left: 365px;
}

.field.next.none {
  left : 500px;
}

.field.prev {
  left: -330px;
}

.field.prev.none {
  left: -500px;
}

/*.beach .field {*/
/*  background: url(../assets/images/field_bc.png) no-repeat center transparent;*/
/*  background-size: cover;*/
/*}*/

/*.mountain .field {*/
/*  background: url(../assets/images/field_mt.png) no-repeat center transparent;*/
/*  background-size: cover;*/
/*}*/

/*.desert .field {*/
/*  background: url(../assets/images/field_dt.png) no-repeat center transparent;*/
/*  background-size: cover;*/
/*}*/

/*.space .field {*/
/*  background: url(../assets/images/field_bc.png) no-repeat center transparent;*/
/*  background-size: cover;*/
/*}*/

.total-info {
  padding: 0 20px;
}

.total-info p {
  margin: 0 0 10px;
}


.letter-holder {
  position: relative;
  height: 400px;
  margin-bottom: 50px;
}

.letter-holder .capsule {
  position: absolute;
  top : calc(50% - 43px);
  left: calc(50% - 43px);
  width: 86px;
  height: 86px;
  border: 0;
}

.capsule.bamboo {
  background: url(../assets/images/common/capsule/bamboo.png) no-repeat center transparent;
  background-size: cover;
}
.capsule.bottle {
  background: url(../assets/images/common/capsule/bottle.png) no-repeat center transparent;
  background-size: cover;
}
.capsule.candybox {
  background: url(../assets/images/common/capsule/candybox.png) no-repeat center transparent;
  background-size: cover;
}
.capsule.egg {
  background: url(../assets/images/common/capsule/egg.png) no-repeat center transparent;
  background-size: cover;
}


.select-letter {
  position: absolute;
}

.select-letter.position-1 {
  top: 10px;
  left : calc(50% - 43px);
}

.select-letter.position-2 {
  top: 80px;
  right : calc(16% - 43px);
}

.select-letter.position-3 {
  bottom: 80px;
  right : calc(16% - 43px);
}

.select-letter.position-4 {
  bottom: 10px;
  left : calc(50% - 43px);
}

.select-letter.position-5 {
  bottom: 80px;
  left : calc(16% - 43px);
}

.select-letter.position-6 {
  top: 80px;
  left : calc(16% - 43px);
}

.select-letter span {
  display: inherit;
  text-align: center;
  margin-top: -4px;
}

.select-letter button.letter {
  width: 86px;
  height: 86px;
  border: 0;
}

.select-letter button.letter:active,
.letter-holder .capsule:active {
  margin-top: 1px;
  margin-left: 1px;
  opacity: 0.9;
}




</style>