<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"  %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
<script>
	$(document).ready(function() {
		$('table').tablesorter();
	});
</script>
</head>
<body>
	<table class="table table-bordered">
		<caption>Total: ${fn:length(analysisResult.entities)}</caption>
		<thead>
			<tr>
				<c:set var="firstEntity" value="${analysisResult.entities[0]}"/>
				<c:forEach items="${firstEntity.properties}" var="property">
					<th>${property.name} <div class="table-sorter-icon"></div></th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${analysisResult.entities}" var="entity">
				<tr>
					<th scope="row">
						<c:forEach items="${entity.properties}" var="property">
							<c:choose>
                                <c:when test="${property.name ==  'name'}">
								<a href="#" data-name="${property.value}" data-entity="${entity.label}" class="baseballCardLink">${property.value}</a>
                                </c:when>
                                <c:when test="${property.name == 'location'}">
                                    <td><a href="${property.value}" target="_blank">${property.value}</a></td>
                                </c:when>
                                <c:when test="${property.name == 'schema'}">
                                    <td><a mimetype="application/json" download="schema.json" href="${property.value}" target="_blank">${property.value}</a></td>
                                </c:when>
                                <c:when test="${property.name == 'queue'}">
                                    <td><a href="" target="_blank">${property.value}</a></td>
                                </c:when>
                                <c:when test="${property.name == 'description' || property.name == 'comment'}">
                                    <td>${fn:substring(property.value, 0, 90)}<c:if test="${fn:length(property.value) > 90}">...</c:if></td>
                                </c:when>
                                <c:otherwise>
							<td>${property.value}</td>
                                </c:otherwise>
                            </c:choose>
					</c:forEach>
					</th>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>