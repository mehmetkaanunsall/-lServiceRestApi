
/**
 * 
 * Detaylı Arama içerisinde kullanılan methodları içerisi
 * @author : MakbuleDemirel
 * 
 */

function setCriteria()
{
       $('#no-display-criteria-list').val($('.savedFilter select').val());
       var criterias = jQuery.parseJSON($('#no-display-criteria-list option:selected').attr("data-filter"));
       
       
       fillSearchCriteriaDivFromSelected(criterias);

   // items = criterias;
    //var criterias = jQuery.parseJSON('{"code":[{"field_value":"test","type":"like"},{"field_value":"a","type":"equal"}],"name":[{"field_value":"bc","type":"equal"}]}');
    
       
    
}
var isToggled = true;
var items = {};
var i = "#{potentialcustomer.selectedCriteria.name}";
$(".add-criteria-button").on('click', function () {
    var ownerId = $(this).data("owner-id");
    var dataType = $('#' + ownerId).attr("data-type");
    var dbTable = $('#' + ownerId).attr("db-table");
    var dbColumn = $('#' + ownerId).attr("db-column");

    if (typeof ownerId != "undefined" && ownerId != "") {

        var ownerText = $("[data-group-name='" + ownerId + "'] label:first").text();
        var criteriaHtml = $("<div>" + $(".criteria-html-base").html() + "</div>");
        var targetElement = $("#" + ownerId);

        if (targetElement.length > 0 && targetElement.val() != "") {
            var targetGroup = $("[data-criteria-group='" + ownerId + "']");

            if (targetElement.is("select")) {
                var targetMultiple = targetElement.attr("multiple");
                if (targetMultiple) {
                    var inputText = [];
                    var inputValue = [];
                    $("option:selected", targetElement).each(function () {
                        inputText.push($(this).text());
                        inputValue.push($(this).attr("value"));
                    })
                } else {
                    var inputText = [$("option:selected", targetElement).text()];
                    var inputValue = [targetElement.val()];
                }
            } else {
                var inputText = [targetElement.val()];
                var inputValue = [targetElement.val()];
            }

            $("section.panel", criteriaHtml).attr("data-criteria-group", ownerId);
            $(".panel-heading-success .text", criteriaHtml).html(ownerText);

            for (var i in inputText) {
                var targetText = inputText[i];
                var targetValue = inputValue[i];
                if (targetText && targetValue) {
                    if ($(".date", targetElement.closest(".form-group")).length > 0) {
                        var dateObj = $(".date", targetElement.closest(".form-group"));
                        var dateTimePicker = dateObj.data("datetimepicker");
                        if (dateTimePicker && typeof dateTimePicker == "object") {
                            targetValue = moment(dateTimePicker.getDate()).format("YYYYMMDDHHmm00");
                            targetText = targetText.substring(0, targetText.length - 2) + "00";
                        }
                    }

                    if (targetGroup.length < 1) {
                        $(".search-criteria-body").html($(".search-criteria-body").html() + criteriaHtml.html());
                        targetGroup = $("[data-criteria-group='" + ownerId + "']");
                    }
                    if ($(".criteria-value[data-value='" + targetValue + "']", targetGroup).length < 1) {
                        var appendData = $("<li class='list-equal search-criteria-list-item' data-type='" + dataType + "'><span class='badge switch-criteria-type badge-sm label-equal'>&nbsp;</span> <span class='criteria-value' data-value='" + targetValue + "'>" + targetText + "</span><i class='fa fa-times remove-criteria'></i></li>");
                        appendData.data("value", targetValue).data("type", "equal");
                        $(".criteria-list", targetGroup).append(appendData);
                        targetElement.val("");

                        addCriteriaToJson(inputValue, ownerId, dbTable, dbColumn);
                    }
                }
            }

        }
    }
    $(".criteria-list").sortable();
    $(".criteria-list").disableSelection();

    checkCriteriaGroupCount();
});


/**
 * Tüm kriterlerin HTML içerisinden silinmesi için
 */
$(".search-criteria-head").on('click', '.remove-group', function () {
    var _self = $(this);
    var r = confirm("Bütün filtreleri temizlemek istediğinize emin misiniz?");
    if (r) {
        _self.closest(".search-criterias").find(".search-criteria-body").html("");
        removeCriteriaFromJson("all");
        checkCriteriaGroupCount();
    }
});

