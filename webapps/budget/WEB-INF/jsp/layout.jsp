<%@ taglib uri="../tld/c.tld" prefix="c"%>
<%@ taglib uri="../tld/dd4.tld" prefix="dd4"%>
<%@ page import="com.digitald4.common.model.*"%>
<%@ page import="com.digitald4.common.servlet.*"%>
<%@ page import="com.digitald4.common.util.*"%>
<%@ page import="java.util.*"%>
<%@ page import="com.digitald4.common.component.*"%>
<%
	Company company = Company.get(); 
	User user = (User)session.getAttribute("user");
	String curpage = (String)request.getAttribute("body");
	String backPage = (String)request.getAttribute("backPage");
	curpage = curpage.substring(curpage.lastIndexOf("/") + 1, curpage.lastIndexOf("."));
	if (Navigation.get() == null) {
		ArrayList<NavItem> navItems = new ArrayList<NavItem>();
		navItems.add(new NavItem("Home", "dashboard", false)
			.addSubItem(new NavItem("Dashboard", "home"))
			.addSubItem(new NavItem("My Profile", "myprofile")));
		navItems.add(new NavItem("Patients", "patients", false)
			.addSubItem(new NavItem("Patients", "patients")
				.addSubItem(new NavItem("Patient", "patient")))
			.addSubItem(new NavItem("Pending Assessement", "penass")
				.addSubItem(new NavItem("Assessment", "assessment")))
			.addSubItem(new NavItem("New Intake", "intake"))
			.addSubItem(new NavItem("Pending Intakes", "pintake")));
		navItems.add(new NavItem("Users", "users", false)
			.addSubItem(new NavItem("Users", "users")
				.addSubItem(new NavItem("User", "user")))
			.addSubItem(new NavItem("Add User", "useradd")));
		navItems.add(new NavItem("Billing", "billing", true)
			.addSubItem(new NavItem("Billable", "billable"))
			.addSubItem(new NavItem("Payables", "penpay"))
			.addSubItem(new NavItem("Unpaid Invoices", "unpaidinvoices"))
			.addSubItem(new NavItem("Paid Invoices", "paidinvoices"))
			.addSubItem(new NavItem("Pay History", "payhistory"))
			.addSubItem(new NavItem("Vendors", "vendors")
				.addSubItem(new NavItem("Vendor", "vendor")))
			.addSubItem(new NavItem("Add Vendor", "vendoradd")));
		navItems.add(new NavItem("Nurses", "nurses", false)
			.addSubItem(new NavItem("Nurses", "nurses")
				.addSubItem(new NavItem("Nurse", "nurse")))
			.addSubItem(new NavItem("Add Nurse", "nurseadd"))
			.addSubItem(new NavItem("License Alert", "license_alert")));
		navItems.add(new NavItem("Reports", "reports", false)
			.addSubItem(new NavItem("Reports", "reports")));
		Navigation.setNavigation(new Navigation(navItems));
	}
%>

