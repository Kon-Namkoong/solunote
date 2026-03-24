let subjectLastInputTime = undefined;
let resultLastInputTime = undefined;
let resultLastInputSeq = undefined;
let updateIntervalId = 0;
let audioIntervalId = 0;
let originalText =""
let originalSubject =""	
let findList = {}
let totalMatchCount = 0;
let activeRank = 0;
let curSearchVal = undefined;
let summaryResult=""
		
$(function () {
	
    initTopSpeaker();
    initWordSearchAndReplace()
    initSubject();
    initSpeakerPopup();
    initDownloadPopup();
    initSummary();
    initSummaryChange();
    
    $(document).on('focus', '.btm_grp.editable', function (e) {
    	
        originalText = $(this).text();	  
        
		        
        audio.currentTime = Number($(this).parent().data("start-second"));
        audio.play()	        
        
    });



    $(document).on('blur', '.btm_grp.editable', function () {
    	
        const updatedText = $(this).text();
    	
        if (updatedText !== originalText) {
        	
        	const seq = $(this).parent().data("seq");
        	
        	updateResultsText([seq],[updatedText])
        }	    
        
        if ($(this).parent().hasClass("running")){
        	$(this).parent().removeClass("running")
        }
        
    });    
    
    $(document).on('input', '#arrowTime', function () {
        const arrowTime = $(this).val(); 
        $('.tooltiptext_arrow').each(function (index, element) {

            if ($(element).closest('.forward_arrow').length) {
                $(element).text(`${arrowTime}초 뒤`);
            } else if ($(element).closest('.rewind_arrow').length) {
                $(element).text(`${arrowTime}초 앞`);
            }
        });
    });    
    
    
    $(document).on('click', '.forward_arrow', function () {
        
        const arrowTime = parseFloat($("#arrowTime").val())
        
        if (audio) {
            audio.currentTime = Math.max(audio.currentTime - arrowTime, 0); 
        } else {
            console.error('audio 요소를 찾을 수 없습니다.');
        }
        
    });
    
    $(document).on('click', '.rewind_arrow', function () {
        
        const arrowTime = parseFloat($("#arrowTime").val())
        
        
        if (audio) {
            audio.currentTime = Math.max(audio.currentTime + arrowTime, 0); 
        } else {
            console.error('audio 요소를 찾을 수 없습니다.');
        }
        
    });               
    
    
})	
	
function updateLastModified(val){
    $("#lastModifiedText").text(val);
}
function updateSubject(val){  
    fetch('subject?seq='+$("#queryParamsSeq").val(), {
        method: "POST",
        body: val
    }).then(res=>{
        if(res.ok) {
            subject = val
            res.text().then(text=>{
                updateLastModified(text);
            })
        }
    })
}
function updateResultsText(resultSeqArr, updateResultTextArr){
    
    const obj = {}
    
    for (let i = 0; i < resultSeqArr.length; i++) {
        obj[resultSeqArr[i]] = updateResultTextArr[i]
    }    
        
    const queryParam = "meetSeq="+$("#queryParamsSeq").val();
    fetch('text?'+encodeURI(queryParam), {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(obj)
    }).then(res=>{
        if(res.ok) {
            res.text().then(text=>{
                updateLastModified(text);
            })
        }
    })


}

function closeViewPopup() {
    searchOrReplacePopupHide();
    $("#download-popup").removeClass("show");
}

function stopAudioInterval(audio) {
	
				
	audio.pause();
	
	
	if ( updateIntervalId ) {
		clearInterval(updateIntervalId);
		updateIntervalId = 0;
	}
	
	if ( audioIntervalId ) {
		clearInterval(audioIntervalId);
		audioIntervalId = 0;
	}
}


function startAudioInterval(audio) {
    // 먼저 기존에 실행 중인 모든 interval을 중지합니다.
    stopAudioInterval(audio);

    // 오디오 요소를 가져옵니다.


    // 오디오의 현재 시간에 따라 data-row의 'running' 클래스를 업데이트합니다.
    audioIntervalId = setInterval(() => {
        if (!audio.paused) {
            // 현재 'running' 클래스를 가진 data-row를 가져옵니다.
            const currentRunningEl = $(".chat_detail.running");
            
            if (currentRunningEl.length === 0) {
                // 'running' 클래스가 없는 경우, 첫 번째 data-row에 'running' 클래스를 초기화합니다.
                initFirstRunningClass(audio);
                
            } else if (currentRunningEl.length === 1) {
                // 'running' 클래스를 가진 data-row가 하나인 경우
                const currentRunningStartTime = Number(currentRunningEl.data("start-second"));
                const currentRunningEndTime = Number(currentRunningEl.data("end-second"));

                // 현재 오디오 시간에 따라 'running' 클래스를 다음 또는 이전 data-row로 이동합니다.
                if (currentRunningStartTime > audio.currentTime && currentRunningEl.prev().is(".chat_detail")) {
                    changeRunning(audio, currentRunningEl.prev(), currentRunningStartTime, currentRunningEndTime);
                } else if (currentRunningEndTime < audio.currentTime && currentRunningEl.next().is(".chat_detail")) {
                    changeRunning(audio, currentRunningEl.next(), currentRunningStartTime, currentRunningEndTime);
                } else {
                    // 'running' 클래스가 이동하지 않는 경우
                }
            } else {
                // 'running' 클래스를 가진 data-row가 여러 개 있는 경우, 'running' 클래스를 제거하고 초기화합니다.
                $(".chat_detail.running").removeClass("running");
                initFirstRunningClass(audio);
            }
        }
    }, 100); // 0.1초마다 실행
}

