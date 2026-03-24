
$(function () {
	
	let file
		
    $(document).on('click', '#uploadKeyword .back_btn', function () {    	
    	$("#uploadKeyword").remove();
    	$("#tablePage").show();
    	$(".menu_short").css("z-index","")
    });	
		
    $(document).on('click', '#excel_upload', function () {    	
    	$("#keywordFile").show()	    	
    });
    
    
    $(document).on('click', '#keywordFileClose', function () {    	    	
    	$(".lang_select").show()
    	$("#keywordInputGroup").show()
    	$(".cmx_grid.record_save").removeClass("active")
    	$(".btn_grid.total").show()    	
    	$("#uploadTextInput").val("") 
    	$("#keywordFile").hide()	    	
    	$("#keywordInputHidden").val("")
    	
    	file = "";
    });    

    $(document).on('click', '#uploadFile', function () {    	
    	$("#keywordInputHidden").click()   	
    });    
    
    
    $(document).on('change', '#keywordInputHidden', function (e) {
        file = event.target.files[0];
        const allowedExtensions = /(\.xls|\.xlsx|\.csv)$/i;

        if (file && !allowedExtensions.test(file.name)) {
            alert("올바른 파일 형식(.xls, .xlsx, .csv)만 업로드할 수 있습니다.");
            file = "";
            return false
        }   
        const text = `${file.name}`
        $("#uploadTextInput").val(text) 
        
        
    });    
    

    $(document).on('click', '#keywordSubmit', function () {   
    	
        if (!file) {
            alert('파일을 등록해주세요.');
            return; // 함수 종료
        }
    	
    	uploadFileKeyword(file)
    	$(".lang_select").hide()
    	$("#keywordInputGroup").hide()
    })
    
    $(document).on('click', '#text_create', function () {
    	    		
        const list = [];
        const addList = [];
        
        let count = 0;
        
        $('#keywordRow .page-data-row').each(function() {
            const keyword = $(this).find('.keywordContentInput1').val();
            const speech = $(this).find('.keywordContentInput2').val();  
            const detail = $(this).find('.transDivDB').eq(0).text();  
            const keywordText = $(this).find('.transDivDB').eq(1).text();  

            if (keyword && speech) {
                list.push({ keyword: keyword, speech: speech, detail: detail });

                if (!keywordText || keywordText.trim() === "") {
                	const rowid=$(this).data('rowid');
                    addList.push({ keyword: keyword, speech: speech, detail: detail,rowid : rowid });
                }
            }
        });
                
        if(list.length == 0){
        	alert("키워드와 발음기호를 입력해주세요.");
        	return false 
        }
                
        if(addList.length == list.length) {
            $("#modal_title").text("키워드문장 / 발음문장 생성")
            $("#keywordFile").show()	
        	$(".lang_select").hide()
        	$("#keywordInputGroup").hide()
        	getAudioList(list,2)
        }else{        	
        	if(addList.length == 0){
        		return false
        	}else{        		
	            $("#modal_title").text("키워드문장 / 발음문장 생성")
	            $("#keywordFile").show()	
	        	$(".lang_select").hide()
	        	$("#keywordInputGroup").hide()
	        	
        		getAudioAddList(addList, 2);
	        	
	        			
        	}        
        }
                
        
    })     
    
    
	$(document).on('click', '#audio_create', function () {   
	    const list = [];
	    const addList = []; 
	    let isValid = true;
	
	    $('#keywordRow .page-data-row').each(function() {
	        const keyword = $(this).find('.keywordContentInput1').val();
	        const speech = $(this).find('.keywordContentInput2').val(); 
	        const detail = $(this).find('.transDivDB').eq(0).text(); 
	        const keywordText = $(this).find('.transDivDB').eq(1).text();
	        const pronounceText = $(this).find('.transDivDB').eq(2).text();
	        const audioId = $(this).find('audio').attr('id');
	        
	
	        if (keyword && speech) {
	            if (keywordText && pronounceText) {
	
	                list.push({
	                    keyword: keyword,
	                    speech: speech,
	                    detail: detail,
	                    keywordText: keywordText,
	                    pronounceText: pronounceText
	                });
	
	
	                if (!audioId) {
	                	const rowid=$(this).data('rowid');
	                    addList.push({
	                        keyword: keyword,
	                        speech: speech,
	                        detail: detail,
	                        keywordText: keywordText,
	                        pronounceText: pronounceText,
	                        rowid :rowid
	                    });
	                }
	            } else {
	                $(this).find('.transDivDB').eq(1).focus();
	                alert("키워드문장과 발음문장을 입력해주세요.");
	                isValid = false;
	                return false; // each 중단
	            }
	        }
	    });
	
	
	    if (!isValid) {
	        return false;
	    }
	
	
	    if (addList.length == list.length) {
	        $("#modal_title").text("음성데이터 생성");
	        $("#keywordFile").show();    
	        $(".lang_select").hide();
	        $("#keywordInputGroup").hide();                    
	        getAudioList(list, 3); 
	    } else {
	        if (addList.length == 0) {
	            return false;  
	        } else {
	            $("#modal_title").text("음성데이터 생성");
	            $("#keywordFile").show();    
	            $(".lang_select").hide();
	            $("#keywordInputGroup").hide();   
	            
	            getAudioAddList(addList, 3);
	        }
	    }
	});  
    
    
    $(document).on('click', '#keyword_list_create', function () {   

        let audioSeq = [];
        
        const title = $('#keyword_List_Title').val();

        if (title === "") {
            alert("학습 그룹명을 입력해주세요.");
            $('#keyword_list_title').focus();
            return false;
        }
        

        $('#keywordRow .page-data-row').each(function() {
        	
            let audioId = $(this).find('audio').attr('id');
            if (audioId) {
                let idNumber = audioId.split('-')[1];
                if (idNumber) {
                	audioSeq.push({seq: parseInt(idNumber) });
                }
            }
        });
        

        $("#modal_title").text("키워드 Data 생성")
        $("#keywordFile").show()	
    	$(".lang_select").hide()
    	$("#keywordInputGroup").hide()                
        
        
        addGroup(audioSeq,title)
        
    })     
    
    $(document).on('click', '#get_excel', function () {   

    	excelForm()
        
    })     
    
    $(document).on('click', '#low-delete', function () {   

        if($("#keywordRow .check-box.active:not(.all)").length === 0) {
            alert("선택한 항목이 없습니다.")
        } else if(confirm("삭제 하시겠습니까?")) {
            $('#keywordRow .check-box.active:not(.all)').each(function() {
                // 입력 필드 비우기
            	$(this).closest('tr').remove();

            });
                        
            $('#keywordRow .page-data-row').each(function(index) {
            	
            	$(this).find('td').eq(1).text(index + 1);
            });
        	
        }
        
    })         
    
    $(document).on('click', '#low-add', function () {   

        const tableBody = $('#keywordRow');  
        rowCount = $('#keywordRow tr.page-data-row').length;  
        const nextCount = rowCount + 1;  
        
        const newRow = `
            <tr class="page-data-row page_link" data-rowid="${nextCount}">
                <td style="width: 2%;">
                    <span class="check-box">
                        <img class="unchecked" src="/SGSAS/images/checck.svg"/>
                        <img class="checked" src="/SGSAS/images/checck_on.svg"/>
                    </span>
                </td>
                <td style="width: 4%;">${nextCount}</td>
                <td style="width: 10%;"><input class="keywordContentInput1" placeholder ="키워드 입력"  ></td>
                <td style="width: 10%;"><input class="keywordContentInput2" placeholder ="발음 입력"></td>    
                <td class="transText padding_0" style="width: 15%;">
                    <div class="transDivDB editable" contenteditable="true"></div>
                </td>    
                <td class="transText padding_0" style="width: 22%;">
                    <div class="transDivDB editable" contenteditable="true"></div>
                </td>        
                <td class="transText padding_0" style="width: 22%;">
                    <div class="transDivDB editable" contenteditable="true"></div>
                </td>        		    
        		<td style="width: 15%;" id="audioTd-${nextCount}"></td>
            </tr>
        `;
        
        tableBody.append(newRow); 

        const newRowElement = tableBody.find('tr.page-data-row').last();
        const firstCellInNewRow = newRowElement.find('td').eq(2).find('input');  
        firstCellInNewRow.focus();   

        newRowElement[0].scrollIntoView({ behavior: 'smooth', block: 'center' });         
    })   
      
      
});


