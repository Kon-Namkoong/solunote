function audioPos(e, text) {
	const selection = window.getSelection();
	let range;
	if (selection && selection.rangeCount > 0) {
		range = selection.getRangeAt(0);
	} else {
		return 0;
	}
		
	let node = selection.anchorNode;
    
    range.setEnd(node, node.length);
    const str = range.toString().trim();
    let accum = 1;
    
	setTimeout(	function () {
				selection.removeRange(range);
			},
			100
	);
//    let trail = node.textContent;
//    let accumIdx = node.textContent.length;
	
	let trail = text;
	let accumIdx = trail.length;
    
//    while( node.nextSibling ) {
//    	node = node.nextSibling;
////    	 console.log("2 node.textContent [", node.textContent, "]");
//    	 accum = 2;
//    	 trail += node.textContent;
//    }
        
//    let flag = false;
//    if ( node.parentNode.nodeName.toLowerCase() == 'div' ) {
//    	$.each( node.parentNode.parentNode.childNodes, function(idx, item) {
//    		if ( item.nodeName.toLowerCase() == 'div' ) {
//    			flag = true;
//    			return true;
//    		}
//
//    		if ( flag == true ) {
////	    		console.log("3 item.textContent [", item.textContent, "]")
//	    		accum = 3;
//	    		accumIdx = text.length - item.textContent.length;
//    			trail += item.textContent;
//    		}
//    	});
//    }
    
  
    
//  console.log("text  [", text, "]");
//  console.log("str   [", str, "]");
//  console.log("trail [", trail, "]");
    
    let pct = 0;
    if ( str ) {
    	let idx = trail.indexOf(str);
//    	console.log("idx: ",idx);	
    	const gap = text.length - trail.length;
    	if ( gap > 0 ) {
    		idx += gap;
    	}
    	
    	pct = parseInt( idx * 100 / text.length );
    } else {
//    	console.log("accum [", accum, "]");
    	idx = accum == 1 ? 0 : accumIdx;
    	pct = parseInt( idx * 100 / text.length );
    }
//    console.log("pct : ", pct)
//    console.log("\n")
    return pct;
}

//-------------------
function editorControl(tr, flag) {
	
	if ( tr ) {

		if ( flag ) {
			tr.removeClass("running").addClass("running");
		} else {
			tr.removeClass("running")
		}
	}
	   

}

function audioPlay(audio, pct) {
//	   console.log(pct, ", continual = ", continual, "------------------ play, typeof = ", typeof audio)
	   
	const ele = audio.get(0);
	if ( ele && ele.play && typeof ele.play === 'function' ) {
		const promise = ele.play();
		if (promise !== undefined) {
			promise.then(function() {
				queue.enqueue(audio);
				if ( pct ) {
					const pos = ele.duration * pct * 0.01;
					ele.currentTime = pos.toFixed(2);
				} else {
					ele.currentTime = 0;
				}
//				hideRunning(ele);
		    }).catch(function(error) {
			   console.log("PLAY ERROR : audio id = ", audio.attr("id"), ", error = ", error);
		    });
		}
	} else {
//		console.log("CHECK ME ele = ", ele)
	}
}


function audioPauseAll(currAudio) {
    console.log("Pausing all except:", currAudio);

    let audio;
    while (audio = queue.dequeue()) {
        if (currAudio && currAudio.attr("id") === audio.attr("id")) {
            continue;
        }
        if (!audio.prop("ended")) {
            audio.get(0).pause();  // 먼저 pause 실행
        }
    }

    // 100ms 후 play 실행하여 충돌 방지
    setTimeout(() => {
        if (currAudio) {
            currAudio.get(0).play().catch(e => console.error("Play error:", e));
        }
    }, 100);
}


function audioStopAll() {
	
	let audio;
	while( audio = queue.dequeue() ) {
		audio.sound.currentTime = 0;
		
//		if ( ! audio.prop("ended") ) {
//			audio.get(0).pause();
//		} 
	}
}

function hideRunning(curr) {
	
   if ( audioList.length > 0 ) {
	   audioList.each( function(idx, el) {
		   if ( $(el).closest("tr").hasClass("running")	) {
			   if ( curr.attr("id") != $(el).attr("id")) {
				   editorControl( $(el).closest("tr"), false);
			   }
		   }
	   });
   }
   
}

//---------------------------------------
//  <tr class="data-row" data-seq="10617" data-start-second="13.32" data-end-second="16.31">
//  <tr class="data-row" data-seq="10693" data-start-second="16.31" data-end-second="16.64">
//  <tr class="data-row" data-seq="10676" data-start-second="16.64" data-end-second="22.54">

// asis = [
//			[ 13.21,    16.31],    // prev
//			[ 16.31,    16.64],    // tr
//			[ 16.64,    22.54]     // next
//       ]