function initFirstRunningClass(audio){
    let done = false;
    const tempInterval = setInterval(()=>{
        $(".chat_detail").each(function (idx, el) {
            if(done || $(".chat_detail.running").length > 0) return false;
            const runningStartTime = Number($(this).data("start-second"));
            const runningEndTime = Number($(this).data("end-second"));

            if(runningStartTime <= audio.currentTime && runningEndTime >= audio.currentTime && !done) {

                $(this).addClass("running");
                done = true;
                
                const contentWrapperEl = $('#detailList'); 
                const targetEl = $(".chat_detail.running")
                
	            if (targetEl.length) {
	                const targetOffsetTop = targetEl.offset().top; 
	                const containerOffsetTop = contentWrapperEl.offset().top;
	                const containerScrollTop = contentWrapperEl.scrollTop();
	
	                const scrollToPosition = targetEl.offset().top - contentWrapperEl.offset().top + contentWrapperEl.scrollTop() - 100
	                	                
	                contentWrapperEl.animate({
	                    scrollTop: scrollToPosition
	                }, 100);
	            }
            }
        })
        if(done) clearInterval(tempInterval);
    }, 10)
}


function changeRunning(audio, jqueryEl, startTimeSec, endTimeSec) {
    if(startTimeSec > audio.currentTime) {

        changeRunning(jqueryEl.prev(), startTimeSec, endTimeSec);
    
    } else if(endTimeSec < audio.currentTime) {

    	changeRunning(jqueryEl.next(), startTimeSec, endTimeSec);
    
    } else {
        $(".chat_detail.running").removeClass("running");
    }
}

function initDownloadPopup(){ 
	
    $(document).on("click", ".search .download_btn", function () {
    	$("#download-popup").show()
    }) 
	
    $(document).on("click", ".download_popup .popuup_wrap .popup_tit .close_btn", function () {
    	$("#fileCheck").removeClass("active")
    	$("#summaryCheck").removeClass("active")
    	$("#audioCheck").removeClass("active")
    	
    	$("#download-popup").hide()
	
    }) 	

    
    $(document).on("click", "#download-btn", function () {
        const $this = $(this);
   	    	
        let filename =""
        let fileType  = ""
        let kind = ""	
        	
        let activeCount = $(".check-box.active").length;

        // 체크된 항목이 2개 이상일 경우 경고
        if (activeCount > 1) {
        	alert("다운받으실 종류는 1개만 선택가능합니다.")
        	return false
        } else if (activeCount === 0) {
        	alert("다운받으실 종류를 선택해주세요.")
        	return false
        } else {
        	if($("#fileCheck").hasClass("active")){
        		fileType = $("#fileType").val()
        		kind = 1
     		
        	}

        	if($("#summaryCheck").hasClass("active")){
        		fileType = $("#summaryDownloadType").val() 		
        		kind = 2
        		
        		if ( $("#hasSummary").val() == 0 ) {
        			
        			alert("다운 받을 요약이없습니다, 먼저 요약을 진행해주세요.")
        		    return false;
        		}        		
        		
        	}        
            
        	
        	if($("#audioCheck").hasClass("active")){
        		kind = 3

        	}
        	    	
            let queryParam = "seq="+$("#queryParamsSeq").val()+"&type="+fileType+"&kind="+kind
                    
            
            fetch('view/download?'+encodeURI(queryParam), {
                method: "GET"
            })
                .then( res => {
                    if(res.ok) {
                    	const orig = res.headers.get('filename_base64');

                    	if ( orig ) {
                    		filename = Base64.decode(orig);
                    	} else {
                    		if(kind == 1){
                        		filename = Date.now() + "_회의록." + fileType;                     			
                    		}else if (kind == 2){
                    			filename = Date.now() + "_요약." + fileType;
                    		}else if (kind == 3) {
                                filename = Date.now() + "_음성파일.ogg";  
                            }
               		
                    	}
                        return res.blob();
                    }
                    return Promise.reject(res)
                })
                .then( blob => {
                    var url = window.URL.createObjectURL(blob);
                    var a = document.createElement('a');
                    a.href = url;
                    a.download = filename;
                    document.body.appendChild(a); // append the element to the dom
                    a.click();
                    a.remove(); // afterwards, remove the element
                }).catch(()=>{
                alert("다운로드 중 에러 발생");
            }).finally(()=>{
                $this.removeClass("wait");
            }) 
        }        	
    
    }) 	    
    
	    
    	
}
    	



