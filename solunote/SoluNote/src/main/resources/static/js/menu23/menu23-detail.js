
let continual = true;
let queue;
let viewSeq = 0;
let viewSeq2 = 0;
let viewPct = 0;

let speakerIcon = 0;
let waveValue = 0;
let changeText = 0;

let originalText= "";

$(function () {
		

    $(document).on('click', '.back_btn', function () {
    	
        $(".sound").each(function () {
            this.pause();  // 재생 중이면 정지
            this.currentTime = 0;  // 재생 위치 초기화
            $(this).remove();  // 오디오 태그 삭제
        });
  	
    	$("#detailPage").remove();
    	$("#tablePage").show();
    	$(".menu_short").css("z-index","")
    });
    
    
    //합치기 나눈기 popup
    $(document).on('contextmenu', '.trainingData_text', function (event) {
    	event.preventDefault();
    	 
        const $this = $(this).closest(".stt_txt");
        viewSeq = $(this).closest("tr").data("seq");
        viewSeq2 = $(this).closest("tr").next().data("seq");
        selectDivideCombineShow($this, event)
    });  
	   

    $(document).on("click", ".tool_popup .plus_btn", function () {
    	if ( isReliability() == true ) {
    		return;
    	}  	
    	

    	
    	cloneRowToCombine()
    })

    $(document).on("click", ".tool_popup .divide_btn", function () {
    	if ( isReliability()  == true ) {
    		return;
    	}
        cloneRow();
    })
  

	$(document).on("click", ".spin-up", function () {
	    let currentValue = $("#reliability").val();
	
	    if (currentValue === "") {
	        currentValue = 0;
	    } else {
	        currentValue = parseInt(currentValue, 10); 
	    }
	
	    currentValue += 1;
	
	    $("#reliability").val(currentValue);
	});
    
	$(document).on("click", ".spin-down", function () {
	    let currentValue = $("#reliability").val();
	
	    if (currentValue === "") {
	        currentValue = 0;
	    } else {
	        currentValue = parseInt(currentValue, 10); 
	    }
	
	    currentValue -= 1;
	
	    $("#reliability").val(currentValue);
	});    
    
    
    $(document).on("click", ".rewind", function () {
    	if ( isReliability()  == true ) {
    		return;
    	}
    	
        const tr = $(this).closest("tr");
        const base = $(this).data("base");
        
                
        resetPlayer(tr, base, "rewind");
    })

    $(document).on("click", ".forward", function () {
    	if ( isReliability()  == true ) {
    		return;
    	}
    	
        let tr = $(this).closest("tr");
        let base = $(this).data("base");
                
        resetPlayer(tr, base, "forward");
    })

    $(document).on("click", "#select-btn", function () {

    	const popPageSize =$('select[name="pop_page_count"]').val()
		if (popPageSize != getCookie("popPageSize")  ){
			setCookie("popPageSize",popPageSize,cookeiDayPage)	
		}		
    	    	  	
    	transListPopUp(null, 1);    	
    })

     $(document).on("click", ".tbl_pagination_detail .arrow", function () {

   	  if ( $(this).hasClass("disabled")) {
      	return;
      }
	  
      const clickPageNo = $(this).attr("page-no");
      

      transListPopUp(null, clickPageNo);
      
    })
          
    $(document).on("click", ".tbl_pagination_detail .num", function () {
        const clickPageNo = $(this).attr("page-no");

        
        transListPopUp(null, clickPageNo);
    })    
    

    $(document).on("click", ".speaker-icon", function () {
         const $this = $(this);

         if($this.hasClass("active")) {
             $this.removeClass("active")
             setCookie("speakerIcon", 0, cookeiDayPage);
             speakerIcon = 0;
         } else {
               $this.addClass("active")
               setCookie("speakerIcon", 1, cookeiDayPage);
               speakerIcon = 1;
               audioPauseAll($this);
         }
    })
    
    $(document).on("change", "#pop_page_count", function () {
    	
    	const pageSize =$('select[name="pop_page_count"]').val()
		if (pageSize != getCookie("popPageSize")  ){
			setCookie("popPageSize",pageSize,cookeiDayPage)	
		}			    	

                
        transListPopUp(null, 1);    	

    	
    });
    
    $(document).on("click", "#changeText", function () {
    	const $this = $(this);
       
        if($this.hasClass("active")) {
            $this.removeClass("active")
    		setCookie("changeText", 0, cookeiDayPage);
    		waveValue = 0;        
            
        } else {
            $this.addClass("active")
            setCookie("changeText", 1, cookeiDayPage);
    		waveValue = 1;
    		
        }    	
    		
    	
        transListPopUp(null, 1); 
        
    })	    
    
    $(document).on("click", "#wave", function () {
    	const $this = $(this);
       
        if($this.hasClass("active")) {
            $this.removeClass("active")
    		setCookie("waveValue", 0, cookeiDayPage);
    		waveValue = 0;        
            
        } else {
            $this.addClass("active")
            setCookie("waveValue", 1, cookeiDayPage);
    		waveValue = 1;
    		
        }    	
    		    	
        transListPopUp(null, 1); 
        
    })	    
    
    
    //$("#resume-btn").on("click", function () {
    $(document).on('click', '#resume-btn', function () {
    	
        let audio = $(audioList[currentIndex])[0];
        pausedTime = audio.currentTime;
        pausedIndex = currentIndex;
        audio.pause();
        isPlaying = false;
    	
    	if($("#resume-btn").hasClass("allStop_btn")){

        	$('#resume-btn').removeClass("allStop_btn");            
    	}else{
    		    		
            playAudio(pausedIndex !== null ? pausedIndex : 0);
        	$('#resume-btn').addClass("allStop_btn");
    	}
    	
    });

    
      

});

