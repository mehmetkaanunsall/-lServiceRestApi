/**
 * 
 /* @author Gozde Gürsel- Emrullah Ykaışan
 * *@param {type} chart
 * @returns {undefined}
 * Bu js dosyası dashboardda kullanılan widgetlerin çizilmesi için kullanılır.Dashboard bean tarafından çağırılır.
 */

function printchart(chart) {
    $('#output').empty().append(PF(chart).exportAsImage());
    PF('dlg').show();
}

function blockInput() {
    $(".txtCategory").on("click", function () {
        var $this = $(this);
        $this.attr({"disabled": "true"});
        setTimeout(function () {
            $this.removeAttr("disabled");
        }, 3000);
    });

}
function blockCommand() {
    $(".btnCategory").on("click", function () {
        var $this = $(this);
        $this.attr({"disabled": "true"});
        setTimeout(function () {
            $this.removeAttr("disabled");
        }, 3000);
    });
}

function overrideAxis() {
    this.cfg.axes.xaxis.tickOptions.fontSize = "9pt";
    this.cfg.axes.xaxis.tickOptions.textColor = "#0f446c";
    this.cfg.axes.xaxis.scaleToHiddenSeries = true;

    this.cfg.axes.yaxis.tickOptions.fontSize = "9pt";
    this.cfg.axes.yaxis.tickOptions.textColor = "#0f446c";
    this.cfg.axes.yaxis.scaleToHiddenSeries = true;
}

var chrtmostsoldstocks = nv.models.discreteBarChart(), chrtwashingsystemsales = nv.models.lineChart(), chrtcustomerswhichmakeapurchasemonthly = nv.models.multiBarHorizontalChart(), chrtsalesbycategorization = nv.models.pieChart(), chrtmonthlysalesbybrand = nv.models.discreteBarChart(), chrtsalesbycashier = nv.models.discreteBarChart(), chrtsalesbypumper = nv.models.discreteBarChart()
var datamostsoldstocks, datacustomerswhichmakeapurchasemonthly, datasalesbycategorization, datamonthlysalesbybrand, datawashingsystemsales, datasalesbycashier,datasalesbypumper;

//En Çok Satış Yapılan Stoklar
function mostsoldstocks(dataStr) {
    datamostsoldstocks = dataStr;
    var datas;
    var tempStockSalesList = $.parseJSON(dataStr);
    var series1 = [];
    for (var i in tempStockSalesList)
    {
        series1.push({label: tempStockSalesList[i].name1, value: tempStockSalesList[i].number1});
    }

    datas = [{key: locstock, values: series1}];

    datamostsoldstocks = dataStr;
    nv.addGraph(function () {
        chrtmostsoldstocks = nv.models.discreteBarChart()
                .x(function (d) {
                    return d.label
                })
                .y(function (d) {
                    return d.value
                });

        chrtmostsoldstocks.noData(nodata);
        chrtmostsoldstocks.yAxis
                .axisLabel(locstockcount)
                .axisLabelDistance(20);
        chrtmostsoldstocks.margin({left: 85});
        chrtmostsoldstocks.yAxis
                .tickFormat(d3.format(valueformat));//formatlı yazdırma 
        chrtmostsoldstocks.xAxis
                .tickFormat(function (d) {
                    return d.substring(d.indexOf("-") + 1, d.length)

                });
        chrtmostsoldstocks.color(['#afd285', '#ed7d31', '#a4a9dd', '#ef5ee0', '#838b94', '#6f5697']);
        d3.select('#pnlmostsoldstocks svg')
                .datum(datas)
                .transition().duration(500).call(chrtmostsoldstocks);


        xTicksRemove('pnlmostsoldstocks', 0, 10);
        nv.utils.windowResize(updatemostsoldstocks);
        return chrtmostsoldstocks;
    });
}

function updatemostsoldstocks() {
    chrtmostsoldstocks.update()
    xTicksRemove('pnlmostsoldstocks', 0, 10);
}


