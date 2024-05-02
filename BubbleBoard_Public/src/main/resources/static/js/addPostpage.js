function add() {
	var replyT = $('#pBoard').val();
	console.log(replyT);
	
	var formdata = $('#addPost')[0];
	var data = new FormData(formdata);
	
	if(replyT==''){
		$.ajax({
			enctype: 'multipart/form-data',
			url: "/bboard/add",
			type: "post",
			cache: false,
			data: data,
			dataType: "json",
			processData: false,
			contentType: false,
			timeout: 600000,
			success: function (res) {
				if (res.result) {
					alert("게시글 작성 완료");
					location.href = "/bboard/detail?bnum=" + res.bnum;
					} else alert("게시글 작성 실패");
				},
				error: function (request, status, error) {
					alert('fail');
					console.log("code:" + request.status + "\n" + "message:" + request.responseText + "\n" + "error:" + error);
				}
		});
    	return false;
	}else {
		$.ajax({
            enctype: 'multipart/form-data',
            url: "/bboard/addreply",
            type: "post",
            cache: false,
            data: data,
            dataType: "json",
            processData: false,
            contentType: false,
            timeout: 600000,
            success: function (res) {
                if (res.result) {
                    alert("게시글 작성 완료");
                    location.href = "/bboard/detail?bnum=" + res.bnum;
                } else alert("게시글 작성 실패");
            },
            error: function (request, status, error) {
                alert('fail');
                console.log("code:" + request.status + "\n" + "message:" + request.responseText + "\n" + "error:" + error);
            }

        });
        return false;
	}        
}


	    
$(document).ready(function(){
    $("#submitpost").click(function() {
	    oEditors.getById["content"].exec("UPDATE_CONTENTS_FIELD", []);
	    // 추가로 필요한 로직 처리
	    submitContents(this);
    }); 
});