function playAudio(index) {
    if (index >= audioList.length) {
    	index = 0

    }

    let audio = $(audioList[index])[0];

    audio.currentTime = pausedIndex === index ? pausedTime : 0; // 멈춘 지점부터 재생
    audio.play();
    isPlaying = true;
    currentIndex = index;

    audio.onended = function () {
        if (isPlaying) {
            playAudio(index + 1);
        }
    };
}


function initTrans() {

    queue = new Queue();
        
    speakerIcon = getCookie("speakerIcon");
    waveValue = getCookie("waveValue");
    changeText = getCookie("changeText");
    
    if( speakerIcon == 1 ){
    	$(".speaker-icon").addClass("active");
    }
    if( waveValue == 1 ){
    	$("#wave").addClass("active");
    }
    
	if(changeText ==1 ){
		$("#changeText").addClass("active");		
	}         
        
	

   //-------------------
        // on startup
   $("table.tbl_detail tr.page-data-row").each( function(idx, el) {
           colorfy($(el));
   });

   //--------------------------  
   $("p.trainingData_text").on("click", function(e){
	   
       	const pct = audioPos(e, $(this).text());
        continual = false;
        const audio = $(this).closest("tr").find("audio");
        if ( audio ) {
                audioPlay(audio, pct);
        }

        const ta = $(this).next().find("textarea");
        ta.css("height",  e.currentTarget.clientHeight);               
   });   
   
   
   $(document).on('focus', '.transDivDB', function () {
	   
	   const {activeMenu} = queryParams
	   
	   if (activeMenu == 3 ){
		   return false
	   }
	   
       originalText = $(this).closest('tr').find('td.stt_txt p.trainingData_text').text().trim();
   });

   $(document).on('blur', '.transDivDB', function () {   	
	   
	   const {activeMenu} = queryParams
	   
	   if (activeMenu == 3 ){
		   return false
	   }	   
	   
       const updatedText = $(this).text().trim();
   	       
       if (updatedText !== originalText) {
    	   
    	   const tr = $(this).closest("tr");
    	   
           saveTrans( tr );
       	
       }else{
    	   $(this).closest('tr').children(".transText").find('.transDivDB').css("display","none"); 

       }	    	
       
   });   
   
	$(document).on('click', 'table.tbl_detail tr.page-data-row td.transText', function () {
		
		const tr = $(this).closest('tr').children(".transText")
		
		const orginTr = $(this).closest('tr').children(".stt_txt").find('.trainingData_text').text()
		
		const divText = tr.find('.transDivDB').text();
				
		if(!divText || divText.trim() === ""){
			tr.find('.transDivDB').html(orginTr)
		}
		
		tr.find('.transDivDB').css("display","block");

    });
	
	
    // 개별 오디오 클릭 시 순차 재생
    audioList.on("play", function () {
    	

    	
        let index = audioList.index(this);
        
        if(currentIndex != index){
        	
        	let audio2 = $(audioList[currentIndex])[0];
        	audio2.pause()
        	
            currentIndex = index;
            isPlaying = false;        	
        }else{
        	currentIndex = index
        }
        

    	$('#resume-btn').addClass("allStop_btn");

    });

    audioList.on("pause", function () {
        pausedIndex = audioList.index(this);
        pausedTime = this.currentTime;
        
    	$('#resume-btn').removeClass("allStop_btn");
        
    });
	
}

//-------------------
function colorfy(row) {
		
	    const trans = row.children(".transText")
	    let spanText = trans.children(".transDivDB").text().trim();
			
	    const stt = row.children(".stt_txt")
	    let sttText = stt.children("p.trainingData_text").text().trim();

	    
	    if ( ! spanText || spanText == sttText ) {
	    		
            stt.children("p.trainingData_text").html(sttText);
            return;
	    }
	    
	    if(spanText !=sttText){
		    const dmp = new diff_match_patch();
		    		   
		 	const json = dmp.diff_main(sttText, spanText);
		 	
		 	const [left, right] = dmp.diff_changed(json);
		 	
		 	
		 	stt.children("p.trainingData_text").html(left);
		 	trans.children(".transDivDB").html(right);	    	
	    	
		 	trans.children(".transDivDB").css("display","block");
	    }     	
     	
}





function saveTrans(tr){
        const seq = tr.data("seq");

//      const dbText = tr.find(".transDb").text().trim();
        let transInput = tr.find('td.transText .transDivDB').text().trim()
        if ( ! transInput  ) {
                transInput = "NULL";
        }

        
        console.log("transInput",transInput)
        
        
    fetch('trans?seq='+seq, {
        method: "POST",
        body: transInput
    }).then(res=>{
        if(res.ok) {
            res.text().then(text=>{
                if ( transInput == "NULL" ) {
                        transInput = "";
                        tr.find(".transInput").val("");
                }
                colorfy(tr);
            })
        } else {
                alert("error");
        }
    }).finally(()=>{
    })
}
