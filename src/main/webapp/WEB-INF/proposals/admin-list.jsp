<%--

    Copyright © 2014 Instituto Superior Técnico

    This file is part of FenixEdu Academic Thesis.

    FenixEdu Academic Thesis is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FenixEdu Academic Thesis is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FenixEdu Academic Thesis.  If not, see <http://www.gnu.org/licenses/>.

--%>
<!DOCTYPE html>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


${portal.toolkit()}

<div class="page-header">
	<h1><spring:message code="title.thesisProposal.management"/></h1>
</div>

<script type="text/javascript">

	$(document).ready(function() {
		$("input[name=isVisible][value=${isVisible}]").prop('checked', 'true');
		$("input[name=isAttributed][value=${isAttributed}]").prop('checked', 'true');
		$("input[name=hasCandidacy][value=${hasCandidacy}]").prop('checked', 'true');
		$(".filter").change(function() {
			$("#search").submit();
		});

		$("#configuration").change(function() {
			$("#chooseConfiguration").submit();
		});
	});
</script>

<c:if test="${empty configuration}">
	<div class="alert alert-info" role="alert"><spring:message code="label.no.configurations.available"/></div>
</c:if>

<c:if test="${not empty configuration}">
<c:url var="viewCandidatesUrl" value="/admin-proposals/candidates?configuration=${configuration.externalId}"/>
<c:url var="exportToExcelUrl" value="/admin-proposals/export?configuration=${configuration.externalId}"/>

<div class="well">
	<p>
		<spring:message code="label.proposals.admin.well"/>
	</p>
</div>
<form class="form" id="chooseConfiguration" method="GET">
	<div class="form-group">
		<label for="configuration"><spring:message code="label.configuration"/></label>
		<select id="configuration" name="configuration" class="form-control">
			<c:forEach items="${configurations}" var="config">
				<option <c:if test="${config.externalId eq configuration.externalId}">selected="selected"</c:if> value="${config.externalId}">${config.presentationName}</option>
			</c:forEach>
		</select>
	</div>
</form>

<div class="alert alert-info">
	<p><spring:message code="label.thesis.proposal.info" arguments="${configuration.executionDegree.degree.sigla},${configuration.proposalPeriod.start.toString('dd-MM-YYY HH:mm')},${configuration.proposalPeriod.end.toString('dd-MM-YYY HH:mm')}"/></p>
	<p><spring:message code="label.thesis.candidacy.info" arguments="${configuration.executionDegree.degree.sigla},${configuration.candidacyPeriod.start.toString('dd-MM-YYY HH:mm')},${configuration.candidacyPeriod.end.toString('dd-MM-YYY HH:mm')}"/></p>
</div>


<p>
	<a href="${viewCandidatesUrl}"><spring:message code="label.view.candidates"/></a>
	<br />
	<a href="${exportToExcelUrl}"><spring:message code="label.proposals.export.to.excel"/></a>
</p>

<div class="panel panel-default">
  <div class="panel-heading"><spring:message code="label.filter" /></div>
  <div class="panel-body">
    <form class="form" method="GET" id="search">
    	<input type="hidden" name="configuration" value="${configuration.externalId}"/>
		<table class="table table-condensed">
			<thead>
				<tr>
					<th><spring:message code="label.visibility"/></th>
					<th><spring:message code="label.attribution"/></th>
					<th><spring:message code="label.candidacies"/></th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>
						<input name="isVisible" class="filter" type="radio" value=""> <spring:message code="label.all"/></br>
					</td>
					<td>
						<input name="isAttributed" class="filter" type="radio" value=""> <spring:message code="label.all"/></br>
					</td>
					<td>
						<input name="hasCandidacy" class="filter" type="radio" value=""> <spring:message code="label.all"/></br>
					</td>
				</tr>
				<tr>
					<td>
						<input name="isVisible" class="filter" type="radio" value="true"> <spring:message code="label.visible"/></br>
					</td>
					<td>
						<input name="isAttributed" class="filter" type="radio" value="true"> <spring:message code="label.is.attributed"/></br>
					</td>
					<td>
						<input name="hasCandidacy" class="filter" type="radio" value="true"> <spring:message code="label.with.candidates"/></br>
					</td>
				</tr>
				<tr>
					<td>
						<input name="isVisible" class="filter" type="radio" value="false"> <spring:message code="label.proposal.status.hidden"/></br>
					</td>
					<td>
						<input name="isAttributed" class="filter" type="radio" value="false"> <spring:message code="label.not.attributed"/></br>
					</td>
					<td>
						<input name="hasCandidacy" class="filter" type="radio" value="false"> <spring:message code="label.without.candidates"/></br>
					</td>
				</tr>
			</tbody>
		</table>
	</form>
  </div>
</div>

<br />

<c:url var="createProposalUrl" value="/admin-proposals/createProposal?configuration=${configuration.externalId}"/>
<a href="${createProposalUrl}" class='btn btn-default'><spring:message code='title.thesisProposal.create'/></a>

<c:if test="${empty coordinatorProposals}">
	<p><spring:message code="label.proposals.search.result.empty"/></p>
</c:if>

<c:if test="${not empty coordinatorProposals}">
	<h4><spring:message code="label.proposals.search.result" arguments="${coordinatorProposals.size()}"/></h4>
	<div class="table-responsive">
		<table class="table">
			<thead>
				<tr>
					<th>
						<spring:message code='label.thesis.id'/>
					</th>
