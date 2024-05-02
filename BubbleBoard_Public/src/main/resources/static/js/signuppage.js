    // 이메일 중복 검사
    var emailck = 0;
    function emailCheck() {
        var email = $('#email').val();
        var obj = {};
        obj.email = email;

        if (expEmail()) {
            $.ajax({
                url: '/buser/emailcheck',
                type: 'post',
                cache: false,
                data: obj,
                dataType: 'json',
                success: function (res) {
                    if (res.result) {
                        alert('중복된 이메일입니다.');
                        $('#email').focus();
                    } else {
                        alert('사용가능한 이메일 입니다.');
                        $('#email').focus();
                        emailck = 1;
                        document.getElementById("emailcheck").innerHTML="";
                    }
                },
                error: function (xhr, status, err) {
                    alert(err);
                }
            });
        }else {
			alert("작성한 이메일을 다시 확인하세요");
		}
    }
    
    var pwdck = 0;
    function pwdCheck() {
        if (expPwd()) {
			alert("비밀번호 확인 완료");
			pwdck=1;
			document.getElementById("pwdcheck").innerHTML="";
        }
        $('#userPwd').focus();
    }


    // 유효성 검사
    function expEmail() {
        var email = $('#email').val();
        var etest = /^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-Za-z0-9\-]+/;
        console.log(email);
        if (etest.test(email) == false) {
            document.getElementById("emailcheck").innerHTML = "올바른 이메일 형식이 아닙니다";
            $('#email').focus();
            return false;
        } else return true;
    }

    function expPwd() {
        var pwd = $('#userPwd').val();
        var ptest = /^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_]).{7,}$/;
        if (ptest.test(pwd) == false) {
            document.getElementById("pwdcheck").innerHTML = "올바른 비밀번호 형식이 아닙니다";
            $('#userPwd').focus();
            return false;
        } else return true;
    }

    // 유효성 검사 고지
    function notiExpEmail() {
        document.getElementById("emailcheck").innerHTML = "예시: bubbleboard@naver.com"
    }

    function notiExpPwd() {
        document.getElementById("pwdcheck").innerHTML = "영문,숫자,특수문자 모두 포함해야 허용하며 2개 이상 조합";
    }

	function signUp(){
		
		var nickName = $('#nickName').val();
		var userPwd=$('#userPwd').val();
		var email=$('#email').val();
	
		if(nickName==""||nickName==null){
			alert('이름을 입력해주세요');
			$('#nickName').focus();
			return false;
		}
		
		if(email==""||email==null){
			alert('이메일을 입력해주세요');
			$('#email').focus();
			return false;
		}
		
		if(userPwd==""||userPwd==null){
			alert('비밀번호를 입력해주세요');
			$('#userPwd').focus();
			return false;
		}		
		
		//이메일 유효성 검사
		if(emailck==0) {
			$('#email').focus();
			return false;
		}
		
		//비밀번호 유효성 검사
		if(pwdck==0) {
			$('#userPwd').focus();
			return false;
		}	
		
	}