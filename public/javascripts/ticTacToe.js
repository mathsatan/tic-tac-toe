(function ($) { 
		$.fn.tickTackToe = function(options) {
			var field = null;		
			var _self = this;
			
			if (this.html() != '') {
				return this;
			}
			
			var settings = $.extend({
				rowCount: 5,
				colCount: 5,
				statusBar: new StatusBar('#status_bar'),
				tableClass: 'table',
				onCellClick: function(i, j, self) {
				
				}
			}, options);
			
			var humanSign = settings.humanSign;
			var robotSign = settings.robotSign;
			
			var table = "<table id='ticTable' class='" + settings.tableClass + "'>";
			for (let i = 0; i < settings.rowCount; ++i) {
				table += "<tr>";
				for (let j = 0; j < settings.colCount; ++j) {
					table += "<td></td>";
				}
				table += "</tr>";
			}
			table += "</table>";
			this.append(table);

			this.setData = function(i, j, value, gameStatus = false) {
				if (i < 0 || j < 0 || i >= settings.rowCount || j >= settings.colCount) {
					console.error('tickTackToe.setData fails. Cause: wrong index range');
					return;
				}
				if (field) {
					if (_self.isDisabled()) {
						return;
					}
					if (field[0].rows[i].cells[j].innerHTML != '') {
						settings.statusBar.error('Cell is not empty', 2000);
						return;
					}
					field[0].rows[i].cells[j].innerHTML = value;
				}
			};
			
			field = this.find('#ticTable');

			this.cellCss = function(i, j, styleCss = '') {
				if (i < 0 || j < 0 || i >= settings.rowCount || j >= settings.colCount) {
					console.error('tickTackToe.setData fails. Cause: wrong index range');
					return;
				}
				if (field) {
					if (_self.isDisabled()) {
						return;
					}
					if (styleCss === '') {
						return field[0].rows[i].cells[j].style;
					}
					field[0].rows[i].cells[j].style = styleCss;
				}
			};

			this.winnerLine = function(x0, y0, x1, y1, styleCss) {
	        	function g(x) {
		        	if (x == 0) {
		        		return 0;
		        	}
		        	if (x < 0) {
		        		return 1;
		        	}
		        	return -1;
	        	}

	        	let dx = x0 - x1;
	        	let dy = y0 - y1;
	        	let x = g(dx);
	        	let y = g(dy);
	        	if ((x0 + x*4) == x1 && (y0 + y*4 == y1)) {
					for (let i = 0; i < 5; ++i) {
		                this.cellCss(x0 + x*i, y0 + y*i, styleCss);
		            }
	        	} else {
	        		console.error('Wrong winner coordinates');
	        	}
        	}

			this.getField = function() {
				return field;
			}
			
			this.rowCount = function() {
				return settings.rowCount;
			}
			
			this.colCount = function() {
				return settings.colCount;
			}

			this.clearField = function() {
				$('#ticTable').remove();
			}

			this.isDisabled = function(isDisabled) {
				if (typeof isDisabled == 'undefined') {
					return $(field).hasClass('disabled_table');
				}
				if (isDisabled && field) {
					$(field).addClass('disabled_table');
				} else {
					$(field).removeClass('disabled_table');
				}
			}

			return this;
		};
	}(jQuery));