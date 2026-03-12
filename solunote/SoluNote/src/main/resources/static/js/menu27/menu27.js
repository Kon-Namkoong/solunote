let prevKeyword = '';

var isDefaultAuth = false;
var isUseUser = false;

$(function () {
	
	if(getCookie("searchStartDate") !=null ){
		$("#searchStartDate").val(getCookie("searchStartDate"))
	}

	if(getCookie("searchEndDate") !=null ){
		$("#searchEndDate").val(getCookie("searchEndDate"))		
	}
	calenderStartName = "#searchStartDate";
	calenderEndName = "#searchEndDate";

	// 달력 사용 ()
	setCalenderStartEnd();

    $('#searchStartDate').on('change', function() {
    	setCookie("searchStartDate",$("#searchStartDate").val(),cookieDay);

    });
    
    $('#searchEndDate').on('change', function() {
    	setCookie("searchEndDate",$("#searchEndDate").val(),cookieDay);

    });	    
	
	//당일 데이타 검색	
	$("#searchDateNow").click(function() {
		var d = new Date();
	    $(calenderStartName).datepicker("setDate", d );
	    $(calenderEndName ).datepicker("setDate", d );
	    
		setCookie("searchStartDate",$("#searchStartDate").val(),cookieDay); 
		setCookie("searchEndDate",$("#searchEndDate").val(),cookieDay)	
		
		$('.date_button button').removeClass('active');
	    $(this).addClass("active")		
	    
	});

	//1개월 검색
	$("#searchDateMonth").click(function() {	
		_setCalenderMonthTerm(-1);	

		setCookie("searchStartDate",$("#searchStartDate").val(),cookieDay); 
		setCookie("searchEndDate",$("#searchEndDate").val(),cookieDay)	
		
		$('.date_button button').removeClass('active');
	    $(this).addClass("active")		
		
		
		
	});
		
	//1년 검색(당해년도)
	$("#searchDateyear").click(function() {
		_setCalenderMonthTerm(-12);	
		
		setCookie("searchStartDate",$("#searchStartDate").val(),cookieDay); 
		setCookie("searchEndDate",$("#searchEndDate").val(),cookieDay)	
		
		
		$('.date_button button').removeClass('active');
	    $(this).addClass("active")		
	});	
	
    $("#sideUpload").on("click", function () {
   	 $("#modalWrap").addClass("active")
   	 $("#recordStart").css("display","none")
	    
   })
   
   $("#sideRecording").on("click", function () {
	   	 $("#modalWrap").addClass("active")
	   	 $("#recordStart").trigger('click');
	   	$("#changeTitleM").html("실시간 녹음")	   	
	    
 	}) 	
	
	$(document).on("click", ".tbl_pagination .arrow", function () {
		if ( $(this).hasClass("disabled")) {
			return;
		}
		const clickPageNo = $(this).attr("page-no");
		
    	const {activeMenu} = queryParams
    	
    	if( activeMenu == 1 || activeMenu == 2 ){
            loadList(clickPageNo);	
    	}else if ( activeMenu == 4){
    		settingList(clickPageNo);
    	}else if (activeMenu == 5){
    		
    	} 
	})        
	  
	$(document).on("click", ".tbl_pagination .num", function () {
		const clickPageNo = $(this).attr("page-no");
		
    	const {activeMenu} = queryParams
    	
    	if( activeMenu == 1 || activeMenu == 2 ){
            loadList(clickPageNo);	
    	}else if ( activeMenu == 4){
    		settingList(clickPageNo);
    	}else if (activeMenu == 5){
    		
    	} 
		
	})

	//--------------------------------------------
    $("#search-keyword + label > svg").on("click", function(){
        $("#search-keyword").val('');
    })
    $("#search-keyword").on("blur", function (e) {
        searchKeyword();
    })
    $("#search-keyword").on("keypress", function (event) {
        if ( event.keyCode == 13 || event.which == 13 ) {
            searchKeyword();
        }
    })
    
    function searchKeyword(){
        if($("#search-keyword").val().trim() !== prevKeyword) {
        	const {activeMenu} = queryParams
        	if( activeMenu == 1 || activeMenu == 2 ){
                loadList(1);	
        	}else if ( activeMenu == 4){
        		settingList(1);
        	}else if (activeMenu == 5){
        		
        	}        
        }
    }	
	
    $('#page_count').change(function() {

    	const pageSize =$('select[name="page_count"]').val()
		if (pageSize != getCookie("pageSize")  ){
			setCookie("pageSize",pageSize,cookeiDayPage)	
		}			    	
        let pageNoC = $(".tbl_pagination .active").attr("page-no");
        
        if(!pageNoC){
        	pageNoC =1
        }        
        
    	const {activeMenu} = queryParams
    	if( activeMenu == 1 || activeMenu == 2 ){
            loadList(pageNoC);	
    	}else if ( activeMenu == 4){
    		settingList(pageNoC);
    	}else if (activeMenu == 5){
    		
    	}  
    });     
	
    //신규등록
    $("#btn_several").on("click", function(){    	
    	createSeveral()        
    })    
    
	
	//조회 버튼
	$("#search-btn").click(function() {
    	const pageSize =$('select[name="page_count"]').val()
		if (pageSize != getCookie("pageSize")  ){
			setCookie("pageSize",pageSize,cookeiDayPage)	
		}		
    	
        let pageNoC = $(".tbl_pagination .active").attr("page-no");
    	
        if(!pageNoC){
        	pageNoC =1
        }     	
    	
    	const {activeMenu} = queryParams
    	if( activeMenu == 1 || activeMenu == 2 ){
            loadList(pageNoC);	
    	}else if ( activeMenu == 4){
    		settingList(pageNoC);
    	}else if (activeMenu == 5){
    		
    	}         	
    		
	});
	
	// 계정등록 페이지 전환
	$(document).on("click", "#btn_register", function () {
		
    	const {activeMenu} = queryParams
    	if( activeMenu != 4){
    		$("#tablePage").hide();
    		$("#userReg").show();
    	}else{
    		$("#tablePage").hide();
    		$("#settingReg").show();
    	}  		
		

	})
	
    $(document).on('click', '.back_btn', function () {    	
    	const {activeMenu} = queryParams
    	if( activeMenu != 4){
            $('#tcId').val("")
            $('#tcPw').val("")
            $('#tcPw_cm').val("")
            $('#tcName').val("")
            $('#tcEmail').val("")
            $('#tcPhone').val("")
            $('#tcLevel').val("")    		
        	$("#userReg").hide();
        	$("#tablePage").show();
    	}else{    		
    		$("#settingReg").hide();
    		$("#tablePage").show();
    	}     	
    });
	
    $(document).on('click', '.back_btn.Change', function () {
    	const {activeMenu} = queryParams
    	if( activeMenu != 4){
        	$("#userRegChange").remove();
        	$("#tablePage").show();
    	}else{
    		$("#settingRegChange").remove();
    		$("#tablePage").show();
    	}         	
    });		
	
	//중복 체크 버튼	
    $(document).on('click', '.btn_idcheck', function () {
    	 	    	
    	checkId()    		    	
    	   	    	
    });	
	    
	//계정 등록 실행
    $(document).on('click', '#btn_regUser', function () {
    	
    	regUser()
    	   	    	
    });		
    
    
    //전화번호 - 자동 입력
    $('#tcPhone').on('input', function() {
        let input = $(this).val().replace(/-/g, ''); 
        let formattedInput = '';

        // 10자리 또는 11자리의 전화번호 형식에 맞춰 '-' 삽입
        if (input.length < 4) {
            formattedInput = input;
        } else if (input.length < 7) {
            formattedInput = input.slice(0, 3) + '-' + input.slice(3);
        } else if (input.length < 11) {
            formattedInput = input.slice(0, 3) + '-' + input.slice(3, 7) + '-' + input.slice(7);
        } else {
            formattedInput = input.slice(0, 3) + '-' + input.slice(3, 7) + '-' + input.slice(7, 11);
        }

        if (input.length > 13) {
            formattedInput = formattedInput.slice(0, 13); 
        }        
        
        $(this).val(formattedInput);
        
    });    
    
    $(document).on('input', '#tcPhone-change', function () {
        let input = $(this).val().replace(/-/g, ''); 
        let formattedInput = '';

        // 10자리 또는 11자리의 전화번호 형식에 맞춰 '-' 삽입
        if (input.length < 4) {
            formattedInput = input;
        } else if (input.length < 7) {
            formattedInput = input.slice(0, 3) + '-' + input.slice(3);
        } else if (input.length < 11) {
            formattedInput = input.slice(0, 3) + '-' + input.slice(3, 7) + '-' + input.slice(7);
        } else {
            formattedInput = input.slice(0, 3) + '-' + input.slice(3, 7) + '-' + input.slice(7, 11);
        }

        if (input.length > 13) {
            formattedInput = formattedInput.slice(0, 13); 
        }        
        
        $(this).val(formattedInput);
        
    });        
                
	// 계정 수정	
    $(document).on('click', '#btn_regUser_change', function () {
    	
    	changeDetailUser()
    	   	    	
    });			
						
    $(document).on("click", ".page-data-row td:not(.check-box)", function (){
    	
    	const {activeMenu} = queryParams
    	if (activeMenu ==1 || activeMenu == 3){
    		const userId = $(this).closest("tr").data("userid")
        	userinfo(userId);        	   
    	}else if (activeMenu == 4){
    		
    		const settingTitle = $(this).closest("tr").data("configname")
    		
    		settingInfo(settingTitle);
    		    		
    	}
          
      })

	//계정 제외 사용 버튼 
    $("#btn_del").on("click", function(){
    	const {activeMenu} = queryParams
    	if($(".check-box.active:not(.all)").length === 0) {
    		alert("선택한 항목이 없습니다.")
    		return false
    	} 
    	
    	if (activeMenu==1){
    		if(!confirm("선택된 계정을 사용하시지 않겠습니까?")) {
    			return false
    		}
    	}else if (activeMenu==2){
    		if(!confirm("선택된 계정을 다시 사용하시겠습니까?")) {
    			return false
    		}    		
    	}

    		let query = "";
    		$(".check-box.active:not(.all)").each(function(idx, el){
    			query += ("userId=" + $(el).closest("tr").data("userid")) + "&"
    		})
    		  		
            let pageNoC = $(".tbl_pagination .active").attr("page-no");
            if( $(".check-box.all").hasClass("active") || $("#tableRow tr").length == 1){
            	pageNoC = pageNoC-1
            	if(pageNoC == 0){
            		pageNoC = 1
            	}               	
            }   		
    		
    		const queryParam = "activeMenu="+activeMenu
    		    		
    		fetch("removeId?"+encodeURI(queryParam), {
    			method: "POST",
    			body: query,
    	        headers: {
    	            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
    	            "Accept": "text/html"
    			},    			
    		}).then(res=>{
    	    	if ( res.redirected == true ) {
    	    		window.location.href = res.url;
    	    	} else if(res.ok) {
    	    	    return res.text();
    	    	} else {
    	    		return Promise.reject(res);
    	    	}
	    	 }).then(text => {
	    	    if (activeMenu==1){
	    	    	alert("계정을 제외하였습니다.")
	    	    }else if (activeMenu==2){
	    	    	alert("계정을 다시 등록하였습니다.")    		
	    	    }
	    		 loadList(pageNoC)
	        }).catch(err=>{
	    	    if (activeMenu==1){
	    	    	alert("계정제외에 실패하였습니다 관리자에게 문의하세요")
	    	    }else if (activeMenu==2){
	    	    	alert("계정등록에 실패하였습니다 관리자에게 문의하세요")		
	    	    }
	        })

    })      
            	   
	//계정 제외 사용 버튼 
    $("#btn_delP").on("click", function(){
    	const {activeMenu} = queryParams
    	if($(".check-box.active:not(.all)").length === 0) {
    		alert("선택한 항목이 없습니다.")
    		return false
    	} 

    	
		if(!confirm("선택된 계정을 영구삭제 하시겠습니까?")) {
			return false
		}    	
    	
		let query = "";
		$(".check-box.active:not(.all)").each(function(idx, el){
			query += ("userId=" + $(el).closest("tr").data("userid")) + "&"
		})
		  		
        let pageNoC = $(".tbl_pagination .active").attr("page-no");
        if( $(".check-box.all").hasClass("active") || $("#tableRow tr").length == 1){
        	pageNoC = pageNoC-1
        	if(pageNoC == 0){
        		pageNoC = 1
        	}               	
        }   		
		
		const queryParam = "activeMenu="+activeMenu
		    		
		fetch("permanentlyId?"+encodeURI(queryParam), {
			method: "POST",
			body: query,
	        headers: {
	            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
	            "Accept": "text/html"
			},    			
		}).then(res=>{
	    	if ( res.redirected == true ) {
	    		window.location.href = res.url;
	    	} else if(res.ok) {
	    	    return res.text();
	    	} else {
	    		return Promise.reject(res);
	    	}
    	 }).then(text => {
    		 alert("계정을 영구삭제 하였습니다.")   
    		 loadList(pageNoC)
        }).catch(err=>{
        	alert("영구삭제에 실패하였습니다 관리자에게 문의하세요")		
        })

    })      
    

    $(document).on("click", ".check-box", function () {

        const $this = $(this);

        if($this.hasClass("active")) {
            $this.removeClass("active")
            if($this.hasClass("all")) {
                $(".check-box").removeClass("active")
            } else {
                $(".check-box.all").removeClass("active")
            }
        } else {
            $this.addClass("active")
            if($this.hasClass("all")) {
                $(".check-box").addClass("active")
            } else if($(".check-box:not(.active):not(.all)").length === 0) {
                $(".check-box.all").addClass("active")
            } else {
                $(".check-box.all").removeClass("active")
            }
        }

    })    
    
    
    //삭제일정 설정 등록 
    $("#btn_regSetting").click( function(){    _settingDetailRegister()    ;});    
    
    
    // 파일관리 기간입력시 숫자만 + 마지막에 일추가
    $('#settingValue').on('input', function() {
        const inputVal = $(this).val();

        if (/\D/.test(inputVal)) {
            $('#checkValue').text('* 숫자만 입력할 수 있습니다.').show();
        }else{
        	$('#checkValue').hide();
        }        
                
        const onlyNumbers = inputVal.replace(/\D/g, '');
        $(this).val(onlyNumbers);
    });
    
 

    $(document).on("input", "#settingValueChange", function () {
        const inputVal = $(this).val();

        if (/\D/.test(inputVal)) {
        	console.log($('#valueChange').text())
            $('#valueChange').text('* 숫자만 입력할 수 있습니다.').css("display","inline");
        }else{
        	$('#valueChange').css("display","none");
        }        
                
        const onlyNumbers = inputVal.replace(/\D/g, '');
        $(this).val(onlyNumbers);
    });
    

    
            
    //삭제일정 설정 등록
    _settingDetailRegister = function() {
            
    	const settingTitle =$("#settingTitle").val()
        const settingValue =$("#settingValue").val()

        if(settingTitle == null || settingTitle == ""){
            $("#settingTitle").focus();
    		$("#checkTitle").text("파일관리 제목을 입력해주세요.").show()                  	
            return false;
        }
                
        if(settingValue == null || settingValue == ""){
            $("#settingValue").focus();
    		$("#checkValue").text("시간을 입력해주세요").show() 
            return false;
        }
        
        _checkSettingTitle(settingTitle)
        
    }    
         

    
    //삭제일정 설정 등록 중복체크    
    _checkSettingTitle = function(settingTitle) {
        
        var formData = new FormData();
        formData.append("settingTitle", settingTitle);
        _callAjax(formData, _resultCheckTitle, "/menu27/cont/checkSetting", "POST", _error);
        
    };
    
    //삭제일정 설정 등록
    _resultCheckTitle = function(data)  { 
    	
        if(data != 0) {
            $("#settingTitle").focus();
    		$("#checkTitle").text("* 같은제목이있습니다 다른제목을 입력해주세요.").show()    
        }else{
            
        	const settingTitle =$("#settingTitle").val()
            const settingValue =$("#settingValue").val()
            const settingUseYn =$("#settingUseYn").val()
            const settingDetail =$("#settingDetail").val()
            
            
            
            const {activeMenu} = queryParams
            const queryParam = "activeMenu="+activeMenu
            
            let params = "settingTitle="+settingTitle+"&settingValue="+settingValue+"&settingUseYn="+settingUseYn+"&settingDetail="+settingDetail;
                        
                fetch("settingRes?"+encodeURI(queryParam), {
                    method: "POST",
                    body: params,
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
                        "Accept": "text/html"
                    },
                    redirect: "follow" // manual, *follow, error
                }).then(res=>{
                    if ( res.redirected == true ) {
                        window.location.href = res.url;
                    } else if(res.ok) {
                        return res.text();
                    } else {
                        return Promise.reject(res);
                    }
                 }).then(text => {
                     alert("등록을 완료하였습니다.")                     
                     $("#settingTitle").val("")
                     $("#settingValue").val("")
                     $("#settingDetail").val("")
                     $("#checkTitle").hide() 
                     $("#checkValue").hide() 
                     $("#settingReg").hide();
             		 $("#tablePage").show();                     
             		 settingList(1)
                }).catch(err=>{
                    alert("설정 등록에 실패하였습니다 관리자에게 문의하세요")        
                })            
        }
    };        
	
    //삭제일정 설정 등록 수정
    $(document).on("click", "#btn_regSettingChange", function () {

        let pageNoC = $(".tbl_pagination .active").attr("page-no");
        if( $(".check-box.all").hasClass("active") || $("#tableRow tr").length == 1){
        	pageNoC = pageNoC-1
        	if(pageNoC == 0){
        		pageNoC = 1
        	}               	
        }	
     
		const settingTitleChange =$("#settingTitleChange").val()
        const settingValueChange =$("#settingValueChange").val()
        const settingUseYnChange =$("#settingUseYnChange").val()
        const settingDetailChange =$("#settingDetailChange").val()
               
        if(settingValueChange == null || settingValueChange == ""){
        	$("#valueChange").text("* 설정하실 기간을 입력해주세요.").show() 
            return false;
        }
        
		if(!confirm("설정을 수정하시겠습니까?")) {
			return false
		}
		
        const {activeMenu} = queryParams
        const queryParam = "activeMenu="+activeMenu
        
        console.log(settingUseYnChange)
        
        
        let params = "settingTitleChange="+settingTitleChange+"&settingValueChange="+settingValueChange+"&settingUseYnChange="+settingUseYnChange+"&settingDetailChange="+settingDetailChange;
           
            fetch("settingInfo_change?"+encodeURI(queryParam), {
                method: "POST",
                body: params,
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
                    "Accept": "text/html"
                },
                redirect: "follow" // manual, *follow, error
            }).then(res=>{
                if ( res.redirected == true ) {
                    window.location.href = res.url;
                } else if(res.ok) {
                    return res.text();
                } else {
                    return Promise.reject(res);
                }
             }).then(text => {
                 alert("수정을 완료하였습니다.")  
                 $("#settingRegChange").remove()
                 $("#tablePage").show()
                 settingList(pageNoC)
            }).catch(err=>{
                alert("삭제일정 등록에 실패하였습니다 관리자에게 문의하세요")        
            })        
        
		
	});
	
	
    //삭제일정 설정 등록 삭제
	$("#btn_delM").click(function() {
    	
		
		
    	if($(".check-box.active:not(.all)").length === 0) {
    		alert("선택한 항목이 없습니다.")
    		return false
        }    	

		if(!confirm("설정을 삭제하시겠습니까?")) {
			return false
		}
    	
		let query = "";
		$(".check-box.active:not(.all)").each(function(idx, el){
			query += ("settingTitle=" + $(el).closest("tr").data("configname")) + "&"
		})
		
        let pageNoC = $(".tbl_pagination .active").attr("page-no");
        if( $(".check-box.all").hasClass("active") || $("#tableRow tr").length == 1){
        	pageNoC = pageNoC-1
        	if(pageNoC == 0){
        		pageNoC = 1
        	}               	
        }
        
        const {activeMenu} = queryParams
        const queryParam = "activeMenu="+activeMenu    	
	
    		fetch("settingInfo_delete?"+encodeURI(queryParam), {
    			method: "POST",
    			body: query,
    	        headers: {
    	            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
    	            "Accept": "text/html"
    			},    			
    		}).then(res=>{
    	    	if ( res.redirected == true ) {
    	    		window.location.href = res.url;
    	    	} else if(res.ok) {
    	    	    return res.text();
    	    	} else {
    	    		return Promise.reject(res);
    	    	}
	    	 }).then(text => {
	    		 alert("설정을 삭제하였습니다.")
	    		 settingList(pageNoC)
	        }).catch(err=>{
	        	alert("설정 삭제에 실패하였습니다 관리자에게 문의하세요")
	        })		
		
		
	});    
    

    $("#search-btn").trigger("click");
    
})

