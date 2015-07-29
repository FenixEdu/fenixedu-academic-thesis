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
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import jvstm.cps.ConsistencyException;

import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.thesis.domain.ThesisProposalParticipantType;
import org.fenixedu.academic.thesis.domain.ThesisProposalsConfiguration;
import org.fenixedu.academic.thesis.domain.ThesisProposalsSystem;
import org.fenixedu.academic.thesis.ui.bean.ConfigurationBean;
import org.fenixedu.academic.thesis.ui.bean.ParticipantTypeBean;
import org.fenixedu.academic.thesis.ui.exception.OverlappingIntervalsException;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@SpringFunctionality(app = ThesisProposalsController.class, title = "title.configuration.management",
        accessGroup = "#managers | thesisSystemManagers")
@RequestMapping("/configuration")
public class ConfigurationController {

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String listConfigurations(Model model) {

        TreeSet<ExecutionYear> executionYearsList = new TreeSet<ExecutionYear>(ExecutionYear.REVERSE_COMPARATOR_BY_YEAR);
        executionYearsList.addAll(Bennu.getInstance().getExecutionYearsSet());

        model.addAttribute("executionYearsList", executionYearsList);

        Set<ThesisProposalsConfiguration> configurationsSet =
                ThesisProposalsSystem.getInstance().getThesisProposalsConfigurationSet();

        List<ThesisProposalsConfiguration> configurationsList =
                configurationsSet
                        .stream()
                        .filter((x) -> ThesisProposalsSystem.canManage(x.getExecutionDegree().getDegree(), Authenticate.getUser()))
                        .collect(Collectors.toList());
        Collections.sort(configurationsList, ThesisProposalsConfiguration.COMPARATOR_BY_YEAR_AND_EXECUTION_DEGREE);

        model.addAttribute("configurationsList", configurationsList);

        List<ThesisProposalParticipantType> participantTypeList =
                ThesisProposalsSystem.getInstance().getThesisProposalParticipantTypeSet().stream().collect(Collectors.toList());

        Collections.sort(participantTypeList, ThesisProposalParticipantType.COMPARATOR_BY_WEIGHT);

        model.addAttribute("participantTypeList", participantTypeList);

        model.addAttribute("isManager", Group.managers().isMember(Authenticate.getUser()));

        return "/configuration/list";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public ModelAndView createConfigurationForm(Model model, @RequestParam ConfigurationBean thesisProposalsConfigurationBean) {

        ModelAndView mav = new ModelAndView("/configuration/create", "command", thesisProposalsConfigurationBean);

        TreeSet<ExecutionYear> executionYearsList = new TreeSet<ExecutionYear>(ExecutionYear.REVERSE_COMPARATOR_BY_YEAR);
        executionYearsList.addAll(Bennu.getInstance().getExecutionYearsSet());
        model.addAttribute("executionYearsList", executionYearsList);

        List<ExecutionDegree> executionDegreeList =
                Bennu.getInstance().getExecutionDegreesSet().stream().collect(Collectors.toList());
        Collections.sort(executionDegreeList, ExecutionDegree.COMPARATOR_BY_DEGREE_NAME);
        mav.addObject("executionDegreeList", executionDegreeList);

        return mav;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ModelAndView createConfiguration(@ModelAttribute ConfigurationBean configurationBean, Model model) {

        try {
            if (configurationBean.getExecutionDegree() == null) {
                model.addAttribute("unselectedExecutionDegreeException", true);
                return createConfigurationForm(model, configurationBean);
            }

            new ConfigurationBean.Builder(configurationBean).build();
        } catch (ConsistencyException exception) {
            model.addAttribute("createException", true);
            model.addAttribute("command", configurationBean);

            TreeSet<ExecutionYear> executionYearsList = new TreeSet<ExecutionYear>(ExecutionYear.REVERSE_COMPARATOR_BY_YEAR);
            executionYearsList.addAll(Bennu.getInstance().getExecutionYearsSet());
            model.addAttribute("executionYearsList", executionYearsList);

            return new ModelAndView("/configuration/create", model.asMap());
        } catch (IllegalArgumentException exception) {
            model.addAttribute("illegalArgumentException", true);
            model.addAttribute("command", configurationBean);

            TreeSet<ExecutionYear> executionYearsList = new TreeSet<ExecutionYear>(ExecutionYear.REVERSE_COMPARATOR_BY_YEAR);
            executionYearsList.addAll(Bennu.getInstance().getExecutionYearsSet());
            model.addAttribute("executionYearsList", executionYearsList);

            return new ModelAndView("/configuration/create", model.asMap());
        } catch (OverlappingIntervalsException e) {
            model.addAttribute("overlappingIntervalsException", true);
            model.addAttribute("command", configurationBean);

            TreeSet<ExecutionYear> executionYearsList = new TreeSet<ExecutionYear>(ExecutionYear.REVERSE_COMPARATOR_BY_YEAR);
            executionYearsList.addAll(Bennu.getInstance().getExecutionYearsSet());
            model.addAttribute("executionYearsList", executionYearsList);

            return new ModelAndView("/configuration/create", model.asMap());
        }

        return new ModelAndView("redirect:/configuration");
    }

    @RequestMapping(value = "/delete/{oid}", method = RequestMethod.POST)
    public ModelAndView deleteConfiguration(@PathVariable("oid") ThesisProposalsConfiguration thesisProposalsConfiguration,
            Model model) {

        try {
            delete(thesisProposalsConfiguration);
        } catch (DomainException exception) {
            model.addAttribute("deleteException", true);
            return editConfigurationForm(thesisProposalsConfiguration, model);
        }

        return new ModelAndView(listConfigurations(model));
    }

    @Atomic(mode = TxMode.WRITE)
    private void delete(ThesisProposalsConfiguration thesisProposalsConfiguration) {
        thesisProposalsConfiguration.delete();
    }

    @RequestMapping(value = "/edit/{oid}", method = RequestMethod.GET)
    public ModelAndView editConfigurationForm(@PathVariable("oid") ThesisProposalsConfiguration configuration, Model model) {

        ConfigurationBean configurationBean =
                new ConfigurationBean(configuration.getProposalPeriod().getStart(), configuration.getProposalPeriod().getEnd(),
                        configuration.getCandidacyPeriod().getStart(), configuration.getCandidacyPeriod().getEnd(),
                        configuration.getExecutionDegree(), configuration.getExternalId(),
                        configuration.getMaxThesisCandidaciesByStudent(), configuration.getMaxThesisProposalsByUser(),
                        configuration.getMinECTS1stCycle(), configuration.getMinECTS2ndCycle());

        ModelAndView mav = new ModelAndView("configuration/edit", "command", configurationBean);

        return mav;
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ModelAndView editConfiguration(@ModelAttribute ConfigurationBean configurationBean, Model model) {

        try {
            edit(configurationBean);
        } catch (IllegalArgumentException exception) {
            model.addAttribute("illegalArgumentException", true);
            model.addAttribute("command", configurationBean);

            return new ModelAndView("/configuration/edit", model.asMap());
        } catch (OverlappingIntervalsException e) {
            model.addAttribute("overlappingIntervalsException", true);
            model.addAttribute("command", configurationBean);

            return new ModelAndView("/configuration/edit", model.asMap());
        }

        return new ModelAndView("redirect:/configuration");
    }

    @Atomic(mode = TxMode.WRITE)
    private void edit(ConfigurationBean configurationBean) throws OverlappingIntervalsException {

        ThesisProposalsConfiguration thesisProposalsConfiguration =
                FenixFramework.getDomainObject(configurationBean.getExternalId());

        DateTimeFormatter formatter = ISODateTimeFormat.dateTime();

        DateTime proposalPeriodStartDT = formatter.parseDateTime(configurationBean.getProposalPeriodStart());
        DateTime proposalPeriodEndDT = formatter.parseDateTime(configurationBean.getProposalPeriodEnd());
        DateTime candidacyPeriodStartDT = formatter.parseDateTime(configurationBean.getCandidacyPeriodStart());
        DateTime candidacyPeriodEndDT = formatter.parseDateTime(configurationBean.getCandidacyPeriodEnd());

        Interval proposalPeriod = new Interval(proposalPeriodStartDT, proposalPeriodEndDT);
        Interval candidacyPeriod = new Interval(candidacyPeriodStartDT, candidacyPeriodEndDT);

        for (ThesisProposalsConfiguration config : thesisProposalsConfiguration.getExecutionDegree()
                .getThesisProposalsConfigurationSet()) {
            if (!config.equals(thesisProposalsConfiguration)
                    && (config.getProposalPeriod().overlaps(proposalPeriod)
                            || config.getCandidacyPeriod().overlaps(candidacyPeriod)
                            || config.getProposalPeriod().overlaps(candidacyPeriod) || config.getCandidacyPeriod().overlaps(
                            proposalPeriod))) {
                throw new OverlappingIntervalsException();
            }
        }

        Set<ThesisProposalsConfiguration> sharedConfigs =
                thesisProposalsConfiguration.getThesisProposalSet().stream()
                .flatMap(proposal -> proposal.getThesisConfigurationSet().stream()).collect(Collectors.toSet());
        sharedConfigs.add(thesisProposalsConfiguration);

        sharedConfigs.forEach(config -> {
            config.setProposalPeriod(proposalPeriod);
            config.setCandidacyPeriod(candidacyPeriod);

            config.setMaxThesisCandidaciesByStudent(configurationBean.getMaxThesisCandidaciesByStudent());
            config.setMaxThesisProposalsByUser(configurationBean.getMaxThesisProposalsByUser());

            config.setMinECTS1stCycle(configurationBean.getMinECTS1stCycle());
            config.setMinECTS2ndCycle(configurationBean.getMinECTS2ndCycle());
        });

    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/execution-year/{executionYear}/execution-degrees",
            method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> getExecutionDegreesByYear(
            @PathVariable("executionYear") ExecutionYear executionYear) {

        JsonArray response = new JsonArray();

        List<ExecutionDegree> executionDegreeList =
                ExecutionDegree.getAllByExecutionYear(executionYear).stream()
                        .filter(executionDegree -> executionDegree.getDegree().getCycleTypes().contains(CycleType.SECOND_CYCLE))
                        .filter((x) -> ThesisProposalsSystem.canManage(x.getDegree(), Authenticate.getUser()))
                        .collect(Collectors.toList());

        Collections.sort(executionDegreeList,
                ExecutionDegree.EXECUTION_DEGREE_COMPARATORY_BY_DEGREE_TYPE_AND_NAME_AND_EXECUTION_YEAR);

        executionDegreeList.forEach(executionDegree -> response.add(executionDegreeToJson(executionDegree)));

        return new ResponseEntity<String>(response.toString(), HttpStatus.OK);
    }

    private JsonElement executionDegreeToJson(ExecutionDegree executionDegree) {
        JsonObject json = new JsonObject();

        json.addProperty("externalId", executionDegree.getExternalId());
        json.addProperty("name", executionDegree.getPresentationName());

        return json;
    }

    @RequestMapping(value = "createParticipantType", method = RequestMethod.GET)
    public String createParticipantTypeForm(Model model) {

        List<ThesisProposalParticipantType> participantTypeList =
                ThesisProposalsSystem.getInstance().getThesisProposalParticipantTypeSet().stream().collect(Collectors.toList());

        Collections.sort(participantTypeList, ThesisProposalParticipantType.COMPARATOR_BY_WEIGHT);

        model.addAttribute("participantTypeList", participantTypeList);

        return "participantsType/create";
    }

    @RequestMapping(value = "/createParticipantType", method = RequestMethod.POST)
    public String createParticipantType(@RequestParam LocalizedString name, @RequestParam int weight) {

        createThesisProposalParticipantType(name, weight);

        return "redirect:/configuration";
    }

    @Atomic(mode = TxMode.WRITE)
    public void createThesisProposalParticipantType(LocalizedString name, int weight) {
        new ThesisProposalParticipantType(name, weight);
    }

    @RequestMapping(value = "deleteParticipantType/{participantType}", method = RequestMethod.POST)
    public String deleteParticipantType(@PathVariable("participantType") ThesisProposalParticipantType participantType,
            Model model) {

        return delete(participantType, model);
    }

    @Atomic(mode = TxMode.WRITE)
    private String delete(ThesisProposalParticipantType participantType, Model model) {
        try {
            participantType.delete();
        } catch (DomainException domainException) {
            model.addAttribute("deleteException", true);
            return createParticipantTypeForm(model);
        }

        return "redirect:/configuration";
    }

    @RequestMapping(value = "editParticipantType/{participantType}", method = RequestMethod.GET)
    public ModelAndView editParticipantTypeForm(@PathVariable("participantType") ThesisProposalParticipantType participantType,
            Model model) {

        ParticipantTypeBean thesisProposalParticipantTypeBean =
                new ParticipantTypeBean(participantType.getName(), participantType.getWeight(), participantType.getExternalId());

        ModelAndView mav = new ModelAndView("participantsType/edit", "command", thesisProposalParticipantTypeBean);
        return mav;
    }

    @RequestMapping(value = "editParticipantType", method = RequestMethod.POST)
    public String editParticipantType(@RequestParam LocalizedString name, @RequestParam String externalId,
            @RequestParam int weight) {

        ParticipantTypeBean bean = new ParticipantTypeBean(name, weight, externalId);

        return edit(bean);
    }

    @Atomic(mode = TxMode.WRITE)
    private String edit(ParticipantTypeBean participantTypeBean) {
        ThesisProposalParticipantType thesisProposalParticipantType =
                FenixFramework.getDomainObject(participantTypeBean.getExternalId());

        thesisProposalParticipantType.setName(participantTypeBean.getName());

        return "redirect:/configuration";
    }

    @RequestMapping(value = "/updateWeights", method = RequestMethod.POST)
    public String updateParticipantTypeWeights(@RequestParam String json) {

        JsonParser parser = new JsonParser();
        JsonArray jsonArray = (JsonArray) parser.parse(json);

        updateParticipantTypeWeights(jsonArray);

        return "redirect:/configuration";
    }

    @Atomic(mode = TxMode.WRITE)
    public void updateParticipantTypeWeights(JsonArray jsonArray) {
        jsonArray.forEach((JsonElement elem) -> {
            String externalId = elem.getAsJsonObject().get("externalId").getAsString();
            int weight = elem.getAsJsonObject().get("weight").getAsInt();

            ThesisProposalParticipantType type = FenixFramework.getDomainObject(externalId);
            type.setWeight(weight);
        });
    }

    @RequestMapping(value = "inactivateParticipantType/{participantType}", method = RequestMethod.GET)
    public String inactivateParticipantType(@PathVariable("participantType") ThesisProposalParticipantType participantType,
            Model model) {

        setParticipantTypeInactiveness(participantType, true);

        return "redirect:/configuration";
    }

    @RequestMapping(value = "activateParticipantType/{participantType}", method = RequestMethod.GET)
    public String activateParticipantType(@PathVariable("participantType") ThesisProposalParticipantType participantType,
            Model model) {

        setParticipantTypeInactiveness(participantType, false);

        return "redirect:/configuration";
    }

    @Atomic(mode = TxMode.WRITE)
    private void setParticipantTypeInactiveness(ThesisProposalParticipantType participantType, boolean inactiveness) {
        participantType.setInactive(inactiveness);
    }

}
