var ACTION_FORFM_NAME= "#action_form";
var calenderStartName;
var calenderEndName;
var currentPageName = "#page";

var calenderItemName;
let uploadMenu;

const cookieDay = 1;
const cookeiDayPage = 365; 
var contextPath = $('#contextPathHolder').attr('data-contextPath') ? $('#contextPathHolder').attr('data-contextPath') : '';

$(function() {
	
	_checkIdFormat = function(string) {
	    // 아이디가 알파벳 대소문자, 숫자로만 이루어져 있고, 길이가 4~12인지 검사
	    var stringRegx = /^[a-zA-Z0-9]{4,12}$/; 
	    var isValid = false; 

	    if (stringRegx.test(string)) {
	        isValid = true;
	    } else {
	        // 한글이 포함된 경우 false
	        var koreanRegx = /[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]/;
	        if (koreanRegx.test(string)) {
	            isValid = false;
	        }
	    }

	    return isValid;  
	}
	
	
checkStringFormat = function (string) { 
	var stringRegx = /^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,50}$/; 
	var isValid = false; 
	if(stringRegx.test(string)) { 
		isValid = true; 
	} 
	return isValid; 
}
checkSpecialCharacter = function (string) {
	var spchar = /[\{\}\[\]\/?.,;:|\)*~`!^\-_+<>@\#$%&\\\=\(\'\"]/gi;
	var isValid = false; 
	if (spchar.test(string)) {
		isValid = true; 
	}
	return isValid; 
}
checkEmailFormat = function (string) {
	// 정규식 - 이메일 유효성 검사
	var regEmail = /([\w-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([\w-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$/;
	var isValid = false;
	if (regEmail.test(string)) {
		isValid = true;
	}
	return isValid;
}
checkPhoneFormat = function (string) {
	// 정규식 -전화번호 유효성 검사
	var regPhone = /^(010|011|016|017|018|019)-\d{3,4}-\d{4}$/
	var isValid = false;
	if (regPhone.test(string)) {
		isValid = true;
	}	
	return isValid;
}
fnCheckCompPass = function (idVal, pwdVal, pwdVal2) {
	
	if (pwdVal != pwdVal2) {
    	$("#tcPw_cm").focus();
    	$("#checkPwCm").text("비밀번호와 비밀번호 확인 정보가 다릅니다.")	
    	$("#checkPw").hide()	
		$("#checkPwCm").css("display","block")	
		return false;
	}

	if (pwdVal.length < 8) {		
    	$("#tcPw").focus();
    	$("#checkPw").text("비밀번호는 영문+숫자+특수문자 조합으로 8자이상을 입력하셔야 합니다.")
    	$("#checkPwCm").hide()	
		$("#checkPw").css("display","block")		
		return false;
	}

	var v_alp = checkStringFormat(pwdVal);
	if (v_alp == false) {
    	$("#tcPw").focus();
    	$("#checkPw").text("비밀번호는 영문+숫자+특수문자 조합으로 8자이상을 입력하셔야 합니다.")	
    	$("#checkPwCm").hide()
		$("#checkPw").css("display","block")
		return false;
	}

	var b_dup = false;
	for (var i=0 ; i<(idVal.length-2) ; i++) {
		var v_uid = idVal.substring( i, (i+3) );
		var n_idx = pwdVal.indexOf(v_uid.toString());
		if (n_idx != undefined) {
			if (n_idx >= 0)
				b_dup = true;
		}
	}
	if (b_dup == true) { 
    	$("#tcPw").focus();
    	$("#checkPw").text("아이디와 중복된 정보를 비밀번호를 입력할 수 없습니다.")
    	$("#checkPwCm").hide()
		$("#checkPw").css("display","block")		
		return false;
	}
	
	return true;
}	

fnCheckCompPassChange = function (idVal, pwdVal, pwdVal2) {
	
	if (pwdVal != pwdVal2) {
    	$("#tcPw_cm-change").focus();
    	$("#checkPwCmChange").text("비밀번호와 비밀번호 확인 정보가 다릅니다.")	
    	$("#checkPwChange").hide()	
		$("#checkPwCmChange").css("display","block")	
		return false;
	}

	if (pwdVal.length < 8) {		
    	$("#tcPw-change").focus();
    	$("#checkPwChange").text("비밀번호는 영문+숫자+특수문자 조합으로 8자이상을 입력하셔야 합니다.")
    	$("#checkPwCmChange").hide()	
		$("#checkPwChange").css("display","block")		
		return false;
	}

	var v_alp = checkStringFormat(pwdVal);
	if (v_alp == false) {
    	$("#tcPw-change").focus();
    	$("#checkPwChange").text("비밀번호는 영문+숫자+특수문자 조합으로 8자이상을 입력하셔야 합니다.")	
    	$("#checkPwCmChange").hide()
		$("#checkPwChange").css("display","block")
		return false;
	}

	var b_dup = false;
	for (var i=0 ; i<(idVal.length-2) ; i++) {
		var v_uid = idVal.substring( i, (i+3) );
		var n_idx = pwdVal.indexOf(v_uid.toString());
		if (n_idx != undefined) {
			if (n_idx >= 0)
				b_dup = true;
		}
	}
	if (b_dup == true) { 
    	$("#tcPw-change").focus();
    	$("#checkPwChange").text("아이디와 중복된 정보를 비밀번호를 입력할 수 없습니다.")
    	$("#checkPwCmChange").hide()
		$("#checkPwChange").css("display","block")		
		return false;
	}
	
	return true;
}	
	
//file attach
$(document).on('click', '.file_attach .btn_file', function() {
    $(this).closest('.file_attach').find('.form_file').click();
});

setCalender =function (calenderItemName) {
  $( calenderItemName ).datepicker({
        changeMonth: true,
        changeYear: true,
//          buttonImage: "button.png", 
//          buttonImageOnly: true ,
        showMonthAfterYear: true,
        dateFormat: "yy-mm-dd",
        monthNamesShort: ['1','2','3','4','5','6','7','8','9','10','11','12'],
//         onSelect: function(date) {
//             var s_date = $(endName).val() ;
//             var d1 = new Date(date) ;
//             var d2 = new Date(s_date) ;
//             d1.setHours(0, 0, 0, 0) ;
//             d2.setHours(0, 0, 0, 0) ;
//             if (d1 > d2) {
//                 $(endName).val(date) ;
//             }
//         }
    });
};

setCookie = function(name, value, exp) {
	var date = new Date();      
	date.setTime(date.getTime() + exp*24*60*60*1000);  
	date.setHours(0, 0, 0, 0)
	document.cookie = name + '=' + value + ';expires=' + date.toUTCString() + ';path=/';  

};

getCookie = function(name) {      
	var value = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');      
	return value? value[2] : null;  
};


	
if(getCookie("pageSize") != null){
	$("#page_count").val(getCookie("pageSize"))
}

if(getCookie("popPageSize") != null){
	$("#pop_page_count").val(getCookie("popPageSize"))
	$("#page_count_list").val(getCookie("popPageSize"))		
	
}

if (!getCookie("pageSize")) {
	setCookie("pageSize", 10, cookeiDayPage);
}

if (!getCookie("popPageSize")) {		
	setCookie("popPageSize", 10, cookeiDayPage);
}	
	
// 시작일 종료일 선택시 사용
setCalenderStartEnd = function () {	
    $( calenderStartName ).datepicker({
        changeMonth: true,
        changeYear: true,
//         buttonImage: "button.png", 
//         buttonImageOnly: true ,
        showMonthAfterYear: true,
        dateFormat: "yy-mm-dd",
        monthNamesShort: ['1','2','3','4','5','6','7','8','9','10','11','12'],
        onSelect: function(date,inst) {	
            var s_date = $(calenderEndName).val() ;
            var d1 = new Date(date) ;
            var d2 = new Date(s_date) ;
            d1.setHours(0, 0, 0, 0) ;
            d2.setHours(0, 0, 0, 0) ;
            setCookie("searchStartDate",date,cookieDay)            
        }
    });
    
    $( calenderEndName ).datepicker({
        changeMonth: true,
        changeYear: true,
        showMonthAfterYear: true,
        dateFormat: "yy-mm-dd",
        monthNamesShort: ['1','2','3','4','5','6','7','8','9','10','11','12'],
        onSelect: function(date,inst) {            
            var s_date = $(calenderStartName).val() ;
            var d1 = new Date(date) ;
            var d2 = new Date(s_date) ;
            d1.setHours(0, 0, 0, 0) ;
            d2.setHours(0, 0, 0, 0) ;
            setCookie("searchEndDate",date,cookieDay)

        }
    });
}; //setCalenderStartEnd

_setCalenderDate = function(calenderName, manthTerm) {
	var d = new Date();
	var lastDayofLastMonth = ( new Date( d.getYear(), d.getMonth(), 0) ).getDate();
	if(d.getDate() > lastDayofLastMonth) {
	    d.setDate(lastDayofLastMonth);
	}
	var month = d.getMonth() + manthTerm;
	d.setMonth(month);
	$(calenderName).datepicker("setDate", d );
}; 


//기간 선택 (월 단위 선택)
_setCalenderMonthTerm = function(monthTerm) {
	
	_setCalenderDate(calenderStartName, monthTerm);
	var d = new Date();
	$(calenderEndName ).datepicker("setDate", d );
//	 	_search();
	
};


_goPage = function (pageNum) {
	
	var datatimeRegexp = /[0-9]{4}-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])/;

    if (!$(calenderStartName) && !datatimeRegexp.test($(calenderStartName).val()) ) {
        alert("날짜는 yyyy-mm-dd 형식으로 입력해주세요.");
        $(calenderStartName).focus();
        return false;
    }
    
    if (!$(calenderEndName) &&  !datatimeRegexp.test($(calenderEndName).val()) ) {
    	alert("날짜는 yyyy-mm-dd 형식으로 입력해주세요.");
    	$(calenderStartName).focus();
    	return false;
    }
	
    $(currentPageName).val(pageNum);
//    $(ACTION_FORFM_NAME).attr("method", "post").submit();
    
    var params = jQuery(ACTION_FORFM_NAME).serialize(); // serialize() : 입력된 모든Element(을)를 문자열의 데이터에 serialize 한다.
//  alert(params);
    
    _setLoading(true);
    
  jQuery.ajax({
      url: contextPath+LIST_URL,
      type: 'POST',
      data:params,
      contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
      dataType: 'html',
      success: function (result) {
    	  _isAjaxLogin(result);
    	  
          if (result){
          	 $("#ajax_list_div").replaceWith(result);
          	//data row 선택시 처리 모든 목록에서 함수 정의 필요
          	 _setClickEvent("#div_content_list");
          	_setLoading(false);
          }
      }
  });
  
};

_setClickEvent = function (id) {
	alert("_setClickEvent 함수 정의해서 사용하시기 바랍니다. elementId :"+id);
}

_search = function (){
	_goPage(0);
// 	$("#action_form").submit();
};


//_callAjax = function(_frmData, _cfn, _curl="../detail", _ctype="POST", _error) {
_callAjax = function(_frmData, _cfn, _curl, _ctype, _error) {
	
	console.log(_frmData) ;
	// console.log(_cfn) ;
	console.log(_curl) ;
	console.log(_ctype) ;
	
	console.log({
		url : contextPath+_curl,
		type : _ctype,
		processData : false,
		contentType : false,
		data : _frmData,
		success : _cfn,
		error : _error
	}) ;
	
	jQuery.ajax({
		url : contextPath+_curl,
		type : _ctype,
		processData : false,
		contentType : false,
		data : _frmData,
		success : _cfn,
		error : _error
	});
};

_error = function(e, ee, eee) {
	alert(e);
	console.log(e) ;
	console.log(ee) ;
	console.log(eee) ;
};

_cfn = function(json) {
	var obj = JSON.parse(json);
//    this.remove();
alert(obj);
console.log(obj) ;
//alert(obj.fileList[0]);
//$('#test'+obj.seq).html("<img src=\"../images/img_upload_end.png\">");
};

//_setAudioPlayer = function(audioSeq, id="#player") {
_setAudioPlayer = function(audioSeq, id) {
	
	var audioUrl = contextPath+'/menu18/mp3_download?seq='+audioSeq;
	$("#audio_src").attr("src", audioUrl);
	$(id)[0].load();

	$(id).on(
	    "timeupdate", 
	    function(event){
//	    	onTrackedVideoFrame(this.currentTime, this.duration);
	    	fnSetHoverMove(this.currentTime);
	    });

	$(".chatting_wrap ul li").click( function() {
		$(id)[0].currentTime = eval($(this).data('ref_sp')/100);
		$(id)[0].play();
	});
	
};

fnSetRate = function (n_rate) {
	$("#player")[0].playbackRate = n_rate;
};

fnSetHoverMove = function (v_pos) {
    v_pos = v_pos*100;

    $(".chatting_wrap ul li").each( function() {
    	v_pos_s = $(this).data('ref_sp');
    	v_pos_e = $(this).data('ref_ep');
    	if (parseInt(v_pos_s) <= parseInt(v_pos) && parseInt(v_pos) <= parseInt(v_pos_e)) {
    		$(this).children().children().children().addClass("mark");
    	} else {
    		$(this).children().children().children().removeClass("mark");
    	}
    });
};

//open layer popup
_openLayerPopup = function(id) {
	$(id).bPopup({
		modalClose: false,
	    opacity: 0.7
	});
};


_setLoading =function(isShow) {
	if(isShow) {
        $("#loading").bPopup({
            modalClose: false,
            opacity: 0
        });
	} else {
		 $("#loading").bPopup().close();
	}
}

_downloadExcelFile = function(frmId, callUrl, callMethod) {
	var xhr = new XMLHttpRequest();
	
	xhr.onreadystatechange = function(){
	    if (this.readyState == 4 && this.status == 200){
	      
	       var filename = "";
	       var disposition = xhr.getResponseHeader('Content-Disposition');
	         if (disposition && disposition.indexOf('attachment') !== -1) {
	             var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
	             var matches = filenameRegex.exec(disposition);
	             if (matches != null && matches[1]) filename = matches[1].replace(/['"]/g, '');
	         }
	      
	        //this.response is what you're looking for
	        console.log(this.response, typeof this.response);
	        
	        _setLoading(false);
	        var a = document.createElement("a");
	        var url = URL.createObjectURL(this.response)
	        a.href = url;
	        a.download = filename;
	        document.body.appendChild(a);
	        a.click();
	        window.URL.revokeObjectURL(url);
	    }
	};

	var form = document.getElementById(frmId);
	var data = new FormData(form);

	
	_setLoading(true);
	xhr.open(callMethod, contextPath+callUrl);
	xhr.responseType = 'blob'; // !!필수!!
	xhr.send(data); // 파라미터 설정하는 부분이며 formData 설정 부분은 생략
	
	return xhr;
}

_isJson = function(str) {
	try {
		if(typeof JSON.parse(str)== 'object')	return true; 
		else return false;
	} catch (e) {
		return false;
	}
}

_isAjaxLogin = function(str) {
	if(_isJson(str)) {
		json = JSON.parse(str);
		
//		var isLogout = json.invalid_session;
//		
//		if(typeof isLogout == "undefined"){
//			return;
//		}
		
		if(json.invalid_session) {
//			alert("로그인 정보가 없습니다. 로그인 하여 주십시오.");
			location.href = contextPath+"/user/login";
		}
	}
	
}
});

(function($) {
	let ffh = {} ;
	ffh.init = function () {
		ffh.table = $("table#div_content_list") ;
		if (ffh.table.hasClass("ffh")) {
			ffh.header = ffh.table.find("thead") ;
			ffh.tb_clone = ffh.table.clone() ;
			ffh.tb_clone.attr('id', '') ;
			ffh.tb_clone.css('width', ffh.table.outerWidth()+'px') ;
			$("body").append(ffh.tb_clone) ;
			ffh.tb_clone.wrap('<div class="fixed_header_cont"></div>') ;
			ffh.cont = $(".fixed_header_cont") ;
			ffh.cont.css('height', ffh.header.outerHeight()+'px') ;
			ffh.cont.hide() ;
	        var scrollbody = $("div.common_content") ;
	        scrollbody.scroll( function() {
	        	if (ffh.table.offset().top < 0) {
	        		if (ffh.table.offset().top + ffh.table.height() > 60+ffh.header.outerHeight()+10) {
	            		ffh.cont.fadeIn(50) ;
	        		} else {
	            		ffh.cont.fadeOut(100) ;
	        		}
	        	} else {
	        		ffh.cont.fadeOut(100) ;
	        	}
	        });
		}
	}
	window.ffh = ffh ;
	$(window).resize( function() {
		if (ffh && ffh.tb_clone) {
			ffh.tb_clone.css('width', ffh.table.outerWidth()+'px') ;			
		}
	}) ;
})(jQuery) ;

//----------------------------
function insertUrlParam(key, value) {
    if (history.replaceState) {
        let searchParams = new URLSearchParams(window.location.search);
        searchParams.set(key, value);
        let newurl = window.location.protocol + "//" + window.location.host + window.location.pathname + '?' + searchParams.toString();
        history.replaceState({path: newurl}, '', newurl);
    }
}


function isValidDate(yyyymmdd) {

	var r = true;

	try {

		var date = [];
		if (yyyymmdd.length == 8) {

			date[0] = yyyymmdd.substring(0, 4);
			date[1] = yyyymmdd.substring(4, 6);
			date[2] = yyyymmdd.substring(6, 8);

		} else if (yyyymmdd.length == 10) {
			date = yyyymmdd.split("-");
		}

		var yyyy = parseInt(date[0], 10);
		var mm = parseInt(date[1], 10);
		var dd = parseInt(date[2], 10);

		var dateRegex = /^(?=\d)(?:(?:31(?!.(?:0?[2469]|11))|(?:30|29)(?!.0?2)|29(?=.0?2.(?:(?:(?:1[6-9]|[2-9]\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00)))(?:\x20|$))|(?:2[0-8]|1\d|0?[1-9]))([-.\/])(?:1[012]|0?[1-9])\1(?:1[6-9]|[2-9]\d)?\d\d(?:(?=\x20\d)\x20|$))?(((0?[1-9]|1[012])(:[0-5]\d){0,2}(\x20[AP]M))|([01]\d|2[0-3])(:[0-5]\d){1,2})?$/;

		r = dateRegex.test(dd + '-' + mm + '-' + yyyy);

	} catch (err) {
		r = false;
	}

	return r;

}

function checkDate() {


	var datatimeRegexp =   /[0-9]{4}-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])/;

    if (!datatimeRegexp.test($("#searchStartDate").val())) {
        alert("날짜는 YYYY-MM-DD 형식으로 입력해주세요.");
        $("#searchStartDate").focus();
        return false;
    }
    
    if (!datatimeRegexp.test($("#searchEndDate").val()) ) {
    	alert("날짜는 YYYY-MM-DD 형식으로 입력해주세요.");
    	$("#searchEndDate").focus();
    	return false;
    }
    
    if (!isValidDate($("#searchStartDate").val()) ) {
    	alert("월에 맞는 날짜를 입력해주세요.");
    	$("#searchStartDate").focus();
    	return false;
    }

    if (!isValidDate($("#searchEndDate").val()) ) {
    	alert("월에 맞는 날짜를 입력해주세요.");
    	$("#searchStartDate").focus();
    	return false;
    }
    
    
    if ($("#searchStartDate").val() > $("#searchEndDate").val()){      
        alert("조회기간의 시작날짜가 종료날짜보다 클수 없습니다.")
        return false
    }      
    

	return true;

}

