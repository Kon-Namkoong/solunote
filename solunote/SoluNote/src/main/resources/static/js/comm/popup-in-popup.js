function selectDivideCombineShow($this, event){
	
	if ( isReliability() == true ) {
		return;
	}
	
//  var sttTxt = $(this).closest('td.stt_txt');
    var toolPopup = $('.tool_popup');
    var hideTimeout;

    toolPopup.css({
        'display': 'flex',
        'position': 'absolute',
        'left': event.pageX,
        'top': (event.pageY - 20) + 'px'
    });

    var hidePopup = function () {
        hideTimeout = setTimeout(function () {
            toolPopup.css('display', 'none');
        }, 100);
    };

//    sttTxt.on('mouseleave', hidePopup);
//
//    toolPopup.on('mouseenter', function () {
//        if (hideTimeout) clearTimeout(hideTimeout);
//    });

    toolPopup.on('mouseleave', hidePopup);
	
//	const elem = $this[0]
//    const boundingRect = elem.getBoundingClientRect();
//	
//    $(".popup-in-popup").css({"left": boundingRect.x + 100, "top":boundingRect.y-200})	
//    $(".popup-in-popup").show();
    
//    viewPct = audioPos(event, $this.text());
}

function cloneRow() {
	
  const viewRow = $("#div_content_list tbody tr[data-seq='" + viewSeq + "']")

  const trainingTextElement = viewRow.find(".trainingData_text").get(0); // .trainingData_text 요소 가져오기
  const selection = window.getSelection();
  let caretPos = null;

  if (selection.rangeCount > 0) {
      const range = selection.getRangeAt(0);
      const selectedNode = range.commonAncestorContainer; 
      

      if (trainingTextElement.contains(selectedNode)) {

          const text = trainingTextElement.textContent;
          const caretOffsetInText = range.startOffset; 

          caretPos = caretOffsetInText;
      } else {
          alert("커서가 텍스트 영역 안에 있지 않습니다.");
          return;
      }
  } else {
      alert("텍스트를 선택하거나, 커서가 텍스트에 위치해야 합니다.");
      return;
  }

  const sttTxt = $(this).closest('td.stt_txt');
  const toolPopup = $('.tool_popup');
  toolPopup.css('display', 'none');
	
	
	const text = viewRow.find(".trainingData_text").text();
	
	// 텍스트 분리: 커서 위치를 기준으로 나눔
//    const text = viewRow.find(".trainingData_text").text();
	const text1 = text.substring(0, caretPos); // 커서 이전 부분
	const text2 = text.substring(caretPos);    // 커서 이후 부분
	const viewPct = text1.length / text.length;
	
    // viewRow를 다시 찾을 필요 없이 전달받은 viewRow를 그대로 사용
    const start1 = Number(viewRow.attr("data-start-second"));
    const end2 = Number(viewRow.attr("data-end-second"));
    const gap = end2 - start1;
    const diff = gap * viewPct;
    let start2 = start1 + diff;
    start2 = start2.toFixed(2);

    if (text1.length < 1 || text2.length < 1) {
        alert("문장을 분리하려면 커서를 텍스트 내 적절한 위치에 놓고 시도하세요.");
        return;
    }

	const prevEnd = findPrevEnd(viewRow);
	const nextStart = findNextStart(viewRow);

    console.log("viewRow:", viewRow);
    console.log("start2:", start2);
    console.log("text1:", text1);
    console.log("text2:", text2);
    console.log("end2:", end2);
    console.log("prevEnd:", prevEnd);
    console.log("nextStart:", nextStart);

    // 데이터 저장 함수 호출
    const result = saveSplit(viewRow, start2, text1, text2, end2, prevEnd, nextStart);
    
}

