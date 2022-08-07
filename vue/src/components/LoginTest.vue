<template>
  <div class="field">
  <div class="content">
    <div class="name"></div>
    <span class="title">타임캡슐 확인</span>
    <div style="top: 129px; position: absolute; left: 41px;">
      <div class="box">
        <a style="margin-left: 48px;">좌표</a>
      </div>
      <div style="margin-top: 8px;">
        <div class="inputField">
          <input v-model="id" id="id" placeholder="좌표명">

        </div>
      </div>
    </div>

    <span class="text">좌표가 없으신가요? 새로 만들기 </span>
    <div style="top: 270px; left: 41px; position: absolute;">

      <div class="box">
        <a style="margin-left: 33px;">비밀번호</a>
      </div>
      <div style="margin-top: 8px;">
        <div class="inputField">
          <input id="id" v-model="pw" placeholder="숫자 4자리">
        </div>
      </div>
      <p class="error-content">{{error}}</p>
    </div>

    <div class="footer">
      <div class="next" @click="next()">
        <div style="margin-top: 14px; margin-left: 32px;">
          <span style=" color: rgba(255, 255, 255, 1); opacity: 1; text-align: center;">다음</span>
        </div>
      </div>
      <div class="cancel">
        <div style="margin-top: 14px; margin-left: 32px;">
          <span style=" color: rgba(255, 255, 255, 1); opacity: 1; text-align: center;">취소</span>
        </div>
      </div>
     </div>
    <div class="name"></div>

  </div>
  </div>

</template>

<script>
import axios from "axios";

export default {
  name: "cLoginTest",
  data () {
    return {
      id : null,
      pw : null,
      error : null
    }
  },
  methods : {
    next: function() {
      if(this.id ==null){
        this.error = '아이디가 입력되지 않았습니다.';

      }else if(this.pw == null){
        this.error = '비밀번호가 입력되지 않았습니다.';
      }else{
        axios.get('/api/timecapsule/user',{
          params :{
            id : 1,
            password: 'test'
          }
        }).then((res)=> {
          console.log(res);
        });
      }
    }
  }
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

body {
  font-size: 14px;
}
.field {
  margin: 20% auto;
  width: 88%;
  height: 80%;
  background: url(../assets/images/field.png) no-repeat center transparent;
  /*background-size: cover;*/
}
.content {
  width: 375px;
  height: 700px;
  opacity: 1;
  position: absolute;
  overflow: hidden;
}

.name {
  color: #fff;
}


.title {
  width: 166px;
  color: rgba(255, 255, 255, 1);
  position: absolute;
  top: 48px;
  left: 39px;
  text-shadow: 1px 1px 0px rgb(0 0 1);
  font-weight: Regular;
  font-size: 25px;
  opacity: 1;
  text-align: left;
  text-shadow: -1px 0px #6698ff, 3px 1px #6698ff, 3px 0px #6698ff, 2px -2px #6698ff;
}

.box {
  width: 124px;
  height: 37px;
  background: url("../assets/images/boxImage.png");
  background-repeat: no-repeat;
  background-position: center center;
  background-size: cover;
  opacity: 1;
  overflow: hidden;
}
.box a{
  color: white;
  text-shadow: -1px 0 #677ce3, 0 1px #3949b7, 1px 0 #000, 0 -1px #000;
  margin-left: 48px;
  position: absolute;
  top: 11px;
  font-size: 13px;
}

.inputField input{
  width: 260px;
  height: 34px;
  background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAASUAAAAqCAYAAAD1Rk0WAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAFKSURBVHgB7dfBSSRRFIbR96QdhlmN7sUQxAhsMzADzcRYzKBDqBBsIxBEFwriTkW7rl2gKZT/4pzF4/IC+Li3t283VSdjbYYGMLPqdXrcd4dpXkzPuur8X23O9tpOA5jbY42X6/o8POqLq8W0If2pzcXf1pf7vTeAub1WX75VvWx7dNuvx8862G5IggT8pueqdtdG9xqQRZSAKKIERBElIIooAVFECYgiSkAUUQKiiBIQRZSAKKIERBElIIooAVFECYgiSkAUUQKiiBIQRZSAKKIERBElIIooAVFECYgiSkAUUQKiiBIQRZSAKKIERBElIIooAVFECYgiSkAUUQKiiBIQRZSAKKIERNmpXqcvva3ua2wAv+Fh25+nPg5TjxbHfXdYVx1+tPH/c6tlA5jZW++r91arqUf95/Om6mSszdAAZjZtSFOQpvkLj0xVbXJ/DsgAAAAASUVORK5CYII=);
  background-repeat: no-repeat;
  background-position: center center;
  background-size: cover;
  background-repeat: round;
  opacity: 1;
  position: absolute;
  overflow: hidden;
  border-top: none;
  border-bottom: none;
  border-left: none;
  border-right: none;
  border-radius: 8px;
}

.text {
  width: 179px;
  color: rgba(37, 76, 175, 1);
  position: absolute;
  top: 225px;
  left: 44px;
  font-family: DungGeunMo;
  font-weight: Regular;
  font-size: 12px;
  opacity: 1;
  text-align: left;
}


.footer {
  width: 217px;
  height: 41px;
  background-repeat: no-repeat;
  background-position: center center;
  background-size: cover;
  opacity: 1;
  position: absolute;
  top: 521px;
  left: 50px;
  overflow: hidden;
}

.next {
  width: 104px;
  height: 41px;
    background: url("../assets/images/next.png");
  background-repeat: no-repeat;
  background-position: center center;
  background-size: cover;
  opacity: 1;
  position: absolute;
  top: 0px;
  left: 113px;
  overflow: hidden;
}

.cancel {
  width: 104px;
  height: 41px;
  background: url("../assets/images/cancle.png");
  background-repeat: no-repeat;
  background-position: center center;
  background-size: cover;
  opacity: 1;
  position: absolute;
  top: 0px;
  left: 0px;
  overflow: hidden;
}

.name {
  color: #fff;
}




</style>
