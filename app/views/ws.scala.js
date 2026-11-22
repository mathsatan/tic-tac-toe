$(function(){
/*
    // get websocket class, firefox has a different way to get it
    var WS = window['MozWebSocket'] ? window['MozWebSocket'] : WebSocket;
    
    // open pewpew with websocket
    var socket = new WS('@routes.Application.wsInterface().webSocketURL(request)'); //.replace('ws://', 'wss://'));
    
    var writeMessages = function(event) {
        console.log(event.data);
        let m = JSON.parse(event.data);
        $('#socket-messages').prepend('<p>' + m.message + '</p>');
    }

    socket.onmessage = writeMessages;

    $('#socket-input').keyup(function(event) {
        let charCode = (event.which) ? event.which : event.keyCode;
       
        if (charCode === 13) {
            let str = JSON.stringify({
                message: $(this).val()
            });
            console.log(str);
            socket.send(str);
            $(this).val('');    
        }
    });*/

    function TicTacToeGame(_field, _socket, _status) {
        let ticTable = _field;
        let clientSocket = _socket;
        let statusBar = _status;
        let self = this;
        
        this.startGame = function() {
            ticTable.isDisabled(false);
            clientSocket.initSocket();
            self.waitForConnection(function() {
                clientSocket.sendData(JSON.stringify({
                    status: 0,
                    x: ticTable.rowCount(),
                    y: ticTable.colCount()
                }));
            }, 200);
        }
        
        this.waitForConnection = function (callback, interval) {
            if (clientSocket.socketStatus() === 1) {
                callback();
            } else {
                let self = this;
                setTimeout(function () {
                    self.waitForConnection(callback, interval);
                }, interval);
            }
        };
            
        this.gameOver = function(obj) {
        };
        
        this.stopGame = function() {
            clientSocket.stopGame();
            delete clientSocket;
            clientSocket = null;
            ticTable.clearField();            
        }
        
        ticTable.getField().click(function(e) {
            if (ticTable.isDisabled()) {
                return;
            }
            var cell = e.target;
            if (cell.tagName == 'TD') {
                ticTable.setData(cell.parentNode.rowIndex, cell.cellIndex, humanSign, 3);
                if (clientSocket.socketStatus() == 1) {
                    clientSocket.sendData(JSON.stringify({
                        status: 3,
                        x: cell.parentNode.rowIndex,
                        y: cell.cellIndex
                    }));
                }
            }
        });
    }

    let tableObj = null;
        let barObj = new StatusBar('#status_bar');
        let socketObj = new GameSocket({
                statusBar: barObj,
                url: "@routes.Application.wsInterface().webSocketURL(request)".replace("ws://", "wss://"),
                onMessage: function(event) {
                    var obj = JSON.parse(event.data);
                    console.log(obj);
                    if (obj.hasOwnProperty('status')) {
                        switch (obj.status) {
                            case 2: //GAME_STARTED
                                console.log('Game started');
                                break;
                            case 3: //NEXT_MOVE
                                tableObj.setData(obj.x, obj.y, robotSign);
                                console.log('Next move');
                                break;
                            case 5: // game over
                                barObj.info('Game over!', 2000);
                                tableObj.isDisabled(true);
                                break;
                            case 6: // game stopped
                                tableObj.isDisabled(true);
                                barObj.info('Game has been stopped!', 2000);
                                break;
                            case 1: //START_GAME_ERROR
                                barObj.error('Game start fails', 2000);
                                break;
                            case 4: //MOVE_ERROR
                                barObj.error('Move error', 2000);
                                break;
                            case 7: //MATRIX_ERROR
                                barObj.error('Matrix error', 2000);
                                break;                      
                            case 8: //GAME OVER
                                if (obj.x >= 0 && obj.y >= 0) {
                                    tableObj.setData(obj.x, obj.y, robotSign);
                                }
                                let res = JSON.parse(obj.details);
                                let winnerStr = '';
                                switch (res.winner) {
                                    case 1: winnerStr = 'Robot wins!'; break;
                                    case 2: winnerStr = 'You win!'; break;
                                    case 3: winnerStr = 'Draw!'; break;
                                    default: winnerStr = 'Error has occured';
                                }
                                barObj.info('Game over! ' + winnerStr, 2000);

                                tableObj.winnerLine(res.x0, res.y0, res.x1, res.y1, "background-color: #FDB2AB;");                                
                                tableObj.isDisabled(true);
                                break;
                            case 9: //PARSE_JSON_ERROR
                                barObj.error('Stringify json error', 2000);
                                break;
                            case 10: //PARSE_STRING_ERROR
                                barObj.error('Parse string error', 2000);
                                break;
                            default: console.log('Unknown status');
                        }
                    }
                }
            });
            
        let gameObj = null;
        
        $('#start_game').click(function() {
            
            var r = parseInt($('#input_rows').val());
            var c = parseInt($('#input_cols').val());
            if (isNaN(r) || isNaN(c) || r < 5 || c < 5) {
                console.log('Wrong row or column count!');
                return;
            }

            $('#stop_game').removeClass('disabled');
            $(this).addClass('disabled');

			if (tableObj != null) {
				return;
			}
            
            tableObj = $('#field').tickTackToe({
                rowCount: r,
                colCount: c
            });
            
            gameObj = new TicTacToeGame(tableObj, socketObj, barObj);
            gameObj.startGame();   
        });
        
        $('#stop_game').click(function() {
            $(this).addClass('disabled');
            if (gameObj != null) {
                gameObj.stopGame();
                delete gameObj;
                gameObj = null;
                delete tableObj;
                tableObj = null;
            }
            $('#start_game').removeClass('disabled');
        });
});