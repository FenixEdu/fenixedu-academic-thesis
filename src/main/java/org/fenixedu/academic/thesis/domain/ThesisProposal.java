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
package org.fenixedu.academic.thesis.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.i18n.BundleUtil;

public class ThesisProposal extends ThesisProposal_Base {

    public final static Comparator<ThesisProposal> COMPARATOR_BY_NUMBER_OF_CANDIDACIES = new Comparator<ThesisProposal>() {

        @Override
        public int compare(ThesisProposal arg0, ThesisProposal arg1) {
            if (arg1.getStudentThesisCandidacySet().size() < arg0.getStudentThesisCandidacySet().size()) {
                return -1;
            } else {
                return 1;
            }
        }
    };

    public final static Comparator<ThesisProposal> COMPARATOR_BY_NUMBER_OF_CANDIDACIES_AND_ID = new Comparator<ThesisProposal>() {

        @Override
        public int compare(ThesisProposal arg0, ThesisProposal arg1) {
            if (arg1.getStudentThesisCandidacySet().size() < arg0.getStudentThesisCandidacySet().size()) {
                return -1;
            } else {
                if (arg1.getStudentThesisCandidacySet().size() > arg0.getStudentThesisCandidacySet().size()) {
                    return 1;
                } else {
                    return arg0.getIdentifier().compareTo(arg1.getIdentifier());
                }
            }
        }
    };

    public final static Comparator<ThesisProposal> COMPARATOR_BY_PROPOSAL_PERIOD = new Comparator<ThesisProposal>() {

        @Override
        public int compare(ThesisProposal arg0, ThesisProposal arg1) {

            if (arg0.getSingleThesisProposalsConfiguration().getProposalPeriod()
                    .isBefore(arg1.getSingleThesisProposalsConfiguration().getProposalPeriod())) {
                return -1;
            } else {
                return 1;
            }
        }
    };

    public static final String SIGNAL_CREATED = "fenixedu.academic.thesis.thesisProposal.created";

    @Override
    public Set<StudentThesisCandidacy> getStudentThesisCandidacySet() {
        return super.getStudentThesisCandidacySet();
    }

    public ThesisProposalsConfiguration getSingleThesisProposalsConfiguration() {
        return (ThesisProposalsConfiguration) getThesisConfigurationSet().toArray()[0];
    }

    @Deprecated
    public ThesisProposal(String title, String observations, String requirements, String goals, String localization,
            List<ThesisProposalParticipant> participants, Set<ThesisProposalsConfiguration> configurations) {

        this(title, observations, requirements, goals, localization, new HashSet<ThesisProposalParticipant>(participants),
                configurations);
    }

    public ThesisProposal(String title, String observations, String requirements, String goals, String localization,
            Set<ThesisProposalParticipant> participants, Set<ThesisProposalsConfiguration> configurations) {

        setThesisProposalsSystem(ThesisProposalsSystem.getInstance());
        setIdentifier(ThesisProposalsSystem.getInstance().generateProposalIdentifier());
        setTitle(title);
        setObservations(observations);
        setRequirements(requirements);
        setGoals(goals);
        setLocalization(localization);
        getThesisProposalParticipantSet().addAll(participants);
        getThesisConfigurationSet().addAll(configurations);
        setHidden(false);
    }

    public int getNumberOfStudentCandidacies() {
        return getStudentThesisCandidacySet().size();
    }

    public void delete() {

        DomainException.throwWhenDeleteBlocked(getDeletionBlockers());

        this.getThesisConfigurationSet().clear();
        this.getStudentThesisCandidacySet().clear();

        for (ThesisProposalParticipant thesisProposalParticipant : getThesisProposalParticipantSet()) {
            thesisProposalParticipant.delete();
        }

        this.getThesisProposalParticipantSet().clear();

        this.setThesisProposalsSystem(null);

        deleteDomainObject();
    }

    @Override
    protected void checkForDeletionBlockers(Collection<String> blockers) {
        super.checkForDeletionBlockers(blockers);

        if (!getStudentThesisCandidacySet().isEmpty()
                || !getSingleThesisProposalsConfiguration().getProposalPeriod().containsNow()) {
            blockers.add(BundleUtil.getString("resources.ThesisProposalsResources", "error.proposals.cant.delete"));
        }
    }

    public List<ThesisProposalParticipant> getSortedParticipants() {

        List<ThesisProposalParticipant> sortedParticipants =
                this.getThesisProposalParticipantSet().stream().collect(Collectors.toList());
        Collections.sort(sortedParticipants, ThesisProposalParticipant.COMPARATOR_BY_WEIGHT);

        return sortedParticipants;
    }

    public static Set<ThesisProposal> readCurrentByParticipant(User user) {

        return user
                .getThesisProposalParticipantSet()
                .stream()
                .map(participant -> participant.getThesisProposal())
                .filter(proposal -> proposal
                        .getThesisConfigurationSet()
                        .stream()
                        .anyMatch(
                                configuration -> configuration.getExecutionDegree().getExecutionYear()
                                .isAfterOrEquals(ExecutionYear.readCurrentExecutionYear()))).collect(Collectors.toSet());
    }

    public Set<ExecutionDegree> getExecutionDegreeSet() {
        return this.getThesisConfigurationSet().stream().map(config -> config.getExecutionDegree()).collect(Collectors.toSet());
    }

    @Override
    public String getIdentifier() {
        return super.getIdentifier();
    }

    public ThesisProposal(ThesisProposal proposal, Set<ThesisProposalsConfiguration> configs) {

        new ThesisProposal(proposal.getTitle(), proposal.getObservations(), proposal.getRequirements(), proposal.getGoals(),
                proposal.getLocalization(), proposal.getThesisProposalParticipantSet(), configs);
    }

    @Override
    public boolean getHidden() {
        if (super.getHidden()) {
            return true;
        } else {
            return false;
        }
    }

}
