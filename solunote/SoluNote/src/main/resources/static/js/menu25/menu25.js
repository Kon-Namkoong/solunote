let prevKeyword = '';
let reliability = "";

$(function () {
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
	
    $('#page_count').change(function() {

    	const pageSize =$('select[name="page_count"]').val()
		if (pageSize != getCookie("pageSize")  ){
			setCookie("pageSize",pageSize,cookeiDayPage)	
		}			    	
        let pageNoC = $(".tbl_pagination .active").attr("page-no");
        
        if(!pageNoC){
        	pageNoC =1
        }
        
    	loadList(pageNoC)
    });    	
	
    $('#page_count_list').change(function() {

    	const pageSize =$('select[name="page_count_list"]').val()
		if (pageSize != getCookie("popPageSize")  ){
			setCookie("popPageSize",pageSize,cookeiDayPage)	
		}			    	
        let pageNoC = $(".tbl_pagination .active").attr("page-no");

        if(!pageNoC){
        	pageNoC =1
        }
                
    	loadList(pageNoC)
    });     
    
    
	//조회 버튼
	$("#search-btn").click(function() {
		 const {activeMenu} = queryParams
		
		if(activeMenu == 1){
	    	const pageSize =$('select[name="page_count"]').val()
			if (pageSize != getCookie("pageSize")  ){
				setCookie("pageSize",pageSize,cookeiDayPage)	
			}				
		}else{
	    	const pageSize =$('select[name="page_count_list"]').val()
			if (pageSize != getCookie("popPageSize")  ){
				setCookie("popPageSize",pageSize,cookeiDayPage)	
			}					
		}    	

        let pageNoC = $(".tbl_pagination .active").attr("page-no");
        
        if(!pageNoC){
        	pageNoC =1
        }    	
    	
		loadList(pageNoC);

	});

    
    //신규등록
    $("#create-btn").on("click", function(){
//        $("#modalWrap").addClass("active")
//        $("#recordStart").css("display","none")
    	
    	$("#uploadFile").addClass("active")
        uploadMenu = 2    	
    })
    
    $("#uploadFileClose").on("click", function(){
    	$("#uploadFile").removeClass("active")
    	location.reload(true);
    })        
    
    
    $("#modalRmCls").on("click", function(){
        $("#modalWrap").removeClass("active")
    })  
    
    $("#sideUpload").on("click", function () {
    	 $("#modalWrap").addClass("active")
    	 $("#recordStart").css("display","none")
    	 $("#changeTitleM").html("회의록 등록")
    	 uploadMenu = 1    	 
	    
    })
    
    $("#sideRecording").on("click", function () {
	   	 $("#modalWrap").addClass("active")
	   	 $("#recordStart").trigger('click');
	   	 $("#changeTitleM").html("실시간 녹음")
	   	uploadMenu = 1
	    
  	})     
	
 	
    $("#ex-btn").on("click", function(){
        const pageNoC = $(".tbl_pagination .num.active").attr("page-no");
//        if( $(".check-box.all").hasClass("active") || $("#tableRow tr").length == 1){
//        	pageNoC = pageNoC-1
//        	if(pageNoC == 0){
//        		pageNoC = 1
//        	}                 	
//        }
    	exAddProc("N",pageNoC);
    	
    })
    
    
    $("#add-btn").on("click", function(){
        const pageNoC =$(".tbl_pagination .num.active").attr("page-no");
//        if( $(".check-box.all").hasClass("active") || $("#tableRow tr").length == 1){
//        	pageNoC = pageNoC-1
//        	if(pageNoC == 0){
//        		pageNoC = 1
//        	}                 	
//        }
    	exAddProc("Y",pageNoC);
    })
	    

    $("#trash-btn").on("click", function(){
        if($(".check-box.active:not(.all)").length === 0) {
            alert("선택한 항목이 없습니다.")
        } else if(confirm("삭제하시겠습니까?")) {
            let query = "";
            $(".check-box.active:not(.all)").each(function(idx, el){
                query += ("seq[]="+$(el).closest("tr").data("seq"))+"&"
            })
            query = query.substring(0, query.length-1);

            let pageNoC = $(".tbl_pagination .active").attr("page-no");
            if( $(".check-box.all").hasClass("active") || $("#tableRow tr").length == 1){
            	pageNoC = pageNoC-1
            	if(pageNoC == 0){
            		pageNoC = 1
            	}                 	
            }
            
            fetch("excludeTest?"+encodeURI(query), {
                method: "POST"
            }).then(res=>{
                if(res.ok) loadList(pageNoC)
            })
        }
    })
    
    
    $("#rollback-trash-btn").on("click", function(){
        if($(".check-box.active:not(.all)").length === 0) {
            alert("선택한 항목이 없습니다.")
        } else if(confirm("복원하시겠습니까?")) {
            let query = "";
            $(".check-box.active:not(.all)").each(function(idx, el){
                query += ("seq[]="+$(el).closest("tr").data("seq"))+"&"
            })
            query = query.substring(0, query.length-1);
            
            let pageNoC = $("#content-footer .active").attr("page-no");
            if( $(".check-box.all").hasClass("active") || $("#tableRow tr").length == 1){
            	pageNoC = pageNoC-1
            	if(pageNoC == 0){
            		pageNoC = 1
            	}               	
            }            
            
            fetch("trash/rollback?"+encodeURI(query), {
                method: "POST"
            }).then(res=>{
                if(res.ok) loadList(pageNoC)
            })
        }
    })

    
    // pop 리스트 조회
    $(document).on("click", ".page-data-row td:not(.check-box)", function (){
		const $this = $(this).closest("tr");
        $this.find("td").css("color", "#555");
		const seq = "seq="+$this.data("seq");
		const name = "&name="+$this.data("name");
		const page = "&page="+ 1;
		//        location.href = '?activeMenu=4&seq='+seq;
		    
		if($this.data("name") == null){
		return false
		}
		    
		$("#titleName").text($this.data("name")) 
		  
		const queryParams = seq+page
				
		transListPopUp($this,1)
	})

    
    $(document).on("click", ".check-box:not(.speaker-icon)", function () {    	
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
	
    $(currentPageName).val(reqPageNo);
//    $(ACTION_FORFM_NAME).attr("method", "post").submit();
    
    var params = jQuery(ACTION_FORFM_NAME).serialize(); // serialize() : 입력된 모든Element(을)를 문자열의 데이터에 serialize 한다.

    const {activeMenu} = queryParams
    
    let pageSize
    	
	if(activeMenu == 1){
		 pageSize = getCookie("pageSize")
	}else{			
		pageSize = getCookie("popPageSize")
	}
	
	   
    let useYn = $("#useYn").val();
    if ( useYn == undefined ) {
    	useYn = "";
    }
    
    
    const query = "activeMenu="+activeMenu+"&searchText="+$("#search-keyword").val().trim()+"&page="+reqPageNo+"&size="+pageSize+"&searchStartDate="+$(calenderStartName).val()+"&searchEndDate="+$(calenderEndName).val() + "&searchUseYn=" + useYn;
    
    console.log(query)
    
    fetch("loadList?"+encodeURI(query), {
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
         $("#spanHiddenCount").text($("#hiddenCount").val());  
         $("span.all.check-box").removeClass("active");
         contentPagination( $("#hiddenCount").val(), pageSize);
    }).catch(err=>{
        console.log(err)
        alert("페이지 갱신 실패")
    })

}

function  exAddProc(invalidValue,pageNoC) {
	
	let msg = "";
	let query = "";
	
	if($(".check-box.active:not(.all)").length === 0) {
		msg = "선택한 항목이 없습니다.";
	} else {
		$(".check-box.active:not(.all)").each(function(idx, el){
			const tr = $(el).closest("tr");
			if ( tr.children(".use-yn").text() === invalidValue ) {
				if ( invalidValue == "Y" ) {
					msg = "사용여부가 'Y' 인 데이터는 '사용'을 선택할 수 없습니다."
				} else {
					msg = "사용여부가 'N' 인 데이터는 '제외'를 선택할 수 없습니다."
				}
				return false;   // break each loop only
			} else {
				query += ("seq[]=" + tr.data("seq") ) + "&"
			}
		});
	}
	
	if ( msg ) {
		alert(msg);
		return;
	}
	
	if(!confirm("사용여부를 변경하시겠습니까?")) {
		return false
	}
	
	query += "value=" + invalidValue;
	
	fetch("excludeTestCandiate?"+encodeURI(query), {
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
		 console.log("text : [" + text + "]")
		 loadList(pageNoC);
    }).catch(err=>{
        console.log(err)
        alert("학습사용/제외 서버작업 실패")
    })
}

function transListPopUp(tr, reqPageNo) {
	
    let seq;
    let suffix = "";
	
	if ( tr ) {
	    seq = tr.data("seq");
	    reliability = 100;
	} else {
        seq = $("#hiddenSeq").val()
        reliability = $("#reliability").val().trim();
        suffix = "-list";
	}
    
    const popPageSize = getCookie("popPageSize")
    const waveValue = getCookie("waveValue") == 1 ? 1 : 0;
    const changeTextValue = getCookie("changeText") == 1 ? 1 : 0;
    const category = "test";
	
	 const query = "seq="+seq+"&reliability="+reliability+"&page="+reqPageNo+"&size="+popPageSize+"&waveValue="+waveValue+"&category="+category+"&changeTextValue="+changeTextValue+"&suffix=" + suffix;
	  
	 fetch("transListPopUp?"+encodeURI(query), { 
		  method: "GET"
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
		
		if ( tr ) {
			$(".common_content").append(html);	      
		} else {
	        $("table.tbl_detail").children().remove();
	        $("table.tbl_detail").append(html);   
		}
		  
	      let reliability = $("#hiddenReliability").val();
		
	      if (reliability > 100){
	    	  reliability = 100
	      }
		
	      $("#reliability").val(reliability);
		
	      if (reliability != "" && reliability != 0 && reliability != 100){
				$(".rewind").prop("disabled", true);	
				$(".forward").prop("disabled", true);	
				$(".rewind").addClass("rewindChangeImg");	
				$(".forward").addClass("rewindChangeImg");
				$(".rewind").removeClass("rewind");	
				$(".forward").removeClass("forward");	
	      }	      
	     
		  $("#spanPopupCount").text($("#popupCount").val());
			
		  $('select[name="pop_page_count"]').val(popPageSize);
		  
	      popupPagination( $("#popupCount").val(), $("#popupPageSize").val() );
	      
	      $(".menu_short").css("z-index","999")
	      
	      initTrans();
	      
	      if ( waveValue == 1 ) {
				appendWaveSpectrum("appendWaveSpectrum?" + encodeURI(query));
		  }

	 }).catch(err=>{
	     console.log(err)
	     alert("페이지 갱신 실패")
	 })
}
