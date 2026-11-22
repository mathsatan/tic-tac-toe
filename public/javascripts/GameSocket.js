function GameSocket(_settings) {
		// private
	var socket = null;
	var settings = $.extend({
			statusBar: null,
			onMessage: function(event) {
				console.log('GameSocket.onMessage: onMessage called');
			}
		}, _settings);

	this.initSocket = function () {
		settings.statusBar.info('Connecting <i class="fa fa-spinner fa-spin" style="font-size:16px"></i>');
		socket = new WebSocket(settings.url);
		socket.onmessage = settings.onMessage;

		socket.onopen = function() {
			settings.statusBar.info('Connection established', 2000);
		};

		socket.onclose = function(event) {
			if (event.wasClean) {
				//console.log('GameSocket.onClose: Connection closed');
				settings.statusBar.info('GameSocket.onClose: Connection closed', 2000);
			} else {
				let code = parseInt(event.code);
				if (!isNaN(code)) {
					let errorMessage = '';
					switch (code) {
						case 1001: errorMessage = 'Error either because of a server failure or because of browser'; break;
						case 1002: errorMessage = 'Protocol error'; break;
						case 1003: errorMessage = 'Unsupported data'; break;
						case 1005: errorMessage = 'No status code was provided '; break;
						case 1006: errorMessage = 'Connection was closed abnormally'; break;
						case 1007: errorMessage = 'Invalid frame payload data'; break;
						case 1008: errorMessage = 'Policy Violation'; break;
						case 10013: errorMessage = 'Try again later'; break;
						default: errorMessage = 'Connection error has occured';
					}
					settings.statusBar.error(errorMessage, 2000);
				}
			}
		};

		socket.onerror = function(error) {
			console.log('onerror');
			console.log(error);
		};
	}

		// public
	this.sendData = function(_msg) {
		if (socket == null) {
			console.error('Socket object is null');
			return;
		}
		socket.send(_msg);
	}
	
	this.socketStatus = function() {
		return socket.readyState;
	}

	this.stopGame = function() {
		socket.onclose = function() {};
		socket.close(1000, 'Game stopped by user');
		/*socket.send(JSON.stringify({
					status: 11,
					x: 0,
					y: 0
				}));*/
	}
}

function StatusBar(_id) {
	var id = _id;
	this.info = function(message, delay = 0) {
		$(id).removeClass('tic_error');
		$(id).addClass('tic_info');
		if (delay) {
			$(id).html(message).fadeIn(100).delay(delay).fadeOut(500);
		} else {
			$(id).html(message).fadeIn(100);
		}
	}
	this.error = function(message, delay = 0) {
		$(id).removeClass('tic_info');
		$(id).addClass('tic_error');
		if (delay) {
			$(id).html(message).fadeIn(100).delay(delay).fadeOut(1000);
		} else {
			$(id).html(message).fadeIn(100);
		}
	}
}