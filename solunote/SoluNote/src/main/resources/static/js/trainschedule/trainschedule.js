let prevKeyword = '';

$(function () {
	

	$('select[name="page_count"]').val(getCookie("pageSize"))
	

	//----------
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
	
	//--------------------------------------------
	$(document).on("click", ".tbl_pagination .arrow", function () {
		if ( $(this).hasClass("disabled")) {
			return;
		}
		const clickPageNo = $(this).attr("page-no");
		loadList(clickPageNo);
	})        
	  
	$(document).on("click", ".tbl_pagination .num", function () {
		const clickPageNo = $(this).attr("page-no");
		loadList(clickPageNo);
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
            loadList(1);
        }
    }
	
	$('select[name="page_count"]').change(function() {

    	const pageSize =$('select[name="page_count"]').val()
		if (pageSize != getCookie("pageSize")  ){
			setCookie("pageSize",pageSize,cookeiDayPage)	
		}			    	
        
    	loadList(1)
    });    		

	
	
	//조회 버튼
	$("#search-btn").click(function() {
    	const pageSize =$('select[name="page_count"]').val()
		if (pageSize != getCookie("pageSize")  ){
			setCookie("pageSize",pageSize,cookeiDayPage)	
		}	
    	
    	const pageNoC = $(".tbl_pagination .num.active").attr("page-no");

       
    	
		loadList(pageNoC ?pageNoC : 1);

	});
	
	
	//추가버튼 (popup)
//	$("#create-btn").click(function() {
//		$("#file_register").bPopup({
//			modalClose: false,
//		    opacity: 0.7,
//		    onOpen: function() { $(".tbl_scroll .tbl_list tbody").empty(); }
//		});
//	});
	
    $("#create-btn").on("click", function(){
    	var d = new Date();
	    $("#scheduleDate").datepicker("setDate", d );
	    
        $("#scheduleModalWrap").addClass("active")
    })

    $("#scheduleModalRmCls").on("click", function(){
        $("#scheduleModalWrap").removeClass("active")
    })   
    
    $("#sideUpload").on("click", function () {
    	 $("#modalWrap").addClass("active")
    	 $("#recordStart").css("display","none")
	    
    })
    
    $("#sideRecording").on("click", function () {
	   	 $("#modalWrap").addClass("active")
	   	 $("#recordStart").trigger('click');
	   	$("#changeTitleM").html("실시간 녹음")	   	
  	})     
    

	//추가버튼 (popup 내부)
	$("#scheduleModalRmRegister").click(function() {
		trainAdd(1);	
	});
	

	//추가버튼 (popup 내부 수정)
	$("#act_button_change").click(function() {
		registerChange(queryParams.pageNo ? queryParams.pageNo : 1);	
	});
	
	
	//삭제버튼 
	
    $("#trash-btn").on("click", function(){
    	if($(".check-box.active:not(.all)").length === 0) {
    		alert("선택한 항목이 없습니다.")
    		return false; 
    	}
		let query = "";
		let result = true;
		$(".check-box.active:not(.all)").each(function(idx, el){
			if($(el).closest("tr").data("color") != 1){
				result = false				
			}
			query += ("seq[]=" + $(el).closest("tr").data("seq")) + "&"
		})
		
		if(!result){
    		alert("지나간 학습 예정날짜는 삭제하실 수 없습니다.")
    		return false;			
		}
		
    	if(!confirm("선택된 항목을 학습일정에서 제외하겠습니까?")) {
    		return false;
    	}		
		
		query = query.substring(0, query.length-1);
		
		const pageNoC =$(".tbl_pagination .num.active").attr("page-no");
//        if( $(".check-box.all").hasClass("active") || $("#tableRow tr").length == 1){
//        	pageNoC = pageNoC-1
//        	if(pageNoC == 0){
//        		pageNoC = 1
//        	}        	
//        	
//        }		
		
		fetch("removeList?"+encodeURI(query), {
			method: "POST"
		}).then(res=>{
	    	if ( res.redirected == true ) {
	    		window.location.href = res.url;
	    	} else if(res.ok) {
	    	    return res.text();
	    	} else {
	    		return Promise.reject(res);
	    	}
    	 }).then(text => {
    		 alert("학습일정을 제외  하였습니다.")
    		 loadList(pageNoC)
        }).catch(err=>{
            console.log(err)
            alert("학습일정 제외  실패")
        })    	
    })
	
    
    // popup 클릭시 데이터 
    $(document).on("click", ".page-data-row td", function (){
    	
	  const $this = $(this);
	  
	  const modelId = $this.closest("tr").data("model-id")
	  const seq = $this.closest("tr").data("seq")
	  
	  if(modelId == null){
	  	return false
	  }
	  
	  $("#titleName").text($this.closest("tr").data("name-id"))
//	  modelId = "modelId=" + modelId;
//	  const page = "&page="+ 1;
	  const queryParams =  "modelId=" + modelId
	  						+ "&seq=" + seq;

      trainListPopUp(true, queryParams, 1)
        
    })
    
//    //조회버튼 (popup)
//	$("#btn_search").click(function() {
//        
////        const modelId = "modelId=" + $("#hiddenModelId").val();
////        const page = "&page="+ 1;
////        const orderby = "&orderby="+ $("#orderbyStandard").val();
//        
//        const modelId = "modelId=" + $("#hiddenModelId").val();
//        const seq = "&seq=" + $("#hiddenSeq").val();
//        const page = "&page="+ clickPageNo;
//        const orderby = "&orderby="+ $("#orderBy").val();
//        const queryParams = modelId+seq+page+orderby
//        console.log(queryParams)
//        
//		if ($("#pop_page_count").val() != getCookie("popPageSize")  ){
//			setCookie("popPageSize",$("#pop_page_count").val(),cookeiDayPage)	
//		}
//        trainListPopUp(false, queryParams,1)
//		
//	});
    
//    
//     // popup 페이지조회
//    $(document).on("click", "#popup-footer .page-num-wrapper", function () {
//    	if ( $(this).hasClass("disabled") ) {
//    		return;
//    	}
//        const clickPageNo = $(this).attr("page-no");
//        
//        
//        const modelId = "modelId=" + $("#hiddenModelId").val();
//        const seq = "&seq=" + $("#hiddenSeq").val();
//        const page = "&page="+ clickPageNo;
//        const orderby = "&orderby="+ $("#orderBy").val();
//        let queryParams = modelId+seq+page+orderby
//        
//        $(currentPageName).val(clickPageNo);
//        
//		if ($("#pop_page_count").val() != getCookie("popPageSize")  ){
//			setCookie("popPageSize",$("#pop_page_count").val(),cookeiDayPage)	
//		}        
//        
//        trainListPopUp(false, queryParams, clickPageNo)
//
//    })
    
    
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
    
    $("#search-btn").trigger("click");

})

