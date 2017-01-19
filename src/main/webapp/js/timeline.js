function TimelineModel()
{
	selectedEntity = {}; // Name and count of the selected entity
	this.parentEntity = {}; // Name and count of the parent entity
	this.childEntity = {}; // Name and count of the child entity
	this.sumOn = []; // List of names and counts of properties of the selected entity
	
	this.timeline = null; // The graph that gets drawn
	analysisModel = null;
	
	var that = this;
	
	this.init = function()
	{
		$.getJSON("getTimelineModel.exec", function(model)
		{
			console.log(model);
//			model = {
//				selectedEntity: {name: "", value: ""},
//				eventCount: integer,
//				totalBudget: integer,
//				
//				timelineData: {items: [], groups: [], types: [],
//				
//				budgetData: [{name: "", value: ""}]
//			}
			updateHeaderTiles(model.selectedEntity, model.eventCount, model.totalBudget);
			
			selectedEntity = model.selectedEntity;
			
			// Initialize the Timeline
			that.timeline = new Timeline();
			that.timeline.init(model.timelineData);
			
			// Initialize the Analysis Model
			analysisModel = new AnalysisModel()
			analysisModel.init(model.budgetData, model.timelineData);
			analysisModel.drawTable();
		});
	}
	
	this.saveTimeline = function(callback) 
	{
		var timelineData = {};
		var data = [];
		
		var dataset = that.timeline.dataset._data;
		
		for (item in dataset)
		{
			var startDate = new Date(dataset[item].start);
			var endDate = dataset[item].end ? new Date(dataset[item].end) : startDate;
			
			var event = {
					group: dataset[item].group,
					id: '',
					content: dataset[item].content,
					type: dataset[item].type,
					color: '',
					start: (startDate.getMonth() + 1) + '/' + startDate.getDate() + '/' + startDate.getFullYear(),
					end: (endDate.getMonth() + 1) + '/' + endDate.getDate() + '/' + endDate.getFullYear(),
					cost: dataset[item].cost,
					internalType: dataset[item].internalType,
					entityLabel: dataset[item].entityLabel
			}
			
			data.push(event);
		}
		
		// Set the data, groups, and types (groups and types are empty arrays b/c we are only
		// saving the events of the timeline, not the groups or types
		timelineData.data = data;
		timelineData.groups = [];
		timelineData.types = [];
		
		$.ajax({
			type: 'POST',
			contentType: 'application/json; charset=utf-8',
			dataType: 'json',
			url: 'http://localhost:8080/attune/saveTimeline.exec',
			data: JSON.stringify(timelineData)
		}).done(function(response) {
			return callback(response);
		});
	}
	
	function updateHeaderTiles(selectedEntity, eventCount, totalBudget)
	{
		$('#selected-entity-count').html(selectedEntity.value + ' <small>' + pluralizeByCount(selectedEntity.name, selectedEntity.value) + '</small>');
		$('#event-count').html(eventCount + ' <small>Events</small>');
		$('#total-budget').html(totalBudget + ' <small>Budget</small>');
	}
	
	function Timeline()
	{
		this.dataset = null; // This contains only the items plotted, not the groups
		this.container = null; // Graph container
		this.groups = null; // The rows of the timeline
		this.options = null; // Options of the timeline
		this.graph = null;
		this.config = null;
		this.colorMap = null;
		
		var that = this;
		
		this.init = function(timelineData)
		{
			that.groups = sortGroups(timelineData.groups);
			
			// Verify the format of the dates being used
			correctDates(timelineData.data);
			
			// Form the color map that will is used to set the color of points
			that.colorMap = getColorMap(timelineData.types);
			
			// Add styling to the data points
			addStyle(timelineData.data, that.colorMap);
			
			// Get the start/end dates from the data
			var startDate = getStartDate(timelineData.data);
			var endDate = getEndDate(timelineData.data);
			
			// Build the dataset that gets plotted
			that.dataset = new vis.DataSet(timelineData.data);
			
			// Timeline container
			that.container = document.getElementById('timeline');
			
			// Options for timeline
			that.options = {
				width: '100%',
				height: '100%',
				
				editable: {
					add: false,
					remove: false,
					updateGroup: false,
					updateTime: true,
				},
				
				selectable: true,
				
				start: startDate,
				min: startDate,
				end: endDate,
				max: endDate,
				
				stack: false,
				
				snap: function snap(date, scale, step) {
					var day = 24 * 60 * 60 * 1000;
					var fourHours = day / 6;
					return (Math.round(date / day) * day) + fourHours;
				},
				
				zoomable: true,
				
				dataAttributes: ["content", "id", "description", "start", "end", "cost", "internalType", 'entityLabel'],
				
				orientation: {
					axis: 'top'
				},
				
				groupOrder: 'order',
				
				onMove: function (item, callback) {
					analysisModel.update(item, false);
					callback(item);
					$('#analysis-report td').removeClass('updating');
					that.updateDependency(item);
					
				},
				
				onMoving: function(item, callback) {
					analysisModel.update(item, false);
					callback(item);
					that.updateDependency(item);
				},
				
				onAdd: function(item, callback) {
					item.type = "point";
					item.style = 'margin-left: -8px; margin-top: 2px; width: 0px; height: 0px; border-left: 8px solid transparent; border-right: 8px solid transparent; border-bottom: 16px solid red;';
					$('#addItemModal').modal('show');
					var date = new Date(item.start);
					$('#addItem-start').datepicker('setDate', date);
					callback(item);
					hideMilestoneLabels();
				},
				
				onRemove: function(item, callback) {
					if (item.type == "point")
						analysisModel.update(item, true);
					
					$('#analysis-report td').removeClass('updating');
					
					callback(item);
				}
			}
			
			buildTimeline();
			
			linkBaseballCards();
			
			hideMilestoneLabels();
			
			config = new Config(that.groups);
			config.init();
			
			$('.vis-item').each(function() 
			{
				$(this).attr('data-toggle', 'tooltip');
			})
			
			$('.vis-item').tooltip(
				{
					title: function() {
						var title = '';
						var name = $(this).attr('data-internaltype') ? $(this).attr('data-internaltype') : $(this).attr('data-content');
						
						title += '<b>' + name + '</b><br>\n';
						var startDate = new Date($(this).attr('data-start'));
						var start = (startDate.getMonth() + 1) + '/' + startDate.getDate();
						var endDate = new Date($(this).attr('data-end') ? $(this).attr('data-end') : startDate);
						var end = (endDate.getMonth() + 1) + '/' + endDate.getDate();
						
						if (startDate.getMonth() === endDate.getMonth() && startDate.getDate() === endDate.getDate()
								&& startDate.getFullYear() === endDate.getFullYear())
							title += start;
						else
							title += start + ' - ' + end;
						
						return title;
					},
					trigger: 'hover',
					container: 'body',
					html: true
				}
			);
		}
		
		this.updateDependency = function(item)
		{
			var internalType = (typeof item === 'string') ? item : item.internalType;
			var events = getEventsByInternalType(internalType);
			var overlaps = getOverlaps(events);
			
			for (var i = 0; i < events.length; i++)
			{
				if (overlaps.indexOf(events[i]) >= 0)
				{
					if (events[i].data.className && events[i].data.className.indexOf('hasOverlap') === -1)
						events[i].data.className += ' hasOverlap';
				}
				else
					events[i].data.className = events[i].data.className.replace(/hasOverlap/g, '');
			}
			
			that.graph.redraw();
		}
		
		function getEventsByInternalType(internalType)
		{
			var events = [];
			var items = that.graph.itemSet.items
			
			for (var event in items)
				if (internalType && items[event].data.internalType === internalType)
					events.push(items[event]);
			
			return events;
		}
		
		function getOverlaps(events)
		{
			var overlaps = [];
			
			for (var i = 0; i < events.length; i++)
			{
				var eventOne = events[i];
				var eventOneStart = new Date(eventOne.data.start);
				var eventOneEnd = new Date(eventOne.data.end);
				
				for (var j = (i+1); j < events.length; j++)
				{
					var hasOverlap = false;
					
					var eventTwo = events[j];
					var eventTwoStart = new Date(eventTwo.data.start);
					var eventTwoEnd = new Date(eventTwo.data.end);
					
					if (eventOneStart >= eventTwoStart && eventOneStart <= eventTwoEnd)
						hasOverlap = true;
					if (eventOneEnd >= eventTwoStart && eventOneEnd <= eventTwoEnd)
						hasOverlap = true;
					if (eventTwoStart >= eventOneStart && eventTwoStart <= eventOneEnd)
						hasOverlap = true;
					if (eventTwoEnd >= eventOneStart && eventTwoEnd <= eventOneEnd)
						hasOverlap = true;
					if (eventOneStart.getTime() === eventTwoStart.getTime() && eventOneEnd.getTime() === eventTwoEnd.getTime())
						hasOverlap = true;
					
					if (hasOverlap)
					{
						if (overlaps.indexOf(eventOne) === -1)
							overlaps.push(eventOne);
						if (overlaps.indexOf(eventTwo) === -1)
							overlaps.push(eventTwo);
					}
				}
			}
			
			return overlaps;
		}
		
		// Custom sorting function for groups
		function sorter(a, b) {
			if (parseInt(a.groupByValue) > parseInt(b.groupByValue))
				return 1;
			if (parseInt(a.groupByValue) < parseInt(b.groupByValue))
				return -1;
			return 0;
		}
		
		function sortGroups(unsortedGroups) {
			var fundedGroups = [];
			var unfundedGroups = [];
			
			// Separate the funded/unfunded groups first
			for (i = 0; i < unsortedGroups.length; i++) {
				if (unsortedGroups[i].funded === true)
					fundedGroups.push(unsortedGroups[i]);
				else
					unfundedGroups.push(unsortedGroups[i]);
			}
			
			// Sort each list of groups
			fundedGroups.sort(sorter);
			unfundedGroups.sort(sorter);
			
			var index = 0;
			for (i = 0; i < fundedGroups.length; i++) {
				unsortedGroups[index] = fundedGroups[i];
				unsortedGroups[index].order = (index + 1);
				index++;
			}
			
			for (i = 0; i < unfundedGroups.length; i++) {
				unsortedGroups[index] = unfundedGroups[i];
				unsortedGroups[index].order = (index + 1);
				index++;
			}
			
			return unsortedGroups;
		}
		
		/***
		 * Config object. Allows the user to change the ordering of the timeline
		 * groups by dragging/dropping their order in a list that is in a 
		 * popup modal.
		 * 
		 * The order object holds the groups of the timeline model. When the
		 * user changes the order and applies, the groups order variable will
		 * be updated, and the graph redrawn.
		 */
		var Config = function(groups) {
			this.order = groups;
			this.unfundedIndexes = [];
			var that = this;
			
			this.init = function() {
				refreshList();
				
				var orderedList = document.getElementById("orderedList");
			     
				var listSorter = Sortable.create(orderedList, 
				{
					animation: 250,
				    draggable: 'li',
				    scrollSensitivity: 15,
				    scrollSpeed: 5,
				    onUpdate: function(event) {}
				}); 
				
				// Handler that updates the graph after clicking 'Apply' 
				$('#applyConfigLink').click(function() {
					var newGroups = [];
					var newOrder = [];
					var fundedGroups = [];
					var unfundedGroups = [];
					
					that.unfundedIndexes = [];
					
					// Get the new order of the groups
					$('#orderedList').find('li.list-group-item').each(function() {
						newOrder.push($(this).attr('data-groupId'));
					});
					
					newOrder.forEach(function(order) {
						var group = getGroup(order);
						
						if (group.funded === true)
							fundedGroups.push(group);
						else
							unfundedGroups.push(group);
					})
					
					var index = 0;
					for (i = 0; i < fundedGroups.length; i++) {
						newGroups[index] = fundedGroups[i];
						newGroups[index].order = (index + 1);
						
						index++;
					}
					
					for (i = 0; i < unfundedGroups.length; i++) {
						newGroups[index] = unfundedGroups[i];
						newGroups[index].order = (index + 1);

						// Push the new unfunded index on to the array
						that.unfundedIndexes.push(index);
						
						index++;
					}
					
					that.order = newGroups;
					setGroups(that.order);
					refreshList();
					refreshUnfundedIndexes();
					linkBaseballCards()
				});
				
				// Find the unfunded indexes for our first run through
				for (i = 0; i < that.order.length; i++) {
					if (that.order[i].funded === false)
						that.unfundedIndexes.push(i);
				}
				
				refreshUnfundedIndexes();
			}
			
			// Draws the list that the user can rearrange
			function refreshList() {
				var list = $('#orderedList');
				
				var html = '';
				
				for (i = 0; i < that.order.length; i++) {
					var nextItem = that.order[i];
					
					html += '<li class="list-group-item" data-groupId="' + nextItem.id + '">\n';
					html += '	<h4 class="timeline-entity-title">' + nextItem.content + '</h4>\n';
					html += '	<button type="button" class="navbar-toggle">\n';
					html += '		<span class="icon-bar base-color4"></span>\n';
					html += '		<span class="icon-bar base-color4"></span>\n';
					html += '		<span class="icon-bar base-color4"></span>\n';
					html += '	</button>\n';
					html += '</li>\n';
				}
				
				list.html(html);
			}
			
			// Draws the red background on the timline rows that are 'unfunded'
			function refreshUnfundedIndexes() {
				$('.vis-panel.vis-left .vis-label').each(function(index) {
					if (that.unfundedIndexes.indexOf(index) >= 0)
						$(this).addClass('unfunded');
					else
						$(this).removeClass('unfunded');
				});
				
				$('.vis-itemset .vis-foreground .vis-group').each(function(index) {
					if (that.unfundedIndexes.indexOf(index) >= 0)
						$(this).addClass('unfunded');
					else
						$(this).removeClass('unfunded');
				});
			}
		}
		
		this.reorderGroups = function(newGroups) {
			that.graph.setGroups(newGroups);
			that.graph.redraw();
		}
		
		function setGroups(newGroups) {
			that.graph.setGroups(newGroups);
			that.graph.redraw();
		}

		this.getDataset = function()
		{
			return that.dataset;
		}
		
		function buildTimeline()
		{
			that.graph = new vis.Timeline(that.container, that.dataset, that.groups, that.options);
		}
		
		function getGroup(groupId) {
			for (i = 0; i < that.groups.length; i++)
				if (that.groups[i].id === groupId)
					return that.groups[i];
		}
			
		function linkBaseballCards()
		{
			// Set the Y-axis labels
			$('.vis-inner').each(function()
			{
				var name = $(this).text().trim();
				var entity = selectedEntity.name;
				$(this).attr('data-name', name);
				$(this).attr('data-entity', entity);
				$(this).addClass('baseballCardLink');
			});
			
			// Register the listener for the links
			$('.baseballCardLink').click(function(){
		    	
		    	var baseballEntity = $(this).attr('data-entity');
		    	var baseballName = $(this).attr('data-name');
		    	
		    	localStorage.setItem("originalEntity", baseballEntity);
		    	localStorage.setItem("originalName", baseballName);   	
		    	
				$('#baseballCardModal').modal('show');
				
				var url = "baseballCard.exec?entity="+encodeURIComponent(baseballEntity)+"&name="+encodeURIComponent(baseballName);
				
				$("#baseballContent").load(url);
		    });
		}

		function hideMilestoneLabels()
		{
			$('.vis-item-content').css('visibility', 'hidden');
		}
	}
	
	/**
	 * The Analysis Model will update with the timeline graph
	 * as the user drags events around. To avoid interference, it
	 * doesn't use the same reference to the dataset that the 
	 * timeline uses, so it updates its dataset where needed.
	 * 
	 */
	function AnalysisModel()
	{
		budgets = null;
		domTable = $('#analysis-content table');
		budgetTableRows = ['Budget', 'Funded Costs', 'Unfunded Costs', 'Outcome'];
		data = null;
		groups = [];
		
		this.init = function(budgetData, dataset)
		{
			budgets = budgetData;
			data = dataset.data;
			groups = dataset.groups;
			
			// Set a new property for ranges, 'costPerDay', which is calculated 
			for (step = 0; step < data.length; step++) {
				if (data[step].cost && data[step].type === 'range') {
					var cost = parseFloat(data[step].cost.replace("$", "").replace(/,/g, ""));
					
					var start = new Date(data[step].start);
					var end = new Date(data[step].end);
					
					var oneDay = 24*60*60*1000;
					var diffDays = Math.round(Math.abs(end.getTime() - start.getTime())/ (oneDay));
					
					data[step].costPerDay = (cost / diffDays);
				}
			}
		}
		
		function getEventsByFundingStatus(isFunded) {
			var desiredGroups = [];
			var desiredEvents = [];
			
			// Default is true
			isFunded = (typeof isFunded !== 'boolean') ? true : isFunded
			for (i = 0; i < groups.length; i++) {
				var group = groups[i];
				
				if (group.funded === isFunded)
					desiredGroups.push(group.id);
			}
			
			data.forEach(function(item, index) {
				if (desiredGroups.indexOf(item.group) >= 0)
					desiredEvents.push(item);
			});
			
			return desiredEvents;
		}

		function getEventIsFunded(event) {
			var isFunded;
			groups.forEach(function(group, index) {
				if (group.id === event.group) {
					isFunded = group.funded;
				}
			});
			
			return isFunded;
		}
		
		// Sums up the costs of all of the events by a certain fiscal year,
		// and it's funding status
		function getCostByFiscalYear(fiscalYear, isFunded)
		{
			var fy = new Date(fiscalYear - 1, 9, 1);
			var totalCost = 0;
			isFunded = (typeof isFunded !== 'boolean') ? true : isFunded
			var events = getEventsByFundingStatus(isFunded);
			events.forEach(function(item, index)
			{
				var date = new Date(item.start);
				var nextFiscalYear = new Date(fiscalYear, 9, 1);
				
				// If the item is a range, then calculate the cost by finding the amount of
				// days in the range that fall in this fiscal year
				if (item.type === 'range') {
					var days = 0;
					var endDate = new Date(item.end);
					
					while (date <= endDate) {
						if ((date < nextFiscalYear) && (date >= fy))
							days++;
						
						date.setDate(date.getDate() + 1);
					}
					
					if (item.costPerDay)
						totalCost += (days * item.costPerDay);
				}
				else {
					if ((date < nextFiscalYear) && (date >= fy) && item.cost)
					{
						var cost = parseFloat(item.cost.replace("$", "").replace(/,/g, ""));
						totalCost += cost;
					}
				}
			})
			return totalCost;
		}
		
		// Based on the current data, this updates the table
		function updateTable()
		{
			var shortfalls = 0;
			budgets.forEach(function(budget, index)
			{
				var fundedCosts = getCostByFiscalYear(budget.name, true);
				var unfundedCosts = getCostByFiscalYear(budget.name, false)
				var budgetAsFloat = parseFloat(budget.value.replace('$', '').replace(/,/g, ''));
				
				$('#' + budget.name + '-fundedcosts').text(accounting.formatMoney(fundedCosts));
				$('#' + budget.name + '-unfundedcosts').text(accounting.formatMoney(unfundedCosts));
				$('#' + budget.name + '-budget').text(accounting.formatMoney(budgetAsFloat));
				
				if (budgetAsFloat < fundedCosts)
				{
					$('#' + budget.name + '-outcome').css('color', 'red');
					$('#' + budget.name + '-outcome').text('Shortfall');
					shortfalls++;
				}
				else
				{
					$('#' + budget.name + '-outcome').css('color', 'black');
					$('#' + budget.name + '-outcome').text('Surplus');
				}
			});
			$('#shortfalls').html(shortfalls + ' <small>Shortfalls</small>');
		}
		
		// Draws the bare minimum amount with budgets inserted in
		this.drawTable = function()
		{
			var html = "";
			
			html += "<thead>";
			html += "	<tr>";
			html += "		<th>Fiscal Year</th>";
			budgets.forEach(function(budget)
			{
				html += "		<th>" + budget.name + "</th>";
			});
			html += "	</tr>";
			html += "</thead>"
			
			html += "<tbody>";
			budgetTableRows.forEach(function(rowTitle)
			{
				html += "	<tr>";
				html += "		<th scope='row'>" + rowTitle + "</th>";
				budgets.forEach(function(budget)
				{
					html += "	<td id='" + budget.name + "-" + rowTitle.toLowerCase().replace(/ /g, '') + "'>" + budget.value + "</td>\n";
				});
				html += "	</tr>";
			});
			html += "</tbody>";
			domTable.append(html);
			
			updateTable();
		}
		
		// External update function used by the timeline object.
		// This function keeps the timeline's data, and the Analysis
		// Model's data in sync
		this.update = function(item, deleteFlag)
		{
			$('#analysis-report td').removeClass('updating');
			data.forEach(function(d, index)
			{
				if (d.id == item.id)
				{
					// Remove the element from the dataset if the intentions are to delete
					if (deleteFlag)
						data.splice(index, 1);
					else {
						d.start = item.start;
						
						if (d.end)
							d.end = item.end
					}
				}
			});
			var eventIsFunded = getEventIsFunded(item);
			var fy = getFiscalYearByCalendarDate(item.start);
			
			if (eventIsFunded === true)
				$('#' + fy + '-fundedcosts').toggleClass('updating');
			else
				$('#' + fy + '-unfundedcosts').toggleClass('updating')
			
			
			updateTable();
		}
	}
}

