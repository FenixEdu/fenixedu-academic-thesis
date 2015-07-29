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


<div class="well">
	<p>
		<spring:message code="label.proposals.old.well"/>
	</p>
</div>

<c:if test="${empty recentProposals}">
	<spring:message code='label.proposals.empty'/>
</c:if>
<c:if test="${!empty recentProposals}">
<div class="table-responsive">
	<table class="table">
		<thead>
			<tr>
				<th>
					<spring:message code='label.thesis.id'/>
				</th>
				<th>
					<spring:message code='label.year'/>
				</th>
				<th>
					<spring:message code='label.title'/>
				</th>
				<th>
					<spring:message code='label.participants'/>
				</th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${recentProposals}" var="thesisProposal">
				<tr>
					<td>${thesisProposal.identifier}</td>
					<td>
						${thesisProposal.getSingleThesisProposalsConfiguration().executionDegree.executionYear.year}
					</td>
					<td>${thesisProposal.title}</td>
					<td>
						<c:forEach items="${thesisProposal.getSortedParticipants()}" var="participant">
							<div>${participant.user.name} <small>as</small> <b>${participant.thesisProposalParticipantType.name.content}</b></div>
						</c:forEach>
					</td>
					<td>
						<form role="form" method="GET" action="${pageContext.request.contextPath}/${baseAction}/transpose/${thesisProposal.externalId}" class="form-horizontal" id="thesisProposalTransposeForm">
							${csrf.field()}
							<button type="submit" class="btn btn-default"><spring:message code="button.transpose"/></button>
						</form>
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

			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code='button.close'/></button>
			</div>
		</div>
	</div>
</div>

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

		['observations','requirements','goals','localization','degrees'].map(function(x){
			$("#view ." + x).html(e.data(x).replace(/\n/g, '<br/>'));
		});

		$('#view').modal('show');
	});
})
</script>
