
$(document).ready(function()
{
	$('#spinner').hide();
	
	$('body').on('click','.execLink',function()
	{
		mainScreen = $(this).attr('href');
		
		$('#main').fadeOut(function(){
			$('#spinner').show();
			
			$('#main').empty().load(mainScreen,function()
			{
				$('#spinner').hide();
				
				$('#main').fadeIn();
				
				// Remove the editable selector elements on config page that cause height issues on other pages
				if (mainScreen != "configDashboard.exec")
					$('.es-list').remove();
				
				// If the user is on mobile device, toggle the sidebar
				if ($(window).width() <= 1024)
					$('#wrapper').addClass('toggled');
			});
		});
	
		return false;
	});
});