const queryParams = new Proxy(new URLSearchParams(window.location.search), {
    get: (searchParams, prop) => searchParams.get(prop),
});


// 계정 목록 
function loadList(reqPageNo) {
        
    const search_type = $("#search_type").val()
    const userType = $("#userType").val()
        
    let tcId = ""
    let tcName = "" 
    let tcPhone = ""
    if (search_type == 1){
    	tcId = $("#search-keyword").val()
    }else if (search_type == 2){
    	tcName = $("#search-keyword").val()
    }else{
    	tcPhone = $("#search-keyword").val()
    }
	
    const	pageSize = getCookie("pageSize")
	
    
    const params = "page="+reqPageNo+"&size="+pageSize+"&userType="+userType+"&tcId="+tcId+"&tcName="+tcName+"&tcPhone="+tcPhone+"&searchStartDate="+$(calenderStartName).val()+"&searchEndDate="+$(calenderEndName).val()  ; 

    const {activeMenu} = queryParams

    const queryParam = "activeMenu="+activeMenu + "&" + params;
  
    
    fetch("loadList?"+encodeURI(queryParam), {
        method: "GET",

		redirect: "follow" // manual, *follow, error
    }).then(res=>{
    	if ( res.redirected == true ) {
    		window.location.href = res.url;
    	} else if(res.ok) {
    	    return res.text();
    	} else {
    		return Promise.reject(res);
    	}
    }).then(html => {
    	pageNo = Number(reqPageNo);
        insertUrlParam('pageNo', reqPageNo);
        $(".tbl_scroll tbody").children().remove();
        $(".tbl_scroll tbody").append(html);
        contentPagination( $("#hiddenCount").val(),pageSize);
        $("#tableTotal").text($("#hiddenCount").val());  
        $(".check-box.all").removeClass("active")
    }).catch(err=>{
        alert("페이지 갱신 실패")
    })

}