function uploadFileKeyword(fo) {
	
    let data = new FormData()
    data.append('file', fo)

	$(".cmx_grid.record_save").addClass("active")
	$(".btn_grid.total").hide()
	
	$(".cmMsgbx").html("엑셀 파일 업로드 중입니다.<br>잠시만 기다려 주세요.")
	                
    fetch("upload", {
        method: "POST",
        body: data
    }).then(res=>{
    	if ( res.redirected == true ) {
    		window.location.href = res.url;
    	} else if(res.ok) {
    		return res.text();
    	} else {
    		return Promise.reject(res);
    	}
    }).then(html=>{
    	$(".loader").hide()
    	$(".cmMsgbx").html("업로드를 완료하였습니다.")
    	
	    setTimeout(function () {
	    	$("#keywordFileClose").click()
			$("#keywordRow").children().remove();
			$("#keywordRow").append(html);
			$("#excel_upload").hide();
			file=""
	    }, 2000);       	
    
    }).catch(err=>{
        console.log(err)
        $(".loader").hide()
        $(".cmMsgbx").html("업로드를 실패하였습니다. 관리자에게 문의해주세요.")
    })
}


function getAudioList(list,step) {
   

	$(".cmx_grid.record_save").addClass("active")
	$(".loader").show()
	$(".btn_grid.total").hide()
	
	if(step==2){
		$(".cmMsgbx").html("키워드문장 / 발음문장을 생성중입니다.<br>잠시만 기다려 주세요.")	
	}else{
		$(".cmMsgbx").html("음성데이터를 생성중입니다.<br>잠시만 기다려 주세요.")	
	}
                
    fetch("getAudio?step="+step, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(list)
    }).then(res=>{
    	if ( res.redirected == true ) {
    		window.location.href = res.url;
    	} else if(res.ok) {
    		return res.text();
    	} else {
    		return Promise.reject(res);
    	}
    }).then(html=>{
    	$(".loader").hide()
    	$(".cmMsgbx").html("생성을 완료하였습니다.")
    	
	    setTimeout(function () {
	    	$("#keywordFileClose").click()
			$("#keywordRow").children().remove();
			$("#keywordRow").append(html);	    
			$("#excel_upload").hide();			

			
			if(step == 2 ){
		    	$('#keyword_span').prepend('<span class = "keywordUploadSpan">*</span>');		    					
			}else{
				$("#keyword_list_create").show()				
				$('#pronounce_span').prepend('<span class = "keywordUploadSpan">*</span>');
			}
	    }, 2000);       	
    
    }).catch(err=>{
        $(".loader").hide()
        $(".cmMsgbx").html("셍성에 실패하였습니다. 관리자에게 문의해주세요.")
    })
}