// Returns the fiscal year that the calendar date
// falls under. Example 9/31/2012 is FY 2012,
// but 10/1/2012 is FY 2013
function getFiscalYearByCalendarDate(date)
{
	date = new Date(date);
	var fiscalYearEnd = new Date(date.getFullYear(), 9, 1); // October first is the cutoff
	return (date < fiscalYearEnd) ? date.getFullYear() : date.getFullYear() + 1;
}

// Gets the earliest date in the data array
function getStartDate(data)
{
	var earliestDate = new Date("12/12/3000");
	data.forEach(function(milestone, index)
	{
		var date = new Date(milestone.start);
		if (date < earliestDate)
			earliestDate = date;
	});
	
	// Subtract 6 months to the date for extra space
	earliestDate.setMonth(earliestDate.getMonth() - 6);
	return earliestDate;
}

// Gets the latest date in the data array
function getEndDate(data)
{
	var latestDate = new Date("1/1/1900");
	data.forEach(function(milestone, index)
	{
		var date;
		if (milestone.type === "point")
			date = new Date(milestone.start);
		else if (milestone.type === "range")
			date = new Date(milestone.end);
		
		if (latestDate < date)
			latestDate = date;
	});
	
	// Add 6 months to the date for extra space
	latestDate.setMonth(latestDate.getMonth() + 6)
	return latestDate;
}

// getColorMap
function getColorMap(types) {
	var colors = ['color1', 'color2', 'color3', 'color4', 'color5', 'color6', 'color7'];
	var colorMap = {};
	
	for (i = 0; i < types.length; i++)
		colorMap[types[i]] = colors[i];
	
	return colorMap;
}

// Add styling to only the points - not ranges
function addStyle(data, colorMap)
{
	data.forEach(function(milestone, index)
	{
		if (milestone.type === 'point')
			milestone.className = 'diamond';
		
		if (milestone.internalType)
			milestone.className = 'diamond ' + colorMap[milestone.internalType];
	});
}

// This replaces any start dates that only specify a year,
// ie. '2012', and replaces it with a full date, ie '1/1/2012'
function correctDates(data)
{
	data.forEach(function(milestone, index)
	{
		if (milestone.type === "point" && milestone.start.length === 4)
			milestone.start = "1/1/" + milestone.start;
	});
}