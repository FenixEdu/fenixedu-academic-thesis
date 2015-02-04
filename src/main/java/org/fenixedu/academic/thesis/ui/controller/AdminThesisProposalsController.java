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

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.academic.domain.accessControl.CoordinatorGroup;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.thesis.domain.StudentThesisCandidacy;
import org.fenixedu.academic.thesis.domain.ThesisProposal;
import org.fenixedu.academic.thesis.domain.ThesisProposalParticipant;
import org.fenixedu.academic.thesis.domain.ThesisProposalsConfiguration;
import org.fenixedu.academic.thesis.ui.bean.AdminProposalsSummaryBean;
import org.fenixedu.academic.thesis.ui.bean.ThesisProposalBean;
import org.fenixedu.academic.thesis.ui.bean.ThesisProposalParticipantBean;
import org.fenixedu.academic.thesis.ui.exception.ThesisProposalException;
import org.fenixedu.academic.thesis.ui.exception.UnequivalentThesisConfigurationsException;
import org.fenixedu.academic.thesis.ui.exception.UnexistentConfigurationException;
import org.fenixedu.academic.thesis.ui.service.ExportThesisProposalsService;
import org.fenixedu.academic.thesis.ui.service.ThesisProposalsService;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.FenixFramework;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

@SpringFunctionality(app = ThesisProposalsController.class, title = "title.thesisProposal.admin.management",
        accessGroup = "thesisSystemManagers | thesisCreators")
@RequestMapping("/admin-proposals")
public class AdminThesisProposalsController {

    @Autowired
    ThesisProposalsService service;

