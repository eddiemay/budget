function saveChange(data) {
	$.ajax({
		url: 'update',
		dataType: 'json',
		type: 'POST',
		data: data,
		success: function(data, textStatus, XMLHttpRequest) {
			if (data.valid) {
				notify('Change Saved');
				for (var prop in data.object) {
				  	updateValue(data.object, prop);
				}
			} else {
				alert(data.error || 'An unexpected error occurred, please try again');
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			alert('Error while contacting server, please try again');
		}
	});
}

function asyncUpdate(comp, className, id, attribute) {
	// Request
	var data = {
		classname: className,
		id: id,
		attribute: attribute,
		value: comp.type === 'checkbox' ? comp.checked : comp.value
	};
	saveChange(data);
}

function saveAddress(place, className, id)	{
	var data = {
		id: id,
		classname: className,
		attribute: 'address',
		address: place.formatted_address,
		latitude: place.geometry.location.lat(),
		longitude: place.geometry.location.lng()
	};
	saveChange(data);
}

function updateLicense(comp, nurseId, licTypeId, attribute) {
	// Request
	var data = {
		classname: 'License',
		id: nurseId,
		lictypeid: licTypeId,
		attribute: attribute,
		value: comp.value
	};
	$.ajax({
		url: 'update',
		dataType: 'json',
		type: 'POST',
		data: data,
		success: function(data, textStatus, XMLHttpRequest) {
			if (data.valid) {
				notify('Change Saved');
				for (var prop in data.object) {
				  	updateValue(data.object, prop);
				}
			} else {
				alert(data.error || 'An unexpected error occurred, please try again');
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			alert('Error while contacting server, please try again');
		}
	});
}

function checkSession() {
	// Request
	var data = {
			action: 'checkSession'
	};
	$.ajax({
		url: 'checkSession',
		dataType: 'json',
		type: 'POST',
		data: data,
		success: function(data, textStatus, XMLHttpRequest) {
			if (!data.valid) {
				console.log('Your session has expired');
				document.location.href = 'login';
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			alert('Error while contacting server, please try again');
		}
	});
}

function updateValue(object, prop) {
	updateValue2(object, prop, object.id);
}

function updateValue2(object, prop, id) {
	var eId = prop + id;
	var element = document.getElementById(eId);
	console.log('looking for : ' + eId, element);
	if (element instanceof HTMLInputElement) {
		element.value = object[prop];
	} else if (element instanceof HTMLSelectElement) {
		// TODO Code Select tag update.
	} else if (element) {
		element.innerHTML = object[prop];
	}
}

function dateChanged(date, set) {
	console.log('date changed: ' + date);
}

function upload(className, id) {
	$.ajax({
		url: 'upload',
		dataType: 'multipart/form-data',
		type: 'POST',
		data: {
			classname: className,
			id: id,
			file: $(file)
		},
		success: function(data, textStatus, XMLHttpRequest) {
			if (data.valid) {
				notify('Change Saved');
				for (var prop in data.object) {
				  	updateValue(data.object, prop);
				}
			} else {
				alert(data.error || 'An unexpected error occurred, please try again');
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			alert('Error while contacting server, please try again');
		}
	});
}

function uploadFile(className, id) {
    var file = document.getElementById('file');
    var url = 'upload';
    var xhr = new XMLHttpRequest();
    xhr.addEventListener('progress', function(e) {
        var done = e.position || e.loaded, total = e.totalSize || e.total;
        console.log('xhr progress: ' + (Math.floor(done/total*1000)/10) + '%');
    }, false);
    if ( xhr.upload ) {
        xhr.upload.onprogress = function(e) {
            var done = e.position || e.loaded, total = e.totalSize || e.total;
            console.log('xhr.upload progress: ' + done + ' / ' + total + ' = ' + (Math.floor(done/total*1000)/10) + '%');
        };
    }
    xhr.onreadystatechange = function(e) {
        if (this.readyState == 4) {
            console.log(['xhr upload complete', e]);
            var element = document.getElementById('dataFileHTML' + id);
            element.innerHTML = '<a href="download?classname=' + className + '&id=' + id + '" class="document-pdf" target="_blank">' +
				'<img src="images/icons/fugue/document-pdf.png" width="16" height="16">Download</a>' +
				'<a title="Delete" href="#" onClick="showDeleteDialog(\'' + className + '\', ' + id + '); return false;">' +
				'<img src="images/icons/fugue/cross-circle.png" width="16" height="16"></a>';
        }
    };
    xhr.open('post', url, true);
    var fd = new FormData;
    fd.append('classname', className);
    fd.append('id', id);
    fd.append('file', file.files[0]);
    xhr.send(fd);
}

function showUploadDialog(className, id, uploadCallback) {
	$.modal({
		content:  '<p>Select the file you wish to upload.</p>' +
				  '<input type="file" id="file" name="file"/>',
		title: 'File Upload',
		maxWidth: 500,
		buttons: {
			'Ok': function(win) { uploadCallback(className, id); win.closeModal(); },
			'Cancel': function(win) { win.closeModal(); }
		}
	});
}

function deleteFile(className, id) {
	$.ajax({
		url: 'deleteFile',
		dataType: 'json',
		type: 'POST',
		data: {
			classname: className,
			id: id
		},
		success: function(data, textStatus, XMLHttpRequest) {
			if (data.valid) {
				notify('Change Saved');
				for (var prop in data.object) {
				  	updateValue(data.object, prop);
				}
			} else {
				alert(data.error || 'An unexpected error occurred, please try again');
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			alert('Error while contacting server, please try again');
		}
	});
}

function showDeleteDialog(className, id) {
	$.modal({
		content:  '<p>Are you sure you want to delete this file?</p>',
		title: 'Delete File Confirmation',
		maxWidth: 500,
		buttons: {
			'Yes': function(win) { deleteFile(className, id); win.closeModal(); },
			'No': function(win) { win.closeModal(); }
		}
	});
}

function showErrorMsg(errorMsg) {
	if (errorMsg) {
		$('.error-block').html('<p class="message error no-margin">' + errorMsg + '</p>');
	} else {
		$('.error-block').html('');
	}
}

function showDeleteDialog(className, id) {
	$.modal({
		content:  '<p>Are you sure you want to delete this?</p>',
		title: 'Delete Confirmation',
		maxWidth: 500,
		buttons: {
			'Yes': function(win) { deleteObj(className, id); win.closeModal(); },
			'No': function(win) { win.closeModal(); }
		}
	});
}

function deleteObj(className, id) {
	$.ajax({
		url: 'delete',
		dataType: 'json',
		type: 'POST',
		data: {
			classname: className,
			id: id
		},
		success: function(data, textStatus, XMLHttpRequest) {
			if (data.valid) {
				notify('Deleted');
				document.location.reload(true);
			} else {
				alert(data.error || 'An unexpected error occurred, please try again');
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			alert('Error while contacting server, please try again');
		}
	});
}