function initSpeakerPopup(){
	$(document).on("scroll", ".common_content", function () {
        speakerPopupHide();
    })
    
    $(document).on("click", "#speaker-change-popup .radio--btn", function () {
        const $this = $(this);
        if($this.hasClass("active")) {

        } else {
            $("#speaker-change-popup .radio--btn.active").removeClass("active");
            $this.addClass("active");
        }
    })

    $(document).on("click", "#speaker-change-popup", function (e) {
        e.stopPropagation()
    })

    $(document).on("click", ".chat_detail .top_grp .name_grid", function (e) {

        e.stopPropagation();
        
        const $this = $(this).closest(".chat_detail");
        const seq = $this.data("seq");
        const speaker = $this.data("speaker");

        let topPosition = e.pageY + 10; // 기본적으로 클릭 위치에서 10px 아래로 설정
        let leftPosition = 290; // 고정된 좌측 위치

        const windowHeight = $(window).height();
        const popupHeight = $('.attend_popup').outerHeight();
        const windowWidth = $(window).width();
        const popupWidth = $('.attend_popup').outerWidth();

        if (topPosition + popupHeight > windowHeight) {
            topPosition = windowHeight - popupHeight - 10; // 10px 여백을 두고 조정
        }

        if (topPosition < 0) {
            topPosition = 10; // 상단에서 10px 여백
        }

        if (leftPosition + popupWidth > windowWidth) {
            leftPosition = windowWidth - popupWidth - 10; // 10px 여백을 두고 조정
        }

        $("#speaker-change-popup li").removeClass("active");
        $("#speaker-change-popup li[speaker='" + speaker + "']").addClass("active");

        $(".attend_popup").data("seq", seq);
        $("#new-speaker-input").val('');
        $(".attend_popup").css({ "left": leftPosition, "top": topPosition });
        $('.attend_popup').show();  	
    	
    	
    })

    $(document).on("click", ".attend_popup .popuup_wrap .popup_tit .close_btn", function () {
	
    	speakerPopupHide();
    	
    })
    
    $(document).on("click", "#new-speaker-input-img", function () {
	
    	$("#new-speaker-input").val('');
    	
    })    
    
    $(document).on("focus", "#new-speaker-input", function () {
    	$("#speaker-change-popup li").removeClass("active")
        $("#new-speaker-input").addClass("active");
    })
    
    $(document).on("click", "#speaker-change-popup .speaker-change-list", function () {
    	$("#new-speaker-input").removeClass("active");
        $("#speaker-change-popup .speaker-change-list.active").removeClass("active")
        $(this).addClass("active");
    })

    $(document).on("click", "#speakerChange_btn", function () {
        let toSpeaker = undefined;
        const selectSpeaker = $("#speaker-change-popup .speaker-change-list.active");
        if(selectSpeaker.length > 0) {
            toSpeaker = selectSpeaker.attr("speaker");
        } else {
            toSpeaker = $("#new-speaker-input").val();
        }
        const currentSeq = $(".attend_popup").data("seq");

        if($("#radio1").is(':checked')){
            const currentDataRow = $(".chat_detail[data-seq='"+currentSeq+"']")
            const speakerValue = currentDataRow.data("speaker");
            
        	speakerChange([currentSeq], toSpeaker ,speakerValue)
        }else if ($("#radio2").is(':checked')){        	        	
            const currentDataRow = $(".chat_detail[data-seq='"+currentSeq+"']")
            const speakerValue = currentDataRow.data("speaker");
            
            let nextDataRowNode = currentDataRow.next();
            const updateSeqArr = [currentSeq]
            while(nextDataRowNode.length > 0) {
                if (nextDataRowNode.data("speaker") === currentDataRow.data("speaker")) {
                    updateSeqArr.push(nextDataRowNode.data("seq"))
                }
                nextDataRowNode = nextDataRowNode.next();
            }
            speakerChange(updateSeqArr, toSpeaker ,speakerValue)        	
        	
        }else if ($("#radio3").is(':checked')){
            const currentDataRow = $(".chat_detail[data-seq='"+currentSeq+"']")
            const speakerValue = currentDataRow.data("speaker");
            
            let nextDataRowNode = $(".chat_detail:eq(0)")
            const updateSeqArr = []
            while(nextDataRowNode.length > 0) {
                if (nextDataRowNode.data("speaker") === currentDataRow.data("speaker")) {
                    updateSeqArr.push(nextDataRowNode.data("seq"))
                }
                nextDataRowNode = nextDataRowNode.next();
            }
            speakerChange(updateSeqArr, toSpeaker ,speakerValue)
            
            
        }
        
    })
}

function deleteSpeaker(speakerName){

    let queryParam = "";
    queryParam += "&meetSeq="+$("#queryParamsSeq").val();

    fetch('speaker_delete?'+encodeURI(queryParam), {
        method: "POST",
        body: speakerName
    }).then(res=>{
        if(res.ok) {
            res.text().then(text=>{

                if(text.length > 0) {
                    $(".person_grid .box .person[speaker='" + speakerName + "']").remove();
                	$("#speaker-change-popup li[speaker='"+speakerName+"']").remove()
                	$(".person_grid .box .person[speaker='" + speakerName + "']").remove();
                    updateLastModified(text);
                }

            })
        } else {
            return Promise.reject(res)
        }
    }).catch(()=>{
        alert("참여자 삭제 실패")
    })  	


}