/*Müşteri Alışları*/
function  customerswhichmakeapurchasemonthly(dataStr) {
    datacustomerswhichmakeapurchasemonthly = dataStr;
    var jsondata = [];
    var series1 = [];
    var tempCustomerPurchasesList = $.parseJSON(dataStr);
    for (var i in tempCustomerPurchasesList) {
        series1.push({
            label: tempCustomerPurchasesList[i].name1,
            value: tempCustomerPurchasesList[i].number1
        });
    }
    jsondata = [
        {key: loccustomer,
            values: series1
        }
    ];
    nv.addGraph(function () {
        chrtcustomerswhichmakeapurchasemonthly = nv.models.multiBarHorizontalChart()
                .x(function (d) {
                    return d.label
                })
                .y(function (d) {
                    return d.value
                })
                .margin({left: 100})
                .showValues(true)
                .tooltips(true)
                .showControls(false);       //Allow user to switch between "Grouped" and "Stacked" mode.

        chrtcustomerswhichmakeapurchasemonthly.noData(nodata);
        chrtcustomerswhichmakeapurchasemonthly.yAxis
                .tickFormat(d3.format(valueformat));//formatlı yazdırma 

        chrtcustomerswhichmakeapurchasemonthly.xAxis
                .tickFormat(function (d) {
                    return d.substring(d.indexOf("-") + 1, d.length)

                });
        chrtcustomerswhichmakeapurchasemonthly.color(['	#E1BEE7', '#c8ac6c', '#303460'])
        d3.select('#pnlcustomerswhichmakeapurchasemonthly svg')
                .datum(jsondata)
                .transition().duration(500).call(chrtcustomerswhichmakeapurchasemonthly);

        xTicksRemove('pnlcustomerswhichmakeapurchasemonthly', 0, 10);

        nv.utils.windowResize(updatecustomerswhichmakeapurchasemonthly);
        return chrtcustomerswhichmakeapurchasemonthly;
    });
}

function updatecustomerswhichmakeapurchasemonthly() {
    chrtcustomerswhichmakeapurchasemonthly.update()
    xTicksRemove('pnlcustomerswhichmakeapurchasemonthly', 0, 10);
}
/*Kategorilere Göre Toplam Satışlar*/
function  monthlysalesbycategorization(dataStr) {
    datasalesbycategorization = dataStr;
    var jsondata = [];
    var isThere = false;
    var list = $.parseJSON(dataStr);
    var list2 = $.parseJSON(dataStr);
    for (var i in list) {
        for (var a in jsondata) {
            if (jsondata[a].label === list[i].name1) {
                isThere = true;
                break;
            } else {
                isThere = false;
            }
        }
        /*Listede Yok İse Listeye Ekle*/
        if (!isThere) {
            var count = 0;
            for (var j in list2) {
                if (list[i].name === list2[j].name1) {
                    count = list2[j].number1;
                }
            }
            var data = {};
            data.label = list[i].name1;
            data.value = list[i].number1;
            jsondata.push(data);
        }
    }
    console.log(JSON.stringify(jsondata));
    nv.addGraph(function () {
        d3.selectAll("#pnlmonthlysalesbycategorization svg > *").remove();
        d3.selectAll(".nvtooltip").remove();
        chrtsalesbycategorization = nv.models.pieChart()
                .x(function (d) {
                    return d.label
                })
                .y(function (d) {
                    return d.value
                })
                .showLabels(true)
                .showLegend(true)
                .labelThreshold(.05)
                .labelType("percent")
                .donut(false)
                .donutRatio(0.35)
                .width(200) // width
                .height(600);


        chrtsalesbycategorization.noData(nodata);
        chrtsalesbycategorization.color(['#F8BBD0', '	#E1BEE7', '	#90CAF9', '	#B2DFDB', '	#FFD54F', '#f3c5fb', '#c5c5c5', '#ffbf81', '#9bdcfb'])
      //  chrtsalesbycategorization.valueFormat(d3.format(',.f'));
        d3.select('#pnlmonthlysalesbycategorization svg')
                .datum(jsondata)
                .transition().duration(500).call(chrtsalesbycategorization);

        var nocomment = d3.select('#pnlmonthlysalesbycategorization svg ').select('.nv-noData');
        nocomment
                .attr('x', 250)
                .attr('y', 150);
        nv.utils.windowResize(chrtsalesbycategorization.update);
        return chrtsalesbycategorization;
    });

}

