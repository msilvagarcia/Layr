define(function(){
	
	function configureNavigationBar(){
		$('.navbar li.dropdown').hover(
			onMenuItemMouseOver,
			onMenuItemMouseOut
		)
		$('.navbar li.dropdown li')
			.unbind('mouseover')
			.unbind('mouseout')
	}

	function onMenuItemMouseOver(){
    	$(this).addClass('open')
    		   .find('ul')
    		   .stop(true,true)
    		   .hide()
    		   .show()
    }

	function onMenuItemMouseOut(){
        $(this).removeClass('open')
        	   .find('ul')
        	   .stop(true,true)
        	   .delay(75)
        	   .fadeOut()
    }

	function configureOLarkChatLinks(){
		$(".open_chat").click(function(event){
			olark('api.box.expand')
			event.preventDefault()
		})
	}

	return {
		initialize: function(){
			configureNavigationBar()
			configureOLarkChatLinks()
		}
	}

})