function speakerPopupHide(){
    $(".attend_popup").hide();
}

function speakerChange(meetResultSeqArr, toSpeaker,speakerValue){
    let queryParam = "";
    for(var i = 0; i< meetResultSeqArr.length; i++){
        queryParam += "seq[]="+meetResultSeqArr[i]
        if (i != (meetResultSeqArr.length-1)) {
            queryParam += "&";
        }
    }
    
    queryParam += "&meetSeq="+$("#queryParamsSeq").val();
    fetch('speaker_change?'+encodeURI(queryParam), {
        method: "POST",
        body: toSpeaker
    }).then(res=>{
        if(res.ok) {
            loadMeetResult()
            speakerPopupHide()
            res.text().then(text=>{
                var splitText = text.split("/");
                if(splitText[0] === '1') {
                    appendSpeaker(toSpeaker);
                }
                updateLastModified(splitText[1]);
            })            
            	
            deleteSpeaker(speakerValue)	

        } else {
            return Promise.reject(res)
        }
    }).catch(()=>{
        alert("변경 실패")
    })
}


function loadMeetResult(){
    fetch('loadMeetResult?seq='+$("#queryParamsSeq").val(), {
        method: "GET"
    }).then(res=>{
        if(res.ok) res.json().then(arr=>{
        	
            let newHtml = "";
            
            let name = [];
            let result = [];

            for (let i = 0; i < arr.length; i++) {

                const meetObj = arr[i];
                
            	name.push(meetObj.meetingSpeaker.name)
            	result = [...new Set(name)];
            	
            }


            for (let i = 0; i < arr.length; i++) {
            	const meetObj = arr[i];
                	
                newHtml += "<li class='chat_detail' data-start-second='" + meetObj.start + "' data-end-second='" + meetObj.end + "' data-speaker='" + meetObj.meetingSpeaker.name + "' data-seq='" + meetObj.seq + "'>";
                newHtml += "<div class='top_grp'>";
                newHtml += "<div class='name_grid'>";
                newHtml += "<i class='i_color_" + meetObj.color + "'>" + meetObj.meetingSpeaker.name[0] + "</i>";  // 이름의 첫 글자
                newHtml += "<span class='name'>" + meetObj.meetingSpeaker.name + "</span>";
                newHtml += "</div>";
                newHtml += "<span class='time'>" + meetObj.startTimeFormat + "</span>";
                newHtml += "</div>";
                newHtml += "<div class='btm_grp editable' contenteditable='true' spellcheck='false'>" + meetObj.text + "</div>";
                newHtml += "</li>";                	
                	

            }
            $("#detailList").children().remove();
            $("#detailList").append(newHtml);
            
        })
        else {
            return Promise.reject(res);
        }
    }).catch(err=>{
        alert("채팅 로드 실패")
    })
    
}

function appendSpeaker(val){
    var newHtml = "";

    if($(".person_grid .box .person[speaker='"+val+"']").length === 0) {
    	newHtml += "<li class='person' speaker='" + val + "'>";
    	newHtml += "<span>" + val + "</span>";
    	newHtml += "<button><img src='/SGSAS/images/name_delete.svg' alt=''></button>";
    	newHtml += "</li>";

    	
    	
        $(".person_grid .box .person").last().after(newHtml)
    }

    newHtml = ""
    if($("#speaker-change-popup .speaker-change-list[speaker='"+val+"']").length === 0){
    	newHtml += "<li class='speaker-change-list' speaker='" + val + "'>";
    	newHtml += val; 
    	newHtml += "</li>";

        $("#speaker-change-popup .speaker-change-list").last().after(newHtml);
    }
}    


