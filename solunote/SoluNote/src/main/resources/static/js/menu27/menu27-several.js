

let idCheck = false
let file

let createId = false;

$(function () {

	
	 $(document).on("click", "#several_back", function () {
		 $("#uploadKeyword").remove();
		 loadList(1)	
		 $("#tablePage").show();
	 })
	
 
    $(document).on('click', '#get_excel', function () {   

    	excelForm()
        
    })  
	 
	 
    $(document).on('click', '#excel_upload', function () {    	
    	$("#keywordFile").show()	    	
    });
	 
	 $(document).on("click", "#id_create", async function () {

		    if (!validateInputs()) {
		        return;
		    }


		    const hasDuplicates = await checkIdAvailability();
		    if (hasDuplicates) {
		        return; 
		    }

		    await createAccounts();
		});

	 
	    $(document).on('click', '#low-delete', function () {   

	        if($("#userRow .check-box.active:not(.all)").length === 0) {
	            alert("선택한 항목이 없습니다.")
	        } else if(confirm("삭제 하시겠습니까?")) {
	            $('#userRow .check-box.active:not(.all)').each(function() {
	                const row = $(this).closest('tr'); // 해당 행 찾기
	                

	                if (row.is(":last-child")) {

	                    row.find('.keywordContentInput1').val(""); 
	                    row.find('.keywordContentInput2').val(""); 
	                } else {
	                    row.remove();
	                }
	            });

	            // 나머지 행들의 번호 갱신
	            $('#userRow .page-data-row2').each(function(index) {
	                $(this).find('td').eq(1).text(index + 1); 
	        	    $(this).removeClass("active");
	            });
	        }
	        
	    })         
	    
	    
	    $(document).on('click', '#low-add', function () {   

	        const tableBody = $('#userRow');  
	        rowCount = $('#userRow tr.page-data-row2').length;  
	        const nextCount = rowCount + 1;  
	        	       
	        const newRow = `
	            <tr class="page-data-row2 page_link" data-rowid="${nextCount}">
	                <td style="width: 5%;">
	                    <span class="check-box">
	                        <img class="unchecked" src="/SGSAS/images/checck.svg"/>
	                        <img class="checked" src="/SGSAS/images/checck_on.svg"/>
	                    </span>
	                </td>
	                <td style="width: 5%;">${nextCount}</td>
	                <td style="width: 45%;"><input class="keywordContentInput1" placeholder ="4~12자의 영문 대소문자와 숫자"  ></td>
	                <td style="width: 45%;"><input class="keywordContentInput2" ></td>    
	            </tr>
	        `;
	        
	        tableBody.append(newRow); 

	        const newRowElement = tableBody.find('tr.page-data-row2').last();
	        const firstCellInNewRow = newRowElement.find('td').eq(2).find('input');  
 
	        newRowElement[0].scrollIntoView({ behavior: 'smooth', block: 'center' });         
	    })  	 
	 
	
	 
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
    	
        uploadUserList(file)
    	$(".lang_select").hide()
    	$("#keywordInputGroup").hide()
    })    
	 
	
})


