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
package org.fenixedu.academic.thesis.ui.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.thesis.domain.StudentThesisCandidacy;
import org.fenixedu.academic.thesis.domain.ThesisProposal;
import org.fenixedu.academic.thesis.domain.ThesisProposalsConfiguration;
import org.fenixedu.academic.thesis.domain.exception.MaxNumberStudentThesisCandidaciesException;
import org.fenixedu.academic.thesis.domain.exception.OutOfCandidacyPeriodException;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@SpringFunctionality(app = ThesisProposalsController.class, title = "title.studentThesisCandidacy.management",
        accessGroup = "activeStudents")
@RequestMapping("/studentCandidacies")
public class StudentCandidaciesController {

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String listProposals(Model model) {

        Student student = Authenticate.getUser().getPerson().getStudent();

        List<StudentThesisCandidacy> candidacies =
                student.getActiveRegistrations()
                        .stream()
                        .flatMap((Registration registration) -> registration.getStudentThesisCandidacySet().stream())
                        .filter(candidacy -> candidacy.getThesisProposal().getSingleThesisProposalsConfiguration()
                                .getCandidacyPeriod().containsNow()).collect(Collectors.toList());

        Collections.sort(candidacies, StudentThesisCandidacy.COMPARATOR_BY_PREFERENCE_NUMBER);

        Set<ThesisProposal> thesisProposalCandidacies = new HashSet<ThesisProposal>();
        for (StudentThesisCandidacy candidacy : candidacies) {
            thesisProposalCandidacies.add(candidacy.getThesisProposal());
        }

        HashMap<Registration, Set<ThesisProposal>> proposals = new HashMap<Registration, Set<ThesisProposal>>();

        Set<ThesisProposalsConfiguration> suggestedConfigs = new HashSet<ThesisProposalsConfiguration>();

        student.getActiveRegistrations().forEach(
                reg -> {
                    Set<ThesisProposalsConfiguration> regConfigs =
                            reg.getDegree()
                                    .getExecutionDegrees()
                                    .stream()
                                    .filter((ExecutionDegree execDegree) -> execDegree.getExecutionYear().isAfterOrEquals(
                                            ExecutionYear.readCurrentExecutionYear()))
                                    .flatMap(
                                            (ExecutionDegree execDegree) -> execDegree.getThesisProposalsConfigurationSet()
                                                    .stream()).collect(Collectors.toSet());

                    Set<ThesisProposalsConfiguration> openConfigs =
                            regConfigs.stream().filter(config -> config.getCandidacyPeriod().containsNow())
                                    .collect(Collectors.toSet());

                    if (openConfigs.isEmpty()) {
                        Optional<ThesisProposalsConfiguration> nextConfig =
                                regConfigs.stream().min(ThesisProposalsConfiguration.COMPARATOR_BY_PROPOSAL_PERIOD_START_ASC);
                        if (nextConfig.isPresent()) {
                            suggestedConfigs.add(nextConfig.get());
                        }
                    } else {
                        suggestedConfigs.addAll(openConfigs);
                    }
                    proposals.put(
                            reg,
                            openConfigs
                                    .stream()
                                    .flatMap((ThesisProposalsConfiguration config) -> config.getThesisProposalSet().stream())
                                    .filter((ThesisProposal proposal) -> !thesisProposalCandidacies.contains(proposal)
                                            && !proposal.getHidden()).collect(Collectors.toSet()));
                });

        int size = 0;
        for (Registration reg : proposals.keySet()) {
            size += proposals.get(reg).size();
        }

        model.addAttribute("availableProposals", size > 0);
        model.addAttribute("proposals", proposals);
        model.addAttribute("studentThesisCandidacies", candidacies);
        model.addAttribute("suggestedConfigs", suggestedConfigs);

        return "studentCandidacies/list";
    }

    @RequestMapping(value = "/candidate/{oid}", method = RequestMethod.POST)
    public String createThesisCandidacyForm(@PathVariable("oid") ThesisProposal thesisProposal,
            @RequestParam Registration registration, Model model) throws MaxNumberStudentThesisCandidaciesException,
            OutOfCandidacyPeriodException {

        try {
            createStudentThesisCandidacy(registration, thesisProposal);
        } catch (MaxNumberStudentThesisCandidaciesException exception) {
            model.addAttribute("maxNumberStudentThesisCandidaciesException", exception);
            return listProposals(model);
        } catch (OutOfCandidacyPeriodException exception) {
            model.addAttribute("outOfCandidacyPeriodException", exception);
            return listProposals(model);
        } catch (NullPointerException exception) {
            model.addAttribute("nullPointerException", exception);
            return listProposals(model);
        }

        return "redirect:/studentCandidacies";
    }

    @Atomic(mode = TxMode.WRITE)
    public void createStudentThesisCandidacy(Registration registration, ThesisProposal thesisProposal)
            throws MaxNumberStudentThesisCandidaciesException, OutOfCandidacyPeriodException {

        ThesisProposalsConfiguration thesisProposalsConfiguration = thesisProposal.getSingleThesisProposalsConfiguration();

        if (!thesisProposalsConfiguration.getCandidacyPeriod().containsNow()) {
            throw new OutOfCandidacyPeriodException();
        } else {

            long candidaciesCount =
                    registration
                            .getStudentThesisCandidacySet()
                            .stream()
                            .filter(candidacy -> candidacy.getThesisProposal().getSingleThesisProposalsConfiguration()
                                    .getCandidacyPeriod().containsNow()).count();

            if (thesisProposalsConfiguration.getMaxThesisCandidaciesByStudent() != -1
                    && candidaciesCount >= thesisProposalsConfiguration.getMaxThesisCandidaciesByStudent()) {
                throw new MaxNumberStudentThesisCandidaciesException(registration.getStudent());
            } else {
                StudentThesisCandidacy studentThesisCandidacy =
                        new StudentThesisCandidacy(registration, (int) candidaciesCount, thesisProposal);
                registration.getStudentThesisCandidacySet().add(studentThesisCandidacy);
            }
        }

    }

    @RequestMapping(value = "/delete/{oid}", method = RequestMethod.GET)
    public String deleteThesisCandidacy(@PathVariable("oid") StudentThesisCandidacy studentThesisCandidacy, Model model) {

        return delete(studentThesisCandidacy, model);
    }

    @Atomic(mode = TxMode.WRITE)
    private String delete(StudentThesisCandidacy studentThesisCandidacy, Model model) {
        try {
            studentThesisCandidacy.delete();
        } catch (DomainException domainException) {
            model.addAttribute("deleteException", true);
            return listProposals(model);
        }

        return "redirect:/studentCandidacies";
    }

    @RequestMapping(value = "/updatePreferences", method = RequestMethod.POST)
    public String updateStudentThesisCandidaciesWeights(@RequestParam String json) {

        JsonParser parser = new JsonParser();
        JsonArray jsonArray = (JsonArray) parser.parse(json);

        updateStudentThesisCandidaciesWeights(jsonArray);

        return "redirect:/studentCandidacies";
    }

    @Atomic(mode = TxMode.WRITE)
    public void updateStudentThesisCandidaciesWeights(JsonArray jsonArray) {
        jsonArray.forEach((JsonElement elem) -> {
            String externalId = elem.getAsJsonObject().get("externalId").getAsString();
            int preference = elem.getAsJsonObject().get("preference").getAsInt();

            StudentThesisCandidacy studentThesisCandidacy = FenixFramework.getDomainObject(externalId);
            studentThesisCandidacy.setPreferenceNumber(preference);
        });
    }
}
