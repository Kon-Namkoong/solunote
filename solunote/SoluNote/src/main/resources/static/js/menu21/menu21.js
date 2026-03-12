//let pageNo = 1;
//const pageSize = 10;
//const pageDepth = 5;
let prevKeyword = '';

let audio
let blobUrl

let intervalId

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
	
	

    $("#search-keyword + label > svg").on("click", function(){
        $("#search-keyword").val('');
    })
//    $("#search-keyword").on("blur", function (e) {
//        searchKeyword();
//    })
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
    
    
    $(document).on('click', '.back_btn', function () {
        if (audio) {
            audio.pause();
            audio.src = "";  // 기존 오디오 소스 제거
            audio.load();  // 변경 사항 반영
            clearInterval(audioIntervalId); // 인터벌 중지
        }
        
        $("#detailPage").remove();
        $("#tablePage").show();
        $(".menu_short").css("z-index", "");
        clearInterval(intervalId);

        // 기존 Blob URL 해제 (기존 오디오가 Blob으로 로드된 경우)
        if (blobUrl) {
            URL.revokeObjectURL(blobUrl);
            blobUrl = null;
        }
    });

    
    
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
    
    
    
    // 조회
    $("#List-btn").on("click", function(){
    	const pageSize =$('select[name="page_count"]').val()
		if (pageSize != getCookie("pageSize")  ){
			setCookie("pageSize",pageSize,cookeiDayPage)	
		}			    	
    	
    	let pageNoC = $(".tbl_pagination .active").attr("page-no");
    	
        if(!pageNoC){
        	pageNoC =1
        }
        
    	loadList(pageNoC)
    })
    
    $("#List-btn").trigger("click");
    
    $("#create-btn").on("click", function(){
        $("#modalWrap").addClass("active")
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
    
 
    $("#delete-btn").on("click", function(){
        if($(".check-box.active:not(.all)").length === 0) {
            alert("선택한 회의록이 없습니다.")
        } else if(confirm("영구 삭제하시겠습니까?")) {
    		const [ errorFlag, params, pageNoC ] = setSeqAndPage();
    		
			fetch("delete", {
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
            }).then(value => {
            	if ( value != "0" ) {
            		alert(value)
            	}
            	loadList(pageNoC)
			}).catch(err=>{
				console.log(err)
				alert("페이지 갱신 실패")
			})         
        }
    })
    
    $("#trash-btn").on("click", function(){
        if($(".check-box.active:not(.all)").length === 0) {
            alert("선택한 회의록이 없습니다.")
        } else if(confirm("삭제하시겠습니까?")) {
    		
    		const [ errorFlag, params, pageNoC ] = setSeqAndPage();
    		
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
            }).then(value => {
            	if ( value != "0" ) {
            		alert(value)
            	}
            	loadList(pageNoC)
			}).catch(err=>{
				console.log(err)
				alert("페이지 갱신 실패")
			})
    	}
    })
    
    $("#to-train-btn").on("click", function(){
    	if($(".check-box.active:not(.all)").length === 0) {
    		alert("선택한 회의록이 없습니다.")
    	} else if(confirm("학습 Data로 전송하시겠습니까?")) {
    		
    		const [ errorFlag, params, pageNoC ] = setSeqAndPage();
    		
    		if ( errorFlag == true ) {
    			alert("한국어  회의록만 전송할 수 있습니다.")
    		} else {
    			fetch("toTrain", {
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
	            }).then(value => {
	            	if ( value != "0" ) {
	            		alert(value)
	            	}else{
		            	alert("학습 DATA 전송완료")	            		
	            	}
	            	loadList(pageNoC)
				}).catch(err=>{
					console.log(err)
					alert("학습 DATA 전송실패. 관리자에게 문의바랍니다.")
				})
	    	}
    	}
    })
    
    $("#to-test-btn").on("click", function(){
    	if($(".check-box.active:not(.all)").length === 0) {
    		alert("선택한 회의록이 없습니다.")
    	} else if(confirm("테스트 Data로 전송하시겠습니까?")) {
  		const [ errorFlag, params, pageNoC ] = setSeqAndPage();
    		
    		if ( errorFlag == true ) {
    			alert("한국어  회의록만 전송할 수 있습니다.")
    		} else {
    			fetch("toTest", {
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
	            }).then(value => {
	            	if ( value != "0" ) {
	            		alert(value)
	            	}else{
		            	alert("테스트 DATA 전송완료")	            		
	            	}
	            	loadList(pageNoC)
				}).catch(err=>{
					console.log(err)
					alert("테스트 DATA 전송실패. 관리자에게 문의바랍니다.")
				})
    		}
    	}
    })
    
    $("#rollback-trash-btn").on("click", function(){
        if($(".check-box.active:not(.all)").length === 0) {
            alert("선택한 회의록이 없습니다.")
        } else if(confirm("복원하시겠습니까?")) {
        	
    		const [ errorFlag, params, pageNoC ] = setSeqAndPage();
    		
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
            }).then(value => {
            	if ( value != "0" ) {
            		alert(value)
            	}
            	loadList(pageNoC)
			}).catch(err=>{
				console.log(err)
				alert("페이지 갱신 실패")
			})
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

    $(document).on("click", ".page-data-row td:not(.check-box):not(.favorites-check)", function (){        
        const tr = $(this).closest("tr");
        const seq = tr.data("seq");
    	
        fetch("view?seq="+seq, {
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
	            
	            $(".menu_short").css("z-index","999")
	            
	            if($(window).width() <= 1545){
	            	$('.detail_right_wrap .tab_btns').hide()
	            }	            
	            
	            let fileNm=$("#hiddenFileNm").val()

	            
				const summaryData = [
				    { summaryId: $("#queryParamsSummaryId").val(), status: $("#queryParamsSummaryStatus").val() , summaryType: 1 },
				    { summaryId: $("#queryParamsSummaryId2").val(), status: $("#queryParamsSummaryStatus2").val() , summaryType: 2 },
				    { summaryId: $("#queryParamsSummaryId3").val(), status: $("#queryParamsSummaryStatus3").val() , summaryType: 3 }
				];
	            
 			
	            

	            for (const { status,summaryId, summaryType } of summaryData) {
	                if (summaryId) {
	                    const show = updateSummaryUI(status, summaryType);	                    
	                    if (show == 0) {
					        getSummaryInterval(seq, summaryId,summaryType);
	                        break; 
	                    }else{
	                        break;                     	
	                    }
	                }
	            }

				
	            fetch("download?fileNm="+fileNm,) // 서버 URL
	            .then(response => {
	                if (!response.ok) throw new Error('Network response was not ok');
	                return response.blob(); // 서버에서 받은 데이터를 Blob으로 변환
	            }).then(blob => {
	                audio = document.getElementById('audio');
	                blobUrl = URL.createObjectURL(blob); // Blob URL 생성
	                audio.src = blobUrl; // Blob URL을 오디오 src에 설정
		            startAudioInterval(audio);	                
	            }).catch(error => console.error('Error fetching audio:', error));	            
	            
	                     
	        }).catch(err=>{
	            console.log(err)
	            alert("페이지 갱신 실패")
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


    $(document).on("click", ".favorites-check", function () {

        const $this = $(this);

        let callbackFunc = undefined;
        const seq = $this.closest("tr").data("seq");
        let remark = false
        
        if($this.hasClass("active")) {
            $this.removeClass("active")
            remark = false;
        } else {
            $this.addClass("active")
            remark = true;
        }
        updateRemark(seq, remark,$this);

    })
})


function updateSummaryUI(status,summaryType) {
	
    $("#summaryBefore").hide();
    $("#summaryType").val(summaryType)
    if (status === "PENDING") {
        $("#summaryIng1").show();
    	return 0;
    } else if (status === "STARTED") {
        $("#summaryIng2").show();
    	return 0;
    } else if (status === "FAILURE") {
    	$("#summaryBefore").show();
    	return 1;
    }else{
    	if(summaryType == 1){
    		$("#summary1").show();
    	}else if (summaryType == 2){
    		$("#summary2").show();
    	}else{
    		$("#summary3").show();
    	}
    	
    	return 1;
    }
}

function getSummaryInterval(seq, summaryId,summaryType){
		
    intervalId = setInterval(function () {
        
    	const queryParam = "seq=" + seq + "&summaryId=" + summaryId+ "&summaryType=" + summaryType;

        fetch("view/statusSummary?" + encodeURI(queryParam), {
            method: "GET",
            redirect: "follow"
        })
        .then(response => response.text())
        .then(status => {

            console.log("서버 응답 상태:", status);
        	
            if (status === "STARTED") {
            	
            	if(summaryType == 1){
            		$("#queryParamsSummaryStatus").val(status)	
            	}else if (summaryType == 2){
            		$("#queryParamsSummaryStatus2").val(status)
            	}else{
            		$("#queryParamsSummaryStatus3").val(status)
            	}
            	
                $("#summaryIng1").hide();
                $("#summaryIng2").show();
            } else if (status === "SUCCESS") {
            	
            	if(summaryType == 1){
            		$("#queryParamsSummaryStatus").val(status)	
            	}else if (summaryType == 2){
            		$("#queryParamsSummaryStatus2").val(status)
            	}else{
            		$("#queryParamsSummaryStatus3").val(status)
            	}            	
            	
            	$("#summaryIng1").hide();
                $("#summaryIng2").hide();
                $("#summaryIng3").show();

                setTimeout(function () {	                    	
                    $("#summaryIng3").hide();
                    $("#summaryFinsh").show();
                }, 1000);

                setTimeout(function () {
                    fetch("view/summary?" + encodeURI(queryParam), {
                        method: "GET",
                        redirect: "follow"
                    })
                    .then(res => {
                        if (res.redirected) {
                            window.location.href = res.url;
                        } else if (res.ok) {
                            return res.text();
                        } else {
                            return Promise.reject(res);
                        }
                    })
                    .then(html => {
                    	$("#summaryFinsh").hide();

                    	if(summaryType == 1){
                    		$("#summary1").remove();
                    	}else if (summaryType == 2){
                    		$("#summary2").remove();
                    	}else{
                    		$("#summary3").remove();
                    	}                     
                    	
                    	$("#hasSummary").val(1)
                        $("#summaryContent").append(html);
                    })
                    .catch(err => {
                        console.log("에러 확인:", err);
                        $("#summaryIng").hide();
                        $("#summaryBefore").show();
                        alert("요약을 불러오는데 실패했습니다. 관리자에게 문의 바랍니다.");
                    });
                }, 2000);

                clearInterval(intervalId); // ✅ 반복 중지
            } else if (status === "FAILURE") {
                console.log("요약 실패");
                
            	if(summaryType == 1){
            		$("#queryParamsSummaryStatus").val(status)	
            	}else if (summaryType == 2){
            		$("#queryParamsSummaryStatus2").val(status)
            	}else{
            		$("#queryParamsSummaryStatus3").val(status)
            	}            	
            	
                $("#summaryIng").hide();
            	$("#summaryIng1").hide();
                $("#summaryIng2").hide();
                $("#summaryBefore").show();
                alert("요약에 실패 했습니다. 다시 요약 버튼을 눌러주세요.");
                clearInterval(intervalId); // ✅ 반복 중지
            }
        })
        .catch(err => {
            console.log("요약 상태 조회 에러 확인:", err);
            alert("요약 상태 조회에 실패 했습니다. 관리자에게 문의 바랍니다.");
            clearInterval(intervalId); // ✅ 에러 발생 시 반복 중지
        });
    }, 3000);	
}


const queryParams = new Proxy(new URLSearchParams(window.location.search), {
    get: (searchParams, prop) => searchParams.get(prop),
});

function updateRemark(seq, remark,$this) {
    fetch("remark?seq="+seq, {
        method: "POST",
        body: remark
    }).then(res=>{
    	if ( res.redirected == true ) {
    		window.location.href = res.url;
    	} else if(res.ok) {
    	    return res.text();
    	} else {
    		return Promise.reject(res);
    	}
    }).then(data=>{

    	 const formattedData = data.replace(/\./g, '-');
    	 $this.closest("tr").find(".updatedAt").text(formattedData);
    }).catch(err=>{
        console.log(err)
        alert("즐겨찾기 실패")
    })
}

function loadList(reqPageNo) {

	if(!checkDate()){
		return  false
	}
	
    const {activeMenu} = queryParams

    const pageSize = getCookie("pageSize")	
    
	    
    const queryParam = "keyword="+$("#search-keyword").val().trim()+"&category="+$("#search-category").val()+"&activeMenu="+activeMenu+"&page="+reqPageNo+"&searchStartDate="+$(calenderStartName).val()+"&searchEndDate="+$(calenderEndName).val()+"&size="+pageSize
    
            
    fetch("loadList?"+encodeURI(queryParam), {
        method: "GET"
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
		$(".tbl_scroll tbody").children().remove();
        $(".tbl_scroll tbody").append(html);
        $("#tableTotal").text($("#hiddenCount").val());        
        contentPagination($("#hiddenCount").val(),pageSize);
               
    }).catch(err=>{
        alert("페이지 갱신 실패")
    })

}


function toggleSummaryDisplay() {
    var cityName = $('.tablinks.active').length ? $('.tablinks.active').data('city') : "Substance";

    if ($('.Summary').length === 0 || $('.Substance').length === 0) return;
    
    var $summationButton = $('.detail_right_wrap .tab_btns');

    if ($(window).width() >= 1545) {
        $('.Summary').removeClass('tabcontent').show();
        $('.Substance').show();
        $summationButton.show();

        // 창 크기가 1545 이상일 때 active 클래스를 추가
        $('.tablinks').removeClass('active'); // 모든 active 제거
        $('.tablinks.defaultOpen').addClass('active'); // defaultOpen에 active 추가
    } else {
    	$summationButton.hide();
        $('.Summary').addClass('tabcontent');
        if (cityName === 'Summary') {
            $('.Summary').show();
            $('.Substance').hide();
        } else {
            $('.Summary').hide();
            $('.Substance').show();
        }
    }

    if (cityName) {
        openCity(null, cityName);
    }
}

// 윈도우 크기 변경 시 이벤트 처리
$(window).on('resize', function () {
    toggleSummaryDisplay(); // 윈도우 크기가 변경될 때마다 다시 기본 상태를 확인
});

function openCity(evt, cityName) {
    $(".tabcontent").hide();
    $(".tablinks").removeClass("active");

    var $summationButton = $('.detail_right_wrap .tab_btns');

    if ($(`.${cityName}`).length) {
        $(`.${cityName}`).show().addClass('tabcontent');
    }

    if (evt) {
        $(evt.currentTarget).addClass("active");
    }

    if ($(window).width() >= 1545) {
        $summationButton.show();
        $('.Summary').show();
        $('.Substance').show();
    } else if (cityName !== 'Summary' && $summationButton.length) {
    	$summationButton.hide();
        $('.Substance').show();
    } else {
        $summationButton.show();
        $('.Summary').show();
    }
}

function setSeqAndPage() {
	
	let errorFlag = false;
	
    let array = [];
    $(".check-box.active:not(.all)").each(function(idx, el){
    	if ( $(el).closest("tr").data("lang") != "ko" ) {
			errorFlag = true;
		}
    	let seq = $(el).closest("tr").data("seq");
    	const item = {
    			seq :  seq
    	}
        array.push( item );
    })
    const params = JSON.stringify(array);
	
	let pageNoC = $(".tbl_pagination .active").attr("page-no");
	if( $(".check-box.all").hasClass("active") || $("#tableRow tr").length == 1){
		pageNoC = pageNoC-1
		if(pageNoC == 0){
			pageNoC = 1
		}               	
	}
	
	return [ errorFlag, params, pageNoC ];
}