function uploadUserList(fo) {
	
    let data = new FormData()
    data.append('file', fo)

	$(".cmx_grid.record_save").addClass("active")
	$(".btn_grid.total").hide()
	
	$(".cmMsgbx").html("엑셀 파일 업로드 중입니다.<br>잠시만 기다려 주세요.")
	                
    fetch("uploadUserList", {
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
			$("#userRow").children().remove();
			$("#userRow").append(html);
			file=""
	    }, 2000);       	
    
    }).catch(err=>{
        console.log(err)
        $(".loader").hide()
        $(".cmMsgbx").html("업로드를 실패하였습니다. 관리자에게 문의해주세요.")
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
	        		filename = "다계정 등록 양식.xlsx"; 
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


function validateInputs() {
    let isValid = true;
    const tcIds = []; 

    $("#userRow .page-data-row2").each(function () {
        const tcIdInput = $(this).find(".keywordContentInput1");
        const tcNameInput = $(this).find(".keywordContentInput2");

        const tcId = tcIdInput.val()?.trim();
        const tcName = tcNameInput.val()?.trim();


        if (!tcId && !tcName) {
            return true; 
        }

        if (tcIds.includes(tcId)) {
            alert(tcId + " : 아이디가 2번 입력되었습니다.");
            tcIdInput.focus();
            isValid = false;
            return false; 
        }
        tcIds.push(tcId); 

        if (!tcId) {
            alert("아이디를 입력해주세요.");
            tcIdInput.focus();
            isValid = false;
            return false; // `each` 루프 중단
        }

        if (!tcName) {
            alert("이름을 입력해주세요.");
            tcNameInput.focus();
            isValid = false;
            return false;
        }

        // 아이디 형식 검사
        if (!_checkIdFormat(tcId)) {
            alert(tcId + " : 4~12자의 영문 대소문자와 숫자로만 입력해주세요.");
            tcIdInput.focus();
            isValid = false;
            return false;
        }
    });

    return isValid;
}


async function checkIdAvailability() {
    const idSet = new Set(); 
    let hasDuplicates = false;

    for (const row of $("#userRow .page-data-row2")) {
        const tcIdInput = $(row).find(".keywordContentInput1");
        const tcId = tcIdInput.val()?.trim();

        if (!tcId || idSet.has(tcId)) continue; 
        idSet.add(tcId);

        try {
            const res = await fetch("check", {
                method: "POST",
                body: "code=" + tcId,
                headers: { "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8" }
            });

            const data = await res.text();

            if (data !== "-1") { 
                alert(tcId + " 는 사용중인 아이디입니다 , 다른아이디를 입력해주세요.");
                tcIdInput.focus(); 
                hasDuplicates = true;
                break; 
            }
        } catch (err) {
            alert("아이디 중복 확인에 실패했습니다. 관리자에게 문의해주세요.");
            tcIdInput.focus();
            hasDuplicates = true;
            break;
        }
    }

    return hasDuplicates;
}

async function createAccounts() {
    const failedIds = [];
    
    for (const row of $("#userRow .page-data-row2")) {
        const tcId = $(row).find(".keywordContentInput1").val()?.trim();
        const tcName = $(row).find(".keywordContentInput2").val()?.trim();
        
        if (!tcId && !tcName) {
            continue;
        }

        const formData = new URLSearchParams({
            tcId: tcId,
            tcPw: "Korloy1966!@",
            tcName: tcName,
            tcEmail: "",
            tcPhone: "",
            tcLevel: 0
        });

        try {
            const res = await fetch("insert", {
                method: "POST",
                body: formData,
                headers: { "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8" }
            });

            if (res.ok) {
                const result = await res.json();
                if (!result.result) failedIds.push(tcId);
            } else {
                throw new Error("서버 오류: " + res.status);
            }
        } catch (err) {
            console.error(err);
            failedIds.push(tcId);
        }
    }

    if (failedIds.length > 0) {
    	    	    	
        alert("다음 ID들은 등록 실패했습니다. 수정 후 다시 시도해주세요:\n" + failedIds.join(", "));
    } else {
        alert("다계정 등록이 완료되었습니다.");
        $("#uploadKeyword").remove(); 
        loadList(1);
        $("#tablePage").show();

    }
}	 


var Base64={_keyStr:"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",encode:function(e){var t="";var n,r,i,s,o,u,a;var f=0;e=Base64._utf8_encode(e);while(f<e.length){n=e.charCodeAt(f++);r=e.charCodeAt(f++);i=e.charCodeAt(f++);s=n>>2;o=(n&3)<<4|r>>4;u=(r&15)<<2|i>>6;a=i&63;if(isNaN(r)){u=a=64}else if(isNaN(i)){a=64}t=t+this._keyStr.charAt(s)+this._keyStr.charAt(o)+this._keyStr.charAt(u)+this._keyStr.charAt(a)}return t},decode:function(e){var t="";var n,r,i;var s,o,u,a;var f=0;e=e.replace(/[^A-Za-z0-9\+\/\=]/g,"");while(f<e.length){s=this._keyStr.indexOf(e.charAt(f++));o=this._keyStr.indexOf(e.charAt(f++));u=this._keyStr.indexOf(e.charAt(f++));a=this._keyStr.indexOf(e.charAt(f++));n=s<<2|o>>4;r=(o&15)<<4|u>>2;i=(u&3)<<6|a;t=t+String.fromCharCode(n);if(u!=64){t=t+String.fromCharCode(r)}if(a!=64){t=t+String.fromCharCode(i)}}t=Base64._utf8_decode(t);return t},_utf8_encode:function(e){e=e.replace(/\r\n/g,"\n");var t="";for(var n=0;n<e.length;n++){var r=e.charCodeAt(n);if(r<128){t+=String.fromCharCode(r)}else if(r>127&&r<2048){t+=String.fromCharCode(r>>6|192);t+=String.fromCharCode(r&63|128)}else{t+=String.fromCharCode(r>>12|224);t+=String.fromCharCode(r>>6&63|128);t+=String.fromCharCode(r&63|128)}}return t},_utf8_decode:function(e){var t="";var n=0;var r=c1=c2=0;while(n<e.length){r=e.charCodeAt(n);if(r<128){t+=String.fromCharCode(r);n++}else if(r>191&&r<224){c2=e.charCodeAt(n+1);t+=String.fromCharCode((r&31)<<6|c2&63);n+=2}else{c2=e.charCodeAt(n+1);c3=e.charCodeAt(n+2);t+=String.fromCharCode((r&15)<<12|(c2&63)<<6|c3&63);n+=3}}return t}}