const queryParams = new Proxy(new URLSearchParams(window.location.search), {
    get: (searchParams, prop) => searchParams.get(prop),
});



function loadList(reqPageNo) {
	
	if(!checkDate()){
		return  false
	}
	
	
    $(currentPageName).val(reqPageNo);

    var params = jQuery(ACTION_FORFM_NAME).serialize(); // serialize() : 입력된 모든Element(을)를 문자열의 데이터에 serialize 한다.

    const {activeMenu} = queryParams
    
    let isSuccess = $("#isSuccess").val()
    
    if(activeMenu == 5){ 
    	isSuccess = 100
    }
    
    const pageSize = getCookie("pageSize")	
    
    const queryParam = "activeMenu=" + activeMenu
    					+"&size=" + pageSize
    					+"&page=" + reqPageNo
    					+"&searchStartDate=" + $(calenderStartName).val()
    					+"&searchEndDate=" + $(calenderEndName).val()
    					+"&isSuccess=" + isSuccess
    
	if (undefined == pageSize)
	{
		pageSize = 10		
	}										
    					
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
         $("#tableTotal").text($("#hiddenCount").val());          
         $("#spanHiddenCount").text($("#hiddenCount").val());  
         $("span.all.check-box").removeClass("active");
         contentPagination( $("#hiddenCount").val(),pageSize);
         $(".check-box.all").removeClass("active")         
    }).catch(err=>{
        console.log(err)
        alert("페이지 갱신 실패")
    })

}