function initWordSearchAndReplace(){
	
    $(document).on("click", ".search .search_btn", function () {
    	$("#search-word-popup").show()
    	$("#search-word-popup").css("z-index", 1000);
    })    

    $(document).on("click", ".search_popup .popuup_wrap .popup_tit .close_btn", function () {
    	searchOrReplacePopupHide()
    }) 	
	

    $(document).on("click", "#search-word-btn", function () {
    	
        const val = $("#find-search").val().trim();
        if (val.length > 0) {
            search(val)
        } else {
            searchResultReset();
            alert('단어를 입력해주세요.')
        }
        
    })     
    
    $(document).on("keypress", "#find-search", function () {

        if ( event.keyCode == 13 || event.which == 13 ) {
            $("#search-word-btn").trigger('click');
        }
        
    })
    
    $(document).on("click", "#up_click", function (e) {

    	e.stopPropagation()
        prev();
        
    })   
    
    $(document).on("click", "#down_click", function (e) {

        e.stopPropagation()
        next();
        
    })  
    

    $(document).on("click", "#changeOne", function () {

        if(replaceValid()){
            const changeWord = $("#change-word").val().trim();

            const realSearchTextEl = $(".search-text.focus");

            const realDataRow = realSearchTextEl.closest(".chat_detail");
            
            realDataRow.find(".search-text.focus").text(changeWord);
            
            const text = realDataRow.find(".btm_grp.editable").text();
            const seq = realDataRow.data('seq')

            updateResultsText([seq], [text])
        }
        
    })    
    
    
    $(document).on("click", "#changeAll", function () {

        if(replaceValid()){
            const changeWord = $("#change-word").val().trim();
            const reqSeqArr = []
            const reqResultTextArr = [];
            
            for (let findListKey in findList) {
                const DataRow = $(".chat_detail[data-seq='"+findListKey+"']");
                const TextArea = DataRow.find(".btm_grp.editable");
                const searchText = DataRow.find(".search-text");
                searchText.text(changeWord);
                reqSeqArr.push(findListKey)
                reqResultTextArr.push(TextArea.text())
                
                
            }                 
            
            updateResultsText(reqSeqArr, reqResultTextArr)
        }
        
    })     
    
    
    
    
           
    function replaceValid(){
        const replaceVal = $("#change-word").val().trim();
        const findVal =   $("#find-search").val().trim();
        if(replaceVal === '') {
            alert("변경 내용을 입력해주세요.");
            return false;
        } else {
        	if(findVal === ""){
        		alert("검색 단어를 입력하세요.")
        		return false;
        	}else if(replaceVal ===findVal ){
                alert("검색 단어와 변경 내용이 일치합니다.");
                return false;
        	}else{
                if(activeRank === 0) {
                    alert("검색 결과가 없습니다.");
                    return false;
                }
        	}
        }

        return true;
    }

    function searchFirst(val){
        findList = {}
        totalMatchCount = 0;
        activeRank = 0;
        curSearchVal = undefined;

        const replaceRegex = new RegExp(val, "ig")
        $(".chat_detail .btm_grp").each((idx, el)=>{
            const dataRow = $(el).closest(".chat_detail");
            let newInnerHtml = el.innerText.replace(/\n/g, "<br>");

            const matchCnt = (newInnerHtml.match(replaceRegex) || []).length
            if(matchCnt > 0) {
                totalMatchCount += matchCnt
                findList[dataRow.data("seq")] = matchCnt;
                newInnerHtml = newInnerHtml.replace(replaceRegex, "<span class='search-text'>"+val+"</span>");
            }
            el.innerHTML = newInnerHtml
        })
        if(totalMatchCount > 0){
            activeRank = 1;
            curSearchVal = val;
            $(".search-text:eq(0)").addClass("focus");
            $(".U_and_D .page").text(activeRank+'/'+totalMatchCount);
        }
    }
    
    function next(){
        if(activeRank < totalMatchCount) {
            activeRank++;
        } else {
            activeRank = 1;
        }    
        moveFocus()
    }
    function prev(){
        if(activeRank > 1) {
            activeRank--;
        } else {
            activeRank = totalMatchCount
        }
        moveFocus()
    }    

    function search(val){
        if(val === curSearchVal) {
            next();
        } else {
            searchFirst(val);
        }
    }
    
    function moveFocus() {
        $(".search-text").removeClass("focus");
        $(".search-text:eq("+(activeRank-1)+")").addClass("focus");
        $(".U_and_D .page").text(activeRank + '/' + totalMatchCount);
        
        setTimeout(function() { 
            const contentWrapperEl = $('#detailList');  
            const targetEl = $(".search-text.focus");  
            
            if (targetEl.length) {
                const targetOffsetTop = targetEl.offset().top; 
                const containerOffsetTop = contentWrapperEl.offset().top; 
                const containerScrollTop = contentWrapperEl.scrollTop(); 


                const scrollToPosition = targetEl.offset().top - contentWrapperEl.offset().top + contentWrapperEl.scrollTop() - 100
                

                contentWrapperEl.animate({
                    scrollTop: scrollToPosition
                }, 100);  
            }

        }, 10);
    }  
    
    function searchOrReplacePopupHide(){
        $("#search-word-popup").hide()
        $(".U_and_D .page").text('');
        $("#find-search").val('');
        $("#change-word").text('');
        searchResultReset();
    }

    function searchResultReset(){
        findList = {}
        totalMatchCount = 0
        activeRank = 0
        curSearchVal = undefined;
        $(".search-text").removeClass("search-text");
    }    

}


function initSubject(){
	
    $(document).on('focus', '#deatilSubject', function () {
    	
        originalSubject = $(this).text();
        
    });

    $(document).on('blur', '#deatilSubject', function () {
    	
        const updatedSubject = $(this).text();
    	
        if (updatedSubject !== originalSubject) {
        	        	
            updateSubject(updatedSubject);
            
            const seq = $("#queryParamsSeq").val()
            
            const $targetRow = $("#tableRow").find(`tr[data-seq='${seq}']`);

            if ($targetRow.length) {

            	const $targetLink = $targetRow.find("td").eq(3).find("a");

                if ($targetLink.length) {
                    $targetLink.text(updatedSubject);
                } else {
                    console.warn("네 번째 <a> 태그를 찾을 수 없습니다.");
                }
            } else {
                console.warn(`data-seq='${seq}' 값을 가진 <tr>을 찾을 수 없습니다.`);
            }            
        }	    	
        
    });

}

