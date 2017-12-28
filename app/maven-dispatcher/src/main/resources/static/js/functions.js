

function tableRender(data) {
	var header = false;
	$.each(data,function(k,v) {
		var headerRow = null;
		var row = "<tr>";
		if (!header) {
			headerRow = "<tr>";							
		}//end if
		$.each(v,function(n,val){
			//do the header
			if (!header) {
				headerRow += "<th>";
				headerRow += n;
				headerRow += "</th>";
			}//end if
			row += "<td>";
			row += val;
			row += "</td>";
		});
		if (!header) {
			headerRow += "</tr>";
			$('#datatable').append(headerRow);
			header = true;
		}//end if
		row += "</tr>";
		$('#datatable').append(row);
	});	
}


function uploadCucumberFile() {
	var dt = new FormData();
	dt.append("file",$('#fileInput')[0].files[0]);
	
	$('#spinner').show();
	
	//build the url
	var url = '/api/runner/file';
	
	var firstParameter = true;
	console.log($('#parameterTable').find("input"));
	
	for (var i=0;i<$('#parameterTable').find("input").length;i++) {
		if (firstParameter) {
			url += "?";
			firstParameter = false;
		} else {
			url += "&";
		}//end if
		url += $('#parameterTable').find("input")[i].id;
		url += "=";
		url += $('#parameterTable').find("input")[i].value;
	}//end for
	
	
	if (!firstParameter) {
		url += "&"
	} else {
		url += "?";
		firstParameter = false;
	}//end if
	
	url += "cucumber=";
	url += isCucumber;
	
	
//	console.log($('#cucumberTag').val());
	
	
	if ($('#cucumberTag').val().length > 0) {
		if (!firstParameter) {
			url += "&"
		} else {
			url += "?";
		}//end if			
		url += "tag=";
		url += $('#cucumberTag').val();
	}//end if
	
	console.log(url);
	
	$.ajax({
		url:url,
		data:dt,
		processData:false,
		contentType:false,
		type:'POST',
		success:function(data) {
			window.location = '/index.html';
			$('#spinner').hide();
		},
		error:function(err,data) {
			alert(err.responseJSON.message);
			$('#spinner').hide();
		}
	});
}