function trainAdd(reqPageNo) {
	
	const scheduleDate = $("#scheduleDate").val();
	const scheduleHour = $("#scheduleHour").val();
	const scheduleMinute = $("#scheduleMinute").val();
    
	const inputHour = scheduleHour < 10 ? '0' + scheduleHour : scheduleHour;
	const inputMinute = scheduleMinute < 10 ? '0' + scheduleMinute : scheduleMinute;
    const inputString = scheduleDate + ' ' + inputHour + ':' + inputMinute;
    
    const today = new Date();
    const trainDay= new Date($("#trainDay").val()); 
    const diffday = (today.getTime() - trainDay.getTime())/(24 * 60 * 60 * 1000);
    const t = today.getTime();
    const tr = trainDay.getTime();
    const diff = today.getTime() - trainDay.getTime();
    const year = today.getFullYear();
    const month = ('0' + (today.getMonth() + 1)).slice(-2);
    const day = ('0' + today.getDate()).slice(-2);

    const hours = ('0' + today.getHours()).slice(-2); 
    const minutes = ('0' + today.getMinutes()).slice(-2);
    const dateString = year + '-' + month  + '-' + day + ' ' + hours + ':' + minutes;
    

    
    if(dateString > inputString){
    	alert("현재보다 이전 시각은 등록할 수 없습니다.")
    	return false;    	
    } 
    
    const dateStringToday = year + '-' + month  + '-' + day
    

    fetch("trainAdd", {
        method: "POST",
        body: JSON.stringify({
            startTime: inputString
        }),
        headers: {
            "Content-Type": "application/json"
//            "Accept": "text/html"
		},
		redirect: "follow" // manual, *follow, error
    }).then(res=>{
    	console.log(res)
    	if ( res.redirected == true ) {
    		window.location.href = res.url;
    	} else if(res.ok) {
    	    return res.text();
    	}else{
    		console.log(Promise.reject(res))
    		return Promise.reject(res);    		
    	}
    }).then(text => {
    	if (text == 2 ){
    		alert("이미 저장된 날짜 또는 시간이 있어 일정추가가 불가능합니다.")
    	}else{
       	 alert("일정을 추가하였습니다");
//       	 $("#trainDay").val(dateStringToday);
//       	 $("#traintime").val("");
       	 loadList(1)   
         $("#modalRmCls").trigger("click");
    	}
    }).catch(err=>{
    		alert("일정을 추가를 실패하였습니다.")
    })

}

function trainListPopUp(isFirst, queryParams, reqPageNo) {

	 const popPageSize = getCookie("popPageSize")	
	 const suffix = isFirst ? "" : "-list";
	 
	 const query  =  queryParams +"&page="+reqPageNo +"&size="+popPageSize + "&suffix=" + suffix;
	    
	  fetch("trainListPopUp?"+encodeURI(query), {  
		  method: "GET",
	  }).then(res=>{
	      if ( res.redirected == true ) {
	          window.location.href = res.url;
	      } else if(res.ok) {
	          return res.text();
	      } else {
	          return Promise.reject(res);
	      }
	  }).then(html => {
		  popupPageNo = Number(reqPageNo);
		  insertUrlParam('pageNo', reqPageNo);
		  
			$("#tablePage").hide();
			
			if ( isFirst ) {
				$(".common_content").append(html);	      
			} else {
		        $("table.tbl_detail").children().remove();
		        $("table.tbl_detail").append(html);   
			}
	      
	      
//	      if ($("#hiddenFail").val() == 1){
//	    	  popupPagination( $("#hiddenCountPopup").val(), popPageSize);  
//	      }else{
//	    	  popupPagination(1, 20);
//	      }

		$(".menu_short").css("z-index","999")
			
		 $('select[name="pop_page_count"]').val(popPageSize);
		
	  	$("#spanPopupCount").text($("#popupCount").val());
	      popupPagination( $("#popupCount").val(), popPageSize);
	      
	      initTrans()
	      
	 }).catch(err=>{
	     console.log(err)
	     alert("페이지 갱신 실패")
	 })

}
