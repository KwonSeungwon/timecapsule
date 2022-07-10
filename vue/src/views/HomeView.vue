<template>
  <div class="container">
    <div class="main" :class="theme">
      <router-view/>
    </div>
  </div>
</template>

<script>

export default {
  name: 'HomeView',
  data () {
    return {
      theme : null,
      themes : ['beach', 'mountain', 'desert', 'jungle']
    }
  },
  methods : {
    setTheme () {
      this.theme = this.themes[Math.floor(Math.random() * this.themes.length)];
    }
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

</style>
