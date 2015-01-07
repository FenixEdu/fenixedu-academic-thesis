package org.fenixedu.academic.thesis.ui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.thesis.domain.ThesisProposal;
import org.fenixedu.academic.thesis.domain.ThesisProposalParticipant;
import org.fenixedu.academic.thesis.domain.ThesisProposalParticipantType;
import org.fenixedu.academic.thesis.domain.ThesisProposalsConfiguration;
import org.fenixedu.academic.thesis.domain.ThesisProposalsSystem;
import org.fenixedu.academic.thesis.domain.exception.CannotEditUsedThesisProposalsException;
import org.fenixedu.academic.thesis.domain.exception.IllegalParticipantTypeException;
import org.fenixedu.academic.thesis.domain.exception.MaxNumberThesisProposalsException;
import org.fenixedu.academic.thesis.domain.exception.OutOfProposalPeriodException;
import org.fenixedu.academic.thesis.domain.exception.UnequivalentThesisConfigurations;
import org.fenixedu.academic.thesis.domain.exception.UnexistentConfigurationException;
import org.fenixedu.academic.thesis.domain.exception.UnexistentThesisParticipantException;
import org.fenixedu.academic.thesis.ui.bean.ThesisProposalBean;
import org.fenixedu.academic.thesis.ui.bean.ThesisProposalParticipantBean;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
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

