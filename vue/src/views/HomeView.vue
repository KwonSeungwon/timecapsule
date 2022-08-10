<template>
  <div class="container">
    <div class="main" :class="theme">
      <router-view/>
    </div>
  </div>
</template>

<script>
//import axios from 'axios';
export default {
  name: 'HomeView',
  data () {
    return {
      theme : null,
      themes : ['beach', 'mountain', 'desert', 'space']
    }
  },
  methods : {
    setTheme () {
      //TODO :  login 상태 or 주소입력시 선택한 theme 표시
      //TODO : 우주는 좌표 배급때만 나오게 수정예정, 임시표시
      this.theme = this.themes[Math.floor(Math.random() * this.themes.length)];
    },
  },
  created() {
    this.setTheme();
  }
}
</script>
<style scoped>
.container {
  display: flex;
}
.main {
  margin: auto;
  width: 375px;
  height: 100vh;
  max-height: 812px;
}

.main.beach {
  background: url(../assets/images/beach.svg) no-repeat center;
  background-size: cover;
}

.main.mountain {
  background: url(../assets/images/bg_mountain.png) no-repeat center;
  background-size: cover;
}

.main.desert {
  background: url(../assets/images/bg_desert.png) no-repeat center;
  background-size: cover;
}
.main.space {
  background: url(../assets/images/bg_sc.png) no-repeat center;
  background-size: cover;
}



</style>