function addGroup(audioSeq,title) {
	

	
	$(".cmx_grid.record_save").addClass("active")
	$(".loader").show()
	$(".btn_grid.total").hide()
	
	$(".cmMsgbx").html("키워드 Data를 생성중입니다.<br>잠시만 기다려 주세요.")	
		
    fetch("addGroup?title="+title, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(audioSeq)
    }).then(res=>{
    	if ( res.redirected == true ) {
    		window.location.href = res.url;
    	} else if(res.ok) {
    		return res.text();
    	} else {
    		return Promise.reject(res);
    	}
    }).then(html=>{
    	
    	$(".loader").hide()
    	$(".cmMsgbx").html("키워드 Data 생성을 완료하였습니다.")
    	
	    setTimeout(function () {
	    	addrowTf= false
	    	location.reload(true);
	    }, 2000);          	
    	
    }).catch(err=>{
    	$(".loader").hide()
    	$(".cmMsgbx").html("키워드 Data 생성을 실패하였습니다.관리자에게 문의해주세요.")    	

	    setTimeout(function () {
	    	location.reload(true);
	    }, 2000);      	
    	
    })
}

function excelForm(){

	fetch('excelForm', {
	    method: "GET"
	}).then( res => {
	        if(res.ok) {
	        	const orig = res.headers.get('filename_base64');

	        	if ( orig ) {
	        		filename = Base64.decode(orig);
	        	} else {
	        		filename = "키워드학습양식.xlsx"; 
	        	}
	            return res.blob();
	        }
	        return Promise.reject(res)
	    }).then( blob => {
	        var url = window.URL.createObjectURL(blob);
	        var a = document.createElement('a');
	        a.href = url;
	        a.download = filename;
	        document.body.appendChild(a); // append the element to the dom
	        a.click();
	        a.remove(); // afterwards, remove the element
	    }).catch((e)=>{
	    	console.log(e)
	    	alert("다운로드 중 에러 발생");
	})

}


