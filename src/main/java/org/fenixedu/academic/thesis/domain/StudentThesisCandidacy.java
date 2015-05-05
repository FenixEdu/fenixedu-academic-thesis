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
package org.fenixedu.academic.thesis.domain;

import java.util.Collection;
import java.util.Comparator;

import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.thesis.ui.exception.ThesisProposalsDomainException;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.joda.time.DateTime;

public class StudentThesisCandidacy extends StudentThesisCandidacy_Base {

    public final static Comparator<StudentThesisCandidacy> COMPARATOR_BY_PREFERENCE_NUMBER =
            new Comparator<StudentThesisCandidacy>() {

                @Override
                public int compare(StudentThesisCandidacy arg0, StudentThesisCandidacy arg1) {
                    return arg0.getPreferenceNumber() - arg1.getPreferenceNumber();
                }
            };

    public final static Comparator<StudentThesisCandidacy> COMPARATOR_BY_DATETIME = new Comparator<StudentThesisCandidacy>() {

        @Override
        public int compare(StudentThesisCandidacy arg0, StudentThesisCandidacy arg1) {
            return arg0.getTimestamp().compareTo(arg1.getTimestamp());
        }
    };

    public final static Comparator<StudentThesisCandidacy> COMPARATOR_BY_CANDIDACY_PERIOD =
            new Comparator<StudentThesisCandidacy>() {

                @Override
                public int compare(StudentThesisCandidacy arg0, StudentThesisCandidacy arg1) {
                    return ThesisProposalsConfiguration.COMPARATOR_BY_CANDIDACY_PERIOD_START_DESC.compare(arg0
                            .getThesisProposal().getSingleThesisProposalsConfiguration(), arg1.getThesisProposal()
                            .getSingleThesisProposalsConfiguration());
                }
            };

    public final static Comparator<StudentThesisCandidacy> COMPARATOR_BY_CANDIDACY_PERIOD_AND_PREFERENCE_NUMBER =
            COMPARATOR_BY_CANDIDACY_PERIOD.thenComparing(COMPARATOR_BY_PREFERENCE_NUMBER);

    public StudentThesisCandidacy(Registration registration, Integer preferenceNumber, ThesisProposal thesisProposal) {
        super();
        setThesisProposalsSystem(ThesisProposalsSystem.getInstance());
        setThesisProposal(thesisProposal);
        setRegistration(registration);
        setTimestamp(new DateTime());
        setAcceptedByAdvisor(false);
        setPreferenceNumber(preferenceNumber);
        
        new ProposalsLog(thesisProposal, this, "Creating new student thesis candidacy");
    }

    @Override
    public void setPreferenceNumber(int preferenceNumber) {
        new ProposalsLog(getThesisProposal(), this, "Updating preference number " + getPreferenceNumber() + " -> "
                + preferenceNumber);
        super.setPreferenceNumber(preferenceNumber);
    }

    @Override
    public void setAcceptedByAdvisor(boolean acceptedByAdvisor) {
        new ProposalsLog(getThesisProposal(), this, "Updating advisor acceptance " + getAcceptedByAdvisor() + " -> "
                + acceptedByAdvisor);
        super.setAcceptedByAdvisor(acceptedByAdvisor);
    }

    public void delete() {

        new ProposalsLog(getThesisProposal(), null, "Deleting Student thesis candidacy");

        ThesisProposalsDomainException.throwWhenDeleteBlocked(getDeletionBlockers());

        this.setThesisProposal(null);
        this.setThesisProposalsSystem(null);
        this.setRegistration(null);

        deleteDomainObject();
    }

    @Override
    protected void checkForDeletionBlockers(Collection<String> blockers) {
        super.checkForDeletionBlockers(blockers);

        if (getAcceptedByAdvisor()) {
            blockers.add(BundleUtil.getString("resources.FenixEduThesisProposalsResources",
                    "domain.exception.accepted.by.advisor"));
        }
        if (!getThesisProposal().getSingleThesisProposalsConfiguration().getCandidacyPeriod().contains(DateTime.now())) {
            blockers.add(BundleUtil.getString("resources.FenixEduThesisProposalsResources",
                    "domain.exception.out.of.candidacy.period"));
        }
    }

    @Override
    public DateTime getTimestamp() {
        return super.getTimestamp();
    }
}
