$(document).ready( function() {
	document.body.style.overflowY = "hidden";
	
	$('.logout.tooltip').click(function (event) {
	    event.preventDefault(); 

	    if (confirm("로그아웃 하시겠습니까?")) {
	        window.location.href = "/SGSAS/user/logout"; 
	    }
	});
	
    let activeMenu = sessionStorage.getItem('activeMenu');
    let activeSubMenu = sessionStorage.getItem('activeSubMenu');

    // 활성화된 대메뉴 복원
    if (activeMenu) {    	
        var $activeMainMenu = $('#' + activeMenu);
        $activeMainMenu.addClass('active');
        var $subMenu = $activeMainMenu.next('.sub_menu');
        if ($subMenu.length) {
            $subMenu.show(); // 서브메뉴가 있으면 열어둠
        }
    }

    // 활성화된 서브메뉴 복원
    if (activeSubMenu) {
        $('.sub_menu li a[href="' + activeSubMenu + '"]').parent('li').addClass('active');
    }

    // 대메뉴 클릭 이벤트
    $('.main_menu > li > a').click(function () {
        var $subMenu = $(this).next('.sub_menu');
        var $currentLink = $(this);
        
        // 모든 서브메뉴 닫기, 클릭한 메뉴 제외
        $('.sub_menu').not($subMenu).slideUp('fast', function () {
            $(this).prev('a').removeClass('active');
        });

        // 서브메뉴가 있을 때만 서브메뉴 열고 active 상태 처리
        if ($subMenu.length) {
            $subMenu.slideToggle('fast', function () {
                if ($subMenu.is(':visible')) {
                    $currentLink.addClass('active');
                    sessionStorage.setItem('activeMenu', $currentLink.attr('id')); // 활성화된 대메뉴 저장
                } else {
                    $currentLink.removeClass('active');
                    sessionStorage.removeItem('activeMenu'); // 비활성화된 경우 저장된 메뉴 삭제
                }
            });
        } else {
            // 소메뉴가 없는 경우 클릭 시 대메뉴에 active 추가
            $('.main_menu > li > a').removeClass('active'); // 다른 대메뉴 active 제거
            $currentLink.addClass('active'); // 현재 클릭한 대메뉴에만 active 추가
            sessionStorage.setItem('activeMenu', $currentLink.attr('id')); // 활성화된 대메뉴 저장
        }
    });

    // 소메뉴 클릭 시 active 클래스 추가 및 저장
    $('.sub_menu li a').click(function () {
        $('.sub_menu li').removeClass('active');
        $(this).parent('li').addClass('active');
        sessionStorage.setItem('activeSubMenu', $(this).attr('href')); // 활성화된 서브메뉴 저장
    });

    // 서브메뉴 포커스 이벤트 처리
    $('.sub_menu li a').focus(function () {
        $('.sub_menu li').removeClass('active');
        $(this).parent('li').addClass('active');
    }).blur(function () {
        var that = this;

        // 일정 시간 후 포커스가 사라지면 서브메뉴 닫기
        setTimeout(function () {
            if (!$(that).closest('.sub_menu').find('li a').is(':focus')) {
                $(that).closest('.sub_menu').find('li').removeClass('active');
                var $mainMenuLink = $(that).closest('.main_menu > li').children('a');

                // 메인메뉴가 활성화 상태가 아니면 서브메뉴 닫기
                if (!$mainMenuLink.hasClass('active')) {
                    $(that).closest('.sub_menu').slideUp('fast');
                }
            }
        }, 100);
    });


    function openMenu() {
        var $element = $('ul.main_menu > li.first_menu > a span');
        var $subMenu = $('ul.main_menu > li.first_menu > a.active > ul.sub_menu');
        var $firstMenuLink = $('ul.main_menu > li.first_menu > a.active');

        $('.side_menu').stop(true, true).animate({ width: '220px' }, 100).promise().done(function () {
            $element.stop(true, true).fadeIn(300).css('display', 'block').promise().done(function () {
                $('ul.main_menu>li.first_menu').removeClass('short');
                $('button.menu_short').removeClass('open');
                $('.side_menu').removeClass('short');
                $('ul.side_top_menu').removeClass('short_active');

                if ($firstMenuLink) {
                    $subMenu.stop(true, true).slideDown(100);
                }
            });
        });
    }   
    

    function closeMenu() {
        var $element = $('ul.main_menu > li.first_menu > a span');
        var $subMenu = $('ul.main_menu > li.first_menu > a.active > ul.sub_menu');

        $element.stop(true, true).fadeOut(600).css('display', 'none');
        $('.side_menu').stop(true, true).animate({ width: '60px' }, 300);
        $('ul.main_menu>li.first_menu').addClass('short');
        $('button.menu_short').addClass('open');
        $('.side_menu').addClass('short');
        $('ul.side_top_menu').addClass('short_active');
        
        $subMenu.stop(true, true).slideUp(300);
    }

    // 버튼 클릭 시 메뉴 열기/닫기
    $('button.menu_short').click(function () {
        if ($(this).hasClass('open')) {
            openMenu();
            $('.menu_wrap').off('mouseenter mouseleave');
        } else {
            closeMenu();
            $('.menu_wrap').hover(openMenu, closeMenu);
        }
    });

    // 화면 크기가 1440px 이하일 때 자동으로 메뉴 간소화
    $(window).resize(function () {
        if ($(window).width() <= 1440) {
            closeMenu();
            $('.menu_wrap').hover(openMenu, closeMenu);
        } else {
            openMenu();
            $('.menu_wrap').off('mouseenter mouseleave');
        }
    }).trigger('resize');
    


    // 탭메뉴
    $('.tab').click(function () {
        var index = $(this).index();

        $('.tab').removeClass('active');
        $('.tab_item').removeClass('active');

        $(this).addClass('active');
        $('.tab_item').eq(index).addClass('active');
    });


//    // 테이블 체크박스
//    $('.check-box').click(function () {
//        if ($(this).hasClass('all')) {
//            var isActive = $(this).hasClass('active');
//            $('.check-box').each(function () {
//                if (isActive) {
//                    $(this).removeClass('active');
//                } else {
//                    $(this).addClass('active');
//                }
//            });
//        } else {
//            $(this).toggleClass('active');
//        }
//    });

    // 즐겨찾기 체크
//    $('.favorites-check').click(function () {
//        $(this).toggleClass('active');
//    });


    // 회원정보 레이어 팝업
    $('.admin').on('click', function (event) {
        event.stopPropagation();
        if ($('.admin_popup').is(':visible')) {
            $('.admin_popup').fadeOut(100);
        } else {
            $('.admin_popup').fadeIn(100);
        }
    });

    $(document).on('click', function (event) {
        if (!$(event.target).closest('.admin_popup, .admin').length) {
            $('.admin_popup').fadeOut(100);
        }
    });
    
    let observer = new MutationObserver(function () {
        $('.tbl_scroll').each(function () {
            let $scrollElement = $(this);
            let $topBtn = $('.top_btn');

            if ($scrollElement.prop('scrollHeight') > $scrollElement.innerHeight()) {
                $topBtn.show();
            } else {
                $topBtn.hide();
            }
        });
    });

    $('.tbl_scroll').each(function () {
        observer.observe(this, { childList: true, subtree: true });
    });

});