/*Tahsilatlar*/
function  recorveries(dataStr, type) {
    var jsondata = [];
    var list = $.parseJSON(dataStr);
    var data = {};
    data.label = loctotaloverdueamount + loctotal;
    data.value = 0;
    jsondata.push(data);
    var data1 = {};
    data1.label = locamounttobecollected + loctotal;
    data1.value = 0;
    jsondata.push(data1);
    var total1 = 0, total2 = 0, total3 = 0;
    for (var a in list) {
        if (list[a].typeId == type) {
            if (list[a].typeId2 == 1) {
                total1 += list[a].bigDecimal1;
            }
            total2 += list[a].bigDecimal1;
            total3 = total2 - total1;
        }
    }
    jsondata[0].value = total1;
    jsondata[1].value = total3;

    nv.addGraph(function () {
        d3.selectAll("#pnlRecoveries svg > *").remove();
        d3.selectAll(".nvtooltip").remove();
        chrtrecoveries = nv.models.pieChart()
                .x(function (d) {
                    return d.label
                })
                .y(function (d) {
                    return d.value
                })
                .showLabels(true)
                .showLegend(true)
                .labelThreshold(.05)
                .labelType("percent")
                .donut(false)
                .donutRatio(0.35)
                .width(100) // width
                .height(500);



        chrtrecoveries.noData(nodata);
        chrtrecoveries.color(['#b76144', '#f3a68d', '#91acb5', '#89afff', '#c2be91'])
        chrtrecoveries.valueFormat(d3.format(valueformat));
        d3.select('#pnlRecoveries svg')
                .datum(jsondata)
                .transition().duration(500).call(chrtrecoveries);


        var nocomment = d3.select('#pnlRecoveries svg ').select('.nv-noData');
        nocomment
                .attr('x', 250)
                .attr('y', 150);
        nv.utils.windowResize(chrtrecoveries.update);
        return chrtrecoveries;
    });

}


/*Ödemeler*/
function  payments(dataStr, type) {
    var jsondata = [];
    var list = $.parseJSON(dataStr);
    var data = {};
    data.label = loctotaloverdueamount + loctotal;
    data.value = 0;
    jsondata.push(data);
    var data1 = {};
    data1.label = locamounttobecollected + loctotal;
    data1.value = 0;
    jsondata.push(data1);
    var total1 = 0, total2 = 0, total3 = 0;
    for (var a in list) {
        if (list[a].typeId == type) {
            if (list[a].typeId2 == 1) {
                total1 += list[a].bigDecimal1;
            }
            total2 += list[a].bigDecimal1;
            total3 = total2 - total1;
        }
    }
    jsondata[0].value = total1;
    jsondata[1].value = total3;

    nv.addGraph(function () {
        d3.selectAll("#pnlPayments svg > *").remove();
        d3.selectAll(".nvtooltip").remove();
        chrtrecoveries = nv.models.pieChart()
                .x(function (d) {
                    return d.label
                })
                .y(function (d) {
                    return d.value
                })
                .showLabels(true)
                .showLegend(true)
                .labelThreshold(.05)
                .labelType("percent")
                .donut(false)
                .donutRatio(0.35)
                .width(100) // width
                .height(500);

        d3.select(".nv-legendWrap")
                .attr("transform", "translate(-50,-30)");

        chrtrecoveries.noData(nodata);
        chrtrecoveries.color(['#bf4e6d', '#ecc0cb', '#91acb5', '#89afff', '#c2be91'])
        chrtrecoveries.valueFormat(d3.format(valueformat));
        d3.select('#pnlPayments svg')
                .datum(jsondata)
                .transition().duration(500).call(chrtrecoveries);


        var nocomment = d3.select('#pnlPayments svg ').select('.nv-noData');
        nocomment
                .attr('x', 250)
                .attr('y', 150);
        nv.utils.windowResize(chrtrecoveries.update);
        return chrtrecoveries;
    });

}


