package org.fenixedu.academic.thesis.ui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.thesis.domain.StudentThesisCandidacy;
import org.fenixedu.academic.thesis.domain.ThesisProposal;
import org.fenixedu.academic.thesis.domain.exception.OutOfCandidacyPeriodException;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@SpringFunctionality(app = ThesisProposalsController.class, title = "title.thesisCandidacy.management", accessGroup = "thesisSystemManagers | thesisCreators")
@RequestMapping("/thesisCandidacies")
public class ThesisCandidaciesController {

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String listProposals(Model model) {

	ArrayList<ThesisProposal> thesisProposalsList = new ArrayList<ThesisProposal>(
		ThesisProposal.readCurrentByParticipant(Authenticate.getUser()));
	Collections.sort(thesisProposalsList, ThesisProposal.COMPARATOR_BY_NUMBER_OF_CANDIDACIES);
	model.addAttribute("thesisProposalsList", thesisProposalsList);

	HashMap<String, Integer> bestAccepted = new HashMap<String, Integer>();
	for (ThesisProposal thesisProposal : thesisProposalsList) {
	    for (StudentThesisCandidacy candidacy : thesisProposal.getStudentThesisCandidacySet()) {
		Registration registration = candidacy.getRegistration();
		if (!bestAccepted.containsKey(registration.getExternalId())) {
		    for (StudentThesisCandidacy studentCandidacy : registration.getStudentThesisCandidacySet()) {
			if (studentCandidacy.getAcceptedByAdvisor()
				&& studentCandidacy.getPreferenceNumber() < bestAccepted.getOrDefault(
					registration.getExternalId(), Integer.MAX_VALUE)) {
			    bestAccepted.put(registration.getExternalId(), studentCandidacy.getPreferenceNumber());
			}
		    }
		}
	    }
	}

	model.addAttribute("bestAccepted", bestAccepted);

	return "thesisCandidacies/list";
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

	    return "redirect:/thesisCandidacies/manage/" + studentThesisCandidacy.getThesisProposal().getExternalId();
	} catch (OutOfCandidacyPeriodException exception) {
	    model.addAttribute("outOfCandidacyPeriodException", true);
	    return listProposals(model);
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

	    return "redirect:/thesisCandidacies/manage/" + studentThesisCandidacy.getThesisProposal().getExternalId();
	} catch (OutOfCandidacyPeriodException exception) {
	    model.addAttribute("outOfCandidacyPeriodException", true);
	    return listProposals(model);
	}
    }

    @RequestMapping(value = "/manage/{oid}", method = RequestMethod.GET)
    public ModelAndView manageCandidacies(@PathVariable("oid") ThesisProposal thesisProposal, Model model) {

	ModelAndView mav = new ModelAndView("thesisCandidacies/manage");

	Set<StudentThesisCandidacy> candidacies = thesisProposal.getStudentThesisCandidacySet();

	mav.addObject("candidaciesList", candidacies);

	return mav;
    }
}