function checkId() {

	const tcId =$("#tcId").val();   
	
	if(!_checkIdFormat(tcId)){
		$("#checkId").text("*4~12자의 영문 대소문자와 숫자 로만 입력해주세요")
		$("#checkId").css("display","block")
		$("#tcId").val(""); 
		return;
	}
	
	const params = "code="+tcId
	    	
	fetch("check", {
		method: "POST",
		body: params,
        headers: {
            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
            "Accept": "text/html"
		},    			
	}).then(res=>{
    	if ( res.redirected == true ) {
    		window.location.href = res.url;
    	} else if(res.ok) {
    	    return res.text();
    	} else {
    		return Promise.reject(res);
    	}
	 }).then(data => {
		 if(data == -1){
			 $("#checkId").text("* 사용가능한 아이디 입니다.")
			 $("#checkId").css("display","block")
			 isUseUser = true;
		 } else if(data == 0 || data == 3){
				 $("#checkId").text("* (영구)삭제된 아이디입니다. 재사용할 수 없습니다.")
				 $("#checkId").css("display","block")
				 isUseUser = false; 
		 }else{
			 $("#checkId").text("* 사용중인 아이디가 있습니다. 다른 아이디를 입력해주세요.")
			 $("#checkId").css("display","block")
			 isUseUser = false; 
		 }
    }).catch(err=>{        	
    	$("#checkId").text("* 확인에 실패하였습니다. 관리자에게 문의해주세요.")
    	$("#checkId").css("display","block")
    })	 
}


