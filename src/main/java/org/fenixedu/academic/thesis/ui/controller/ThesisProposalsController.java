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
package org.fenixedu.academic.thesis.ui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.accessControl.CoordinatorGroup;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.thesis.domain.StudentThesisCandidacy;
import org.fenixedu.academic.thesis.domain.ThesisProposal;
import org.fenixedu.academic.thesis.domain.ThesisProposalParticipant;
import org.fenixedu.academic.thesis.domain.ThesisProposalParticipantType;
import org.fenixedu.academic.thesis.domain.ThesisProposalsConfiguration;
import org.fenixedu.academic.thesis.domain.ThesisProposalsSystem;
import org.fenixedu.academic.thesis.domain.exception.MaxNumberThesisProposalsException;
import org.fenixedu.academic.thesis.domain.exception.OutOfCandidacyPeriodException;
import org.fenixedu.academic.thesis.domain.exception.OutOfProposalPeriodException;
import org.fenixedu.academic.thesis.ui.bean.ThesisProposalBean;
import org.fenixedu.academic.thesis.ui.bean.ThesisProposalParticipantBean;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.signals.DomainObjectEvent;
import org.fenixedu.bennu.signals.Signal;
import org.fenixedu.bennu.spring.portal.SpringApplication;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.joda.time.DateTime;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@SpringApplication(group = "thesisCreators | activeStudents | #managers", path = "thesisProposals",
        title = "application.title.thesis", hint = "Thesis")
@SpringFunctionality(app = ThesisProposalsController.class, title = "title.thesisProposal.management",
        accessGroup = "thesisSystemManagers | thesisCreators")
@RequestMapping("/proposals")
public class ThesisProposalsController {

    public class UnexistentThesisParticipantException extends Exception {

    }

    public class CannotEditUsedThesisProposalsException extends Exception {

        private static final long serialVersionUID = -4965296880371661815L;
        private ThesisProposal thesisProposal;

        public CannotEditUsedThesisProposalsException(ThesisProposal thesisProposal) {
            this.thesisProposal = thesisProposal;
        }

        public ThesisProposal getThesisProposal() {
            return thesisProposal;
        }

        public void setThesisProposal(ThesisProposal thesisProposal) {
            this.thesisProposal = thesisProposal;
        }

    }

    public class IllegalParticipantTypeException extends Exception {

        private static final long serialVersionUID = -3114050449816099494L;
        private User user;

        public IllegalParticipantTypeException(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

    }

    public class UnequivalentThesisConfigurations extends Exception {

        private static final long serialVersionUID = -4270028206922579262L;
        private ThesisProposalsConfiguration configuration0;
        private ThesisProposalsConfiguration configuration1;

        public ThesisProposalsConfiguration getConfiguration0() {
            return configuration0;
        }

        public void setConfiguration0(ThesisProposalsConfiguration configuration0) {
            this.configuration0 = configuration0;
        }

        public ThesisProposalsConfiguration getConfiguration1() {
            return configuration1;
        }

        public void setConfiguration1(ThesisProposalsConfiguration configuration1) {
            this.configuration1 = configuration1;
        }

        public UnequivalentThesisConfigurations(ThesisProposalsConfiguration configuration0,
                ThesisProposalsConfiguration configuration1) {
            this.configuration0 = configuration0;
            this.configuration1 = configuration1;
        }

    }

    public class UnexistentConfigurationException extends Exception {

        private static final long serialVersionUID = -85534820603380001L;

