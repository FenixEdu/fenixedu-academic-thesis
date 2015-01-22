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
package org.fenixedu.academic.thesis.ui.bean;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.thesis.domain.StudentThesisCandidacy;
import org.fenixedu.academic.thesis.domain.ThesisProposal;
import org.fenixedu.academic.thesis.domain.ThesisProposalParticipant;
import org.fenixedu.academic.thesis.domain.ThesisProposalParticipantType;
import org.fenixedu.academic.thesis.domain.ThesisProposalsConfiguration;
import org.fenixedu.academic.thesis.domain.exception.MaxNumberThesisProposalsException;
import org.fenixedu.academic.thesis.domain.exception.OutOfProposalPeriodException;
import org.fenixedu.bennu.core.domain.User;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

public class ThesisProposalBean {

    private String title;
    private String observations;
    private String requirements;
    private String goals;
    private String localization;
    private Set<ThesisProposalsConfiguration> thesisProposalsConfigurations;
    private Set<StudentThesisCandidacy> studentThesisCandidacy;
    private Set<ThesisProposalParticipantBean> thesisProposalParticipantsBean;
    private String externalId;
    private boolean hidden;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public String getLocalization() {
        return localization;
    }

    public void setLocalization(String localization) {
        this.localization = localization;
    }

    public Set<ThesisProposalsConfiguration> getThesisProposalsConfigurations() {
        return thesisProposalsConfigurations;
    }

    public void setThesisProposalsConfigurations(Set<ThesisProposalsConfiguration> thesisProposalsConfigurations) {
        this.thesisProposalsConfigurations = thesisProposalsConfigurations;
    }

    public Set<StudentThesisCandidacy> getStudentThesisCandidacy() {
        return studentThesisCandidacy;
    }

    public void setStudentThesisCandidacy(Set<StudentThesisCandidacy> studentThesisCandidacy) {
        this.studentThesisCandidacy = studentThesisCandidacy;
    }

    public Set<ThesisProposalParticipantBean> getThesisProposalParticipantsBean() {
        return thesisProposalParticipantsBean;
    }

    public void setThesisProposalParticipantsBean(Set<ThesisProposalParticipantBean> thesisProposalParticipantsBean) {
        this.thesisProposalParticipantsBean = thesisProposalParticipantsBean;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public ThesisProposalBean(String title, String observations, String requirements, String goals, String localization,
            Set<ThesisProposalsConfiguration> configurations, Set<StudentThesisCandidacy> studentThesisCandidacy,
            Set<ThesisProposalParticipantBean> thesisProposalParticipantsBean) {
        this.title = title;
        this.observations = observations;
        this.requirements = requirements;
        this.goals = goals;
        this.localization = localization;
        this.setThesisProposalsConfigurations(configurations);
        this.studentThesisCandidacy = studentThesisCandidacy;
        this.thesisProposalParticipantsBean = thesisProposalParticipantsBean;
    }

    public ThesisProposalBean(String title, String observations, String requirements, String goals, String localization,
            Set<ThesisProposalsConfiguration> configurations, Set<StudentThesisCandidacy> studentThesisCandidacy,
            Set<ThesisProposalParticipantBean> thesisProposalParticipantsBean, boolean hidden, String externalId) {
        this.title = title;
        this.observations = observations;
        this.requirements = requirements;
        this.goals = goals;
        this.localization = localization;
        this.setThesisProposalsConfigurations(configurations);
        this.studentThesisCandidacy = studentThesisCandidacy;
        this.thesisProposalParticipantsBean = thesisProposalParticipantsBean;
        this.hidden = true == hidden ? true : false;
        this.externalId = externalId;
    }

    public ThesisProposalBean() {
    }

    public static class Builder {

        private final String title;
        private final String observations;
        private final String requirements;
        private final String goals;
        private final String localization;
        private final Set<ThesisProposalsConfiguration> configurations;
        private final Set<ThesisProposalParticipantBean> thesisProposalParticipantsBean;

        public Builder(ThesisProposalBean proposalBean) {
            this.title = proposalBean.getTitle();
            this.observations = proposalBean.getObservations();
            this.requirements = proposalBean.getRequirements();
            this.goals = proposalBean.getGoals();
            this.localization = proposalBean.getLocalization();
            this.configurations = proposalBean.getThesisProposalsConfigurations();
            this.thesisProposalParticipantsBean = proposalBean.getThesisProposalParticipantsBean();
        }

        @Atomic(mode = TxMode.WRITE)
        public ThesisProposal build() throws MaxNumberThesisProposalsException, OutOfProposalPeriodException {
            Set<ThesisProposalParticipant> participants = new HashSet<ThesisProposalParticipant>();

            for (ThesisProposalParticipantBean participantBean : thesisProposalParticipantsBean) {

                ThesisProposalParticipantType participantType =
                        FenixFramework.getDomainObject(participantBean.getParticipantTypeExternalId());

                User username = FenixFramework.getDomainObject(participantBean.getUserExternalId());
                ThesisProposalParticipant participant = new ThesisProposalParticipant(username, participantType);

                participants.add(participant);
            }

            ThesisProposalsConfiguration baseConfig = configurations.iterator().next();

            if (!baseConfig.getProposalPeriod().containsNow()) {
                throw new OutOfProposalPeriodException();
            }

            for (ThesisProposalParticipant participant : participants) {
                for (ThesisProposalsConfiguration configuration : configurations) {
                    int proposalsCount =
                            configuration
                                    .getThesisProposalSet()
                                    .stream()
                                    .filter(proposal -> proposal.getThesisProposalParticipantSet().stream().map(p -> p.getUser())
                                            .collect(Collectors.toSet()).contains(participant.getUser()))
                                    .collect(Collectors.toSet()).size();

                    if (configuration.getMaxThesisProposalsByUser() != -1
                            && proposalsCount >= configuration.getMaxThesisProposalsByUser()) {
                        throw new MaxNumberThesisProposalsException(participant);
                    }
                }
            }

            return new ThesisProposal(title, observations, requirements, goals, localization, participants, configurations);
        }
    }

    public Set<ExecutionDegree> getExecutionDegreeSet() {
        return this.getThesisProposalsConfigurations().stream().map(config -> config.getExecutionDegree())
                .collect(Collectors.toSet());
    }

    public boolean getHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

}
