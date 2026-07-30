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
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
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
