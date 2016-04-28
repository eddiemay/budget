$(document).ready(function() {
	$('.multicheck').each(function(i) {
		var children = $(this).children();
		var options = [];
		for (var x = 0; x < children.length; x++) {
			var child = children[x];
			if (child instanceof HTMLOptionElement) {
				options.push(child);
			} 
		}
		new MultiCheck(this, options);
	});
});

MultiCheck = function(div, options) {
	this.options = options;
	var out = '';
	for (var idx = 0; idx < options.length; idx++) {
		out += '<input type="checkbox" value="' + options[idx].value + '"';
		if (options[idx].selected) {
			out += ' checked';
		}
		out += ' onchange="multiCheckChange(this)"><label>' + options[idx].innerHTML + '</label>';
	}
	div.innerHTML = out;
};

MultiCheck.prototype.options = [];

MultiCheck.prototype.selected = [];

MultiCheck.prototype.getSelected = function() {
	var selected = [];
	var children = $(this).children();
	for (var x = 0; x < children.length; x++) {
		if (children[x].checked) {
			selected.push(children[x].value);
		}
	}
	return selected;
};

multiCheckChange = function(checkbox) {
	var parent = $(checkbox).parent()[0];
	console.log(checkbox + ' multicheck: ' + parent + ' children: ' + $(parent).children());
	var selected = [];
	var children = $(parent).children();
	for (var x = 0; x < children.length; x++) {
		console.log(children[x]);
		if (children[x].checked) {
			selected.push(children[x].value);
		}
	}
	parent.value = selected.toString();
	console.log('selected: ' + parent.value);
};