function initTopSpeaker(){
    $(document).on("click", ".person_grid .box .person button", function () {

    	const $this = $(this).closest(".person");
    	const speakerName = $this.find("span").text().trim();
    	
        let queryParam = "";
        queryParam += "&meetSeq="+$("#queryParamsSeq").val();
        
        
        fetch('speaker_delete?'+encodeURI(queryParam), {
            method: "POST",
            body: speakerName
        }).then(res=>{
            if(res.ok) {
                res.text().then(text=>{
                    if(text.length > 0) {
                        $this.remove();
                        $("#speaker-change-popup li[speaker='"+speakerName+"']").remove()
                        updateLastModified(text);
                    } else {
                        alert("사용 중인 참석자명은 삭제할 수 없습니다.");
                    }
                })
            } else {
                return Promise.reject(res)
            }
        }).catch(()=>{
            alert("참여자 삭제 실패")
        }).finally(()=>{
            $this.removeClass("loading-wait");
        })
    	
    })

}

function initSummary(){
    $(document).on("click", ".summation_btn", function () {
    	
    	const hse = $("#hiddenSummaryEnable").val();
    	const hl = $("#hiddenLang").val();
    	const checkSummary = $("#checkSummlen").val();    	
    	if ( hse == false ) {
    		return;
    	}
    	
    	if ( hl != 'ko') {
    	 	alert("한국어 회의록만 요약할 수 있습니다.")
    		return;
    	}

    	if(checkSummary == "X"){
    		alert("요약하려는 길이가 너무 짧습니다. 500자 이상부터 요약이 가능합니다.")
    		return false;
    	}    	

    	const summaryType = $("#summaryType").val();
    	const summaryStatus = $("#queryParamsSummaryStatus").val()
    	const summaryStatus2 = $("#queryParamsSummaryStatus2").val()
    	const summaryStatus3 = $("#queryParamsSummaryStatus3").val()
    	
    	
	    if (summaryType == 1 && (summaryStatus == "PENDING" ||  summaryStatus == "STARTED")  ){
	    	alert("일반요약을 진행중입니다, 잠시만 기달려주세요.");
	    	return;
	    }
    	
	    if (summaryType == 2 && (summaryStatus2 == "PENDING" ||  summaryStatus2 == "STARTED")  ){
	    	alert("발표요약을 진행중입니다, 잠시만 기달려주세요.");
	    	return;
	    }
	    
	    if (summaryType == 3 && summaryStatus3 == "PENDING" ||  summaryStatus3 == "STARTED"  ){
	    	alert("시간별 요약을 진행중입니다, 잠시만 기달려주세요.");
	    	return;
	    }	    
    	  	    	
    	const queryParam = "seq="+$("#queryParamsSeq").val()+"&summaryType="+summaryType;
    	       	    	
    	$("#summaryBefore").hide()

    	$("#summaryIng1").show();
    	
	    let seq = $("#queryParamsSeq").val()
	    	    	    
	    fetch("view/sendSummary?" + encodeURI(queryParam), {
	        method: 'POST'
	    })
	    .then(response => response.text()) 
	    .then(summId => {
	    		    	
	    	if(summaryType == 1){
	    		$("#queryParamsSummaryId").val(summId);	
	    		$("#queryParamsSummaryStatus").val("PENDING");	
	    	}else if(summaryType == 2) {
	    		$("#queryParamsSummaryId2").val(summId);	
	    		$("#queryParamsSummaryStatus2").val("PENDING");	
	    	}else{
	    		$("#queryParamsSummaryId3").val(summId);	
	    		$("#queryParamsSummaryStatus3").val("PENDING");	
	    	}
	    	getSummaryInterval(seq,summId,summaryType)
	    })
	    .catch(error => {
	        console.error("요약 등록 에러 발생:", error);
	        alert("요약 등록에 실패 했습니다. 관리자에게 문의 바랍니다.");
	    });
 	
    	
    })
    
    
    $(document).on("change", "#summaryType", function () {
    	clearInterval(intervalId);
    	    	
        const summaryType = $("#summaryType").val();

        $("#summary1, #summary2, #summary3, #summaryBefore, #summaryIng1, #summaryIng2").hide();

        const seq = $("#queryParamsSeq").val();
        const summaryMap = {
            1: { id: $("#queryParamsSummaryId").val(), status: $("#queryParamsSummaryStatus").val() },
            2: { id: $("#queryParamsSummaryId2").val(), status: $("#queryParamsSummaryStatus2").val() },
            3: { id: $("#queryParamsSummaryId3").val(), status: $("#queryParamsSummaryStatus3").val() }
        };
        
        const summary = summaryMap[summaryType];
                
                
        if (summary.status) {
            const show = updateSummaryUI(summary.status, summaryType);

            if (show == 0) {
            	            
                getSummaryInterval(seq, summary.id, summaryType);
            }
        } else {
            $("#summaryBefore").show();
        }
        
    })
      
}