function resetPlayer(tr, base, action) {
	
	let delta = 0.1;
	const prev = tr.prev();
	const next = tr.next();
	
	const trs = [ prev, tr, next ];
	
	const TRS_PREV = 0;
	const TRS_TR = 1;
	const TRS_NEXT = 2;
	
	const POS_START = 0;
	const POS_END = 1;
	
//	let orig = [];
	let asis = [];
	let channel = [];
	
	// original array   : string type 으로 저장
	$(trs).each( function() {
		let start = this.data("start-second");
		let end = this.data("end-second");
		asis.push([ start, end ]);
		channel.push( this.find(".channel").text() )
	})
	
	
	const firstLimit = channel[TRS_TR] == "L" ? $("#lFirstLimit").val() : $("#rFirstLimit").val();
	const lastLimit =  channel[TRS_TR] == "L" ? $("#lLastLimit").val() : $("#rLastLimit").val();
	
	let tobe = [];
	
	$(asis).each( function(idx, ary) {
		tobe.push([ ary[0], ary[1] ]);
	})
//	$(tobe).each( function(idx, ele) {
//		console.log(idx + " tobe :  " + ele[0] + "  ,  " + ele[1])
//	});
	

	
	if ( base == "start" ) {
		if ( action == "rewind") {
//			if ( prev && tr.find(".channel").text() != prev.find(".channel").text() ) {   // 다음번의 channel 이 현재와 다르면 다르면
//				lastLimit = 0;
//			}
			
			if ( Number((Number(tobe[TRS_TR][POS_START]) - delta).toFixed(2)) < firstLimit  ) {
				console.log("tobe : return A");
				return;   // prev 가 없으므로 불가
			}
//			console.log("a-1: " + tobe[TRS_TR][POS_START])
			tobe[TRS_TR][POS_START] = (Number(tobe[TRS_TR][POS_START]) - delta).toFixed(2);
//			console.log("a-2: " + tobe[TRS_TR][POS_START])
		} else {     // "forward"
//			console.log("b-1: " + tobe[TRS_TR][POS_START])
			tobe[TRS_TR][POS_START] = (Number(tobe[TRS_TR][POS_START]) + delta).toFixed(2);
//			console.log("b-2: " + tobe[TRS_TR][POS_START])
		}
	} else {       //  // "end"
		if ( action == "rewind") {
//			console.log("c-1: " + tobe[TRS_TR][POS_END] )
			tobe[TRS_TR][POS_END] = (Number(tobe[TRS_TR][POS_END]) - delta).toFixed(2);
//			console.log("c-2: " + tobe[TRS_TR][POS_END] )
		} else {     // "forward"
//			if ( next && tr.find(".channel").text() != next.find(".channel").text() ) {   // 다음번의 channel 이 현재와 다르면 다르면
//				lastLimit = timeDuration;
//			}
			if ( Number((Number(tobe[TRS_TR][POS_END]) + delta).toFixed(2)) > lastLimit  ) {
				console.log("tobe : return B");
				return;   // next 가 없으므로 불가
			}
//			console.log("d-1: " +tobe[TRS_TR][POS_END]  )
			tobe[TRS_TR][POS_END] = (Number(tobe[TRS_TR][POS_END]) + delta).toFixed(2);
//			console.log("d-2: " +tobe[TRS_TR][POS_END]  )
		}
	}

	
	
	
	//   a--b   C--d  e--f
	// check 1. tobe[TRS_TR][POS_START]
	if ( channel[TRS_PREV] == channel[TRS_TR] 
				&& Number(tobe[TRS_PREV][POS_END]) <= Number(tobe[TRS_TR][POS_START])
				&& Number(tobe[TRS_TR][POS_START]) < Number(tobe[TRS_TR][POS_END]) ) {
			; // best case : 1, do nothing
//			console.log("tobe : 1 do nothing");
	} else if (  channel[TRS_PREV] == channel[TRS_TR] 
				&& Number(tobe[TRS_PREV][POS_START]) < Number(tobe[TRS_TR][POS_START]) 
				&& Number(tobe[TRS_TR][POS_START]) <= Number(tobe[TRS_PREV][POS_END])	) {
		tobe[TRS_PREV][POS_END] = Number(tobe[TRS_TR][POS_START] ).toFixed(2);
//		console.log("tobe : 2");
//	} else if ( Number(tobe[TRS_TR][POS_START]) <= Number(tobe[TRS_PREV][POS_START]) ) {
	} else if (  channel[TRS_PREV] == channel[TRS_TR] 
				&& Number(tobe[TRS_TR][POS_START]) <= Number(tobe[TRS_PREV][POS_START]) ) {
		tobe[TRS_TR][POS_START] = Number(asis[TRS_TR][POS_START] ).toFixed(2);
//		console.log("tobe : 3 no db update ");
	} else if ( Number(tobe[TRS_TR][POS_END]) <= Number(tobe[TRS_TR][POS_START]) ) {
		tobe[TRS_TR][POS_START] = Number(asis[TRS_TR][POS_START] ).toFixed(2);
//		console.log("tobe : 4 no db update ");
	}
	
	//  a--b   c--D  e--f
	// check 2. tobe[TRS_TR][POS_END]
	if ( channel[TRS_TR] == channel[TRS_NEXT] 
			&& Number(tobe[TRS_TR][POS_START]) < Number(tobe[TRS_TR][POS_END]) 
			&& Number(tobe[TRS_TR][POS_END]) <= Number(tobe[TRS_NEXT][POS_START]) ) {
		; // bese case : 2, do nothing
//		console.log("tobe : 4  do nothing");
	} else if ( channel[TRS_TR] == channel[TRS_NEXT] 
				&& Number(tobe[TRS_NEXT][POS_END]) <= Number(tobe[TRS_TR][POS_END]) ) {
		tobe[TRS_TR][POS_END] = Number(asis[TRS_TR][POS_END]).toFixed(2);
//		console.log("tobe : 5 db no update");
	} else if ( channel[TRS_TR] == channel[TRS_NEXT] 
			&& Number(tobe[TRS_NEXT][POS_START]) <= Number(tobe[TRS_TR][POS_END])
			&& Number(tobe[TRS_TR][POS_END]) < Number(tobe[TRS_NEXT][POS_END])) {
		tobe[TRS_NEXT][POS_START] = Number(tobe[TRS_TR][POS_END]).toFixed(2);
//		console.log("tobe : 6 ");
	} else if ( Number(tobe[TRS_TR][POS_END]) <= Number(tobe[TRS_TR][POS_START]) ) {
		tobe[TRS_TR][POS_END] = Number(asis[TRS_TR][POS_END]).toFixed(2);
//		console.log("tobe : 7 db no update");
	}
		
	let url = null;

	continual = false;
	audioPauseAll();
//	audioStopAll();
	tr.find("audio").trigger("play");
	
//	console.log(" call save split")
	//-------------
	
	// idx:0 prev
	$(tobe).each( function(idx, ele) {
		if ( asis[idx][0] != tobe[idx][0] || asis[idx][1] != tobe[idx][1] ) {
//			console.log(idx + " tobe changed :  " + ele[0] + "  ,  " + ele[1])
			const currTr = trs[idx];
			const start = tobe[idx][0];
			const end = tobe[idx][1];
			let result = saveReset(currTr, start, end);
		}
	});
	
}



