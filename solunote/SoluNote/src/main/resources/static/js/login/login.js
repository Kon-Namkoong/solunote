window.onload=function (){
    var contextPath = document.getElementById('contextPathHolder').getAttribute('data-contextpath')
    var cookieId = "sUserId";
    
    sessionStorage.setItem('activeMenu', "side_dashbord")    
    sessionStorage.removeItem('activeSubMenu');
    
    if (getCookie(cookieId)) { // getCookie함수로 id라는 이름의 쿠키를 불러와서 있을경우
        document.getElementById("username").value = getCookie(cookieId)
        document.getElementById("remember-box").checked = true;
        document.getElementById("remember-box").parentElement.classList.add("active");
    }

    document.getElementById("remember-box").addEventListener("click", function(e) {
        const parent = e.target.parentElement;
        if (e.target.checked) {
            parent.classList.add("active");
        } else {
            parent.classList.remove("active");
        }
    });

    document.getElementById("frmLogin").addEventListener("submit", function (event) {
        event.preventDefault();
        var username = document.getElementById("username").value;
        var password = document.getElementById("password").value
		
        if(username == '') {
            alert("아이디를 입력해 주십시오.")
            return false;
        }

        if(password == '') {
            alert("비밀번호를 입력해 주십시오.")
            return false;
        }
		if (document.getElementById("remember-box").checked) {
		    setCookie(cookieId, username, 7); // 쿠키를 7일 동안 설정
		} else {
		    setCookie(cookieId, username, 0); //날짜를 0으로 저장하여 쿠키삭제
		}

		//        const formData = new FormData(event.target);
		        
		const details = {
			'username': username,
			'password': password
		};

		let formBody = [];
		for (const property in details) {
		    const encodedKey = encodeURIComponent(property);
		    const encodedValue = encodeURIComponent(details[property]);
		    		formBody.push(encodedKey + "=" + encodedValue);
		}
    	formBody = formBody.join("&");

        fetch(event.target.action, {
            method: "POST",
	//      body: formData,
	        body: formBody,
            headers: {
		                "Accept": "application/json",
		                "Content-Type":	"application/x-www-form-urlencoded"
	        	}
		}).then(res=>{
		    if(res.ok) {
		        res.json().then(json => {
		        	if(json.result =='success') {
		                location.href=contextPath;
		            } else {
		                        alert("로그인 실패! 로그인 정보를  확인 후, 다시 시도해 주세요.");
		            }
		        })
		    } else {
		        alert("로그인 실패! 로그인 정보를  확인 후, 다시 시도해 주세요.");
		    }
		})
    })

}



function getCookie(Name) { // 쿠키 불러오는 함수
    var search = Name + "=";
    if (document.cookie.length > 0) { // if there are any cookies
        offset = document.cookie.indexOf(search);
        if (offset != -1) { // if cookie exists
            offset += search.length; // set index of beginning of value
            end = document.cookie.indexOf(";", offset); // set index of end of cookie value
            if (end == -1)
                end = document.cookie.length;
            return unescape(document.cookie.substring(offset, end));
        }
    }
}

function setCookie(name, value, expiredays){//쿠키저장함수
    var todayDate = new Date();
    todayDate.setDate(todayDate.getDate() + expiredays);
    document.cookie = name + "=" + escape(value) + "; path=/; expires="
        + todayDate.toGMTString() + ";"
}