/*Markalara Göre Toplam Satışlar*/
function  monthlysalesbybrand(dataStr) {
    datamonthlysalesbybrand = dataStr;
    var datas;
    var tempSalesByBrandList = $.parseJSON(dataStr);
    var series1 = [];
    for (var i in tempSalesByBrandList)
    {
        series1.push({label: tempSalesByBrandList[i].name1, value: tempSalesByBrandList[i].number1});
    }

    datas = [{key: loctotal, values: series1}];

    datamonthlysalesbybrand = dataStr;
    nv.addGraph(function () {
        chrtmonthlysalesbybrand = nv.models.discreteBarChart()
                .x(function (d) {
                    return d.label
                })
                .y(function (d) {
                    return d.value
                });
        chrtmonthlysalesbybrand.margin({left: 75});
        chrtmonthlysalesbybrand.noData(nodata);
        chrtmonthlysalesbybrand.yAxis
                .tickFormat(d3.format(',.f'));//formatlı yazdırma 
        chrtmonthlysalesbybrand.yAxis
                .axisLabel(locamount)
                .axisLabelDistance(14);
        chrtmonthlysalesbybrand.xAxis
                .tickFormat(function (d) {
                    return d.substring(d.indexOf("-") + 1, d.length)

                });
        chrtmonthlysalesbybrand.color(['#2d3f50', '#fd4349', '#6dbcdc', '#f7e34a', '#d7d9db', '#6f5697']);
        d3.select('#pnlmonthlysalesbybrand svg')
                .datum(datas)
                .transition().duration(500).call(chrtmonthlysalesbybrand);

        xTicksRemove('pnlmonthlysalesbybrand', 0, 10);
        nv.utils.windowResize(updatemonthlysalesbybrand);
        return chrtmonthlysalesbybrand;
    });
}

function updatemonthlysalesbybrand() {
    chrtmonthlysalesbybrand.update()
    xTicksRemove('pnlmonthlysalesbybrand', 0, 10);
}

/* Önümüzdeki 12 haftanın nakit akışı */
function weeklycashflow(dataStr) {
    dataweeklycashflow = dataStr;
    var datas;
    var list = $.parseJSON(dataStr);
    var datas = [], series = [], series1 = [];
    for (var i in list)
    {
        var arr = [], arr2 = [], timestamp;
        timestamp = Date.parse(list[i].beginDate.replace(/[ap]m$/i, ''));

        arr.push(timestamp)
        arr.push(list[i].bigDecimal1)

        arr2.push(timestamp)
        arr2.push(list[i].bigDecimal2)

        series.push(arr)
        series1.push(arr2)
    }

    datas.push({key: loccashentry, values: series});
    datas.push({key: loccashout, values: series1});

    nv.addGraph(function () {
        var chartweeklycashflow = nv.models.stackedAreaChart()
                .x(function (d) {
                    return d[0]

                })
                .y(function (d) {
                    return d[1]
                })
                .clipEdge(false)
                .showControls(false)
                .useInteractiveGuideline(false);

        chartweeklycashflow.xAxis
                .showMaxMin(false)
                .tickFormat(function (d) {
                    return d3.time.format('%d.%m.%Y')(new Date(d))
                });
        chartweeklycashflow.color(['#015801', '#af0909']);
        chartweeklycashflow.yAxis
                .tickFormat(d3.format(valueformat));

        d3.select('#pnlweeklycashflow svg')
                .datum(datas)
                .transition().duration(500).call(chartweeklycashflow);

        nv.utils.windowResize(chartweeklycashflow.update);
        return chartweeklycashflow;
    });

}