function regUser() {

	if (!isUseUser) {
		$("#checkId").text("아이디 중복여부를 확인 해주세요.")
		$("#checkId").css("display","block")
		return false;
	}
	
	const s_name = $("#tcName").val();
    if (s_name == undefined || s_name == "") {    	
    	$("#tcName").focus();
    	$("#checkName").css("display","block")
    	return false;
    }	
	
	if($("#tcPw").val() == '') {
		$("#checkPw").text("* 비밀번호를 입력해주세요.")
    	$("#tcPw").focus();
		$("#checkPw").css("display","block")
		return false;
	}

	if($("#tcPw_cm").val() == '') {
    	$("#tcPw_cm").focus();
		$("#checkPwCm").css("display","block")
		return false;
	}	
	
		
	if($("#tcPw").val() != '')
		if(!fnCheckCompPass($("#tcId").val(), $("#tcPw").val(), $("#tcPw_cm").val())) return false;
	
    
	if ($("#tcEmail").val().trim() !== "") { // 빈 값이 아닌 경우에만 체크
	    if (!checkEmailFormat($("#tcEmail").val())) {
	        $("#tcEmail").focus();
	        $("#checkEmail").text("이메일 주소가 유효하지 않습니다.");
	        $("#checkEmail").css("display", "block");
	        return false;
	    }
	}
//    
//	if(!checkPhoneFormat($("#tcPhone").val())) {
//        $('#checkPhone').css("display","block")
//		return false;
//	}
		

    const formData = {
            tcId: $('#tcId').val(),
            tcPw: $('#tcPw').val(),
            tcName: $('#tcName').val(),
            tcEmail: $('#tcEmail').val(),
            tcPhone: $('#tcPhone').val(),
            tcLevel: $('#tcLevel').val()
        };

        fetch('insert', {
            method: 'POST',
            body: new URLSearchParams(formData),
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                'Accept': 'application/json'
            }
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                return Promise.reject('서버 에러: ' + response.status);
            }
        })
        .then(data => {
        	alert("계정 등록을 완료하였습니다.")
            $('#tcId').val("")
            $('#tcPw').val("")
            $('#tcPw_cm').val("")
            $('#tcName').val("")
            $('#tcEmail').val("")
            $('#tcPhone').val("")
            $('#tcLevel').val("")
            $("#checkId").hide()
            $("#checkName").hide()
            $("#checkPw").hide()
            $("#checkPwCm").hide()
            $("#checkPhone").hide()
            $("#checkEmail").hide()
            $("#userReg").hide();
            $("#tablePage").show();
            loadList(1)
        })
        .catch(error => {
        	alert("계정 등록을 실패하였습니다. 관리자에게 문의바랍니다.")
        });	
	
}



