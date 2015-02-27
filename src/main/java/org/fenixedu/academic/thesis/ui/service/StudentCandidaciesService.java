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
package org.fenixedu.academic.thesis.ui.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.thesis.domain.StudentThesisCandidacy;
import org.fenixedu.academic.thesis.domain.ThesisProposal;
import org.fenixedu.academic.thesis.domain.ThesisProposalsConfiguration;
import org.fenixedu.academic.thesis.ui.exception.MaxNumberStudentThesisCandidaciesException;
import org.fenixedu.academic.thesis.ui.exception.OutOfCandidacyPeriodException;
import org.fenixedu.academic.thesis.ui.exception.ThesisProposalsDomainException;
import org.fenixedu.academic.thesis.ui.exception.Unsuficient1stCycleCreditsException;
import org.fenixedu.academic.thesis.ui.exception.Unsuficient2ndCycleCreditsException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Service
public class StudentCandidaciesService {

    @Atomic(mode = TxMode.WRITE)
    public void updateStudentThesisCandidaciesWeights(JsonArray jsonArray) throws OutOfCandidacyPeriodException {
        for (JsonElement elem : jsonArray) {

            String externalId = elem.getAsJsonObject().get("externalId").getAsString();
            int preference = elem.getAsJsonObject().get("preference").getAsInt();

            StudentThesisCandidacy studentThesisCandidacy = FenixFramework.getDomainObject(externalId);

            if (studentThesisCandidacy.getThesisProposal().getSingleThesisProposalsConfiguration().getCandidacyPeriod()
                    .containsNow()) {
                studentThesisCandidacy.setPreferenceNumber(preference);
            } else {
                throw new OutOfCandidacyPeriodException();
            }
        }
    }

    @Atomic(mode = TxMode.WRITE)
    public boolean delete(StudentThesisCandidacy studentThesisCandidacy, Model model) {
        try {
            studentThesisCandidacy.delete();
        } catch (ThesisProposalsDomainException domainException) {
            model.addAttribute("domainException", domainException.getLocalizedMessage());
            return false;
        }

        return true;
    }

    @Atomic(mode = TxMode.WRITE)
    public void createStudentThesisCandidacy(Registration registration, ThesisProposal thesisProposal)
            throws MaxNumberStudentThesisCandidaciesException, OutOfCandidacyPeriodException,
            Unsuficient1stCycleCreditsException, Unsuficient2ndCycleCreditsException {

        ThesisProposalsConfiguration thesisProposalsConfiguration = thesisProposal.getSingleThesisProposalsConfiguration();

        StudentCurricularPlan studentCurricularPlan = registration.getActiveStudentCurricularPlan();

        if (studentCurricularPlan.getFirstCycle() != null
                && thesisProposalsConfiguration.getMinECTS1stCycle() > studentCurricularPlan.getFirstCycle()
                        .getAprovedEctsCredits()) {
            throw new Unsuficient1stCycleCreditsException();
        }

        if (studentCurricularPlan.getSecondCycle() == null
                || thesisProposalsConfiguration.getMinECTS2ndCycle() > studentCurricularPlan.getSecondCycle()
                        .getAprovedEctsCredits()) {
            throw new Unsuficient2ndCycleCreditsException();
        }

        if (!thesisProposalsConfiguration.getCandidacyPeriod().containsNow()) {
            throw new OutOfCandidacyPeriodException();
        }

        long candidaciesCount =
                registration
                        .getStudentThesisCandidacySet()
                        .stream()
                        .filter(candidacy -> candidacy.getThesisProposal().getSingleThesisProposalsConfiguration()
                                .getCandidacyPeriod().containsNow()).count();

        if (thesisProposalsConfiguration.getMaxThesisCandidaciesByStudent() != -1
                && candidaciesCount >= thesisProposalsConfiguration.getMaxThesisCandidaciesByStudent()) {
            throw new MaxNumberStudentThesisCandidaciesException(registration.getStudent());
        }

        StudentThesisCandidacy studentThesisCandidacy =
                new StudentThesisCandidacy(registration, (int) candidaciesCount + 1, thesisProposal);
        registration.getStudentThesisCandidacySet().add(studentThesisCandidacy);

    }