/*Kasiyere Göre Toplam Satışlar*/
function  salesbycashier(dataStr) {
    datasalesbycashier = dataStr;
    var datas;
    var list = $.parseJSON(dataStr);
    var series1 = [];
    for (var i in list)
    {
        series1.push({label: list[i].name1, value: list[i].number1});
    }

    datas = [{key: loctotal, values: series1}];
     console.log("cashier===="+JSON.stringify(datas));
    datasalesbycashier = dataStr;
    nv.addGraph(function () {
        chrtsalesbycashier = nv.models.discreteBarChart()
                .x(function (d) {
                    return d.label
                })
                .y(function (d) {
                    return d.value
                });
        chrtsalesbycashier.margin({left: 75});
        chrtsalesbycashier.noData(nodata);
        chrtsalesbycashier.yAxis
                .axisLabel(loctotal)
                .axisLabelDistance(14);
        chrtsalesbycashier.xAxis
                .tickFormat(function (d) {
                    return d.substring(d.indexOf("-") + 1, d.length)
                });
        chrtsalesbycashier.yAxis
                .tickFormat(d3.format(valueformat));//formatlı yazdırma 
        chrtsalesbycashier.color(['#91acb5', '#756a72', '#c2be91', '#878a8f', '#dbd8c5', '#6f5697']);
        d3.select('#pnlmonthlysalesbycashier svg')
                .datum(datas)
                .transition().duration(500).call(chrtsalesbycashier);

        xTicksRemove('pnlmonthlysalesbycashier', 0, 10);
        nv.utils.windowResize(updatesalesbycashier);
        return chrtsalesbycashier;
    });
}
function updatesalesbycashier() {
    chrtsalesbycashier.update()
    xTicksRemove('pnlmonthlysalesbycashier', 0, 10);
}


/*Pompacılara Göre Toplam Satışlar*/
function  salesbypumper(dataStr) {
    datasalesbypumper = dataStr;
    var datas;
    var tempPumperSalesList = $.parseJSON(dataStr);
    var series1 = [];
    for (var i in tempPumperSalesList)
    {
        series1.push({label: tempPumperSalesList[i].name1, value: tempPumperSalesList[i].number1});
    }

    datas = [{key: loctotal, values: series1}];
    console.log(JSON.stringify(datas));
  //  datasalesbypumper = dataStr;
    nv.addGraph(function () {
        chrtsalesbypumper = nv.models.discreteBarChart()
                .x(function (d) {
                    return d.label
                })
                .y(function (d) {
                    return d.value
                });
        chrtsalesbypumper.margin({left: 75});
        chrtsalesbypumper.noData(nodata);
        chrtsalesbypumper.yAxis
                .axisLabel(loctotal)
                .axisLabelDistance(14);
        chrtsalesbypumper.xAxis
                .tickFormat(function (d) {
                    return d.substring(d.indexOf("-") + 1, d.length)
                });
        chrtsalesbypumper.yAxis
                .tickFormat(d3.format(valueformat));//formatlı yazdırma 
        chrtsalesbypumper.color(['#91acb5', '#756a72', '#c2be91', '#878a8f', '#dbd8c5', '#6f5697']);
        d3.select('#pnlSalesByPumper svg')
                .datum(datas)
                .transition().duration(500).call(chrtsalesbypumper);

        xTicksRemove('pnlSalesByPumper', 0, 10);    
        nv.utils.windowResize(updatesalesbypumper);
        return chrtsalesbypumper;
    });
}    


function updatesalesbypumper() {
    chrtsalesbypumper.update()
    xTicksRemove('pnlSalesByPumper', 0, 10); 
}


function updateDshWidget(widgets) {
    var list = $.parseJSON(widgets);
    console.log(widgets)
    for (var i in list) {
        if (list[i].trim() === 'productsales') {
            mostsoldstocks(datamostsoldstocks);
        } else if (list[i].trim() === 'customerpurchases') {
            customerswhichmakeapurchasemonthly(datacustomerswhichmakeapurchasemonthly);
        } else if (list[i].trim() === 'monthlysalesbycategorization') {
            monthlysalesbycategorization(datasalesbycategorization);
        } else if (list[i].trim() === 'brandsales') {
            monthlysalesbybrand(datamonthlysalesbybrand);
        } else if (list[i].trim() === 'cashiersales') {
            salesbycashier(datasalesbycashier);
        } else if (list[i].trim() === 'washingsystemsales') {
            washingsystemsales(datawashingsystemsales, washingsalestype);
        } else if (list[i].trim() === 'pumpersales') {
            salesbypumper(datasalesbypumper);
        }

    }
}