function userinfo(userId) {

    const {activeMenu} = queryParams
    	
    const queryParam = "userId="+userId+"&activeMenu="+activeMenu
    
    
    fetch("userinfo?"+encodeURI(queryParam), {
        method: "GET",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
            "Accept": "text/html"
		},
		redirect: "follow" // manual, *follow, error
    }).then(res=>{
    	if ( res.redirected == true ) {
    		window.location.href = res.url;
    	} else if(res.ok) {
    	    return res.text();
    	} else {
    		return Promise.reject(res);
    	}
    }).then(html => {
		$("#tablePage").hide();
		$(".common_content").append(html);
		$("#userRegChange").remove();

    }).catch(err=>{
        alert("유저 조회를 실패했습니다 관리자에게 문의해주세요.")
    })   
}

function changeDetailUser() {

	if($("#tcPw-change").val() != '' || $("#tcPw_cm-change").val() != '')
		if(!fnCheckCompPassChange($("#tcId-change").val(), $("#tcPw-change").val(), $("#tcPw_cm-change").val())) return false;
	
	if ($("#tcEmail-change").val().trim() !== "") { // 빈 값이 아닌 경우에만 체크
	    if (!checkEmailFormat($("#tcEmail-change").val())) {
			$("#checkEmailChange").css("display","block")
	        $("#tcEmail-change").focus();
	        return false;
	    }
	}	
    
//    	
//	if(!checkPhoneFormat($("#tcPhone-change").val())) {
//		$("#checkPhoneChange").css("display","block")
//		return false;
//	}
	
	if(!confirm("계정을 수정하시겠습니까?")) {
		return false
	}

	const {activeMenu} = queryParams
    const queryParam = "activeMenu="+activeMenu
    
    const tcId =$("#tcId-change").text()
	const tcName =$("#tcName-change").val()
	const tcPw =$("#tcPw-change").val()
	const tcPhone =$("#tcPhone-change").val()
	const tcEmail =$("#tcEmail-change").val()
	let tcLevel
	if(activeMenu ==3){
		 tcLevel = $("#tcLevel-change").attr("value");
	}else{
		 tcLevel =$("#tcLevel-change").val()	
	}
	
	const params = "tcId="+tcId+"&tcName="+tcName+"&tcPw="+tcPw+"&tcPhone="+tcPhone +"&tcEmail="+tcEmail +"&tcLevel="+tcLevel  ;
    	
	
		fetch("userinfo?"+encodeURI(queryParam), {
			method: "POST",
			body: params,
	        headers: {
	            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
	            "Accept": "text/html"
			},
			redirect: "follow" // manual, *follow, error
		}).then(res=>{
	    	if ( res.redirected == true ) {
	    		window.location.href = res.url;
	    	} else if(res.ok) {
	    	    return res.text();
	    	} else {
	    		return Promise.reject(res);
	    	}
    	 }).then(text => {
    		 alert("수정을 완료하였습니다.") 
    		 
    		 if(activeMenu !=3){
        		 $("#userRegChange").remove();
        		 $("#tablePage").show();
        		 loadList(1)    			 
    		 }else{
    			 location.reload(true);
    		 }
    		 
        }).catch(err=>{
        	alert("계정수정에 실패하였습니다 관리자에게 문의하세요")		
        }) 
}