/**
 * Gruba ait kriterlerin HTML içerisinden silinmesi için
 */
$(".search-criteria-body").on('click', '.remove-group', function () {
    var _self = $(this);
    var r = confirm("Gruba ait bütün filtreleri temizlemek istediğinize emin misiniz?");
    if (r) {
        removeCriteriaFromJson("group", _self);
        _self.closest("section.panel").remove();
        checkCriteriaGroupCount();
    }
});
/**
 * Kriterlerin tiplerinin değiştirilmesi için (eşit, içerir,büyük,vs)
 */
$(".search-criteria-body").on('click', '.switch-criteria-type', function () {
    var switchTypesForDefault = ["equal", "notequal", "contains", "startwith", "endwith"];
    var switchClassesForTextbox = ["equal", "notequal", "contains", "startwith", "endwith"];
    var switchClassesForDate = ["equal", "notequal", "lessthan", "greaterthan"];
    var switchClassesForNumber = ["equal", "notequal", "lessthan", "greaterthan"];
    var switchClassesForSelect = ["equal", "notequal"];
    var switchTypes = [];

    var dataType = $(this).closest("li").attr('data-type');

    if (dataType === "text")
        switchTypes = switchClassesForTextbox;
    else if (dataType === "number")
        switchTypes = switchClassesForNumber;
    else if (dataType === "select")
        switchTypes = switchClassesForSelect;
    else if (dataType === "date")
        switchTypes = switchClassesForDate;
    else
        switchTypes = switchTypesForDefault;


    var ownerItem = $(this).closest("li.search-criteria-list-item");
    var currentType = ownerItem.data("type");
    var currentPosition = switchTypes.indexOf(currentType);
    var nextPosition = currentPosition + 1;
    var ownerId = $(this).closest("section").attr('data-criteria-group');
    var field_value = $(this).closest("li").find('.criteria-value').attr('data-value');


    if (nextPosition === switchTypes.length) {
        nextPosition = 0;
    }

    items[ownerId] = items[ownerId].filter(
            function (obj) {
                if (obj.field_value === field_value)
                    obj.type = switchTypes[nextPosition];
                return obj;
            }
    );

    $(this).removeClass("label-" + switchTypes[currentPosition]).addClass("label-" + switchTypes[nextPosition]);
    ownerItem.removeClass("list-" + switchTypes[currentPosition]).addClass("list-" + switchTypes[nextPosition]).data("type", switchTypes[nextPosition]);

    fillCriteriaInputbox();

});

/**
 * Tek bir kriterin HTML içerisinden silinmesi için
 */
$(".search-criteria-body").on('click', '.remove-criteria', function () {
    removeCriteriaFromJson("one", $(this));
    var parent = $(this).closest("ul");
    $(this).closest('li.search-criteria-list-item').remove();
    if ($("li", parent).length < 1) {
        parent.closest("[data-criteria-group]").remove();
    }
    checkCriteriaGroupCount();
});

/**
 * Eklenen kriterleri kontrol ederek, kapatma butonlarını aktif-deaktif eder
 */
function checkCriteriaGroupCount() {
    if ($("[data-criteria-group]").length > 0) {
        $(".search-criteria-head .remove-group").removeClass("display-none");
    } else {
        $(".search-criteria-head .remove-group").addClass("display-none");
    }
}
/**
 * Sol panelin açık ve kapalı olma durumunu kontrol ederek açma ve kapama işlemini gerçekleştirir
 */
function changeDisplay()
{
    

    if (isToggled)
    {
        $('.collapse-button').removeClass("icon-right-open").addClass("icon-left-open");
        $('.left-save-menu').parent().removeClass("collapsed-width").addClass("expanded-width");
        $('.left-save-menu .ui-panelgrid-content ').removeClass("display-none");
        $('.left-save-menu .ui-selectonelistbox').removeClass("display-none");
        $('.left-save-menu .detail-search-left-overlay-toolbar').removeClass("display-none");
        isToggled = false;
    } else
    {
        $('.collapse-button').removeClass("icon-left-open").addClass("icon-right-open");
        $('.left-save-menu').parent().removeClass("expanded-width").addClass("collapsed-width");
        ;
        $('.left-save-menu .ui-panelgrid-content ').addClass("display-none");
        $('.left-save-menu .ui-selectonelistbox').addClass("display-none");
        $('.left-save-menu .detail-search-left-overlay-toolbar').addClass("display-none");

        isToggled = true;
    }
}