//Yıkama Satışları 
function washingsystemsales(dataStr, type) {
    datawashingsystemsales = dataStr;
    washingsalestype = type;
    var list = $.parseJSON(dataStr);
    console.log("list===" + JSON.stringify(list));
    var remaining = 0;
    var series = [];
    var seriesParent = [];
    var name;
    var colors;
    if (washingsalestype == 3) {
        if (list.length > 0) {
            remaining = list.length / 15;
        }
        for (i = 0; i < remaining; i++) {
            series = [];
            for (k = 0; k < 15; k++) {

                var d = new Date(list[(i * 15) + k].month);
                var date = d.getTime();
                series.push({x: date, y: list[(i * 15) + k].quantity});
                name = list[(i * 15) + k].name;
                colors = i == 0 ? '#c70039' : i == 1 ? '#007ec7' : i == 2 ? '	#00c78e' : i == 3 ? '##bfd78b' : '#d3d3d3';
            }
            seriesParent.push({key: name, values: series, color: colors});
        }
    } else {
        if (list.length > 0) {
            remaining = list.length / 7;
        }
        for (i = 0; i < remaining; i++) {
            series = [];
            for (k = 0; k < 7; k++) {
                series.push({x: list[(i * 7) + k].month, y: list[(i * 7) + k].quantity});
                name = list[(i * 7) + k].name;
                colors = i == 0 ? '#c70039' : i == 1 ? '#007ec7' : i == 2 ? '	#00c78e' : i == 3 ? '##bfd78b' : '#d3d3d3';
            }
            seriesParent.push({key: name, values: series, color: colors});
        }
    }
    nv.addGraph(function () {
        chrtwashingsystemsales = nv.models.lineChart()
                .showLegend(true)
 //               .useInteractiveGuideline(true)
                .showYAxis(true) //Show the y-axis
                .showXAxis(true) //Show the x-axis 
//                .margin({"left": 35, "right": 20, "top": 0, "bottom": washingsalestype == 3 ? 90 : 20})
        
        chrtwashingsystemsales.noData(nodata);
        if (washingsalestype == 3) {

            chrtwashingsystemsales.xAxis
                    .orient("bottom").ticks(15)
                    .rotateLabels(-85);

            chrtwashingsystemsales.xAxis.tickFormat(function (d) {
                return d3.time.format('%d.%m.%Y')(new Date(d));
            });

        } else {
            chrtwashingsystemsales.xAxis
                    .orient("bottom").ticks(7)
                    .tickFormat(function (d) {
                        if (d === 1) {
                            return locmonday;
                        } else if (d === 2) {
                            return loctuesday;
                        } else if (d === 3) {
                            return locwednesday;
                        } else if (d === 4) {
                            return locthursday;
                        } else if (d === 5) {
                            return locfriday;
                        } else if (d === 6) {
                            return locsaturday;
                        } else if (d === 7) {
                            return locsunday;
                        }
                        ;
                    });
        }


        d3.select('#pnlwashingsystemsales svg')
                .datum(seriesParent)
                .transition().duration(500).call(chrtwashingsystemsales);
        xTicksRemove('pnlwashingsystemsales', 0, 10);
        nv.utils.windowResize(updatewashingsystemsales);
        return chrtwashingsystemsales;
    });


}

function updatewashingsystemsales() {
    chrtwashingsystemsales.update()
    xTicksRemove('pnlwashingsystemsales', 0, 10);
}

/*Bu Fonskiyon X Ekseninde Gizlenmek İstenen Karakter Kadar Gizleme Yapar.*/
function xTicksRemove(widgetname, begin, end) {
    var xTicks = d3.select('#' + widgetname + ' svg .nv-x.nv-axis > g').selectAll('g');
    xTicks.selectAll('text').select(function () {
        if (this.textContent != null) {
            return this.textContent = this.textContent.substring(begin, end);
        }
    });
}


