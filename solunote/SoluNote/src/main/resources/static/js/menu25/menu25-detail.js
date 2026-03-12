
let continual = true;
let queue;
let audioList;

let viewSeq = 0;
let viewSeq2 = 0;
let viewPct = 0;

let speakerIcon = 0;
let waveValue = 0;
let changeText = 0;

let originalText= "";

$(function () {
	
    $(document).on('click', '.back_btn', function () {
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
    	
//    	const viewRow = $(this).closest("tr");
//    	
//        const toolPopup = $('.tool_popup');
//        toolPopup.css('display', 'none');
    	
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
    	
    	if($("#changeText").hasClass("active")){
    		setCookie("changeText", 1, cookeiDayPage);
    		changeText = 1;    		
    	}else{
    		setCookie("changeText", 0, cookeiDayPage);
    		changeText = 0;    		
    	}

    	
    	if($("#wave").hasClass("active")){
    		setCookie("waveValue", 1, cookeiDayPage);
    		waveValue = 1;		
    	}else{
    		setCookie("waveValue", 0, cookeiDayPage);
    		waveValue = 0;  		
    	}
    	
        let seq = "seq=" + $("#hiddenSeq").val();
        const page = "&page="+ 1;
        let queryParams = seq+page
        
    	  transListPopUp(null, 1)
    })

     $(document).on("click", ".tbl_pagination_detail .arrow", function () {

   	  if ( $(this).hasClass("disabled")) {
      	return;
      }
	  
      const clickPageNo = $(this).attr("page-no");
      
      let seq = "seq=" + $("#hiddenSeq").val();
      const page = "&page="+ clickPageNo;
      let queryParams = seq+page
      
      $(currentPageName).val(clickPageNo);
      
      transListPopUp(null,clickPageNo)
      
    })
          
    $(document).on("click", ".tbl_pagination_detail .num", function () {
        const clickPageNo = $(this).attr("page-no");

        let seq = "seq=" + $("#hiddenSeq").val();
        const page = "&page="+ clickPageNo;
        let queryParams = seq+page
        
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

});


function initTrans() {

        queue = new Queue();
        audioList = $(".sound");
        
    speakerIcon = getCookie("speakerIcon");
    waveValue = getCookie("waveValue");
    changeText = getCookie("changeText");
    
    if( speakerIcon == 1 ){
    	$(".speaker-icon").addClass("active");
    }
    if( waveValue == 1 ){
    	$("#wave").addClass("active");
    }
    
	if(getCookie("changeText") ==1 ){
		$("#changeText").addClass("active");		
	}         
        

   //-------------------
        // on startup
   $("table.tbl_detail tr.page-data-row").each( function(idx, el) {
           colorfy($(el));
   });

 //-------------------
   if ( audioList.length > 0 ) {

           audioList.on("play", function(e) {
                   audioPauseAll($(this));
                   hideRunning($(this));
                   editorControl($(this).closest("tr"), true);
           });

           audioList.on("ended", function() {
                   const next = $(this).closest("tr").next();
                   const audio = next.find(".sound");
                         if ( continual == true ) {
                                 audioPlay(audio);
                         }

           });

           audioList.on("contextmenu", function (evt) {
                    evt.preventDefault();
           });

   }

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
       originalText = $(this).closest('tr').find('td.stt_txt p.trainingData_text').text().trim();
   });

   $(document).on('blur', '.transDivDB', function () {   	
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
   
     //-------------------------------------

    //-------------------
     
    $(document).on('click', '#resume-btn', function (e) {          
            if($("#resume-btn").hasClass("allStop_btn")){
            	$("#resume-btn").removeClass("allStop_btn")
                
            	continual = false;
                audioPauseAll();
            	
            }else{
                continual = true;
                let audio;
            	
            	$("#resume-btn").addClass("allStop_btn")
            	
	            audioList.each( function(idx, el) {
	                    if ( $(el).closest("tr").hasClass("running")    ) {
	                            audio = $(el);
	                    }
	            });
	
	            if ( audio ) {
	                     audioPlay(audio);
	            } else {
	                    if ( audioList.length ) {
	                             audioPlay($(audioList[0]));
	                    }
	            }            	
            	
            }

    })

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

