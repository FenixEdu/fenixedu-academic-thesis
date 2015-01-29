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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.thesis.domain.StudentThesisCandidacy;
import org.fenixedu.academic.thesis.domain.ThesisProposal;
import org.fenixedu.academic.thesis.domain.ThesisProposalsConfiguration;
import org.fenixedu.academic.thesis.ui.exception.MaxNumberStudentThesisCandidaciesException;
import org.fenixedu.academic.thesis.ui.exception.OutOfCandidacyPeriodException;
import org.fenixedu.academic.thesis.ui.exception.ThesisProposalException;
import org.fenixedu.academic.thesis.ui.service.StudentCandidaciesService;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

@SpringFunctionality(app = ThesisProposalsController.class, title = "title.studentThesisCandidacy.management",
        accessGroup = "activeStudents")
@RequestMapping("/studentCandidacies")
public class StudentCandidaciesController {

    @Autowired
    StudentCandidaciesService service;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String listProposals(Model model) {

        Student student = Authenticate.getUser().getPerson().getStudent();

        Set<ThesisProposalsConfiguration> suggestedConfigs = service.getSuggestedConfigs(student);

        HashMap<Registration, Set<ThesisProposal>> proposalsByReg = service.getOpenProposalsByReg(student);

        Map<ThesisProposalsConfiguration, List<StudentThesisCandidacy>> candidaciesByConfig =
                service.getCandidaciesByConfig(student);

        int proposalsSize = proposalsByReg.values().stream().map(Set::size).reduce(0, (a, b) -> a + b);
        int candidaciesSize = candidaciesByConfig.values().stream().map(List::size).reduce(0, (a, b) -> a + b);

        model.addAttribute("suggestedConfigs", suggestedConfigs);
        model.addAttribute("proposalsSize", proposalsSize);
        model.addAttribute("candidaciesSize", candidaciesSize);
        model.addAttribute("candidaciesByConfig", candidaciesByConfig);
        model.addAttribute("proposalsByReg", proposalsByReg);

        return "studentCandidacies/list";
    }

    @RequestMapping(value = "/candidate/{oid}", method = RequestMethod.POST)
    public String createThesisCandidacyForm(@PathVariable("oid") ThesisProposal thesisProposal,
            @RequestParam Registration registration, Model model) throws MaxNumberStudentThesisCandidaciesException,
            OutOfCandidacyPeriodException {

        try {
            service.createStudentThesisCandidacy(registration, thesisProposal);
        } catch (Exception exception) {
            model.addAttribute("error", exception);
            return listProposals(model);
        }

        return "redirect:/studentCandidacies";
    }

    @RequestMapping(value = "/delete/{oid}", method = RequestMethod.GET)
    public String deleteThesisCandidacy(@PathVariable("oid") StudentThesisCandidacy studentThesisCandidacy, Model model) {

        boolean result = service.delete(studentThesisCandidacy, model);

        if (result) {
            return "redirect:/studentCandidacies";
        } else {
            return listProposals(model);
        }
    }

    @RequestMapping(value = "/updatePreferences", method = RequestMethod.POST)
    public String updateStudentThesisCandidaciesWeights(@RequestParam String json, Model model) {

        JsonParser parser = new JsonParser();
        JsonArray jsonArray = (JsonArray) parser.parse(json);

        try {
            service.updateStudentThesisCandidaciesWeights(jsonArray);
        } catch (ThesisProposalException e) {
            model.addAttribute("error", e.getClass().getSimpleName());
        }

        return listProposals(model);
    }

}