// 캘린더 팝업
$(function () {
    var startDateTextBox = $('.start-date');
    var endDateTextBox = $('.end-date');

    startDateTextBox.datepicker({
        dateFormat: "yy-mm-dd",
        onClose: function (selectedDate) {
            endDateTextBox.datepicker("option", "minDate", selectedDate);
        }
    });

    endDateTextBox.datepicker({
        dateFormat: "yy-mm-dd",
        onClose: function (selectedDate) {
            startDateTextBox.datepicker("option", "maxDate", selectedDate);
        }
    });
});


document.addEventListener('DOMContentLoaded', function () {
    // 테이블 top버튼
    document.querySelector('.top_btn a').addEventListener('click', function (e) {
        e.preventDefault();
        let scrollElements = document.querySelectorAll('.table_content .tbl_scroll');
        scrollElements.forEach(function (element) {
            // 요소의 display 상태 확인
            if (window.getComputedStyle(element).display !== 'none') {
                element.scrollTo({
                    top: 0,
                    behavior: 'smooth'
                });
            }
        });
    });

    // 요약내용 li가 2개 이상일 때
    const ulElements = document.querySelectorAll('.sum_grid_cont_box.scroll_custom > ul');

    ulElements.forEach(function (ul) {
        const listItems = ul.querySelectorAll('li');

        if (listItems.length > 1) {
            listItems.forEach(function (item) {
                item.classList.add('more-than-one');
            });
        }
    });


    const listItems = document.querySelectorAll('.attend_list ul li');

    listItems.forEach(function (item) {
        item.addEventListener('click', function () {
            listItems.forEach(function (li) {
                li.classList.remove('active');
            });
            item.classList.add('active');
        });
    });
    
   
}); 