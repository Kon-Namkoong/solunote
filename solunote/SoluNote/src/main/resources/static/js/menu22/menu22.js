let newCharts = undefined;
let canvas = undefined;
const offset = new Date().getTimezoneOffset() * 60000;

let currentDate = new Date(new Date() - offset);

// 시작 날짜 계산
let monthAgo = new Date(currentDate);  // 현재 날짜 복사
monthAgo.setMonth(monthAgo.getMonth() - 1);  // 한 달 전으로 이동

// 지난달의 마지막 날 계산 (예: 2월 말 처리)
let lastDayOfMonth = new Date(monthAgo.getFullYear(), monthAgo.getMonth() + 1, 0).getDate();

// 현재 날짜의 일을 지난달의 마지막 날과 비교하여 조정
if (monthAgo.getDate() > lastDayOfMonth) {
    monthAgo.setDate(lastDayOfMonth);
}

monthAgo = new Date(monthAgo - offset).toISOString().substring(0, 10);
$("#searchStartDate").val(monthAgo);
startDate = new Date(monthAgo);

let today = new Date(new Date()-offset).toISOString().substring(0,10)
endDate = new Date(today);

$( function() {
	
	$("#changeH3").text("월간 이용현황");
	
	function updateOverflowStyle() {
	    if (window.innerWidth < 1920 && window.innerHeight < 1080) {
	        document.body.style.overflowY = "auto";
	    } else {
	        document.body.style.overflowY = "hidden"; // 기본값 설정 (필요 시 수정)
	    }
	}

	// 초기 실행
	updateOverflowStyle();

	// 화면 크기 변경 시에도 적용
	window.addEventListener('resize', updateOverflowStyle);
	
    canvas = document.getElementById('charts');
    loadingCanvas();
    initChartLabels();
    initCard();
    drawUserChart();
   

    $(".tab_menu > ul > li").on("click", function () {
        if ($(this).hasClass("active")) {
            // 현재 활성화된 탭의 클래스 제거
            $(".tab_menu > ul > li.active").removeClass("active");

            // 클릭된 탭을 활성화
            $(this).addClass("active");

            // 날짜 범위 가져오기
            endDate = new Date($("#searchEndDate").val());
            startDate = new Date($("#searchStartDate").val());

            // 차트 그리기 함수 호출
            if ($(this).hasClass("user-cnt-btn")) {
                drawUserChart();
            } else if ($(this).hasClass("file-cnt-btn")) {
                drawFileChart();
            }
        }
    });


    $( "#searchEndDate" ).datepicker({
        changeMonth: true,
        changeYear: true,
        showMonthAfterYear: true,
        dateFormat: "yy-mm-dd",
        monthNamesShort: ['1','2','3','4','5','6','7','8','9','10','11','12'],
        onSelect: function(dateText,inst){
        	if ($("#searchEndDate").val() > new Date().toISOString().substring(0,10) ){
            	alert("조회기간의 종료날짜는 현재날짜 이후 날짜는 조회하실수 없습니다.")
            	$("#searchEndDate").val(inst.lastVal);
            	return false
            }
            if ($("#searchStartDate").val() >= $("#searchEndDate").val()){  	
            	alert("조회기간의 종료날짜가 시작날짜보다 같거나 작을수 없습니다.")
            	$("#searchEndDate").val(inst.lastVal);
            	return false
            }
        	
            endDate = new Date(dateText);
            initChartLabels();
            initCard();
        }
    });
    
    $( "#searchStartDate" ).on("change", function(e) {
    	onStartEndDate();
    })
    
    $( "#searchEndDate" ).on("change", function(e) {
    	onStartEndDate();
    })

    $( "#searchStartDate" ).datepicker({
        changeMonth: true,
        changeYear: true,
        showMonthAfterYear: true,
        dateFormat: "yy-mm-dd",
        monthNamesShort: ['1','2','3','4','5','6','7','8','9','10','11','12'],
        onSelect: function(dateText,inst){
            if ($("#searchStartDate").val() >= $("#searchEndDate").val()){  	
            	alert("조회기간의 시작날짜가 종료날짜보다 같거나 클수 없습니다.")
            	$("#searchStartDate").val(inst.lastVal);
            	return false
            }
            startDate = new Date(dateText)
            initChartLabels();
            initCard();
        }
    });
    
    
    $("#searchDateDay").on("click", function () {
    	const offset = new Date().getTimezoneOffset() * 60000;
    	const today = new Date(new Date()-offset).toISOString().substring(0,10)
    	const yesterday = new Date(new Date().setDate(new Date().getDate() - 1)-offset).toISOString().substring(0,10); 
    	$("#searchStartDate").val(yesterday);
    	$("#searchEndDate").val(today);
    	endDate = new Date(today)
    	startDate = new Date(yesterday)
	    initChartLabels();
	    initCard();
	    startDate = new Date();	    
	    
	    $('.date_button button').removeClass('active');
	    $(this).addClass("active")
	    	    
	    $("#changeH3").text("일간 이용현황");
    })  
    
    $("#searchDateWeek").on("click", function () {
    	const offset = new Date().getTimezoneOffset() * 60000;
    	
    	let weekAgo =new Date(new Date().setDate(new Date().getDate() - 6)-offset).toISOString().substring(0,10);  
    	$("#searchStartDate").val(weekAgo)
    	startDate = new Date(weekAgo);
    	
    	let today = new Date(new Date()-offset).toISOString().substring(0,10)
    	$("#searchEndDate").val(today);
    	endDate = new Date(today);	
	    initChartLabels();
	    initCard();    
	    startDate = new Date();	
	    
	    $('.date_button button').removeClass('active');
	    $(this).addClass("active")
	    
	    $("#changeH3").text("주간 이용현황");

    })  
    
	$("#searchDateMonth").on("click", function () {
	    const offset = new Date().getTimezoneOffset() * 60000;
	    
	    // 현재 날짜 구하기
	    let currentDate = new Date(new Date() - offset);
	
	    // 시작 날짜 계산
	    let monthAgo = new Date(currentDate);  // 현재 날짜 복사
	    monthAgo.setMonth(monthAgo.getMonth() - 1);  // 한 달 전으로 이동
	
	    // 지난달의 마지막 날 계산 (예: 2월 말 처리)
	    let lastDayOfMonth = new Date(monthAgo.getFullYear(), monthAgo.getMonth() + 1, 0).getDate();
	
	    // 현재 날짜의 일을 지난달의 마지막 날과 비교하여 조정
	    if (monthAgo.getDate() > lastDayOfMonth) {
	        monthAgo.setDate(lastDayOfMonth);
	    }
	
	    monthAgo = new Date(monthAgo - offset).toISOString().substring(0, 10);
	    $("#searchStartDate").val(monthAgo);
	    startDate = new Date(monthAgo);
	
	    // 오늘 날짜 계산
	    let today = new Date(new Date() - offset).toISOString().substring(0, 10);
	    $("#searchEndDate").val(today);
	    endDate = new Date(today);   
	    
	    initChartLabels();
	    initCard();
	    startDate = new Date();
	
	    $('.date_button button').removeClass('active');
	    $(this).addClass("active");
	
	    $("#changeH3").text("월간 이용현황");
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
                 
});

function initCard(){
    const queryParam = "startDate="+dateFormat(startDate)+"&endDate="+dateFormat(endDate);
        
    fetch('dashboardCard?'+encodeURI(queryParam)).then(res=>{

        if(res.ok) {
            res.json().then(json=>{
                $("#userCnt").text(json.userCount)
                $("#meetingCount").text(json.meetingCount)
                if(json.avgMeetingTimeDurationMs == null){
                	$("#avgMeetingTimeDurationMs").text("0")
                }else{
                	$("#avgMeetingTimeDurationMs").text(json.avgMeetingTimeDurationMs)
                }
                if(json.avgFileSizeValue == null){
                	$("#avgFileSizeValue").text("0")
                }else{
                	$("#avgFileSizeValue").text(json.avgFileSizeValue)
                }            
                $("#avgFileSizeFormat").text(" "+json.avgFileSizeFormat)
            })
        } else {
            alert("이용현황 갱신 실패");
        }

    })
}

function initChartLabels(){
    let plusDays = 0;
    datas = [];
//    while (true) {
//        let diffDate = new Date(startDate);
//        diffDate.setDate(diffDate.getDate()+plusDays);
//        const text = (diffDate.getMonth()+1)+"."+(diffDate.getDate());
//        datas.push({"label": text, date: diffDate, count: 0})
//        if(diffDate.getFullYear() === endDate.getFullYear() && diffDate.getMonth() === endDate.getMonth() && diffDate.getDate() === endDate.getDate()) {
//            break;
//        }
//        plusDays++;
//    }
    
    // while 무한루핑 대체
    // 시작일부터 종료일까지 날짜를 순회
    for (let currentDate = new Date(startDate); currentDate <= endDate; currentDate.setDate(currentDate.getDate() + 1)) {
        // 월과 일을 텍스트로 변환
        const text = (currentDate.getMonth() + 1) + "." + currentDate.getDate();
        // 데이터 배열에 추가
        datas.push({ "label": text, date: new Date(currentDate), count: 0 });
    }    
    
    if(newCharts != undefined) {
        newCharts.data.labels = datas.map(o=>o.label);
        newCharts.data.datasets[0].data = datas.map(o=>o.count);
        newCharts.update();

        const activeBtnEl = $(".tab_menu > ul > li.active");
        if(activeBtnEl.hasClass("user-cnt-btn")) {
        	startDate = new Date($("#searchStartDate").val());
        	endDate = new Date($("#searchEndDate").val());        	
            drawUserChart();
        } else if( $(".tab_menu > ul > li.active").hasClass("file-cnt-btn")) {
        	startDate = new Date($("#searchStartDate").val());
        	endDate = new Date($("#searchEndDate").val());     
            drawFileChart()
        }
    }
}

//function drawFileChart(){
//
//    for (let i = 0; i < datas.length; i++) {
//        datas[i].count = 0;
//    }
//
//    const queryParam = "startDate="+dateFormat(startDate)+"&endDate="+dateFormat(endDate);
//    fetch('fileCountGroupDate?'+encodeURI(queryParam)).then(res=>{
//        if(res.ok) {
//
//            res.json().then(jsonArr=>{
//
//                for (let i = 0; i < datas.length; i++) {
//                    const dataObj = datas[i];
//
//                    for (let j = 0; j < jsonArr.length; j++) {
//                        const jsonObj = jsonArr[j];
//                        let month = dataObj.date.getMonth()+1;
//                        month = month < 10 ? ("0"+month) : month
//
//                        let day = dataObj.date.getDate();
//                        day = day < 10 ? ("0"+day) : day
//
//                        if((dataObj.date.getFullYear()+""+(month)+""+day) === jsonObj.date) {
//                            datas[i].count = jsonObj.count;
//                            jsonArr.splice(j, 1)
//                            break;
//                        }
//                    }
//
//                }
//
//                if(newCharts != undefined) {
//                    newCharts.data.datasets[0].data = datas.map(o=>o.count);
//                    newCharts.data.datasets[0].borderColor = 'rgb(72, 138, 255)'
//                    newCharts.update();
//                }
//
//            })
//
//        } else {
//            loadingCanvas();
//            $(".tab_menu > ul > li.active").removeClass("active");
//            alert("회의 등록 건수 차트 갱신 실패")
//        }
//    })
//
//    if(newCharts == undefined) {
//        const data = {
//            labels: datas.map(o=>o.label),
//            datasets: [{
//                label: 'count',
//                data: datas.map(o=>o.count),
//                borderColor: 'rgb(131,89,224)'
//            }]
//        };
//        const config = {
//            type: 'line',
//            data: data,
//            options: {
//                interaction: {
//                    mode: 'index',
//                    intersect: false,
//                },
//                scales: {                 	
//                    y: {
//                    	min:0,
//                        ticks: {
//                            stepSize: 1
//                        }
//                    },
//                },
//                plugins: {
//                    legend: {
//                        display: false
//                    }
//                },
//                responsive: true,
//                maintainAspectRatio: true,
//                layout: {
//                    padding: {
//                        left: 45,
//                        right: 30
//                    }
//                }
//            }
//        };
//        canvas.width = canvas.parentNode.offsetWidth;
//        canvas.height = canvas.parentNode.offsetHeight*2;
//        canvas.classList.remove("loading-canvas")
//
//        newCharts = new Chart(canvas, config)
//    }
//}
//
//function drawUserChart(){
//
//    for (let i = 0; i < datas.length; i++) {
//        datas[i].count = 0;
//    }
//
//    const queryParam = "startDate="+dateFormat(startDate)+"&endDate="+dateFormat(endDate);
//    
//    console.log(queryParam)
//    
//    fetch('userCountGroupDate?'+encodeURI(queryParam)).then(res=>{
//        if(res.ok) {
//
//            res.json().then(jsonArr=>{
//
//                for (let i = 0; i < datas.length; i++) {
//                    const dataObj = datas[i];
//
//                    for (let j = 0; j < jsonArr.length; j++) {
//                        const jsonObj = jsonArr[j];
//                        let month = dataObj.date.getMonth()+1;
//                        month = month < 10 ? ("0"+month) : month
//
//                        let day = dataObj.date.getDate();
//                        day = day < 10 ? ("0"+day) : day
//
//                        if((dataObj.date.getFullYear()+""+(month)+""+day) === jsonObj.date) {
//                            datas[i].count = jsonObj.count;
//                            jsonArr.splice(j, 1)
//                            break;
//                        }
//                    }
//
//                }
//
//                if(newCharts != undefined) {
//                    newCharts.data.datasets[0].data = datas.map(o=>o.count);
//                    newCharts.data.datasets[0].borderColor = 'rgb(72, 138, 255)'
//                    newCharts.update();
//                }
//
//            })
//
//        } else {
//            loadingCanvas();
//            $(".tab_menu > ul > li.active").removeClass("active");
//            alert("사용자 수 차트 갱신 실패")
//        }
//    })
//
//    if(newCharts == undefined) {
//        const data = {
//            labels: datas.map(o=>o.label),
//            datasets: [{
//                label: 'count',
//                data: datas.map(o=>o.count),
//                borderColor: 'rgb(72, 138, 255)'
//            }]
//        };
//        const config = {
//            type: 'line',
//            data: data,
//            options: {
//                scales: {                	
//                    y: {
//                    	min: 0,
//                        ticks: {
//                            stepSize: 1
//                        }
//                    },
//                },
//                plugins: {
//                    legend: {
//                        display: false
//                    }
//                },
//                responsive: true,
//                maintainAspectRatio: true,
//                layout: {
//                    padding: {
//                        left: 45,
//                        right: 30
//                    }
//                }
//            }
//        };
//        canvas.width = canvas.parentNode.offsetWidth;
//        canvas.height = canvas.parentNode.offsetHeight*2;
//        canvas.classList.remove("loading-canvas")
//
//        newCharts = new Chart(canvas, config)
//    }
//}

function dateFormat(date){
    let month = (date.getMonth()+1);
    month = (month < 10 ? "0" : "") + month

    let days = date.getDate();
    days = (days < 10 ? "0" : "") + days
    return date.getFullYear()+"-"+month+"-"+days
}

function loadingCanvas() {
    const d = 140;
    const ctx = canvas.getContext('2d');
    
    canvas.width = d;
    canvas.height = d;

    if (!canvas.classList.contains("loading-canvas")) {
        canvas.classList.add("loading-canvas");
    }

    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.translate(d / 2, d / 2);
    ctx.rotate(Math.PI * 360 / 360);
    ctx.lineWidth = Math.ceil(d / 50);
    ctx.lineCap = 'square';

    for (let i = 0; i <= 360; i++) {
        ctx.save();
        ctx.rotate((Math.PI * i / 180));
        ctx.beginPath();
        ctx.moveTo(0, 0);
        const opacity = (360 - (i * 0.95)) / 360;
        ctx.strokeStyle = `rgba(72, 138, 255,${opacity.toFixed(2)})`;
        ctx.lineTo(0, d + 30);
        ctx.stroke();
        ctx.closePath();
        ctx.restore();
    }

    ctx.globalCompositeOperation = 'source-out';
    ctx.beginPath();
    ctx.arc(0, 0, d / 2, 2 * Math.PI, false);
    ctx.fillStyle = 'black';
    ctx.fill();

    ctx.globalCompositeOperation = 'destination-out';
    ctx.beginPath();
    ctx.arc(0, 0, (d / 2) * .9, 2 * Math.PI, false);
    ctx.fill();

    newCharts = undefined;
}

function drawUserChart() {
    drawChart('userCountGroupDate', 'rgb(0, 128, 188)');
}

function drawFileChart() {
    drawChart('fileCountGroupDate', 'rgb(0, 128, 188)');
}

function drawChart(url, borderColor) {
    for (let i = 0; i < datas.length; i++) {
        datas[i].count = 0;
    }

    const queryParam = "startDate=" + dateFormat(startDate) + "&endDate=" + dateFormat(endDate);
    fetch(url + '?' + encodeURI(queryParam)).then(res => {
        if (res.ok) {
            res.json().then(jsonArr => {
                for (let i = 0; i < datas.length; i++) {
                    const dataObj = datas[i];
                    for (let j = 0; j < jsonArr.length; j++) {
                        const jsonObj = jsonArr[j];
                        let month = dataObj.date.getMonth() + 1;
                        month = month < 10 ? ("0" + month) : month;
                        let day = dataObj.date.getDate();
                        day = day < 10 ? ("0" + day) : day;
                        
                        if ((dataObj.date.getFullYear() + "" + month + "" + day) === jsonObj.date) {
                            datas[i].count = jsonObj.count;
                            jsonArr.splice(j, 1);
                            break;
                        }
                    }
                }
                if (newCharts !== undefined) {
                    newCharts.data.datasets[0].data = datas.map(o => o.count);
                    newCharts.data.datasets[0].borderColor = borderColor;
                    newCharts.update();
                }
            });
        } else {
            loadingCanvas();
            $(".tab_menu > ul > li.active").removeClass("active");
            alert("차트 갱신 실패");
        }
    });

    if (newCharts === undefined) {
        const data = {
            labels: datas.map(o => o.label),
            datasets: [{
                label: 'count',
                data: datas.map(o => o.count),
                borderColor: borderColor
            }]
        };
        const config = {
            type: 'line',
            data: data,
            options: {
                interaction: {
                    mode: 'index',
                    intersect: false,
                },
                scales: {
                    y: {
                        min: 0,
                        ticks: {
                            stepSize: 1
                        }
                    },
                },
                plugins: {
                    legend: {
                        display: false
                    }
                },
                responsive: true,
                maintainAspectRatio: true,
                layout: {
                    padding: {
                        left: 45,
                        right: 30
                    }
                }
            }
        };
        canvas.width = canvas.parentNode.offsetWidth;
        canvas.height = canvas.parentNode.offsetHeight * 2;
        canvas.classList.remove("loading-canvas");
        newCharts = new Chart(canvas, config);
    }
}

function onStartEndDate() {
	const startInput  = $("#searchStartDate").val();
	const endInput    = $("#searchEndDate").val();
	
	if ( startInput < endInput ) {
		
		  // 날짜 범위 가져오기
        endDate = new Date(endInput);
        startDate = new Date(startInput);
        
        initChartLabels();
        initCard();

        // 차트 그리기 함수 호출
        if ($(".user-cnt-btn").hasClass("active")) {
            drawUserChart();
        } else if ($(".file-cnt-btn").hasClass("active")) {
            drawFileChart();
        }
	}
}
