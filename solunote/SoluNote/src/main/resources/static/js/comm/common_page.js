let 	popupPageNo = 1;
const 	popupPageDepth = 5;
const	DefaultPageSize = 10;

function popupPagination(totalContent, pageSize){

	let	localPageSize = pageSize
	if (undefined == localPageSize)
	{
		localPageSize = DefaultPageSize		
	}
	
    const totalPageCnt = Math.ceil(totalContent / localPageSize);
    const startPageNo = parseInt((popupPageNo - 1) / popupPageDepth) * popupPageDepth + 1

    let endPageNo = (totalPageCnt === popupPageNo)?popupPageNo:(startPageNo+popupPageDepth-1);
    endPageNo = totalPageCnt < endPageNo ? totalPageCnt : endPageNo;
    
    $(".tbl_pagination_detail .arrow.first").attr("page-no", 1);
    $(".tbl_pagination_detail .arrow.prev").attr("page-no", popupPageNo-1)
    $(".tbl_pagination_detail .arrow.first").css("display","")
    $(".tbl_pagination_detail .arrow.prev").css("display","")    
     
    
    if(popupPageNo > 1 && endPageNo > 1) {
    	$(".tbl_pagination_detail .arrow.first").removeClass("disabled")
        $(".tbl_pagination_detail .arrow.prev").removeClass("disabled")
    } else {
    	$(".tbl_pagination_detail .arrow.first").removeClass("disabled").addClass("disabled")
        $(".tbl_pagination_detail .arrow.prev").removeClass("disabled").addClass("disabled")
    }

    let pageDom = "";
    for (let i = 0; i < endPageNo+1-startPageNo; i++) {
        const crnpopupPageNo = (i+startPageNo);
        pageDom += "<li class= 'numli'><a class='num "+(crnpopupPageNo === popupPageNo ? 'active':'')+" 'page-no='"+crnpopupPageNo+"'>"+(crnpopupPageNo)+"</a></li>";
        
    }
    
    $(".tbl_pagination_detail .numli").remove();
    $(".tbl_pagination_detail ul li:nth-child(2)").after(pageDom);

    $(".tbl_pagination_detail .arrow.next").attr("page-no", popupPageNo+1);
    $(".tbl_pagination_detail .arrow.last").attr("page-no", totalPageCnt);
    $(".tbl_pagination_detail .arrow.next").css("display","")
    $(".tbl_pagination_detail .arrow.last").css("display","")

    if(totalPageCnt > popupPageNo) {
    	$(".tbl_pagination_detail .arrow.next").removeClass("disabled")
        $(".tbl_pagination_detail .arrow.last").removeClass("disabled")
    } else {
    	$(".tbl_pagination_detail .arrow.next").removeClass("disabled").addClass("disabled")
        $(".tbl_pagination_detail .arrow.last").removeClass("disabled").addClass("disabled")
    }

}


let pageNo = 1;
const pageDepth = 5;

function contentPagination(totalContent, pageSize){
	
	let	localPageSize = pageSize;
	if (undefined = localPageSize)
	{
		localPageSize = DefaultPageSize		
	}
	
    const totalPageCnt = Math.ceil(totalContent / localPageSize);
    const startPageNo = parseInt((pageNo - 1) / pageDepth) * pageDepth + 1

    let endPageNo = (totalPageCnt === pageNo)?pageNo:(startPageNo+pageDepth-1);
    endPageNo = totalPageCnt < endPageNo ? totalPageCnt : endPageNo;

    console.log("totalContents = ", totalContent)
	console.log("pageSize      = ", pageSize)
	
    
    $(".tbl_pagination .arrow.first").attr("page-no", 1);
    $(".tbl_pagination .arrow.prev").attr("page-no", pageNo-1)
    $(".tbl_pagination .arrow.first").css("display","")
    $(".tbl_pagination .arrow.prev").css("display","")    
     
    
    if(pageNo > 1 && endPageNo > 1) {
    	$(".tbl_pagination .arrow.first").removeClass("disabled")
        $(".tbl_pagination .arrow.prev").removeClass("disabled")
    } else {
    	$(".tbl_pagination .arrow.first").removeClass("disabled").addClass("disabled")
        $(".tbl_pagination .arrow.prev").removeClass("disabled").addClass("disabled")
    }

    let pageDom = "";
    for (let i = 0; i < endPageNo+1-startPageNo; i++) {
        const crnpopupPageNo = (i+startPageNo);
        pageDom += "<li class= 'numli'><a class='num "+(crnpopupPageNo === pageNo ? 'active':'')+" 'page-no='"+crnpopupPageNo+"'>"+(crnpopupPageNo)+"</a></li>";
    }
    
    $(".tbl_pagination .numli").remove();
    $(".tbl_pagination ul li:nth-child(2)").after(pageDom);

    $(".tbl_pagination .arrow.next").attr("page-no", pageNo+1);
    $(".tbl_pagination .arrow.last").attr("page-no", totalPageCnt);
    $(".tbl_pagination .arrow.next").css("display","")
    $(".tbl_pagination .arrow.last").css("display","")

    if(totalPageCnt > pageNo) {
    	$(".tbl_pagination .arrow.next").removeClass("disabled")
        $(".tbl_pagination .arrow.last").removeClass("disabled")
    } else {
    	$(".tbl_pagination .arrow.next").removeClass("disabled").addClass("disabled")
        $(".tbl_pagination .arrow.last").removeClass("disabled").addClass("disabled")
    }
}