<!doctype html>
<!--[if lt IE 8 ]><html lang="en" class="no-js ie ie7"><![endif]-->
<!--[if IE 8 ]><html lang="en" class="no-js ie"><![endif]-->
<!--[if (gt IE 8)|!(IE)]><!--><html lang="en" class="no-js"><!--<![endif]-->
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
    <meta charset="utf-8">
	
	<title><%=company.getName()%></title>
	<meta name="description" content="">
	<meta name="author" content="">
	
	<!-- Global stylesheets -->
	<link href="css/reset.css" rel="stylesheet" type="text/css">
	<link href="css/common.css" rel="stylesheet" type="text/css">
	<link href="css/form.css" rel="stylesheet" type="text/css">
	<link href="css/standard.css" rel="stylesheet" type="text/css">
	
	<!-- Comment/uncomment one of these files to toggle between fixed and fluid layout -->
	<!--<link href="css/960.gs.css" rel="stylesheet" type="text/css">-->
	<link href="css/960.gs.fluid.css" rel="stylesheet" type="text/css">
	
	<!-- Custom styles -->
	<link href="css/simple-lists.css" rel="stylesheet" type="text/css">
	<link href="css/block-lists.css" rel="stylesheet" type="text/css">
	<link href="css/planning.css" rel="stylesheet" type="text/css">
	<link href="css/table.css" rel="stylesheet" type="text/css">
	<link href="css/calendars.css" rel="stylesheet" type="text/css">
	<link href="css/wizard.css" rel="stylesheet" type="text/css">
	<link href="css/gallery.css" rel="stylesheet" type="text/css">
	
	<!-- Favicon -->
	<link rel="shortcut icon" type="image/x-icon" href="favicon.ico">
	<link rel="icon" type="image/png" href="favicon-large.png">
	
	<!-- Modernizr for support detection, all javascript libs are moved right above </body> for better performance -->
	<script src="js/libs/modernizr.custom.min.js"></script>
	
	<script src="js/libs/jquery-1.10.2.min.js"></script>
	<script src="js/libs/jquery-ui-1.11.0.min.js"></script>
	<script src="js/libs/angular.min.js"></script>	
	
	
	<!-- Digital D4 -->
	<script src="js/angular/route.js"></script>
	<script src="js/angular/billsservice.js"></script>
	<script src="js/angular/defaultviewctrl.js"></script>
	<script src="js/angular/listctrl.js"></script>
	<script src="js/angular/module.js"></script>
	<script src="js/update.js"></script>
	<script src="js/mapauto.js"></script>
	
</head>

