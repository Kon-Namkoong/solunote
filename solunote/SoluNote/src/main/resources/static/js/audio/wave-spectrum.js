function appendWaveSpectrum(url) {
	
  	fetch(url, {
	}).then(res => {
		if (res.redirected == true) {
			window.location.href = res.url;
		} else if (res.ok) {
			return res.json();
		} else {
			return Promise.reject(res);
		}
	  }).then(jsonArr=>{
	        addSpectrum(jsonArr)
	}).catch(err => {
		console.log(err)
		//alert("페이지 갱신 실패 4")
	})
	
}

function addSpectrum(arr){
    for (let i = 0; i < arr.length; i++) {
        let obj = arr[i];
        $("#img-" + obj.seq + " *").remove();   // 하위요소를 삭제
        $("#img-" + obj.seq).append('<img class="wave" src="data:image/png;base64,' + obj.image + '"/>');
    }
}

