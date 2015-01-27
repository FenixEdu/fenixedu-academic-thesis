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

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.thesis.domain.StudentThesisCandidacy;
import org.fenixedu.academic.thesis.domain.ThesisProposalsConfiguration;
import org.fenixedu.academic.thesis.ui.service.ThesisProposalsService;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@SpringFunctionality(app = ThesisProposalsController.class, title = "title.thesisProposal.admin.management",
        accessGroup = "thesisSystemManagers | thesisCreators")
@RequestMapping("/admin-proposals")
public class AdminThesisProposalsController {

    @Autowired
    ThesisProposalsService service;

    @RequestMapping(method = RequestMethod.GET)
    public String listProposals(Model model, @RequestParam(required = false) ThesisProposalsConfiguration configuration,
            @RequestParam(required = false) Boolean isVisible, @RequestParam(required = false) Boolean isAttributed,
            @RequestParam(required = false) Boolean hasCandidacy) {

        List<ThesisProposalsConfiguration> configurations =
                service.getThesisProposalsConfigurationsForCoordinator(Authenticate.getUser());

        if (configuration == null && !configurations.isEmpty()) {
            configuration = configurations.iterator().next();
        }

        model.addAttribute("configuration", configuration);
        model.addAttribute("configurations", configurations);
        model.addAttribute("isVisible", isVisible);
        model.addAttribute("isAttributed", isAttributed);
        model.addAttribute("hasCandidacy", hasCandidacy);
        model.addAttribute("service", service);
        model.addAttribute("coordinatorProposals",
                service.getCoordinatorProposals(configuration, isVisible, isAttributed, hasCandidacy));
        return "proposals/admin-list";
    }

    @RequestMapping(method = RequestMethod.GET, value = "candidates")
    public String listCandidates(Model model, @RequestParam(required = false) ThesisProposalsConfiguration configuration) {

        Map<Registration, TreeSet<StudentThesisCandidacy>> registrations = service.getCoordinatorCandidacies(configuration);

        model.addAttribute("registrations", registrations);
        model.addAttribute("configuration", configuration);

        return "proposals/admin-candidates-list";
    }

    @RequestMapping(method = RequestMethod.GET, value = "deleteCandidacy/{oid}")
    public String deleteCandidacy(Model model, @PathVariable("oid") StudentThesisCandidacy studentThesisCandidacy) {
        service.delete(studentThesisCandidacy);
        return "redirect:/admin-proposals";
    }

    @RequestMapping(method = RequestMethod.GET, value = "acceptCandidacy/{oid}")
    public String attributeProposal(Model model, @PathVariable("oid") StudentThesisCandidacy studentThesisCandidacy) {
        service.accept(studentThesisCandidacy);
        return "proposals/admin-candidates-list";
    }
}
