document.addEventListener('DOMContentLoaded', function () {

	//#fn1. 회의록등록 - 파일 업로드


	//upload회의록등록

	const dropZone = document.getElementById('uploadDropZone');
	const uploadhiddenInput = document.getElementById('uploadhiddenInput');
	const uploadfilePath = document.getElementById('fileInput');
	const uploadGuidetxtBx = document.getElementById('guidetext');
	const uploadSumbitBtn = document.getElementById('uploadSubmit');
	
	let file
	
	// 클릭활용 후 직접업로드
	uploadfilePath && uploadfilePath.addEventListener('change', function (event) {
		file = event.target.files[0];
		if (file) {

			uploadhiddenInput.value = file.name; // 파일 이름을 히든 인풋에 저장
			dropZone.classList.add('clickUploading');
			uploadGuidetxtBx.textContent = `업로드예정 파일명: ${file.name}`;
			
//		        uploadEvent(file)
						
		}
		
	});
	

		
	// 드래그 활용 업로드

	// 드래그 오버(Drag Over) 시 스타일 변경
	dropZone && dropZone.addEventListener('dragover', (event) => {
		event.preventDefault();
		dropZone.classList.add('dragover');

	});

	// 드래그 떠날 경우 (Drag Leave) 시 스타일 qhrrn
	dropZone && dropZone.addEventListener('dragleave', () => {
		dropZone.classList.remove('dragover');
	});

	// 파일 드롭 시 처리
	dropZone && dropZone.addEventListener('drop', (event) => {
		event.preventDefault();
		dropZone.classList.remove('dragover');

		const files = event.dataTransfer.files;
		file = event.dataTransfer.files[0];
		if (files.length > 0) {
			const ckeckfile = files[0];

			// 파일 확장자 확인 (옵션)
			const allowedExtensions = ['wav', 'mp3', 'mp4', 'ogg', 'm4a', 'aac', 'mpeg', 'webm', 'avi'];
			const fileExtension = ckeckfile.name.split('.').pop().toLowerCase();

			dropZone.classList.add('dropUploading');

			if (allowedExtensions.includes(fileExtension)) {
				uploadhiddenInput.value = ckeckfile.name; // 파일 이름을 히든 인풋에 저장
				uploadGuidetxtBx.textContent = `업로드예정 파일명: ${file.name}`;
				
				 (ckeckfile)
			} else {
				file
				uploadGuidetxtBx.innerHTML = '잘못된 파일입니다.  wav, mp3, mp4, ogg, m4a, aac, mpeg, webm, avi 확장자만 시도해주세요.';
			}
		}
	});
	
	
	
	// 클릭활용 후 직접업로드
	uploadSumbitBtn && uploadSumbitBtn.addEventListener('click', function (event) {
		uploadEvent(file)

	});
	

    $("#trainFile").on("click", function () {
        $("#trainFileInputhidden").click()
    })

    //파일 업로드 파일 형식  체크 및 input 값에 적용
    $("#trainFileInputhidden").on("change", function (e) {     
		file = event.target.files[0];
		if (file) {	
			
			const text = `${file.name}`
			console.log(text)
			$("#trainFileInput").val(text) 
		}
	
    })
    
    $("#trainSubmit").on("click", function () {
    	uploadEvent(file)
    	$(".lang_select").hide()
    	$("#trainInputGroup").hide()
    })    
    


});


////파일 업로드 실행시 서버와 통신
function uploadEvent(file) {
    fileValidation(file).then(boolResult => {
        if (!boolResult)
        	alert("*mp3,mp4,wav,ogg 파일 지원, 1회 최대 150분 길이");
        else {            
            uploadFile2(file);
        }
    }).catch((e) => {
        alert("*파일이 없거나 잘못된 파일입니다. mp3, mp4, wav, ogg 확장자만 시도해주세요.");
    })
}

