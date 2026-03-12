
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
    

    $(document).on("click", "#select-btn", function () {

    	const popPageSize =$('select[name="pop_page_count"]').val()
		if (popPageSize != getCookie("popPageSize")  ){
			setCookie("popPageSize",popPageSize,cookeiDayPage)	
		}		
    	    	  	
    	transList(null, 1);    	
    })

     $(document).on("click", ".tbl_pagination_detail .arrow", function () {

   	  if ( $(this).hasClass("disabled")) {
      	return;
      }
	  
      const clickPageNo = $(this).attr("page-no");
      

      transList(null, clickPageNo);
      
    })
          
    $(document).on("click", ".tbl_pagination_detail .num", function () {
        const clickPageNo = $(this).attr("page-no");

        
        transList(null, clickPageNo);
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
                
        transList(null, 1);    	

    	
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
        transList(null, 1);         
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
    		    	
        transList(null, 1); 
        
    })	    

});


function initTrans() {
        
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
	   
	   if (activeMenu == 4 ){
		   return false
	   }	 	   
	   
       originalText = $(this).text().trim();
   });

   $(document).on('blur', '.transDivDB', function () {   
	   
	   const {activeMenu} = queryParams
	   
	   if (activeMenu == 4 ){
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
		
		const divText = tr.find('.transDivDB').text();
				
		if(!divText || divText.trim() === ""){			
			if(originalText !="" || originalText == null){
				tr.find('.transDivDB').html(originalText)
			}			
		}		
		tr.find('.transDivDB').css("display","block");
    });
	
	
/*    queue = new Queue();
    audioList = $(".sound");
	
   if ( audioList.length > 0 ) {

       audioList.on("play", function(e) {
               audioPauseAll($(this));
               hideRunning($(this));
               editorControl($(this).closest("tr"), true);
       });

       audioList.on("ended", function() {
               const next = $(this).closest("tr").next();
               const audio = next.find(".sound");
               
               const allEnded = audioList.toArray().every(a => $(a).prop('ended'));
               if (allEnded) {
                   $("#resume-btn").removeClass("allStop_btn");
               }               
               
	             if ( continual == true ) {
	                     audioPlay(audio);
	             }               
       });

       audioList.on("contextmenu", function (evt) {
                evt.preventDefault();
       });

   }	

	
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

    })	*/
	
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