        public UnexistentConfigurationException() {
        }

    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String listProposals(Model model) {

        Set<ExecutionDegree> notPastExecDegrees =
                Authenticate
                        .getUser()
                        .getPerson()
                        .getTeacher()
                        .getProfessorships(ExecutionYear.readCurrentExecutionYear())
                        .stream()
                        .flatMap(professorship -> professorship.getExecutionCourse().getExecutionDegrees().stream())
                        .map(execDegree -> execDegree.getDegree())
                        .flatMap(degree -> degree.getExecutionDegrees().stream())
                        .filter(executionDegree -> executionDegree.getExecutionYear().isAfterOrEquals(
                                ExecutionYear.readCurrentExecutionYear())).collect(Collectors.toSet());

        HashMap<Degree, Set<ThesisProposalsConfiguration>> map = new HashMap<Degree, Set<ThesisProposalsConfiguration>>();

        notPastExecDegrees.stream().flatMap(execDegree -> execDegree.getThesisProposalsConfigurationSet().stream())
                .filter(config -> config.getProposalPeriod().getEnd().isAfterNow()).forEach(config -> {
                    Degree degree = config.getExecutionDegree().getDegree();
                    if (!map.containsKey(degree)) {
                        map.put(degree, new HashSet<ThesisProposalsConfiguration>());
                    }
                    map.get(degree).add(config);
                });;

        Set<ThesisProposalsConfiguration> suggestedConfigs = new HashSet<ThesisProposalsConfiguration>();
        for (Degree degree : map.keySet()) {
            Optional<ThesisProposalsConfiguration> config =
                    map.get(degree).stream().min(ThesisProposalsConfiguration.COMPARATOR_BY_CANDIDACY_PERIOD_START_ASC);
            if (config.isPresent()) {
                suggestedConfigs.add(config.get());
            }
        }

        model.addAttribute("suggestedConfigs", suggestedConfigs);

        Set<ThesisProposal> thesisProposalsList = ThesisProposal.readCurrentByParticipant(Authenticate.getUser());

        model.addAttribute("thesisProposalsList", thesisProposalsList);

        HashMap<Degree, Set<ThesisProposal>> coordinatorProposals = new HashMap<Degree, Set<ThesisProposal>>();

        for (Degree degree : Degree.readBolonhaDegrees()) {

            Set<ThesisProposal> proposals =
                    degree.getExecutionDegrees()
                            .stream()
                            .filter(executionDegree -> CoordinatorGroup.get(executionDegree.getDegree()).isMember(
                                    Authenticate.getUser()))
                            .filter(executionDegree -> executionDegree.getExecutionYear().isAfterOrEquals(
                                    ExecutionYear.readCurrentExecutionYear()))
                            .flatMap(executionDegree -> executionDegree.getThesisProposalsConfigurationSet().stream())
                            .flatMap(config -> config.getThesisProposalSet().stream()).collect(Collectors.toSet());

            if (!proposals.isEmpty()) {
                coordinatorProposals.put(degree, proposals);
            }
        }

        model.addAttribute("coordinatorProposals", coordinatorProposals);

        return "proposals/list";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public ModelAndView createThesisProposalsForm(Model model) {

        ModelAndView mav = new ModelAndView("proposals/create", "command", new ThesisProposalBean());

        List<ThesisProposalsConfiguration> configs =
                ThesisProposalsSystem.getInstance().getThesisProposalsConfigurationSet().stream()
                        .filter(config -> config.getProposalPeriod().containsNow()).collect(Collectors.toList());
        mav.addObject("configurations", configs);

        Collections.sort(configs, ThesisProposalsConfiguration.COMPARATOR_BY_YEAR_AND_EXECUTION_DEGREE);

        List<ThesisProposalParticipantType> participantTypeList = new ArrayList<ThesisProposalParticipantType>();
        participantTypeList.addAll(ThesisProposalsSystem.getInstance().getThesisProposalParticipantTypeSet());
        Collections.sort(participantTypeList, ThesisProposalParticipantType.COMPARATOR_BY_WEIGHT);
        mav.addObject("participantTypeList", participantTypeList);

        return mav;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ModelAndView createThesisProposals(@ModelAttribute ThesisProposalBean proposalBean,
            @RequestParam String participantsJson, @RequestParam Set<ThesisProposalsConfiguration> thesisProposalsConfigurations,
            Model model) {

        if (thesisProposalsConfigurations == null || thesisProposalsConfigurations.isEmpty()) {
            model.addAttribute("unexistentConfigurationException", true);

            Set<ThesisProposalsConfiguration> configs =
                    ThesisProposalsSystem.getInstance().getThesisProposalsConfigurationSet().stream()
                            .filter(config -> config.getProposalPeriod().containsNow()).collect(Collectors.toSet());
            model.addAttribute("configurations", configs);

            List<ThesisProposalParticipantType> participantTypeList = new ArrayList<ThesisProposalParticipantType>();
            participantTypeList.addAll(ThesisProposalsSystem.getInstance().getThesisProposalParticipantTypeSet());
            Collections.sort(participantTypeList, ThesisProposalParticipantType.COMPARATOR_BY_WEIGHT);
            model.addAttribute("participantTypeList", participantTypeList);

            model.addAttribute("command", proposalBean);
            return new ModelAndView("proposals/create", model.asMap());
        }

        ThesisProposalsConfiguration base = (ThesisProposalsConfiguration) thesisProposalsConfigurations.toArray()[0];

        try {
            for (ThesisProposalsConfiguration configuration : thesisProposalsConfigurations) {
                if (!base.isEquivalent(configuration)) {
                    throw new UnequivalentThesisConfigurations(base, configuration);
                }
            }
        } catch (UnequivalentThesisConfigurations exception) {
            model.addAttribute("unequivalentThesisConfigurationsException", exception);

            Set<ThesisProposalsConfiguration> configs =
                    ThesisProposalsSystem.getInstance().getThesisProposalsConfigurationSet().stream()
                            .filter(config -> config.getProposalPeriod().containsNow()).collect(Collectors.toSet());
            model.addAttribute("configurations", configs);

            List<ThesisProposalParticipantType> participantTypeList = new ArrayList<ThesisProposalParticipantType>();
            participantTypeList.addAll(ThesisProposalsSystem.getInstance().getThesisProposalParticipantTypeSet());
            Collections.sort(participantTypeList, ThesisProposalParticipantType.COMPARATOR_BY_WEIGHT);
            model.addAttribute("participantTypeList", participantTypeList);

            model.addAttribute("command", proposalBean);
            return new ModelAndView("proposals/create", model.asMap());
        }

        proposalBean.setThesisProposalsConfigurations(thesisProposalsConfigurations);

        try {
            JsonParser parser = new JsonParser();
            JsonArray jsonArray = (JsonArray) parser.parse(participantsJson);

            ArrayList<ThesisProposalParticipantBean> participants = new ArrayList<ThesisProposalParticipantBean>();

            for (JsonElement elem : jsonArray) {
                String userId = elem.getAsJsonObject().get("userId").getAsString();
                String userType = elem.getAsJsonObject().get("userType").getAsString();

                if (userType.isEmpty()) {
                    throw new IllegalParticipantTypeException(User.findByUsername(userId));
                }

                participants.add(new ThesisProposalParticipantBean(User.findByUsername(userId), userType));
            }

            if (participants.isEmpty()) {
                throw new UnexistentThesisParticipantException();
            }

            ThesisProposal thesisProposal = createThesisProposal(proposalBean, participants);
            Signal.emit(ThesisProposal.SIGNAL_CREATED, new DomainObjectEvent<ThesisProposal>(thesisProposal));

        } catch (OutOfProposalPeriodException exception) {
            model.addAttribute("createOutOfProposalPeriodException", exception);

            Set<ThesisProposalsConfiguration> configs =
                    ThesisProposalsSystem.getInstance().getThesisProposalsConfigurationSet().stream()
                            .filter(config -> config.getProposalPeriod().containsNow()).collect(Collectors.toSet());
            model.addAttribute("configurations", configs);

            List<ThesisProposalParticipantType> participantTypeList = new ArrayList<ThesisProposalParticipantType>();
            participantTypeList.addAll(ThesisProposalsSystem.getInstance().getThesisProposalParticipantTypeSet());
            Collections.sort(participantTypeList, ThesisProposalParticipantType.COMPARATOR_BY_WEIGHT);
            model.addAttribute("participantTypeList", participantTypeList);

            model.addAttribute("command", proposalBean);
            return new ModelAndView("proposals/create", model.asMap());
        } catch (MaxNumberThesisProposalsException exception) {
            model.addAttribute("createMaxNumberThesisProposalsException", exception);

            Set<ThesisProposalsConfiguration> configs =
                    ThesisProposalsSystem.getInstance().getThesisProposalsConfigurationSet().stream()
                            .filter(config -> config.getProposalPeriod().containsNow()).collect(Collectors.toSet());
            model.addAttribute("configurations", configs);

            List<ThesisProposalParticipantType> participantTypeList = new ArrayList<ThesisProposalParticipantType>();
            participantTypeList.addAll(ThesisProposalsSystem.getInstance().getThesisProposalParticipantTypeSet());
            Collections.sort(participantTypeList, ThesisProposalParticipantType.COMPARATOR_BY_WEIGHT);
            model.addAttribute("participantTypeList", participantTypeList);

            model.addAttribute("command", proposalBean);
            return new ModelAndView("proposals/create", model.asMap());
        } catch (IllegalParticipantTypeException exception) {
            model.addAttribute("illegalParticipantTypeException", exception);

            Set<ThesisProposalsConfiguration> configs =
                    ThesisProposalsSystem.getInstance().getThesisProposalsConfigurationSet().stream()
                            .filter(config -> config.getProposalPeriod().containsNow()).collect(Collectors.toSet());
            model.addAttribute("configurations", configs);

            List<ThesisProposalParticipantType> participantTypeList = new ArrayList<ThesisProposalParticipantType>();
            participantTypeList.addAll(ThesisProposalsSystem.getInstance().getThesisProposalParticipantTypeSet());
            Collections.sort(participantTypeList, ThesisProposalParticipantType.COMPARATOR_BY_WEIGHT);
            model.addAttribute("participantTypeList", participantTypeList);

            model.addAttribute("command", proposalBean);
            return new ModelAndView("proposals/create", model.asMap());
        } catch (UnexistentThesisParticipantException exception) {
            model.addAttribute("unexistentThesisParticipantException", exception);

            Set<ThesisProposalsConfiguration> configs =
                    ThesisProposalsSystem.getInstance().getThesisProposalsConfigurationSet().stream()
                            .filter(config -> config.getProposalPeriod().containsNow()).collect(Collectors.toSet());
            model.addAttribute("configurations", configs);

            List<ThesisProposalParticipantType> participantTypeList = new ArrayList<ThesisProposalParticipantType>();
            participantTypeList.addAll(ThesisProposalsSystem.getInstance().getThesisProposalParticipantTypeSet());
            Collections.sort(participantTypeList, ThesisProposalParticipantType.COMPARATOR_BY_WEIGHT);
            model.addAttribute("participantTypeList", participantTypeList);

            model.addAttribute("command", proposalBean);
            return new ModelAndView("proposals/create", model.asMap());
        }

        return new ModelAndView(listProposals(model));
    }

    @Atomic(mode = TxMode.WRITE)
    private ThesisProposal createThesisProposal(ThesisProposalBean proposalBean,
            ArrayList<ThesisProposalParticipantBean> participantsBean) throws MaxNumberThesisProposalsException,
            OutOfProposalPeriodException {

        proposalBean.setThesisProposalParticipantsBean(new HashSet<ThesisProposalParticipantBean>());
        proposalBean.getThesisProposalParticipantsBean().addAll(participantsBean);

        return new ThesisProposalBean.Builder(proposalBean).build();
    }

    @RequestMapping(value = "/delete/{oid}", method = RequestMethod.POST)
    public ModelAndView deleteThesisProposals(@PathVariable("oid") ThesisProposal thesisProposal, Model model) {

        return delete(thesisProposal, model);
    }

    @Atomic(mode = TxMode.WRITE)
    private ModelAndView delete(ThesisProposal thesisProposal, Model model) {
        try {
            thesisProposal.delete();
        } catch (DomainException domainException) {
            model.addAttribute("deleteException", true);
            return new ModelAndView(listProposals(model));
        }
        return new ModelAndView("redirect:/proposals");
    }

    @RequestMapping(value = "/edit/{oid}", method = RequestMethod.GET)
    public ModelAndView editProposalForm(@PathVariable("oid") ThesisProposal thesisProposal, Model model) {

        boolean isManager = DynamicGroup.get("managers").isMember(Authenticate.getUser());
        boolean isDegreeCoordinator =
                thesisProposal.getExecutionDegreeSet().stream()
                        .anyMatch(execDegree -> CoordinatorGroup.get(execDegree.getDegree()).isMember(Authenticate.getUser()));

        try {
            if (!(isManager || isDegreeCoordinator)
                    && !thesisProposal.getSingleThesisProposalsConfiguration().getProposalPeriod().contains(DateTime.now())) {
                throw new OutOfProposalPeriodException();
            } else {
                if (!thesisProposal.getStudentThesisCandidacySet().isEmpty()) {
                    throw new CannotEditUsedThesisProposalsException(thesisProposal);
                } else {
                    HashSet<ThesisProposalParticipantBean> thesisProposalParticipantsBean =
                            new HashSet<ThesisProposalParticipantBean>();

                    for (ThesisProposalParticipant participant : thesisProposal.getThesisProposalParticipantSet()) {

                        String participantType = participant.getThesisProposalParticipantType().getExternalId();

                        ThesisProposalParticipantBean bean =
                                new ThesisProposalParticipantBean(participant.getUser(), participantType);

                        thesisProposalParticipantsBean.add(bean);
                    }

                    ThesisProposalBean thesisProposalBean =
                            new ThesisProposalBean(thesisProposal.getTitle(), thesisProposal.getObservations(),
                                    thesisProposal.getRequirements(), thesisProposal.getGoals(),
                                    thesisProposal.getLocalization(), thesisProposal.getThesisConfigurationSet(),
                                    thesisProposal.getStudentThesisCandidacySet(), thesisProposalParticipantsBean,
                                    thesisProposal.getExternalId());

                    ModelAndView mav = new ModelAndView("proposals/edit", "command", thesisProposalBean);

                    Set<ThesisProposalsConfiguration> configs =
                            ThesisProposalsSystem
                                    .getInstance()
                                    .getThesisProposalsConfigurationSet()
                                    .stream()
                                    .filter(config -> config.getProposalPeriod().overlaps(
                                            thesisProposal.getSingleThesisProposalsConfiguration().getProposalPeriod()))
                                    .collect(Collectors.toSet());

                    mav.addObject("configurations", configs);

                    List<ThesisProposalParticipantType> participantTypeList = new ArrayList<ThesisProposalParticipantType>();
                    participantTypeList.addAll(ThesisProposalsSystem.getInstance().getThesisProposalParticipantTypeSet());
                    Collections.sort(participantTypeList, ThesisProposalParticipantType.COMPARATOR_BY_WEIGHT);

                    mav.addObject("participantTypeList", participantTypeList);

                    return mav;
                }
            }
        } catch (OutOfProposalPeriodException exception) {
            model.addAttribute("editOutOfProposalPeriodException", exception);
            return new ModelAndView(listProposals(model));
        } catch (CannotEditUsedThesisProposalsException exception) {
            model.addAttribute("cannotEditUsedThesisProposalsException", exception);
            return new ModelAndView(listProposals(model));
        }
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ModelAndView editProposal(@ModelAttribute ThesisProposalBean thesisProposalBean,
            @RequestParam String participantsJson, @RequestParam Set<ThesisProposalsConfiguration> thesisProposalsConfigurations,
            Model model) {

        thesisProposalBean.setThesisProposalsConfigurations(thesisProposalsConfigurations);

        ThesisProposal thesisProposal = FenixFramework.getDomainObject(thesisProposalBean.getExternalId());

        JsonParser parser = new JsonParser();
        JsonArray jsonArray = (JsonArray) parser.parse(participantsJson);

        try {
            return editThesisProposal(thesisProposalBean, thesisProposal, jsonArray);
        } catch (MaxNumberThesisProposalsException exception) {
            model.addAttribute("editMaxNumberThesisProposalsException", exception);
            return editProposalForm(thesisProposal, model);
        } catch (OutOfProposalPeriodException exception) {
            model.addAttribute("outOfProposalPeriodException", true);
            return editProposalForm(thesisProposal, model);
        } catch (IllegalParticipantTypeException exception) {
            model.addAttribute("illegalParticipantTypeException", exception);
            return editProposalForm(thesisProposal, model);
        } catch (UnexistentConfigurationException exception) {
            model.addAttribute("unexistentConfigurationException", exception);
            return editProposalForm(thesisProposal, model);
        } catch (UnexistentThesisParticipantException exception) {
            model.addAttribute("unexistentThesisParticipantException", exception);
            return editProposalForm(thesisProposal, model);
        } catch (UnequivalentThesisConfigurations exception) {
            model.addAttribute("unequivalentThesisConfigurations", exception);
            return editProposalForm(thesisProposal, model);
        }
    }

    @Atomic(mode = TxMode.WRITE)
    private ModelAndView editThesisProposal(ThesisProposalBean thesisProposalBean, ThesisProposal thesisProposal,
            JsonArray jsonArray) throws MaxNumberThesisProposalsException, OutOfProposalPeriodException,
            IllegalParticipantTypeException, UnexistentConfigurationException, UnexistentThesisParticipantException,
            UnequivalentThesisConfigurations {

        ArrayList<ThesisProposalParticipantBean> participantsBean = new ArrayList<ThesisProposalParticipantBean>();

        for (JsonElement elem : jsonArray) {
            String userId = elem.getAsJsonObject().get("userId").getAsString();
            String userType = elem.getAsJsonObject().get("userType").getAsString();

            if (userType.isEmpty()) {
                throw new IllegalParticipantTypeException(User.findByUsername(userId));
            }

            ThesisProposalParticipantBean participantBean =
                    new ThesisProposalParticipantBean(User.findByUsername(userId), userType);

            participantsBean.add(participantBean);
        }

        if (participantsBean.isEmpty()) {
            throw new UnexistentThesisParticipantException();
        }

        for (ThesisProposalParticipant participant : thesisProposal.getThesisProposalParticipantSet()) {
            participant.delete();
        }

        thesisProposal.getThesisProposalParticipantSet().clear();

        ArrayList<ThesisProposalParticipant> participants = new ArrayList<ThesisProposalParticipant>();

        for (ThesisProposalParticipantBean participantBean : participantsBean) {
            User user = FenixFramework.getDomainObject(participantBean.getUserExternalId());

            ThesisProposalParticipantType participantType =
                    FenixFramework.getDomainObject(participantBean.getParticipantTypeExternalId());

            ThesisProposalParticipant participant = new ThesisProposalParticipant(user, participantType);

            for (ThesisProposalsConfiguration configuration : thesisProposal.getThesisConfigurationSet()) {
                int proposalsCount =
                        configuration
                                .getThesisProposalSet()
                                .stream()
                                .filter(proposal -> proposal.getThesisProposalParticipantSet().stream().map(p -> p.getUser())
                                        .collect(Collectors.toSet()).contains(participant.getUser())).collect(Collectors.toSet())
                                .size();

                if (configuration.getMaxThesisProposalsByUser() != -1
                        && proposalsCount >= configuration.getMaxThesisProposalsByUser()) {
                    throw new MaxNumberThesisProposalsException(participant);
                }

                else {
                    participant.setThesisProposal(thesisProposal);
                    participants.add(participant);
                }
            }
        }

        thesisProposal.setTitle(thesisProposalBean.getTitle());
        thesisProposal.setObservations(thesisProposalBean.getObservations());
        thesisProposal.setRequirements(thesisProposalBean.getRequirements());
        thesisProposal.setGoals(thesisProposalBean.getGoals());
        thesisProposal.getThesisConfigurationSet().clear();
        thesisProposal.getThesisConfigurationSet().addAll(thesisProposalBean.getThesisProposalsConfigurations());

        thesisProposal.getThesisProposalParticipantSet().addAll(participants);

        ThesisProposalsConfiguration base =
                (ThesisProposalsConfiguration) thesisProposalBean.getThesisProposalsConfigurations().toArray()[0];

        for (ThesisProposalsConfiguration configuration : thesisProposalBean.getThesisProposalsConfigurations()) {
            if (!base.isEquivalent(configuration)) {
                throw new UnequivalentThesisConfigurations(base, configuration);
            }
        }

        ThesisProposalsConfiguration config = thesisProposal.getSingleThesisProposalsConfiguration();

        boolean isManager = DynamicGroup.get("managers").isMember(Authenticate.getUser());
        boolean isDegreeCoordinator =
                thesisProposal.getExecutionDegreeSet().stream()
                        .anyMatch(execDegree -> CoordinatorGroup.get(execDegree.getDegree()).isMember(Authenticate.getUser()));

        if (!(isManager || isDegreeCoordinator) && !config.getProposalPeriod().containsNow()) {
            throw new OutOfProposalPeriodException();
        }
        thesisProposal.setLocalization(thesisProposalBean.getLocalization());

        return new ModelAndView("redirect:/proposals");
    }

    @RequestMapping(value = "/accept/{studentThesisCandidacy}", method = RequestMethod.POST)
    public String acceptStudentThesisCandidacy(
            @PathVariable("studentThesisCandidacy") StudentThesisCandidacy studentThesisCandidacy, Model model) {

        return accept(studentThesisCandidacy, model);
    }

    @Atomic(mode = TxMode.WRITE)
    private String accept(StudentThesisCandidacy studentThesisCandidacy, Model model) {
        try {
            if (!studentThesisCandidacy.getThesisProposal().getSingleThesisProposalsConfiguration().getCandidacyPeriod()
                    .containsNow()) {
                throw new OutOfCandidacyPeriodException();
            }

            for (StudentThesisCandidacy candidacy : studentThesisCandidacy.getThesisProposal().getStudentThesisCandidacySet()) {
                candidacy.setAcceptedByAdvisor(false);
            }

            studentThesisCandidacy.setAcceptedByAdvisor(true);

            return "redirect:/proposals/manage/" + studentThesisCandidacy.getThesisProposal().getExternalId();
        } catch (OutOfCandidacyPeriodException exception) {
            model.addAttribute("outOfCandidacyPeriodException", true);
            return "redirect:/proposals/manage/" + studentThesisCandidacy.getThesisProposal().getExternalId();
        }
    }

    @RequestMapping(value = "/reject/{studentThesisCandidacy}", method = RequestMethod.POST)
    public String rejectStudentThesisCandidacy(
            @PathVariable("studentThesisCandidacy") StudentThesisCandidacy studentThesisCandidacy, Model model) {

        return reject(studentThesisCandidacy, model);
    }

    @Atomic(mode = TxMode.WRITE)
    private String reject(StudentThesisCandidacy studentThesisCandidacy, Model model) {
        try {
            if (!studentThesisCandidacy.getThesisProposal().getSingleThesisProposalsConfiguration().getCandidacyPeriod()
                    .containsNow()) {
                throw new OutOfCandidacyPeriodException();
            }
            studentThesisCandidacy.setAcceptedByAdvisor(false);

            return "redirect:/proposals/manage/" + studentThesisCandidacy.getThesisProposal().getExternalId();
        } catch (OutOfCandidacyPeriodException exception) {
            model.addAttribute("outOfCandidacyPeriodException", true);
            return "redirect:/proposals/manage/" + studentThesisCandidacy.getThesisProposal().getExternalId();
        }
    }

    @RequestMapping(value = "/manage/{oid}", method = RequestMethod.GET)
    public ModelAndView manageCandidacies(@PathVariable("oid") ThesisProposal thesisProposal, Model model) {

        ModelAndView mav = new ModelAndView("thesisCandidacies/manage");

        mav.addObject("thesisProposal", thesisProposal);

        ArrayList<StudentThesisCandidacy> candidacies =
                new ArrayList<StudentThesisCandidacy>(thesisProposal.getStudentThesisCandidacySet());

        Collections.sort(candidacies, StudentThesisCandidacy.COMPARATOR_BY_DATETIME);

        mav.addObject("candidaciesList", candidacies);

        HashMap<String, Integer> bestAccepted = new HashMap<String, Integer>();

        for (StudentThesisCandidacy candidacy : thesisProposal.getStudentThesisCandidacySet()) {
            Registration registration = candidacy.getRegistration();
            if (!bestAccepted.containsKey(registration.getExternalId())) {
                for (StudentThesisCandidacy studentCandidacy : registration.getStudentThesisCandidacySet()) {
                    if (studentCandidacy.getAcceptedByAdvisor()
                            && studentCandidacy.getPreferenceNumber() < bestAccepted.getOrDefault(registration.getExternalId(),
                                    Integer.MAX_VALUE)) {
                        bestAccepted.put(registration.getExternalId(), studentCandidacy.getPreferenceNumber());
                    }
                }
            }
        }

        model.addAttribute("bestAccepted", bestAccepted);

        return mav;
    }

    @RequestMapping(value = "/transpose", method = RequestMethod.GET)
    public String listOldProposals(Model model) {

        User user = Authenticate.getUser();

        Set<ThesisProposal> proposals =
                user.getThesisProposalParticipantSet().stream().map(participant -> participant.getThesisProposal())
                        .collect(Collectors.toSet());

        HashMap<String, Set<ThesisProposal>> proposalTitleMap = new HashMap<String, Set<ThesisProposal>>();

        for (ThesisProposal proposal : proposals) {
            if (!proposalTitleMap.containsKey(proposal.getTitle())) {
                proposalTitleMap.put(proposal.getTitle(), new HashSet<ThesisProposal>());
            }
            proposalTitleMap.get(proposal.getTitle()).add(proposal);
        }

        Set<ThesisProposal> recentProposals = new HashSet<ThesisProposal>();
        for (String key : proposalTitleMap.keySet()) {
            recentProposals.add(proposalTitleMap.get(key).stream().max(ThesisProposal.COMPARATOR_BY_PROPOSAL_PERIOD).get());
        }

        model.addAttribute("recentProposals", recentProposals);

        return "proposals/old";
    }

    @RequestMapping(value = "/transpose/{oid}", method = RequestMethod.GET)
    public ModelAndView transposeProposal(@PathVariable("oid") ThesisProposal thesisProposal, Model model) {

        ThesisProposalBean proposalBean = new ThesisProposalBean();
        proposalBean.setGoals(thesisProposal.getGoals());
        proposalBean.setLocalization(thesisProposal.getLocalization());
        proposalBean.setObservations(thesisProposal.getObservations());
        proposalBean.setRequirements(thesisProposal.getRequirements());
        proposalBean.setTitle(thesisProposal.getTitle());

        HashSet<ThesisProposalParticipantBean> thesisProposalParticipantsBean = new HashSet<ThesisProposalParticipantBean>();

        for (ThesisProposalParticipant participant : thesisProposal.getThesisProposalParticipantSet()) {
            String participantType = participant.getThesisProposalParticipantType().getExternalId();
            ThesisProposalParticipantBean bean = new ThesisProposalParticipantBean(participant.getUser(), participantType);
            thesisProposalParticipantsBean.add(bean);
        }

        proposalBean.setThesisProposalParticipantsBean(thesisProposalParticipantsBean);

        ModelAndView mav = new ModelAndView("proposals/create", "command", proposalBean);

        Set<ThesisProposalsConfiguration> configs =
                ThesisProposalsSystem.getInstance().getThesisProposalsConfigurationSet().stream()
                        .filter(config -> config.getProposalPeriod().containsNow()).collect(Collectors.toSet());

        mav.addObject("configurations", configs);

        List<ThesisProposalParticipantType> participantTypeList = new ArrayList<ThesisProposalParticipantType>();
        participantTypeList.addAll(ThesisProposalsSystem.getInstance().getThesisProposalParticipantTypeSet());
        Collections.sort(participantTypeList, ThesisProposalParticipantType.COMPARATOR_BY_WEIGHT);

        mav.addObject("participantTypeList", participantTypeList);

        return mav;
    }

}
