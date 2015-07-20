var app = {
    
		
	storage: window.localStorage,
	
		
		
    initialize: function() {
        this.bindEvents();
        scheda.load("Ieri");
        
    },

    bindEvents: function() {
        document.addEventListener('deviceready', this.onDeviceReady, false);
    },

    onDeviceReady: function() {
        app.receivedEvent('deviceready');
    },
   
    receivedEvent: function(id) {
        var parentElement = document.getElementById(id);
        var listeningElement = parentElement.querySelector('.listening');
        var receivedElement = parentElement.querySelector('.received');

        listeningElement.setAttribute('style', 'display:none;');
        receivedElement.setAttribute('style', 'display:block;');

        console.log('Received Event: ' + id);
    }
};

var scheda = {
	     
	    data: {nome:"", valore:""},
	    
	    save: function() {
	         
	        if (scheda.data.nome != "") {
	             
	            app.storage.setItem(scheda.data.nome, JSON.stringify(scheda.data));
	        }
	    },
	    
	    load: function(nome) {
	        
	        if (nome != "") {
	             
	            var value = app.storage.getItem($.trim(nome));
	            scheda.data = JSON.parse(value);
	        }
	    },
	    
	    delete: function(nome) {
	        
	        if (nome != "") {
	            app.storage.removeItem($.trim(nome));
	        }
	    },
	    
	}

app.initialize();