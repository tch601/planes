<template>
      <v-row justify="center">
        <div class="core-page-loader splash disable-spinner"
             style="transform: translate3d(100%, 0px, 0px);">
          <v-row justify="center" style="padding-top:30px;">
            <div class="plane-spinner">
                <div class="spinner-plane">
                </div>
            </div>
          </v-row>
        </div>
      </v-row>
</template>

<style media="screen" type="text/css">
	body {
		background-size:     cover;
		background-repeat:   no-repeat;
		background-position: center center;
	}
  .core-page-loader {
    position: fixed;
    width: 500;
    height: 500;
    z-index: 1000;
    pointer-events: none;
    transform: translateZ(0);
  }

  .core-page-loader .plane-spinner {
    margin: 15px auto 0;
  }
  .core-page-loader .plane-spinner::before {
    background: #424242;
  }
  .core-page-loader .plane-spinner::after {
    background: #424242;
  }

  .core-page-loader.splash .plane-spinner{
    /* margin: -35px 0px 0px -35px; */
    position: absolute;
    /* top: 50%;
    left: 50%; */
    transform: translate(-50%, -50%);
  }

  .spinner-plane {
    -webkit-transform: translateZ(0);
    -webkit-backface-visibility: hidden;
    transform: translateZ(0);
    backface-visibility: hidden;
    perspective: 1000;
    background: url('../assets/loadingplane2.svg');
    background-size: 40px;
    background-repeat: no-repeat;
    position: relative;
    left: 40px;
    bottom: 30px;
    z-index: 999;
    transform: rotate(-16deg);
    height: 40px;
    /* width: 40px; */
  }

  .plane-spinner {
    height: 70px;
    width: 70px;
    border-radius: 50px;
    background: linear-gradient(to top,#F1C933 10%,rgba(229,229,229,0) 90%);
    position: relative;
    animation: spin 990ms linear reverse infinite;
    -webkit-animation: spin 990ms linear reverse infinite;
  }

  .plane-spinner::before {
    -webkit-transform: translateZ(0);
    -webkit-backface-visibility: hidden;
    transform: translateZ(0);
    backface-visibility: hidden;
    perspective: 1000;
    content: '';
    background: #ffffff;
    display: block;
    /* width: 67px; */
    height: 67px;
    border-radius: 100%;
    position: relative;
    top: 0;
    left: 4px;
  }
  .plane-spinner::after {
    -webkit-transform: translateZ(0);
    -webkit-backface-visibility: hidden;
    transform: translateZ(0);
    backface-visibility: hidden;
    perspective: 1000;
    content: '';
    background: #ffffff;
    display: block;
    position: absolute;
    right: 0;
    bottom: 0;
  }

  @-webkit-keyframes spin {
      0% {transform: rotate(0deg); }
      100% {transform: rotate(360deg); }
  }
  @keyframes spin {
      0% {transform: rotate(0deg); }
      100% {transform: rotate(360deg); }
  }
	#footer {
		width: 100%;
		margin-top: 760px;
	}  
</style>