package org.fenixedu.academic.thesis.ui.bean;

import org.fenixedu.academic.thesis.domain.ThesisProposalsConfiguration;
import org.fenixedu.academic.thesis.ui.service.ThesisProposalsService;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class AdminProposalsSummaryBean {

    private final ThesisProposalsConfiguration configuration;

    private final ThesisProposalsService service;

    public AdminProposalsSummaryBean(ThesisProposalsService service, ThesisProposalsConfiguration configuration) {
        this.configuration = configuration;
        this.service = service;
    }

    public int getTotalOfProposals() {
        return service.getCoordinatorProposals(configuration).size();
    }

    public int getVisibleProposalsNumber() {
        return service.getCoordinatorProposals(configuration, true, null, null).size();
    }

    public int getHiddenProposalsNumber() {
        return service.getCoordinatorProposals(configuration, false, null, null).size();
    }

    public int getProposalsWithCandadaciesNumber() {
        return service.getCoordinatorProposals(configuration, null, null, true).size();
    }

    public int getProposalsWithoutCandidaciesNumber() {
        return service.getCoordinatorProposals(configuration, null, null, false).size();
    }

    public int getProposalsWithAcceptedCandidaciesNumber() {
        return service.getCoordinatorProposals(configuration, null, true, null).size();
    }

}
