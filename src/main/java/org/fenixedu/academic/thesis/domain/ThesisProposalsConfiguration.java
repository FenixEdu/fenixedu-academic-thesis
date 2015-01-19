/**
 * Copyright © ${project.inceptionYear} Instituto Superior Técnico
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
package org.fenixedu.academic.thesis.domain;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.joda.time.Interval;

public class ThesisProposalsConfiguration extends ThesisProposalsConfiguration_Base {

    static final public Comparator<ThesisProposalsConfiguration> COMPARATOR_BY_YEAR_AND_EXECUTION_DEGREE =
            new Comparator<ThesisProposalsConfiguration>() {
                @Override
                public int compare(ThesisProposalsConfiguration o1, ThesisProposalsConfiguration o2) {

                    int yearComp =
                            o2.getExecutionDegree().getExecutionYear().compareTo(o1.getExecutionDegree().getExecutionYear());

                    return yearComp != 0 ? yearComp : o2.getExecutionDegree().getPresentationName()
                            .compareTo(o1.getExecutionDegree().getPresentationName());
                }
            };

    static final public Comparator<ThesisProposalsConfiguration> COMPARATOR_BY_PROPOSAL_PERIOD_START_ASC =
            new Comparator<ThesisProposalsConfiguration>() {
                @Override
                public int compare(ThesisProposalsConfiguration o1, ThesisProposalsConfiguration o2) {
                    return o1.getProposalPeriod().getStart().compareTo(o2.getProposalPeriod().getStart());
                }
            };

    static final public Comparator<ThesisProposalsConfiguration> COMPARATOR_BY_CANDIDACY_PERIOD_START_ASC =
            new Comparator<ThesisProposalsConfiguration>() {
                @Override
                public int compare(ThesisProposalsConfiguration o1, ThesisProposalsConfiguration o2) {
                    return o1.getCandidacyPeriod().getStart().compareTo(o2.getCandidacyPeriod().getStart());
                }
            };

    public ThesisProposalsConfiguration(Interval proposalPeriod, Interval candidacyPeriod, ExecutionDegree executionDegree,
            int maxThesisCandidaciesByStudent, int maxThesisProposalsByUser) {
        super();
        setProposalPeriod(proposalPeriod);
        setCandidacyPeriod(candidacyPeriod);
        setExecutionDegree(executionDegree);
        setThesisProposalsSystem(ThesisProposalsSystem.getInstance());
        setMaxThesisCandidaciesByStudent(maxThesisCandidaciesByStudent);
        setMaxThesisProposalsByUser(maxThesisProposalsByUser);
    }

    public ThesisProposalsConfiguration() {
        super();
    }

    public void delete() {

        DomainException.throwWhenDeleteBlocked(getDeletionBlockers());

        this.setExecutionDegree(null);
        this.setThesisProposalsSystem(null);

        deleteDomainObject();
    }

    @Override
    protected void checkForDeletionBlockers(Collection<String> blockers) {
        super.checkForDeletionBlockers(blockers);
        if (getExecutionDegree() != null && !getThesisProposalSet().isEmpty()) {
            blockers.add(BundleUtil.getString("resources.ThesisProposalsResources", "error.configurations.cant.delete"));
        }
    }

    public boolean isEquivalent(ThesisProposalsConfiguration configuration) {
        return getMaxThesisCandidaciesByStudent() == configuration.getMaxThesisCandidaciesByStudent()
                && getMaxThesisProposalsByUser() == configuration.getMaxThesisProposalsByUser()
                && getProposalPeriod().isEqual(configuration.getProposalPeriod())
                && getCandidacyPeriod().isEqual(configuration.getCandidacyPeriod());
    }

    @Override
    public String toString() {
        return getProposalPeriod() + " , " + getCandidacyPeriod() + " w/ " + getMaxThesisProposalsByUser() + " , "
                + getMaxThesisProposalsByUser();

    }

    public static Set<ThesisProposalsConfiguration> getConfigurationsWithOpenProposalPeriod(ExecutionDegree executionDegree) {
        return executionDegree.getThesisProposalsConfigurationSet().stream()
                .filter(config -> config.getProposalPeriod().containsNow()).collect(Collectors.toSet());
    }
}