function saveSplit(viewRow, start2, text1, text2, end2, prevEnd, nextStart) {
   const {activeMenu} = queryParams
   
   const viewSeq = viewRow.data("seq"); 
   
   const data = {
		   "seq": viewSeq,
		   "start2": start2,
		   "text1": text1,
		   "text2": text2,
		   "soundSeq": $("#hiddenSeq").val()
   };
    fetch("split", {
        method: "POST",
        headers: {
             "Content-Type": "application/json",
          },
        body: JSON.stringify(data),
		redirect: "follow" // manual, *follow, error
    }).then(res=>{
    	if ( res.redirected == true ) {
    		window.location.href = res.url;
    	} else if(res.ok) {
    		return res.text();
    	} else {
    		return Promise.reject(res);
    	}
    }).then( seq2 =>{
    	
    	splitSuccess(viewRow, start2, text1, text2, end2, seq2, prevEnd, nextStart);

    }).catch(err=>{
        console.log(err)
        alert("페이지 갱신 실패")
    })
}

function splitSuccess(viewRow, start2, text1, text2, end2, seq2, prevEnd, nextStart) {
	
	const start1 = viewRow.find(".start").text();
	const end1 =  viewRow.find(".end").text();
	const seq1 = viewRow.attr("data-seq");
	
	const timeLength1 = (start2-viewRow.find(".start").text()).toFixed(2)
	const timeLength2 = (end2-start2).toFixed(2)
	
//	viewRow.data("end-second", start2);        // 이것을 사용하면 반영 안됨
	viewRow.attr("data-end-second", start2);
	viewRow.find(".trainingData_text").text(text1);
	viewRow.find(".transDivDB").text(text1);
	viewRow.find(".transDivDB").css("display","none")
	viewRow.find(".end").text(start2);
	viewRow.find(".timeLength").text(timeLength1);
		
	let url1 = viewRow.find("source").attr("src");
	let url2 = url1;
	
	console.log("url1 : ", url1);
	
	url1 = url1.replace(/end=[^&]*/, "end=" + start2);

	viewRow.find("source").attr("src", url1);
	console.log("url1 : ", url1);
	
	viewRow.find("audio")[0].load();
	viewRow.removeClass("combine")	
	viewRow.removeClass("warning")
	viewRow.addClass("split")
	if (timeLength1 > 30) { viewRow.addClass("warning") }
	let waveDiv1 = viewRow.find("div.wave");
	
	// change new row -----------------------
	const newRow = viewRow.clone(); //clone it
	
	newRow.find("audio").attr("id", "sound-" + seq2);
	newRow.attr("data-seq", seq2);
	newRow.removeClass("running");
	newRow.removeClass("warning");
	newRow.attr("data-start-second", start2);
	newRow.attr("data-end-second", end2);
	newRow.find(".trainingData_text").text(text2);
	newRow.find(".transDivDB").text(text2);
	newRow.find(".transDivDB").css("display","none")	
	newRow.find(".start").text(start2);
	newRow.find(".end").text(end2);
	newRow.find(".timeLength").text((end2-start2).toFixed(2));	
	newRow.addClass("split")
	if (timeLength2 > 30) { newRow.addClass("warning") }
	
	
	url2 = url2.replace(/start=[^&]*/, "start=" + start2);
	url2 = url2.replace(/end=[^&]*/, "end=" + end2);
	newRow.find("source").attr("src", url2);
	
	if ( waveDiv1 ) {
		newRow.find("div.wave").attr("id", "img-" + seq2);
	}
	
	viewRow.after(newRow); //add in the new row at the end
	
	newRow.find("audio")[0].load();
	
	$("#div_content_list tbody tr").each( function(idx, ele) {
		$(ele).find("td:first").text(idx + 1);
	})
	
	initTrans();
	
	if ( waveDiv1 ) {
		let queryParam = "hiddenSeq=" + $("#hiddenSeq").val();
		queryParam += "&seq=" + seq1 + "," + seq2;
		queryParam += "&start=" + start1 + "," + start2;
		queryParam += "&end=" + start2 + "," + end2;
		queryParam += "&prevEnd=" + prevEnd;
		queryParam += "&nextStart=" + nextStart;
		queryParam += "&category=" +  $("#hiddenCategory").val();
		queryParam += "&channelChar=" +  viewRow.find(".channel").text();
		
		appendWaveSpectrum("replaceWaveSpectrum?" + encodeURI(queryParam));
	}
}


