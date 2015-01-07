<!DOCTYPE html>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<div class="page-header">
	<h1><spring:message code="title.thesisCandidacy.management"/></h1>
</div>

<div class="well">
	<p><spring:message code="label.candidacies.well"/></p>
</div>

<div class="table-responsive">
	<table class="table">
		<thead>
			<tr>
				<th>
					<spring:message code='label.thesis.id' />
				</th>
				<th>
					<spring:message code='label.title' />
				</th>
				<th>
					<spring:message code='label.participants' />
				</th>
				<th>
				</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${thesisProposalsList}" var="thesisProposal">
			<tr>
				<td>${thesisProposal.identifier}</td>
				<td>${thesisProposal.title}</td>
				<td>
					<c:forEach items="${thesisProposal.thesisProposalParticipantSet}" var="participant">
					<div>
						${participant.user.name} <small>as</small> <b>${participant.thesisProposalParticipantType.name.content}</b>
					</div>
				</c:forEach>
			</td>
			<td>
				<div class="btn-group btn-group-xs">
					<form role="form" method="GET" action="${pageContext.request.contextPath}/thesisCandidacies/manage/${thesisProposal.externalId}" class="form-horizontal">
						<input type='button' class='detailsButton btn btn-default' data-observations="${thesisProposal.observations}" data-requirements="${thesisProposal.requirements}" data-goals="${thesisProposal.goals}" data-localization="${thesisProposal.localization}" data-degrees="${result}" value='<spring:message code="button.details"/>' data-thesis="${thesisProposal.externalId}">

						<button type="submit" class="btn btn-default"><spring:message code="label.candidacies.manage"/></button>
					</form>
					</div
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
</div>

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
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.observations'/></label>
					<div class="col-sm-10">
						<div class="information observations"></div>
					</div>
				</div>

				<div class="form-group">
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.requirements'/></label>
					<div class="col-sm-10">
						<div class="information requirements"></div>
					</div>
				</div>

				<div class="form-group">
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.goals'/></label>
					<div class="col-sm-10">
						<div class="information goals"></div>
					</div>
				</div>

				<div class="form-group">
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.localization'/></label>
					<div class="col-sm-10">
						<div class="information localization"></div>
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
		margin-left: 25px;
	}
</style>

<script type="text/javascript">
	jQuery(document).ready(function(){
		jQuery('.detailsButton').on('click', function(event) {
			$("#details" + $(this).data("thesis")).toggle('show');

		});
	});

	$(function(){
		$(".detailsButton").on("click", function(evt){
			var e = $(evt.target);

			['observations','requirements','goals','localization','degrees'].map(function(x){
				$("#view ." + x).html(e.data(x));
			});

			$('#view').modal('show');
		});
	})
</script>