<body>
	
	<!-- Header -->
	
	<!-- Server status -->
	<header><div class="container_12">
		
		<p id="skin-name"><small>Budget</small> <strong>0.5</strong></p>
		<div class="server-info">Server: <strong><%=application.getServerInfo()%></strong></div>
		<div class="server-info">Java: <strong>1.6.0_27</strong></div>
		
	</div></header>
	<!-- End server status -->
	
	<!-- Main nav -->
	<dd4:nav selected="<%=curpage%>" navigation="<%=Navigation.get()%>" />
	<!-- End main nav -->
	
	<!-- Sub nav -->
	<div id="sub-nav"><div class="container_12">
		<a href="#" title="Help" class="nav-button"><b>Help</b></a>
		<form id="search-form" name="search-form" method="post" action="search.html">
			<input type="text" name="s" id="s" value="" title="Search admin..." autocomplete="off">
		</form>
	</div></div>
	<!-- End sub nav -->
	
	<!-- Status bar -->
	<div id="status-bar"><div class="container_12">
	
		<ul id="status-infos">
			<li class="spaced">Logged as: <strong><%=user.getEmail()%></strong></li>
			<li><a href="logout" class="button red" title="Logout"><span class="smaller">LOGOUT</span></a></li>
		</ul>
		
		<!-- v1.5: you can now add class red to the breadcrumb -->
		<dd4:breadcrumb selected="<%=curpage%>" navigation="<%=Navigation.get()%>" />
	
	</div></div>
	<!-- End status bar -->
	
	<div id="header-shadow"></div>
	<!-- End header -->
	
	<!-- Always visible control bar -->
	<div id="control-bar" class="grey-bg clearfix"><div class="container_12">
		<% if (backPage != null) { %>
			<div class="float-left">
				<a href="<%=backPage%>"><button type="button"><img src="images/icons/fugue/navigation-180.png" width="16" height="16"> Back to list</button></a>
			</div>
		<%}%>
		<!--div class="float-right"> 
			<button type="button" disabled="disabled">Disabled</button>
			<button type="button" class="red">Cancel</button> 
			<button type="button" class="grey">Reset</button> 
			<button type="button"><img src="images/icons/fugue/tick-circle.png" width="16" height="16"> Save</button>
		</div-->
			
	</div></div>
	<!-- End control bar -->
	
	<!-- Content -->
	<c:import url="${body}" />
	<!-- End content -->
	
	<footer>
		<div class="float-right">
			<a href="#top" class="button"><img src="images/icons/fugue/navigation-090.png" width="16" height="16">Page top</a>
		</div>
	</footer>
	
	<!--
	
	Updated as v1.5:
	Libs are moved here to improve performance
	
	-->
	
	<!-- Generic libs -->
	<script src="js/libs/jquery.hashchange.js"></script>
	
	<!-- Template libs -->
	<script src="js/jquery.accessibleList.js"></script>
	<script src="js/searchField.js"></script>
	<script src="js/common.js"></script>
	<script src="js/standard.js"></script>
	<!--[if lte IE 8]><script src="js/standard.ie.js"></script><![endif]-->
	<script src="js/jquery.tip.js"></script>
	<script src="js/jquery.contextMenu.js"></script>
	<script src="js/jquery.modal.js"></script>
	
	<!-- Custom styles lib -->
	<script src="js/list.js"></script>
	
	<!-- Plugins -->
	<script src="js/libs/jquery.dataTables-1.10.0.js"></script>
	<script src="js/libs/jquery.datepick/jquery.datepick.js"></script>
	
	<!-- Charts library -->
	<!--Load the AJAX API-->
	<script src="http://www.google.com/jsapi"></script>
	
	<script>
		
		/*
		 * This script shows how to setup the various template plugins and functions
		 */
		
		$(document).ready(function()
		{
			checkSession();
			
			/*
			 * Example context menu
			 */
			
			// Context menu for all favorites
			$('.favorites li').bind('contextMenu', function(event, list)
			{
				var li = $(this);
				
				// Add links to the menu
				if (li.prev().length > 0) {
					list.push({ text: 'Move up', link:'#', icon:'up' });
				}
				if (li.next().length > 0) {
					list.push({ text: 'Move down', link:'#', icon:'down' });
				}
				list.push(false);	// Separator
				list.push({ text: 'Delete', link:'#', icon:'delete' });
				list.push({ text: 'Edit', link:'#', icon:'edit' });
			});
			
			// Extra options for the first one
			$('.favorites li:first').bind('contextMenu', function(event, list)
			{
				list.push(false);	// Separator
				list.push({ text: 'Settings', icon:'terminal', link:'#', subs:[
					{ text: 'General settings', link: '#', icon: 'blog' },
					{ text: 'System settings', link: '#', icon: 'server' },
					{ text: 'Website settings', link: '#', icon: 'network' }
				] });
			});
			
			/*
			 * Dynamic tab content loading
			 */
			
			$('#tab-comments').onTabShow(function() {
				$(this).loadWithEffect('ajax-tab.html', function() {
					notify('Content loaded via ajax');
				});
			}, true);
			
			/*
			 * Table sorting
			 */
			
			// A small classes setup...
			$.fn.dataTableExt.oStdClasses.sWrapper = 'no-margin last-child';
			$.fn.dataTableExt.oStdClasses.sInfo = 'message no-margin';
			$.fn.dataTableExt.oStdClasses.sLength = 'float-left';
			$.fn.dataTableExt.oStdClasses.sFilter = 'float-right';
			$.fn.dataTableExt.oStdClasses.sPaging = 'sub-hover paging_';
			$.fn.dataTableExt.oStdClasses.sPagePrevEnabled = 'control-prev';
			$.fn.dataTableExt.oStdClasses.sPagePrevDisabled = 'control-prev disabled';
			$.fn.dataTableExt.oStdClasses.sPageNextEnabled = 'control-next';
			$.fn.dataTableExt.oStdClasses.sPageNextDisabled = 'control-next disabled';
			$.fn.dataTableExt.oStdClasses.sPageFirst = 'control-first';
			$.fn.dataTableExt.oStdClasses.sPagePrevious = 'control-prev';
			$.fn.dataTableExt.oStdClasses.sPageNext = 'control-next';
			$.fn.dataTableExt.oStdClasses.sPageLast = 'control-last';
			
			// Apply to table
			$('.sortable').each(function(i) {
				// DataTable config
				var table = $(this),
					oTable = table.dataTable({
						//"bSort": false,
						
						/*
						 * We set specific options for each columns here. Some columns contain raw data to enable correct sorting, so we convert it for display
						 * @url http://www.datatables.net/usage/columns
						 *
						aoColumns: [
							{ bSortable: false},	// No sorting for this columns, as it only contains checkboxes
							{ sType: 'string' },
							{ sType: 'date'   },
							{ sType: 'string' },
							{ sType: 'string' },
							{ sType: 'numeric'},
							{ sType: 'string' }
						],*/
						
						"aoColumnDefs": [
						  { "bSortable": false, "aTargets": [ 0 ] }
						],
						
						//"order": [[ 1, "asc" ]],
						
						
						/*
						 * Set DOM structure for table controls
						 * @url http://www.datatables.net/examples/basic_init/dom.html
						 */
						sDom: '<"block-controls"<"controls-buttons"p>>rti<"block-footer clearfix"lf>',
						
						/*
						 * Callback to apply template setup
						 */
						fnDrawCallback: function()
						{
							this.parent().applyTemplateSetup();
						},
						fnInitComplete: function()
						{
							this.parent().applyTemplateSetup();
						}
					});
				
				// Sorting arrows behaviour
				table.find('thead .sort-up').click(function(event) {
					// Stop link behaviour
					event.preventDefault();
					
					// Find column index
					var column = $(this).closest('th'),
						columnIndex = column.parent().children().index(column.get(0));
					
					// Send command
					oTable.fnSort([[columnIndex, 'asc']]);
					
					// Prevent bubbling
					return false;
				});
				table.find('thead .sort-down').click(function(event) {
					// Stop link behaviour
					event.preventDefault();
					
					// Find column index
					var column = $(this).closest('th'),
						columnIndex = column.parent().children().index(column.get(0));
					
					// Send command
					oTable.fnSort([[columnIndex, 'desc']]);
					
					// Prevent bubbling
					return false;
				});
			});
			
			/*
			 * Datepicker
			 * Thanks to sbkyle! http://themeforest.net/user/sbkyle
			 */
			$('.datepicker').datepick({
				alignment: 'bottom',
				showOtherMonths: true,
				selectOtherMonths: true,
				renderer: {
					picker: '<div class="datepick block-border clearfix form"><div class="mini-calendar clearfix">' +
							'{months}</div></div>',
					monthRow: '{months}', 
					month: '<div class="calendar-controls" style="white-space: nowrap">' +
								'{monthHeader:M yyyy}' +
							'</div>' +
							'<table cellspacing="0">' +
								'<thead>{weekHeader}</thead>' +
								'<tbody>{weeks}</tbody></table>', 
					weekHeader: '<tr>{days}</tr>', 
					dayHeader: '<th>{day}</th>', 
					week: '<tr>{days}</tr>', 
					day: '<td>{day}</td>', 
					monthSelector: '.month', 
					daySelector: 'td', 
					rtlClass: 'rtl', 
					multiClass: 'multi', 
					defaultClass: 'default', 
					selectedClass: 'selected', 
					highlightedClass: 'highlight', 
					todayClass: 'today', 
					otherMonthClass: 'other-month', 
					weekendClass: 'week-end', 
					commandClass: 'calendar', 
					commandLinkClass: 'button',
					disabledClass: 'unavailable'
				},
				onSelect: function(dateText, inst) {
					$(this).change();
				}
			});
			
			$('.weekpicker').datepick({selectWeek: true, closeOnSelect: false});
		});
		
		
		// Demo modal
		function openModal() {
			$.modal({
				content: '<p>This is an example of modal window. You can open several at the same time (click button below!), move them and resize them.</p>'+
						  '<p>The plugin provides several other functions to control them, try below:</p>'+
						  '<ul class="simple-list with-icon">'+
						  '    <li><a href="javascript:void(0)" onclick="$(this).getModalWindow().setModalTitle(\'\')">Remove title</a></li>'+
						  '    <li><a href="javascript:void(0)" onclick="$(this).getModalWindow().setModalTitle(\'New title\')">Change title</a></li>'+
						  '    <li><a href="javascript:void(0)" onclick="$(this).getModalWindow().loadModalContent(\'ajax-modal.html\')">Load Ajax content</a></li>'+
						  '</ul>',
				title: 'Example modal window',
				maxWidth: 500,
				buttons: {
					'Open new modal': function(win) { openModal(); },
					'Close': function(win) { win.closeModal(); }
				}
			});
		}
	
	</script>

</body>
</html>