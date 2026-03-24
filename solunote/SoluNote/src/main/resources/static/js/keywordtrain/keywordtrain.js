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

    	const pageSize = $('select[name="page_count"]').val()
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

    	const pageSize = $('select[name="page_count_list"]').val()
		
		if (pageSize != getCookie("popPageSize")  ){
			setCookie("popPageSize",pageSize,cookeiDayPage)	
		}			    	
        let pageNoC = $(".tbl_pagination.active").attr("page-no");
				
        if(!pageNoC){
        	pageNoC =1
        }
      	loadList(pageNoC)
    });    
	
	
	//조회 버튼
	$("#search-btn").click(function() {
		
		const {activeMenu} = queryParams
		
		if(activeMenu == 1 || activeMenu == 4 ){
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
    	
		loadList(queryParams.pageNo ? queryParams.pageNo : 1);

	});
	
    //신규등록
    $("#create-btn").on("click", function(){    	
    	createKeyword()        
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
    
	//조회 버튼
	$("#sendtest-btn").click(function() {
        if($(".check-box.active:not(.all)").length === 0) {
            alert("선택한 항목이 없습니다.")
        } else if(confirm("테스트Datat생성 항목으로 전송하시겠습니까?")) {
            let array = [];
            $(".check-box.active:not(.all)").each(function(idx, el){
            	let seq = $(el).closest("tr").data("seq");
            	const item = {
            			seq :  seq
            	}
                array.push( item );
            })
            var params = JSON.stringify(array);
            
            let pageNoC = $(".tbl_pagination .active").attr("page-no");
            if( $(".check-box.all").hasClass("active") || $("#tableRow tr").length == 1){
            	pageNoC = pageNoC-1
            	if(pageNoC == 0){
            		pageNoC = 1
            	}               	
            }
                           
            fetch("sendtest", {
                method: "POST",
                headers: {
                     "Content-Type": "application/json",
                  },
                body: params,
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
            	loadList(pageNoC,"")
			}).catch(err=>{
				console.log(err)
				alert("페이지 갱신 실패")
			})
        }

	});
   
	

    $("#delete-btn").on("click", function(){
    	
        if($(".check-box.active:not(.all)").length === 0) {
            alert("선택한 항목이 없습니다.")
        } else if(confirm("영구삭제하시겠습니까?")) {
            let array = [];
            $(".check-box.active:not(.all)").each(function(idx, el){
            	let seq = $(el).closest("tr").data("seq");
            	const item = {
            			seq :  seq
            	}
                array.push( item );
            })
            
            let pageNoC = $(".tbl_pagination .active").attr("page-no");
            if( $(".check-box.all").hasClass("active") || $("#tableRow tr").length == 1){
            	pageNoC = pageNoC-1
            	if(pageNoC == 0){
            		pageNoC = 1
            	}               	
            }
            
            
            var params = JSON.stringify(array);
//            var params = "array[]=" + seqs.join();            

            fetch("delete", {
                method: "POST",
                headers: {
                     "Content-Type": "application/json",
//                     "Content-Type": "application/x-www-form-urlencoded",
                  },
                body: params,
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
            	loadList(pageNoC)
			}).catch(err=>{
				console.log(err)
				alert("페이지 갱신 실패")
			})
        }
    })
    
    
    $("#trash-btn").on("click", function(){

        if($("#tableRow .check-box.active:not(.all)").length === 0) {
            alert("선택한 항목이 없습니다.")
        } else if(confirm("삭제하시겠습니까?")) {
            let array = [];
            $("#tableRow .check-box.active:not(.all)").each(function(idx, el){
            	let seq = $(el).closest("tr").data("seq");
            	const item = {
            			seq :  seq
            	}
                array.push( item );
            })
            
            let pageNoC = $(".tbl_pagination .active").attr("page-no");
            if( $(".check-box.all").hasClass("active") || $("#tableRow tr").length == 1){
            	pageNoC = pageNoC-1
            	if(pageNoC == 0){
            		pageNoC = 1
            	}               	
            }
            
            
            var params = JSON.stringify(array);      

            console.log(params)
            
            fetch("trash", {
                method: "POST",
                headers: {
                     "Content-Type": "application/json",
                  },
                body: params,
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
            	loadList(pageNoC)
			}).catch(err=>{
				console.log(err)
				alert("페이지 갱신 실패")
			})
        }
    })
    
    
    $("#rollback-trash-btn").on("click", function(){
        if($(".check-box.active:not(.all)").length === 0) {
            alert("선택한 항목이 없습니다.")
        } else if(confirm("복원하시겠습니까?")) {
            let array = [];
            $(".check-box.active:not(.all)").each(function(idx, el){
            	let seq = $(el).closest("tr").data("seq");
            	const item = {
            			seq :  seq
            	}
                array.push( item );
            })
            var params = JSON.stringify(array);

            let pageNoC = $(".tbl_pagination .active").attr("page-no");
            if( $(".check-box.all").hasClass("active") || $("#tableRow tr").length == 1){
            	pageNoC = pageNoC-1
            	if(pageNoC == 0){
            		pageNoC = 1
            	}               	
            }

            fetch("trash/rollback", {
                method: "POST",
                headers: {
                     "Content-Type": "application/json",
                  },
                body: params,
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
            	loadList(pageNoC)
			}).catch(err=>{
				console.log(err)
				alert("페이지 갱신 실패")
			})
        }
    })
    
    
    $("#ex-btn").on("click", function(){
    	if($(".check-box.active:not(.all)").length === 0) {
    		alert("선택한 항목이 없습니다.")
    	} else if(confirm("선택된 항목을 학습요청에서 제외하겠습니까?")) {
    		let query = "";
    		$(".check-box.active:not(.all)").each(function(idx, el){
            	let seq = $(el).closest("tr").data("seq");
                query += ("seq[]="+seq)+"&"
    		})
    		
    		query = query.substring(0, query.length-1)
    		
            let pageNoC = $(".tbl_pagination .active").attr("page-no");
            if( $(".check-box.all").hasClass("active") || $("#tableRow tr").length == 1){
            	pageNoC = pageNoC-1
            	if(pageNoC == 0){
            		pageNoC = 1
            	}               	
            }       		
    		
    		fetch("excludeTrans?"+encodeURI(query), {
    			method: "POST"
    		}).then(res=>{
    			if(res.ok) loadList(pageNoC)
    		})
    	}
    })    
    
    $("#inc-btn").on("click", function(){
    	if($(".check-box.active:not(.all)").length === 0) {
    		alert("선택한 항목이 없습니다.")
    	} else {
    		let query = "";
    		$(".check-box.active:not(.all)").each(function(idx, el){
            	let seq = $(el).closest("tr").data("seq");
            	const useyn = $(el).closest("tr").data("useyn");
            	
            	
    			if ( useyn == 'Y') {
    				alert("사용중인 항목이 선택되어 있습니다.");
    				return false; // each loop 이므로  return false 로 loop 을 빠져나간다. 
    			}
            	
            	
                query += ("seq[]="+seq)+"&"
    		})
    		
    		query = query.substring(0, query.length-1)
    		
            let pageNoC = $(".tbl_pagination .active").attr("page-no");
            if( $(".check-box.all").hasClass("active") || $("#tableRow tr").length == 1){
            	pageNoC = pageNoC-1
            	if(pageNoC == 0){
            		pageNoC = 1
            	}               	
            }       		
            
            if(confirm("선택된 항목을 학습요청에 포함하겠습니까?")){
        		fetch("includeTrans?"+encodeURI(query), {
        			method: "POST"
        		}).then(res=>{
        			if(res.ok) loadList(pageNoC)
        		})	
            }
    		
    	}
    })        

    
    $(document).on("click", ".page-data-row td:not(.check-box)", function (){
    	
    	const {activeMenu} = queryParams
    	
        const $this = $(this).closest("tr");
        $this.find("td").css("color", "#555");
        const name = "&name="+$this.data("name");
        
	  	  if($this.data("name") == null){
	  	  	return false
	  	  }
	        
	  	  $("#titleName").text($this.data("name")) 
  	  

    		  	   
	  	transList($this, 1);	
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
    
    $("#search-btn").trigger("click");
})

const queryParams = new Proxy(new URLSearchParams(window.location.search), {
    get: (searchParams, prop) => searchParams.get(prop),
});

function loadList(reqPageNo) {
	 
    const {activeMenu} = queryParams
        
    let pageSize	
    
    
	if(activeMenu == 1 || activeMenu == 4 ){
		 pageSize = getCookie("pageSize")
	}else{			
    	pageSize = getCookie("popPageSize")
	}    
    

    let query = "keyword="+$("#search-keyword").val().trim()+"&activeMenu="+activeMenu+"&page="+reqPageNo+"&size="+pageSize+"&searchStartDate="+$(calenderStartName).val()+"&searchEndDate="+$(calenderEndName).val();
    if ( activeMenu == 3 ) {
    	query += "&useYn="+$("#useYn").val();
    }
    
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
    }).then(html=>{
        pageNo = Number(reqPageNo);
        insertUrlParam('page', reqPageNo);
		$("#tableRow").children().remove();
		$("#tableRow").append(html);
		$("#spanHiddenCount").text($("#hiddenCount").val());  
        $("span.all.check-box").removeClass("active");
		contentPagination( $("#hiddenCount").val(), pageSize);
    }).catch(err=>{
        console.log(err)
        alert("페이지 갱신 실패")
    })

}


function transList(tr, reqPageNo) {
	
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
//    const changeTextValue = getCookie("changeText") == 1 ? 1 : 0;
    const changeTextValue = 0
    const category = "tts";
        
    const query = "seq="+seq+"&reliability="+reliability+"&page="+reqPageNo+"&size="+popPageSize+"&waveValue="+waveValue+"&category="+category+"&changeTextValue="+changeTextValue+"&suffix=" + suffix+"&searchStartDate="+$(calenderStartName).val()+"&searchEndDate="+$(calenderEndName).val();;
 
   
	fetch("transList?" + encodeURI(query), {
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
		popupPageNo = Number(reqPageNo);
		
    	$("#tablePage").hide();

    	if ( tr ) {
    		$(".common_content").append(html);	        	
    	} else {
            $("table.tbl_detail").children().remove();
            $("table.tbl_detail").append(html); 
    	}
		
		$("#name").text($("#hiddenName").val());
		
		let reliability = $("#hiddenReliability").val();
			
		if (reliability > 100){
			reliability = 100
		}
		
		$("#reliability").val(reliability);
		
		$('select[name="pop_page_count"]').val(popPageSize);
   		
		$("#spanPopupCount").text($("#popupCount").val());
		popupPagination($("#popupCount").val(), $("#popupPageSize").val() );
		$(".menu_short").css("z-index","999")
		
		initTrans();
		
//		console.log(query)
//		
//		if ( waveValue == 1 ) {
//			appendWaveSpectrum("appendWaveSpectrum?" + encodeURI(query));
//		}
		
	}).catch(err => {
		console.log(err)
		alert("페이지 갱신 실패")
	})
}



function createKeyword() {
	
	fetch("createKeyword?", {
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
				
	}).catch(err => {
		console.log(err)
		alert("페이지 갱신 실패")
	})
}