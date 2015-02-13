/**
 * Copyright © 2014 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Academic Thesis.
 *
 * FenixEdu Academic Thesis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Academic Thesis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Academic Thesis.  If not, see <http://www.gnu.org/licenses/>.
 */
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

    public int getproposalsNotAcceptedWithCandidaciesNumber() {
        return service.getCoordinatorProposals(configuration, null, false, true).size();
    }

}
