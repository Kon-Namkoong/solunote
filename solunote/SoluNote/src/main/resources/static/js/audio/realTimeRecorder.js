var hostIndex = location.href.indexOf(location.host) + location.host.length;
var ctxRoot = location.href.substring(0, location.href.indexOf("/", hostIndex) <= 0 ? hostIndex + 1 : location.href.indexOf("/", hostIndex + 1));

var SEND_DOMAIN_CODE = "SG0001";
var RECEIVE_REAL = false;

/**
 * 실시간레코더 realTimeRecorder.js
 * 
 * @author 윤기정
 * @since 2020.07.27
 * @version 1.0
 * @see
 * 	- 2020.07.28(박현택) 소스 변경
 * @description
 *  - 마이크 스트리밍 
 *   getUserMedia
 *  - 스트리밍 녹음
 *   MediaRecorder
 *  - 신호 탐지 방법
 *   1. Zero Crossing
 *   2. Amp Size
 */
var track = false;
var recorder = null;
var volume = null;
var chunks = [];
var workStatus = 0; // (0:음성녹음종료, 1:발화 후 음성녹음 중, 2:음성녹음시작, 3: 강제종료)
var getStream = false;
var blobUrl = "";
var lastClap = null; // 마지막 발화 시간
var stopStatus = ""; // 녹음종료 상태(null:, 1:묵음종료)


var Recording = function(cb){
	var recording = true;
	var audioInput = null;
	var audioContext = null;
	var callback = cb;
	
	navigator.mediaDevices.getUserMedia = navigator.mediaDevices.getUserMedia || navigator.mediaDevices.webkitGetUserMedia || 
	navigator.mediaDevices.mozGetUserMedia || navigator.mediaDevices.msGetUserMedia;
	
	// browser check
	if(navigator.mediaDevices.getUserMedia){
		navigator.mediaDevices.getUserMedia({audio:true}).then(function(stream){
	        // create stop object
			// track.stop() = microphton off
			track = stream.getAudioTracks()[0];
			// record 
			getStream = new MediaRecorder(stream);
			// record start
			getStream.start();
			getStream.ondataavailable = function(e) {
				// record buffer collect
	            chunks.push(e.data);
			} 
			// record stop 
			getStream.onstop = function(e) {
				var blob = new Blob(chunks, {type : 'audio/wav', sampleRate: 8000, get16BitAudio: true, codecs:'pcm'});
	        	 
	            var reader = new FileReader();
	            var base64data;
	            reader.readAsDataURL(blob); 
	            reader.onloadend = function() {
	            	
	            	console.log("stopStatus ////"+stopStatus);
	            	
	            	if(stopStatus == ""){ 
	            		base64data = reader.result;
	            		fnSendVoice(base64data);
	            	} else{ // 녹음 시작 후 5초 동안 발화가 없는 경우 종료 인 경우
	            		stopStatus = ""; // 초기화
	            		var msg = "음성 발화 시간이 초과되어 음성인식 서비스를 종료하였습니다";
	            		alert(msg);
	            		var param = {};
	        			param.text = msg;
	            	}
	            }
			}

			// get audio buffer
	        var AudioContext = window.AudioContext || window.webkitAudioContext;
	        audioContext = new AudioContext();
	        volume = audioContext.createGain();
	        audioInput = audioContext.createMediaStreamSource(stream);
	        audioInput.connect(volume);
	        recorder = audioContext.createScriptProcessor(2048, 1, 1);
	        recorder.onaudioprocess = function(e){
	        	if(!recording) return;
	           	// auodio buffer to float
	           	var left = e.inputBuffer.getChannelData(0);
	           	fnCallback(callback, left);
	        };
	        // connect the recorder
	        volume.connect(recorder);
	        recorder.connect(audioContext.destination);
		});
	} else{
		alert('Error capturing audio.');
	}
};

// Recording 콜백 함수
function fnCallback(callback, left){
	callback(new Float32Array(left));
}

