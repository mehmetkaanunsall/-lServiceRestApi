
/**
 * Bu js dosyası sistem içerisinde kullanılan harici javascript kodlarını barındırır.
 *
 *
 * @author Cihat Kucukbagriacik
 *
 * @date   10.09.2016 14:56:40
 *
 *
 */
$(function () {

    /**
     * Mobile görünümde menünün tıklandığında kapanmasını sağlar     
     */
    $(".sm_modules > ul > li").click(function () {
        if ($(document).width() < 640) {
            $("#layout-menubar-resize2").trigger("click");
        }
    });


    /**
     * Üst menüdeki menünün tıklanınca geri kapanmasını sağlar
     */
    $("#sm-topmenu li a").on('click', function () {
        $('#sm-mobiletopmenu').trigger("click");
    });

});




var Centrowiz = {
    init: function () {
        alert("Centrowiz");
    },
    readJson: function (url) {
        var listArray;
        $.ajax({
            url: url,
            async: false,
            dataType: 'json',
            success: function (response) {
                listArray = response;
            }
        });
        console.debug(listArray);
        return listArray;
    },
    removeVariable: function () {
        delete window.maskList;
        delete window.returnSelects;
        delete window.selectedCode;
        delete window.c;
    },
    panelEffect: function (formId, direction) {
        $(formId).hide("slide", {direction: direction}, "slow");
    },
    panelToggle: function () {
        $('.panelToggle').slideToggle();
        $('.panelToggleRotate').toggleClass('down');
    },
    panelClose: function () {
        $('.panelToggle').slideUp();
        $('.panelToggleRotate').addClass('down');
    },
    panelAndTabEffect: function (formId, tabId, direction) {        //Limit ve Ön Ödeme de kullanıldı.
        $(formId).hide("slide", {direction: direction}, "slow");
        $(tabId).hide("slide", {direction: direction}, "slow");
    },
    openCloseCategory: function () {
        var divWidth = 0;
        var categoryDiv = $(".categoryDiv");
        var divTable = $(".divTable");
        if (categoryDiv.hasClass("open")) {
            $("#frmCategorization\\:ftrPanel").hide();
            divWidth = 0;
            categoryDiv.removeClass("open").addClass("close");
        } else if (categoryDiv.hasClass("close")) {
            $("#frmCategorization\\:ftrPanel").show();
            divWidth = 250;
            categoryDiv.removeClass("close").addClass("open");
        }
        $('.btnRotate').toggleClass('down');
        if (divWidth != 0)
            categoryDiv.show();
        categoryDiv.animate(700, function () {
            if (divWidth == 0)
                categoryDiv.hide();
        });
        if (divWidth == 0) {
            divTable.removeClass("ui-lg-10").addClass("ui-lg-12");
        } else {
            divTable.removeClass("ui-lg-12").addClass("ui-lg-10");
        }

    }
}