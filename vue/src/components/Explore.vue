<template>
  <div class="game-field">
    <div class="field-title">
      <h2 class="pixel-text">🗺️ 공개 캡슐 탐색</h2>
    </div>

    <div class="explore-body">
      <div v-if="isLoading" class="state-box">
        <div class="state-icon">⏳</div>
        <p>땅을 파는 중...</p>
      </div>

      <div v-else-if="error" class="state-box">
        <div class="state-icon">⚠️</div>
        <p>{{ error }}</p>
        <button class="pixel-btn" @click="load">다시 시도</button>
      </div>

      <div v-else-if="capsules.length === 0" class="state-box">
        <div class="state-icon">🕳️</div>
        <p>아직 공개된 캡슐이 없습니다.</p>
      </div>

      <div v-else class="biome-list">
        <p class="summary">총 {{ capsules.length }}개의 캡슐이 묻혀 있습니다.</p>

        <section v-for="group in groupedByBiome" :key="group.biome" class="biome-group">
          <h3 class="biome-heading">
            {{ group.emoji }} {{ group.name }}
            <span class="biome-count">{{ group.items.length }}</span>
          </h3>

          <div class="capsule-grid">
            <article v-for="capsule in group.items" :key="capsule.id" class="capsule-card"
                     :class="{ openable: capsule.openable }">
              <div class="card-head">
                <span class="card-title">{{ capsule.title }}</span>
                <span class="dday" :class="{ ready: capsule.openable }">
                  {{ capsule.openable ? 'OPEN' : 'D-' + capsule.dday }}
                </span>
              </div>
              <p class="card-sender">✍️ {{ capsule.senderName }}</p>
              <p class="card-coords">📍 위도 {{ capsule.xCoord }} / 경도 {{ capsule.yCoord }}</p>
              <p class="card-date">📅 {{ formatDate(capsule.openAt) }} 개봉</p>
              <p v-if="capsule.openable" class="card-hint">
                개봉일이 지났습니다. 수신자만 암호로 열어볼 수 있습니다.
              </p>
            </article>
          </div>
        </section>
      </div>
    </div>

    <div class="footer-actions">
      <button class="pixel-btn" @click="$router.push('/')">🏠 홈으로</button>
      <button class="pixel-btn primary" @click="$router.push('/find')">내 캡슐 찾기</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { toServiceDate } from "@/utils/serviceDate";

// 백엔드 LocationMapper 의 바이옴과 1:1 로 맞춘다.
const BIOME_META = {
  BEACH: { name: '고요한 바다', emoji: '🌊' },
  MOUNTAIN: { name: '바람의 능선', emoji: '⛰️' },
  DESERT: { name: '붉은 사막', emoji: '🏜️' },
  FOREST: { name: '신비한 숲', emoji: '🌲' }
};

const BIOME_ORDER = ['BEACH', 'MOUNTAIN', 'DESERT', 'FOREST'];

export default {
  name: "Explore",
  data() {
    return {
      capsules: [],
      isLoading: true,
      error: ''
    };
  },
  computed: {
    groupedByBiome() {
      const buckets = new Map();
      for (const capsule of this.capsules) {
        const key = BIOME_META[capsule.biome] ? capsule.biome : 'UNKNOWN';
        if (!buckets.has(key)) buckets.set(key, []);
        buckets.get(key).push(capsule);
      }

      const order = [...BIOME_ORDER, 'UNKNOWN'];
      return order
        .filter(key => buckets.has(key))
        .map(key => ({
          biome: key,
          name: BIOME_META[key] ? BIOME_META[key].name : '알 수 없는 곳',
          emoji: BIOME_META[key] ? BIOME_META[key].emoji : '📍',
          // 개봉이 임박한 순서로 — 이미 열린 캡슐이 위로 온다.
          items: buckets.get(key).slice().sort((a, b) => a.dday - b.dday)
        }));
    }
  },
  created() {
    this.load();
  },
  methods: {
    load() {
      this.isLoading = true;
      this.error = '';
      const self = this;
      axios.get('/api/v1/capsule/public')
        .then(function (response) {
          self.capsules = Array.isArray(response.data) ? response.data : [];
        })
        .catch(function (err) {
          self.error = '캡슐 목록을 불러오지 못했습니다: '
            + (err.response?.data?.message || '알 수 없는 오류');
        })
        .finally(function () {
          self.isLoading = false;
        });
    },
    formatDate(value) {
      // 바로 옆 뱃지의 D-Day 는 서버가 KST 로 센 값이다. 여기만 브라우저 로컬로 찍으면
      // 미국 서부에서 "2026.07.31 개봉" 옆에 8/1 까지 세는 D-Day 가 나란히 붙는다.
      const serviceDate = toServiceDate(value);
      return serviceDate ? serviceDate.split('-').join('.') : '날짜 미정';
    }
  }
}
</script>

<style scoped>
.field-title {
  text-align: center;
  margin-bottom: 20px;
}