@SpringApplication(group = "thesisCreators | activeStudents | #managers", path = "thesisProposals", title = "application.title.thesis", hint = "Thesis")
@SpringFunctionality(app = ThesisProposalsController.class, title = "title.thesisProposal.management", accessGroup = "thesisCreators")
@RequestMapping("/proposals")
public class ThesisProposalsController {

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String listProposals(Model model) {

	Set<ThesisProposal> thesisProposalsList = ThesisProposal.readCurrentByParticipant(Authenticate.getUser());

	model.addAttribute("thesisProposalsList", thesisProposalsList);

	return "proposals/list";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public ModelAndView createThesisProposalsForm(Model model) {

	ModelAndView mav = new ModelAndView("proposals/create", "command", new ThesisProposalBean());

	Set<ThesisProposalsConfiguration> configs = ThesisProposalsSystem.getInstance().getThesisProposalsConfigurationSet()
		.stream().filter(config -> config.getProposalPeriod().containsNow()).collect(Collectors.toSet());
	mav.addObject("configurations", configs);

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

	    Set<ThesisProposalsConfiguration> configs = ThesisProposalsSystem.getInstance().getThesisProposalsConfigurationSet()
		    .stream().filter(config -> config.getProposalPeriod().containsNow()).collect(Collectors.toSet());
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

	    Set<ThesisProposalsConfiguration> configs = ThesisProposalsSystem.getInstance().getThesisProposalsConfigurationSet()
		    .stream().filter(config -> config.getProposalPeriod().containsNow()).collect(Collectors.toSet());
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

	    createThesisProposal(proposalBean, participants);

	} catch (OutOfProposalPeriodException exception) {
	    model.addAttribute("createOutOfProposalPeriodException", exception);

	    Set<ThesisProposalsConfiguration> configs = ThesisProposalsSystem.getInstance().getThesisProposalsConfigurationSet()
		    .stream().filter(config -> config.getProposalPeriod().containsNow()).collect(Collectors.toSet());
	    model.addAttribute("configurations", configs);

	    List<ThesisProposalParticipantType> participantTypeList = new ArrayList<ThesisProposalParticipantType>();
	    participantTypeList.addAll(ThesisProposalsSystem.getInstance().getThesisProposalParticipantTypeSet());
	    Collections.sort(participantTypeList, ThesisProposalParticipantType.COMPARATOR_BY_WEIGHT);
	    model.addAttribute("participantTypeList", participantTypeList);

	    model.addAttribute("command", proposalBean);
	    return new ModelAndView("proposals/create", model.asMap());
	} catch (MaxNumberThesisProposalsException exception) {
	    model.addAttribute("createMaxNumberThesisProposalsException", exception);

	    Set<ThesisProposalsConfiguration> configs = ThesisProposalsSystem.getInstance().getThesisProposalsConfigurationSet()
		    .stream().filter(config -> config.getProposalPeriod().containsNow()).collect(Collectors.toSet());
	    model.addAttribute("configurations", configs);

	    List<ThesisProposalParticipantType> participantTypeList = new ArrayList<ThesisProposalParticipantType>();
	    participantTypeList.addAll(ThesisProposalsSystem.getInstance().getThesisProposalParticipantTypeSet());
	    Collections.sort(participantTypeList, ThesisProposalParticipantType.COMPARATOR_BY_WEIGHT);
	    model.addAttribute("participantTypeList", participantTypeList);

	    model.addAttribute("command", proposalBean);
	    return new ModelAndView("proposals/create", model.asMap());
	} catch (IllegalParticipantTypeException exception) {
	    model.addAttribute("illegalParticipantTypeException", exception);

	    Set<ThesisProposalsConfiguration> configs = ThesisProposalsSystem.getInstance().getThesisProposalsConfigurationSet()
		    .stream().filter(config -> config.getProposalPeriod().containsNow()).collect(Collectors.toSet());
	    model.addAttribute("configurations", configs);

	    List<ThesisProposalParticipantType> participantTypeList = new ArrayList<ThesisProposalParticipantType>();
	    participantTypeList.addAll(ThesisProposalsSystem.getInstance().getThesisProposalParticipantTypeSet());
	    Collections.sort(participantTypeList, ThesisProposalParticipantType.COMPARATOR_BY_WEIGHT);
	    model.addAttribute("participantTypeList", participantTypeList);

	    model.addAttribute("command", proposalBean);
	    return new ModelAndView("proposals/create", model.asMap());
	} catch (UnexistentThesisParticipantException exception) {
	    model.addAttribute("unexistentThesisParticipantException", exception);

	    Set<ThesisProposalsConfiguration> configs = ThesisProposalsSystem.getInstance().getThesisProposalsConfigurationSet()
		    .stream().filter(config -> config.getProposalPeriod().containsNow()).collect(Collectors.toSet());
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
    public ModelAndView editConfigurationForm(@PathVariable("oid") ThesisProposal thesisProposal, Model model) {

	try {
	    if (!thesisProposal.getSingleThesisProposalsConfiguration().getProposalPeriod().contains(DateTime.now())) {
		throw new OutOfProposalPeriodException();
	    } else {
		if (!thesisProposal.getStudentThesisCandidacySet().isEmpty()) {
		    throw new CannotEditUsedThesisProposalsException(thesisProposal);
		} else {
		    HashSet<ThesisProposalParticipantBean> thesisProposalParticipantsBean = new HashSet<ThesisProposalParticipantBean>();

		    for (ThesisProposalParticipant participant : thesisProposal.getThesisProposalParticipantSet()) {

			String participantType = participant.getThesisProposalParticipantType().getExternalId();

			ThesisProposalParticipantBean bean = new ThesisProposalParticipantBean(participant.getUser(),
				participantType);

			thesisProposalParticipantsBean.add(bean);
		    }

		    ThesisProposalBean thesisProposalBean = new ThesisProposalBean(thesisProposal.getTitle(),
			    thesisProposal.getObservations(), thesisProposal.getRequirements(), thesisProposal.getGoals(),
			    thesisProposal.getLocalization(), thesisProposal.getThesisConfigurationSet(),
			    thesisProposal.getStudentThesisCandidacySet(), thesisProposalParticipantsBean,
			    thesisProposal.getExternalId());

		    ModelAndView mav = new ModelAndView("proposals/edit", "command", thesisProposalBean);

		    Set<ThesisProposalsConfiguration> configs = ThesisProposalsSystem.getInstance()
			    .getThesisProposalsConfigurationSet().stream()
			    .filter(config -> config.getProposalPeriod().containsNow()).collect(Collectors.toSet());
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
    public ModelAndView editConfiguration(@ModelAttribute ThesisProposalBean thesisProposalBean,
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
	    return editConfigurationForm(thesisProposal, model);
	} catch (OutOfProposalPeriodException exception) {
	    model.addAttribute("outOfProposalPeriodException", true);
	    return editConfigurationForm(thesisProposal, model);
	} catch (IllegalParticipantTypeException exception) {
	    model.addAttribute("illegalParticipantTypeException", exception);
	    return editConfigurationForm(thesisProposal, model);
	} catch (UnexistentConfigurationException exception) {
	    model.addAttribute("unexistentConfigurationException", exception);
	    return editConfigurationForm(thesisProposal, model);
	} catch (UnexistentThesisParticipantException exception) {
	    model.addAttribute("unexistentThesisParticipantException", exception);
	    return editConfigurationForm(thesisProposal, model);
	} catch (UnequivalentThesisConfigurations exception) {
	    model.addAttribute("unequivalentThesisConfigurations", exception);
	    return editConfigurationForm(thesisProposal, model);
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

	    ThesisProposalParticipantBean participantBean = new ThesisProposalParticipantBean(User.findByUsername(userId),
		    userType);

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

	    ThesisProposalParticipantType participantType = FenixFramework.getDomainObject(participantBean
		    .getParticipantTypeExternalId());

	    ThesisProposalParticipant participant = new ThesisProposalParticipant(user, participantType);

	    if (thesisProposal.getSingleThesisProposalsConfiguration().getMaxThesisProposalsByUser() != -1
		    && user.getThesisProposalParticipantSet().size() >= thesisProposal.getSingleThesisProposalsConfiguration()
			    .getMaxThesisProposalsByUser()) {
		throw new MaxNumberThesisProposalsException(participant);
	    } else {
		participant.setThesisProposal(thesisProposal);
		participants.add(participant);
	    }
	}

	thesisProposal.setTitle(thesisProposalBean.getTitle());
	thesisProposal.setObservations(thesisProposalBean.getObservations());
	thesisProposal.setRequirements(thesisProposalBean.getRequirements());
	thesisProposal.setGoals(thesisProposalBean.getGoals());
	thesisProposal.getThesisConfigurationSet().clear();
	thesisProposal.getThesisConfigurationSet().addAll(thesisProposalBean.getThesisProposalsConfigurations());

	thesisProposal.getThesisProposalParticipantSet().addAll(participants);

	ThesisProposalsConfiguration base = (ThesisProposalsConfiguration) thesisProposalBean.getThesisProposalsConfigurations()
		.toArray()[0];

	for (ThesisProposalsConfiguration configuration : thesisProposalBean.getThesisProposalsConfigurations()) {
	    if (!base.isEquivalent(configuration)) {
		throw new UnequivalentThesisConfigurations(base, configuration);
	    }
	}

	ThesisProposalsConfiguration config = thesisProposal.getSingleThesisProposalsConfiguration();

	if (!config.getProposalPeriod().containsNow() || !config.getProposalPeriod().containsNow()) {
	    throw new OutOfProposalPeriodException();
	}
	thesisProposal.setLocalization(thesisProposalBean.getLocalization());

	return new ModelAndView("redirect:/proposals");
    }
}
