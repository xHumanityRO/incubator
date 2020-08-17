<#if karmaEnabled>
	function likeVote(postId) {
		if (confirm("${I18n.getMessage("Like.confirmVote")}")) {
			document.location = "${contextPath}/like/insert/${start}/" + postId + "/1${extension}";
		}
	}

	function likeImage(postId)
	{
		document.write('<a href="${contextPath}/like/insert/${start}/' + postId + '/1${extension}"' + ' id="like' + postId + '" onclick="return likeVote(' + postId + ');"><img src="${contextPath}/templates/${templateName}/images/like.png" alt="[Like]" /></a>');  
	}

	function dislikeVote(postId) {
		if (confirm("${I18n.getMessage("Like.confirmVote")}")) {
			document.location = "${contextPath}/like/insert/${start}/" + postId + "/-1${extension}";
		}
	}

	function dislikeImage(postId)
	{
		document.write('<a href="${contextPath}/like/insert/${start}/' + postId + '/-1${extension}"' + ' id="dislike' + postId + '" onclick="return dislikeVote(' + postId + ');"><img src="${contextPath}/templates/${templateName}/images/dislike.png" alt="[Dislike]" /></a>');  
	}
</#if>