
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
    	
    	callPopup(1);
    })

    
     $(document).on("click", ".tbl_pagination_detail .arrow", function () {

   	  if ( $(this).hasClass("disabled")) {
      	return;
      }
	  
      const clickPageNo = $(this).attr("page-no");
      
      $(currentPageName).val(clickPageNo);
    
      callPopup(clickPageNo);
      
      
    })
          
    
    $(document).on("click", ".tbl_pagination_detail .num", function () {
        const clickPageNo = $(this).attr("page-no");
        
        $(currentPageName).val(clickPageNo);
        
        callPopup(clickPageNo);
        
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
    
    $(document).on("change", "#pop_page_count", function () {
    	
		const pageSize =$('select[name="pop_page_count"]').val()
		if (pageSize != getCookie("popPageSize")  ){
			setCookie("popPageSize",pageSize,cookeiDayPage)	
		}			    	
	
		let pageNoC = $(".tbl_pagination_detail .active").attr("page-no");
    	
 	   const modelId = "modelId=" + $("#hiddenModelId").val();
       const seq = "&seq=" + $("#hiddenSeq").val();
       const orderby = "&orderby="+ $("#orderBy").val();
       const queryParams = modelId + seq + orderby

       trainListPopUp(false, queryParams, 1)
    	
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


function callPopup(clickPageNo) {
	   const modelId = "modelId=" + $("#hiddenModelId").val();
       const seq = "&seq=" + $("#hiddenSeq").val();
       const orderby = "&orderby="+ $("#orderBy").val();
       const queryParams = modelId + seq + orderby
       console.log(queryParams)
       
		if ($("#pop_page_count").val() != getCookie("popPageSize")  ){
			setCookie("popPageSize",$("#pop_page_count").val(),cookeiDayPage)	
		}
       trainListPopUp(false, queryParams, clickPageNo)
       
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