.explore-body {
  flex: 1;
  overflow-y: auto;
  padding-right: 4px;

  /* flex 아이템의 min-height 기본값은 0 이 아니라 auto 라서, 콘텐츠보다 작게
   * 줄어들지 않는다. 그래서 이 min-height:0 이 없으면 바로 위의 overflow-y:auto 가
   * 영영 발동하지 않는다 — .explore-body 가 캡슐 목록 높이만큼 그대로 늘어나서
   * 넘칠 것이 없기 때문이다. 그러면 대신 .game-field 쪽이 스크롤되고,
   * 제목과 하단 버튼까지 같이 밀려 올라간다.
   * 이 한 줄이 있어야 목록만 스크롤되고 제목·버튼은 제자리에 고정된다. */
  min-height: 0;
}

.state-box {
  text-align: center;
  padding: 60px 20px;
  color: #555;
}

.state-icon {
  font-size: 56px;
  margin-bottom: 12px;
}

.state-box p {
  margin-bottom: 16px;
  font-size: 15px;
}

.summary {
  font-size: 13px;
  color: #666;
  margin-bottom: 14px;
  text-align: center;
}

.biome-group {
  margin-bottom: 26px;
}

.biome-heading {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: bold;
  color: #5d7a54;
  border-bottom: 3px solid #000;
  padding-bottom: 6px;
  margin-bottom: 12px;
}

.biome-count {
  background: #5d7a54;
  color: #fff;
  font-size: 12px;
  padding: 1px 8px;
  border-radius: 10px;
}

.capsule-grid {
  display: grid;

  /* ⚠️ minmax 의 첫 인자를 고정 길이(220px)로 되돌리지 말 것.
   * 1fr 은 최대값일 뿐이라 트랙은 220px 밑으로 못 내려가고, 그리드가 그보다
   * 좁아지는 순간 카드가 그대로 삐져나간다. auto-fill 도 못 구한다 — 어떤
   * 반복 수로도 넘치면 1열로 떨어질 뿐 하한 자체는 그대로다.
   * min(220px, 100%) 로 감싸야 넓을 때는 220px 하한이 살고 좁을 때만 내려간다.
   *
   * 여기가 늘 1열인 건 "폭이 좁아서"가 아니라 timecapsulue.css 의
   * .game-field { max-width: 420px } 때문이다. 그 상한 탓에 .explore-body 는
   * 아무리 넓은 화면에서도 372px(뷰포트 8400/19≈442.11 ~ 480px, 0.95*뷰포트폭 이
   * max-width 420 에 닿으면서 padding 이 아직 16px 인 구간이 최대)를 못 넘고,
   * padding-right:4px 를 뺀 그리드는 368px 다.
   * 2열엔 220+12+220=452px 이 필요하니 368px 로는 어림도 없다.
   * max-width 를 올리면 이 문단은 아무 신호 없이 거짓이 된다.
   *
   * 실측(.explore-body 기준 가로 넘침, 수정 전 → 후):
   *   뷰포트 280px  컨테이너 218px   2px → 0 (카드 220 → 214)
   *   뷰포트 250px  컨테이너 189.5px 30px → 0 (카드 220 → 185.5)
   *   뷰포트 220px  컨테이너 161px  59px → 0 (카드 220 → 157)
   *   1366/844 컨테이너 356·카드 352, 320 은 256·252 — 전후 완전 동일
   * 전후가 같아지는 경계는 컨테이너가 아니라 **그리드 폭 220px** 이고,
   * .explore-body 의 padding-right:4px 만큼 밀려 컨테이너로는 224px 이다.
   * 그 사이(컨테이너 220~223px)는 넘침이 양쪽 다 0 이면서 카드 폭만 다르다.
   *
   * myCapsule.vue:268 에도 동명의 .capsule-grid 가 있다(repeat(2, 1fr), 다른
   * scope). 정의는 그 둘뿐이지만 클래스명으로 grep 하면 템플릿 사용처와
   * 별개 클래스인 .capsule-grid-container 까지 딸려 오니 헷갈리지 말 것. */
  grid-template-columns: repeat(auto-fill, minmax(min(220px, 100%), 1fr));
  gap: 12px;
}

.capsule-card {
  background: #fff;
  border: 4px solid #000;
  box-shadow: 4px 4px 0 rgba(0, 0, 0, 0.25);
  padding: 12px 14px;
}

.capsule-card.openable {
  box-shadow: 4px 4px 0 #ffe24a;
}

.card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 8px;
}

.card-title {
  font-weight: bold;
  font-size: 15px;
  color: #111;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dday {
  flex-shrink: 0;
  font-size: 11px;
  font-weight: bold;
  border: 2px solid #000;
  padding: 1px 6px;
  background: #eee;
}

.dday.ready {
  background: #ffe24a;
}

.capsule-card p {
  font-size: 12px;
  color: #555;
  line-height: 1.7;
  margin: 0;
}

.card-hint {
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px dashed #ccc;
  color: #8a6d00;
}

.footer-actions {
  display: flex;
  gap: 10px;
  margin-top: 16px;
}

.footer-actions .pixel-btn {
  flex: 1;
}
</style>
