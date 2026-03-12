document.addEventListener('DOMContentLoaded', function() {
// 회의록 등록 - 실시간 녹음

// 상수 정의
const CANVAS_WIDTH = 800;
const CANVAS_HEIGHT = 180;
const THRESHOLD = 10;
const SMOOTHING_FACTOR = 0.2;

// DOM 요소 셀렉터
const micSetupBtn = document.getElementById('micUse');
const micSetupWrap = document.getElementById('micsetupwrap');
const recordStartBtn = document.getElementById('recordStart');
const statusType = document.getElementById('statusAdjust');
const recTime = document.getElementById('recTime');
const recText = document.getElementById('rectxt');
const recUtil = document.getElementById('record_utilbx');
const recToggle = document.getElementById('recToggle');


const recUploadBtn = document.getElementById('recUploadBtn');
const recUploading = document.getElementById('recUploading');



const recSaveBtn = document.getElementById('recSaveBtn');
const recSaveLoading = document.getElementById('recSaveLoading');
const recSaveTimer = document.getElementById('savingTime');

const modalRmCls = document.getElementById('modalRmCls');


// UI 초기화 함수
function recordUiInit() {
    recordStartBtn.classList.add('recordStart');
//    micSetupBtn.classList.add('hidden');
    statusType.classList.add('recordStart');
    recToggle.classList.add('init');
}

// 마이크 설정 UI 업데이트 함수
function micSetupUi() {
    statusType.classList.add('recordMode');
    recordStartBtn.innerHTML = '<span>실시간 녹음시작</span>';
}

// 녹음 캔버스 초기화 함수
function initRec() {
    const recCanvas = document.getElementById('realtimeChart');
    const ctx = recCanvas.getContext('2d');
    recCanvas.width = CANVAS_WIDTH;
    recCanvas.height = CANVAS_HEIGHT;

    ctx.fillStyle = 'rgb(242, 244, 247)';
    ctx.fillRect(0, 0, recCanvas.width, recCanvas.height);

    ctx.lineWidth = 4;
    ctx.strokeStyle = 'rgb(0, 70, 253)';

    ctx.beginPath();
    ctx.moveTo(0, recCanvas.height / 2);
    ctx.lineTo(recCanvas.width, recCanvas.height / 2);
    ctx.stroke();
}

// 오디오 입력 장치 목록 채우기 함수
async function populateAudioInputDevices() {
    micSetupWrap.classList.add('active');
    const selectElement = document.getElementById('mcinput');
    try {
        const streamAvailable = await navigator.mediaDevices.getUserMedia({ audio: true });

        if (streamAvailable) {
            
            const devices = await navigator.mediaDevices.enumerateDevices();
            const audioInputs = devices.filter(device => device.kind === 'audioinput');

            audioInputs.forEach(device => {
                const option = document.createElement('option');
                option.value = device.deviceId;
                option.textContent = device.label || `Device ${device.deviceId}`;
                selectElement.appendChild(option);
            });

            streamAvailable.getTracks().forEach(track => track.stop());

//            micSetupBtn.classList.add('active');

        }
    } catch (error) {
        handleError(error);
    }
}

// 오류 처리 함수
function handleError(error) {
    if (error.name === 'NotAllowedError' || error.name === 'PermissionDeniedError') {
        alert('마이크 접근이 거부되었습니다. 마이크 권한을 허용해 주세요.');
    } else {
    	const msg = error.message;
        alert('오디오 입력 장치 접근 중 오류가 발생했습니다. 다시 시도해 주세요. (' + msg + ")");
    }
}

// 녹음 시작 함수
async function startRec() {
	let audioChunks = [];
	let interval;
    let mediaRecorder;
    let startTime;
    let isRecording = false;
    let elapsedTime = 0; // 누적된 시간 저장을 위한 변수
    let tracks;
    let audioContext;

    checkRecording = true;
    
    const recTimeElem = document.querySelector('.recTime');

    try {
        const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
        
        tracks = stream.getTracks();
        console.log("traks : " + tracks.length);

        audioContext = new (window.AudioContext || window.webkitAudioContext)();
        const source = audioContext.createMediaStreamSource(stream);
        const analyser = audioContext.createAnalyser();
        analyser.fftSize = 2048;
        const bufferLength = analyser.fftSize;
        const dataArray = new Uint8Array(bufferLength);

        source.connect(analyser);

        const recCanvas = document.getElementById('realtimeChart');
        const ctx = recCanvas.getContext('2d');
        recCanvas.width = CANVAS_WIDTH;
        recCanvas.height = CANVAS_HEIGHT;

        let lastSoundDetectedTime = 0;
        let smoothedRms = 0;

        function drawWaveform() {
            requestAnimationFrame(drawWaveform);

            analyser.getByteTimeDomainData(dataArray);

            ctx.fillStyle = 'rgb(242, 244, 247)';
            ctx.fillRect(0, 0, recCanvas.width, recCanvas.height);

            ctx.lineWidth = 4;
            ctx.strokeStyle = 'rgb(0, 70, 253)';

            ctx.beginPath();

            const sliceWidth = recCanvas.width * 5.0 / bufferLength;
            let x = 0;
            let sum = 0;

            for (let i = 0; i < bufferLength; i++) {
                const v = dataArray[i] / 128.0;
                const y = v * recCanvas.height / 2;

                if (i === 0) {
                    ctx.moveTo(x, y);
                } else {
                    ctx.lineTo(x, y);
                }

                x += sliceWidth;
                sum += (dataArray[i] - 128) * (dataArray[i] - 128);
            }

            const rms = Math.sqrt(sum / bufferLength);
            smoothedRms = SMOOTHING_FACTOR * smoothedRms + (1 - SMOOTHING_FACTOR) * rms;

            if (smoothedRms > THRESHOLD && Date.now() - lastSoundDetectedTime > 500) {
                console.log(`Sound detected with RMS value: ${smoothedRms}`);
                lastSoundDetectedTime = Date.now();
            }

            ctx.lineTo(recCanvas.width, recCanvas.height / 2);
            ctx.stroke();
        }

        drawWaveform();

        mediaRecorder = new MediaRecorder(stream);

        mediaRecorder.ondataavailable = (event) => {
            if (event.data.size > 0) {
                audioChunks.push(event.data);
            }
        };

        mediaRecorder.onstop = () => {
            const audioBlob = new Blob(audioChunks, { type: 'audio/ogg' });
            audioChunks = [];
            clearInterval(interval);
            recTimeElem.textContent = '00:00:00';

            // 오디오 파일을 저장하기 위한 다운로드 링크 생성
            const url = URL.createObjectURL(audioBlob);
            const a = document.createElement('a');
            a.style.display = 'none';
            a.href = url;
            a.download = "recording_on_" + todayFormat() + ".ogg";
            document.body.appendChild(a);
            a.click();
            URL.revokeObjectURL(url);

        };

        mediaRecorder.start();
        startTime = Date.now();
        isRecording = true;
        
        saveInterval = setInterval(() => {
            saveAudioData(audioChunks);
        }, 60000); // 1분마다 저장

        interval = setInterval(updateRecTime, 1000);

        function updateRecTime() {
            if (!isRecording) return; // 녹음 중이 아닐 때는 시간 업데이트 중지
            elapsedTime += 1;
            const hours = String(Math.floor(elapsedTime / 3600)).padStart(2, '0');
            const minutes = String(Math.floor((elapsedTime % 3600) / 60)).padStart(2, '0');
            const seconds = String(elapsedTime % 60).padStart(2, '0');
            recTimeElem.textContent = `${hours}:${minutes}:${seconds}`;
        }
    } catch (error) {
        console.error('Error accessing microphone:', error);
    }

    return {
        pause: () => {
            isRecording = false;
            clearInterval(interval); // 타이머 중지
            if ( mediaRecorder.state == 'active' ) {
            	mediaRecorder.pause(); // MediaRecorder 일시 중지
            }
        },
        resume: () => {
            isRecording = true;
            startTime = Date.now() - elapsedTime * 1000; // 누적 시간부터 다시 시작
            if ( mediaRecorder.state == 'active' ) {
            	mediaRecorder.resume(); // MediaRecorder 재개
            }
            interval = setInterval(updateRecTime, 1000); // 타이머 재개
        },


		stopNsave: (fileName = 'recording', saverAfter) => {
			fileName += "_" + todayFormat();
		    console.log('Saving file as:', fileName); // 파일명을 콘솔에 출력
		    isRecording = false;
		    clearInterval(interval); // 타이머 중지
		    mediaRecorder.stop(); // MediaRecorder 중지

		    mediaRecorder.onstop = () => {
		        const audioBlob = new Blob(audioChunks, { type: 'audio/ogg' });
		        audioChunks = [];
		        clearInterval(interval);
		        recTimeElem.textContent = '00:00:00';

		        // 오디오 파일을 저장하기 위한 다운로드 링크 생성
		        const url = URL.createObjectURL(audioBlob);
		        const a = document.createElement('a');
		        a.style.display = 'none';
		        a.href = url;
		        a.download = `${fileName}.ogg`; // 입력된 파일명으로 저장
		        document.body.appendChild(a);
		        a.click();
		        URL.revokeObjectURL(url);


		        // 저장이 완료된 후 콜백 함수 실행
		        if (typeof saverAfter  === 'function') {
		            console.log("저장완료!")
		        
					saverAfter();
		        }

		    };
		},

		stopNupload: (fileName = 'recording', saverAfter) => {
			fileName += "_" + todayFormat();
			console.log('Saving file as:', fileName); // 파일명을 콘솔에 출력
			isRecording = false;
			clearInterval(interval); // 타이머 중지
			mediaRecorder.stop(); // MediaRecorder 중지
			
			mediaRecorder.onstop = () => {
			    const audioBlob = new Blob(audioChunks, {
			        type: 'audio/ogg'
			    });
			    audioChunks = [];
			    clearInterval(interval);
			    recTimeElem.textContent = '00:00:00';

			    const file = new File([audioBlob], fileName + ".ogg", {
			        type: 'audio/ogg'
			    });

			    const formData = new FormData();
			    formData.append('file', file);
			    
			    let currentLocation = window.location;
			    console.log("currentLocation : " + currentLocation)

			    uploadFile(file);
			    
			    
			};
		},
		
		close: () => {
			audioContext.close();
		}
    };
}


// micSetupBtn.addEventListener('click', async function () {
// 	populateAudioInputDevices();
//     //micSetupUi();

// });


	// 녹음 시작 및 마이크 설정 관련 이벤트
	recordStartBtn && recordStartBtn.addEventListener('click', async function () {
	    if (recordStartBtn.classList.contains('recordReady')) {
	        console.log("버튼을 통한 시작");
	        micSetupWrap.classList.remove('active');
	        recordUiInit();
	        
	        setInterval(keepingLogin, 1800000);
	        
	        recorderControls = await startRec(); // 녹음 시작
	    } else {
	        recordStartBtn.classList.add('recordReady');
	        console.log("녹음준비");
	        populateAudioInputDevices();
	        micSetupUi();
	        initRec();
	    }
	});

	let recorderControls;
	// 토글버튼을 통한 녹음시작 및 일시중지 이벤트
	recToggle && recToggle.addEventListener('click', async function () {
	    if (!recToggle.classList.contains('init')) {
	    	micSetupWrap.classList.remove('active');
	        // 최초 클릭 시: 녹음을 위한 초기화 및 시작
			console.log("토글을을 통한 시작");

	        recToggle.classList.add('init');
	        recText.textContent = '실시간 녹음중';

	        recordUiInit(); // UI 초기화
	        recorderControls = await startRec(); // 녹음 시작
	    } else if (recToggle.classList.contains('init') && !recToggle.classList.contains('pause')) {
	        // 녹음 중일 때: 일시 중지 상태로 전환
	        if (recorderControls && typeof recorderControls.pause === 'function') {
	        	console.log('일시중지.'); 
	            recToggle.classList.add('pause');
	            statusType.classList.add('recordPause');
	            recText.textContent = '일시 중지됨';
	            recorderControls.pause();
	        } else {
	            console.error('recorderControls가 정의되지 않았거나, pause 메서드를 찾을 수 없습니다.');
	        }
	    } else if (recToggle.classList.contains('init') && recToggle.classList.contains('pause')) {
	        // 일시 중지 상태에서 다시 녹음 시작
	        if (recorderControls && typeof recorderControls.resume === 'function') {
	            recToggle.classList.remove('pause');
	            statusType.classList.remove('recordPause');
	            recText.textContent = '실시간 녹음 재개됨';
	            recorderControls.resume();
	        } else {
	            console.error('recorderControls가 정의되지 않았거나, resume 메서드를 찾을 수 없습니다.');
	        }
	    }
	});


	//녹음 파일업로드
	recUploadBtn && recUploadBtn.addEventListener('click', function () {
	    if (recorderControls && typeof recorderControls.pause === 'function') {
	        recorderControls.pause(); 
	        console.log('업로드를 위해서 녹음을 중단합니다.'); 
	    } 
	    
	    const fileName = 'realtime'; // 기본 파일명 설정

	    if (recorderControls && typeof recorderControls.stopNupload === 'function') {
	        // UI 변경
	        statusType.classList.remove('recordStart');
	        statusType.classList.add('recordUploading');
			recUploading.classList.add('active');

	        let countdown = 3;
	        const timexElem = document.getElementById('timex');
	        
	        const recuplodLoader = document.getElementById('recuplodLoader');
	        const uploadMmsgbx = document.getElementById('uploadMmsgbx');
            const uploadRecBtn = document.getElementById('uploadRecBtn');

	        timexElem.textContent = countdown;
	        
	        
	        recorderControls.stopNupload(fileName, function () {
	            // 저장 완료 후 실행할 동작 정의
				//+ statusType.classList.remove('recordUploading');
	        	//+ statusType.classList.add('recordSaveEnd');
				//+ recuplodLoader.style.display = 'none';
	        	
	             console.log('업로드 성공:', response);
	             //업로드 성공 이후 완료 메시지 출력 UI
	             recUploading.classList.remove('active');
	             uploadMmsgbx.textContent = '업로드가 완료되었습니다.';

					//이후 콜백시나리오
	            console.log('파일이 성공적으로 저장되었습니다.');

	        });
	    } else {
	        console.error('recorderControls가 정의되지 않았거나, stopNsave 메서드를 찾을 수 없습니다.');
	    }
	});

	//녹음 완료 후 내컴퓨터 저장 
	recSaveBtn && recSaveBtn.addEventListener('click', function () {


	    // 녹음을 멈추는 로직 추가
	    if (recorderControls && typeof recorderControls.pause === 'function') {
	        recorderControls.pause(); 
	        console.log('저장을 위해서 녹음을 중단합니다.'); 
	    } 

	    if (recorderControls && typeof recorderControls.stopNsave === 'function') {
	        // UI 변경
	        statusType.classList.remove('recordStart');
	        statusType.classList.add('recordSaving');
	        recSaveLoading.classList.add('active');


	        let countdown = 3;
	        const timexElem = document.getElementById('timex');
	        const recSaveToMypc = document.getElementById('recsavetoMypc');
	        const recSaveLoader = document.getElementById('recsaveLoader');
            const recSavePcBtn = document.getElementById('savingPcBtn');

	        
	        timexElem.textContent = countdown;

	        // 카운트다운 시작
	        const countdownInterval = setInterval(() => {
	            countdown -= 1;
	            timexElem.textContent = countdown;

	            if (countdown <= 0) {
	                clearInterval(countdownInterval);
	                // 녹음 저장
	                //recorderControls.stopNsave();

                    recSaveLoader.style.display = 'none';

                    recSaveToMypc.classList.add('active');
                    recSavePcBtn.classList.add('active');

                    recSaveTimer.textContent = '저장할 파일명을 입력해주세요.미 입력시 자동지정됩니다.';

	                // 저장 버튼 클릭 시 파일명으로 저장
					recSavePcBtn.addEventListener('click', function () {
					    const fileName = recSaveToMypc.value.trim() || 'recording'; // 기본 파일명 설정
					    if (recorderControls && typeof recorderControls.stopNsave === 'function') {
					        recorderControls.stopNsave(fileName, function () {
					            // 저장 완료 후 실행할 동작 정의

				            	recSavePcBtn.classList.remove('active');
				            	recSaveToMypc.classList.remove('active');
 								statusType.classList.remove('recordSaving');
	        					statusType.classList.add('recordSaveEnd');
				            	
 								recSaveTimer.textContent = '저장이 완료되었습니다.';

 								checkRecording = false;
 								
 								//이후 콜백시나리오
					            console.log('파일이 성공적으로 저장되었습니다.');
		
					        });
					    } else {
					        console.error('recorderControls가 정의되지 않았거나, stopNsave 메서드를 찾을 수 없습니다.');
					    }
					});
	            }
	        }, 1000);

	    } else {
	        console.error('recorderControls가 정의되지 않았거나, stopNsave 메서드를 찾을 수 없습니다.');
	    }
	});

	//모달닫기진행시
	function stopRecWithoutSaving() {
	    if (recorderControls) {
	        recorderControls.pause(); // 일시 중지
		    recorderControls.close();
	        recorderControls = null;  // 녹음 컨트롤 객체 초기화 
	    }
	}
	
	
	//회의록 등록 모달 종료
	modalRmCls && modalRmCls.addEventListener('click', function ()  {

	   const modalWrap = document.getElementById('modalWrap'); // 모달의 ID를 사용하여 모달 요소 선택
	   
		if(checkRecording) {
    		if(!confirm("실시간 음성녹화중입니다. 창을 닫으시겠습니까?")) {    			
    			return false
    		}else{
    			checkRecording = false
    		}    			
		}
	   
	    if (modalWrap) {
	    	
	        // 모달 닫기
	        modalWrap.classList.remove('active'); // 모달이 보이게 하는 'active' 클래스를 제거
	       	        
	        // 녹음 중지 및 초기화
	        stopRecWithoutSaving();
	        initRec();
	        
			statusType.classList.remove('recordStart');
	        statusType.classList.remove('recordMode');
			micSetupWrap.classList.remove('active');
			recordStartBtn.classList.remove('recordReady');
			recordStartBtn.classList.remove('recordStart');
			recToggle.classList.remove('init');
			recText.textContent = '실시간 녹음 중';	
			recTime.textContent = '00:00:00';		
//			micSetupBtn.classList.remove('active');
//			micSetupBtn.classList.remove('hidden');

		    let isRecording = false;
		    let elapsedTime = 0;

	        location.reload(true);  // reload from server
		    
	    } else {
	        console.error('모달 요소를 찾을 수 없습니다.');
	    }
	});
	
	window.addEventListener('beforeunload', (event) => {
	    if (checkRecording) {
	        event.preventDefault(); // 기본 동작 방지
	        event.returnValue = ''; // 브라우저 기본 메시지 표시

	    }
	});	
	
	function keepingLogin() {
	    fetch("/SGSAS/images/dash_icon01.svg", {})		
	    
	}
	
	function saveAudioData(audioChunks) {
	    if (audioChunks.length === 0) return; // 저장할 데이터가 없으면 종료

	    const audioBlob = new Blob(audioChunks, { type: 'audio/ogg' });
	    const fileName = `recording_part_${new Date().toISOString().replace(/[:.-]/g, '_')}.ogg`;

	    // 다운로드 방식으로 저장
	    const url = URL.createObjectURL(audioBlob);
	    const a = document.createElement('a');
	    a.style.display = 'none';
	    a.href = url;
	    a.download = fileName;
	    document.body.appendChild(a);
	    a.click();
	    URL.revokeObjectURL(url);

	    // 기존 데이터 초기화
	    audioChunks.length = 0;
	    console.log(`Saved recording: ${fileName}`);
	}
	
	

});



