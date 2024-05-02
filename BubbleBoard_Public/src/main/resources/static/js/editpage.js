    function edit() {
    	
        var formdata = $('#editPost')[0];
        var data = new FormData(formdata);
    		$.ajax({
	            enctype: 'multipart/form-data',
	            url: "/bboard/edit",
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
    
	$(document).ready(function(){
	    $("#submitpost").click(function() {
        
        oEditors.getById["content"].exec("UPDATE_CONTENTS_FIELD", []);
        // 추가로 필요한 로직 처리
        submitContents(this);
    	}); 
	});
	
	function fDel(fname){
		var obj = {};
		obj.fname = fname;
		
		if(confirm("삭제하시겠습니까? 복구할 수 없습니다.")){
			$.ajax({
	            url: "/bboard/filedeleted",
	            type: "post",
	            cache: false,
	            data: obj,
	            dataType: "json",
	            success: function (res) {
	                if (res.result) {
	                	$('#'+fname).empty();
	                	$('[data-fname="' + fname + '"]').remove();
	                    alert("삭제 완료");
	                } else alert("삭제 실패");
	            },
	            error: function (request, status, error) {
	                alert('fail');
	                console.log("code:" + request.status + "\n" + "message:" + request.responseText + "\n" + "error:" + error);
	            }
	
	        });
		}
	}