    @Autowired
    ExportThesisProposalsService exportService;

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
        model.addAttribute("summary", configuration == null ? null : new AdminProposalsSummaryBean(service, configuration));
        model.addAttribute("configurations", configurations);
        model.addAttribute("isVisible", isVisible);
        model.addAttribute("isAttributed", isAttributed);
        model.addAttribute("hasCandidacy", hasCandidacy);
        model.addAttribute("service", service);
        model.addAttribute("coordinatorProposals",
                service.getCoordinatorProposals(configuration, isVisible, isAttributed, hasCandidacy));
        return "proposals/admin-list";
    }

    @RequestMapping(value = "/edit/{oid}", method = RequestMethod.GET)
    public ModelAndView editProposalForm(@PathVariable("oid") ThesisProposal thesisProposal,
            @RequestParam(required = false) ThesisProposalsConfiguration configuration, Model model) {

        boolean isManager = DynamicGroup.get("managers").isMember(Authenticate.getUser());
        boolean isDegreeCoordinator =
                thesisProposal.getExecutionDegreeSet().stream()
                        .anyMatch(execDegree -> CoordinatorGroup.get(execDegree.getDegree()).isMember(Authenticate.getUser()));

        if (configuration == null) {
            configuration = thesisProposal.getSingleThesisProposalsConfiguration();
        }

        model.addAttribute("configuration", configuration);
        model.addAttribute("action", "admin-proposals/edit");
        model.addAttribute("adminEdit", true);
        HashSet<ThesisProposalParticipantBean> thesisProposalParticipantsBean = new HashSet<ThesisProposalParticipantBean>();

        for (ThesisProposalParticipant participant : thesisProposal.getThesisProposalParticipantSet()) {
            String participantType = participant.getThesisProposalParticipantType().getExternalId();
            ThesisProposalParticipantBean bean = new ThesisProposalParticipantBean(participant.getUser(), participantType);
            thesisProposalParticipantsBean.add(bean);
        }

        ThesisProposalBean thesisProposalBean =
                new ThesisProposalBean(thesisProposal.getTitle(), thesisProposal.getObservations(),
                        thesisProposal.getRequirements(), thesisProposal.getGoals(), thesisProposal.getLocalization(),
                        thesisProposal.getThesisConfigurationSet(), thesisProposal.getStudentThesisCandidacySet(),
                        thesisProposalParticipantsBean, thesisProposal.getHidden(), thesisProposal.getExternalId());

        ModelAndView mav = new ModelAndView("proposals/edit", "command", thesisProposalBean);
        Set<ThesisProposalsConfiguration> configurations = thesisProposal.getThesisConfigurationSet();
        mav.addObject("configurations", configurations);
        mav.addObject("participantTypeList", service.getThesisProposalParticipantTypes());

        return mav;

    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ModelAndView editProposal(@ModelAttribute ThesisProposalBean thesisProposalBean,
            @RequestParam String participantsJson, @RequestParam(required = false) ThesisProposalsConfiguration configuration,
            @RequestParam Set<ThesisProposalsConfiguration> thesisProposalsConfigurations, Model model,
            RedirectAttributes redirectAttrs) {

        thesisProposalBean.setThesisProposalsConfigurations(thesisProposalsConfigurations);

        ThesisProposal thesisProposal = FenixFramework.getDomainObject(thesisProposalBean.getExternalId());

        JsonParser parser = new JsonParser();
        JsonArray jsonArray = (JsonArray) parser.parse(participantsJson);

        try {
            service.editThesisProposal(Authenticate.getUser(), thesisProposalBean, thesisProposal, jsonArray);
            redirectAttrs.addAttribute("configuration", configuration != null ? configuration.getExternalId() : null);
            return new ModelAndView("redirect:/admin-proposals");
        } catch (ThesisProposalException exception) {
            model.addAttribute("error", exception.getClass().getSimpleName());
            return editProposalForm(thesisProposal, configuration, model);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "candidates")
    public String listCandidates(Model model, @RequestParam(required = false) ThesisProposalsConfiguration configuration) {

        Map<Registration, TreeSet<StudentThesisCandidacy>> registrations = service.getCoordinatorCandidacies(configuration);

        model.addAttribute("registrations", registrations);
        model.addAttribute("configuration", configuration);

        return "proposals/admin-candidates-list";
    }

    @RequestMapping(method = RequestMethod.GET, value = "acceptCandidacy/{oid}")
    public String attributeProposal(Model model, @PathVariable("oid") StudentThesisCandidacy studentThesisCandidacy,
            @RequestParam ThesisProposalsConfiguration configuration) {
        service.accept(studentThesisCandidacy);

        return "redirect:/admin-proposals/candidates?configuration=" + configuration.getExternalId();
    }

    @RequestMapping(method = RequestMethod.GET, value = "revokeCandidacyAcceptance/{oid}")
    public String revokeProposalAttribution(Model model, @PathVariable("oid") StudentThesisCandidacy studentThesisCandidacy,
            @RequestParam ThesisProposalsConfiguration configuration) {
        service.revoke(studentThesisCandidacy);

        return "redirect:/admin-proposals/candidates?configuration=" + configuration.getExternalId();
    }

    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void exportCSV(@RequestParam ThesisProposalsConfiguration configuration, HttpServletResponse response)
            throws IOException, UnavailableException {

        String filename = "proposals_" + configuration.getPresentationName();

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition", "attachment; filename=" + filename + ".xls");
        try (OutputStream outputStream = response.getOutputStream()) {
            exportService.exportThesisProposalsToExcel(configuration, outputStream);
        }
    }

    @RequestMapping(value = "/toggle/{proposal}", method = RequestMethod.GET)
    public String toggleVisibility(@PathVariable ThesisProposal proposal, Model model,
            @RequestParam(required = false) ThesisProposalsConfiguration configuration,
            @RequestParam(required = false) Boolean isVisible, @RequestParam(required = false) Boolean isAttributed,
            @RequestParam(required = false) Boolean hasCandidacy) {
        service.toggleVisibility(proposal);
        return listProposals(model, configuration, isVisible, isAttributed, hasCandidacy);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/createProposal")
    public ModelAndView createProposalForm(Model model, @RequestParam(required = false) ThesisProposalsConfiguration configuration) {

        ModelAndView modelAndview = new ModelAndView("proposals/create", "command", new ThesisProposalBean());

        Set<ThesisProposalsConfiguration> currentThesisProposalsConfigurations =
                new HashSet<ThesisProposalsConfiguration>(
                        service.getCurrentThesisProposalsConfigurations(ThesisProposalsConfiguration.COMPARATOR_BY_YEAR_AND_EXECUTION_DEGREE));
        currentThesisProposalsConfigurations.add(configuration);

        modelAndview.addObject("configurations", currentThesisProposalsConfigurations);
        modelAndview.addObject("participantTypeList", service.getThesisProposalParticipantTypes());

        modelAndview.addObject("action", "admin-proposals/createProposal");

        return modelAndview;
    }

    @RequestMapping(value = "/accept/{studentThesisCandidacy}", method = RequestMethod.POST)
    public String acceptStudentThesisCandidacy(
            @PathVariable("studentThesisCandidacy") StudentThesisCandidacy studentThesisCandidacy) {
        service.accept(studentThesisCandidacy);
        return "redirect:/admin-proposals/manage/" + studentThesisCandidacy.getThesisProposal().getExternalId();
    }

    @RequestMapping(value = "/manage/{oid}", method = RequestMethod.GET)
    public ModelAndView manageCandidacies(@PathVariable("oid") ThesisProposal thesisProposal, Model model) {
        ModelAndView view = new ModelAndView("thesisCandidacies/manage");
        view.addObject("coordinatorManagement", true);
        view.addObject("action", "admin-proposals/accept");
        view.addObject("thesisProposal", thesisProposal);
        view.addObject("candidaciesList", service.getStudentThesisCandidacy(thesisProposal));
        view.addObject("bestAccepted", service.getBestAccepted(thesisProposal));
        return view;
    }

    @RequestMapping(value = "/createProposal", method = RequestMethod.POST)
    public ModelAndView createThesisProposals(@ModelAttribute ThesisProposalBean proposalBean,
            @RequestParam String participantsJson, @RequestParam Set<ThesisProposalsConfiguration> thesisProposalsConfigurations,
            Model model) {

        try {
            if (thesisProposalsConfigurations == null || thesisProposalsConfigurations.isEmpty()) {
                throw new UnexistentConfigurationException();
            }

            ThesisProposalsConfiguration base = thesisProposalsConfigurations.iterator().next();

            for (ThesisProposalsConfiguration configuration : thesisProposalsConfigurations) {
                if (!base.isEquivalent(configuration)) {
                    throw new UnequivalentThesisConfigurationsException(base, configuration);
                }
            }

            proposalBean.setThesisProposalsConfigurations(thesisProposalsConfigurations);
            service.createThesisProposal(proposalBean, participantsJson);
        } catch (ThesisProposalException exception) {
            model.addAttribute("error", exception.getClass().getSimpleName());
            model.addAttribute("configurations", service.getCurrentThesisProposalsConfigurations());
            model.addAttribute("participantTypeList", service.getThesisProposalParticipantTypes());
            model.addAttribute("command", proposalBean);
            model.addAttribute("action", "admin-proposals/createProposal");
            return new ModelAndView("proposals/create", model.asMap());
        }

        return new ModelAndView("redirect:/admin-proposals");
    }
}
