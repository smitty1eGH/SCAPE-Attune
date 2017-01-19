<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"  %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
</head>
<body>
	<div id="compare-container">
		<c:forEach items="${data}" var="entity">
			<div class="comparing-entity-container">
				<div class="comparing-entity">
					<div class="comparing-entity-header">
						<c:forEach items="${entity.value}" var="property">
							<c:if test="${property.key ==  'name'}">
								<h2 class="comparing-entity-title">${property.value}</h2>
							</c:if>
						</c:forEach>
					</div>
					<div class="comparing-entity-body">
						<c:forEach items="${entity.value}" var="property">
							<c:if test="${property.key !=  'name'}">
								<div class="comparing-property-container">
									<h3 class="comparing-property">${property.key}</h3>
									<p class="comparing-value">${property.value}</p>
								</div>
							</c:if>
						</c:forEach>
					</div>
				</div>
			</div>
		</c:forEach>
	</div>
</body>
</html>