function initSummaryChange(){
	
	$(document).on('click', '#summary1 ul', function () {
		
	    $(this).find('li').each(function() {
	        const content = $(this).text().trim();

	        if (content === '') {
	            $(this).remove(); 
	        }
	    });
	    let result = [];

	    $('#summary1 ul').each(function() {
	        const $ul = $(this);

	        const heading = $ul.find('h3').text().trim();
	        if (heading) {
	            result.push(" " +heading+":");
	        }

	        $ul.find('li').each(function() {
	            let liText = $(this).text().trim();
	            if (liText) {
	                result.push('- ' + liText);
	            }
	        });
	    });
	   	    
	    summaryResult = result.join('\n').replace(/\n/g, '');
	   	      	    	    	    	    	    
	});	
	
	$(document).on('click', '#summary2 ul', function () {
		
	    $(this).find('li').each(function() {
	        const content = $(this).text().trim();

	        if (content === '') {
	            $(this).remove(); 
	        }
	    });
	    let result = [];

	    $('#summary2 ul').each(function() {
	        const $ul = $(this);

	        const heading = $ul.find('h3').text().trim();
	        if (heading) {
	            result.push(" " +heading+":");
	        }

	        $ul.find('li').each(function() {
	            let liText = $(this).text().trim();
	            if (liText) {
	                result.push('- ' + liText);
	            }
	        });
	    });
	   	    
	    summaryResult = result.join('\n').replace(/\n/g, '');
	   	      	    	    	    	    	    
	});		
	
	
	$(document).on('click', '#summary3 ul', function () {
	    let result = [];  // 배열로 초기화

	    // #summary3 내 모든 ul을 순회하면서 데이터 처리
	    $('#summary3 ul').each(function () {
	        const $block = $(this);  // 현재 ul 요소

	        // 각 항목 추출
	        const time = $block.find('.sum_time').text().trim();
	        const subject = $block.find('.sum_subject').text().trim();
	        const detailedItems = [];

	        $block.find('li').each(function () {
	            const detail = $(this).text().trim();
	            if (detail) {
	                detailedItems.push(detail);
	            }
	        });

	        // 해당 block의 정보를 형식화하여 result 배열에 추가
	        if (time) {
	            result.push(` 시간: ${time}`);
	        }
	        if (subject) {
	            result.push(` 주제: ${subject}`);
	        }
	        if (detailedItems.length > 0) {
	            result.push(` 세부요약:`);
	            detailedItems.forEach(item => {
	                result.push(`- ${item}`);
	            });
	        }
	    });

	    // 배열을 문자열로 결합하고 줄바꿈을 제거
	     summaryResult = result.join('\n').replace(/\n/g, '');
	     
	    
	});	
	


	$(document).on('keydown', '.btm_grp_summary.editable', function(e) {
	    const $li = $(this);
	    const $parent = $li.parent();
	    const liCount = $parent.find('li').length; 

	    if (e.key === 'Backspace') {
	        if (liCount === 1 && $li.text().trim() === '') {
	            e.preventDefault(); 
	        }
	    }
	});
	
	

	$(document).on('blur', '#summary1 ul', function () {

	    $(this).find('li').each(function() {
	        const content = $(this).text().trim();

	        if (content === '') {
	            $(this).remove(); 
	        }
	    });
	    let result = [];

	    $('#summary1 ul').each(function() {
	        const $ul = $(this);

	        let heading = $ul.find('h3').text().trim();
	        
	        if (heading === '향후일정 및 계획') {
	            heading = '향후일정';
	        }	        
	        
	        if (heading) {
	            result.push(" " +heading+":");
	        }

	        $ul.find('li').each(function() {
	            let liText = $(this).text().trim();
	            if (liText) {
	                result.push('- ' + liText);
	            }
	        });
	    });

	    const finalResult = result.join('\n').replace(/\n/g, '');

	    // 변경 사항이 있을 경우 서버 업데이트
	    if (finalResult != summaryResult) {
	    		    	
	        const queryParam = "meetSeq=" + $("#queryParamsSeq").val()+ "&summaryType=1";
	        fetch('summaryUpdate?' + encodeURI(queryParam), {
	            method: "POST",
	            headers: {
	                "Content-Type": "application/json"
	            },
	            body: finalResult
	        }).then(res => {
	            if (res.ok) {
	                res.text().then(text => {
	                    updateLastModified(text);
	                    summaryResult = finalResult;
	                });
	            }
	        });
	    }
	});
	
	$(document).on('blur', '#summary2 ul', function () {
	   
	    $(this).find('li').each(function() {
	        const content = $(this).text().trim();

	        if (content === '') {
	            $(this).remove(); 
	        }
	    });
	    let result = [];

	    $('#summary2 ul').each(function() {
	        const $ul = $(this);

	        let heading = $ul.find('h3').text().trim();
      
	        
	        if (heading) {
	            result.push(" " +heading+":");
	        }

	        $ul.find('li').each(function() {
	            let liText = $(this).text().trim();
	            if (liText) {
	                result.push('- ' + liText);
	            }
	        });
	    });

	    const finalResult = result.join('\n').replace(/\n/g, '');

	    // 변경 사항이 있을 경우 서버 업데이트
	    if (finalResult != summaryResult) {
	        const queryParam = "meetSeq=" + $("#queryParamsSeq").val() + "&summaryType=2";
	        fetch('summaryUpdate?' + encodeURI(queryParam), {
	            method: "POST",
	            headers: {
	                "Content-Type": "application/json"
	            },
	            body: finalResult
	        }).then(res => {
	            if (res.ok) {
	                res.text().then(text => {
	                    updateLastModified(text);
	                    summaryResult = finalResult;
	                });
	            }
	        });
	    }
	});
	
	
	$(document).on('blur', '#summary3 ul', function () {
	    let result = [];  // 배열로 초기화

	    // #summary3 내 모든 ul을 순회하면서 데이터 처리
	    $('#summary3 ul').each(function () {
	        const $block = $(this);  // 현재 ul 요소

	        // 각 항목 추출
	        const time = $block.find('.sum_time').text().trim();
	        	        
	        const subject = $block.find('.sum_subject').text().trim();
	        	        
	        const detailedItems = [];

	        $block.find('li').each(function () {
	            const detail = $(this).text().trim();
	            if (detail) {
	                detailedItems.push(detail);
	            }
	        });

	        // 해당 block의 정보를 형식화하여 result 배열에 추가
	        if (time) {
	            result.push(` 시간: ${time}`);
	        }
	        if (subject) {
	            result.push(` 주제: ${subject}`);
	        }
	        if (detailedItems.length > 0) {
	            result.push(` 세부요약:`);
	            detailedItems.forEach(item => {
	                result.push(`- ${item}`);
	            });
	        }
	    });

	    // 배열을 문자열로 결합하고 줄바꿈을 제거
	    const finalResult = result.join('\n').replace(/\n/g, '');

	    // 변경 사항이 있을 경우 서버 업데이트
	    if (finalResult != summaryResult) {
	    	    		    	
	        const queryParam = "meetSeq=" + $("#queryParamsSeq").val()+ "&summaryType=3";
	        fetch('summaryUpdate?' + encodeURI(queryParam), {
	            method: "POST",
	            headers: {
	                "Content-Type": "application/json"
	            },
	            body: finalResult
	        }).then(res => {
	            if (res.ok) {
	                res.text().then(text => {
	                    updateLastModified(text);
	                    summaryResult = finalResult;
	                });
	            }
	        });
	    }
	});	
	
	
}

