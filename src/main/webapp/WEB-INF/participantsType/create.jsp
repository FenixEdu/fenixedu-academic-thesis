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

<style type="text/css">
	.tDnD_whileDrag{
		background:#f3f3f3;
	}
</style>

<div class="page-header">
	<h1><spring:message code="title.thesisProposalsParticipantsType.management"/></h1>
</div>

<c:if test="${!empty deleteException}">
<p class="text-danger"><spring:message code="error.thesisProposal.participantType.delete.used"/></p>
</c:if>
<c:if test="${!empty editException}">
<p class="text-danger"><spring:message code="error.thesisProposal.participantType.edit.used"/></p>
</c:if>

<div class="well">
	<p>
	<spring:message code="label.dragAndDrop.hint"/>
	<spring:message code="label.dragAndDrop.increasing.hint"/>
	</p>
</div>

<p>
	<div class="row">
		<div class="col-sm-8">

<form:form role="form" method="POST" action="${pageContext.request.contextPath}/participantsType/updateWeights" class="form-horizontal">
	${csrf.field()}
	<button type="button"  class="btn btn-primary" data-toggle="modal" data-target="#create"><spring:message code="button.create"/></button>
<input type="hidden" name="json" id="json"/>
<button type="submit" class="btn btn-default" style="display:none;" id="saveButton"><spring:message code="button.order.save"/> </button>
</form:form>

		</div>
		<div class="col-sm-4">
</div>
</div>
</p>

<div class="table-responsive">
	<table class="table" id="table">
	<colgroup>
		<col></col>
		<col></col>
	</colgroup>
		<thead>
			<tr>
				<th><spring:message code='label.participantType.name'/></th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${participantTypeList}" var="participantType">
			<div class="sortable">
				<tr data-participantType-id="${participantType.externalId}">
					<td>${participantType.name.content}</td>
					<td>
						<c:if test="${participantType.thesisProposalParticipantSet.size() == 0}">
							<a class="btn btn-xs btn-default" href="/participantsType/edit/${participantType.externalId}"><spring:message code='button.edit'/></a>
						</c:if>
						<c:if test="${participantType.thesisProposalParticipantSet.size() > 0}">
							<button class="btn disabled btn-disabled btn-xs btn-default" data-toggle="tooltip" data-placement="right" title="<spring:message code="error.thesisProposal.participantType.edit.used"/>"><spring:message code='button.edit'/></button>
						</c:if>
				</tr>
			</div>
		</c:forEach>
	</tbody>
</table>
</div>

<!-- Modal -->
<div class="modal fade" id="create" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
<form role="form" method="POST" action="/participantsType/" class="form-horizontal" commandname="participantTypeBean" id="participantTypeBean">
	${csrf.field()}
	<div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
			<button type="button" class="close"><span class="sr-only"><spring:message code="button.close"/></span></button>
			<h3 class="modal-title"><spring:message code="label.create"/></h3>
			<small class="explanation"><spring:message code="label.participantType.modal.create.explanation"/></small>
		</div>
      <div class="modal-body">
			<spring:message code='label.participantType.name' var="participantTypeName"/>
			<div class="form-group">
				<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.participantType.name'/></label>
				<div class="col-sm-10">
					<input  type="text" class="form-control" name="name" id="local" path="local" required="required" bennu-localized-string/>
				</div>
			</div>
			<div class="form-group">
				<div class="col-sm-10">
					<input type="hidden" class="form-control" name="weight" id="Weight" placeholder="Weight" path="weight" required="required" value="${participantTypeList.size() + 1}"/>
				</div>
			</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button type="submit" class="btn btn-primary"><spring:message code="button.create"/></button>
      </div>
    </div>
  </div>
  </form>
</div>

<script type="text/javascript">
	$(document).ready(function() {
		$("#table").tableDnD({
			onDrop:function(){
				$("#saveButton").show();
			}
		});
	});
</script>

<script type="text/javascript">
	$("#saveButton").on("click", function(e) {
		var rows = $("#table").find("tbody").find("tr")
		participantTypesJSON = {
			participantTypes: []
		}
		for (index=0; index < rows.length; index++) {
			var row = rows.eq(index)
			var externalId = row.data("participanttype-id")
			participantTypesJSON.participantTypes.push({
				"externalId" : externalId,
				"weight" : (rows.length - index)
			});
		}
		$("#json").val(JSON.stringify(participantTypesJSON.participantTypes));
	});
</script>
