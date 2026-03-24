//let pageNo = 1;
//let pageSize = 10;
//const pageDepth = 5;
let prevKeyword = '';

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
//    loadList(queryParams.pageNo ? queryParams.pageNo : 1);
	
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
    
    $('#pop_page_count').change(function() {

    	const pageSize =$('select[name="pop_page_count"]').val()
		if (pageSize != getCookie("popPageSize")  ){
			setCookie("popPageSize",pageSize,cookeiDayPage)	
		}			    	

        
    	loadList(1)
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
	
	//조회 버튼
	$("#search-btn").click(function() {
    	const pageSize =$('select[name="pop_page_count"]').val()
		if (pageSize != getCookie("popPageSize")  ){
			setCookie("popPageSize",pageSize,cookeiDayPage)	
		}		
		loadList(queryParams.pageNo ? queryParams.pageNo : 1);
		
		 		
	});
	
	$("#search-btn").trigger("click");

    $("#train-btn").on("click", function(){
        if($(".check-box.active:not(.all)").length === 0) {
            alert("선택한 항목이 없습니다.")
        } else if(confirm("선택된 항목을 학습하도록 요청하시겠습니까?")) {
            let query = "";
            $(".check-box.active:not(.all)").each(function(idx, el){
                query += ("seq[]="+el.closest("tr").getAttribute("seq"))+"&"
            })
            
            query = query.substring(0, query.length-1);
            
            let pageNoC = $("#content-footer .active").attr("page-no");
            if( $(".check-box.all").hasClass("active") || $("#tableRow tr").length == 1){
            	pageNoC = pageNoC-1
            	if(pageNoC == 0){
            		pageNoC = 1
            	}               	
            }            
            
            fetch("requestTrain?"+encodeURI(query), {
                method: "POST"
            }).then(res=>{
                if(res.ok) loadList(pageNoC)
            })
        }
    })
    
    $("#ex-btn").on("click", function(){
    	if($(".check-box.active:not(.all)").length === 0) {
    		alert("선택한 항목이 없습니다.")
    	} else {
    		let array = [];
    		$(".check-box.active:not(.all)").each(function(idx, el){
    			let seq = $(el).closest("tr").data("seq");
    			const useyn = $(el).closest("tr").data("useyn");
    			if ( useyn == 'N') {
    				alert("이미 학습완료된 항목입니다. 학습대상이 아닙니다.");
    				return false; // each loop 이므로  return false 로 loop 을 빠져나간다. 
    			}
    			const item = {
            			seq :  seq
            	}
                array.push( item );
    		})
    		
    		if(array.length > 0 && confirm("선택된 항목을 학습요청에서 제외하겠습니까?")) {
        		
        		let pageNoC = $(".tbl_pagination .active").attr("page-no");
        		if( $(".check-box.all").hasClass("active") || $("#tableRow tr").length == 1){
        			pageNoC = pageNoC-1
        			if(pageNoC == 0){
        				pageNoC = 1
        			}               	
        		}  
        		
        		const params = JSON.stringify(array);
        		fetch("excludeTrans", {
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
    	}
    })
    
    
    $("#inc-btn").on("click", function(){
    	if($(".check-box.active:not(.all)").length === 0) {
    		alert("선택한 항목이 없습니다.")
    	} else {
    		let array = [];
    		$(".check-box.active:not(.all)").each(function(idx, el){
    			let seq = $(el).closest("tr").data("seq");
    			const useyn = $(el).closest("tr").data("useyn");
    			if ( useyn == 'Y') {
    				alert("사용중인 항목이 선택되어 있습니다.");
    				return false; // each loop 이므로  return false 로 loop 을 빠져나간다. 
    			}
    			const item = {
            			seq :  seq
            	}
                array.push( item );
    		})
    		
    		if(array.length > 0 && confirm("선택된 항목을 학습요청에 포함하겠습니까?")) {
        		
        		let pageNoC = $(".tbl_pagination .active").attr("page-no");
        		if( $(".check-box.all").hasClass("active") || $("#tableRow tr").length == 1){
        			pageNoC = pageNoC-1
        			if(pageNoC == 0){
        				pageNoC = 1
        			}               	
        		}  
        		
        		const params = JSON.stringify(array);
        		fetch("includeTrans", {
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
    	}
    })
    
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


    $(document).on("click", ".remark-svg-icon", function () {

        const $this = $(this);
        $this.addClass("loading-wait")

        let callbackFunc = undefined;
        const seq = $this.closest("tr").attr("seq");
        let remark = false

        if($this.hasClass("active")) {
            callbackFunc = function () {
                $this.removeClass("active").removeClass("loading-wait")
            }
            remark = false;
        } else {
            callbackFunc = function () {
                $this.addClass("active").removeClass("loading-wait")
            }
            remark = true;
        }
        updateRemark(seq, remark, callbackFunc);

    })
})

const queryParams = new Proxy(new URLSearchParams(window.location.search), {
    get: (searchParams, prop) => searchParams.get(prop),
});

function updateRemark(seq, remark, callbackFunc) {
    fetch("remark?seq="+seq, {
        method: "POST",
        body: remark
    }).then(res=>{
        if(res.ok) {
            res.text().then(datetimeFormat => {
                $(".page-data-row[seq='"+seq+"'] .updatedAt").text(datetimeFormat);
            })
            return callbackFunc()
        }
        return Promise.reject(res);
    }).catch(err=>{
        alert("즐겨찾기 실패")
    })
}

function loadList(reqPageNo) {
	
	if(!checkDate()){
		return  false
	}
	
    $(currentPageName).val(reqPageNo);
    
    const {activeMenu} = queryParams
    	
	const pageSize = getCookie("popPageSize")

	let queryParam = "keyword="+$("#search-keyword").val()+"&activeMenu="+activeMenu+"&page="+reqPageNo+"&searchStartDate="+$(calenderStartName).val()+"&searchEndDate="+$(calenderEndName).val()+"&size="+pageSize	
	
	if(activeMenu == 2){
		queryParam += "&useYn="+$("#useYn").val()
	}
	
	 console.log(queryParam)
           
    fetch("loadList?"+encodeURI(queryParam), {
        method: "GET",
		redirect: "follow" // manual, *follow, error
    }).then(res=>{
    	if ( res.redirected == true ) {
    		console.log(res.url)
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
         contentPagination( $("#hiddenCount").val(), pageSize);
    }).catch(err=>{
        console.log(err)
        alert("페이지 갱신 실패")
    })

}

//-------------------


function pageContent(arr){
}
