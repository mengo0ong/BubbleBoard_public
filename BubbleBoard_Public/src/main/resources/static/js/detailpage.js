	document.addEventListener("DOMContentLoaded", function() {
	    document.querySelector(".reply-form").addEventListener("submit", function(event) {
	        var textarea = document.getElementById("replyText");
	        if (textarea.value.trim() === "") {
	            alert("댓글 내용을 입력하세요.");
	            event.preventDefault();
	        }
	    });
	});

	function deleted(bnum){
		var obj = {};
		obj.bnum= bnum;
		
		if(confirm("삭제하시겠습니까? 복구할 수 없습니다.")){
			$.ajax({
	            url: "/bboard/deleted",
	            type: "post",
	            cache: false,
	            data: obj,
	            dataType: "json",
	            success: function (res) {
	                if (res.result) {
	                    alert("삭제 완료");
	                    location.href = "/bboard/main";
	                } else alert("삭제 실패");
	            },
	            error: function (request, status, error) {
	                alert('fail');
	                console.log("code:" + request.status + "\n" + "message:" + request.responseText + "\n" + "error:" + error);
	            }
	
	        });
		}
	}
	
    function toggleReplyForm(formId) {
        var form = document.getElementById(formId);
        if (form.style.display === "none") {
            form.style.display = "block";
        } else {
            form.style.display = "none";
        }
    }