// 파일 형식 체크 함수
async function fileValidation(file) {
	
    const fullName = file.name;

    const lastDot = fullName.lastIndexOf('.');
    
    if (lastDot === -1) {
        return false;
    } else {
        let {fileName, ext} = getFileNameWithExt(file);
        ext = ext.toLowerCase();

        if (ext === 'wav' || ext === 'mp3' || ext === 'mp4' || ext === 'ogg' || ext === 'm4a' || ext === 'aac' || ext === 'mpeg' || ext === 'webm' || ext === 'avi') {
            const video = await loadVideo(file)
            const duration = video.duration;
//            if(duration < 1 || duration > 21600) {
//                return false
//            }
        } else {
            return false;
        }
    }

    return true;
}

// 파일 이름 가져오는 함수
function getFileNameWithExt(file) {

    const name = file.name;
    const lastDot = name.lastIndexOf('.');

    // 파일 이름 확장자명 제거
    if(lastDot === -1){
        return {
            fileName: name,
            ext: ""
        }
    } else {
        const fileName = name.substring(0, lastDot);
        const ext = name.substring(lastDot + 1);

        return {
            fileName: fileName,
            ext: ext
        }
    }

}

//파일 메타 데이터 가져오는 함수
const loadVideo = file => new Promise((resolve, reject) => {
    try {
    	//파일 메타 데이터 추출
        let video = document.createElement('video')
        video.preload = 'metadata'

        video.onloadedmetadata = function () {
            resolve(this)
        }

        video.onerror = function () {
            reject("Invalid video. Please select a video file.")
        }

        video.src = window.URL.createObjectURL(file)
    } catch (e) {
        reject(e)
    }
})


//#fn2. 학습일정 등록 - 타임 셀렉트 옵션

document.addEventListener('DOMContentLoaded', function () {

	// 시(hour) 옵션 추가
	const hourSelects = document.querySelectorAll('.hour');
	hourSelects.forEach(hourSelect => {
		for (let i = 0; i < 24; i++) {
			const option = document.createElement("option");
			option.value = i;
			option.text = i < 10 ? `0${i}` : i;
			hourSelect.appendChild(option);
		}
	});

	// 분(minute) 옵션 추가
	const minuteSelects = document.querySelectorAll('.minute');
	minuteSelects.forEach(minuteSelect => {
		for (let i = 0; i < 60; i++) {
			const option = document.createElement("option");
			option.value = i;
			option.text = i < 10 ? `0${i}` : i;
			minuteSelect.appendChild(option);
		}
	});

});

let checkRecording = false;

function uploadFile(fo) {
		
	checkRecording = false;
	
    var data = new FormData();
    data.append('file', fo);
    data.append('type', "recoding");
    if ($('#hiddenLetter').val() == 'true') {
        data.append('letter', $('#meetinglangtype').val());
    }

    let url = contextPath + "/menu21/cont/";
    if (uploadMenu == 1) {
        url += "uploadDiff";
    } else {
        url += "upload";
    }

    const statusType = document.getElementById('statusAdjust');
    const recUploading = document.getElementById('recUploading');

    statusType.classList.remove('recordStart');
    statusType.classList.add('recordUploading');
    recUploading.classList.add('active');

    const recuplodLoader = document.getElementById('recuplodLoader');
    const uploadMmsgbx = document.getElementById('uploadMmsgbx');

    setTimeout(() => {
        uploadMmsgbx.textContent = '실시간 음성 파일 분석 진행 중입니다.';

        fetch(url, {
            method: "POST",
            body: data
        }).then(res => {
            if (res.ok) {
                res.json().then(json => {
                    uploadMmsgbx.textContent = '서버와 통신을 진행중입니다.';
                    if (json.errorMessage) {
                        handleError(fo, json.errorMessage);
                    } else {
                        setTimeout(() => {
                            statusType.classList.remove('recordUploading');
                            statusType.classList.add('recordSaveEnd');
                            recuplodLoader.style.display = 'none';
                            uploadMmsgbx.textContent = '서버에 저장이 완료되었습니다.';                            
                            
                            
                        }, 2000);

                        setTimeout(function () {
                            $("#modalRmCls").click();
                        }, 4000);
                    }
                }).catch(() => {
                    handleError(fo, "음성파일에 음성이없습니다. 음성파일을 확인해주세요.");
                });
            } else {
                if (res.status === 408 || res.status === 504) {
                	uploadMmsgbx.textContent = "업로드를 완료되었습니다. 파일용량이 큰 관계로 5분 이상 시간이 소요됩니다.";
                } else {
                    handleError(fo, "서버 업로드 중 실패 했습니다, 관리자에게 문의해주세요");
                }
            }
        }).catch((err) => {
            handleError(fo, "서버와 연결 끊김, 업로드 유무 불명");
        });
    }, 2000);

    function handleError(file, message) {
        statusType.classList.remove('recordUploading');
        statusType.classList.add('recordSaveEnd');
        recuplodLoader.style.display = 'none';
        
        if(message =="speaker diarize Faile, list index out of range"){
        	uploadMmsgbx.textContent ="음성파일에 음성이없습니다. 음성파일을 확인해주세요."
        }else{
        	uploadMmsgbx.textContent = message;	
        }
        
        // 로컬에 파일 저장
        const url = URL.createObjectURL(file);
        const a = document.createElement('a');
        a.style.display = 'none';
        a.href = url;
        a.download = `recording_${todayFormat()}.ogg`;
        document.body.appendChild(a);
        a.click();
        URL.revokeObjectURL(url);
    }
}

