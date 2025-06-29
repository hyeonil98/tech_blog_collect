function search(type='ALL', keyword="") {
    $("#search_type").val(type);
    $("#search_page").val(0);
    $("#search_keyword").val(keyword);
    $("#search_form").submit();
}

function movePage(page) {
    $("#search_page").val(page);
    $("#search_form").submit();
}

$("#searchInput").on("keyup",function(key) {
    let keyword = $("#searchInput").val();
    let type = $("#search_type").val();
    if (key.keyCode === 13) {
        search(type, keyword);
    }
});

$("#startBtn").on("click", function (event) {
    $("#loading-screen").show();

    $.ajax({
        url: '/run-batch',
        method: 'GET',
        success: function (data) {
        },
        error: function () {
            alert('크롤링에 실패했습니다.');
        }
    });
    $("#loading-screen").hide();
});