function settingList(reqPageNo) {
    
    const searchType = $("#serachType").val()
    const useYn = $("#useYn").val()
    const searchKeyword =$("#search-keyword").val()
        
    const	pageSize = getCookie("pageSize")
    
    const params = "page="+reqPageNo+"&size="+pageSize+"&useYn="+useYn+"&searchType="+searchType+"&searchKeyword="+searchKeyword+"&searchStartDate="+$(calenderStartName).val()+"&searchEndDate="+$(calenderEndName).val()  ; 

    const {activeMenu} = queryParams

    const queryParam = "activeMenu="+activeMenu + "&" + params;
    
	
    fetch("settingList?"+encodeURI(queryParam), {
        method: "GET",

		redirect: "follow" // manual, *follow, error
    }).then(res=>{
    	if ( res.redirected == true ) {
    		window.location.href = res.url;
    	} else if(res.ok) {
    	    return res.text();
    	} else {
    		return Promise.reject(res);
    	}
    }).then(html => {
    	pageNo = Number(reqPageNo);
        insertUrlParam('pageNo', reqPageNo);
        $(".tbl_scroll tbody").children().remove();
        $(".tbl_scroll tbody").append(html);
        contentPagination( $("#hiddenCount").val(),pageSize);
        $("#tableTotal").text($("#hiddenCount").val());  
        $(".check-box.all").removeClass("active")
    }).catch(err=>{
        alert("페이지 갱신 실패")
    })

}