function uploadFile2(fo) {
	
    var data = new FormData()
    data.append('file', fo)
    data.append('type', "file")
    if ( $('#hiddenLetter').val() == 'true' ) {
	    // 변수명으로 lang 을 사용하면 ${#messages.msg() 를 en 으로 바꾼다 
	    data.append('letter', $('#meetinglangtype').val())
    }
    
    let url ;
    
    if( uploadMenu == 1){
    	url = "uploadDiff"
    }else{
    	url = "upload"
    }
    
    
    
	$(".cmx_grid.record_save").addClass("active")
	$(".btn_grid.total").hide()
	
	$(".cmMsgbx").html("음성 파일 업로드 중입니다.<br>잠시만 기다려 주세요.")
	
    setTimeout(function () {
    	$(".cmMsgbx").html("음성 파일 분석 진행 중입니다.")
    	
    fetch(url, {
        method: "POST",
        body: data
    }).then(res => {
        if (res.ok) {
            res.json().then(json => {
            	$(".cmMsgbx").html("서버와 통신을 진행중입니다.")       

            	 if (json.errorMessage) {
                     if (json.errorMessage =="speaker diarize Faile, list index out of range"){
                     	 $(".loader").hide()
                    	 $(".cmMsgbx").html("음성파일에 음성이없습니다. 음성파일을 확인해주세요.")
                     }else{
                     	 $(".loader").hide()
                         $(".cmMsgbx").html("서버 업로드 에러 : " + json.errorMessage)
                     }
                 } else {
      			    setTimeout(function () {
    			    	$(".loader").hide()
    			    	$(".cmMsgbx").html("서버에 저장이 완료되었습니다.")
    			    }, 2000);                     	
                	
                	file="";
                	$('#fileInput').val("")
                	$("#uploadhiddenInput").val("")
                	                
                	if( uploadMenu == 2){
            		    setTimeout(function () {
            		    	$("#uploadFileClose").click()
            		    }, 4000);
                	}else{
            		    setTimeout(function () {
            		    	$("#modalRmCls").click()
            		    }, 4000);                		
                	}
                 }                
            }).catch(()=>{
            	$(".loader").hide()
            	$(".cmMsgbx").html("서버 업로드 중 실패 했습니다, 관리자에게 문의해주세요")
            })
        } else {
            if (res.status === 408 || res.status === 504) {
            	$(".loader").hide()
            	$(".cmMsgbx").html("업로드를 완료되었습니다. 파일용량이큰 관계로  5분이상 시간이 소요됩니다.")
            } else {
            	$(".loader").hide()
            	$(".cmMsgbx").html("서버 업로드 중 실패 했습니다, 관리자에게 문의해주세요")
            }
        }
    }).catch((err) => {
    	$(".loader").hide()
    	$(".cmMsgbx").html("서버와 연결 끊김, 업로드 유무 불명")

    })    	
    	
    }, 2000); // 2000 milliseconds = 2 seconds            
    

}

