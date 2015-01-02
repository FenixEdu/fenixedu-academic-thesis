package org.fenixedu.academic.thesis.ui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fenixedu.academic.domain.ExecutionDegree;
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
import org.fenixedu.academic.thesis.domain.exception.ThesisProposalExecutionDegreeRequiredException;
import org.fenixedu.academic.thesis.domain.exception.UnequivalentThesisConfigurations;
import org.fenixedu.academic.thesis.domain.exception.UnexistentConfigurationException;
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

	Set<ThesisProposal> thesisProposalsList = ThesisProposal.readByParticipant(Authenticate.getUser());

	model.addAttribute("thesisProposalsList", thesisProposalsList);

	return "proposals/list";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public ModelAndView createThesisProposalsForm(Model model) {

	ModelAndView mav = new ModelAndView("proposals/create", "command", new ThesisProposalBean());

	List<ExecutionDegree> executionDegreeList = ThesisProposal.getThesisExecutionDegrees();
	mav.addObject("executionDegreeList", executionDegreeList);

	List<ThesisProposalParticipantType> participantTypeList = new ArrayList<ThesisProposalParticipantType>();
	participantTypeList.addAll(ThesisProposalsSystem.getInstance().getThesisProposalParticipantTypeSet());
	Collections.sort(participantTypeList, ThesisProposalParticipantType.COMPARATOR_BY_WEIGHT);
	mav.addObject("participantTypeList", participantTypeList);

	return mav;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ModelAndView createThesisProposals(@ModelAttribute ThesisProposalBean proposalBean,
	    @RequestParam String participantsJson, @RequestParam Set<ExecutionDegree> executionDegrees, Model model)
	    throws ThesisProposalExecutionDegreeRequiredException {

	if (executionDegrees == null || executionDegrees.isEmpty()) {
	    throw new ThesisProposalExecutionDegreeRequiredException();
	}
	ThesisProposalsConfiguration base = ((ExecutionDegree) executionDegrees.toArray()[0]).getThesisProposalsConfiguration();

	try {
	    for (ExecutionDegree executionDegree : executionDegrees) {
		if (executionDegree.getThesisProposalsConfiguration() == null) {
		    throw new UnexistentConfigurationException(executionDegree);
		}
		if (!base.isEquivalent(executionDegree.getThesisProposalsConfiguration())) {
		    throw new UnequivalentThesisConfigurations(base, executionDegree.getThesisProposalsConfiguration());
		}
	    }
	} catch (UnexistentConfigurationException exception) {
	    model.addAttribute("unexistentConfigurationException", true);

	    List<ExecutionDegree> executionDegreeList = ThesisProposal.getThesisExecutionDegrees();
	    model.addAttribute("executionDegreeList", executionDegreeList);

	    List<ThesisProposalParticipantType> participantTypeList = new ArrayList<ThesisProposalParticipantType>();
	    participantTypeList.addAll(ThesisProposalsSystem.getInstance().getThesisProposalParticipantTypeSet());
	    Collections.sort(participantTypeList, ThesisProposalParticipantType.COMPARATOR_BY_WEIGHT);
	    model.addAttribute("participantTypeList", participantTypeList);

	    model.addAttribute("command", proposalBean);
	    return new ModelAndView("proposals/create", model.asMap());
	} catch (UnequivalentThesisConfigurations exception) {
	    model.addAttribute("unequivalentThesisConfigurationsException", exception);

	    List<ExecutionDegree> executionDegreeList = ThesisProposal.getThesisExecutionDegrees();
	    model.addAttribute("executionDegreeList", executionDegreeList);

	    List<ThesisProposalParticipantType> participantTypeList = new ArrayList<ThesisProposalParticipantType>();
	    participantTypeList.addAll(ThesisProposalsSystem.getInstance().getThesisProposalParticipantTypeSet());
	    Collections.sort(participantTypeList, ThesisProposalParticipantType.COMPARATOR_BY_WEIGHT);
	    model.addAttribute("participantTypeList", participantTypeList);

	    model.addAttribute("command", proposalBean);
	    return new ModelAndView("proposals/create", model.asMap());
	}

	proposalBean.setExecutionDegree(executionDegrees);

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

	    createThesisProposal(proposalBean, participants);

	} catch (OutOfProposalPeriodException exception) {
	    model.addAttribute("createOutOfProposalPeriodException", exception);

	    List<ExecutionDegree> executionDegreeList = ThesisProposal.getThesisExecutionDegrees();
	    model.addAttribute("executionDegreeList", executionDegreeList);

	    List<ThesisProposalParticipantType> participantTypeList = new ArrayList<ThesisProposalParticipantType>();
	    participantTypeList.addAll(ThesisProposalsSystem.getInstance().getThesisProposalParticipantTypeSet());
	    Collections.sort(participantTypeList, ThesisProposalParticipantType.COMPARATOR_BY_WEIGHT);
	    model.addAttribute("participantTypeList", participantTypeList);

	    model.addAttribute("command", proposalBean);
	    return new ModelAndView("proposals/create", model.asMap());
	} catch (MaxNumberThesisProposalsException exception) {
	    model.addAttribute("createMaxNumberThesisProposalsException", exception);

	    List<ExecutionDegree> executionDegreeList = ThesisProposal.getThesisExecutionDegrees();
	    model.addAttribute("executionDegreeList", executionDegreeList);

	    List<ThesisProposalParticipantType> participantTypeList = new ArrayList<ThesisProposalParticipantType>();
	    participantTypeList.addAll(ThesisProposalsSystem.getInstance().getThesisProposalParticipantTypeSet());
	    Collections.sort(participantTypeList, ThesisProposalParticipantType.COMPARATOR_BY_WEIGHT);
	    model.addAttribute("participantTypeList", participantTypeList);

	    model.addAttribute("command", proposalBean);
	    return new ModelAndView("proposals/create", model.asMap());
	} catch (IllegalParticipantTypeException exception) {
	    model.addAttribute("illegalParticipantTypeException", exception);

	    List<ExecutionDegree> executionDegreeList = ThesisProposal.getThesisExecutionDegrees();
	    model.addAttribute("executionDegreeList", executionDegreeList);

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
	    if (!thesisProposal.getSingleExecutionDegree().getThesisProposalsConfiguration().getProposalPeriod()
		    .contains(DateTime.now())) {
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
			    thesisProposal.getLocalization(), thesisProposal.getExecutionDegreeSet(),
			    thesisProposal.getStudentThesisCandidacySet(), thesisProposalParticipantsBean,
			    thesisProposal.getExternalId());

		    ModelAndView mav = new ModelAndView("proposals/edit", "command", thesisProposalBean);

		    List<ExecutionDegree> executionDegreeList = ThesisProposal.getThesisExecutionDegrees();

		    mav.addObject("executionDegreeList", executionDegreeList);

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
	    @RequestParam String participantsJson, @RequestParam Set<ExecutionDegree> executionDegrees, Model model) {

	thesisProposalBean.setExecutionDegree(executionDegrees);

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
	}
    }

    @Atomic(mode = TxMode.WRITE)
    private ModelAndView editThesisProposal(ThesisProposalBean thesisProposalBean, ThesisProposal thesisProposal,
	    JsonArray jsonArray) throws MaxNumberThesisProposalsException, OutOfProposalPeriodException,
	    IllegalParticipantTypeException, UnexistentConfigurationException {
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

	ArrayList<ThesisProposalParticipant> participants = new ArrayList<ThesisProposalParticipant>();

	for (ThesisProposalParticipantBean participantBean : participantsBean) {
	    User user = FenixFramework.getDomainObject(participantBean.getUserExternalId());

	    ThesisProposalParticipantType participantType = FenixFramework.getDomainObject(participantBean
		    .getParticipantTypeExternalId());

	    ThesisProposalParticipant participant = new ThesisProposalParticipant(user, participantType);

	    if (thesisProposal.getSingleExecutionDegree().getThesisProposalsConfiguration().getMaxThesisProposalsByUser() != -1
		    && user.getThesisProposalParticipantSet().size() >= thesisProposal.getSingleExecutionDegree()
			    .getThesisProposalsConfiguration().getMaxThesisProposalsByUser()) {
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
	thesisProposal.getExecutionDegreeSet().clear();
	thesisProposal.getExecutionDegreeSet().addAll(thesisProposalBean.getExecutionDegrees());
	thesisProposal.getThesisProposalParticipantSet().clear();
	thesisProposal.getThesisProposalParticipantSet().addAll(participants);

	ThesisProposalsConfiguration config = thesisProposal.getSingleExecutionDegree().getThesisProposalsConfiguration();

	if (config == null) {
	    throw new UnexistentConfigurationException(thesisProposal.getSingleExecutionDegree());
	}

	if (!config.getProposalPeriod().containsNow() || !config.getProposalPeriod().containsNow()) {
	    throw new OutOfProposalPeriodException();
	}
	thesisProposal.setLocalization(thesisProposalBean.getLocalization());

	return new ModelAndView("redirect:/proposals");
    }
}