function getAudioAddList(list,step) {
	   

	$(".cmx_grid.record_save").addClass("active")
	$(".loader").show()
	$(".btn_grid.total").hide()
	
	if(step==2){
		$(".cmMsgbx").html("키워드문장 / 발음문장을 생성중입니다.<br>잠시만 기다려 주세요.")	
	}else{
		$(".cmMsgbx").html("음성데이터를 생성중입니다.<br>잠시만 기다려 주세요.")	
	}
                
    fetch("getAudioAdd?step="+step, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(list)
    }).then(response => response.json())
    .then(pages =>{
    	$(".loader").hide()
    	$(".cmMsgbx").html("생성을 완료하였습니다.")
    	    	
	    setTimeout(function () {
	    	

			if(step == 2 ){					
	            pages.forEach((page, index) => {
	            	console.log(page)
	    	    	const addRow = $('#keywordRow').find(`tr[data-rowid="${page.rowid}"]`);
	            	console.log(addRow)
	            	addRow.find('.keywordContentInput1').val(page.keyword);
	            	addRow.find('.keywordContentInput2').val(page.speech);
	            	addRow.find('.transDivDB').eq(0).text(page.detail);
	            	addRow.find('.transDivDB').eq(1).text(page.keywordText);
	            	addRow.find('.transDivDB').eq(2).text(page.pronounceText);
	            });	    
		    	$("#keywordFileClose").click()		            
			}else{
	            pages.forEach((page, index) => {
	                const audioTd = document.getElementById(`audioTd-${page.rowid}`);
	                if (audioTd) {
	                	const correctedFilePath = page.newnm.replace(/\\/g, '/');
	                    audioTd.innerHTML = `
	                        <div class="player">
	                            <div class="play_grid">
	                                <audio id="sound-${page.SEQ}" controls controlsList="nodownload" class="sound">
	                                	<source src="/SGSAS/keywordtrain/download?fileNm=${correctedFilePath}">
	                                </audio>
	                            </div>
	                        </div>
	                    `;
	                }
	            });	  
		    	$("#keywordFileClose").click() 	
				$("#keyword_list_create").show()	            
			}
	    }, 2000);       	
    
    }).catch(err=>{
        $(".loader").hide()
        $(".cmMsgbx").html("셍성에 실패하였습니다. 관리자에게 문의해주세요.")
    })
}

var Base64={_keyStr:"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",encode:function(e){var t="";var n,r,i,s,o,u,a;var f=0;e=Base64._utf8_encode(e);while(f<e.length){n=e.charCodeAt(f++);r=e.charCodeAt(f++);i=e.charCodeAt(f++);s=n>>2;o=(n&3)<<4|r>>4;u=(r&15)<<2|i>>6;a=i&63;if(isNaN(r)){u=a=64}else if(isNaN(i)){a=64}t=t+this._keyStr.charAt(s)+this._keyStr.charAt(o)+this._keyStr.charAt(u)+this._keyStr.charAt(a)}return t},decode:function(e){var t="";var n,r,i;var s,o,u,a;var f=0;e=e.replace(/[^A-Za-z0-9\+\/\=]/g,"");while(f<e.length){s=this._keyStr.indexOf(e.charAt(f++));o=this._keyStr.indexOf(e.charAt(f++));u=this._keyStr.indexOf(e.charAt(f++));a=this._keyStr.indexOf(e.charAt(f++));n=s<<2|o>>4;r=(o&15)<<4|u>>2;i=(u&3)<<6|a;t=t+String.fromCharCode(n);if(u!=64){t=t+String.fromCharCode(r)}if(a!=64){t=t+String.fromCharCode(i)}}t=Base64._utf8_decode(t);return t},_utf8_encode:function(e){e=e.replace(/\r\n/g,"\n");var t="";for(var n=0;n<e.length;n++){var r=e.charCodeAt(n);if(r<128){t+=String.fromCharCode(r)}else if(r>127&&r<2048){t+=String.fromCharCode(r>>6|192);t+=String.fromCharCode(r&63|128)}else{t+=String.fromCharCode(r>>12|224);t+=String.fromCharCode(r>>6&63|128);t+=String.fromCharCode(r&63|128)}}return t},_utf8_decode:function(e){var t="";var n=0;var r=c1=c2=0;while(n<e.length){r=e.charCodeAt(n);if(r<128){t+=String.fromCharCode(r);n++}else if(r>191&&r<224){c2=e.charCodeAt(n+1);t+=String.fromCharCode((r&31)<<6|c2&63);n+=2}else{c2=e.charCodeAt(n+1);c3=e.charCodeAt(n+2);t+=String.fromCharCode((r&15)<<12|(c2&63)<<6|c3&63);n+=3}}return t}}