var Base64={_keyStr:"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",encode:function(e){var t="";var n,r,i,s,o,u,a;var f=0;e=Base64._utf8_encode(e);while(f<e.length){n=e.charCodeAt(f++);r=e.charCodeAt(f++);i=e.charCodeAt(f++);s=n>>2;o=(n&3)<<4|r>>4;u=(r&15)<<2|i>>6;a=i&63;if(isNaN(r)){u=a=64}else if(isNaN(i)){a=64}t=t+this._keyStr.charAt(s)+this._keyStr.charAt(o)+this._keyStr.charAt(u)+this._keyStr.charAt(a)}return t},decode:function(e){var t="";var n,r,i;var s,o,u,a;var f=0;e=e.replace(/[^A-Za-z0-9\+\/\=]/g,"");while(f<e.length){s=this._keyStr.indexOf(e.charAt(f++));o=this._keyStr.indexOf(e.charAt(f++));u=this._keyStr.indexOf(e.charAt(f++));a=this._keyStr.indexOf(e.charAt(f++));n=s<<2|o>>4;r=(o&15)<<4|u>>2;i=(u&3)<<6|a;t=t+String.fromCharCode(n);if(u!=64){t=t+String.fromCharCode(r)}if(a!=64){t=t+String.fromCharCode(i)}}t=Base64._utf8_decode(t);return t},_utf8_encode:function(e){e=e.replace(/\r\n/g,"\n");var t="";for(var n=0;n<e.length;n++){var r=e.charCodeAt(n);if(r<128){t+=String.fromCharCode(r)}else if(r>127&&r<2048){t+=String.fromCharCode(r>>6|192);t+=String.fromCharCode(r&63|128)}else{t+=String.fromCharCode(r>>12|224);t+=String.fromCharCode(r>>6&63|128);t+=String.fromCharCode(r&63|128)}}return t},_utf8_decode:function(e){var t="";var n=0;var r=c1=c2=0;while(n<e.length){r=e.charCodeAt(n);if(r<128){t+=String.fromCharCode(r);n++}else if(r>191&&r<224){c2=e.charCodeAt(n+1);t+=String.fromCharCode((r&31)<<6|c2&63);n+=2}else{c2=e.charCodeAt(n+1);c3=e.charCodeAt(n+2);t+=String.fromCharCode((r&15)<<12|(c2&63)<<6|c3&63);n+=3}}return t}}