<!-- 					<th> -->
<%-- 						<spring:message code='label.year'/> --%>
<!-- 					</th> -->
					<th>
						<spring:message code='label.title'/>
					</th>
					<th>
						<spring:message code='label.participants'/>
					</th>
					<th>
						<spring:message code='label.proposal.status'/>
					</th>
					<th>
						<spring:message code='label.number.of.candidacies'/>
					</th>
					<th>
						<spring:message code='label.student.candidacy.accepted'/>
					</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${coordinatorProposals}" var="thesisProposal">
					<tr>
						<td>${thesisProposal.identifier}</td>
<%-- 						<td>${thesisProposal.getSingleThesisProposalsConfiguration().executionDegree.executionYear.year}</td> --%>
						<td>${thesisProposal.title}</td>
						<td>
							<c:forEach items="${thesisProposal.getSortedParticipants()}" var="participant">
								<div>${participant.user.name} <small>-</small> <b>${participant.thesisProposalParticipantType.name.content}</b></div>
							</c:forEach>
						</td>
						<td>
							<c:if test="${thesisProposal.hidden}">
								<spring:message code='label.proposal.status.hidden'/>
							</c:if>
							<c:if test="${!thesisProposal.hidden}">
								<spring:message code='label.proposal.status.visible'/>
							</c:if>
						</td>
						<td>
							${thesisProposal.getStudentThesisCandidacySet().size()}
						</td>
						<td>
							<spring:message code="label.${service.isAccepted(thesisProposal) ? 'yes' : 'no'}"/>
						</td>
						<td>
							<c:set var="degreesLabels" value="${fn:join(service.getThesisProposalDegrees(thesisProposal), ',')}"/>
							<c:set var="candidatesLabels" value='${fn:join(service.getThesisProposalCandidates(thesisProposal), "</br>")}'/>
							<c:url var="toggleVisibilityUrl" value="/admin-proposals/toggle/${thesisProposal.externalId}?configuration=${configuration.externalId}&isVisible=${isVisible}&isAttributed=${isAttributed}&hasCandidacy=${hasCandidacy}"/>
							<p></p>
							<div class="btn-group btn-group-xs">
							<a href="${toggleVisibilityUrl}" class="btn btn-default"><spring:message code="label.toggle.visibility"/></a>
							<button class='detailsButton btn btn-default' data-observations="<c:out escapeXml="true" value="${thesisProposal.observations}"/>" data-requirements="<c:out escapeXml="true" value="${thesisProposal.requirements}"/>" data-goals="<c:out escapeXml="true" value="${thesisProposal.goals}"/>" data-localization="<c:out value="${thesisProposal.localization}"/>" data-degrees="${degreesLabels}" data-candidates="${candidatesLabels}"value='<spring:message code="button.details"/>' data-thesis="${thesisProposal.externalId}">
								<spring:message code="label.details"/>
							</button>
							<a href="${pageContext.request.contextPath}/admin-proposals/edit/${thesisProposal.externalId}" class="btn btn-default">
								<spring:message code='button.edit'/>
							</a>
							<c:if test="${thesisProposal.studentThesisCandidacy.size() > 0}">
								<a href="${pageContext.request.contextPath}/admin-proposals/manage/${thesisProposal.externalId}" class="btn btn-default"><spring:message code="label.candidacies.manage"/></a>
							</c:if>
							</div>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</c:if>
<style>
form{
	display: inline
}
</style>

<!-- Modal -->
<div class="modal fade" id="view" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="form-horizontal modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close"><span class="sr-only"><spring:message code="button.close"/></span></button>
				<h3 class="modal-title"><spring:message code="label.details"/></h3>
				<small class="explanation"><spring:message code="label.modal.proposals.details"/></small>
			</div>
			<div class="modal-body">
				<div class="form-group">
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.goals'/></label>
					<div class="col-sm-10">
						<div class="information goals"></div>
					</div>
				</div>

				<div class="form-group">
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.requirements'/></label>
					<div class="col-sm-10">
						<div class="information requirements"></div>
					</div>
				</div>

				<div class="form-group">
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.localization'/></label>
					<div class="col-sm-10">
						<div class="information localization"></div>
					</div>
				</div>

				<div class="form-group">
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.observations'/></label>
					<div class="col-sm-10">
						<div class="information observations"></div>
					</div>
				</div>

				<div class="form-group">
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.executionDegrees'/></label>
					<div class="col-sm-10">
						<div class="information degrees"></div>
					</div>
				</div>

				<div class="form-group">
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.candidates'/></label>
					<div class="col-sm-10">
						<div class="information candidates"></div>
					</div>
				</div>

			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code='button.close'/></button>
			</div>
		</div>
	</div>
</div>

</c:if>

<style type="text/css">
.information{
	margin-top: 7px;
	margin-left: 10px;
}
</style>

<script type="text/javascript">
$(".manageButton").on("click", function(){
	var id = $(this).data('thesis-proposal')
	$("#" + id).submit();
})


jQuery(document).ready(function(){
	jQuery('.detailsButton').on('click', function(event) {
		$("#details" + $(this).data("thesis")).toggle('show');

	});
});

$(function(){
	$(".detailsButton").on("click", function(evt){
		var e = $(evt.target);

		['observations','requirements','goals','localization','degrees', 'candidates'].map(function(x){
			$("#view ." + x).html(e.data(x));
		});

		$('#view').modal('show');
	});
})
</script>
