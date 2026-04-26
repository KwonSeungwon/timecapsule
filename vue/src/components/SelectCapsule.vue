<template>
  <div class="field pixel-border">
    <div class="title">
      <p class="pixel-text-shadow">타임캡슐 선택</p>
    </div>
    <div class="content">
      <div class="letter-holder">
        <div class="letter-object" :class="{'on' : capsuleId === 'BOTTLE'}">
          <div class="selectField">
            <input id="capsule1" type="radio" v-model="capsuleId" value="BOTTLE" @click="selectCapsule('BOTTLE')">
            <label for="capsule1" class="capsule bottle"></label>
          </div>
          <p>유리병</p>
        </div>
        <div class="letter-object" :class="{'on' : capsuleId === 'BAMBOO'}">
          <div class="selectField">
            <input id="capsule2" type="radio" v-model="capsuleId" value="BAMBOO" @click="selectCapsule('BAMBOO')">
            <label for="capsule2" class="capsule bamboo"></label>
          </div>
          <p>죽통</p>
        </div>
        <div class="letter-object" :class="{'on' : capsuleId === 'EGG'}">
          <div class="selectField">
            <input id="capsule3" type="radio" v-model="capsuleId" value="EGG" @click="selectCapsule('EGG')">
            <label for="capsule3" class="capsule egg"></label>
          </div>
          <p>달걀</p>
        </div>
        <div class="letter-object" :class="{'on' : capsuleId === 'CANDYBOX'}">
          <div class="selectField">
            <input id="capsule4" type="radio" v-model="capsuleId" value="CANDYBOX" @click="selectCapsule('CANDYBOX')">
            <label for="capsule4" class="capsule candybox"></label>
          </div>
          <p>캔디통</p>
        </div>
      </div>
    </div>
    <Footer next="매립하기" :disable_next="capsuleId === null" v-on:footer_res="complete"></Footer>
  </div>
</template>

<script>
import Footer from '@/components/Footer';
import axios from 'axios';

export default {
  name: "selectCapsule",
  components : {Footer},
  data () {
    return {
      capsuleId : null,
      parameters : {}
    }
  },
  methods : {
    selectCapsule(id) {
      this.capsuleId = id;
      this.parameters.capsuleType = id;
    },
    complete (next) {
      if (next) {
        // 도트 삽 애니메이션 효과를 줄 수 있는 상태값 추가 가능
        axios.post('/api/v1/letter', this.parameters).then(res => {
          this.$router.push('/success');
        });
      }
    }
  },
  created() {
    this.parameters = { ...this.$route.query };
  }
}
</script>

<style scoped>
.field {
  margin: 20% auto;
  width: 341px;
  background: #fdf6e3;
  padding: 20px;
}

.capsule {
  width: 80px;
  height: 80px;
  display: block;
  margin: 0 auto;
  background-size: contain;
  background-repeat: no-repeat;
}

.bottle { background-image: url(../assets/images/beach/beach.png); }
.bamboo { background-image: url(../assets/images/capsule_bamboo.svg); }
.egg { background-image: url(../assets/images/capsule_egg.svg); }
.candybox { background-image: url(../assets/images/common/capsule/candybox.PNG); }

.letter-object {
  width: 45%;
  display: inline-block;
  margin: 2%;
  text-align: center;
  transition: transform 0.2s;
}

.letter-object.on {
  transform: scale(1.1);
  filter: drop-shadow(0 0 5px #FFE24A);
}

.selectField {
  padding: 10px;
  background: rgba(0,0,0,0.05);
  border-radius: 8px;
}

.pixel-text-shadow {
  font-size: 24px;
  text-align: center;
}
</style>