/*음성 감지 체크
 * t - lastClap
        대화 이후 묵음 시간 (millisecond 1/1000초)
 * zero crossing
        음성 신호가 0을 몇번을 통과 하는지를 통해 음성 구간을 검출한다.
              통과 횟수를 높게 잡으면 음성의 구간을 넓게 잡고, 반대로 통과 횟수를 낮게 잡으면 음성의 구간을 좁게 잡음
              제로크로싱 횟수 : zeroCrossings > 80
 * 음성 신호의 세기
        음성신호가 특정 임계치를 몇 번 넘겼는지를 탐지
        임계치를 낮게 잡거나 임계치 넘긴 횟수를 많이 허용하면 잡음에 예민함
        반대로 임계치를 높게 잡거나 임계치를 넘긴 횟수를 적게 허용하면 잡음을 무시할 수 있음.
        임계치 : if(Math.abs(data[i]) > 0.25) highAmp++;
        임계치 횟수 : highAmp > 10                     
*/
function detectClap(data){
	var t = (new Date()).getTime(); // 현재 시간
	var zeroCrossings = 0, highAmp = 0; // 제로크로싱, 임계치
	console.log("t(현재 시간) : " + t +" / lastClap(마지막 발화 시간) : "+ lastClap + " / t - lastClap(대화 이후 묵음 시간) : " + (t - lastClap));		
    if(t - lastClap > 2000){
    	console.log("종료 ["+"workStatus(음성녹음 상태) : "+workStatus+" / highAmp(임계치 횟수) : " + highAmp+' / zeroCrossings(제로크로싱 횟수) : '+zeroCrossings+"]");
    	// 발화 후 음성녹음 중 상태에서 4초 동안 발화가 없는 경우 종료
    	if (workStatus == 1){
    		fnStop();  
    		return false;
		// 음성녹음시작 상태
    	} else if(workStatus == 2) { 
    		// 녹음 시작 후 5초 동안 발화가 없는 경우 종료
    		if(t - lastClap > 5000){
    			fnStop("1");  
    			stopStatus = "1";
	    		return false;
    		}
    	}
    }
    
    for(var i = 1; i < data.length; i++){
    	if(Math.abs(data[i]) > 0.25) highAmp++;
    	if(data[i] > 0 && data[i-1] < 0 || data[i] < 0 && data[i-1] > 0) zeroCrossings++;
    }
    if(highAmp > 20 && zeroCrossings > 20){ // 제로크로싱 횟수, 임계치 횟수
    	console.log("유지 ["+"workStatus(음성녹음 상태) : "+workStatus+" / highAmp(임계치 횟수) : " + highAmp+' / zeroCrossings(제로크로싱 횟수) : '+zeroCrossings+"]");	    	
    	lastClap = t;
    	workStatus = 1;
    	return true;
    }
}

var rec;
// 음성녹음시작
function fnStart(){

    console.log('음성 입력 시작');
    
    if(!(workStatus == 2 || workStatus ==1)) {
    	
	    track = false;
	    chunks = [];
	    lastClap = (new Date()).getTime(); // 마지막 발화 시간
	    rec = new Recording(function(data){
	    	if(detectClap(data)){
	    		console.log('clap!');
	    	}
	    });
	    
		workStatus = 2;
    }

}

// 음성녹음종료
function fnStop(){
	stopStatus = "";
	workStatus = 0;
	track.stop();
	getStream.stop();
	recorder.onaudioprocess = null;
	console.log('음성 입력 종료');
}


//음성녹음종료
function fnFinStop(){
	fnStop();
	workStatus = 3;
}

// 키보드 이벤트(필요시 사용)
document.onkeypress = function(e) {
	e = e || window.event;
    var charCode = e.keyCode || e.which;
    // 스페이스바
    if (charCode == 32) {
    	if (workStatus == 0){
    		fnStart();
    	} else {
    		fnStop();
    	}	
    }
}

// 음성 녹음 데이터 전송
function fnSendVoice(base64data){
	
	rsVoiceUpload("{\"sttList\":[{\"sttResult\":\"음성파일 업로드 중입니다.\"}]}");
	
	var param = {};
		param.base64data = base64data;
		param.s_domain_code= SEND_DOMAIN_CODE;
		param.receive_type = RECEIVE_REAL;
	$.ajax({
        type: "POST",
        asyncType : false,
        datatype: "text",
        url : contextPath + "/RECORD/doRecord",
        data: param,
        success: function(data) {
            console.log(data);
            if(SEND_DOMAIN_CODE != "SG0003") {
            	if(workStatus != 3)
            		fnStart();
            } 

            rsVoiceUpload(data);
        },
        error: function(aa,bb,cc){
            alert("error");
        }
    });
}

