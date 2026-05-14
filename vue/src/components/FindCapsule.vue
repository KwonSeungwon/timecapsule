<template>
  <div class="game-field">
    <div class="field-title">
      <h2 class="pixel-text">🔍 내 캡슐 찾기</h2>
    </div>
    
    <div class="form-container">
      <div class="search-icon">📦</div>
      <div class="input-group">
        <label>📧 수신자 이메일</label>
        <input type="email" v-model="email" class="pixel-input" placeholder="작성 시 입력한 이메일 주소">
      </div>
      <p class="info-text">묻어둔 타임캡슐 목록을 불러옵니다.</p>
      
      <div class="footer-actions">
        <button class="pixel-btn" @click="cancel">뒤로가기</button>
        <button class="pixel-btn primary" :disabled="!email" @click="search">조회하기</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "FindCapsule",
  data () {
    return {
      email : ''
    }
  },
  methods : {
    search() {
      if (this.email) {
        this.$router.push({ name: 'myCapsule', query: { email: this.email } });
      }
    },
    cancel() {
      this.$router.push('/');
    }
  }
}
</script>

<style scoped>
.field-title {
  text-align: center;
  margin-bottom: 30px;
}

.search-icon {
  font-size: 64px;
  text-align: center;
  margin-bottom: 20px;
}

.form-container {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.input-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
}

.info-text {
  font-size: 13px;
  color: #666;
  text-align: center;
  margin: 10px 0 30px;
}

.footer-actions {
  display: flex;
  gap: 10px;
  margin-top: auto;
}

.footer-actions .pixel-btn {
  flex: 1;
}
</style>
