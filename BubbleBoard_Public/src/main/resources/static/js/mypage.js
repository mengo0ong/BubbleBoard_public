    function showUploadForm() {
        var imageChangeButton = document.getElementById("imageChangeButton");
        
        imageChangeButton.style.display = "none";

        var uploadForm = document.getElementById("imgUploadForm");
        uploadForm.style.display = "block";
    }

    function uploadImage() {
        var formdata = $('#imgUploadForm')[0];
        var data = new FormData(formdata);
        
		$.ajax({
            enctype: 'multipart/form-data',
            url: "/buser/userimgupdate",
            type: "post",
            cache: false,
            data: data,
            dataType: "json",
            processData: false,
            contentType: false,
            timeout: 600000,
            success: function (res) {
                if (res.result) {
                	alert("이미지 변경 완료");
                    location.href = "/buser/mypage?unum=" + res.unum;
                } else {
                	alert("이미지 변경 실패");
                	location.href = "/buser/mypage?unum=" + res.unum;
                }
            },
            error: function (request, status, error) {
                alert('fail');
                console.log("code:" + request.status + "\n" + "message:" + request.responseText + "\n" + "error:" + error);
            }
		})
    }
    
    function showNicknameChangeForm() {
        var nicknameChangeBtn = document.getElementById("nicknameChangeBtn");
        var userNickname = document.getElementById("userNickname");
        
        nicknameChangeBtn.style.display = "none";
        userNickname.style.display = "none";

        
        var nickNameForm = document.getElementById("nickNameForm");
        nickNameForm.style.display = "flex";
        
    }
    
    function nicknameChange() {
        var data = $('#nickNameForm').serialize();
        
		$.ajax({
            url: "/buser/usernicknamechange",
            type: "post",
            cache: false,
            data: data,
            dataType: "json",
            success: function (res) {
                if (res.result) {
                	alert("닉네임 변경 완료");
                    location.href = "/buser/mypage?unum=" + res.unum;
                } else {
                	alert("닉네임 변경 실패");
                	location.href = "/buser/mypage?unum=" + res.unum;
                }
            },
            error: function (request, status, error) {
                alert('fail');
                console.log("code:" + request.status + "\n" + "message:" + request.responseText + "\n" + "error:" + error);
            }
		})
    }
    
    function unlinkCheck(){
        var result = confirm("정말로 계정을 탈퇴하시겠습니까?");
        if (result) {
            window.location.href = document.getElementById('unlink').getAttribute('href');
        }else event.preventDefault();
    }