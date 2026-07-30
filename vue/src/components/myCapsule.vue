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
            <div v-for="letter in currentPageLetters" :key="letter.id" class="capsule-item">
              <div class="capsule-wrapper" :class="{'is-opened': isOpened(letter)}" @click="promptPassword(letter)">
                <div class="capsule-icon" :class="capsuleClass(letter)"></div>
                <span class="sender-name">{{letter.senderName}}</span>
                <div class="d-day" v-if="!letter.openable">D-{{ letter.dday }}</div>
              </div>
            </div>
          </template>
          <template v-else>
            <div class="no-data">
              <div class="empty-icon">🌬️</div>
              <!-- 이메일 없이 이 화면에 오면(직접 URL / 오래된 북마크) 조회할 방법이 없다.
                   빈 목록만 띄우고 끝내지 말고 찾기 화면으로 돌려보낸다. -->
              <p>{{ email ? '아직 묻힌 캡슐이 없어요.' : '조회할 이메일이 없어요.' }}</p>
              <button class="pixel-btn" @click="$router.push('/find')">🔍 이메일로 찾기</button>
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
          <p class="unlock-date" v-if="!selectLetter.openable">
            📅 개봉일: {{ formatDate(selectLetter.openAt) }} (D-{{ selectLetter.dday }})
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
import { formatServiceDateLocalized } from "@/utils/serviceDate";

// 백엔드 GlobalExceptionHandler 는 { error, message, status } 를 내려준다.
// 네트워크 단절이면 response 자체가 없으므로 호출부에서 기본 문구로 대체한다.
function serverMessage(error) {
  return error && error.response && error.response.data && error.response.data.message;
}

// 서버 dday 는 KST 달력 날짜 차이(LetterPaper.getDDay)고 openable 은 인스턴트 비교라,
// openAt 이 자정이 아닌 옛날 데이터에서는 dday=0 인데 아직 못 여는 구간이 존재한다.
function notYetOpenMessage(dday) {
  return dday > 0
      ? `아직 개봉할 수 없어요. 개봉까지 D-${dday} 남았습니다.`
      : '아직 개봉할 수 없어요. 오늘 중 개봉 시각이 지나면 열립니다.';
}

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
        // 개봉 전이라도 이 요청은 200 이다 — LetterService.getDetail 이 비밀번호만 맞으면
        // 조기 반환하고, 컨트롤러가 content 를 뺀 채 dday/openable 을 실어 보낸다.
        // 그래서 남은 날짜를 아는 유일한 경로가 이 성공 응답이다. 여기서 버리면 사용자는
        // "언제 열리는지" 를 어디서도 볼 수 없다.
        self.selectLetter = response.data;
        if (!self.selectLetter.openable) {
          alert(notYetOpenMessage(self.selectLetter.dday));
          return;
        }
        if (!self.selectLetter.content) {
          alert('편지 내용이 비어 있습니다.');
          return;
        }
        self.openLetter = true;
        self.passwordPopup.open = false;
      })
      .catch(error => {
        // 여기로 오는 건 비밀번호 불일치·없는 캡슐 같은 400 뿐이다(개봉 전은 위의 200 경로).
        // 서버 메시지를 버리면 그 400 들이 전부 한 문장으로 뭉개진다.
        alert(serverMessage(error) || '비밀번호가 틀렸거나 오류가 발생했습니다.');
        console.error(error);
      });
    },
    isOpened(letter) {
      return letter.status === 'OPENED';
    },
    capsuleClass(letter) {
      return (letter.capsuleType || 'BAMBOO').toLowerCase();
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
      return formatServiceDateLocalized(dateStr) || '날짜 미정';
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

  /* ⚠️ 여기에 min-height 를 다시 박지 말 것 — 특히 320px 같은 고정값.
   * 이 요소는 .game-field(스크롤 컨테이너) 안의 flex 아이템이고, 안쪽 .capsule-grid 는
   * 3행짜리 그리드라 실측 415px 를 요구한다. flex 아이템의 min-height 기본값은 auto,
   * 즉 "콘텐츠보다 작아지지 않는다" 인데, 320px 를 명시하면 그 자동 최소값을 **덮어써서**
   * 320px 까지 찌부러진다. 줄어든 건 컨테이너뿐이고 그리드는 그대로 415px 라
   * 95px 가 overflow:visible 로 아래 형제(.pagination / .footer-actions) 위로 삐져나온다.
   * 겹친 영역의 히트테스트는 트리 순서상 뒤에 오는 .pagination 이 가져가므로
   * 마지막 줄 캡슐 2개가 클릭을 통째로 잃는다. 배경이 투명해서 눈에는 멀쩡해 보인다.
   *
   * 게다가 삐져나온 부분은 .game-field 의 스크롤 범위에 잡히지 않는다 —
   * 스크롤을 끝까지 내려도 도달할 수 없고, 1366x768 에서는 스크롤 범위 자체가 0 이라
   * 스크롤바조차 안 생긴다("스크롤이 안 먹는다"의 정체).
   * 실측(공개 캡슐 7개 = 2페이지, min-height:320px 상태에서 5·6번 캡슐 도달 불가):
   *   1366x768(스크롤범위 0) / 1366x700 / 1280x720 / 844x390 / 360x560 / 320x480.
   * min-height 선언을 지워 auto 로 되돌리면 위 6종 전부에서 도달 가능해진다.
   *
   * min-height:0 도 답이 아니다 — 그건 오히려 261px 까지 더 찌부러뜨려서
   * 겹침을 키운다(그때는 .footer-actions 가 클릭을 가져갔다). 필요한 건 auto 다. */
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

.capsule-icon.bamboo { background-image: url(../assets/images/common/capsule/bamboo.png); }
.capsule-icon.bottle { background-image: url(../assets/images/common/capsule/bottle.png); }
.capsule-icon.egg { background-image: url(../assets/images/common/capsule/egg.png); }
.capsule-icon.candybox { background-image: url(../assets/images/common/capsule/candybox.png); }

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
.no-data .pixel-btn { margin-top: 14px; font-size: 11px; }

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