function saveReset(currTr, start, end) {
	
//   const {activeMenu} = queryParams
   
   const seq = currTr.data("seq");
//   console.log(" saveReset(seq = " + seq + ", start = " + start + ", end = " + end);
   
   const data = {
		   "seq": seq,
		   "start": start,
		   "end": end
   };
    
    fetch("resetFrame", {
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
    	
    	saveResetSuccess(currTr, start, end);

    }).catch(err=>{
        console.log(err)
        alert("페이지 갱신 실패")
    })
}

function saveResetSuccess(tr, startValue, endValue) {
	
	if ( tr.length == 0 ) {
		return;
	}
	
	let url = tr.find("source").attr("src");
		
	if ( startValue ) {
		url = url.replace(/start=[^&]*/, "start=" + startValue);
		tr.data("start-second", startValue);
		
//		tr.attr("data-start-second", startValue);   // 이를 사용하면 안됨
	}
	
	if ( endValue ) {
		url = url.replace(/end=[^&]*/, "end=" + endValue);
		
		tr.data("end-second", endValue);
//		tr.attr("data-end-second", endValue);    // 이를 사용하면 안됨
	}
	
	tr.find("source").attr("src", url);
//	console.log("이쪽이에러1")
//	tr.find("audio").load();
	
	if ( startValue ) {
		tr.find(".start").text(startValue);
		tr.find(".timeLength").text((endValue-startValue).toFixed(2));		
	}
	
	if ( endValue ) {
		tr.find(".end").text(endValue);
		tr.find(".timeLength").text((endValue-startValue).toFixed(2));	
	}
	
	const timeLength = tr.find(".timeLength").text();
	
	if (tr.hasClass("warning")){
		tr.removeClass("warning");
	}
	
	if (timeLength > 30){
		tr.addClass("warning")			
	}
	
	const playid = tr.find(".play_grid").attr("id");
	const audio = tr.find("audio");
	const audioid = audio.attr("id");
	const status = audio.data("status");
	const source = audio.find("source");
	const src = source.attr("src");
	
	$("#" + playid).empty();
	const element = '<audio id="' + audioid + '" controls="" controlslist="nodownload" class="sound" data-status="' + status + '">\n'
	           + '      <source src="' + src + '" />"' +
	           + '</audio>"';
	$("#" + playid).html(element);

}