function settingInfo(settingTitle) {

    const {activeMenu} = queryParams
    
    console.log(settingTitle)
	
    const queryParam = "settingTitle="+settingTitle
        
    fetch("settingRes?"+encodeURI(queryParam), {
        method: "GET",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
            "Accept": "text/html"
		},
		redirect: "follow" // manual, *follow, error
    }).then(res=>{
    	if ( res.redirected == true ) {
    		window.location.href = res.url;
    	} else if(res.ok) {
    	    return res.text();
    	} else {
    		return Promise.reject(res);
    	}
    }).then(html => {
		$("#tablePage").hide();
		$(".common_content").append(html);
		$("#settingRegChange").remove();

    }).catch(err=>{
        alert("파일관리 상세조회를 실패했습니다 관리자에게 문의해주세요.")
    })   
}


function createSeveral() {
	
	fetch("createSeveral?", {
		  method: "GET"
	}).then(res => {
		if (res.redirected == true) {
			window.location.href = res.url;
		} else if (res.ok) {
			return res.text();
		} else {
			return Promise.reject(res);
		}
	}).then(html => {
		
    	$("#tablePage").hide();

    	$(".common_content").append(html);
    	    	
		$("#uploadKeyword").remove();
			
	}).catch(err => {
		console.log(err)
		alert("페이지 갱신 실패")
	})
}