    public Set<ThesisProposalsConfiguration> getConfigurationsForRegistration(Registration reg) {
        return reg.getAllCurriculumGroups().stream().map(group -> group.getDegreeCurricularPlanOfDegreeModule())
                .filter(Objects::nonNull).map(dcp -> dcp.getDegree()).flatMap(degree -> degree.getExecutionDegrees().stream())
                .flatMap((ExecutionDegree execDegree) -> execDegree.getThesisProposalsConfigurationSet().stream())
                .collect(Collectors.toSet());
    }

    public Set<ThesisProposalsConfiguration> getStudentConfigurations(Student student) {
        return student.getActiveRegistrations().stream().flatMap(reg -> getConfigurationsForRegistration(reg).stream())
                .collect(Collectors.toSet());
    }

    public List<ThesisProposalsConfiguration> getStudentOpenConfigurations(Student student) {
        return getStudentConfigurations(student).stream().filter(config -> config.getCandidacyPeriod().containsNow())
                .collect(Collectors.toList());
    }

    public Map<ThesisProposalsConfiguration, List<StudentThesisCandidacy>> getCandidaciesByConfig(Student student) {

        Set<ThesisProposalsConfiguration> configs = getStudentConfigurations(student);

        HashMap<ThesisProposalsConfiguration, List<StudentThesisCandidacy>> candidaciesByConfig =
                new HashMap<ThesisProposalsConfiguration, List<StudentThesisCandidacy>>();

        configs.forEach(config -> {
            List<StudentThesisCandidacy> studentCandidacies =
                    config.getThesisProposalSet().stream().flatMap(proposal -> proposal.getStudentThesisCandidacySet().stream())
                            .filter(candidacy -> candidacy.getRegistration().getStudent().equals(student)).distinct()
                            .sorted(StudentThesisCandidacy.COMPARATOR_BY_PREFERENCE_NUMBER).collect(Collectors.toList());
            candidaciesByConfig.put(config, studentCandidacies);
        });

        return candidaciesByConfig;
    }

    public HashMap<Registration, Set<ThesisProposal>> getOpenProposalsByReg(Student student) {
        HashMap<Registration, Set<ThesisProposal>> proposalsByReg = new HashMap<Registration, Set<ThesisProposal>>();

        student.getActiveRegistrations().forEach(
                reg -> {
                    Set<ThesisProposal> openProposals =
                            getConfigurationsForRegistration(reg)
                                    .stream()
                                    .filter(config -> config.getCandidacyPeriod().containsNow())
                                    .flatMap(config -> config.getThesisProposalSet().stream())
                                    .filter(proposal -> !proposal.getHidden())
                                    .filter(proposal -> !(proposal.getStudentThesisCandidacySet().stream()
                                            .map(candidacy -> candidacy.getRegistration().getStudent()).anyMatch(st -> st
                                            .equals(student)))).collect(Collectors.toSet());
                    proposalsByReg.put(reg, openProposals);
                });

        return proposalsByReg;
    }

    public Set<ThesisProposalsConfiguration> getSuggestedConfigs(Student student) {

        Set<ThesisProposalsConfiguration> suggestedConfigs = new HashSet<ThesisProposalsConfiguration>();
        student.getActiveRegistrations().forEach(
                reg -> {
                    Set<ThesisProposalsConfiguration> regConfigs = getConfigurationsForRegistration(reg);

                    Set<ThesisProposalsConfiguration> openRegConfigs =
                            regConfigs.stream().filter(config -> config.getCandidacyPeriod().containsNow())
                                    .collect(Collectors.toSet());

                    if (openRegConfigs.isEmpty()) {
                        Optional<ThesisProposalsConfiguration> nextConfig =
                                regConfigs.stream().max(ThesisProposalsConfiguration.COMPARATOR_BY_PROPOSAL_PERIOD_START_ASC);
                        if (nextConfig.isPresent()) {
                            suggestedConfigs.add(nextConfig.get());
                        }
                    } else {
                        suggestedConfigs.addAll(openRegConfigs);
                    }
                });

        return suggestedConfigs;
    }

}