function cloneRowToCombine() {
	
	if (viewSeq2 =="" || viewSeq2 == undefined ){
		alert("마지막 행은 합칠수가 없습니다.")
		return false
	}
	
    const viewRow = $("#div_content_list tbody tr[data-seq='" + viewSeq + "']")
    const nextRow = viewRow.next("tr");
		
	const start = Number(viewRow.attr("data-start-second"));	
	const end = Number(nextRow.attr("data-end-second"));		
			
	const textFirst = viewRow.find(".trainingData_text").text();
	const textSecond = nextRow.find(".trainingData_text").text();
	
	let textCombine =textFirst + " " + textSecond
	
	saveCombine(viewRow,nextRow ,start,end,textCombine);
}


function saveCombine(viewRow,nextRow ,start,end,textCombine) {
   const {activeMenu} = queryParams
   
   const viewSeq = viewRow.data("seq"); 
   const viewSeq2 = nextRow.data("seq"); 
   	   
   const data = {
		   "seq": viewSeq,
		   "seq2": viewSeq2,
		   "end": end,
		   "text": textCombine,
		   "soundSeq": $("#hiddenSeq").val()
   };
    fetch("combine", {
        method: "POST",
        headers: {
             "Content-Type": "application/json",
          },
        body: JSON.stringify(data),
		redirect: "follow" // manual, *follow, error
    }).then(res=>{
    	if ( res.redirected == true ) {
    		window.location.href = res.url;
    	} else if(res.ok) {
    		return res.text();
    	} else {
    		return Promise.reject(res);
    	}
    }).then( seq =>{
    	combineSuccess(viewRow,nextRow ,start,end,textCombine)
    }).catch(err=>{
        console.log(err)
        alert("페이지 갱신 실패")
    })
}

function combineSuccess(viewRow,nextRow ,start,end,textCombine) {
	
	let url1 = viewRow.find("source").attr("src");
	
	const  timeLength = (end-viewRow.find(".start").text()).toFixed(2)
	
	//     viewRow.data("end-second", start2); 를 사용하면 반영이 안됨
	viewRow.attr("data-end-second", end);	
	viewRow.find(".trainingData_text").text(textCombine)
	viewRow.find(".transDivDB").text(textCombine);
	viewRow.find(".transDivDB").css("display","none")
	viewRow.find(".end").text(end)
	viewRow.find(".timeLength").text(timeLength)
	
	url1 = url1.replace(/end=[^&]*/, "end=" + end);
	
	viewRow.find("source").attr("src", url1);
	viewRow.find("audio")[0].load();
	viewRow.removeClass("split")
	viewRow.removeClass("warning")
	viewRow.addClass("combine")
	if(timeLength >30 ) { viewRow.addClass("warning")}
		
	nextRow.remove(); //add in the new row at the end

	$(".tbl_detail tbody tr").each( function(idx, ele) {
		$(ele).find("td:first").text(idx + 1);
	})
	
	let waveDiv1 = viewRow.find("div.wave");
	if ( waveDiv1 ) {
		
		const prevEnd = findPrevEnd(viewRow);
		const nextStart = findNextStart(viewRow);
		let queryParam = "hiddenSeq=" + $("#hiddenSeq").val();
		queryParam += "&seq=" + viewRow.attr("data-seq");	
		queryParam += "&start=" + start;
		queryParam += "&end=" + end;
		queryParam += "&prevEnd=" + prevEnd;
		queryParam += "&nextStart=" + nextStart;
		queryParam += "&category=" +  $("#hiddenCategory").val();
		queryParam += "&channelChar=" +  viewRow.find(".channel").text();
		
		appendWaveSpectrum("replaceWaveSpectrum?" + encodeURI(queryParam));
	}
	
}

function findPrevEnd(row) {
	return row.prev().length ? row.prev().attr("data-end-second") : row.find(".channel") == "L" ? $("#lFirstLimit").val() : $("#rFirstLimit").val();
}

function findNextStart(row) {
	return row.next().length ? row.next().attr("data-start-second") : row.find(".channel") == "L" ? $("#lLastLimit").val() : $("#rLastLimit").val();
}

function isReliability() {
	
	const reliability = $('#reliability').val();
	
	 if (reliability != "" && reliability != 0 && reliability != 100){
		 alert("신뢰도가 입력된 상태에서는 실행되지 않습니다.");
		 return true;
	 } else {
		 return false;
	 }
}