/**
 * Sayfa yüklendiğinde kaydetmek için kullanılan sol menünün kapalı gelmesini sağlar 
 */
$(function () {
    $('.collapse-button').removeClass("icon-left-open").addClass("icon-right-open");
    $('.left-save-menu').parent().removeClass("expanded-width").addClass("collapsed-width");
    ;
    $('.left-save-menu .ui-panelgrid-content ').addClass("display-none");
    $('.left-save-menu .ui-selectonelistbox').addClass("display-none");
    $('.left-save-menu .detail-search-left-overlay-toolbar').addClass("display-none");
});
/*
 * 
 * Json Array input alan içerisine eklenir
 */
function fillCriteriaInputbox()
{
    $('.return-area').val(JSON.stringify(items));
}

/*
 * Json Array içerisine yeni kriter ekler
 */
function addCriteriaToJson(inputValue, ownerId, dbTable, dbColumn)
{
    var item = {"field_value": inputValue[0], "type": "equal", "db_table": dbTable, "db_column": dbColumn};


    if ($.isEmptyObject(items[ownerId]))
        items[ownerId] = [];

    items[ownerId].push(item);
    fillCriteriaInputbox();
}

/*
 * Json Array içerisinden kriter siler
 * one : sadece gönderilen silinir
 * group : gönderilene ait grup silinir
 * all : tüm liste silinir
 */
function removeCriteriaFromJson(type, _self)
{
    if (type === "one")
    {
        var ownerId = _self.closest("section").attr('data-criteria-group');
        var field_value = _self.closest("li").find('.criteria-value').attr('data-value');
        items[ownerId] = items[ownerId].filter(
                function (obj) {
                    return obj.field_value !== field_value;
                }
        );
    } else if (type === "group")
    {
        var ownerId = _self.closest("section").attr('data-criteria-group');
        items[ownerId] = [];

    } else if (type === "all")
    {
        items = {};
    }
    fillCriteriaInputbox();
}

function fillSearchCriteriaDivFromSelected(criterias)
{
    $(".search-criteria-body").html("");
    removeCriteriaFromJson("all");
    items = criterias;
    //var criterias = jQuery.parseJSON('{"code":[{"field_value":"test","type":"like"},{"field_value":"a","type":"equal"}],"name":[{"field_value":"bc","type":"equal"}]}');
    $.each(criterias, function (key, values) {
        var ownerId = key;
        if (typeof ownerId !== "undefined" && ownerId !== "") {

            var ownerText = $("[data-group-name='" + ownerId + "'] label:first").text();
            var targetGroup = $("[data-criteria-group='" + ownerId + "']");
            var criteriaHtml = $("<div>" + $(".criteria-html-base").html() + "</div>");

            $("section.panel", criteriaHtml).attr("data-criteria-group", ownerId);
            $(".panel-heading-success .text", criteriaHtml).html(ownerText);

            if (targetGroup.length < 1) {
                $(".search-criteria-body").html($(".search-criteria-body").html() + criteriaHtml.html());
                targetGroup = $("[data-criteria-group='" + ownerId + "']");
            }

            $.each(values, function (k, value) {

                var field_value = value.field_value;
                var type = value.type;

                if ($(".criteria-value[data-value='" + field_value + "']", targetGroup).length < 1) {
                    var appendData = $("<li class='list-" + type + " search-criteria-list-item'><span class='badge switch-criteria-type badge-sm label-" + type + "'>&nbsp;</span> <span class='criteria-value' data-value='" + field_value + "'>" + field_value + "</span><i class='fa fa-times remove-criteria'></i></li>");
                    appendData.data("value", field_value).data("type", type);
                    $(".criteria-list", targetGroup).append(appendData);
                }
            });
        }
    });
    fillCriteriaInputbox();
}

/**
 * Sayfa yüklendiğinde kaydetmek için kullanılan sol menünün kapalı gelmesini sağlar 
 */
$(function () {
    $('.collapse-button').removeClass("icon-left-open").addClass("icon-right-open");
    $('.left-save-menu').parent().removeClass("expanded-width").addClass("collapsed-width");
    ;
    $('.left-save-menu .ui-panelgrid-content ').addClass("display-none");
    $('.left-save-menu .ui-selectonelistbox').addClass("display-none");
    $('.left-save-menu .detail-search-left-overlay-toolbar').addClass("display-none");
    
   
});
